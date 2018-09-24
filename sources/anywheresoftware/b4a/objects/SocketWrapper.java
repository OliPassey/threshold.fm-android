package anywheresoftware.b4a.objects;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.CheckForReinitialize;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.BA.Version;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

@Version(1.51f)
@ShortName("Socket")
public class SocketWrapper implements CheckForReinitialize {
    private String eventName;
    private volatile Socket socket;

    /* renamed from: anywheresoftware.b4a.objects.SocketWrapper$1 */
    class C00401 implements X509TrustManager {
        C00401() {
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    @ShortName("ServerSocket")
    public static class ServerSocketWrapper implements CheckForReinitialize {
        private BA ba;
        private String eventName;
        private volatile ServerSocket ssocket;

        /* renamed from: anywheresoftware.b4a.objects.SocketWrapper$ServerSocketWrapper$1 */
        class C00451 implements Runnable {
            C00451() {
            }

            public void run() {
                try {
                    Socket s = ServerSocketWrapper.this.ssocket.accept();
                    new SocketWrapper().socket = s;
                    ServerSocketWrapper.this.ba.raiseEventFromDifferentThread(ServerSocketWrapper.this, ServerSocketWrapper.this, 0, new StringBuilder(String.valueOf(ServerSocketWrapper.this.eventName)).append("_newconnection").toString(), true, new Object[]{Boolean.valueOf(true), sw});
                } catch (IOException e) {
                    if (ServerSocketWrapper.this.ssocket != null) {
                        ServerSocketWrapper.this.ba.setLastException(e);
                        BA access$1 = ServerSocketWrapper.this.ba;
                        ServerSocketWrapper serverSocketWrapper = ServerSocketWrapper.this;
                        ServerSocketWrapper serverSocketWrapper2 = ServerSocketWrapper.this;
                        String stringBuilder = new StringBuilder(String.valueOf(ServerSocketWrapper.this.eventName)).append("_newconnection").toString();
                        Object[] objArr = new Object[2];
                        objArr[0] = Boolean.valueOf(false);
                        access$1.raiseEventFromDifferentThread(serverSocketWrapper, serverSocketWrapper2, 0, stringBuilder, true, objArr);
                    }
                }
            }
        }

        public void Initialize(BA ba, int Port, String EventName) throws IOException {
            this.ba = ba;
            this.eventName = EventName.toLowerCase(BA.cul);
            this.ssocket = new ServerSocket(Port);
        }

        public boolean IsInitialized() {
            return this.ssocket != null;
        }

        public String GetMyWifiIP() {
            String lh = "127.0.0.1";
            WifiInfo wifiInfo = ((WifiManager) BA.applicationContext.getSystemService("wifi")).getConnectionInfo();
            if (wifiInfo == null || wifiInfo.getIpAddress() == 0) {
                return lh;
            }
            return String.format("%d.%d.%d.%d", new Object[]{Integer.valueOf(wifiInfo.getIpAddress() & 255), Integer.valueOf((wifiInfo.getIpAddress() >> 8) & 255), Integer.valueOf((wifiInfo.getIpAddress() >> 16) & 255), Integer.valueOf((wifiInfo.getIpAddress() >> 24) & 255)});
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.String GetMyIP() throws java.net.SocketException {
            /*
            r8 = this;
            r4 = "127.0.0.1";
            r6 = r8.GetMyWifiIP();
            r7 = r6.equals(r4);
            if (r7 != 0) goto L_0x000d;
        L_0x000c:
            return r6;
        L_0x000d:
            r3 = 0;
            r0 = java.net.NetworkInterface.getNetworkInterfaces();
            if (r0 == 0) goto L_0x001a;
        L_0x0014:
            r7 = r0.hasMoreElements();
            if (r7 != 0) goto L_0x0021;
        L_0x001a:
            if (r3 == 0) goto L_0x004c;
        L_0x001c:
            r6 = r3.getHostAddress();
            goto L_0x000c;
        L_0x0021:
            r5 = r0.nextElement();
            r5 = (java.net.NetworkInterface) r5;
            r1 = r5.getInetAddresses();
        L_0x002b:
            r7 = r1.hasMoreElements();
            if (r7 == 0) goto L_0x0014;
        L_0x0031:
            r2 = r1.nextElement();
            r2 = (java.net.InetAddress) r2;
            r7 = r2.isLoopbackAddress();
            if (r7 != 0) goto L_0x002b;
        L_0x003d:
            r7 = r2 instanceof java.net.Inet6Address;
            if (r7 == 0) goto L_0x0047;
        L_0x0041:
            if (r3 != 0) goto L_0x002b;
        L_0x0043:
            r3 = r2;
            r3 = (java.net.Inet6Address) r3;
            goto L_0x002b;
        L_0x0047:
            r6 = r2.getHostAddress();
            goto L_0x000c;
        L_0x004c:
            r6 = r4;
            goto L_0x000c;
            */
            throw new UnsupportedOperationException("Method not decompiled: anywheresoftware.b4a.objects.SocketWrapper.ServerSocketWrapper.GetMyIP():java.lang.String");
        }

        public void Listen() throws IOException {
            if (!BA.isTaskRunning(this, 0)) {
                BA.submitRunnable(new C00451(), this, 0);
            }
        }

        public void Close() throws IOException {
            if (this.ssocket != null) {
                ServerSocket sss = this.ssocket;
                this.ssocket = null;
                sss.close();
            }
        }
    }

    @ShortName("UDPSocket")
    public static class UDPSocket implements CheckForReinitialize {
        private DatagramSocket ds;
        private UDPReader reader;

        @Hide
        public static class MyDatagramPacket {
            public final String host;
            public final DatagramPacket packet;
            public final int port;

            public MyDatagramPacket(String host, int port, DatagramPacket packet) {
                this.host = host;
                this.port = port;
                this.packet = packet;
            }
        }

        @ShortName("UDPPacket")
        public static class UDPPacket extends AbsObjectWrapper<MyDatagramPacket> {
            public void Initialize(byte[] Data, String Host, int Port) throws SocketException {
                Initialize2(Data, 0, Data.length, Host, Port);
            }

            public void Initialize2(byte[] Data, int Offset, int Length, String Host, int Port) throws SocketException {
                setObject(new MyDatagramPacket(Host, Port, new DatagramPacket(Data, Offset, Length)));
            }

            public int getLength() {
                return ((MyDatagramPacket) getObject()).packet.getLength();
            }

            public byte[] getData() {
                return ((MyDatagramPacket) getObject()).packet.getData();
            }

            public int getOffset() {
                return ((MyDatagramPacket) getObject()).packet.getOffset();
            }

            public int getPort() {
                return ((MyDatagramPacket) getObject()).packet.getPort();
            }

            public String getHost() {
                return ((MyDatagramPacket) getObject()).packet.getAddress().getHostName();
            }

            public String getHostAddress() {
                return ((MyDatagramPacket) getObject()).packet.getAddress().getHostAddress();
            }

            public String toString() {
                if (getObjectOrNull() == null) {
                    return super.toString();
                }
                return "Length=" + getLength() + ", Offset=" + getOffset() + ", Host=" + getHost() + ", Port=" + getPort();
            }
        }

        private static class UDPReader implements Runnable {
            BA ba;
            String eventName;
            int receiveLength;
            DatagramSocket socket;
            volatile boolean working;

            private UDPReader() {
            }

            public void run() {
                while (this.working) {
                    try {
                        DatagramPacket p = new DatagramPacket(new byte[this.receiveLength], this.receiveLength);
                        this.socket.receive(p);
                        new UDPPacket().setObject(new MyDatagramPacket("", 0, p));
                        this.ba.raiseEventFromDifferentThread(null, null, 0, this.eventName + "_packetarrived", false, new Object[]{u});
                    } catch (IOException e) {
                        if (this.working) {
                            try {
                                e.printStackTrace();
                                Thread.sleep(100);
                            } catch (InterruptedException e2) {
                            }
                        }
                    }
                }
            }
        }

        public void Initialize(BA ba, String EventName, int Port, int ReceiveBufferSize) throws SocketException {
            Close();
            if (Port == 0) {
                this.ds = new DatagramSocket();
            } else {
                this.ds = new DatagramSocket(Port);
            }
            if (ReceiveBufferSize > 0) {
                this.reader = new UDPReader();
                this.reader.working = true;
                this.reader.socket = this.ds;
                this.reader.receiveLength = ReceiveBufferSize;
                this.reader.ba = ba;
                this.reader.eventName = EventName.toLowerCase(BA.cul);
                Thread t = new Thread(this.reader);
                t.setDaemon(true);
                t.start();
            }
        }

        public boolean IsInitialized() {
            return (this.ds == null || this.ds.isClosed()) ? false : true;
        }

        public int getPort() {
            return this.ds.getLocalPort();
        }

        public void Send(final UDPPacket Packet) throws IOException {
            BA.submitRunnable(new Runnable() {
                public void run() {
                    try {
                        ((MyDatagramPacket) Packet.getObject()).packet.setSocketAddress(new InetSocketAddress(((MyDatagramPacket) Packet.getObject()).host, ((MyDatagramPacket) Packet.getObject()).port));
                        UDPSocket.this.ds.send(((MyDatagramPacket) Packet.getObject()).packet);
                    } catch (Exception e) {
                        Log.w("B4A", "", e);
                        throw new RuntimeException(e);
                    }
                }
            }, this, 1);
        }

        public void Close() {
            if (this.reader != null) {
                this.reader.working = false;
            }
            if (this.ds != null) {
                this.ds.close();
            }
            this.reader = null;
            this.ds = null;
        }

        public String toString() {
            if (this.ds == null) {
                return "Not initialized";
            }
            return "Port=" + getPort();
        }
    }

    public void Initialize(String EventName) {
        this.socket = new Socket();
        this.eventName = EventName.toLowerCase(BA.cul);
    }

    public void InitializeSSL(String EventName, InputStream KeyStoreStream, String Password) throws Exception {
        if (KeyStoreStream == null) {
            this.socket = SSLSocketFactory.getDefault().createSocket();
        } else {
            KeyStore ks = KeyStore.getInstance("BKS");
            ks.load(KeyStoreStream, Password.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
            this.socket = sslContext.getSocketFactory().createSocket();
        }
        this.eventName = EventName.toLowerCase(BA.cul);
    }

    public void InitializeSSLAcceptAll(String EventName) throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new C00401()}, new SecureRandom());
        this.socket = sslContext.getSocketFactory().createSocket();
        this.eventName = EventName.toLowerCase(BA.cul);
    }

