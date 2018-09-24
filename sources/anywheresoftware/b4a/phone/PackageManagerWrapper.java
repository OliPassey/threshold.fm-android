package anywheresoftware.b4a.phone;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.objects.IntentWrapper;
import anywheresoftware.b4a.objects.collections.List;

@ShortName("PackageManager")
public class PackageManagerWrapper {
    private PackageManager pm = BA.applicationContext.getPackageManager();

    public List GetInstalledPackages() {
        List l = new List();
        java.util.List<PackageInfo> list = this.pm.getInstalledPackages(0);
        l.Initialize();
        for (PackageInfo pi : list) {
            l.Add(pi.packageName);
        }
        return l;
    }

    public int GetVersionCode(String Package) throws NameNotFoundException {
        return this.pm.getPackageInfo(Package, 0).versionCode;
    }

    public String GetVersionName(String Package) throws NameNotFoundException {
        return this.pm.getPackageInfo(Package, 0).versionName;
    }

    public String GetApplicationLabel(String Package) throws NameNotFoundException {
        CharSequence cs = this.pm.getApplicationLabel(this.pm.getApplicationInfo(Package, 0));
        return cs == null ? "" : cs.toString();
    }

    public IntentWrapper GetApplicationIntent(String Package) {
        IntentWrapper iw = new IntentWrapper();
        iw.setObject(this.pm.getLaunchIntentForPackage(Package));
        return iw;
    }

    public Drawable GetApplicationIcon(String Package) throws NameNotFoundException {
        return this.pm.getApplicationIcon(Package);
    }

    public List QueryIntentActivities(Intent Intent) {
        java.util.List<ResolveInfo> res = this.pm.queryIntentActivities(Intent, 0);
        List l = new List();
        l.Initialize();
        for (ResolveInfo ri : res) {
            l.Add(new ComponentName(ri.activityInfo.packageName, ri.activityInfo.name).flattenToShortString());
        }
        return l;
    }
}
