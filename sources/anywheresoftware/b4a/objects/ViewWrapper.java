package anywheresoftware.b4a.objects;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.B4aDebuggable;
import anywheresoftware.b4a.BA.DesignerName;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.Pixel;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.BALayout.LayoutParams;
import anywheresoftware.b4a.keywords.Common;
import anywheresoftware.b4a.keywords.LayoutBuilder.ViewWrapperAndAnchor;
import anywheresoftware.b4a.objects.drawable.BitmapDrawable;
import anywheresoftware.b4a.objects.drawable.ColorDrawable;
import anywheresoftware.b4a.objects.drawable.ColorDrawable.GradientDrawableWithCorners;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Hide
public class ViewWrapper<T extends View> extends AbsObjectWrapper<T> implements B4aDebuggable {
    @Hide
    public static int animationDuration = 400;
    @Hide
    public static final int defaultColor = -984833;
    @Hide
    public static int lastId = 0;
    protected BA ba;

    public void Initialize(BA ba, String EventName) {
        innerInitialize(ba, EventName.toLowerCase(BA.cul), false);
    }

    @Hide
    public void innerInitialize(final BA ba, final String eventName, boolean keepOldObject) {
        this.ba = ba;
        View view = (View) getObject();
        int i = lastId + 1;
        lastId = i;
        view.setId(i);
        if (ba.subExists(new StringBuilder(String.valueOf(eventName)).append("_click").toString())) {
            ((View) getObject()).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ba.raiseEvent(v, eventName + "_click", new Object[0]);
                }
            });
        }
        if (ba.subExists(new StringBuilder(String.valueOf(eventName)).append("_longclick").toString())) {
            ((View) getObject()).setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View v) {
                    ba.raiseEvent(v, eventName + "_longclick", new Object[0]);
                    return true;
                }
            });
        }
    }

    public Drawable getBackground() {
        return ((View) getObject()).getBackground();
    }

    public void setBackground(Drawable drawable) {
        ((View) getObject()).setBackgroundDrawable(drawable);
    }

    @DesignerName("SetBackgroundImage")
    public BitmapDrawable SetBackgroundImageNew(Bitmap Bitmap) {
        BitmapDrawable bd = new BitmapDrawable();
        bd.Initialize(Bitmap);
        ((View) getObject()).setBackgroundDrawable((Drawable) bd.getObject());
        return bd;
    }

    public void SetBackgroundImage(Bitmap Bitmap) {
        SetBackgroundImageNew(Bitmap);
    }

    public void Invalidate() {
        ((View) getObject()).invalidate();
    }

    public void Invalidate2(Rect Rect) {
        ((View) getObject()).invalidate(Rect);
    }

    public void Invalidate3(int Left, int Top, int Right, int Bottom) {
        ((View) getObject()).invalidate(Left, Top, Right, Bottom);
    }

    public void setWidth(@Pixel int width) {
        getLayoutParams().width = width;
        requestLayout();
    }

    public int getWidth() {
        return getLayoutParams().width;
    }

    public int getHeight() {
        return getLayoutParams().height;
    }

    public int getLeft() {
        return ((LayoutParams) getLayoutParams()).left;
    }

    public int getTop() {
        return ((LayoutParams) getLayoutParams()).top;
    }

    public void setHeight(@Pixel int height) {
        getLayoutParams().height = height;
        requestLayout();
    }

    public void setLeft(@Pixel int left) {
        ((LayoutParams) getLayoutParams()).left = left;
        requestLayout();
    }

    public void setTop(@Pixel int top) {
        ((LayoutParams) getLayoutParams()).top = top;
        requestLayout();
    }

    private void requestLayout() {
        ViewParent parent = ((View) getObject()).getParent();
        if (parent != null) {
            parent.requestLayout();
        }
    }

    public void setPadding(int[] p) {
        ((View) getObject()).setPadding(p[0], p[1], p[2], p[3]);
    }

    public int[] getPadding() {
        return new int[]{((View) getObject()).getPaddingLeft(), ((View) getObject()).getPaddingTop(), ((View) getObject()).getPaddingRight(), ((View) getObject()).getPaddingBottom()};
    }

    public void setColor(int color) {
        Drawable d = ((View) getObject()).getBackground();
        if (d == null || !(d instanceof GradientDrawable)) {
            ((View) getObject()).setBackgroundColor(color);
        } else if (!(d instanceof GradientDrawableWithCorners) || ((GradientDrawableWithCorners) d).borderWidth == 0) {
            ColorDrawable cd = new ColorDrawable();
            cd.Initialize(color, (int) findRadius());
            ((View) getObject()).setBackgroundDrawable((Drawable) cd.getObject());
        } else {
            ((GradientDrawableWithCorners) d).setColor(color);
            ((View) getObject()).invalidate();
            ((View) getObject()).requestLayout();
        }
    }

    private float findRadius() {
        float radius = Common.Density;
        Drawable d = ((View) getObject()).getBackground();
        if (d == null || !(d instanceof GradientDrawable)) {
            return radius;
        }
        if (d instanceof GradientDrawableWithCorners) {
            return ((GradientDrawableWithCorners) d).cornerRadius;
        }
        GradientDrawable g = (GradientDrawable) ((View) getObject()).getBackground();
        try {
            Field state = g.getClass().getDeclaredField("mGradientState");
            state.setAccessible(true);
            Object gstate = state.get(g);
            return ((Float) gstate.getClass().getDeclaredField("mRadius").get(gstate)).floatValue();
        } catch (Exception e) {
            e.printStackTrace();
            return radius;
        }
    }

    public void setTag(Object tag) {
        ((View) getObject()).setTag(tag);
    }

    public Object getTag() {
        return ((View) getObject()).getTag();
    }

    public Object getParent() {
        return ((View) getObject()).getParent();
    }

    public void setVisible(boolean Visible) {
        ((View) getObject()).setVisibility(Visible ? 0 : 8);
    }

    public boolean getVisible() {
        return ((View) getObject()).getVisibility() == 0;
    }

    public void setEnabled(boolean Enabled) {
        ((View) getObject()).setEnabled(Enabled);
    }

    public boolean getEnabled() {
        return ((View) getObject()).isEnabled();
    }

    public void BringToFront() {
        if (((View) getObject()).getParent() instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) ((View) getObject()).getParent();
            vg.removeView((View) getObject());
            vg.addView((View) getObject());
        }
    }

    public void SendToBack() {
        if (((View) getObject()).getParent() instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) ((View) getObject()).getParent();
            vg.removeView((View) getObject());
            vg.addView((View) getObject(), 0);
        }
    }

    public void RemoveView() {
        if (((View) getObject()).getParent() instanceof ViewGroup) {
            ((ViewGroup) ((View) getObject()).getParent()).removeView((View) getObject());
        }
    }

    public void SetLayout(@Pixel int Left, @Pixel int Top, @Pixel int Width, @Pixel int Height) {
        LayoutParams lp = (LayoutParams) getLayoutParams();
        lp.left = Left;
        lp.top = Top;
        lp.width = Width;
        lp.height = Height;
        if (((View) getObject()).getParent() != null) {
            ((View) getObject()).getParent().requestLayout();
        }
    }

    private ViewGroup.LayoutParams getLayoutParams() {
        ViewGroup.LayoutParams lp = ((View) getObject()).getLayoutParams();
        if (lp != null) {
            return lp;
        }
        lp = new LayoutParams(0, 0, 0, 0);
        ((View) getObject()).setLayoutParams(lp);
        return lp;
    }

    public void SetLayoutAnimated(int Duration, @Pixel int Left, @Pixel int Top, @Pixel int Width, @Pixel int Height) {
        LayoutParams lp = (LayoutParams) ((View) getObject()).getLayoutParams();
        if (lp == null) {
            SetLayout(Left, Top, Width, Height);
            return;
        }
        int pLeft = lp.left;
        int pTop = lp.top;
        int pWidth = lp.width;
        int pHeight = lp.height;
        SetLayout(Left, Top, Width, Height);
        AnimateFrom((View) getObject(), Duration, pLeft, pTop, pWidth, pHeight);
    }

    public void SetColorAnimated(int Duration, int FromColor, int ToColor) {
        if (VERSION.SDK_INT < 11 || Duration <= 0) {
            setColor(ToColor);
            return;
        }
        GradientDrawableWithCorners gdc;
        final View target = (View) getObject();
        if (target.getBackground() instanceof GradientDrawableWithCorners) {
            gdc = (GradientDrawableWithCorners) target.getBackground();
        } else {
            gdc = new GradientDrawableWithCorners();
        }
        gdc.setColor(FromColor);
        target.setBackgroundDrawable(gdc);
        final float[] from = new float[3];
        final float[] to = new float[3];
        Color.colorToHSV(FromColor, from);
        Color.colorToHSV(ToColor, to);
        ValueAnimator anim = ValueAnimator.ofFloat(new float[]{Common.Density, 1.0f});
        anim.setDuration((long) Duration);
        final float[] hsv = new float[3];
        final int fromAlpha = Color.alpha(FromColor);
        final int toAlpha = Color.alpha(ToColor);
        anim.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                hsv[0] = from[0] + ((to[0] - from[0]) * animation.getAnimatedFraction());
                hsv[1] = from[1] + ((to[1] - from[1]) * animation.getAnimatedFraction());
                hsv[2] = from[2] + ((to[2] - from[2]) * animation.getAnimatedFraction());
                gdc.setColor(Color.HSVToColor((int) (((float) fromAlpha) + (((float) (toAlpha - fromAlpha)) * animation.getAnimatedFraction())), hsv));
                target.invalidate();
            }
        });
        anim.start();
    }

    public void SetVisibleAnimated(int Duration, final boolean Visible) {
        if (Visible != getVisible()) {
            if (VERSION.SDK_INT < 11 || Duration <= 0) {
                setVisible(Visible);
                return;
            }
            ObjectAnimator oa;
            final View target = (View) getObject();
            if (Visible) {
                oa = ObjectAnimator.ofFloat(target, "alpha", new float[]{Common.Density, 1.0f});
            } else {
                oa = ObjectAnimator.ofFloat(target, "alpha", new float[]{1.0f, Common.Density});
            }
            oa.addListener(new AnimatorListener() {
                public void onAnimationCancel(Animator animation) {
                }

                public void onAnimationEnd(Animator animation) {
                    if (!Visible) {
                        target.setVisibility(8);
                    }
                    target.setAlpha(1.0f);
                }

                public void onAnimationRepeat(Animator animation) {
                }

                public void onAnimationStart(Animator animation) {
                }
            });
            oa.setDuration((long) Duration).start();
            if (Visible) {
                target.setVisibility(0);
            }
        }
    }

    @Hide
    public static void AnimateFrom(View target, int Duration, int pLeft, int pTop, int pWidth, int pHeight) {
        LayoutParams lp = (LayoutParams) target.getLayoutParams();
        if (VERSION.SDK_INT >= 11 && Duration > 0 && pWidth >= 0 && pHeight >= 0) {
            target.setPivotX(Common.Density);
            target.setPivotY(Common.Density);
            WeakReference<AnimatorSet> wr = (WeakReference) AbsObjectWrapper.getExtraTags(target).get("prevSet");
            AnimatorSet prevSet = wr != null ? (AnimatorSet) wr.get() : null;
            if (prevSet != null && prevSet.isRunning()) {
                prevSet.end();
            }
            AnimatorSet set = new AnimatorSet();
            AbsObjectWrapper.getExtraTags(target).put("prevSet", new WeakReference(set));
            r6 = new Animator[4];
            r6[0] = ObjectAnimator.ofFloat(target, "translationX", new float[]{(float) (pLeft - lp.left), Common.Density});
            r6[1] = ObjectAnimator.ofFloat(target, "translationY", new float[]{(float) (pTop - lp.top), Common.Density});
            r6[2] = ObjectAnimator.ofFloat(target, "scaleX", new float[]{((float) pWidth) / ((float) lp.width), 1.0f});
            r6[3] = ObjectAnimator.ofFloat(target, "scaleY", new float[]{((float) pHeight) / ((float) lp.height), 1.0f});
            set.playTogether(r6);
            set.setDuration((long) Duration);
            set.start();
        }
    }

    public boolean RequestFocus() {
        return ((View) getObject()).requestFocus();
    }

    @Hide
    public String toString() {
        String s = baseToString();
        if (!IsInitialized()) {
            return s;
        }
        s = new StringBuilder(String.valueOf(s)).append(": ").toString();
        if (!getEnabled()) {
            s = new StringBuilder(String.valueOf(s)).append("Enabled=false, ").toString();
        }
        if (!getVisible()) {
            s = new StringBuilder(String.valueOf(s)).append("Visible=false, ").toString();
        }
        if (((View) getObject()).getLayoutParams() == null || !(((View) getObject()).getLayoutParams() instanceof LayoutParams)) {
            s = new StringBuilder(String.valueOf(s)).append("Layout not available").toString();
        } else {
            s = new StringBuilder(String.valueOf(s)).append("Left=").append(getLeft()).append(", Top=").append(getTop()).append(", Width=").append(getWidth()).append(", Height=").append(getHeight()).toString();
        }
        if (getTag() != null) {
            return new StringBuilder(String.valueOf(s)).append(", Tag=").append(getTag().toString()).toString();
        }
        return s;
    }

    @Hide
    public static View build(Object prev, Map<String, Object> props, boolean designer) throws Exception {
        View v = (View) prev;
        if (v.getTag() == null && designer) {
            HashMap<String, Object> defaults = new HashMap();
            defaults.put("background", v.getBackground());
            defaults.put("padding", new int[]{v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom()});
            v.setTag(defaults);
        }
        LayoutParams lp = (LayoutParams) v.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams();
            v.setLayoutParams(lp);
        }
        lp.setFromUserPlane(((Integer) props.get("left")).intValue(), ((Integer) props.get("top")).intValue(), ((Integer) props.get("width")).intValue(), ((Integer) props.get("height")).intValue());
        v.setEnabled(((Boolean) props.get("enabled")).booleanValue());
        if (!designer) {
            int visible = 0;
            if (!((Boolean) props.get("visible")).booleanValue()) {
                visible = 8;
            }
            v.setVisibility(visible);
            v.setTag(props.get("tag"));
        }
        int[] padding = (int[]) props.get("padding");
        if (padding != null) {
            v.setPadding(Math.round(BALayout.getDeviceScale() * ((float) padding[0])), Math.round(BALayout.getDeviceScale() * ((float) padding[1])), Math.round(BALayout.getDeviceScale() * ((float) padding[2])), Math.round(BALayout.getDeviceScale() * ((float) padding[3])));
        } else if (designer) {
            int[] defaultPadding = (int[]) getDefault(v, "padding", null);
            v.setPadding(defaultPadding[0], defaultPadding[1], defaultPadding[2], defaultPadding[3]);
        }
        return v;
    }

    @Hide
    public static void fixAnchor(int pw, int ph, ViewWrapperAndAnchor vwa) {
        if (vwa.hanchor == ViewWrapperAndAnchor.RIGHT) {
            vwa.right = vwa.vw.getLeft();
            vwa.vw.setLeft((pw - vwa.right) - vwa.vw.getWidth());
        } else if (vwa.hanchor == ViewWrapperAndAnchor.BOTH) {
            vwa.right = vwa.vw.getWidth();
            vwa.vw.setWidth((pw - vwa.right) - vwa.vw.getLeft());
        }
        if (vwa.vanchor == ViewWrapperAndAnchor.BOTTOM) {
            vwa.bottom = vwa.vw.getTop();
            vwa.vw.setTop((ph - vwa.bottom) - vwa.vw.getHeight());
        } else if (vwa.vanchor == ViewWrapperAndAnchor.BOTH) {
            vwa.bottom = vwa.vw.getHeight();
            vwa.vw.setHeight((ph - vwa.bottom) - vwa.vw.getTop());
        }
    }

    @Hide
    public Object[] debug(int limit, boolean[] outShouldAddReflectionFields) {
        Object[] res = new Object[]{"ToString", toString()};
        outShouldAddReflectionFields[0] = true;
        return res;
    }

    @Hide
    public static <T> T buildNativeView(Context context, Class<T> cls, HashMap<String, Object> props, boolean designer) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        Class<?> c;
        String overideClass = (String) props.get("nativeClass");
        if (overideClass != null && overideClass.startsWith(".")) {
            overideClass = new StringBuilder(String.valueOf(BA.applicationContext.getPackageName())).append(overideClass).toString();
        }
        if (!(designer || overideClass == null)) {
            try {
                if (overideClass.length() != 0) {
                    c = Class.forName(overideClass);
                    return c.getConstructor(new Class[]{Context.class}).newInstance(new Object[]{context});
                }
            } catch (ClassNotFoundException e) {
                int i = overideClass.lastIndexOf(".");
                c = Class.forName(overideClass.substring(0, i) + "$" + overideClass.substring(i + 1));
            }
        }
        c = cls;
        return c.getConstructor(new Class[]{Context.class}).newInstance(new Object[]{context});
    }

    @Hide
    public static Object getDefault(View v, String key, Object defaultValue) {
        HashMap<String, Object> map = (HashMap) v.getTag();
        if (map.containsKey(key)) {
            return map.get(key);
        }
        map.put(key, defaultValue);
        return defaultValue;
    }
}