    public static void LIBRARY_DOC() {
    }

    public boolean IsInitialized() {
        return this.socket != null;
    }

    public String ResolveHost(String Host) throws UnknownHostException {
        return InetAddress.getByName(Host).getHostAddress();
    }

    public int getTimeOut() throws SocketException {
        return this.socket.getSoTimeout();
    }

    public void setTimeOut(int value) throws SocketException {
        this.socket.setSoTimeout(value);
    }

    public void Connect(BA ba, String Host, int Port, int TimeOut) throws UnknownHostException {
        final String str = Host;
        final int i = Port;
        final int i2 = TimeOut;
        final BA ba2 = ba;
        BA.submitRunnable(new Runnable() {
            public void run() {
                Socket mySocket = SocketWrapper.this.socket;
                try {
                    InetSocketAddress i = new InetSocketAddress(InetAddress.getByName(str), i);
                    if (mySocket == SocketWrapper.this.socket) {
                        SocketWrapper.this.socket.connect(i, i2);
                        if (SocketWrapper.this.socket instanceof SSLSocket) {
                            final SSLSocket ssocket = (SSLSocket) SocketWrapper.this.socket;
                            final BA ba = ba2;
                            ssocket.addHandshakeCompletedListener(new HandshakeCompletedListener() {
                                public void handshakeCompleted(HandshakeCompletedEvent event) {
                                    final SSLSocket sSLSocket = ssocket;
                                    final BA ba = ba;
                                    BA.submitRunnable(new Runnable() {
                                        public void run() {
                                            try {
                                                sSLSocket.getInputStream();
                                                ba.raiseEventFromDifferentThread(SocketWrapper.this, SocketWrapper.this, 0, new StringBuilder(String.valueOf(SocketWrapper.this.eventName)).append("_connected").toString(), true, new Object[]{Boolean.valueOf(true)});
                                            } catch (IOException e) {
                                                ba.setLastException(e);
                                                ba.raiseEventFromDifferentThread(SocketWrapper.this, SocketWrapper.this, 0, new StringBuilder(String.valueOf(SocketWrapper.this.eventName)).append("_connected").toString(), true, new Object[]{Boolean.valueOf(false)});
                                            }
                                        }
                                    }, null, 0);
                                }
                            });
                            ssocket.startHandshake();
                            return;
                        }
                        ba2.raiseEventFromDifferentThread(SocketWrapper.this, SocketWrapper.this, 0, new StringBuilder(String.valueOf(SocketWrapper.this.eventName)).append("_connected").toString(), true, new Object[]{Boolean.valueOf(true)});
                    }
                } catch (Exception e) {
                    if (mySocket == SocketWrapper.this.socket) {
                        ba2.setLastException(e);
                        ba2.raiseEventFromDifferentThread(SocketWrapper.this, SocketWrapper.this, 0, new StringBuilder(String.valueOf(SocketWrapper.this.eventName)).append("_connected").toString(), true, new Object[]{Boolean.valueOf(false)});
                    }
                }
            }
        }, this, 0);
    }

    public InputStream getInputStream() throws IOException {
        return this.socket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return this.socket.getOutputStream();
    }

    public boolean getConnected() {
        return this.socket != null && this.socket.isConnected();
    }

    public void Close() throws IOException {
        if (this.socket != null) {
            final Socket s = this.socket;
            this.socket = null;
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        if (s != null) {
                            if (!(s instanceof SSLSocket)) {
                                if (!(s.isInputShutdown() || s.isClosed())) {
                                    s.shutdownInput();
                                }
                                if (!(s.isOutputShutdown() || s.isClosed())) {
                                    s.shutdownOutput();
                                }
                            }
                            if (!s.isClosed()) {
                                s.close();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            if (this.socket instanceof SSLSocket) {
                BA.submitRunnable(r, null, 0);
            } else {
                r.run();
            }
        }
    }
}
