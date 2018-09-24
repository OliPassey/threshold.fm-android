package anywheresoftware.b4a.objects;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.ActivityObject;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.DynamicBuilder;
import java.util.HashMap;

@ActivityObject
@ShortName("ScrollView")
public class ScrollViewWrapper extends ViewWrapper<ScrollView> {
    private PanelWrapper pw = new PanelWrapper();

    @Hide
    public static class MyScrollView extends ScrollView {
        public BA ba;
        public String eventName;

        public MyScrollView(Context context) {
            super(context);
        }

        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            super.onScrollChanged(l, t, oldl, oldt);
            if (this.ba != null) {
                this.ba.raiseEventFromUI(this, this.eventName + "_scrollchanged", Integer.valueOf(t));
            }
        }
    }

    public void Initialize(BA ba, int Height) {
        Initialize2(ba, Height, "");
    }

    public void Initialize2(BA ba, int Height, String EventName) {
        super.Initialize(ba, EventName);
        PanelWrapper p = new PanelWrapper();
        p.Initialize(ba, "");
        ((ScrollView) getObject()).addView((View) p.getObject(), -1, Height);
    }

    @Hide
    public void innerInitialize(BA ba, String EventName, boolean keepOldObject) {
        if (!keepOldObject) {
            setObject(new MyScrollView(ba.context));
        }
        super.innerInitialize(ba, EventName, true);
        if (ba.subExists(new StringBuilder(String.valueOf(EventName)).append("_scrollchanged").toString()) && (getObject() instanceof MyScrollView)) {
            MyScrollView m = (MyScrollView) getObject();
            m.ba = ba;
            m.eventName = EventName;
        }
    }

    public PanelWrapper getPanel() {
        this.pw.setObject((ViewGroup) ((ScrollView) getObject()).getChildAt(0));
        return this.pw;
    }

    public void FullScroll(boolean Bottom) {
        ((ScrollView) getObject()).fullScroll(Bottom ? 130 : 33);
    }

    public int getScrollPosition() {
        return ((ScrollView) getObject()).getScrollY();
    }

    public void setScrollPosition(int Scroll) {
        ((ScrollView) getObject()).smoothScrollTo(0, Scroll);
    }

    public void ScrollToNow(int Scroll) {
        ((ScrollView) getObject()).scrollTo(0, Scroll);
    }

    @Hide
    public static View build(Object prev, HashMap<String, Object> props, boolean designer, Object tag) throws Exception {
        if (prev == null) {
            prev = ViewWrapper.buildNativeView((Context) tag, MyScrollView.class, props, designer);
        }
        ScrollView v = (ScrollView) ViewWrapper.build(prev, props, designer);
        if (v.getChildCount() > 0) {
            v.removeAllViews();
        }
        Drawable d = (Drawable) DynamicBuilder.build(prev, (HashMap) props.get("drawable"), designer, null);
        if (props.containsKey("innerHeight")) {
            BALayout b = new BALayout((Context) tag);
            v.addView(b, -1, (int) (((float) ((Integer) props.get("innerHeight")).intValue()) * BALayout.getDeviceScale()));
            b.setBackgroundDrawable(d);
        } else {
            v.setBackgroundDrawable(d);
        }
        return v;
    }
}
