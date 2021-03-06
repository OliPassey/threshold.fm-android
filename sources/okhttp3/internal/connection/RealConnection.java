package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.Reference;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownServiceException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import okhttp3.Address;
import okhttp3.CertificatePinner;
import okhttp3.Connection;
import okhttp3.ConnectionSpec;
import okhttp3.Handshake;
import okhttp3.HttpUrl;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.Util;
import okhttp3.internal.Version;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http1.Http1Codec;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.Http2Connection;
import okhttp3.internal.http2.Http2Connection.Builder;
import okhttp3.internal.http2.Http2Connection.Listener;
import okhttp3.internal.http2.Http2Stream;
import okhttp3.internal.platform.Platform;
import okhttp3.internal.tls.OkHostnameVerifier;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

public final class RealConnection extends Listener implements Connection {
    public int allocationLimit;
    public final List<Reference<StreamAllocation>> allocations = new ArrayList();
    private Handshake handshake;
    public volatile Http2Connection http2Connection;
    public long idleAtNanos = Long.MAX_VALUE;
    public boolean noNewStreams;
    private Protocol protocol;
    private Socket rawSocket;
    private final Route route;
    public BufferedSink sink;
    public Socket socket;
    public BufferedSource source;
    public int successCount;

    public RealConnection(Route route) {
        this.route = route;
    }

    public void connect(int connectTimeout, int readTimeout, int writeTimeout, List<ConnectionSpec> connectionSpecs, boolean connectionRetryEnabled) {
        if (this.protocol != null) {
            throw new IllegalStateException("already connected");
        }
        RouteException routeException = null;
        ConnectionSpecSelector connectionSpecSelector = new ConnectionSpecSelector(connectionSpecs);
        if (this.route.address().sslSocketFactory() == null) {
            if (connectionSpecs.contains(ConnectionSpec.CLEARTEXT)) {
                String host = this.route.address().url().host();
                if (!Platform.get().isCleartextTrafficPermitted(host)) {
                    throw new RouteException(new UnknownServiceException("CLEARTEXT communication to " + host + " not permitted by network security policy"));
                }
            }
            throw new RouteException(new UnknownServiceException("CLEARTEXT communication not enabled for client"));
        }
        while (this.protocol == null) {
            try {
                if (this.route.requiresTunnel()) {
                    buildTunneledConnection(connectTimeout, readTimeout, writeTimeout, connectionSpecSelector);
                } else {
                    buildConnection(connectTimeout, readTimeout, writeTimeout, connectionSpecSelector);
                }
            } catch (IOException e) {
                Util.closeQuietly(this.socket);
                Util.closeQuietly(this.rawSocket);
                this.socket = null;
                this.rawSocket = null;
                this.source = null;
                this.sink = null;
                this.handshake = null;
                this.protocol = null;
                if (routeException == null) {
                    routeException = new RouteException(e);
                } else {
                    routeException.addConnectException(e);
                }
                if (!connectionRetryEnabled || !connectionSpecSelector.connectionFailed(e)) {
                    throw routeException;
                }
            }
        }
    }

    private void buildTunneledConnection(int connectTimeout, int readTimeout, int writeTimeout, ConnectionSpecSelector connectionSpecSelector) throws IOException {
        Request tunnelRequest = createTunnelRequest();
        HttpUrl url = tunnelRequest.url();
        int attemptedConnections = 0;
        while (true) {
            attemptedConnections++;
            if (attemptedConnections > 21) {
                throw new ProtocolException("Too many tunnel connections attempted: " + 21);
            }
            connectSocket(connectTimeout, readTimeout);
            tunnelRequest = createTunnel(readTimeout, writeTimeout, tunnelRequest, url);
            if (tunnelRequest == null) {
                establishProtocol(readTimeout, writeTimeout, connectionSpecSelector);
                return;
            }
            Util.closeQuietly(this.rawSocket);
            this.rawSocket = null;
            this.sink = null;
            this.source = null;
        }
    }

