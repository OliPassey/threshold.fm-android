package anywheresoftware.b4a.objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.BALayout.LayoutParams;
import anywheresoftware.b4a.keywords.Common;
import anywheresoftware.b4a.keywords.constants.Colors;
import java.util.ArrayList;

@Hide
public class SimpleListAdapter extends BaseAdapter implements ListAdapter {
    private static final int SINGLE_LINE_ITEM = 0;
    private static final int TWO_LINES_AND_BITMAP_ITEM = 2;
    private static final int TWO_LINES_ITEM = 1;
    public SingleLineLayout SingleLine;
    public TwoLinesLayout TwoLines;
    public TwoLinesAndBitmapLayout TwoLinesAndBitmap;
    public BALayout dummyParent;
    public ArrayList<SimpleItem> items = new ArrayList();
    private ItemLayout[] layouts;

    public interface ItemLayout {
        int getItemHeight();
    }

    public interface SimpleItem {
        void addNewToLayout(ViewGroup viewGroup, SimpleListAdapter simpleListAdapter);

        Object getReturnValue();

        int getType();

        void updateExisting(ViewGroup viewGroup, SimpleListAdapter simpleListAdapter);
    }

    @Hide
    public static class SingleLineData implements SimpleItem {
        public Object ReturnValue;
        public CharSequence Text;

        @Hide
        public void addNewToLayout(ViewGroup layout, SimpleListAdapter adapter) {
            layout.setBackgroundDrawable(adapter.SingleLine.Background);
            addNewToLayoutImpl(layout, (TextView) adapter.SingleLine.Label.getObject());
        }

        protected void addNewToLayoutImpl(ViewGroup layout, TextView model) {
            TextView tv = new TextView(layout.getContext());
            layout.addView(tv, model.getLayoutParams());
            tv.setBackgroundDrawable(model.getBackground());
            tv.setTextSize(model.getTextSize() / layout.getContext().getResources().getDisplayMetrics().scaledDensity);
            tv.setTextColor(model.getTextColors());
            tv.setGravity(model.getGravity());
            tv.setVisibility(model.getVisibility());
            tv.setTypeface(model.getTypeface());
        }

        @Hide
        public void updateExisting(ViewGroup layout, SimpleListAdapter adapter) {
            ((TextView) layout.getChildAt(0)).setText(this.Text);
        }

        @Hide
        public int getType() {
            return 0;
        }

        @Hide
        public Object getReturnValue() {
            return this.ReturnValue == null ? String.valueOf(this.Text) : this.ReturnValue;
        }
    }

    public static class SingleLineLayout implements ItemLayout {
        public Drawable Background;
        public LabelWrapper Label;
        protected int itemHeight;

        private SingleLineLayout(ViewGroup dummyParent, boolean dontSetLayout) {
            TextView t = new TextView(dummyParent.getContext());
            this.Label = new LabelWrapper();
            this.Label.setObject(t);
            if (!dontSetLayout) {
                t.setTextSize(22.5f);
                t.setTextColor(-1);
                this.itemHeight = Common.DipToCurrent(50);
                t.setGravity(16);
                dummyParent.addView(t, new LayoutParams(Common.DipToCurrent(5), 0, -1, -1));
            }
        }

        public int getItemHeight() {
            return this.itemHeight;
        }

        public void setItemHeight(int Height) {
            this.itemHeight = Height;
        }
    }

    @Hide
    public static class TwoLinesData extends SingleLineData {
        public CharSequence SecondLineText;

        @Hide
        public void addNewToLayout(ViewGroup layout, SimpleListAdapter adapter) {
            layout.setBackgroundDrawable(adapter.TwoLines.Background);
            super.addNewToLayoutImpl(layout, (TextView) adapter.TwoLines.Label.getObject());
            super.addNewToLayoutImpl(layout, (TextView) adapter.TwoLines.SecondLabel.getObject());
        }

        @Hide
        public void updateExisting(ViewGroup layout, SimpleListAdapter adapter) {
            super.updateExisting(layout, adapter);
            ((TextView) layout.getChildAt(1)).setText(this.SecondLineText);
        }

        @Hide
        public int getType() {
            return 1;
        }
    }

    @Hide
    public static class TwoLinesAndBitmapData extends TwoLinesData {
        public Bitmap Bitmap;

