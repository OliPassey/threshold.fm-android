package anywheresoftware.b4a.keywords;

import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper;

public class B4AApplication {
    public static String getLabelName() throws NameNotFoundException {
        return String.valueOf(BA.applicationContext.getPackageManager().getApplicationLabel(BA.applicationContext.getPackageManager().getApplicationInfo(BA.packageName, 0)));
    }

    public static String getVersionName() throws NameNotFoundException {
        return BA.applicationContext.getPackageManager().getPackageInfo(BA.packageName, 0).versionName;
    }

    public static int getVersionCode() throws NameNotFoundException {
        return BA.applicationContext.getPackageManager().getPackageInfo(BA.packageName, 0).versionCode;
    }

    public static String getPackageName() {
        return BA.packageName;
    }

    public static BitmapWrapper getIcon() throws NameNotFoundException {
        BitmapWrapper bw = new BitmapWrapper();
        Drawable d = BA.applicationContext.getPackageManager().getApplicationIcon(BA.packageName);
        if (d instanceof BitmapDrawable) {
            bw.setObject(((BitmapDrawable) d).getBitmap());
        }
        return bw;
    }
}
