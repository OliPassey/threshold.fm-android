package anywheresoftware.b4h.okhttp;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.BA.Version;
import anywheresoftware.b4a.keywords.Common;
import anywheresoftware.b4a.objects.collections.Map;
import anywheresoftware.b4a.objects.streams.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.JavaNetCookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.Util;
import okhttp3.internal.http.RequestLine;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

@Version(1.2f)
@ShortName("OkHttpClient")
public class OkHttpClientWrapper {
    @Hide
    public OkHttpClient client;
    private String eventName;

    /* renamed from: anywheresoftware.b4h.okhttp.OkHttpClientWrapper$1 */
    class C00851 implements HostnameVerifier {
        C00851() {
        }

        public boolean verify(String arg0, SSLSession arg1) {
            return true;
        }
    }

    @Hide
    public static class B4AAuthenticator implements Authenticator {
        private static final char[] HEXADECIMAL = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        private static Pattern ptDigest;
        public final String password;
        public final String username;

        public B4AAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public Request authenticate(Route route, Response response) throws IOException {
            if (responseCount(response) >= 3) {
                return null;
            }
            String credential;
            String raw = response.header("WWW-Authenticate");
            if (raw == null) {
                raw = "";
            }
            if (raw.toLowerCase(BA.cul).contains("digest")) {
                credential = handleDigest(response, raw);
            } else {
                credential = Credentials.basic(this.username, this.password);
                if (credential.equals(response.request().header("Authorization"))) {
                    return null;
                }
            }
            return response.request().newBuilder().header("Authorization", credential).build();
        }

        private String handleDigest(Response response, String raw) throws IOException {
            Request request = response.request();
            String methodName = request.method();
            String uri = RequestLine.requestPath(request.url());
            if (ptDigest == null) {
                ptDigest = Pattern.compile("(\\w+)=\\\"([^\"]+)\\\"");
            }
            Matcher m = ptDigest.matcher(raw);
            HashMap<String, String> params = new HashMap();
            while (m.find()) {
                params.put(m.group(1), m.group(2));
            }
            String nonce = (String) params.get("nonce");
            String realm = (String) params.get("realm");
            try {
                String serverDigestValue;
                MessageDigest md = MessageDigest.getInstance("MD5");
                StringBuilder temp = new StringBuilder();
                temp.append(this.username).append(":").append(realm).append(":").append(this.password);
                String md5a1 = encode(md.digest(temp.toString().getBytes("ISO-8859-1")));
                String a2 = new StringBuilder(String.valueOf(methodName)).append(":").append(uri).toString();
                boolean qopMissing = !raw.contains("qop");
                String md5a2 = encode(md.digest(a2.getBytes("ASCII")));
                String NC = "00000001";
                String cnonce = null;
                StringBuilder tmp2;
                if (qopMissing) {
                    tmp2 = new StringBuilder();
                    tmp2.append(md5a1);
                    tmp2.append(':');
                    tmp2.append(nonce);
                    tmp2.append(':');
                    tmp2.append(md5a2);
                    serverDigestValue = tmp2.toString();
                } else {
                    cnonce = encode(md.digest(Long.toString(System.currentTimeMillis()).getBytes("ASCII")));
                    tmp2 = new StringBuilder();
                    tmp2.append(md5a1);
                    tmp2.append(':');
                    tmp2.append(nonce);
                    tmp2.append(':');
                    tmp2.append(NC);
                    tmp2.append(':');
                    tmp2.append(cnonce);
                    tmp2.append(':');
                    tmp2.append("auth");
                    tmp2.append(':');
                    tmp2.append(md5a2);
                    serverDigestValue = tmp2.toString();
                }
                String serverDigest = encode(md.digest(serverDigestValue.getBytes("ASCII")));
                StringBuilder sb = new StringBuilder();
                sb.append("Digest ").append(param("username", this.username, true)).append(",").append(param("realm", realm, true)).append(",").append(param("nonce", nonce, true)).append(",").append(param("uri", uri, true)).append(",");
                if (!qopMissing) {
                    sb.append(param("qop", "auth", false)).append(",").append(param("nc", NC, false)).append(",").append(param("cnonce", cnonce, true)).append(",");
                }
                sb.append(param("response", serverDigest, true));
                String opaque = (String) params.get("opaque");
                if (opaque != null) {
                    sb.append(",").append(param("opaque", opaque, true));
                }
                return sb.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException();
            }
        }

