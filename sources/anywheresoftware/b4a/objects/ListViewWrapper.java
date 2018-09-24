package anywheresoftware.b4a.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.ActivityObject;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.DynamicBuilder;
import anywheresoftware.b4a.objects.SimpleListAdapter.SimpleItem;
import anywheresoftware.b4a.objects.SimpleListAdapter.SingleLineData;
import anywheresoftware.b4a.objects.SimpleListAdapter.SingleLineLayout;
import anywheresoftware.b4a.objects.SimpleListAdapter.TwoLinesAndBitmapData;
import anywheresoftware.b4a.objects.SimpleListAdapter.TwoLinesAndBitmapLayout;
import anywheresoftware.b4a.objects.SimpleListAdapter.TwoLinesData;
import anywheresoftware.b4a.objects.SimpleListAdapter.TwoLinesLayout;
import java.util.HashMap;

@ActivityObject
@ShortName("ListView")
public class ListViewWrapper extends ViewWrapper<SimpleListView> {

    @Hide
    public static class SimpleListView extends ListView {
        public SimpleListAdapter adapter;

        public SimpleListView(Context context) {
            super(context);
            this.adapter = new SimpleListAdapter(context);
            setAdapter(this.adapter);
        }
    }

    @Hide
    public void innerInitialize(final BA ba, final String eventName, boolean keepOldObject) {
        if (!keepOldObject) {
            setObject(new SimpleListView(ba.context));
        }
        super.innerInitialize(ba, eventName, true);
        if (ba.subExists(new StringBuilder(String.valueOf(eventName)).append("_itemclick").toString())) {
            ((SimpleListView) getObject()).setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    ba.raiseEventFromUI(ListViewWrapper.this.getObject(), eventName + "_itemclick", Integer.valueOf(position), ((SimpleListView) ListViewWrapper.this.getObject()).adapter.getItem(position));
                }
            });
        }
        if (ba.subExists(new StringBuilder(String.valueOf(eventName)).append("_itemlongclick").toString())) {
            ((SimpleListView) getObject()).setOnItemLongClickListener(new OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                    ba.raiseEventFromUI(ListViewWrapper.this.getObject(), eventName + "_itemlongclick", Integer.valueOf(position), ((SimpleListView) ListViewWrapper.this.getObject()).adapter.getItem(position));
                    return true;
                }
            });
        }
    }

    public int getSize() {
        return ((SimpleListView) getObject()).adapter.getCount();
    }

    public SingleLineLayout getSingleLineLayout() {
        return ((SimpleListView) getObject()).adapter.SingleLine;
    }

    public TwoLinesLayout getTwoLinesLayout() {
        return ((SimpleListView) getObject()).adapter.TwoLines;
    }

    public TwoLinesAndBitmapLayout getTwoLinesAndBitmap() {
        return ((SimpleListView) getObject()).adapter.TwoLinesAndBitmap;
    }

    public void AddSingleLine(CharSequence Text) {
        AddSingleLine2(Text, null);
    }

    public void AddSingleLine2(CharSequence Text, Object ReturnValue) {
        SingleLineData sl = new SingleLineData();
        sl.Text = Text;
        sl.ReturnValue = ReturnValue;
        add(sl);
    }

    public void AddTwoLines(CharSequence Text1, CharSequence Text2) {
        AddTwoLines2(Text1, Text2, null);
    }

    public void AddTwoLines2(CharSequence Text1, CharSequence Text2, Object ReturnValue) {
        TwoLinesData t = new TwoLinesData();
        t.Text = Text1;
        t.ReturnValue = ReturnValue;
        t.SecondLineText = Text2;
        add(t);
    }

    public void AddTwoLinesAndBitmap(CharSequence Text1, CharSequence Text2, Bitmap Bitmap) {
        AddTwoLinesAndBitmap2(Text1, Text2, Bitmap, null);
    }

    public void AddTwoLinesAndBitmap2(CharSequence Text1, CharSequence Text2, Bitmap Bitmap, Object ReturnValue) {
        TwoLinesAndBitmapData t = new TwoLinesAndBitmapData();
        t.Text = Text1;
        t.ReturnValue = ReturnValue;
        t.SecondLineText = Text2;
        t.Bitmap = Bitmap;
        add(t);
    }

    @Hide
    public void add(SimpleItem si) {
        ((SimpleListView) getObject()).adapter.items.add(si);
        ((SimpleListView) getObject()).adapter.notifyDataSetChanged();
    }

    public Object GetItem(int Index) {
        return ((SimpleListView) getObject()).adapter.getItem(Index);
    }

    public void RemoveAt(int Index) {
        ((SimpleListView) getObject()).adapter.items.remove(Index);
        ((SimpleListView) getObject()).adapter.notifyDataSetChanged();
    }

    public void Clear() {
        ((SimpleListView) getObject()).adapter.items.clear();
        ((SimpleListView) getObject()).adapter.notifyDataSetChanged();
    }

    public void setFastScrollEnabled(boolean Enabled) {
        ((SimpleListView) getObject()).setFastScrollEnabled(Enabled);
    }

    public boolean getFastScrollEnabled() {
        return ((SimpleListView) getObject()).isFastScrollEnabled();
    }

    public void setScrollingBackgroundColor(int Color) {
        ((SimpleListView) getObject()).setCacheColorHint(Color);
    }

    public void SetSelection(int Position) {
        ((SimpleListView) getObject()).setSelection(Position);
    }

    @Hide
    public static View build(Object prev, HashMap<String, Object> props, boolean designer, Object tag) throws Exception {
        if (prev == null) {
            prev = ViewWrapper.buildNativeView((Context) tag, SimpleListView.class, props, designer);
        }
        ListView list = (ListView) ViewWrapper.build(prev, props, designer);
        Drawable d = (Drawable) DynamicBuilder.build(list, (HashMap) props.get("drawable"), designer, null);
        if (d != null) {
            list.setBackgroundDrawable(d);
        }
        list.setFastScrollEnabled(((Boolean) props.get("fastScrollEnabled")).booleanValue());
        if (designer) {
            SimpleListView slv = (SimpleListView) list;
            if (slv.adapter.items.size() == 0) {
                for (int i = 1; i <= 10; i++) {
                    SingleLineData s = new SingleLineData();
                    s.Text = "Item #" + i;
                    slv.adapter.items.add(s);
                }
                slv.adapter.notifyDataSetChanged();
            }
        }
        return list;
    }
}