        @Hide
        public void addNewToLayout(ViewGroup layout, SimpleListAdapter adapter) {
            layout.setBackgroundDrawable(adapter.TwoLinesAndBitmap.Background);
            super.addNewToLayoutImpl(layout, (TextView) adapter.TwoLinesAndBitmap.Label.getObject());
            super.addNewToLayoutImpl(layout, (TextView) adapter.TwoLinesAndBitmap.SecondLabel.getObject());
            ImageView iv = new ImageView(layout.getContext());
            ImageView model = (ImageView) adapter.TwoLinesAndBitmap.ImageView.getObject();
            layout.addView(iv, model.getLayoutParams());
            iv.setVisibility(model.getVisibility());
        }

        @Hide
        public void updateExisting(ViewGroup layout, SimpleListAdapter adapter) {
            super.updateExisting(layout, adapter);
            ImageView iv = (ImageView) layout.getChildAt(2);
            iv.setVisibility(this.Bitmap != null ? 0 : 8);
            if (this.Bitmap != null) {
                iv.setBackgroundDrawable(new BitmapDrawable(this.Bitmap));
            }
        }

        @Hide
        public int getType() {
            return 2;
        }
    }

    public static class TwoLinesLayout extends SingleLineLayout {
        public LabelWrapper SecondLabel;

        private TwoLinesLayout(ViewGroup dummyParent, boolean dontSetLayout) {
            super(dummyParent, true);
            TextView t2 = new TextView(dummyParent.getContext());
            this.SecondLabel = new LabelWrapper();
            this.SecondLabel.setObject(t2);
            if (!dontSetLayout) {
                TextView t = (TextView) this.Label.getObject();
                t.setTextSize(19.5f);
                t2.setTextSize(16.5f);
                this.itemHeight = Common.DipToCurrent(60);
                t.setGravity(80);
                t2.setGravity(48);
                t.setTextColor(-1);
                t2.setTextColor(Colors.Gray);
                dummyParent.addView(t, new LayoutParams(Common.DipToCurrent(5), 0, -1, Common.DipToCurrent(30)));
                dummyParent.addView(t2, new LayoutParams(Common.DipToCurrent(5), Common.DipToCurrent(32), -1, Common.DipToCurrent(28)));
            }
        }
    }

    public static class TwoLinesAndBitmapLayout extends TwoLinesLayout {
        public ImageViewWrapper ImageView;

        private TwoLinesAndBitmapLayout(ViewGroup dummyParent) {
            super(dummyParent, true);
            TextView t = (TextView) this.Label.getObject();
            TextView t2 = (TextView) this.SecondLabel.getObject();
            t.setTextSize(19.5f);
            t2.setTextSize(16.5f);
            this.itemHeight = Common.DipToCurrent(60);
            t.setGravity(80);
            t2.setGravity(48);
            t.setTextColor(-1);
            t2.setTextColor(Colors.Gray);
            dummyParent.addView(t, new LayoutParams(Common.DipToCurrent(60), 0, -1, Common.DipToCurrent(30)));
            dummyParent.addView(t2, new LayoutParams(Common.DipToCurrent(60), Common.DipToCurrent(32), -1, Common.DipToCurrent(28)));
            ImageView iv = new ImageView(dummyParent.getContext());
            this.ImageView = new ImageViewWrapper();
            this.ImageView.setObject(iv);
            dummyParent.addView(iv, new LayoutParams(Common.DipToCurrent(5), Common.DipToCurrent(5), Common.DipToCurrent(50), Common.DipToCurrent(50)));
        }
    }

    public SimpleListAdapter(Context context) {
        this.dummyParent = new BALayout(context);
        this.SingleLine = new SingleLineLayout(this.dummyParent, false);
        this.TwoLines = new TwoLinesLayout(this.dummyParent, false);
        this.TwoLinesAndBitmap = new TwoLinesAndBitmapLayout(this.dummyParent);
        this.layouts = new ItemLayout[3];
        this.layouts[0] = this.SingleLine;
        this.layouts[1] = this.TwoLines;
        this.layouts[2] = this.TwoLinesAndBitmap;
    }

    public int getItemViewType(int position) {
        return ((SimpleItem) this.items.get(position)).getType();
    }

    public int getViewTypeCount() {
        return 3;
    }

    public int getCount() {
        return this.items.size();
    }

    public Object getItem(int position) {
        return ((SimpleItem) this.items.get(position)).getReturnValue();
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleItem item = (SimpleItem) this.items.get(position);
        if (convertView == null) {
            View bl = new BALayout(parent.getContext());
            bl.setLayoutParams(new AbsListView.LayoutParams(-1, this.layouts[item.getType()].getItemHeight()));
            item.addNewToLayout(bl, this);
            convertView = bl;
        }
        item.updateExisting((ViewGroup) convertView, this);
        return convertView;
    }
}
