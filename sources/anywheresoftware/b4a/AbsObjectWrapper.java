package anywheresoftware.b4a;

import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.ShortName;
import java.util.HashMap;
import java.util.WeakHashMap;

public class AbsObjectWrapper<T> implements ObjectWrapper<T> {
    @Hide
    public static boolean Activity_LoadLayout_Was_Called = false;
    private static final WeakHashMap<Object, HashMap<String, Object>> extraMap = new WeakHashMap();
    private T object;

    public boolean IsInitialized() {
        return this.object != null;
    }

    @Hide
    public T getObjectOrNull() {
        return this.object;
    }

    @Hide
    public T getObject() {
        if (this.object != null) {
            return this.object;
        }
        ShortName typeName = (ShortName) getClass().getAnnotation(ShortName.class);
        String msg = "Object should first be initialized";
        if (typeName == null) {
            msg = new StringBuilder(String.valueOf(msg)).append(".").toString();
        } else {
            msg = new StringBuilder(String.valueOf(msg)).append(" (").append(typeName.value()).append(").").toString();
        }
        try {
            if (Class.forName("anywheresoftware.b4a.objects.ViewWrapper").isInstance(this) && !Activity_LoadLayout_Was_Called) {
                msg = new StringBuilder(String.valueOf(msg)).append("\nDid you forget to call Activity.LoadLayout?").toString();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new RuntimeException(msg);
    }

    @Hide
    public static HashMap<String, Object> getExtraTags(Object me) {
        HashMap<String, Object> map = (HashMap) extraMap.get(me);
        if (map != null) {
            return map;
        }
        map = new HashMap();
        extraMap.put(me, map);
        return map;
    }

    @Hide
    public void setObject(T object) {
        this.object = object;
    }

    @Hide
    public int hashCode() {
        return this.object == null ? 0 : this.object.hashCode();
    }

    @Hide
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            if (this.object != null) {
                return false;
            }
            return true;
        } else if (obj instanceof AbsObjectWrapper) {
            AbsObjectWrapper<?> other = (AbsObjectWrapper) obj;
            if (this.object != null) {
                return this.object.equals(other.object);
            }
            if (other.object != null) {
                return false;
            }
            return true;
        } else if (this.object == null) {
            return false;
        } else {
            return this.object.equals(obj);
        }
    }

    @Hide
    public String baseToString() {
        String type;
        if (this.object != null) {
            type = this.object.getClass().getSimpleName();
        } else {
            ShortName typeName = (ShortName) getClass().getAnnotation(ShortName.class);
            if (typeName != null) {
                type = typeName.value();
            } else {
                type = getClass().getSimpleName();
            }
        }
        int i = type.lastIndexOf(".");
        if (i > -1) {
            type = type.substring(i + 1);
        }
        String s = "(" + type + ")";
        if (this.object == null) {
            return new StringBuilder(String.valueOf(s)).append(" Not initialized").toString();
        }
        return s;
    }

    @Hide
    public String toString() {
        String s = baseToString();
        return this.object == null ? s : new StringBuilder(String.valueOf(s)).append(" ").append(this.object.toString()).toString();
    }

    @Hide
    public static ObjectWrapper ConvertToWrapper(ObjectWrapper ow, Object o) {
        ow.setObject(o);
        return ow;
    }
}