    private void buildConnection(int connectTimeout, int readTimeout, int writeTimeout, ConnectionSpecSelector connectionSpecSelector) throws IOException {
        connectSocket(connectTimeout, readTimeout);
        establishProtocol(readTimeout, writeTimeout, connectionSpecSelector);
    }

    private void connectSocket(int connectTimeout, int readTimeout) throws IOException {
        Proxy proxy = this.route.proxy();
        Socket createSocket = (proxy.type() == Type.DIRECT || proxy.type() == Type.HTTP) ? this.route.address().socketFactory().createSocket() : new Socket(proxy);
        this.rawSocket = createSocket;
        this.rawSocket.setSoTimeout(readTimeout);
        try {
            Platform.get().connectSocket(this.rawSocket, this.route.socketAddress(), connectTimeout);
            this.source = Okio.buffer(Okio.source(this.rawSocket));
            this.sink = Okio.buffer(Okio.sink(this.rawSocket));
        } catch (ConnectException e) {
            ConnectException ce = new ConnectException("Failed to connect to " + this.route.socketAddress());
            ce.initCause(e);
            throw ce;
        }
    }

    private void establishProtocol(int readTimeout, int writeTimeout, ConnectionSpecSelector connectionSpecSelector) throws IOException {
        if (this.route.address().sslSocketFactory() != null) {
            connectTls(readTimeout, writeTimeout, connectionSpecSelector);
        } else {
            this.protocol = Protocol.HTTP_1_1;
            this.socket = this.rawSocket;
        }
        if (this.protocol == Protocol.HTTP_2) {
            this.socket.setSoTimeout(0);
            Http2Connection http2Connection = new Builder(true).socket(this.socket, this.route.address().url().host(), this.source, this.sink).listener(this).build();
            http2Connection.start();
            this.allocationLimit = http2Connection.maxConcurrentStreams();
            this.http2Connection = http2Connection;
            return;
        }
        this.allocationLimit = 1;
    }

    private void connectTls(int readTimeout, int writeTimeout, ConnectionSpecSelector connectionSpecSelector) throws IOException {
        Address address = this.route.address();
        Socket sslSocket = null;
        try {
            sslSocket = (SSLSocket) address.sslSocketFactory().createSocket(this.rawSocket, address.url().host(), address.url().port(), true);
            ConnectionSpec connectionSpec = connectionSpecSelector.configureSecureSocket(sslSocket);
            if (connectionSpec.supportsTlsExtensions()) {
                Platform.get().configureTlsExtensions(sslSocket, address.url().host(), address.protocols());
            }
            sslSocket.startHandshake();
            Handshake unverifiedHandshake = Handshake.get(sslSocket.getSession());
            if (address.hostnameVerifier().verify(address.url().host(), sslSocket.getSession())) {
                address.certificatePinner().check(address.url().host(), unverifiedHandshake.peerCertificates());
                String maybeProtocol = connectionSpec.supportsTlsExtensions() ? Platform.get().getSelectedProtocol(sslSocket) : null;
                this.socket = sslSocket;
                this.source = Okio.buffer(Okio.source(this.socket));
                this.sink = Okio.buffer(Okio.sink(this.socket));
                this.handshake = unverifiedHandshake;
                this.protocol = maybeProtocol != null ? Protocol.get(maybeProtocol) : Protocol.HTTP_1_1;
                if (sslSocket != null) {
                    Platform.get().afterHandshake(sslSocket);
                }
                if (!true) {
                    Util.closeQuietly(sslSocket);
                    return;
                }
                return;
            }
            X509Certificate cert = (X509Certificate) unverifiedHandshake.peerCertificates().get(0);
            throw new SSLPeerUnverifiedException("Hostname " + address.url().host() + " not verified:\n    certificate: " + CertificatePinner.pin(cert) + "\n    DN: " + cert.getSubjectDN().getName() + "\n    subjectAltNames: " + OkHostnameVerifier.allSubjectAltNames(cert));
        } catch (AssertionError e) {
            if (Util.isAndroidGetsocknameError(e)) {
                throw new IOException(e);
            }
            throw e;
        } catch (Throwable th) {
            if (sslSocket != null) {
                Platform.get().afterHandshake(sslSocket);
            }
            if (!false) {
                Util.closeQuietly(sslSocket);
            }
        }
    }

