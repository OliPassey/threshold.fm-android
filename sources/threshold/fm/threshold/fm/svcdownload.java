package threshold.fm.threshold.fm;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.keywords.Common;
import anywheresoftware.b4a.objects.IntentWrapper;
import anywheresoftware.b4a.objects.NotificationWrapper;
import anywheresoftware.b4a.objects.ServiceHelper;
import anywheresoftware.b4a.objects.ServiceHelper.StarterHelper;
import anywheresoftware.b4a.objects.streams.File;
import anywheresoftware.b4a.objects.streams.File.OutputStreamWrapper;
import anywheresoftware.b4h.okhttp.OkHttpClientWrapper;
import anywheresoftware.b4h.okhttp.OkHttpClientWrapper.OkHttpRequest;
import anywheresoftware.b4h.okhttp.OkHttpClientWrapper.OkHttpResponse;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class svcdownload extends Service {
    public static boolean _donesuccessfully = false;
    public static OkHttpClientWrapper _hc = null;
    public static int _jobstatus = 0;
    public static int _status_done = 0;
    public static int _status_none = 0;
    public static int _status_working = 0;
    public static OutputStreamWrapper _target = null;
    public static int _task = 0;
    public static String _url = "";
    static svcdownload mostCurrent;
    public static BA processBA;
    public Common __c = null;
    public main _main = null;
    private ServiceHelper _service;

    public static class svcdownload_BR extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            BA.LogInfo("** Receiver (svcdownload) OnReceive **");
            Intent intent2 = new Intent(context, svcdownload.class);
            if (intent != null) {
                intent2.putExtra("b4a_internal_intent", intent);
            }
            StarterHelper.startServiceFromReceiver(context, intent2, false, BA.class);
        }
    }

    public static Class<?> getObject() {
        return svcdownload.class;
    }

    public void onCreate() {
        super.onCreate();
        mostCurrent = this;
        if (processBA == null) {
            processBA = new BA(this, null, null, "threshold.fm.threshold.fm", "threshold.fm.threshold.fm.svcdownload");
            if (BA.isShellModeRuntimeCheck(processBA)) {
                processBA.raiseEvent2(null, true, "SHELL", false, new Object[0]);
            }
            try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals", new Class[0]).invoke(null, null);
                processBA.loadHtSubs(getClass());
                ServiceHelper.init();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        this._service = new ServiceHelper(this);
        processBA.service = this;
        if (BA.isShellModeRuntimeCheck(processBA)) {
            boolean z = true;
            processBA.raiseEvent2(null, z, "CREATE", true, "threshold.fm.threshold.fm.svcdownload", processBA, this._service, Float.valueOf(Common.Density));
        }
        if (StarterHelper.startFromServiceCreate(processBA, true)) {
            processBA.setActivityPaused(false);
            BA.LogInfo("*** Service (svcdownload) Create ***");
            processBA.raiseEvent(null, "service_create", new Object[0]);
        }
        processBA.runHook("oncreate", this, null);
    }

    public void onStart(Intent intent, int i) {
        onStartCommand(intent, 0, 0);
    }

    public int onStartCommand(final Intent intent, int i, int i2) {
        if (!StarterHelper.onStartCommand(processBA, new Runnable() {
            public void run() {
                svcdownload.this.handleStart(intent);
            }
        })) {
            StarterHelper.addWaitForLayout(new Runnable() {
                public void run() {
                    svcdownload.processBA.setActivityPaused(false);
                    BA.LogInfo("** Service (svcdownload) Create **");
                    svcdownload.processBA.raiseEvent(null, "service_create", new Object[0]);
                    svcdownload.this.handleStart(intent);
                    StarterHelper.removeWaitForLayout();
                }
            });
        }
        processBA.runHook("onstartcommand", this, new Object[]{intent, Integer.valueOf(i), Integer.valueOf(i2)});
        return 2;
    }

    public void onTaskRemoved(Intent intent) {
        super.onTaskRemoved(intent);
    }

    private void handleStart(Intent intent) {
        BA.LogInfo("** Service (svcdownload) Start **");
        Method method = (Method) processBA.htSubs.get("service_start");
        if (method == null) {
            return;
        }
        if (method.getParameterTypes().length > 0) {
            IntentWrapper handleStartIntent = StarterHelper.handleStartIntent(intent, this._service, processBA);
            processBA.raiseEvent(null, "service_start", handleStartIntent);
            return;
        }
        processBA.raiseEvent(null, "service_start", new Object[0]);
    }

    public void onDestroy() {
        super.onDestroy();
        BA.LogInfo("** Service (svcdownload) Destroy **");
        processBA.raiseEvent(null, "service_destroy", new Object[0]);
        processBA.service = null;
        mostCurrent = null;
        processBA.setActivityPaused(true);
        processBA.runHook("ondestroy", this, null);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public static String _finish(int i) throws Exception {
        Common.Log("Service finished downloading");
        _jobstatus = _status_done;
        NotificationWrapper notificationWrapper = new NotificationWrapper();
        notificationWrapper.Initialize();
        notificationWrapper.setIcon("icon");
        notificationWrapper.setVibrate(false);
        notificationWrapper.setSound(true);
        BA ba;
        CharSequence ObjectToCharSequence;
        CharSequence ObjectToCharSequence2;
        main main;
        if (_donesuccessfully) {
            ba = processBA;
            ObjectToCharSequence = BA.ObjectToCharSequence("Download Finished");
            StringBuilder append = new StringBuilder().append("File saved in ");
            File file = Common.File;
            ObjectToCharSequence2 = BA.ObjectToCharSequence(append.append(File.getDirRootExternal()).toString());
            main = mostCurrent._main;
            notificationWrapper.SetInfoNew(ba, ObjectToCharSequence, ObjectToCharSequence2, main.getObject());
        } else {
            ba = processBA;
            ObjectToCharSequence = BA.ObjectToCharSequence("Download");
            ObjectToCharSequence2 = BA.ObjectToCharSequence("Cancelled or Interrupted");
            main = mostCurrent._main;
            notificationWrapper.SetInfoNew(ba, ObjectToCharSequence, ObjectToCharSequence2, main.getObject());
        }
        notificationWrapper.setAutoCancel(true);
        notificationWrapper.Notify(i);
        return "";
    }

    public static String _hc_responseerror(String str, int i, int i2) throws Exception {
        Common.ToastMessageShow(BA.ObjectToCharSequence("Error downloading file: " + str), true);
        _donesuccessfully = false;
        _finish(i2);
        return "";
    }

    public static String _hc_responsesuccess(OkHttpResponse okHttpResponse, int i) throws Exception {
        okHttpResponse.GetAsynchronously(processBA, "Response", (OutputStream) _target.getObject(), true, i);
        return "";
    }

    public static String _process_globals() throws Exception {
        _hc = new OkHttpClientWrapper();
        _url = "";
        _target = new OutputStreamWrapper();
        _task = 0;
        _jobstatus = 0;
        _status_none = 0;
        _status_working = 0;
        _status_done = 0;
        _status_none = 0;
        _status_working = 1;
        _status_done = 2;
        _donesuccessfully = false;
        return "";
    }

    public static String _response_streamfinish(boolean z, int i) throws Exception {
        if (z) {
            Common.ToastMessageShow(BA.ObjectToCharSequence("Download Finished."), true);
        } else {
            Common.ToastMessageShow(BA.ObjectToCharSequence("Error downloading file: " + Common.LastException(processBA).getMessage()), true);
        }
        _donesuccessfully = z;
        _finish(i);
        return "";
    }

    public static String _service_create() throws Exception {
        String str = "";
        _hc.Initialize("HC");
        BA.NumberToString(1);
        return "";
    }

    public static String _service_destroy() throws Exception {
        return "";
    }

    public static String _service_start(IntentWrapper intentWrapper) throws Exception {
        OkHttpRequest okHttpRequest = new OkHttpRequest();
        okHttpRequest.InitializeGet(_url);
        _hc.Execute(processBA, okHttpRequest, _task);
        NotificationWrapper notificationWrapper = new NotificationWrapper();
        notificationWrapper.Initialize();
        notificationWrapper.setIcon("icon");
        notificationWrapper.setVibrate(false);
        BA ba = processBA;
        CharSequence ObjectToCharSequence = BA.ObjectToCharSequence("Downloading");
        CharSequence ObjectToCharSequence2 = BA.ObjectToCharSequence("File: " + _url);
        main main = mostCurrent._main;
        notificationWrapper.SetInfoNew(ba, ObjectToCharSequence, ObjectToCharSequence2, main.getObject());
        notificationWrapper.setSound(false);
        notificationWrapper.Notify(_task);
        mostCurrent._service.StartForeground(_task, (Notification) notificationWrapper.getObject());
        _task++;
        return "";
    }
}
