package okhttp3;

import java.io.IOException;

public interface Authenticator {
    public static final Authenticator NONE = new C00871();

    /* renamed from: okhttp3.Authenticator$1 */
    static class C00871 implements Authenticator {
        C00871() {
        }

        public Request authenticate(Route route, Response response) {
            return null;
        }
    }

    Request authenticate(Route route, Response response) throws IOException;
}