    private Request createTunnel(int readTimeout, int writeTimeout, Request tunnelRequest, HttpUrl url) throws IOException {
        String requestLine = "CONNECT " + Util.hostHeader(url, true) + " HTTP/1.1";
        Response response;
        do {
            Http1Codec tunnelConnection = new Http1Codec(null, null, this.source, this.sink);
            this.source.timeout().timeout((long) readTimeout, TimeUnit.MILLISECONDS);
            this.sink.timeout().timeout((long) writeTimeout, TimeUnit.MILLISECONDS);
            tunnelConnection.writeRequest(tunnelRequest.headers(), requestLine);
            tunnelConnection.finishRequest();
            response = tunnelConnection.readResponse().request(tunnelRequest).build();
            long contentLength = HttpHeaders.contentLength(response);
            if (contentLength == -1) {
                contentLength = 0;
            }
            Source body = tunnelConnection.newFixedLengthSource(contentLength);
            Util.skipAll(body, Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
            body.close();
            switch (response.code()) {
                case 200:
                    if (this.source.buffer().exhausted() && this.sink.buffer().exhausted()) {
                        return null;
                    }
                    throw new IOException("TLS tunnel buffered too many bytes!");
                case 407:
                    tunnelRequest = this.route.address().proxyAuthenticator().authenticate(this.route, response);
                    if (tunnelRequest != null) {
                        break;
                    }
                    throw new IOException("Failed to authenticate with proxy");
                default:
                    throw new IOException("Unexpected response code for CONNECT: " + response.code());
            }
        } while (!"close".equalsIgnoreCase(response.header("Connection")));
        return tunnelRequest;
    }

    private Request createTunnelRequest() {
        return new Request.Builder().url(this.route.address().url()).header("Host", Util.hostHeader(this.route.address().url(), true)).header("Proxy-Connection", "Keep-Alive").header("User-Agent", Version.userAgent()).build();
    }

    public Route route() {
        return this.route;
    }

    public void cancel() {
        Util.closeQuietly(this.rawSocket);
    }

    public Socket socket() {
        return this.socket;
    }

    public boolean isHealthy(boolean doExtensiveChecks) {
        if (this.socket.isClosed() || this.socket.isInputShutdown() || this.socket.isOutputShutdown()) {
            return false;
        }
        if (this.http2Connection != null) {
            if (this.http2Connection.isShutdown()) {
                return false;
            }
            return true;
        } else if (!doExtensiveChecks) {
            return true;
        } else {
            int readTimeout;
            try {
                readTimeout = this.socket.getSoTimeout();
                this.socket.setSoTimeout(1);
                if (this.source.exhausted()) {
                    this.socket.setSoTimeout(readTimeout);
                    return false;
                }
                this.socket.setSoTimeout(readTimeout);
                return true;
            } catch (SocketTimeoutException e) {
                return true;
            } catch (IOException e2) {
                return false;
            } catch (Throwable th) {
                this.socket.setSoTimeout(readTimeout);
            }
        }
    }

    public void onStream(Http2Stream stream) throws IOException {
        stream.close(ErrorCode.REFUSED_STREAM);
    }

    public void onSettings(Http2Connection connection) {
        this.allocationLimit = connection.maxConcurrentStreams();
    }

    public Handshake handshake() {
        return this.handshake;
    }

    public boolean isMultiplexed() {
        return this.http2Connection != null;
    }

    public Protocol protocol() {
        if (this.http2Connection == null) {
            return this.protocol != null ? this.protocol : Protocol.HTTP_1_1;
        } else {
            return Protocol.HTTP_2;
        }
    }

    public String toString() {
        return "Connection{" + this.route.address().url().host() + ":" + this.route.address().url().port() + ", proxy=" + this.route.proxy() + " hostAddress=" + this.route.socketAddress() + " cipherSuite=" + (this.handshake != null ? this.handshake.cipherSuite() : "none") + " protocol=" + this.protocol + '}';
    }
}
