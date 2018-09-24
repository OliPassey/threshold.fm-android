package anywheresoftware.b4a.keywords;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.widget.RemoteViews;
import android.widget.Toast;
import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.B4AClass;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.ActivityObject;
import anywheresoftware.b4a.BA.B4ARunnable;
import anywheresoftware.b4a.BA.DesignerName;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.Pixel;
import anywheresoftware.b4a.BA.RaisesSynchronousEvents;
import anywheresoftware.b4a.BA.ResumableSub;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.BA.SubDelegator;
import anywheresoftware.b4a.BA.Version;
import anywheresoftware.b4a.BA.WaitForEvent;
import anywheresoftware.b4a.Msgbox;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.keywords.constants.Colors;
import anywheresoftware.b4a.keywords.constants.DialogResponse;
import anywheresoftware.b4a.keywords.constants.Gravity;
import anywheresoftware.b4a.keywords.constants.KeyCodes;
import anywheresoftware.b4a.keywords.constants.TypefaceWrapper;
import anywheresoftware.b4a.objects.B4AException;
import anywheresoftware.b4a.objects.LabelWrapper;
import anywheresoftware.b4a.objects.PanelWrapper;
import anywheresoftware.b4a.objects.collections.List;
import anywheresoftware.b4a.objects.collections.Map;
import anywheresoftware.b4a.objects.collections.Map.MyMap;
import anywheresoftware.b4a.objects.drawable.BitmapDrawable;
import anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper;
import anywheresoftware.b4a.objects.streams.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Random;

@Version(8.3f)
@ActivityObject
public class Common {
    public static final B4AApplication Application = null;
    public static final Bit Bit = null;
    public static final String CRLF = "\n";
    public static final Colors Colors = null;
    public static final DateTime DateTime = null;
    public static final float Density = BA.density;
    public static final DialogResponse DialogResponse = null;
    public static final boolean False = false;
    public static final File File = null;
    public static final Gravity Gravity = null;
    public static KeyCodes KeyCodes = null;
    public static final Object Null = null;
    public static final String QUOTE = "\"";
    public static final Regex Regex = null;
    public static final String TAB = "\t";
    public static final boolean True = true;
    public static final TypefaceWrapper Typeface = null;
    public static final double cE = 2.718281828459045d;
    public static final double cPI = 3.141592653589793d;
    private static Random random;

    /* renamed from: anywheresoftware.b4a.keywords.Common$11 */
    class AnonymousClass11 implements Runnable {
        int retries = 5;
        private final /* synthetic */ Object[] val$Arguments;
        private final /* synthetic */ Object val$Component;
        private final /* synthetic */ String val$Sub;
        private final /* synthetic */ BA val$mine;

        AnonymousClass11(BA ba, Object obj, String str, Object[] objArr) {
            this.val$mine = ba;
            this.val$Component = obj;
            this.val$Sub = str;
            this.val$Arguments = objArr;
        }

