package anywheresoftware.b4a.objects;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.keywords.Common;
import java.util.HashMap;
import java.util.List;

@ShortName("AutoCompleteEditText")
public class AutoCompleteEditTextWrapper extends EditTextWrapper {

    @Hide
    public static class MyArrayAdapter extends ArrayAdapter<String> {
        int gravity;
        int textColor;
        float textSize;
        private Typeface typeface;

        public MyArrayAdapter(Context context, List list, float textSize, Typeface typeface, int gravity, int textColor) {
            super(context, 0, list);
            this.typeface = typeface;
            this.textColor = textColor;
            this.textSize = textSize;
            this.gravity = gravity;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                tv = new TextView(getContext());
                tv.setLayoutParams(new LayoutParams(-1, -1));
                int p = Common.DipToCurrent(10);
                tv.setPadding(p, p, p, p);
                tv.setTextColor(this.textColor);
                tv.setTextSize(this.textSize);
                tv.setTypeface(this.typeface);
                tv.setGravity(this.gravity);
            } else {
                tv = (TextView) convertView;
            }
            tv.setText((CharSequence) getItem(position));
            return tv;
        }
    }

    @Hide
    public void innerInitialize(final BA ba, final String eventName, boolean keepOldObject) {
        this.ba = ba;
        if (!keepOldObject) {
            setObject(new AutoCompleteTextView(ba.context));
            ((EditText) getObject()).setSingleLine(true);
            ((EditText) getObject()).setImeOptions(6);
        }
        super.innerInitialize(ba, eventName, true);
        final AutoCompleteTextView a = (AutoCompleteTextView) getObject();
        if ((a.getInputType() & 15) == 1) {
            a.setInputType(a.getInputType() | 524288);
        }
        a.setThreshold(1);
        a.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ((InputMethodManager) BA.applicationContext.getSystemService("input_method")).hideSoftInputFromWindow(((EditText) AutoCompleteEditTextWrapper.this.getObject()).getWindowToken(), 0);
                ba.raiseEventFromUI(AutoCompleteEditTextWrapper.this.getObject(), eventName + "_itemclick", String.valueOf(a.getAdapter().getItem(position)));
            }
        });
    }

    public void SetItems(BA ba, anywheresoftware.b4a.objects.collections.List Items) {
        ((AutoCompleteTextView) getObject()).setAdapter(new MyArrayAdapter(ba.context, (List) Items.getObject(), getTextSize(), getTypeface(), getGravity(), getTextColor()));
    }

    public void SetItems2(BA ba, anywheresoftware.b4a.objects.collections.List Items, Typeface Typeface, int Gravity, float TextSize, int TextColor) {
        ((AutoCompleteTextView) getObject()).setAdapter(new MyArrayAdapter(ba.context, (List) Items.getObject(), TextSize, Typeface, Gravity, TextColor));
    }

    public void ShowDropDown() {
        ((AutoCompleteTextView) getObject()).showDropDown();
    }

    public void DismissDropDown() {
        ((AutoCompleteTextView) getObject()).dismissDropDown();
    }

    @Hide
    public static View build(Object prev, HashMap<String, Object> props, boolean designer, Object tag) throws Exception {
        AutoCompleteTextView v;
        if (prev == null) {
            v = (AutoCompleteTextView) ViewWrapper.buildNativeView((Context) tag, AutoCompleteTextView.class, props, designer);
        } else {
            v = (AutoCompleteTextView) prev;
        }
        return EditTextWrapper.build(v, props, designer, tag);
    }
}
