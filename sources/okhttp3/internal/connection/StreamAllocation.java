package okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;
import okhttp3.Address;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Route;
import okhttp3.internal.Internal;
import okhttp3.internal.Util;
import okhttp3.internal.http.HttpCodec;
import okhttp3.internal.http1.Http1Codec;
import okhttp3.internal.http2.ConnectionShutdownException;
import okhttp3.internal.http2.ErrorCode;
import okhttp3.internal.http2.Http2Codec;
import okhttp3.internal.http2.StreamResetException;

public final class StreamAllocation {
    static final /* synthetic */ boolean $assertionsDisabled = (!StreamAllocation.class.desiredAssertionStatus());
    public final Address address;
    private final Object callStackTrace;
    private boolean canceled;
    private HttpCodec codec;
    private RealConnection connection;
    private final ConnectionPool connectionPool;
    private int refusedStreamCount;
    private boolean released;
    private Route route;
    private final RouteSelector routeSelector;

    public static final class StreamAllocationReference extends WeakReference<StreamAllocation> {
        public final Object callStackTrace;

        StreamAllocationReference(StreamAllocation referent, Object callStackTrace) {
            super(referent);
            this.callStackTrace = callStackTrace;
        }
    }

    public StreamAllocation(ConnectionPool connectionPool, Address address, Object callStackTrace) {
        this.connectionPool = connectionPool;
        this.address = address;
        this.routeSelector = new RouteSelector(address, routeDatabase());
        this.callStackTrace = callStackTrace;
    }

    public HttpCodec newStream(OkHttpClient client, boolean doExtensiveHealthChecks) {
        int connectTimeout = client.connectTimeoutMillis();
        int readTimeout = client.readTimeoutMillis();
        int writeTimeout = client.writeTimeoutMillis();
        try {
            HttpCodec resultCodec;
            RealConnection resultConnection = findHealthyConnection(connectTimeout, readTimeout, writeTimeout, client.retryOnConnectionFailure(), doExtensiveHealthChecks);
            if (resultConnection.http2Connection != null) {
                resultCodec = new Http2Codec(client, this, resultConnection.http2Connection);
            } else {
                resultConnection.socket().setSoTimeout(readTimeout);
                resultConnection.source.timeout().timeout((long) readTimeout, TimeUnit.MILLISECONDS);
                resultConnection.sink.timeout().timeout((long) writeTimeout, TimeUnit.MILLISECONDS);
                resultCodec = new Http1Codec(client, this, resultConnection.source, resultConnection.sink);
            }
            synchronized (this.connectionPool) {
                this.codec = resultCodec;
            }
            return resultCodec;
        } catch (IOException e) {
            throw new RouteException(e);
        }
    }

