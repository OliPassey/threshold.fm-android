package anywheresoftware.b4a.objects;

import android.view.View;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.ActivityObject;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.ShortName;

@ShortName("View")
@ActivityObject
public class ConcreteViewWrapper extends ViewWrapper<View> {
    @Hide
    public void Initialize(BA ba, String eventName) {
        throw new RuntimeException("Cannot initialize object.");
    }
}
