package anywheresoftware.b4a.objects;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import anywheresoftware.b4a.B4AClass;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.BALayout.LayoutParams;
import anywheresoftware.b4a.DynamicBuilder;
import anywheresoftware.b4a.keywords.Common.DesignerCustomView;
import anywheresoftware.b4a.keywords.LayoutBuilder.DesignerTextSizeMethod;
import anywheresoftware.b4a.objects.collections.Map;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map.Entry;

@Hide
public class CustomViewWrapper extends ViewWrapper<BALayout> implements DesignerTextSizeMethod {
    public Object customObject;
    private String eventName;
    public HashMap<String, Object> props;

    @Hide
    public void innerInitialize(BA ba, String eventName, boolean keepOldObject) {
        this.ba = ba;
        this.eventName = eventName;
    }

    public void AfterDesignerScript() throws ClassNotFoundException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Class<?> c = this.customObject.getClass();
        boolean userClass = this.customObject instanceof B4AClass;
        Map m = new Map();
        m.Initialize();
        m.Put("defaultcolor", Integer.valueOf(ViewWrapper.defaultColor));
        PanelWrapper pw = new PanelWrapper();
        pw.setObject((ViewGroup) getObject());
        LabelWrapper lw = new LabelWrapper();
        lw.setObject((TextView) getTag());
        lw.setTextSize(((Float) this.props.get("fontsize")).floatValue());
        pw.setTag(this.props.get("tag"));
        m.Put("activity", this.ba.vg);
        m.Put("ba", this.ba);
        this.eventName = this.eventName.toLowerCase(BA.cul);
        m.Put("eventName", this.eventName);
        if (this.props.containsKey("customProperties")) {
            for (Entry<String, Object> e : ((HashMap) this.props.get("customProperties")).entrySet()) {
                m.Put(e.getKey(), e.getValue());
            }
        }
        Object target = this.ba.eventsTarget != null ? this.ba.eventsTarget : this.ba.activity.getClass();
        if (BA.isShellModeRuntimeCheck(this.ba) && userClass) {
            this.ba.raiseEvent2(null, true, "CREATE_CUSTOM_VIEW", true, this.customObject, this.ba, target, this.eventName, pw, lw, m);
            return;
        }
        c.getMethod("_initialize", new Class[]{BA.class, Object.class, String.class}).invoke(this.customObject, new Object[]{this.ba, target, this.eventName});
        if (userClass) {
            this.customObject.getBA().raiseEvent2(null, true, "designercreateview", true, pw, lw, m);
        } else {
            ((DesignerCustomView) this.customObject).DesignerCreateView(pw, lw, m);
        }
    }

    public float getTextSize() {
        return ((Float) this.props.get("fontsize")).floatValue();
    }

    public void setTextSize(float TextSize) {
        this.props.put("fontsize", Float.valueOf(TextSize));
    }

    @Hide
    public static View build(Object prev, HashMap<String, Object> props, boolean designer, Object tag) throws Exception {
        if (prev == null) {
            prev = ViewWrapper.buildNativeView((Context) tag, BALayout.class, props, designer);
        }
        ViewGroup v = (ViewGroup) ViewWrapper.build(prev, props, designer);
        Drawable d = (Drawable) DynamicBuilder.build(prev, (HashMap) props.get("drawable"), designer, null);
        if (d != null) {
            v.setBackgroundDrawable(d);
        }
        TextView label = (TextView) TextViewWrapper.build(ViewWrapper.buildNativeView((Context) tag, TextView.class, props, designer), props, designer);
        if (!designer) {
            v.setTag(label);
        }
        if (designer) {
            v.removeAllViews();
            v.addView(label, new LayoutParams(0, 0, -1, -1));
        }
        return v;
    }

    @Hide
    @Deprecated
    public static void replaceBaseWithView(PanelWrapper base, View view) {
        ViewGroup basePanel = (ViewGroup) base.getObject();
        ViewGroup vg = (ViewGroup) basePanel.getParent();
        vg.addView(view, vg.indexOfChild(basePanel), basePanel.getLayoutParams());
        base.RemoveView();
    }

    @Hide
    public static void replaceBaseWithView2(PanelWrapper base, View view) {
        ViewGroup basePanel = (ViewGroup) base.getObject();
        ViewGroup vg = (ViewGroup) basePanel.getParent();
        vg.addView(view, vg.indexOfChild(basePanel), basePanel.getLayoutParams());
        view.setTag(basePanel.getTag());
        base.RemoveView();
    }
}