        private String param(String key, String value, boolean quote) {
            return new StringBuilder(String.valueOf(key)).append("=").append(quote ? Common.QUOTE : "").append(value).append(quote ? Common.QUOTE : "").toString();
        }

        private static String encode(byte[] binaryData) {
            if (binaryData.length != 16) {
                return null;
            }
            char[] buffer = new char[32];
            for (int i = 0; i < 16; i++) {
                int low = binaryData[i] & 15;
                buffer[i * 2] = HEXADECIMAL[(binaryData[i] & 240) >> 4];
                buffer[(i * 2) + 1] = HEXADECIMAL[low];
            }
            return new String(buffer);
        }

        private int responseCount(Response response) {
            int result = 1;
            while (true) {
                response = response.priorResponse();
                if (response == null) {
                    return result;
                }
                result++;
            }
        }
    }

    class ExecuteHelper implements Runnable {
        private OkHttpRequest HttpRequest;
        private String Password;
        private int TaskId;
        private String UserName;
        private BA ba;

        public ExecuteHelper(BA ba, OkHttpRequest HttpRequest, int TaskId, String UserName, String Password) {
            this.ba = ba;
            this.HttpRequest = HttpRequest;
            this.TaskId = TaskId;
            this.UserName = UserName;
            this.Password = Password;
        }

