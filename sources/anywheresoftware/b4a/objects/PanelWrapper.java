package anywheresoftware.b4a.objects;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.ActivityObject;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.IterableList;
import anywheresoftware.b4a.BA.Pixel;
import anywheresoftware.b4a.BA.RaisesSynchronousEvents;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.BALayout.LayoutParams;
import anywheresoftware.b4a.DynamicBuilder;
import anywheresoftware.b4a.keywords.Common;
import anywheresoftware.b4a.keywords.LayoutBuilder;
import anywheresoftware.b4a.keywords.LayoutValues;
import anywheresoftware.b4a.objects.ActivityWrapper.AllViewsIterator;
import java.util.HashMap;

@ShortName("Panel")
@ActivityObject
public class PanelWrapper extends ViewWrapper<ViewGroup> implements IterableList {
    public static final int ACTION_DOWN = 0;
    public static final int ACTION_MOVE = 2;
    public static final int ACTION_UP = 1;

    @Hide
    public void innerInitialize(final BA ba, final String eventName, boolean keepOldObject) {
        if (!keepOldObject) {
            setObject(new BALayout(ba.context));
        }
        super.innerInitialize(ba, eventName, true);
        if (ba.subExists(new StringBuilder(String.valueOf(eventName)).append("_touch").toString())) {
            ((ViewGroup) getObject()).setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    ba.raiseEventFromUI(PanelWrapper.this.getObject(), eventName + "_touch", Integer.valueOf(event.getAction()), Float.valueOf(event.getX()), Float.valueOf(event.getY()));
                    return true;
                }
            });
        }
    }

    public void AddView(View View, int Left, int Top, int Width, int Height) {
        ((ViewGroup) getObject()).addView(View, new LayoutParams(Left, Top, Width, Height));
    }

    public ConcreteViewWrapper GetView(int Index) {
        ConcreteViewWrapper c = new ConcreteViewWrapper();
        c.setObject(((ViewGroup) getObject()).getChildAt(Index));
        return c;
    }

    public void RemoveAllViews() {
        ((ViewGroup) getObject()).removeAllViews();
    }

    public void RemoveViewAt(int Index) {
        ((ViewGroup) getObject()).removeViewAt(Index);
    }

    public int getNumberOfViews() {
        return ((ViewGroup) getObject()).getChildCount();
    }

    public float getElevation() throws Exception {
        if (VERSION.SDK_INT >= 21) {
            return ((Float) View.class.getDeclaredMethod("getElevation", new Class[0]).invoke(getObject(), new Object[0])).floatValue();
        }
        return Common.Density;
    }

    public void setElevation(@Pixel float e) throws Exception {
        if (VERSION.SDK_INT >= 21) {
            View.class.getDeclaredMethod("setElevation", new Class[]{Float.TYPE}).invoke(getObject(), new Object[]{Float.valueOf(e)});
        }
    }

    public void SetElevationAnimated(int Duration, @Pixel float Elevation) throws Exception {
        if (VERSION.SDK_INT >= 21) {
            float current = getElevation();
            setElevation(Elevation);
            ObjectAnimator.ofFloat(getObject(), "translationZ", new float[]{current - Elevation, Common.Density}).setDuration((long) Duration).start();
        }
    }

    @RaisesSynchronousEvents
    public LayoutValues LoadLayout(String LayoutFile, BA ba) throws Exception {
        ViewGroup.LayoutParams lp = ((ViewGroup) getObject()).getLayoutParams();
        boolean zeroSize = false;
        boolean width_fill_parent = false;
        if (lp == null || lp.width == 0 || lp.height == 0) {
            zeroSize = true;
        }
        if (!zeroSize && lp.width == -1) {
            if (((ViewGroup) getObject()).getParent() == null || ((View) ((ViewGroup) getObject()).getParent()).getLayoutParams() == null) {
                zeroSize = true;
            } else {
                setWidth(((View) ((ViewGroup) getObject()).getParent()).getLayoutParams().width);
                width_fill_parent = true;
            }
        }
        if (zeroSize) {
            BA.LogInfo("Panel size is unknown. Layout may not be loaded correctly.");
        }
        LayoutValues lv = LayoutBuilder.loadLayout(LayoutFile, ba, false, (ViewGroup) getObject(), null, false).layoutValues;
        if (width_fill_parent) {
            setWidth(-1);
        }
        return lv;
    }

    @Hide
    public Object Get(int index) {
        return GetView(index).getObject();
    }

    @Hide
    public int getSize() {
        return getNumberOfViews();
    }

    public IterableList GetAllViewsRecursive() {
        return new AllViewsIterator((ViewGroup) getObject());
    }

    @Hide
    public static View build(Object prev, HashMap<String, Object> props, boolean designer, Object tag) throws Exception {
        View vg = (View) prev;
        if (vg == null) {
            vg = (View) ViewWrapper.buildNativeView((Context) tag, BALayout.class, props, designer);
        }
        vg = ViewWrapper.build(vg, props, designer);
        Drawable d = (Drawable) DynamicBuilder.build(vg, (HashMap) props.get("drawable"), designer, null);
        if (d != null) {
            vg.setBackgroundDrawable(d);
        }
        if (VERSION.SDK_INT >= 21) {
            float elevation = ((Float) BA.gm(props, "elevation", Float.valueOf(Common.Density))).floatValue();
            View.class.getDeclaredMethod("setElevation", new Class[]{Float.TYPE}).invoke(vg, new Object[]{Float.valueOf(BALayout.getDeviceScale() * elevation)});
        }
        return vg;
    }
}
