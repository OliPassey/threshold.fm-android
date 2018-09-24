package anywheresoftware.b4a.objects.drawable;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;
import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.ActivityObject;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.keywords.constants.Colors;
import anywheresoftware.b4a.objects.ViewWrapper;
import java.util.HashMap;

@ActivityObject
@ShortName("ColorDrawable")
public class ColorDrawable extends AbsObjectWrapper<Drawable> {

    @Hide
    public static class GradientDrawableWithCorners extends GradientDrawable {
        public int borderColor;
        public int borderWidth;
        public float cornerRadius;

        public GradientDrawableWithCorners(Orientation o, int[] colors) {
            super(o, colors);
        }

        public void setCornerRadius(float radius) {
            super.setCornerRadius(radius);
            this.cornerRadius = radius;
        }

        public void setStroke(int borderWidth, int borderColor) {
            super.setStroke(borderWidth, borderColor);
            this.borderWidth = borderWidth;
            this.borderColor = borderColor;
        }
    }

    public void Initialize(int Color, int CornerRadius) {
        Initialize2(Color, CornerRadius, 0, 0);
    }

    public void Initialize2(int Color, int CornerRadius, int BorderWidth, int BorderColor) {
        GradientDrawableWithCorners gd = new GradientDrawableWithCorners();
        gd.setColor(Color);
        gd.setCornerRadius((float) CornerRadius);
        gd.setStroke(BorderWidth, BorderColor);
        setObject(gd);
    }

    @Hide
    public static Drawable build(Object prev, HashMap<String, Object> d, boolean designer, Object tag) {
        int alpha = ((Integer) d.get("alpha")).intValue();
        int solidColor = ((Integer) d.get("color")).intValue();
        if (solidColor == ViewWrapper.defaultColor) {
            if (!designer) {
                return null;
            }
            if (((Drawable) ViewWrapper.getDefault((View) prev, "background", null)) == null) {
                return new android.graphics.drawable.ColorDrawable(0);
            }
        }
        int color = (alpha << 24) | ((solidColor << 8) >>> 8);
        Integer corners = (Integer) d.get("cornerRadius");
        if (corners == null) {
            corners = Integer.valueOf(0);
        }
        ColorDrawable cd = new ColorDrawable();
        cd.Initialize2(color, (int) (BALayout.getDeviceScale() * ((float) corners.intValue())), (int) (BALayout.getDeviceScale() * ((float) ((Integer) BA.gm(d, "borderWidth", Integer.valueOf(0))).intValue())), ((Integer) BA.gm(d, "borderColor", Integer.valueOf(Colors.Black))).intValue());
        return (Drawable) cd.getObject();
    }
}