        public void run() {
            String reason;
            Response response = null;
            OkHttpResponse res = new OkHttpResponse();
            res.innerInitialize(OkHttpClientWrapper.this);
            try {
                Builder builder = OkHttpClientWrapper.this.client.newBuilder();
                OkHttpClientWrapper.setTimeout(builder, this.HttpRequest.timeout);
                Request req = this.HttpRequest.builder.build();
                boolean recoverable = ((req.body() instanceof PostPayload) && ((PostPayload) req.body()).data == null) ? false : true;
                if (this.UserName != null && this.UserName.length() > 0) {
                    builder.authenticator(new B4AAuthenticator(this.UserName, this.Password));
                    if ((req.body() instanceof PostPayload) && !recoverable) {
                        req = req.newBuilder().header("Authorization", Credentials.basic(this.UserName, this.Password)).build();
                    }
                }
                builder.retryOnConnectionFailure(recoverable);
                response = OkHttpClientWrapper.this.executeWithTimeout(this, builder.build(), req, this.ba, this.TaskId);
                if (response != null) {
                    res.response = response;
                    if (response.isSuccessful()) {
                        this.ba.raiseEventFromDifferentThread(OkHttpClientWrapper.this.client, OkHttpClientWrapper.this, this.TaskId, new StringBuilder(String.valueOf(OkHttpClientWrapper.this.eventName)).append("_responsesuccess").toString(), true, new Object[]{res, Integer.valueOf(this.TaskId)});
                        return;
                    }
                    throw new Exception();
                }
            } catch (Exception e) {
                int statusCode;
                if (response != null) {
                    statusCode = response.code();
                    reason = response.message();
                    if (reason == null) {
                        reason = "";
                    }
                } else {
                    e.printStackTrace();
                    reason = e.toString();
                    statusCode = -1;
                }
                if (response != null) {
                    try {
                        res.errorMessage = response.body().string();
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
                this.ba.raiseEventFromDifferentThread(OkHttpClientWrapper.this.client, OkHttpClientWrapper.this, this.TaskId, new StringBuilder(String.valueOf(OkHttpClientWrapper.this.eventName)).append("_responseerror").toString(), false, new Object[]{res, reason, Integer.valueOf(statusCode), Integer.valueOf(this.TaskId)});
            }
        }
    }

    private static class NaiveTrustManager implements X509TrustManager {
        private NaiveTrustManager() {
        }

        public void checkClientTrusted(X509Certificate[] cert, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] cert, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    @ShortName("OkHttpRequest")
    public static class OkHttpRequest {
        @Hide
        public Request.Builder builder;
        @Hide
        public PostPayload pp;
        int timeout = 30000;

        public void InitializeGet(String URL) {
            this.builder = new Request.Builder().url(URL).get();
        }

        public void InitializeHead(String URL) {
            this.builder = new Request.Builder().url(URL).head();
        }

        public void InitializeDelete(String URL) {
            this.builder = new Request.Builder().url(URL).delete();
        }

        public void InitializeDelete2(String URL, byte[] Data) {
            this.pp = PostPayload.createFromArray(Data);
            this.builder = new Request.Builder().url(URL).delete(this.pp);
        }

        public void InitializePost(String URL, InputStream InputStream, int Length) {
            this.pp = PostPayload.createFromStream(InputStream, Length);
            this.builder = new Request.Builder().url(URL).post(this.pp);
        }

        public void InitializePost2(String URL, byte[] Data) {
            this.pp = PostPayload.createFromArray(Data);
            this.builder = new Request.Builder().url(URL).post(this.pp);
        }

        public void InitializePut(String URL, InputStream InputStream, int Length) {
            this.pp = PostPayload.createFromStream(InputStream, Length);
            this.builder = new Request.Builder().url(URL).put(this.pp);
        }

        public void InitializePut2(String URL, byte[] Data) {
            this.pp = PostPayload.createFromArray(Data);
            this.builder = new Request.Builder().url(URL).put(this.pp);
        }

        public void InitializePatch(String URL, InputStream InputStream, int Length) {
            this.pp = PostPayload.createFromStream(InputStream, Length);
            this.builder = new Request.Builder().url(URL).patch(this.pp);
        }

        public void InitializePatch2(String URL, byte[] Data) {
            this.pp = PostPayload.createFromArray(Data);
            this.builder = new Request.Builder().url(URL).patch(this.pp);
        }

        public void SetHeader(String Name, String Value) {
            this.builder.addHeader(Name, Value);
        }

        public void RemoveHeaders(String Name) {
            this.builder.removeHeader(Name);
        }

        public int getTimeout() {
            return this.timeout;
        }

        public void setTimeout(int t) {
            this.timeout = t;
        }

        public void SetContentType(String ContentType) {
            if (this.pp == null) {
                throw new RuntimeException("Request does not support this method.");
            }
            this.pp.contentType = ContentType;
        }

        public void SetContentEncoding(String Encoding) {
            this.builder.header("Content-Encoding", Encoding);
        }
    }

    @ShortName("OkHttpResponse")
    public static class OkHttpResponse {
        String errorMessage = "";
        private OkHttpClientWrapper parent;
        @Hide
        public Response response;

        private void innerInitialize(OkHttpClientWrapper parent) {
            this.parent = parent;
        }

        public Map GetHeaders() {
            return convertHeaders(this.response.headers().toMultimap());
        }

        public String getContentType() {
            return this.response.header("Content-Type", "");
        }

        public String getContentEncoding() {
            return this.response.header("Content-Encoding", "");
        }

        public String getErrorResponse() {
            return this.errorMessage;
        }

        public long getContentLength() throws IOException {
            return this.response.body().contentLength();
        }

        static Map convertHeaders(java.util.Map<String, List<String>> headers) {
            Map m = new Map();
            m.Initialize();
            for (Entry<String, List<String>> e : headers.entrySet()) {
                m.Put(e.getKey(), e.getValue());
            }
            return m;
        }

        public int getStatusCode() {
            return this.response.code();
        }

        public void Release() throws IOException {
            if (this.response != null && this.response.body() != null) {
                Util.closeQuietly(this.response.body().source());
            }
        }

        public boolean GetAsynchronously(BA ba, String EventName, OutputStream Output, boolean CloseOutput, int TaskId) throws IOException {
            if (BA.isTaskRunning(this.parent, TaskId)) {
                Release();
                return false;
            }
            final OutputStream outputStream = Output;
            final boolean z = CloseOutput;
            final BA ba2 = ba;
            final int i = TaskId;
            final String str = EventName;
            BA.submitRunnable(new Runnable() {
                public void run() {
                    try {
                        File.Copy2(OkHttpResponse.this.response.body().byteStream(), outputStream);
                        if (z) {
                            outputStream.close();
                        }
                        ba2.raiseEventFromDifferentThread(OkHttpResponse.this.response, OkHttpResponse.this.parent, i, new StringBuilder(String.valueOf(str.toLowerCase(BA.cul))).append("_streamfinish").toString(), true, new Object[]{Boolean.valueOf(true), Integer.valueOf(i)});
                    } catch (IOException e) {
                        ba2.setLastException(e);
                        if (z) {
                            try {
                                outputStream.close();
                            } catch (IOException e2) {
                            }
                        }
                        ba2.raiseEventFromDifferentThread(OkHttpResponse.this.response, OkHttpResponse.this.parent, i, new StringBuilder(String.valueOf(str.toLowerCase(BA.cul))).append("_streamfinish").toString(), true, new Object[]{Boolean.valueOf(false), Integer.valueOf(i)});
                    }
                    OkHttpResponse.this.response.body().close();
                }
            }, this.parent, TaskId);
            return true;
        }
    }

    @Hide
    public static class PostPayload extends RequestBody {
        private long contentLength = -1;
        public String contentType = "application/x-www-form-urlencoded";
        public byte[] data;
        private Source source;

        public static PostPayload createFromStream(InputStream input, int Length) {
            PostPayload pp = new PostPayload();
            pp.source = Okio.source(input);
            pp.contentLength = (long) Length;
            return pp;
        }

        public static PostPayload createFromArray(byte[] data) {
            PostPayload pp = new PostPayload();
            pp.data = data;
            return pp;
        }

        public MediaType contentType() {
            return MediaType.parse(this.contentType);
        }

        public long contentLength() throws IOException {
            if (this.data != null) {
                return (long) this.data.length;
            }
            return this.contentLength;
        }

        public void writeTo(BufferedSink sink) throws IOException {
            if (this.data != null) {
                sink.write(this.data);
            } else {
                sink.write(this.source, this.contentLength);
            }
        }
    }

    public void Initialize(String EventName) {
        this.client = sharedInit(EventName).build();
    }

    public void InitializeAcceptAll(String EventName) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        Builder builder = sharedInit(EventName);
        builder.hostnameVerifier(new C00851());
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[]{new NaiveTrustManager()}, new SecureRandom());
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(null);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length == 1 && (trustManagers[0] instanceof X509TrustManager)) {
            builder.sslSocketFactory(sslSocketFactory, trustManagers[0]);
            this.client = builder.build();
            return;
        }
        throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
    }

