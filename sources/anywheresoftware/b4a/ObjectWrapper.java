package anywheresoftware.b4a;

import anywheresoftware.b4a.BA.Hide;

public interface ObjectWrapper<T> {
    @Hide
    T getObject();

    @Hide
    T getObjectOrNull();

    @Hide
    void setObject(T t);
}
