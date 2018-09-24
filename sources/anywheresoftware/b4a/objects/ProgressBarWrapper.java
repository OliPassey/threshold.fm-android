package anywheresoftware.b4a.objects;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.ActivityObject;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.ShortName;
import java.util.HashMap;

@ShortName("ProgressBar")
@ActivityObject
public class ProgressBarWrapper extends ViewWrapper<ProgressBar> {
    @Hide
    public void innerInitialize(BA ba, String eventName, boolean keepOldObject) {
        if (!keepOldObject) {
            ProgressBar pb1 = new ProgressBar(ba.context, null, 16842872);
            pb1.setIndeterminateDrawable(new ProgressBar(ba.context, null, 16842871).getIndeterminateDrawable());
            setObject(pb1);
            ((ProgressBar) getObject()).setMax(100);
            ((ProgressBar) getObject()).setIndeterminate(false);
        }
        super.innerInitialize(ba, eventName, true);
    }

    public int getProgress() {
        return ((ProgressBar) getObject()).getProgress();
    }

    public void setProgress(int value) {
        ((ProgressBar) getObject()).setProgress(value);
    }

    public void setIndeterminate(boolean value) {
        ((ProgressBar) getObject()).setIndeterminate(value);
        ((ProgressBar) getObject()).invalidate();
    }

    public boolean getIndeterminate() {
        return ((ProgressBar) getObject()).isIndeterminate();
    }

    @Hide
    public static View build(Object prev, HashMap<String, Object> props, boolean designer, Object tag) throws Exception {
        boolean indeterminate = ((Boolean) props.get("indeterminate")).booleanValue();
        if (prev == null) {
            String nativeClass = (String) props.get("nativeClass");
            if (nativeClass == null || nativeClass.length() <= 0) {
                ProgressBar pb1 = new ProgressBar((Context) tag, null, 16842872);
                pb1.setIndeterminateDrawable(new ProgressBar((Context) tag, null, 16842871).getIndeterminateDrawable());
                ProgressBar prev2 = pb1;
            } else {
                ViewWrapper.buildNativeView((Context) tag, ProgressBar.class, props, designer);
            }
        }
        ProgressBar v = (ProgressBar) ViewWrapper.build(prev, props, designer);
        v.setIndeterminate(indeterminate);
        v.setMax(100);
        if (designer) {
            v.setProgress(20);
        }
        return v;
    }
}
