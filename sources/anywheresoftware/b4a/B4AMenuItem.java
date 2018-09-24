package anywheresoftware.b4a;

import android.graphics.drawable.Drawable;

public class B4AMenuItem {
    public final boolean addToBar;
    public final Drawable drawable;
    public final String eventName;
    public final CharSequence title;

    public B4AMenuItem(CharSequence title, Drawable drawable, String eventName, boolean addToBar) {
        this.title = title;
        this.drawable = drawable;
        this.eventName = eventName;
        this.addToBar = addToBar;
    }
}