    private RealConnection findHealthyConnection(int connectTimeout, int readTimeout, int writeTimeout, boolean connectionRetryEnabled, boolean doExtensiveHealthChecks) throws IOException {
        RealConnection candidate;
        while (true) {
            candidate = findConnection(connectTimeout, readTimeout, writeTimeout, connectionRetryEnabled);
            synchronized (this.connectionPool) {
                if (candidate.successCount != 0) {
                    if (candidate.isHealthy(doExtensiveHealthChecks)) {
                        break;
                    }
                    noNewStreams();
                } else {
                    break;
                }
            }
        }
        return candidate;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private okhttp3.internal.connection.RealConnection findConnection(int r10, int r11, int r12, boolean r13) throws java.io.IOException {
        /*
        r9 = this;
        r2 = r9.connectionPool;
        monitor-enter(r2);
        r1 = r9.released;	 Catch:{ all -> 0x000f }
        if (r1 == 0) goto L_0x0012;
    L_0x0007:
        r1 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x000f }
        r3 = "released";
        r1.<init>(r3);	 Catch:{ all -> 0x000f }
        throw r1;	 Catch:{ all -> 0x000f }
    L_0x000f:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x000f }
        throw r1;
    L_0x0012:
        r1 = r9.codec;	 Catch:{ all -> 0x000f }
        if (r1 == 0) goto L_0x001e;
    L_0x0016:
        r1 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x000f }
        r3 = "codec != null";
        r1.<init>(r3);	 Catch:{ all -> 0x000f }
        throw r1;	 Catch:{ all -> 0x000f }
    L_0x001e:
        r1 = r9.canceled;	 Catch:{ all -> 0x000f }
        if (r1 == 0) goto L_0x002a;
    L_0x0022:
        r1 = new java.io.IOException;	 Catch:{ all -> 0x000f }
        r3 = "Canceled";
        r1.<init>(r3);	 Catch:{ all -> 0x000f }
        throw r1;	 Catch:{ all -> 0x000f }
    L_0x002a:
        r6 = r9.connection;	 Catch:{ all -> 0x000f }
        if (r6 == 0) goto L_0x0034;
    L_0x002e:
        r1 = r6.noNewStreams;	 Catch:{ all -> 0x000f }
        if (r1 != 0) goto L_0x0034;
    L_0x0032:
        monitor-exit(r2);	 Catch:{ all -> 0x000f }
    L_0x0033:
        return r6;
    L_0x0034:
        r1 = okhttp3.internal.Internal.instance;	 Catch:{ all -> 0x000f }
        r3 = r9.connectionPool;	 Catch:{ all -> 0x000f }
        r4 = r9.address;	 Catch:{ all -> 0x000f }
        r7 = r1.get(r3, r4, r9);	 Catch:{ all -> 0x000f }
        if (r7 == 0) goto L_0x0045;
    L_0x0040:
        r9.connection = r7;	 Catch:{ all -> 0x000f }
        monitor-exit(r2);	 Catch:{ all -> 0x000f }
        r6 = r7;
        goto L_0x0033;
    L_0x0045:
        r8 = r9.route;	 Catch:{ all -> 0x000f }
        monitor-exit(r2);	 Catch:{ all -> 0x000f }
        if (r8 != 0) goto L_0x0059;
    L_0x004a:
        r1 = r9.routeSelector;
        r8 = r1.next();
        r2 = r9.connectionPool;
        monitor-enter(r2);
        r9.route = r8;	 Catch:{ all -> 0x007c }
        r1 = 0;
        r9.refusedStreamCount = r1;	 Catch:{ all -> 0x007c }
        monitor-exit(r2);	 Catch:{ all -> 0x007c }
    L_0x0059:
        r0 = new okhttp3.internal.connection.RealConnection;
        r0.<init>(r8);
        r2 = r9.connectionPool;
        monitor-enter(r2);
        r9.acquire(r0);	 Catch:{ all -> 0x0079 }
        r1 = okhttp3.internal.Internal.instance;	 Catch:{ all -> 0x0079 }
        r3 = r9.connectionPool;	 Catch:{ all -> 0x0079 }
        r1.put(r3, r0);	 Catch:{ all -> 0x0079 }
        r9.connection = r0;	 Catch:{ all -> 0x0079 }
        r1 = r9.canceled;	 Catch:{ all -> 0x0079 }
        if (r1 == 0) goto L_0x007f;
    L_0x0071:
        r1 = new java.io.IOException;	 Catch:{ all -> 0x0079 }
        r3 = "Canceled";
        r1.<init>(r3);	 Catch:{ all -> 0x0079 }
        throw r1;	 Catch:{ all -> 0x0079 }
    L_0x0079:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0079 }
        throw r1;
    L_0x007c:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x007c }
        throw r1;
    L_0x007f:
        monitor-exit(r2);	 Catch:{ all -> 0x0079 }
        r1 = r9.address;
        r4 = r1.connectionSpecs();
        r1 = r10;
        r2 = r11;
        r3 = r12;
        r5 = r13;
        r0.connect(r1, r2, r3, r4, r5);
        r1 = r9.routeDatabase();
        r2 = r0.route();
        r1.connected(r2);
        r6 = r0;
        goto L_0x0033;
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.connection.StreamAllocation.findConnection(int, int, int, boolean):okhttp3.internal.connection.RealConnection");
    }

    public void streamFinished(boolean noNewStreams, HttpCodec codec) {
        synchronized (this.connectionPool) {
            if (codec != null) {
                if (codec == this.codec) {
                    if (!noNewStreams) {
                        RealConnection realConnection = this.connection;
                        realConnection.successCount++;
                    }
                }
            }
            throw new IllegalStateException("expected " + this.codec + " but was " + codec);
        }
        deallocate(noNewStreams, false, true);
    }

    public HttpCodec codec() {
        HttpCodec httpCodec;
        synchronized (this.connectionPool) {
            httpCodec = this.codec;
        }
        return httpCodec;
    }

    private RouteDatabase routeDatabase() {
        return Internal.instance.routeDatabase(this.connectionPool);
    }

    public synchronized RealConnection connection() {
        return this.connection;
    }

    public void release() {
        deallocate(false, true, false);
    }

    public void noNewStreams() {
        deallocate(true, false, false);
    }

    private void deallocate(boolean noNewStreams, boolean released, boolean streamFinished) {
        RealConnection connectionToClose = null;
        synchronized (this.connectionPool) {
            if (streamFinished) {
                this.codec = null;
            }
            if (released) {
                this.released = true;
            }
            if (this.connection != null) {
                if (noNewStreams) {
                    this.connection.noNewStreams = true;
                }
                if (this.codec == null && (this.released || this.connection.noNewStreams)) {
                    release(this.connection);
                    if (this.connection.allocations.isEmpty()) {
                        this.connection.idleAtNanos = System.nanoTime();
                        if (Internal.instance.connectionBecameIdle(this.connectionPool, this.connection)) {
                            connectionToClose = this.connection;
                        }
                    }
                    this.connection = null;
                }
            }
        }
        if (connectionToClose != null) {
            Util.closeQuietly(connectionToClose.socket());
        }
    }

    public void cancel() {
        synchronized (this.connectionPool) {
            this.canceled = true;
            HttpCodec codecToCancel = this.codec;
            RealConnection connectionToCancel = this.connection;
        }
        if (codecToCancel != null) {
            codecToCancel.cancel();
        } else if (connectionToCancel != null) {
            connectionToCancel.cancel();
        }
    }

    public void streamFailed(IOException e) {
        boolean noNewStreams = false;
        synchronized (this.connectionPool) {
            if (e instanceof StreamResetException) {
                StreamResetException streamResetException = (StreamResetException) e;
                if (streamResetException.errorCode == ErrorCode.REFUSED_STREAM) {
                    this.refusedStreamCount++;
                }
                if (streamResetException.errorCode != ErrorCode.REFUSED_STREAM || this.refusedStreamCount > 1) {
                    noNewStreams = true;
                    this.route = null;
                }
            } else if (!(this.connection == null || this.connection.isMultiplexed()) || (e instanceof ConnectionShutdownException)) {
                noNewStreams = true;
                if (this.connection.successCount == 0) {
                    if (!(this.route == null || e == null)) {
                        this.routeSelector.connectFailed(this.route, e);
                    }
                    this.route = null;
                }
            }
        }
        deallocate(noNewStreams, false, true);
    }

    public void acquire(RealConnection connection) {
        if ($assertionsDisabled || Thread.holdsLock(this.connectionPool)) {
            connection.allocations.add(new StreamAllocationReference(this, this.callStackTrace));
            return;
        }
        throw new AssertionError();
    }

    private void release(RealConnection connection) {
        int size = connection.allocations.size();
        for (int i = 0; i < size; i++) {
            if (((Reference) connection.allocations.get(i)).get() == this) {
                connection.allocations.remove(i);
                return;
            }
        }
        throw new IllegalStateException();
    }

    public boolean hasMoreRoutes() {
        return this.route != null || this.routeSelector.hasNext();
    }

    public String toString() {
        return this.address.toString();
    }
}
