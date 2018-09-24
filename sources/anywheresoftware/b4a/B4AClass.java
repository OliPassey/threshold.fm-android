package anywheresoftware.b4a;

public interface B4AClass {

    public static abstract class ImplB4AClass implements B4AClass {
        public BA ba;
        protected ImplB4AClass mostCurrent;

        public BA getBA() {
            return this.ba;
        }

        public BA getActivityBA() {
            BA aba = null;
            if (this.ba.sharedProcessBA.activityBA != null) {
                aba = (BA) this.ba.sharedProcessBA.activityBA.get();
            }
            if (aba == null) {
                return this.ba;
            }
            return aba;
        }

        public String toString() {
            return BA.TypeToString(this, true);
        }

        public boolean IsInitialized() {
            return this.ba != null;
        }
    }

    boolean IsInitialized();

    BA getActivityBA();

    BA getBA();
}
