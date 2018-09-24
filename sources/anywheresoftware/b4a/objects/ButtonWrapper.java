package anywheresoftware.b4a.objects;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.ActivityObject;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.DynamicBuilder;
import java.util.HashMap;

@ShortName("Button")
@ActivityObject
public class ButtonWrapper extends TextViewWrapper<Button> {
    @Hide
    public void innerInitialize(final BA ba, final String eventName, boolean keepOldObject) {
        if (!keepOldObject) {
            setObject(new Button(ba.context));
            removeCaps((Button) getObject());
        }
        super.innerInitialize(ba, eventName, true);
        if (ba.subExists(new StringBuilder(String.valueOf(eventName)).append("_down").toString()) || ba.subExists(new StringBuilder(String.valueOf(eventName)).append("_up").toString())) {
            ((Button) getObject()).setOnTouchListener(new OnTouchListener() {
                private boolean down = false;

                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == 0) {
                        this.down = true;
                        ba.raiseEventFromUI(ButtonWrapper.this.getObject(), eventName + "_down", new Object[0]);
                    } else if (this.down && (event.getAction() == 1 || event.getAction() == 3)) {
                        this.down = false;
                        ba.raiseEventFromUI(ButtonWrapper.this.getObject(), eventName + "_up", new Object[0]);
                    } else if (event.getAction() == 2) {
                        int[] states = v.getDrawableState();
                        if (states != null) {
                            int i = 0;
                            while (i < states.length) {
                                if (states[i] != 16842919) {
                                    i++;
                                } else if (!this.down) {
                                    ba.raiseEventFromUI(ButtonWrapper.this.getObject(), eventName + "_down", new Object[0]);
                                    this.down = true;
                                }
                            }
                            if (this.down) {
                                ba.raiseEventFromUI(ButtonWrapper.this.getObject(), eventName + "_up", new Object[0]);
                                this.down = false;
                            }
                        }
                    }
                    return false;
                }
            });
        }
    }

    private static void removeCaps(Button btn) {
        if (VERSION.SDK_INT >= 21) {
            try {
                TextView.class.getDeclaredMethod("setAllCaps", new Class[]{Boolean.TYPE}).invoke(btn, new Object[]{Boolean.valueOf(false)});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Hide
    public static View build(Object prev, HashMap<String, Object> props, boolean designer, Object tag) throws Exception {
        if (prev == null) {
            prev = ViewWrapper.buildNativeView((Context) tag, Button.class, props, designer);
            removeCaps((Button) prev);
        }
        TextView v = (TextView) TextViewWrapper.build(prev, props, designer);
        Drawable d = (Drawable) DynamicBuilder.build(prev, (HashMap) props.get("drawable"), designer, null);
        if (d != null) {
            v.setBackgroundDrawable(d);
        }
        if (designer) {
            v.setPressed(((Boolean) props.get("pressed")).booleanValue());
        }
        return v;
    }
}
