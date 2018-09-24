package anywheresoftware.b4a.objects.collections;

import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.BA.B4aDebuggable;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.IterableList;
import anywheresoftware.b4a.BA.ShortName;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

@ShortName("Map")
public class Map extends AbsObjectWrapper<MyMap> implements B4aDebuggable {

    /* renamed from: anywheresoftware.b4a.objects.collections.Map$1 */
    class C00591 implements IterableList {
        C00591() {
        }

        public Object Get(int index) {
            return Map.this.GetKeyAt(index);
        }

        public int getSize() {
            return Map.this.getSize();
        }
    }

    /* renamed from: anywheresoftware.b4a.objects.collections.Map$2 */
    class C00602 implements IterableList {
        C00602() {
        }

        public Object Get(int index) {
            return Map.this.GetValueAt(index);
        }

        public int getSize() {
            return Map.this.getSize();
        }
    }

    @Hide
    public static class MyMap implements java.util.Map<Object, Object> {
        private Entry<Object, Object> currentEntry;
        private LinkedHashMap<Object, Object> innerMap = new LinkedHashMap();
        private Iterator<Entry<Object, Object>> iterator;
        private int iteratorPosition;

        public Object getKey(int index) {
            return getEntry(index).getKey();
        }

        public Object getValue(int index) {
            return getEntry(index).getValue();
        }

        private Entry<Object, Object> getEntry(int index) {
            if (!(this.iterator == null || this.iteratorPosition == index)) {
                if (this.iteratorPosition == index - 1) {
                    this.currentEntry = (Entry) this.iterator.next();
                    this.iteratorPosition++;
                } else {
                    this.iterator = null;
                }
            }
            if (this.iterator == null) {
                this.iterator = this.innerMap.entrySet().iterator();
                for (int i = 0; i <= index; i++) {
                    this.currentEntry = (Entry) this.iterator.next();
                }
                this.iteratorPosition = index;
            }
            return this.currentEntry;
        }

        public void clear() {
            this.iterator = null;
            this.innerMap.clear();
        }

        public boolean containsKey(Object key) {
            return this.innerMap.containsKey(key);
        }

        public boolean containsValue(Object value) {
            return this.innerMap.containsValue(value);
        }

        public Set<Entry<Object, Object>> entrySet() {
            return this.innerMap.entrySet();
        }

        public Object get(Object key) {
            return this.innerMap.get(key);
        }

        public boolean isEmpty() {
            return this.innerMap.isEmpty();
        }

        public Set<Object> keySet() {
            return this.innerMap.keySet();
        }

        public Object put(Object key, Object value) {
            this.iterator = null;
            return this.innerMap.put(key, value);
        }

        public void putAll(java.util.Map<? extends Object, ? extends Object> m) {
            this.iterator = null;
            this.innerMap.putAll(m);
        }

        public Object remove(Object key) {
            this.iterator = null;
            return this.innerMap.remove(key);
        }

        public int size() {
            return this.innerMap.size();
        }

        public Collection<Object> values() {
            return this.innerMap.values();
        }

        public String toString() {
            return this.innerMap.toString();
        }
    }

    public void Initialize() {
        setObject(new MyMap());
    }

    public Object Put(Object Key, Object Value) {
        return ((MyMap) getObject()).put(Key, Value);
    }

    public Object Remove(Object Key) {
        return ((MyMap) getObject()).remove(Key);
    }

    public Object Get(Object Key) {
        return ((MyMap) getObject()).get(Key);
    }

    public Object GetDefault(Object Key, Object Default) {
        Object res = ((MyMap) getObject()).get(Key);
        return res == null ? Default : res;
    }

    public void Clear() {
        ((MyMap) getObject()).clear();
    }

    public Object GetKeyAt(int Index) {
        return ((MyMap) getObject()).getKey(Index);
    }

    public Object GetValueAt(int Index) {
        return ((MyMap) getObject()).getValue(Index);
    }

    public int getSize() {
        return ((MyMap) getObject()).size();
    }

    public boolean ContainsKey(Object Key) {
        return ((MyMap) getObject()).containsKey(Key);
    }

    public IterableList Keys() {
        return new C00591();
    }

    public IterableList Values() {
        return new C00602();
    }

    @Hide
    public Object[] debug(int limit, boolean[] outShouldAddReflectionFields) {
        Object[] res = new Object[((Math.min(getSize(), limit) + 1) * 2)];
        res[0] = "Size";
        res[1] = Integer.valueOf(getSize());
        int i = 2;
        for (Entry<Object, Object> e : ((MyMap) getObject()).entrySet()) {
            if (i >= res.length - 1) {
                break;
            }
            res[i] = String.valueOf(e.getKey());
            if (res[i].toString().length() == 0) {
                res[i] = "(empty string)";
            }
            res[i + 1] = e.getValue();
            i += 2;
        }
        outShouldAddReflectionFields[0] = false;
        return res;
    }
}
