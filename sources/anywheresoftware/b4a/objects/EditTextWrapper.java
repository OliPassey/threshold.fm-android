package anywheresoftware.b4a.objects;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.ActivityObject;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.RaisesSynchronousEvents;
import anywheresoftware.b4a.BA.ShortName;
import java.util.HashMap;

@ShortName("EditText")
@ActivityObject
public class EditTextWrapper extends TextViewWrapper<EditText> {
    public static final int INPUT_TYPE_DECIMAL_NUMBERS = 12290;
    public static final int INPUT_TYPE_NONE = 0;
    public static final int INPUT_TYPE_NUMBERS = 2;
    public static final int INPUT_TYPE_PHONE = 3;
    public static final int INPUT_TYPE_TEXT = 1;

    @Hide
    public void innerInitialize(final BA ba, final String eventName, boolean keepOldObject) {
        this.ba = ba;
        if (!keepOldObject) {
            setObject(new EditText(ba.context));
        }
        super.innerInitialize(ba, eventName, true);
        if (ba.subExists(new StringBuilder(String.valueOf(eventName)).append("_textchanged").toString())) {
            ((EditText) getObject()).addTextChangedListener(new TextWatcher() {
                private CharSequence old;

                public void afterTextChanged(Editable s) {
                    ba.raiseEvent2(EditTextWrapper.this.getObject(), false, eventName + "_textchanged", true, this.old, ((EditText) EditTextWrapper.this.getObject()).getText().toString());
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    this.old = s.toString();
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
        }
        if (ba.subExists(new StringBuilder(String.valueOf(eventName)).append("_enterpressed").toString())) {
            ((EditText) getObject()).setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    ba.raiseEvent(EditTextWrapper.this.getObject(), eventName + "_enterpressed", new Object[0]);
                    return false;
                }
            });
        }
        if (ba.subExists(new StringBuilder(String.valueOf(eventName)).append("_focuschanged").toString())) {
            ((EditText) getObject()).setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    ba.raiseEventFromUI(EditTextWrapper.this.getObject(), eventName + "_focuschanged", Boolean.valueOf(hasFocus));
                }
            });
        }
    }

    public void setForceDoneButton(boolean value) {
        if (value) {
            ((EditText) getObject()).setImeOptions(6);
        } else {
            ((EditText) getObject()).setImeOptions(0);
        }
    }

    public void setSingleLine(boolean singleLine) {
        ((EditText) getObject()).setSingleLine(singleLine);
    }

    public void setPasswordMode(boolean value) {
        if (value) {
            ((EditText) getObject()).setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            ((EditText) getObject()).setTransformationMethod(null);
        }
    }

    public int getSelectionStart() {
        return Selection.getSelectionStart(((EditText) getObject()).getText());
    }

    public void setSelectionStart(int value) {
        ((EditText) getObject()).setSelection(value);
    }

    public int getSelectionLength() {
        return Math.max(0, Selection.getSelectionEnd(((EditText) getObject()).getText()) - getSelectionStart());
    }

    public void SetSelection(int Start, int Length) {
        Selection.setSelection(((EditText) getObject()).getText(), Start, Start + Length);
    }

    public void SelectAll() {
        Selection.selectAll(((EditText) getObject()).getText());
    }

    public void setInputType(int value) {
        ((EditText) getObject()).setInputType(value);
    }

    public int getInputType() {
        return ((EditText) getObject()).getInputType();
    }

    public void setWrap(boolean value) {
        ((EditText) getObject()).setHorizontallyScrolling(!value);
    }

    public void setHint(String text) {
        ((EditText) getObject()).setHint(text);
    }

    public String getHint() {
        CharSequence c = ((EditText) getObject()).getHint();
        return c == null ? "" : String.valueOf(c);
    }

    public void setHintColor(int Color) {
        ((EditText) getObject()).setHintTextColor(Color);
    }

    public int getHintColor() {
        return ((EditText) getObject()).getCurrentHintTextColor();
    }

    @RaisesSynchronousEvents
    public void setText(CharSequence Text) {
        super.setText(Text);
    }

    @Hide
    public static View build(Object prev, HashMap<String, Object> props, boolean designer, Object tag) throws Exception {
        EditText v;
        if (prev == null) {
            v = (EditText) ViewWrapper.buildNativeView((Context) tag, EditText.class, props, designer);
        } else {
            v = (EditText) prev;
        }
        TextViewWrapper.build(v, props, designer);
        ColorStateList defaultHintColor = null;
        if (designer) {
            defaultHintColor = (ColorStateList) ViewWrapper.getDefault(v, "hintColor", v.getHintTextColors());
        }
        int hintColor = ((Integer) BA.gm(props, "hintColor", Integer.valueOf(ViewWrapper.defaultColor))).intValue();
        if (hintColor != ViewWrapper.defaultColor) {
            v.setHintTextColor(hintColor);
        } else if (designer) {
            v.setHintTextColor(defaultHintColor);
        }
        String hint = (String) BA.gm(props, "hint", "");
        if (designer && hint.length() == 0) {
            hint = (String) props.get("name");
        }
        v.setHint(hint);
        String inputType = (String) props.get("inputType");
        if (inputType != null) {
            v.setInputType(((Integer) EditTextWrapper.class.getField("INPUT_TYPE_" + inputType).get(null)).intValue());
        }
        boolean singleLine = ((Boolean) props.get("singleLine")).booleanValue();
        v.setSingleLine(singleLine);
        if (designer && singleLine) {
            v.setInputType(524288);
        }
        if (((Boolean) props.get("password")).booleanValue()) {
            v.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            v.setTransformationMethod(null);
        }
        v.setHorizontallyScrolling(!((Boolean) BA.gm(props, "wrap", Boolean.valueOf(true))).booleanValue());
        if (((Boolean) BA.gm(props, "forceDone", Boolean.valueOf(false))).booleanValue()) {
            v.setImeOptions(6);
        } else {
            v.setImeOptions(0);
        }
        return v;
    }
}
