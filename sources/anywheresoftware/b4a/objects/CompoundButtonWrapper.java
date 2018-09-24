package anywheresoftware.b4a.objects;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.ToggleButton;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.ActivityObject;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.RaisesSynchronousEvents;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.DynamicBuilder;
import java.util.HashMap;

@Hide
public class CompoundButtonWrapper<T extends CompoundButton> extends TextViewWrapper<T> {

    @ActivityObject
    @ShortName("CheckBox")
    public static class CheckBoxWrapper extends CompoundButtonWrapper<CheckBox> {
        @Hide
        public void innerInitialize(BA ba, String eventName, boolean keepOldObject) {
            if (!keepOldObject) {
                setObject(new CheckBox(ba.context));
            }
            super.innerInitialize(ba, eventName, true);
        }

        @Hide
        public static View build(Object prev, HashMap<String, Object> props, boolean designer, Object tag) throws Exception {
            if (prev == null) {
                prev = ViewWrapper.buildNativeView((Context) tag, CheckBox.class, props, designer);
            }
            return (CheckBox) CompoundButtonWrapper.build(prev, props, designer);
        }
    }

    @ActivityObject
    @ShortName("RadioButton")
    public static class RadioButtonWrapper extends CompoundButtonWrapper<RadioButton> {

        private static class RadioButtonListener implements OnCheckedChangeListener {
            private BA ba;
            private RadioButton current;
            private String eventName;

            public RadioButtonListener(String eventName, BA ba, RadioButton current) {
                this.eventName = eventName;
                this.ba = ba;
                this.current = current;
            }

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ViewParent vp = this.current.getParent();
                    if (vp instanceof ViewGroup) {
                        ViewGroup vg = (ViewGroup) vp;
                        for (int i = 0; i < vg.getChildCount(); i++) {
                            View v = vg.getChildAt(i);
                            if ((v instanceof RadioButton) && v != this.current) {
                                RadioButton rb = (RadioButton) v;
                                if (rb.isChecked()) {
                                    rb.setChecked(false);
                                }
                            }
                        }
                    }
                    if (this.eventName.length() > 0) {
                        this.ba.raiseEvent2(this.current, false, this.eventName + "_checkedchange", false, Boolean.valueOf(isChecked));
                    }
                }
            }
        }

        @Hide
        public void innerInitialize(BA ba, String eventName, boolean keepOldObject) {
            if (!keepOldObject) {
                setObject(new RadioButton(ba.context));
            }
            super.innerInitialize(ba, eventName, true, false);
            ((RadioButton) getObject()).setOnCheckedChangeListener(new RadioButtonListener(eventName, ba, (RadioButton) getObject()));
        }

        @Hide
        public static View build(Object prev, HashMap<String, Object> props, boolean designer, Object tag) throws Exception {
            if (prev == null) {
                prev = ViewWrapper.buildNativeView((Context) tag, RadioButton.class, props, designer);
            }
            return (RadioButton) CompoundButtonWrapper.build(prev, props, designer);
        }
    }

    @ActivityObject
    @ShortName("ToggleButton")
    public static class ToggleButtonWrapper extends CompoundButtonWrapper<ToggleButton> {
        @Hide
        public void innerInitialize(BA ba, String eventName, boolean keepOldObject) {
            if (!keepOldObject) {
                setObject(new ToggleButton(ba.context));
                ((ToggleButton) getObject()).setText("");
            }
            super.innerInitialize(ba, eventName, true);
        }

        public String getTextOn() {
            return String.valueOf(((ToggleButton) getObject()).getTextOn());
        }

        public void setTextOn(CharSequence value) {
            ((ToggleButton) getObject()).setTextOn(value);
            setChecked(getChecked());
        }

        public String getTextOff() {
            return String.valueOf(((ToggleButton) getObject()).getTextOff());
        }

        public void setTextOff(CharSequence value) {
            ((ToggleButton) getObject()).setTextOff(value);
            setChecked(getChecked());
        }

        @Hide
        public String getText() {
            return "";
        }

        @Hide
        public void setText(CharSequence Text) {
        }

        @Hide
        public static View build(Object prev, HashMap<String, Object> props, boolean designer, Object tag) throws Exception {
            ToggleButton prev2;
            if (prev == null) {
                prev2 = ViewWrapper.buildNativeView((Context) tag, ToggleButton.class, props, designer);
            }
            ToggleButton v = prev2;
            v.setTextOn((String) props.get("textOn"));
            v.setTextOff((String) props.get("textOff"));
            v = (ToggleButton) CompoundButtonWrapper.build(prev2, props, designer);
            v.setTextColor(((Integer) props.get("textColor")).intValue());
            return v;
        }
    }

    @Hide
    public void innerInitialize(BA ba, String eventName, boolean keepOldObject) {
        innerInitialize(ba, eventName, keepOldObject, true);
    }

    protected void innerInitialize(final BA ba, final String eventName, boolean keepOldObject, boolean addCheckedChangeEvent) {
        super.innerInitialize(ba, eventName, true);
        if (ba.subExists(new StringBuilder(String.valueOf(eventName)).append("_checkedchange").toString())) {
            ((CompoundButton) getObject()).setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ba.raiseEvent2(CompoundButtonWrapper.this.getObject(), false, eventName + "_checkedchange", false, Boolean.valueOf(isChecked));
                }
            });
        }
    }

    public boolean getChecked() {
        return ((CompoundButton) getObject()).isChecked();
    }

    @RaisesSynchronousEvents
    public void setChecked(boolean Value) {
        ((CompoundButton) getObject()).setChecked(Value);
    }

    @Hide
    public String toString() {
        String s = super.toString();
        if (IsInitialized()) {
            return new StringBuilder(String.valueOf(s)).append(", Checked=").append(getChecked()).toString();
        }
        return s;
    }

    @Hide
    public static View build(Object prev, HashMap<String, Object> props, boolean designer) throws Exception {
        CompoundButton v = (CompoundButton) TextViewWrapper.build(prev, props, designer);
        v.setChecked(((Boolean) props.get("isChecked")).booleanValue());
        HashMap<String, Object> drawProps = (HashMap) props.get("drawable");
        if (drawProps != null) {
            Drawable d = (Drawable) DynamicBuilder.build(prev, drawProps, designer, null);
            if (d != null) {
                v.setBackgroundDrawable(d);
            }
        }
        return v;
    }
}
