package anywheresoftware.b4a;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import anywheresoftware.b4a.keywords.Common;
import java.util.ArrayList;
import java.util.HashMap;

public class BALayout extends ViewGroup {
    public static final int BOTH = 2;
    public static final int BOTTOM = 1;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int TOP = 0;
    private static float deviceScale = Common.Density;
    public static boolean disableAccessibility = false;
    public static float scale = Common.Density;

    public static class LayoutParams extends android.view.ViewGroup.LayoutParams {
        public int left;
        public int top;

        public LayoutParams(int left, int top, int width, int height) {
            super(width, height);
            this.left = left;
            this.top = top;
        }

        public LayoutParams() {
            super(0, 0);
        }

        public HashMap<String, Object> toDesignerMap() {
            HashMap<String, Object> props = new HashMap();
            props.put("left", Integer.valueOf(Math.round(((float) this.left) / BALayout.scale)));
            props.put("top", Integer.valueOf(Math.round(((float) this.top) / BALayout.scale)));
            props.put("width", Integer.valueOf(Math.round(((float) this.width) / BALayout.scale)));
            props.put("height", Integer.valueOf(Math.round(((float) this.height) / BALayout.scale)));
            return props;
        }

        public void setFromUserPlane(int left, int top, int width, int height) {
            this.left = Math.round(((float) left) * BALayout.scale);
            this.top = Math.round(((float) top) * BALayout.scale);
            if (width > 0) {
                width = Math.round(((float) width) * BALayout.scale);
            }
            this.width = width;
            if (height > 0) {
                height = Math.round(((float) height) * BALayout.scale);
            }
            this.height = height;
        }
    }

    public BALayout(Context context) {
        super(context);
    }

    public static void setDeviceScale(float scale) {
        deviceScale = scale;
    }

    public static void setUserScale(float userScale) {
        if (Float.compare(deviceScale, userScale) == 0) {
            scale = 1.0f;
        } else {
            scale = deviceScale / userScale;
        }
    }

    public static float getDeviceScale() {
        return deviceScale;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                if (child.getLayoutParams() instanceof LayoutParams) {
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    child.layout(lp.left, lp.top, lp.left + child.getMeasuredWidth(), lp.top + child.getMeasuredHeight());
                } else {
                    child.layout(0, 0, getLayoutParams().width, getLayoutParams().height);
                }
            }
        }
    }

    public void addChildrenForAccessibility(ArrayList<View> c) {
        if (!disableAccessibility) {
            super.addChildrenForAccessibility(c);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(resolveSize(getLayoutParams().width, widthMeasureSpec), resolveSize(getLayoutParams().height, heightMeasureSpec));
    }
}
