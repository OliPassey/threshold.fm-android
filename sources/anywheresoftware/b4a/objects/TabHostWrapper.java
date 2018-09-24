package anywheresoftware.b4a.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.ActivityObject;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.RaisesSynchronousEvents;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.keywords.Common;
import anywheresoftware.b4a.objects.drawable.BitmapDrawable;
import java.util.HashMap;

@ActivityObject
@ShortName("TabHost")
public class TabHostWrapper extends ViewWrapper<TabHost> {

    private static class MyContentFactory implements TabContentFactory {
        private View view;

        public MyContentFactory(View view) {
            this.view = view;
        }

        public View createTabContent(String tag) {
            return this.view;
        }
    }

    @Hide
    public static class MyTabHost extends TabHost {
        public MyTabHost(Context context) {
            super(context, null);
        }
    }

    @Hide
    public void innerInitialize(final BA ba, final String eventName, boolean keepOldObject) {
        if (!keepOldObject) {
            setObject(new TabHost(ba.context, null));
        }
        super.innerInitialize(ba, eventName, true);
        initializeTabWidget(ba.context, (TabHost) getObject());
        if (ba.subExists(new StringBuilder(String.valueOf(eventName)).append("_tabchanged").toString())) {
            ((TabHost) getObject()).setOnTabChangedListener(new OnTabChangeListener() {
                public void onTabChanged(String tabId) {
                    ba.raiseEvent2(TabHostWrapper.this.getObject(), false, eventName + "_tabchanged", false, new Object[0]);
                }
            });
        }
        MyContentFactory m = new MyContentFactory(new View(ba.context));
        TabSpec ts = ((TabHost) getObject()).newTabSpec("~temp");
        ts.setContent(m);
        ts.setIndicator("");
        ((TabHost) getObject()).addTab(ts);
    }

    private static void initializeTabWidget(Context context, TabHost tabHost) {
        TabWidget tw = new TabWidget(context);
        LinearLayout ll = new LinearLayout(context);
        int pad = Common.DipToCurrent(5);
        ll.setPadding(pad, pad, pad, pad);
        ll.setOrientation(1);
        tw.setId(16908307);
        ll.addView(tw, new LayoutParams(-1, -2));
        FrameLayout fl = new FrameLayout(context);
        fl.setId(16908305);
        fl.setPadding(pad, pad, pad, pad);
        ll.addView(fl, new LayoutParams(-1, -1));
        tabHost.addView(ll, new LayoutParams(-1, -1));
        tabHost.setup();
    }

    public void AddTab2(String Title, View View) {
        if (((TabHost) getObject()).getCurrentTabTag().equals("~temp")) {
            ((TabHost) getObject()).clearAllTabs();
        }
        MyContentFactory m = new MyContentFactory(View);
        TabSpec ts = ((TabHost) getObject()).newTabSpec("");
        ts.setContent(m);
        ts.setIndicator(Title);
        ((TabHost) getObject()).addTab(ts);
    }

    @RaisesSynchronousEvents
    public void AddTab(BA ba, String Title, String LayoutFile) throws Exception {
        AddTab2(Title, createPanelForLayoutFile(ba, LayoutFile));
    }

    private View createPanelForLayoutFile(BA ba, String LayoutFile) throws Exception {
        PanelWrapper pw = new PanelWrapper();
        pw.Initialize(ba, "");
        int yfix = 84;
        if (BA.applicationContext.getApplicationInfo().targetSdkVersion >= 11 && VERSION.SDK_INT >= 11 && Common.GetDeviceLayoutValues(ba).getApproximateScreenSize() < 5.0d) {
            yfix = 68;
        }
        ((ViewGroup) pw.getObject()).setLayoutParams(new LayoutParams(getWidth() - Common.DipToCurrent(20), getHeight() - Common.DipToCurrent(yfix)));
        pw.LoadLayout(LayoutFile, ba);
        return (View) pw.getObject();
    }

    public void AddTabWithIcon2(String Title, Bitmap DefaultBitmap, Bitmap SelectedBitmap, View View) {
        if (((TabHost) getObject()).getCurrentTabTag().equals("~temp")) {
            ((TabHost) getObject()).clearAllTabs();
        }
        MyContentFactory m = new MyContentFactory(View);
        TabSpec ts = ((TabHost) getObject()).newTabSpec("");
        ts.setContent(m);
        BitmapDrawable bd1 = new BitmapDrawable();
        bd1.Initialize(DefaultBitmap);
        BitmapDrawable bd2 = new BitmapDrawable();
        bd2.Initialize(SelectedBitmap);
        StateListDrawable sd = new StateListDrawable();
        sd.addState(new int[]{anywheresoftware.b4a.objects.drawable.StateListDrawable.State_Selected}, (Drawable) bd2.getObject());
        sd.addState(new int[0], (Drawable) bd1.getObject());
        ts.setIndicator(Title, sd);
        ((TabHost) getObject()).addTab(ts);
    }

    @RaisesSynchronousEvents
    public void AddTabWithIcon(BA ba, String Title, Bitmap DefaultBitmap, Bitmap SelectedBitmap, String LayoutFile) throws Exception {
        AddTabWithIcon2(Title, DefaultBitmap, SelectedBitmap, createPanelForLayoutFile(ba, LayoutFile));
    }

    public int getCurrentTab() {
        return ((TabHost) getObject()).getCurrentTab();
    }

    @RaisesSynchronousEvents
    public void setCurrentTab(int Index) {
        ((TabHost) getObject()).setCurrentTab(Index);
    }

    public int getTabCount() {
        return ((TabHost) getObject()).getTabWidget().getTabCount();
    }

    @Hide
    public static View build(Object prev, HashMap<String, Object> props, boolean designer, Object tag) throws Exception {
        boolean firstTime = false;
        if (prev == null) {
            firstTime = true;
            prev = ViewWrapper.buildNativeView((Context) tag, MyTabHost.class, props, designer);
        }
        TabHost th = (TabHost) ViewWrapper.build(prev, props, designer);
        if (designer && firstTime) {
            initializeTabWidget((Context) tag, th);
            TextView v = new TextView((Context) tag);
            v.setText("This is an example page.\nTab pages should be added programmatically.");
            for (int i = 1; i <= 3; i++) {
                MyContentFactory m = new MyContentFactory(v);
                TabSpec ts = th.newTabSpec("");
                ts.setContent(m);
                ts.setIndicator("Page " + i);
                th.addTab(ts);
            }
        }
        return th;
    }
}
