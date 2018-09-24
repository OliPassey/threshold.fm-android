package anywheresoftware.b4a.objects.drawable;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.ActivityObject;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper;
import anywheresoftware.b4a.objects.streams.File;
import java.io.IOException;
import java.util.HashMap;

@ShortName("BitmapDrawable")
@ActivityObject
public class BitmapDrawable extends AbsObjectWrapper<android.graphics.drawable.BitmapDrawable> {
    public void Initialize(Bitmap Bitmap) {
        setObject(new android.graphics.drawable.BitmapDrawable(BA.applicationContext.getResources(), Bitmap));
    }

    public Bitmap getBitmap() {
        return ((android.graphics.drawable.BitmapDrawable) getObject()).getBitmap();
    }

    public int getGravity() {
        return ((android.graphics.drawable.BitmapDrawable) getObject()).getGravity();
    }

    public void setGravity(int value) {
        ((android.graphics.drawable.BitmapDrawable) getObject()).setGravity(value);
    }

    @Hide
    public static Drawable build(Object prev, HashMap<String, Object> d, boolean designer, Object tag) throws IOException {
        String file = ((String) d.get("file")).toLowerCase(BA.cul);
        if (file.length() == 0) {
            return null;
        }
        String Dir;
        if (designer) {
            Dir = File.getDirInternal();
        } else {
            Dir = File.getDirAssets();
        }
        BitmapDrawable bd = new BitmapDrawable();
        BitmapWrapper bw = new BitmapWrapper();
        bw.Initialize(Dir, file);
        bd.Initialize((Bitmap) bw.getObject());
        Integer gravity = (Integer) d.get("gravity");
        if (gravity != null) {
            ((android.graphics.drawable.BitmapDrawable) bd.getObject()).setGravity(gravity.intValue());
        }
        return (Drawable) bd.getObject();
    }
}