        public void run() {
            try {
                final BA ba = Common.getComponentBA(this.val$mine, this.val$Component);
                final Object sender = this.val$mine.eventsTarget;
                if (ba == null || ba.isActivityPaused()) {
                    if (this.val$Component instanceof B4AClass) {
                        Common.Log("Object context is paused. Ignoring CallSubDelayed: " + this.val$Sub);
                        return;
                    }
                    ComponentName cn = Common.getComponentIntent(this.val$mine, this.val$Component).getComponent();
                    if (cn == null) {
                        Common.Log("ComponentName = null");
                        return;
                    }
                    Class<?> cls = Class.forName(cn.getClassName());
                    Field f = cls.getDeclaredField("mostCurrent");
                    f.setAccessible(true);
                    if (f.get(null) == null && this.retries == 5) {
                        if (Activity.class.isAssignableFrom(cls)) {
                            if (BA.isAnyActivityVisible()) {
                                Common.StartActivity(this.val$mine, this.val$Component);
                            } else {
                                this.retries = 0;
                            }
                        } else if (Service.class.isAssignableFrom(cls)) {
                            Common.StartService(this.val$mine, this.val$Component);
                        }
                    }
                    int i = this.retries - 1;
                    this.retries = i;
                    if (i > 0) {
                        BA.handler.postDelayed(this, 100);
                    } else if (ba != null) {
                        final String str = this.val$Sub;
                        final Object[] objArr = this.val$Arguments;
                        ba.addMessageToPausedMessageQueue("CallSubDelayed - " + this.val$Sub, new Runnable() {
                            public void run() {
                                ba.raiseEvent2(sender, true, str.toLowerCase(BA.cul), true, objArr);
                            }
                        });
                    } else {
                        BA.addMessageToUninitializeActivity(cn.getClassName(), this.val$Sub.toLowerCase(BA.cul), sender, this.val$Arguments);
                    }
                } else if (BA.shellMode) {
                    ba.raiseEventFromDifferentThread(sender, null, 0, this.val$Sub.toLowerCase(BA.cul), false, this.val$Arguments);
                } else {
                    ba.raiseEvent2(sender, true, this.val$Sub.toLowerCase(BA.cul), false, this.val$Arguments);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* renamed from: anywheresoftware.b4a.keywords.Common$12 */
    class AnonymousClass12 implements B4ARunnable {
        private final /* synthetic */ Runnable val$runnable;

        AnonymousClass12(Runnable runnable) {
            this.val$runnable = runnable;
        }

        public void run() {
            this.val$runnable.run();
        }
    }

    /* renamed from: anywheresoftware.b4a.keywords.Common$13 */
    class AnonymousClass13 implements B4ARunnable {
        private final /* synthetic */ BA val$ba;
        private final /* synthetic */ ResumableSub val$rs;

        AnonymousClass13(BA ba, ResumableSub resumableSub) {
            this.val$ba = ba;
            this.val$rs = resumableSub;
        }

        public void run() {
            boolean isActivity = this.val$ba.processBA != null;
            if (isActivity && (this.val$ba.processBA.sharedProcessBA.activityBA == null || this.val$ba != this.val$ba.processBA.sharedProcessBA.activityBA.get())) {
                BA.LogInfo("Sleep not resumed (context destroyed): " + this.val$rs.getClass().getName());
            } else if (!this.val$ba.isActivityPaused()) {
                try {
                    this.val$rs.resume(this.val$ba, null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (isActivity) {
                this.val$ba.processBA.addMessageToPausedMessageQueue("sleep", this);
            } else {
                BA.LogInfo("Sleep not resumed (context is paused): " + this.val$rs.getClass().getName());
            }
        }
    }

    /* renamed from: anywheresoftware.b4a.keywords.Common$14 */
    class AnonymousClass14 implements Runnable {
        private final /* synthetic */ Object val$returnValue;
        private final /* synthetic */ ResumableSub val$rs;

        AnonymousClass14(ResumableSub resumableSub, Object obj) {
            this.val$rs = resumableSub;
            this.val$returnValue = obj;
        }

        public void run() {
            this.val$rs.completed = true;
            if (this.val$rs.waitForBA != null) {
                this.val$rs.waitForBA.raiseEvent(this.val$rs, "complete", this.val$returnValue);
            }
        }
    }

    /* renamed from: anywheresoftware.b4a.keywords.Common$1 */
    class C00151 implements OnClickListener {
        private final /* synthetic */ BA val$mine;

        C00151(BA ba) {
            this.val$mine = ba;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$mine.raiseEvent(dialog, "msgbox_result", Integer.valueOf(which));
        }
    }

    /* renamed from: anywheresoftware.b4a.keywords.Common$2 */
    class C00162 implements OnCancelListener {
        private final /* synthetic */ BA val$mine;

        C00162(BA ba) {
            this.val$mine = ba;
        }

        public void onCancel(DialogInterface dialog) {
            this.val$mine.raiseEvent(dialog, "msgbox_result", Integer.valueOf(-3));
        }
    }

    /* renamed from: anywheresoftware.b4a.keywords.Common$3 */
    class C00173 implements OnClickListener {
        private final /* synthetic */ BA val$mine;

        C00173(BA ba) {
            this.val$mine = ba;
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            this.val$mine.raiseEvent(dialog, "inputlist_result", Integer.valueOf(which));
        }
    }

    /* renamed from: anywheresoftware.b4a.keywords.Common$4 */
    class C00184 implements OnCancelListener {
        private final /* synthetic */ BA val$mine;

        C00184(BA ba) {
            this.val$mine = ba;
        }

        public void onCancel(DialogInterface dialog) {
            this.val$mine.raiseEvent(dialog, "inputlist_result", Integer.valueOf(-3));
        }
    }

    /* renamed from: anywheresoftware.b4a.keywords.Common$5 */
    class C00195 implements OnClickListener {
        private final /* synthetic */ BA val$mine;

        C00195(BA ba) {
            this.val$mine = ba;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$mine.raiseEvent(dialog, "inputmap_result", new Object[0]);
        }
    }

    /* renamed from: anywheresoftware.b4a.keywords.Common$6 */
    class C00206 implements OnCancelListener {
        private final /* synthetic */ BA val$mine;

        C00206(BA ba) {
            this.val$mine = ba;
        }

        public void onCancel(DialogInterface dialog) {
            this.val$mine.raiseEvent(dialog, "inputmap_result", new Object[0]);
        }
    }

    /* renamed from: anywheresoftware.b4a.keywords.Common$7 */
    class C00217 implements OnMultiChoiceClickListener {
        private final /* synthetic */ Map val$Items;
        private final /* synthetic */ CharSequence[] val$items;

        C00217(Map map, CharSequence[] charSequenceArr) {
            this.val$Items = map;
            this.val$items = charSequenceArr;
        }

        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            if (isChecked) {
                this.val$Items.Put(this.val$items[which], Boolean.valueOf(true));
            } else {
                this.val$Items.Put(this.val$items[which], Boolean.valueOf(false));
            }
        }
    }

    /* renamed from: anywheresoftware.b4a.keywords.Common$8 */
    class C00228 implements OnMultiChoiceClickListener {
        private final /* synthetic */ List val$result;

        C00228(List list) {
            this.val$result = list;
        }

        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            if (isChecked) {
                this.val$result.Add(Integer.valueOf(which));
                return;
            }
            this.val$result.RemoveAt(this.val$result.IndexOf(Integer.valueOf(which)));
        }
    }

    /* renamed from: anywheresoftware.b4a.keywords.Common$9 */
    class C00239 implements B4ARunnable {
        private final /* synthetic */ Object val$Service;
        private final /* synthetic */ BA val$mine;

        C00239(BA ba, Object obj) {
            this.val$mine = ba;
            this.val$Service = obj;
        }

        public void run() {
            try {
                this.val$mine.context.startService(Common.getComponentIntent(this.val$mine, this.val$Service));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Hide
    public interface DesignerCustomView {
        void DesignerCreateView(PanelWrapper panelWrapper, LabelWrapper labelWrapper, Map map);

        void _initialize(BA ba, Object obj, String str);
    }

    @ShortName("ResumableSub")
    public static class ResumableSubWrapper extends AbsObjectWrapper<ResumableSub> {
        public boolean getCompleted() {
            return ((ResumableSub) getObject()).completed;
        }
    }

    public static String NumberFormat(double Number, int MinimumIntegers, int MaximumFractions) {
        if (BA.numberFormat == null) {
            BA.numberFormat = NumberFormat.getInstance(Locale.US);
        }
        BA.numberFormat.setMaximumFractionDigits(MaximumFractions);
        BA.numberFormat.setMinimumIntegerDigits(MinimumIntegers);
        return BA.numberFormat.format(Number);
    }

    public static String NumberFormat2(double Number, int MinimumIntegers, int MaximumFractions, int MinimumFractions, boolean GroupingUsed) {
        if (BA.numberFormat2 == null) {
            BA.numberFormat2 = NumberFormat.getInstance(Locale.US);
        }
        BA.numberFormat2.setMaximumFractionDigits(MaximumFractions);
        BA.numberFormat2.setMinimumIntegerDigits(MinimumIntegers);
        BA.numberFormat2.setMinimumFractionDigits(MinimumFractions);
        BA.numberFormat2.setGroupingUsed(GroupingUsed);
        return BA.numberFormat2.format(Number);
    }

    public static void Log(String Message) {
        BA.Log(Message);
    }

    public static void LogColor(String Message, int Color) {
        BA.addLogPrefix("c" + Color, Message);
    }

    public static Object Sender(BA ba) {
        return ba.getSender();
    }

    public static boolean Not(boolean Value) {
        return !Value;
    }

    public static void RndSeed(long Seed) {
        if (random == null) {
            random = new Random(Seed);
        } else {
            random.setSeed(Seed);
        }
    }

    public static int Rnd(int Min, int Max) {
        if (random == null) {
            random = new Random();
        }
        return random.nextInt(Max - Min) + Min;
    }

    public static double Abs(double Number) {
        return Math.abs(Number);
    }

    @Hide
    public static int Abs(int Number) {
        return Math.abs(Number);
    }

    public static double Max(double Number1, double Number2) {
        return Math.max(Number1, Number2);
    }

    @Hide
    public static double Max(int Number1, int Number2) {
        return (double) Math.max(Number1, Number2);
    }

    public static double Min(double Number1, double Number2) {
        return Math.min(Number1, Number2);
    }

    @Hide
    public static double Min(int Number1, int Number2) {
        return (double) Math.min(Number1, Number2);
    }

    public static double Sin(double Radians) {
        return Math.sin(Radians);
    }

    public static double SinD(double Degrees) {
        return Math.sin((Degrees / 180.0d) * 3.141592653589793d);
    }

    public static double Cos(double Radians) {
        return Math.cos(Radians);
    }

    public static double CosD(double Degrees) {
        return Math.cos((Degrees / 180.0d) * 3.141592653589793d);
    }

    public static double Tan(double Radians) {
        return Math.tan(Radians);
    }

    public static double TanD(double Degrees) {
        return Math.tan((Degrees / 180.0d) * 3.141592653589793d);
    }

    public static double Power(double Base, double Exponent) {
        return Math.pow(Base, Exponent);
    }

    public static double Sqrt(double Value) {
        return Math.sqrt(Value);
    }

    public static double ASin(double Value) {
        return Math.asin(Value);
    }

    public static double ASinD(double Value) {
        return (Math.asin(Value) / 3.141592653589793d) * 180.0d;
    }

    public static double ACos(double Value) {
        return Math.acos(Value);
    }

    public static double ACosD(double Value) {
        return (Math.acos(Value) / 3.141592653589793d) * 180.0d;
    }

    public static double ATan(double Value) {
        return Math.atan(Value);
    }

    public static double ATanD(double Value) {
        return (Math.atan(Value) / 3.141592653589793d) * 180.0d;
    }

    public static double ATan2(double Y, double X) {
        return Math.atan2(Y, X);
    }

    public static double ATan2D(double Y, double X) {
        return (Math.atan2(Y, X) / 3.141592653589793d) * 180.0d;
    }

    public static double Logarithm(double Number, double Base) {
        return Math.log(Number) / Math.log(Base);
    }

    public static long Round(double Number) {
        return Math.round(Number);
    }

    public static double Round2(double Number, int DecimalPlaces) {
        double shift = Math.pow(10.0d, (double) DecimalPlaces);
        return ((double) Math.round(Number * shift)) / shift;
    }

    public static double Floor(double Number) {
        return Math.floor(Number);
    }

    public static double Ceil(double Number) {
        return Math.ceil(Number);
    }

    public static int Asc(char Char) {
        return Char;
    }

    public static char Chr(int UnicodeValue) {
        return (char) UnicodeValue;
    }

    @RaisesSynchronousEvents
    public static void DoEvents() {
        Msgbox.sendCloseMyLoopMessage();
        Msgbox.waitForMessage(false, true);
    }

    public static void ToastMessageShow(CharSequence Message, boolean LongDuration) {
        Toast.makeText(BA.applicationContext, Message, LongDuration ? 1 : 0).show();
    }

    @RaisesSynchronousEvents
    public static void Msgbox(CharSequence Message, CharSequence Title, BA ba) {
        Msgbox2(Message, Title, "OK", "", "", null, ba);
    }

    @RaisesSynchronousEvents
    public static int Msgbox2(CharSequence Message, CharSequence Title, String Positive, String Cancel, String Negative, Bitmap Icon, BA ba) {
        Msgbox.DialogResponse dr = new Msgbox.DialogResponse(false);
        Msgbox.msgbox(createMsgboxAlertDialog(Message, Title, Positive, Cancel, Negative, Icon, ba, dr), false);
        return dr.res;
    }

    public static void MsgboxAsync(CharSequence Message, CharSequence Title, BA mine) {
        Msgbox2Async(Message, Title, "OK", "", "", null, mine, true);
    }

    public static Object Msgbox2Async(CharSequence Message, CharSequence Title, String Positive, String Cancel, String Negative, BitmapWrapper Icon, BA mine, boolean Cancelable) {
        AlertDialog ad = createMsgboxAlertDialog(Message, Title, Positive, Cancel, Negative, Icon == null ? null : (Bitmap) Icon.getObjectOrNull(), (BA) mine.sharedProcessBA.activityBA.get(), new C00151(mine));
        ad.setOnCancelListener(new C00162(mine));
        return showAndTrackDialog(ad, Cancelable);
    }

    private static AlertDialog createMsgboxAlertDialog(CharSequence Message, CharSequence Title, String Positive, String Cancel, String Negative, Bitmap Icon, BA ba, OnClickListener listener) {
        Builder b = new Builder(ba.context);
        b.setTitle(Title).setMessage(Message);
        if (Positive.length() > 0) {
            b.setPositiveButton(Positive, listener);
        }
        if (Negative.length() > 0) {
            b.setNegativeButton(Negative, listener);
        }
        if (Cancel.length() > 0) {
            b.setNeutralButton(Cancel, listener);
        }
        if (Icon != null) {
            BitmapDrawable bd = new BitmapDrawable();
            bd.Initialize(Icon);
            b.setIcon((Drawable) bd.getObject());
        }
        return b.create();
    }

    @RaisesSynchronousEvents
    public static int InputList(List Items, CharSequence Title, int CheckedItem, BA ba) {
        Msgbox.DialogResponse dr = new Msgbox.DialogResponse(true);
        Msgbox.msgbox(createInputList(Items, Title, CheckedItem, ba, dr), false);
        return dr.res;
    }

    public static Object InputListAsync(List Items, CharSequence Title, int CheckedItem, BA mine, boolean Cancelable) {
        AlertDialog ad = createInputList(Items, Title, CheckedItem, (BA) mine.sharedProcessBA.activityBA.get(), new C00173(mine));
        ad.setOnCancelListener(new C00184(mine));
        return showAndTrackDialog(ad, Cancelable);
    }

    private static AlertDialog createInputList(List Items, CharSequence Title, int CheckedItem, BA ba, OnClickListener listener) {
        Builder b = new Builder(ba.context);
        CharSequence[] items = new CharSequence[Items.getSize()];
        for (int i = 0; i < Items.getSize(); i++) {
            Object o = Items.Get(i);
            if (o instanceof CharSequence) {
                items[i] = (CharSequence) o;
            } else {
                items[i] = String.valueOf(o);
            }
        }
        b.setSingleChoiceItems(items, CheckedItem, listener);
        b.setTitle(Title);
        return b.create();
    }

    @Hide
    public static Dialog showAndTrackDialog(Dialog ad, boolean Cancelable) {
        ad.setCancelable(Cancelable);
        ad.setCanceledOnTouchOutside(Cancelable);
        ad.show();
        Msgbox.trackAsyncDialog(ad);
        return ad;
    }

    @RaisesSynchronousEvents
    public static void InputMap(Map Items, CharSequence Title, BA ba) {
        Msgbox.msgbox(createInputMap(Items, Title, ba, new Msgbox.DialogResponse(false)), false);
    }

    public static Object InputMapAsync(Map Items, CharSequence Title, BA mine, boolean Cancelable) {
        AlertDialog ad = createInputMap(Items, Title, (BA) mine.sharedProcessBA.activityBA.get(), new C00195(mine));
        ad.setOnCancelListener(new C00206(mine));
        return showAndTrackDialog(ad, Cancelable);
    }

    private static AlertDialog createInputMap(Map Items, CharSequence Title, BA ba, OnClickListener listener) {
        Builder b = new Builder(ba.context);
        CharSequence[] items = new CharSequence[Items.getSize()];
        boolean[] checked = new boolean[Items.getSize()];
        int i = 0;
        for (Entry<Object, Object> e : ((MyMap) Items.getObject()).entrySet()) {
            if (e.getKey() instanceof String) {
                items[i] = (String) e.getKey();
                Object o = e.getValue();
                if (o instanceof Boolean) {
                    checked[i] = ((Boolean) o).booleanValue();
                } else {
                    checked[i] = Boolean.parseBoolean(String.valueOf(o));
                }
                i++;
            } else {
                throw new RuntimeException("Keys must be strings.");
            }
        }
        b.setMultiChoiceItems(items, checked, new C00217(Items, items));
        b.setTitle(Title);
        b.setPositiveButton("Ok", listener);
        return b.create();
    }

    @RaisesSynchronousEvents
    public static List InputMultiList(List Items, CharSequence Title, BA ba) {
        Builder b = new Builder(ba.context);
        CharSequence[] items = new CharSequence[Items.getSize()];
        for (int i = 0; i < Items.getSize(); i++) {
            Object o = Items.Get(i);
            if (o instanceof CharSequence) {
                items[i] = (CharSequence) o;
            } else {
                items[i] = String.valueOf(o);
            }
        }
        Msgbox.DialogResponse dr = new Msgbox.DialogResponse(false);
        List result = new List();
        result.Initialize();
        b.setMultiChoiceItems(items, null, new C00228(result));
        b.setTitle(Title);
        b.setPositiveButton("Ok", dr);
        Msgbox.msgbox(b.create(), false);
        if (dr.res != -1) {
            result.Clear();
        } else {
            result.Sort(true);
        }
        return result;
    }

    public static void ProgressDialogShow(BA ba, CharSequence Text) {
        ProgressDialogShow2(ba, Text, true);
    }

    public static void ProgressDialogShow2(BA ba, CharSequence Text, boolean Cancelable) {
        ProgressDialogHide();
        Msgbox.pd = new WeakReference(ProgressDialog.show(ba.context, "", Text, true, Cancelable));
    }

    public static void ProgressDialogHide() {
        Msgbox.dismissProgressDialog();
    }

    public static String GetType(Object object) {
        return object.getClass().getName();
    }

    public static boolean IsDevTool(String ToolName) {
        return ToolName.toLowerCase(BA.cul).equals("b4a");
    }

    public static int DipToCurrent(int Length) {
        return (int) (Density * ((float) Length));
    }

    public static int PerXToCurrent(float Percentage, BA ba) {
        return (int) ((Percentage / 100.0f) * ((float) ba.vg.getWidth()));
    }

    public static int PerYToCurrent(float Percentage, BA ba) {
        return (int) ((Percentage / 100.0f) * ((float) ba.vg.getHeight()));
    }

    public static boolean IsNumber(String Text) {
        try {
            Double.parseDouble(Text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static B4AException LastException(BA ba) {
        B4AException e = new B4AException();
        e.setObject(ba.getLastException());
        return e;
    }

    public static LayoutValues GetDeviceLayoutValues(BA ba) {
        DisplayMetrics dm = BA.applicationContext.getResources().getDisplayMetrics();
        LayoutValues deviceValues = new LayoutValues();
        deviceValues.Scale = dm.density;
        deviceValues.Width = dm.widthPixels;
        deviceValues.Height = dm.heightPixels;
        return deviceValues;
    }

    public static void StartActivity(BA mine, Object Activity) throws ClassNotFoundException {
        Intent i = getComponentIntent(mine, Activity);
        BA activityBA = null;
        if (mine.sharedProcessBA.activityBA != null) {
            activityBA = (BA) mine.sharedProcessBA.activityBA.get();
        }
        if (activityBA != null) {
            i.addFlags(131072);
            activityBA.context.startActivity(i);
            return;
        }
        i.addFlags(268435456);
        mine.context.startActivity(i);
    }

    public static void StartService(BA mine, Object Service) throws ClassNotFoundException {
        if (BA.shellMode) {
            BA.handler.post(new C00239(mine, Service));
            return;
        }
        BA.handler.post(new Runnable() {
            public void run() {
                Msgbox.isDismissing = false;
            }
        });
        mine.context.startService(getComponentIntent(mine, Service));
        Msgbox.isDismissing = true;
    }

    public static void StartServiceAt(BA mine, Object Service, long Time, boolean DuringSleep) throws ClassNotFoundException {
        int i = 0;
        AlarmManager am = (AlarmManager) BA.applicationContext.getSystemService("alarm");
        PendingIntent pi = createPendingIntentForAlarmManager(mine, Service);
        if (VERSION.SDK_INT < 23 || !DuringSleep) {
            if (!DuringSleep) {
                i = 1;
            }
            am.set(i, Time, pi);
            return;
        }
        am.setAndAllowWhileIdle(0, Time, pi);
    }

    public static void StartServiceAtExact(BA mine, Object Service, long Time, boolean DuringSleep) throws Exception {
        int i = 0;
        AlarmManager am = (AlarmManager) BA.applicationContext.getSystemService("alarm");
        PendingIntent pi = createPendingIntentForAlarmManager(mine, Service);
        if (VERSION.SDK_INT >= 23 && DuringSleep) {
            am.setExactAndAllowWhileIdle(0, Time, pi);
        } else if (VERSION.SDK_INT >= 19) {
            if (!DuringSleep) {
                i = 1;
            }
            am.setExact(i, Time, pi);
        } else {
            if (!DuringSleep) {
                i = 1;
            }
            am.set(i, Time, pi);
        }
    }

    private static PendingIntent createPendingIntentForAlarmManager(BA mine, Object Service) throws ClassNotFoundException {
        return PendingIntent.getBroadcast(mine.context, 1, new Intent(BA.applicationContext, getComponentClass(mine, Service, true)), 134217728);
    }

    public static void CancelScheduledService(BA mine, Object Service) throws ClassNotFoundException {
        ((AlarmManager) BA.applicationContext.getSystemService("alarm")).cancel(createPendingIntentForAlarmManager(mine, Service));
    }

    @Hide
    public static Class<?> getComponentClass(BA mine, Object component, boolean receiver) throws ClassNotFoundException {
        Class<?> resClass = null;
        if (component instanceof Class) {
            resClass = (Class) component;
        } else if (component == null || component.toString().length() == 0) {
            resClass = Class.forName(mine.className);
        } else if (component instanceof String) {
            resClass = Class.forName(BA.packageName + "." + ((String) component).toLowerCase(BA.cul));
        }
        if (resClass == null) {
            return null;
        }
        if (receiver) {
            resClass = Class.forName(resClass.getName() + "$" + resClass.getName().substring(resClass.getName().lastIndexOf(".") + 1) + "_BR");
        }
        return resClass;
    }

    @Hide
    public static Intent getComponentIntent(BA mine, Object component) throws ClassNotFoundException {
        Class<?> cls = getComponentClass(mine, component, false);
        if (cls != null) {
            return new Intent(mine.context, cls);
        }
        return (Intent) component;
    }

    public static void StopService(BA mine, Object Service) throws ClassNotFoundException {
        mine.context.stopService(getComponentIntent(mine, Service));
    }

    public static boolean SubExists(BA mine, Object Object, String Sub) throws IllegalArgumentException, SecurityException, ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        if (Object == null) {
            return false;
        }
        BA ba = getComponentBA(mine, Object);
        if (ba != null) {
            return ba.subExists(Sub.toLowerCase(BA.cul));
        }
        return false;
    }

    @DesignerName("CallSub")
    @RaisesSynchronousEvents
    public static Object CallSubNew(BA mine, Object Component, String Sub) throws Exception {
        return CallSub4(false, mine, Component, Sub, null);
    }

    @DesignerName("CallSub2")
    @RaisesSynchronousEvents
    public static Object CallSubNew2(BA mine, Object Component, String Sub, Object Argument) throws Exception {
        return CallSub4(false, mine, Component, Sub, new Object[]{Argument});
    }

    @DesignerName("CallSub3")
    @RaisesSynchronousEvents
    public static Object CallSubNew3(BA mine, Object Component, String Sub, Object Argument1, Object Argument2) throws Exception {
        return CallSub4(false, mine, Component, Sub, new Object[]{Argument1, Argument2});
    }

    @Hide
    public static Object CallSubDebug(BA mine, Object Component, String Sub) throws Exception {
        return Class.forName("anywheresoftware.b4a.debug.Debug").getDeclaredMethod("CallSubNew", new Class[]{BA.class, Object.class, String.class}).invoke(null, new Object[]{mine, Component, Sub});
    }

    @Hide
    public static Object CallSubDebug2(BA mine, Object Component, String Sub, Object Argument) throws Exception {
        return Class.forName("anywheresoftware.b4a.debug.Debug").getDeclaredMethod("CallSubNew2", new Class[]{BA.class, Object.class, String.class, Object.class}).invoke(null, new Object[]{mine, Component, Sub, Argument});
    }

    @Hide
    public static Object CallSubDebug3(BA mine, Object Component, String Sub, Object Argument1, Object Argument2) throws Exception {
        return Class.forName("anywheresoftware.b4a.debug.Debug").getDeclaredMethod("CallSubNew3", new Class[]{BA.class, Object.class, String.class, Object.class, Object.class}).invoke(null, new Object[]{mine, Component, Sub, Argument1, Argument2});
    }

    private static Object CallSub4(boolean old, BA mine, Object Component, String Sub, Object[] Arguments) throws Exception {
        Object obj = null;
        if (Component instanceof SubDelegator) {
            obj = ((SubDelegator) Component).callSub(Sub, mine.eventsTarget, Arguments);
            if (obj == SubDelegator.SubNotFound) {
                obj = null;
            } else if (obj == null || !(obj instanceof ObjectWrapper)) {
                return obj;
            } else {
                return ((ObjectWrapper) obj).getObject();
            }
        }
        BA ba = getComponentBA(mine, Component);
        if (ba != null) {
            boolean isTargetClass = Component instanceof B4AClass;
            obj = ba.raiseEvent2(mine.eventsTarget, isTargetClass, Sub.toLowerCase(BA.cul), isTargetClass, Arguments);
        }
        if (old) {
            if (obj == null) {
                obj = "";
            }
            return String.valueOf(obj);
        } else if (obj == null || !(obj instanceof ObjectWrapper)) {
            return obj;
        } else {
            return ((ObjectWrapper) obj).getObject();
        }
    }

    public static void CallSubDelayed(BA mine, Object Component, String Sub) {
        CallSubDelayed4(mine, Component, Sub, null);
    }

    public static void CallSubDelayed2(BA mine, Object Component, String Sub, Object Argument) {
        CallSubDelayed4(mine, Component, Sub, new Object[]{Argument});
    }

    public static void CallSubDelayed3(BA mine, Object Component, String Sub, Object Argument1, Object Argument2) {
        CallSubDelayed4(mine, Component, Sub, new Object[]{Argument1, Argument2});
    }

    private static void CallSubDelayed4(BA mine, Object Component, String Sub, Object[] Arguments) {
        Runnable runnable = new AnonymousClass11(mine, Component, Sub, Arguments);
        if (BA.shellMode) {
            BA.handler.post(new AnonymousClass12(runnable));
        } else {
            BA.handler.post(runnable);
        }
    }

    public static boolean IsPaused(BA mine, Object Component) throws ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
        BA ba = getComponentBA(mine, Component);
        return ba == null || ba.isActivityPaused();
    }

    @Hide
    public static BA getComponentBA(BA mine, Object Component) throws ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
        Class<?> c;
        if (Component instanceof Class) {
            c = (Class) Component;
        } else if (Component instanceof B4AClass) {
            return ((B4AClass) Component).getBA();
        } else {
            if (Component == null || Component.toString().length() == 0) {
                return mine;
            }
            c = Class.forName(BA.packageName + "." + ((String) Component).toLowerCase(BA.cul));
        }
        return (BA) c.getField("processBA").get(null);
    }

    public static String CharsToString(char[] Chars, int StartOffset, int Length) {
        return new String(Chars, StartOffset, Length);
    }

    public static String BytesToString(byte[] Data, int StartOffset, int Length, String CharSet) throws UnsupportedEncodingException {
        return new String(Data, StartOffset, Length, CharSet);
    }

    @Hide
    public static Map createMap(Object[] data) {
        Map m = new Map();
        m.Initialize();
        for (int i = 0; i < data.length; i += 2) {
            m.Put(data[i], data[i + 1]);
        }
        return m;
    }

    @Hide
    public static List ArrayToList(Object[] Array) {
        List list = new List();
        list.setObject(Arrays.asList(Array));
        return list;
    }

    @Hide
    public static List ArrayToList(int[] Array) {
        List list = new List();
        Object[] o = new Object[Array.length];
        for (int i = 0; i < Array.length; i++) {
            o[i] = Integer.valueOf(Array[i]);
        }
        list.setObject(Arrays.asList(o));
        return list;
    }

    @Hide
    public static List ArrayToList(long[] Array) {
        List list = new List();
        Object[] o = new Object[Array.length];
        for (int i = 0; i < Array.length; i++) {
            o[i] = Long.valueOf(Array[i]);
        }
        list.setObject(Arrays.asList(o));
        return list;
    }

    @Hide
    public static List ArrayToList(float[] Array) {
        List list = new List();
        Object[] o = new Object[Array.length];
        for (int i = 0; i < Array.length; i++) {
            o[i] = Float.valueOf(Array[i]);
        }
        list.setObject(Arrays.asList(o));
        return list;
    }

    @Hide
    public static List ArrayToList(double[] Array) {
        List list = new List();
        Object[] o = new Object[Array.length];
        for (int i = 0; i < Array.length; i++) {
            o[i] = Double.valueOf(Array[i]);
        }
        list.setObject(Arrays.asList(o));
        return list;
    }

    @Hide
    public static List ArrayToList(boolean[] Array) {
        List list = new List();
        Object[] o = new Object[Array.length];
        for (int i = 0; i < Array.length; i++) {
            o[i] = Boolean.valueOf(Array[i]);
        }
        list.setObject(Arrays.asList(o));
        return list;
    }

    @Hide
    public static List ArrayToList(short[] Array) {
        List list = new List();
        Object[] o = new Object[Array.length];
        for (int i = 0; i < Array.length; i++) {
            o[i] = Short.valueOf(Array[i]);
        }
        list.setObject(Arrays.asList(o));
        return list;
    }

    @Hide
    public static List ArrayToList(byte[] Array) {
        List list = new List();
        Object[] o = new Object[Array.length];
        for (int i = 0; i < Array.length; i++) {
            o[i] = Byte.valueOf(Array[i]);
        }
        list.setObject(Arrays.asList(o));
        return list;
    }

    public static boolean IsBackgroundTaskRunning(BA ba, Object ContainerObject, int TaskId) {
        return BA.isTaskRunning(ContainerObject, TaskId);
    }

    public static BitmapWrapper LoadBitmap(String Dir, String FileName) throws IOException {
        BitmapWrapper bw = new BitmapWrapper();
        bw.Initialize(Dir, FileName);
        return bw;
    }

    public static BitmapWrapper LoadBitmapSample(String Dir, String FileName, @Pixel int MaxWidth, @Pixel int MaxHeight) throws IOException {
        BitmapWrapper bw = new BitmapWrapper();
        bw.InitializeSample(Dir, FileName, MaxWidth, MaxHeight);
        return bw;
    }

    public static BitmapWrapper LoadBitmapResize(String Dir, String FileName, @Pixel int Width, @Pixel int Height, boolean KeepAspectRatio) throws IOException {
        BitmapWrapper bw = new BitmapWrapper();
        bw.InitializeResize(Dir, FileName, Width, Height, KeepAspectRatio);
        return bw;
    }

    public static String SmartStringFormatter(String Format, Object Value) {
        if (Format.length() == 0) {
            return BA.ObjectToString(Value);
        }
        if (Format.equals("date")) {
            return DateTime.Date(BA.ObjectToLongNumber(Value));
        }
        if (Format.equals("datetime")) {
            long l = BA.ObjectToLongNumber(Value);
            return DateTime.Date(l) + " " + DateTime.Time(l);
        } else if (Format.equals("time")) {
            return DateTime.Time(BA.ObjectToLongNumber(Value));
        } else {
            int i;
            if (Format.equals("xml")) {
                StringBuilder sb = new StringBuilder();
                String s = String.valueOf(Value);
                for (i = 0; i < s.length(); i++) {
                    char c = s.charAt(i);
                    switch (c) {
                        case KeyCodes.KEYCODE_F /*34*/:
                            sb.append("&quot;");
                            break;
                        case KeyCodes.KEYCODE_J /*38*/:
                            sb.append("&amp;");
                            break;
                        case KeyCodes.KEYCODE_K /*39*/:
                            sb.append("&#39;");
                            break;
                        case KeyCodes.KEYCODE_SHIFT_RIGHT /*60*/:
                            sb.append("&lt;");
                            break;
                        case KeyCodes.KEYCODE_SPACE /*62*/:
                            sb.append("&gt;");
                            break;
                        default:
                            sb.append(c);
                            break;
                    }
                }
                return sb.toString();
            }
            int minInts;
            int maxFracs;
            i = Format.indexOf(".");
            if (i > -1) {
                minInts = Integer.parseInt(Format.substring(0, i));
                maxFracs = Integer.parseInt(Format.substring(i + 1));
            } else {
                minInts = Integer.parseInt(Format);
                maxFracs = Integer.MAX_VALUE;
            }
            try {
                return NumberFormat(BA.ObjectToNumber(Value), minInts, maxFracs);
            } catch (Exception e) {
                return "NaN";
            }
        }
    }

    public static void Array() {
    }

    public static void CreateMap() {
    }

    public static void If() {
    }

    public static void Try() {
    }

    public static void Catch() {
    }

    public static void Dim() {
    }

    public static void While() {
    }

    public static void Until() {
    }

    public static void For() {
    }

    public static void Type() {
    }

    public static void Return() {
    }

    public static void Sub() {
    }

    public static void Exit() {
    }

    public static void Continue() {
    }

    public static void Select() {
    }

    public static void Is() {
    }

    public static void ExitApplication() {
        System.exit(0);
    }

    public static RemoteViews ConfigureHomeWidget(String LayoutFile, String EventName, int UpdateIntervalMinutes, String WidgetName, boolean CenterWidget) {
        return null;
    }

    public static Object Me(BA ba) {
        return null;
    }

    public static void Sleep(int Milliseconds) {
    }

    @Hide
    public static void Sleep(BA ba, ResumableSub rs, int Milliseconds) {
        BA.handler.postDelayed(new AnonymousClass13(ba, rs), (long) Milliseconds);
    }

    @Hide
    public static void WaitFor(String SubName, BA ba, ResumableSub rs, Object SenderFilter) {
        ResumableSub o;
        if (ba.waitForEvents == null) {
            ba.waitForEvents = new HashMap();
        }
        if (SenderFilter instanceof ObjectWrapper) {
            o = ((ObjectWrapper) SenderFilter).getObject();
        } else {
            o = SenderFilter;
        }
        if (o instanceof ResumableSub) {
            ResumableSub rsSenderFilter = o;
            if (rsSenderFilter.completed) {
                throw new RuntimeException("Resumable sub already completed");
            }
            rsSenderFilter.waitForBA = ba;
        }
        LinkedList<WaitForEvent> ll = (LinkedList) ba.waitForEvents.get(SubName);
        if (ll == null) {
            ll = new LinkedList();
            ba.waitForEvents.put(SubName, ll);
        }
        boolean added = false;
        Iterator<WaitForEvent> it = ll.iterator();
        while (it.hasNext()) {
            WaitForEvent wfe = (WaitForEvent) it.next();
            if (!added && ((o == null && wfe.noFilter()) || (o != null && o == wfe.senderFilter.get()))) {
                added = true;
                wfe.rs = rs;
            } else if (wfe.cleared()) {
                it.remove();
            }
        }
        if (!added) {
            wfe = new WaitForEvent(rs, o);
            if (wfe.noFilter()) {
                ll.addLast(wfe);
            } else {
                ll.addFirst(wfe);
            }
        }
    }

    @Hide
    public static void ReturnFromResumableSub(ResumableSub rs, Object returnValue) {
        BA.handler.post(new AnonymousClass14(rs, returnValue));
    }
}