    @Hide
    public Builder sharedInit(String EventName) {
        Builder builder = new Builder();
        this.eventName = EventName.toLowerCase(BA.cul);
        setTimeout(builder, 30000);
        CookieManager cm = new CookieManager();
        cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        builder.cookieJar(new JavaNetCookieJar(cm));
        return builder;
    }

    public boolean IsInitialized() {
        return this.client != null;
    }

    static void setTimeout(Builder builder, int TimeoutMs) {
        builder.connectTimeout((long) TimeoutMs, TimeUnit.MILLISECONDS);
        builder.writeTimeout((long) TimeoutMs, TimeUnit.MILLISECONDS);
        builder.readTimeout((long) TimeoutMs, TimeUnit.MILLISECONDS);
    }

    public boolean Execute(BA ba, OkHttpRequest HttpRequest, int TaskId) throws IOException {
        return ExecuteCredentials(ba, HttpRequest, TaskId, null, null);
    }

    public boolean ExecuteCredentials(BA ba, OkHttpRequest Request, int TaskId, String UserName, String Password) {
        if (BA.isTaskRunning(this, TaskId)) {
            return false;
        }
        BA.submitRunnable(new ExecuteHelper(ba, Request, TaskId, UserName, Password), this, TaskId);
        return true;
    }

    private Response executeWithTimeout(Runnable handler, OkHttpClient myClient, Request req, BA ba, int TaskId) throws IOException {
        return myClient.newCall(req).execute();
    }
}
