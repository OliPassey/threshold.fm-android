package anywheresoftware.b4a;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Process;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.util.Log;
import anywheresoftware.b4a.Msgbox.DialogResponse;
import anywheresoftware.b4a.keywords.Common;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class BA {
    private static byte[][] _b;
    public static Application applicationContext;
    public static IBridgeLog bridgeLog;
    private static int checkStackTraceEvery50;
    public static final Locale cul = Locale.US;
    public static String debugLine;
    public static int debugLineNum;
    public static boolean debugMode = false;
    public static float density = 1.0f;
    public static final Handler handler = new Handler();
    public static NumberFormat numberFormat;
    public static NumberFormat numberFormat2;
    public static String packageName;
    public static final ThreadLocal<Object> senderHolder = new ThreadLocal();
    public static boolean shellMode = false;
    private static volatile B4AThreadPool threadPool;
    private static HashMap<String, ArrayList<Runnable>> uninitializedActivitiesMessagesDuringPaused;
    public static WarningEngine warningEngine;
    public final Activity activity;
    public final String className;
    public final Context context;
    public final Object eventsTarget;
    public final HashMap<String, Method> htSubs;
    public final BA processBA;
    public Service service;
    public final SharedProcessBA sharedProcessBA;
    public final BALayout vg;
    public HashMap<String, LinkedList<WaitForEvent>> waitForEvents;

    public interface B4ARunnable extends Runnable {
    }

    /* renamed from: anywheresoftware.b4a.BA$3 */
    class C00033 implements Runnable {
        private final /* synthetic */ Object val$Sender;
        private final /* synthetic */ BA val$ba;
        private final /* synthetic */ Callable val$callable;
        private final /* synthetic */ Object[] val$errorResult;
        private final /* synthetic */ String val$eventName;

        C00033(Callable callable, Object obj, BA ba, String str, Object[] objArr) {
            this.val$callable = callable;
            this.val$Sender = obj;
            this.val$ba = ba;
            this.val$eventName = str;
            this.val$errorResult = objArr;
        }

        public void run() {
            Object send;
            try {
                Object[] ret = (Object[]) this.val$callable.call();
                send = this.val$Sender;
                if (this.val$Sender instanceof ObjectWrapper) {
                    send = ((ObjectWrapper) this.val$Sender).getObjectOrNull();
                }
                this.val$ba.raiseEventFromDifferentThread(send, null, 0, this.val$eventName, false, ret);
            } catch (Exception e) {
                e.printStackTrace();
                this.val$ba.setLastException(e);
                send = this.val$Sender;
                if (this.val$Sender instanceof ObjectWrapper) {
                    send = ((ObjectWrapper) this.val$Sender).getObjectOrNull();
                }
                this.val$ba.raiseEventFromDifferentThread(send, null, 0, this.val$eventName, false, this.val$errorResult);
            }
        }
    }

    /* renamed from: anywheresoftware.b4a.BA$5 */
    class C00055 implements OnActivityResultListener {
        C00055(int i, byte[] bArr) throws UnsupportedEncodingException, NameNotFoundException {
            if (BA._b == null) {
                BA._b = new byte[4][];
                BA._b[0] = BA.packageName.getBytes("UTF8");
                BA._b[1] = BA.applicationContext.getPackageManager().getPackageInfo(BA.packageName, 0).versionName.getBytes("UTF8");
                if (BA._b[1].length == 0) {
                    BA._b[1] = "jsdkfh".getBytes("UTF8");
                }
                BA._b[2] = new byte[]{(byte) BA.applicationContext.getPackageManager().getPackageInfo(BA.packageName, 0).versionCode};
            }
            int value = (i / 7) + 1234;
            BA._b[3] = new byte[]{(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value};
            for (int __b = 0; __b < 4; __b++) {
                int b = 0;
                while (b < bArr.length) {
                    try {
                        bArr[b] = (byte) (bArr[b] ^ BA._b[__b][b % BA._b[__b].length]);
                        b++;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
            return false;
        }
    }

    public @interface ActivityObject {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Author {
        String value();
    }

    public static class B4AExceptionHandler implements UncaughtExceptionHandler {
        public final UncaughtExceptionHandler original = Thread.getDefaultUncaughtExceptionHandler();

        public void uncaughtException(Thread t, Throwable e) {
            BA.printException(e, true);
            if (BA.bridgeLog != null) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e2) {
                }
            }
            this.original.uncaughtException(t, e);
        }
    }

    public interface B4aDebuggable {
        Object[] debug(int i, boolean[] zArr);
    }

    public interface CheckForReinitialize {
        boolean IsInitialized();
    }

    @Hide
    @Retention(RetentionPolicy.SOURCE)
    public @interface CustomClass {
        String fileNameWithoutExtension();

        String name();

        int priority() default 0;
    }

    @Hide
    @Retention(RetentionPolicy.SOURCE)
    public @interface CustomClasses {
        CustomClass[] values();
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DependsOn {
        String[] values();
    }

    public @interface DesignerName {
        String value();
    }

    @Hide
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DesignerProperties {
        Property[] values();
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DontInheritEvents {
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Events {
        String[] values();
    }

    public @interface Hide {
    }

    public interface IBridgeLog {
        void offer(String str);
    }

    public interface IterableList {
        Object Get(int i);

        int getSize();
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Permissions {
        String[] values();
    }

    public @interface Pixel {
    }

    @Hide
    @Retention(RetentionPolicy.SOURCE)
    public @interface Property {
        String defaultValue();

        String description() default "";

        String displayName();

        String fieldType();

        String key();

        String list() default "";

        String maxRange() default "";

        String minRange() default "";
    }

    private static class RaiseEventWhenFirstCreate implements Runnable {
        Object[] arguments;
        BA ba;
        String eventName;
        Object sender;

        private RaiseEventWhenFirstCreate() {
        }

        public void run() {
            this.ba.raiseEvent2(this.sender, true, this.eventName, true, this.arguments);
        }
    }

    @Target({ElementType.METHOD})
    public @interface RaisesSynchronousEvents {
    }

    public static abstract class ResumableSub {
        public int catchState;
        public boolean completed;
        public int state;
        public BA waitForBA;

        public abstract void resume(BA ba, Object[] objArr) throws Exception;
    }

    public static class SharedProcessBA {
        public WeakReference<BA> activityBA;
        boolean ignoreEventsFromOtherThreadsDuringMsgboxError = false;
        volatile boolean isActivityPaused = true;
        public final boolean isService;
        Exception lastException = null;
        ArrayList<Runnable> messagesDuringPaused;
        int numberOfStackedEvents = 0;
        int onActivityResultCode = 1;
        HashMap<Integer, WeakReference<IOnActivityResult>> onActivityResultMap;
        public Object sender;

        public SharedProcessBA(boolean isService) {
            this.isService = isService;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface ShortName {
        String value();
    }

    public interface SubDelegator {
        public static final Object SubNotFound = new Object();

        Object callSub(String str, Object obj, Object[] objArr) throws Exception;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Version {
        float value();
    }

    public static class WaitForEvent {
        public ResumableSub rs;
        public WeakReference<Object> senderFilter;

        public WaitForEvent(ResumableSub rs, Object senderFilter) {
            this.rs = rs;
            if (senderFilter == null) {
                this.senderFilter = null;
            } else {
                this.senderFilter = new WeakReference(senderFilter);
            }
        }

        public boolean noFilter() {
            return this.senderFilter == null;
        }

        public boolean cleared() {
            return this.senderFilter != null && this.senderFilter.get() == null;
        }
    }

    public static abstract class WarningEngine {
        public static final int FULLSCREEN_MISMATCH = 1004;
        public static final int OBJECT_ALREADY_INITIALIZED = 1003;
        public static final int SAME_OBJECT_ADDED_TO_LIST = 1002;
        public static final int ZERO_SIZE_PANEL = 1001;

        public abstract void checkFullScreenInLayout(boolean z, boolean z2);

        protected abstract void warnImpl(int i);

        public static void warn(int warning) {
            if (BA.warningEngine != null) {
                BA.warningEngine.warnImpl(warning);
            }
        }
    }

    static {
        Thread.setDefaultUncaughtExceptionHandler(new B4AExceptionHandler());
    }

    public BA(BA otherBA, Object eventTarget, HashMap<String, Method> hashMap, String className) {
        HashMap hashMap2;
        this.vg = otherBA.vg;
        this.eventsTarget = eventTarget;
        if (hashMap == null) {
            hashMap2 = new HashMap();
        }
        this.htSubs = hashMap2;
        this.processBA = null;
        this.activity = otherBA.activity;
        this.context = otherBA.context;
        this.service = otherBA.service;
        this.sharedProcessBA = otherBA.sharedProcessBA == null ? otherBA.processBA.sharedProcessBA : otherBA.sharedProcessBA;
        this.className = className;
    }

    public BA(Context context, BALayout vg, BA processBA, String notUsed, String className) {
        Activity activity;
        boolean isService;
        if (context != null) {
            density = context.getResources().getDisplayMetrics().density;
        }
        if (context == null || !(context instanceof Activity)) {
            activity = null;
        } else {
            activity = (Activity) context;
            applicationContext = activity.getApplication();
        }
        if (context == null || !(context instanceof Service)) {
            isService = false;
        } else {
            isService = true;
            applicationContext = ((Service) context).getApplication();
        }
        if (context != null && packageName == null) {
            packageName = context.getPackageName();
            try {
                Class<?> c = Class.forName("anywheresoftware.b4a.remotelogger.RemoteLogger");
                c.getMethod("Start", new Class[0]).invoke(c.newInstance(), new Object[0]);
            } catch (ClassNotFoundException e) {
                System.out.println("Bridge logger not enabled.");
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        this.eventsTarget = null;
        if (className.endsWith(".starter")) {
            this.context = applicationContext;
        } else {
            this.context = context;
        }
        this.activity = activity;
        this.htSubs = new HashMap();
        this.className = className;
        this.processBA = processBA;
        this.vg = vg;
        if (processBA == null) {
            this.sharedProcessBA = new SharedProcessBA(isService);
        } else {
            this.sharedProcessBA = null;
        }
    }

    public boolean subExists(String sub) {
        if (this.processBA != null) {
            return this.processBA.subExists(sub);
        }
        return this.htSubs.containsKey(sub);
    }

    public boolean runHook(String hook, Object target, Object[] args) {
        if (!subExists(hook)) {
            return false;
        }
        try {
            Boolean b = (Boolean) ((Method) this.htSubs.get(hook)).invoke(target, args);
            if (b == null || !b.booleanValue()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object raiseEvent(Object sender, String event, Object... params) {
        return raiseEvent2(sender, false, event, false, params);
    }

    public java.lang.Object raiseEvent2(java.lang.Object r13, boolean r14, java.lang.String r15, boolean r16, java.lang.Object... r17) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:anywheresoftware.b4a.BA.raiseEvent2(java.lang.Object, boolean, java.lang.String, boolean, java.lang.Object[]):java.lang.Object. bs: [B:25:0x0097, B:53:0x0101]
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:86)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/1408652377.run(Unknown Source)
*/
        /*
        r12 = this;
        r1 = r12.processBA;
        if (r1 == 0) goto L_0x0012;
    L_0x0004:
        r1 = r12.processBA;
        r2 = r13;
        r3 = r14;
        r4 = r15;
        r5 = r16;
        r6 = r17;
        r1 = r1.raiseEvent2(r2, r3, r4, r5, r6);
    L_0x0011:
        return r1;
    L_0x0012:
        r1 = r12.sharedProcessBA;
        r1 = r1.isActivityPaused;
        if (r1 == 0) goto L_0x0030;
    L_0x0018:
        if (r14 != 0) goto L_0x0030;
    L_0x001a:
        r1 = java.lang.System.out;
        r2 = new java.lang.StringBuilder;
        r3 = "ignoring event: ";
        r2.<init>(r3);
        r2 = r2.append(r15);
        r2 = r2.toString();
        r1.println(r2);
        r1 = 0;
        goto L_0x0011;
    L_0x0030:
        r1 = r12.sharedProcessBA;	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r2 = r1.numberOfStackedEvents;	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r2 = r2 + 1;	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r1.numberOfStackedEvents = r2;	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r1 = senderHolder;	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r1.set(r13);	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r1 = r12.waitForEvents;	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        if (r1 == 0) goto L_0x0059;	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
    L_0x0041:
        r0 = r17;	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r1 = r12.checkAndRunWaitForEvent(r13, r15, r0);	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        if (r1 == 0) goto L_0x0059;
    L_0x0049:
        r1 = r12.sharedProcessBA;
        r2 = r1.numberOfStackedEvents;
        r2 = r2 + -1;
        r1.numberOfStackedEvents = r2;
        r1 = senderHolder;
        r2 = 0;
        r1.set(r2);
        r1 = 0;
        goto L_0x0011;
    L_0x0059:
        r1 = r12.htSubs;	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r10 = r1.get(r15);	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r10 = (java.lang.reflect.Method) r10;	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        if (r10 == 0) goto L_0x00a8;
    L_0x0063:
        r1 = r12.eventsTarget;	 Catch:{ IllegalArgumentException -> 0x007a }
        r0 = r17;	 Catch:{ IllegalArgumentException -> 0x007a }
        r1 = r10.invoke(r1, r0);	 Catch:{ IllegalArgumentException -> 0x007a }
        r2 = r12.sharedProcessBA;
        r3 = r2.numberOfStackedEvents;
        r3 = r3 + -1;
        r2.numberOfStackedEvents = r3;
        r2 = senderHolder;
        r3 = 0;
        r2.set(r3);
        goto L_0x0011;
    L_0x007a:
        r8 = move-exception;
        r1 = new java.lang.Exception;	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r2 = new java.lang.StringBuilder;	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r3 = "Sub ";	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r2.<init>(r3);	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r2 = r2.append(r15);	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r3 = " signature does not match expected signature.";	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r2 = r2.append(r3);	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r2 = r2.toString();	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r1.<init>(r2);	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        throw r1;	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
    L_0x0096:
        r8 = move-exception;
        throw r8;	 Catch:{ all -> 0x0098 }
    L_0x0098:
        r1 = move-exception;
        r2 = r12.sharedProcessBA;
        r3 = r2.numberOfStackedEvents;
        r3 = r3 + -1;
        r2.numberOfStackedEvents = r3;
        r2 = senderHolder;
        r3 = 0;
        r2.set(r3);
        throw r1;
    L_0x00a8:
        if (r16 == 0) goto L_0x0177;
    L_0x00aa:
        r1 = new java.lang.Exception;	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r2 = new java.lang.StringBuilder;	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r3 = "Sub ";	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r2.<init>(r3);	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r2 = r2.append(r15);	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r3 = " was not found.";	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r2 = r2.append(r3);	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r2 = r2.toString();	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        r1.<init>(r2);	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
        throw r1;	 Catch:{ B4AUncaughtException -> 0x0096, Throwable -> 0x00c5 }
    L_0x00c5:
        r8 = move-exception;
        r1 = r8 instanceof java.lang.reflect.InvocationTargetException;	 Catch:{ all -> 0x0098 }
        if (r1 == 0) goto L_0x00ce;	 Catch:{ all -> 0x0098 }
    L_0x00ca:
        r8 = r8.getCause();	 Catch:{ all -> 0x0098 }
    L_0x00ce:
        r1 = r8 instanceof anywheresoftware.b4a.B4AUncaughtException;	 Catch:{ all -> 0x0098 }
        if (r1 == 0) goto L_0x00f4;	 Catch:{ all -> 0x0098 }
    L_0x00d2:
        r1 = r12.sharedProcessBA;	 Catch:{ all -> 0x0098 }
        r1 = r1.numberOfStackedEvents;	 Catch:{ all -> 0x0098 }
        r2 = 1;	 Catch:{ all -> 0x0098 }
        if (r1 <= r2) goto L_0x00dc;	 Catch:{ all -> 0x0098 }
    L_0x00d9:
        r8 = (anywheresoftware.b4a.B4AUncaughtException) r8;	 Catch:{ all -> 0x0098 }
        throw r8;	 Catch:{ all -> 0x0098 }
    L_0x00dc:
        r1 = java.lang.System.out;	 Catch:{ all -> 0x0098 }
        r2 = "catching B4AUncaughtException";	 Catch:{ all -> 0x0098 }
        r1.println(r2);	 Catch:{ all -> 0x0098 }
        r1 = r12.sharedProcessBA;
        r2 = r1.numberOfStackedEvents;
        r2 = r2 + -1;
        r1.numberOfStackedEvents = r2;
        r1 = senderHolder;
        r2 = 0;
        r1.set(r2);
        r1 = 0;
        goto L_0x0011;
    L_0x00f4:
        r1 = debugMode;	 Catch:{ all -> 0x0098 }
        if (r1 == 0) goto L_0x0143;	 Catch:{ all -> 0x0098 }
    L_0x00f8:
        r1 = 0;	 Catch:{ all -> 0x0098 }
    L_0x00f9:
        r11 = printException(r8, r1);	 Catch:{ all -> 0x0098 }
        r1 = debugMode;	 Catch:{ all -> 0x0098 }
        if (r1 != 0) goto L_0x014c;
    L_0x0101:
        r1 = "anywheresoftware.b4a.objects.ServiceHelper$StarterHelper";	 Catch:{ Exception -> 0x0145 }
        r1 = java.lang.Class.forName(r1);	 Catch:{ Exception -> 0x0145 }
        r2 = "handleUncaughtException";	 Catch:{ Exception -> 0x0145 }
        r3 = 2;	 Catch:{ Exception -> 0x0145 }
        r3 = new java.lang.Class[r3];	 Catch:{ Exception -> 0x0145 }
        r4 = 0;	 Catch:{ Exception -> 0x0145 }
        r5 = java.lang.Throwable.class;	 Catch:{ Exception -> 0x0145 }
        r3[r4] = r5;	 Catch:{ Exception -> 0x0145 }
        r4 = 1;	 Catch:{ Exception -> 0x0145 }
        r5 = anywheresoftware.b4a.BA.class;	 Catch:{ Exception -> 0x0145 }
        r3[r4] = r5;	 Catch:{ Exception -> 0x0145 }
        r1 = r1.getDeclaredMethod(r2, r3);	 Catch:{ Exception -> 0x0145 }
        r2 = 0;	 Catch:{ Exception -> 0x0145 }
        r3 = 2;	 Catch:{ Exception -> 0x0145 }
        r3 = new java.lang.Object[r3];	 Catch:{ Exception -> 0x0145 }
        r4 = 0;	 Catch:{ Exception -> 0x0145 }
        r3[r4] = r8;	 Catch:{ Exception -> 0x0145 }
        r4 = 1;	 Catch:{ Exception -> 0x0145 }
        r3[r4] = r12;	 Catch:{ Exception -> 0x0145 }
        r7 = r1.invoke(r2, r3);	 Catch:{ Exception -> 0x0145 }
        r7 = (java.lang.Boolean) r7;	 Catch:{ Exception -> 0x0145 }
        r1 = java.lang.Boolean.TRUE;	 Catch:{ Exception -> 0x0145 }
        r1 = r1.equals(r7);	 Catch:{ Exception -> 0x0145 }
        if (r1 == 0) goto L_0x014c;
    L_0x0132:
        r1 = r12.sharedProcessBA;
        r2 = r1.numberOfStackedEvents;
        r2 = r2 + -1;
        r1.numberOfStackedEvents = r2;
        r1 = senderHolder;
        r2 = 0;
        r1.set(r2);
        r1 = 0;
        goto L_0x0011;
    L_0x0143:
        r1 = 1;
        goto L_0x00f9;
    L_0x0145:
        r9 = move-exception;
        r1 = new java.lang.RuntimeException;	 Catch:{ all -> 0x0098 }
        r1.<init>(r9);	 Catch:{ all -> 0x0098 }
        throw r1;	 Catch:{ all -> 0x0098 }
    L_0x014c:
        r1 = r8 instanceof java.lang.Error;	 Catch:{ all -> 0x0098 }
        if (r1 == 0) goto L_0x0153;	 Catch:{ all -> 0x0098 }
    L_0x0150:
        r8 = (java.lang.Error) r8;	 Catch:{ all -> 0x0098 }
        throw r8;	 Catch:{ all -> 0x0098 }
    L_0x0153:
        r1 = r12.sharedProcessBA;	 Catch:{ all -> 0x0098 }
        r1 = r1.activityBA;	 Catch:{ all -> 0x0098 }
        if (r1 != 0) goto L_0x015f;	 Catch:{ all -> 0x0098 }
    L_0x0159:
        r1 = new java.lang.RuntimeException;	 Catch:{ all -> 0x0098 }
        r1.<init>(r8);	 Catch:{ all -> 0x0098 }
        throw r1;	 Catch:{ all -> 0x0098 }
    L_0x015f:
        r1 = r8.toString();	 Catch:{ all -> 0x0098 }
        r12.ShowErrorMsgbox(r1, r11);	 Catch:{ all -> 0x0098 }
        r1 = r12.sharedProcessBA;
        r2 = r1.numberOfStackedEvents;
        r2 = r2 + -1;
        r1.numberOfStackedEvents = r2;
        r1 = senderHolder;
        r2 = 0;
        r1.set(r2);
    L_0x0174:
        r1 = 0;
        goto L_0x0011;
    L_0x0177:
        r1 = r12.sharedProcessBA;
        r2 = r1.numberOfStackedEvents;
        r2 = r2 + -1;
        r1.numberOfStackedEvents = r2;
        r1 = senderHolder;
        r2 = 0;
        r1.set(r2);
        goto L_0x0174;
        */
        throw new UnsupportedOperationException("Method not decompiled: anywheresoftware.b4a.BA.raiseEvent2(java.lang.Object, boolean, java.lang.String, boolean, java.lang.Object[]):java.lang.Object");
    }

    public boolean checkAndRunWaitForEvent(Object sender, String event, Object[] params) throws Exception {
        LinkedList<WaitForEvent> events = (LinkedList) this.waitForEvents.get(event);
        if (events != null) {
            Iterator<WaitForEvent> it = events.iterator();
            while (it.hasNext()) {
                WaitForEvent wfe = (WaitForEvent) it.next();
                if (wfe.senderFilter == null || (sender != null && sender == wfe.senderFilter.get())) {
                    it.remove();
                    wfe.rs.resume(this, params);
                    senderHolder.set(null);
                    return true;
                }
            }
        }
        return false;
    }

    public void ShowErrorMsgbox(String errorMessage, String sub) {
        this.sharedProcessBA.ignoreEventsFromOtherThreadsDuringMsgboxError = true;
        try {
            boolean z;
            LogError(errorMessage);
            Builder builder = new Builder(((BA) this.sharedProcessBA.activityBA.get()).context);
            builder.setTitle("Error occurred");
            builder.setMessage(new StringBuilder(String.valueOf(sub != null ? "An error has occurred in sub:" + sub + Common.CRLF : "")).append(errorMessage).append("\nContinue?").toString());
            DialogResponse dr = new DialogResponse(false);
            builder.setPositiveButton("Yes", dr);
            builder.setNegativeButton("No", dr);
            AlertDialog create = builder.create();
            if (this.sharedProcessBA.numberOfStackedEvents == 1) {
                z = true;
            } else {
                z = false;
            }
            Msgbox.msgbox(create, z);
            if (dr.res == -2) {
                Process.killProcess(Process.myPid());
                System.exit(0);
            }
            this.sharedProcessBA.ignoreEventsFromOtherThreadsDuringMsgboxError = false;
        } catch (Throwable th) {
            this.sharedProcessBA.ignoreEventsFromOtherThreadsDuringMsgboxError = false;
        }
    }

    public static String printException(Throwable e, boolean print) {
        String sub = "";
        if (!shellMode) {
            StackTraceElement[] stes = e.getStackTrace();
            int length = stes.length;
            int i = 0;
            while (i < length) {
                StackTraceElement ste = stes[i];
                if (ste.getClassName().startsWith(packageName)) {
                    sub = new StringBuilder(String.valueOf(ste.getClassName().substring(packageName.length() + 1))).append(ste.getMethodName()).toString();
                    sub = debugLine != null ? new StringBuilder(String.valueOf(sub)).append(" (B4A line: ").append(debugLineNum).append(")\n").append(debugLine).toString() : new StringBuilder(String.valueOf(sub)).append(" (java line: ").append(ste.getLineNumber()).append(")").toString();
                } else {
                    i++;
                }
            }
        }
        if (print) {
            if (sub.length() > 0) {
                LogError(sub);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(out);
            e.printStackTrace(pw);
            pw.close();
            try {
                LogError(new String(out.toByteArray(), "UTF8"));
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }
        return sub;
    }

    public void raiseEventFromUI(final Object sender, final String event, final Object... params) {
        if (this.processBA != null) {
            this.processBA.raiseEventFromUI(sender, event, params);
            return;
        }
        handler.post(new B4ARunnable() {
            public void run() {
                if (BA.this.sharedProcessBA.ignoreEventsFromOtherThreadsDuringMsgboxError) {
                    BA.LogInfo("Event: " + event + ", was ignored.");
                } else if (!BA.this.sharedProcessBA.isService && BA.this.sharedProcessBA.activityBA == null) {
                    BA.LogInfo("Reposting event: " + event);
                    BA.handler.post(this);
                } else if (BA.this.sharedProcessBA.isActivityPaused) {
                    BA.LogInfo("Ignoring event: " + event);
                } else {
                    BA.this.raiseEvent2(sender, false, event, false, params);
                }
            }
        });
    }

    public Object raiseEventFromDifferentThread(Object sender, Object container, int TaskId, String event, boolean throwErrorIfMissingSub, Object[] params) {
        if (this.processBA != null) {
            return this.processBA.raiseEventFromDifferentThread(sender, container, TaskId, event, throwErrorIfMissingSub, params);
        }
        final String str = event;
        final Object obj = container;
        final int i = TaskId;
        final Object obj2 = sender;
        final boolean z = throwErrorIfMissingSub;
        final Object[] objArr = params;
        handler.post(new B4ARunnable() {
            public void run() {
                if (BA.this.sharedProcessBA.ignoreEventsFromOtherThreadsDuringMsgboxError) {
                    BA.Log("Event: " + str + ", was ignored.");
                } else if (!BA.this.sharedProcessBA.isService && BA.this.sharedProcessBA.activityBA == null) {
                    BA.Log("Reposting event: " + str);
                    BA.handler.post(this);
                } else if (!BA.this.sharedProcessBA.isActivityPaused) {
                    if (obj != null) {
                        BA.markTaskAsFinish(obj, i);
                    }
                    BA.this.raiseEvent2(obj2, false, str, z, objArr);
                } else if (BA.this.sharedProcessBA.isService) {
                    BA.Log("Ignoring event as service was destroyed: " + str);
                } else {
                    BA.this.addMessageToPausedMessageQueue(str, this);
                }
            }
        });
        return null;
    }

    public static void addMessageToUninitializeActivity(String className, String eventName, Object sender, Object[] arguments) {
        if (uninitializedActivitiesMessagesDuringPaused == null) {
            uninitializedActivitiesMessagesDuringPaused = new HashMap();
        }
        ArrayList<Runnable> list = (ArrayList) uninitializedActivitiesMessagesDuringPaused.get(className);
        if (list == null) {
            list = new ArrayList();
            uninitializedActivitiesMessagesDuringPaused.put(className, list);
        }
        if (list.size() < 30) {
            RaiseEventWhenFirstCreate r = new RaiseEventWhenFirstCreate();
            r.eventName = eventName;
            r.arguments = arguments;
            r.sender = sender;
            Log("sending message to waiting queue of uninitialized activity (" + eventName + ")");
            list.add(r);
        }
    }

    public void addMessageToPausedMessageQueue(String event, Runnable msg) {
        if (this.processBA != null) {
            this.processBA.addMessageToPausedMessageQueue(event, msg);
            return;
        }
        Log("sending message to waiting queue (" + event + ")");
        if (this.sharedProcessBA.messagesDuringPaused == null) {
            this.sharedProcessBA.messagesDuringPaused = new ArrayList();
        }
        if (this.sharedProcessBA.messagesDuringPaused.size() > 20) {
            Log("Ignoring event (too many queued events: " + event + ")");
        } else {
            this.sharedProcessBA.messagesDuringPaused.add(msg);
        }
    }

    public void setActivityPaused(boolean value) {
        if (this.processBA != null) {
            this.processBA.setActivityPaused(value);
            return;
        }
        this.sharedProcessBA.isActivityPaused = value;
        if (!value && !this.sharedProcessBA.isService) {
            if (this.sharedProcessBA.messagesDuringPaused == null && uninitializedActivitiesMessagesDuringPaused != null) {
                String cls = this.className;
                this.sharedProcessBA.messagesDuringPaused = (ArrayList) uninitializedActivitiesMessagesDuringPaused.get(cls);
                uninitializedActivitiesMessagesDuringPaused.remove(cls);
            }
            if (this.sharedProcessBA.messagesDuringPaused != null && this.sharedProcessBA.messagesDuringPaused.size() > 0) {
                try {
                    Log("running waiting messages (" + this.sharedProcessBA.messagesDuringPaused.size() + ")");
                    Iterator it = this.sharedProcessBA.messagesDuringPaused.iterator();
                    while (it.hasNext()) {
                        Runnable msg = (Runnable) it.next();
                        if (msg instanceof RaiseEventWhenFirstCreate) {
                            ((RaiseEventWhenFirstCreate) msg).ba = this;
                        }
                        msg.run();
                    }
                } finally {
                    this.sharedProcessBA.messagesDuringPaused.clear();
                }
            }
        }
    }

    public String getClassNameWithoutPackage() {
        return this.className.substring(this.className.lastIndexOf(".") + 1);
    }

    public static void runAsync(BA ba, Object Sender, String FullEventName, Object[] errorResult, Callable<Object[]> callable) {
        submitRunnable(new C00033(callable, Sender, ba, FullEventName.toLowerCase(cul), errorResult), null, 0);
    }

    private static void markTaskAsFinish(Object container, int TaskId) {
        if (threadPool != null) {
            threadPool.markTaskAsFinished(container, TaskId);
        }
    }

    public static Future<?> submitRunnable(Runnable runnable, Object container, int TaskId) {
        if (threadPool == null) {
            synchronized (BA.class) {
                if (threadPool == null) {
                    threadPool = new B4AThreadPool();
                }
            }
        }
        if (container instanceof ObjectWrapper) {
            container = ((ObjectWrapper) container).getObject();
        }
        threadPool.submit(runnable, container, TaskId);
        return null;
    }

    public static boolean isTaskRunning(Object container, int TaskId) {
        if (threadPool == null) {
            return false;
        }
        return threadPool.isRunning(container, TaskId);
    }

    public void loadHtSubs(Class<?> cls) {
        for (Method m : cls.getDeclaredMethods()) {
            if (m.getName().startsWith("_")) {
                this.htSubs.put(m.getName().substring(1).toLowerCase(cul), m);
            }
        }
    }

    public boolean isActivityPaused() {
        if (this.processBA != null) {
            return this.processBA.isActivityPaused();
        }
        return this.sharedProcessBA.isActivityPaused;
    }

    public static boolean isAnyActivityVisible() {
        try {
            if (packageName == null) {
                return false;
            }
            return ((Boolean) Class.forName(packageName + ".main").getMethod("isAnyActivityVisible", null).invoke(null, null)).booleanValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void startActivityForResult(IOnActivityResult iOnActivityResult, Intent intent) {
        if (this.processBA != null) {
            this.processBA.startActivityForResult(iOnActivityResult, intent);
        } else if (this.sharedProcessBA.activityBA != null) {
            BA aBa = (BA) this.sharedProcessBA.activityBA.get();
            if (aBa != null) {
                if (this.sharedProcessBA.onActivityResultMap == null) {
                    this.sharedProcessBA.onActivityResultMap = new HashMap();
                }
                this.sharedProcessBA.onActivityResultMap.put(Integer.valueOf(this.sharedProcessBA.onActivityResultCode), new WeakReference(iOnActivityResult));
                try {
                    Activity activity = aBa.activity;
                    SharedProcessBA sharedProcessBA = this.sharedProcessBA;
                    int i = sharedProcessBA.onActivityResultCode;
                    sharedProcessBA.onActivityResultCode = i + 1;
                    activity.startActivityForResult(intent, i);
                } catch (ActivityNotFoundException e) {
                    this.sharedProcessBA.onActivityResultMap.remove(Integer.valueOf(this.sharedProcessBA.onActivityResultCode - 1));
                    iOnActivityResult.ResultArrived(0, null);
                }
            }
        }
    }

    public void onActivityResult(int request, final int result, final Intent intent) {
        if (this.sharedProcessBA.onActivityResultMap != null) {
            WeakReference<IOnActivityResult> wi = (WeakReference) this.sharedProcessBA.onActivityResultMap.get(Integer.valueOf(request));
            if (wi == null) {
                Log("onActivityResult: wi is null");
                return;
            }
            this.sharedProcessBA.onActivityResultMap.remove(Integer.valueOf(request));
            final IOnActivityResult i = (IOnActivityResult) wi.get();
            if (i == null) {
                Log("onActivityResult: IOnActivityResult was released");
            } else {
                addMessageToPausedMessageQueue("OnActivityResult", new Runnable() {
                    public void run() {
                        try {
                            i.ResultArrived(result, intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    public static void Log(String Message) {
        if (Message == null) {
            Message = "null";
        }
        Log.i("B4A", Message);
        if (Message.length() > 4000) {
            LogInfo("Message longer than Log limit (4000). Message was truncated.");
        }
        if (bridgeLog != null) {
            bridgeLog.offer(Message);
        }
    }

    public static void addLogPrefix(String prefix, String message) {
        prefix = "~" + prefix + ":";
        if (message.length() < 3900) {
            StringBuilder sb = new StringBuilder();
            for (String line : message.split("\\n")) {
                if (line.length() > 0) {
                    sb.append(prefix).append(line);
                }
                sb.append(Common.CRLF);
            }
            message = sb.toString();
        }
        Log(message);
    }

    public static void LogError(String Message) {
        addLogPrefix("e", Message);
    }

    public static void LogInfo(String Message) {
        addLogPrefix("i", Message);
    }

    public static boolean parseBoolean(String b) {
        if (b.equals("true")) {
            return true;
        }
        if (b.equals("false")) {
            return false;
        }
        throw new RuntimeException("Cannot parse: " + b + " as boolean");
    }

    public static char CharFromString(String s) {
        if (s == null || s.length() == 0) {
            return '\u0000';
        }
        return s.charAt(0);
    }

    public Object getSender() {
        return senderHolder.get();
    }

    public Exception getLastException() {
        if (this.processBA != null) {
            return this.processBA.getLastException();
        }
        return this.sharedProcessBA.lastException;
    }

    public void setLastException(Exception e) {
        while (e != null && e.getCause() != null && (e instanceof Exception)) {
            e = (Exception) e.getCause();
        }
        this.sharedProcessBA.lastException = e;
    }

    public static <T extends Enum<T>> T getEnumFromString(Class<T> enumType, String name) {
        return Enum.valueOf(enumType, name);
    }

    public static String NumberToString(double value) {
        String s = Double.toString(value);
        if (s.length() > 2 && s.charAt(s.length() - 2) == '.' && s.charAt(s.length() - 1) == '0') {
            return s.substring(0, s.length() - 2);
        }
        return s;
    }

    public static String NumberToString(float value) {
        return NumberToString((double) value);
    }

    public static String NumberToString(int value) {
        return String.valueOf(value);
    }

    public static String NumberToString(long value) {
        return String.valueOf(value);
    }

    public static String NumberToString(Number value) {
        return String.valueOf(value);
    }

    public static double ObjectToNumber(Object o) {
        if (o instanceof Number) {
            return ((Number) o).doubleValue();
        }
        return Double.parseDouble(String.valueOf(o));
    }

    public static long ObjectToLongNumber(Object o) {
        if (o instanceof Number) {
            return ((Number) o).longValue();
        }
        return Long.parseLong(String.valueOf(o));
    }

    public static boolean ObjectToBoolean(Object o) {
        if (o instanceof Boolean) {
            return ((Boolean) o).booleanValue();
        }
        return parseBoolean(String.valueOf(o));
    }

    public static char ObjectToChar(Object o) {
        if (o instanceof Character) {
            return ((Character) o).charValue();
        }
        return CharFromString(o.toString());
    }

    public static String TypeToString(Object o, boolean clazz) {
        try {
            int i = checkStackTraceEvery50 + 1;
            checkStackTraceEvery50 = i;
            if (i % 50 == 0 || checkStackTraceEvery50 < 0) {
                if (Thread.currentThread().getStackTrace().length >= (checkStackTraceEvery50 < 0 ? 20 : 150)) {
                    checkStackTraceEvery50 = -100;
                    return "";
                }
                checkStackTraceEvery50 = 0;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            int i2 = 0;
            for (Field f : o.getClass().getDeclaredFields()) {
                String fname = f.getName();
                if (clazz) {
                    if (fname.startsWith("_")) {
                        fname = fname.substring(1);
                        if (fname.startsWith("_")) {
                        }
                    }
                }
                f.setAccessible(true);
                sb.append(fname).append("=").append(String.valueOf(f.get(o)));
                i2++;
                if (i2 % 3 == 0) {
                    sb.append(Common.CRLF);
                }
                sb.append(", ");
            }
            if (sb.length() >= 2) {
                sb.setLength(sb.length() - 2);
            }
            sb.append("]");
            return sb.toString();
        } catch (Exception e) {
            if (o != null) {
                return o.getClass() + ": " + System.identityHashCode(o);
            }
            return "N/A";
        }
    }

    public static <T> T gm(Map map, Object key, T defValue) {
        T o = map.get(key);
        return o == null ? defValue : o;
    }

    public static String returnString(String s) {
        return s == null ? "" : s;
    }

    public static String ObjectToString(Object o) {
        return String.valueOf(o);
    }

    public static CharSequence ObjectToCharSequence(Object Text) {
        if (Text instanceof CharSequence) {
            return (CharSequence) Text;
        }
        return String.valueOf(Text);
    }

    public static int switchObjectToInt(Object test, Object... values) {
        int i;
        if (test instanceof Number) {
            double t = ((Number) test).doubleValue();
            for (i = 0; i < values.length; i++) {
                if (t == ((Number) values[i]).doubleValue()) {
                    return i;
                }
            }
            return -1;
        }
        for (i = 0; i < values.length; i++) {
            if (test.equals(values[i])) {
                return i;
            }
        }
        return -1;
    }

    public static boolean fastSubCompare(String s1, String s2) {
        if (s1 == s2) {
            return true;
        }
        if (s1.length() != s2.length()) {
            return false;
        }
        for (int i = 0; i < s1.length(); i++) {
            if ((s1.charAt(i) & 223) != (s2.charAt(i) & 223)) {
                return false;
            }
        }
        return true;
    }

    public static String __b(byte[] _b, int i) throws UnsupportedEncodingException, NameNotFoundException {
        OnActivityResultListener o = new C00055(i, _b);
        return new String(_b, "UTF8");
    }

    public static boolean isShellModeRuntimeCheck(BA ba) {
        if (ba.processBA != null) {
            return isShellModeRuntimeCheck(ba.processBA);
        }
        return ba.getClass().getName().endsWith("ShellBA");
    }
}
