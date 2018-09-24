package anywheresoftware.b4a.objects;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan.Standard;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;
import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.B4aDebuggable;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.Pixel;
import anywheresoftware.b4a.BA.ShortName;
import java.util.Iterator;
import java.util.LinkedList;

@ShortName("CSBuilder")
public class CSBuilder extends AbsObjectWrapper<SpannableStringBuilder> implements B4aDebuggable {

    /* renamed from: anywheresoftware.b4a.objects.CSBuilder$1 */
    class C00281 extends SpannableStringBuilder {
        C00281() {
        }

        public int hashCode() {
            return System.identityHashCode(this);
        }

        public boolean equals(Object o) {
            return this == o;
        }
    }

    @Hide
    public static class CustomTypefaceSpan extends MetricAffectingSpan {
        private final Typeface typeface;

        public CustomTypefaceSpan(Typeface typeface) {
            this.typeface = typeface;
        }

        public void updateDrawState(TextPaint drawState) {
            apply(drawState);
        }

        public void updateMeasureState(TextPaint paint) {
            apply(paint);
        }

        private void apply(Paint paint) {
            Typeface oldTypeface = paint.getTypeface();
            int fakeStyle = (oldTypeface != null ? oldTypeface.getStyle() : 0) & (this.typeface.getStyle() ^ -1);
            if ((fakeStyle & 1) != 0) {
                paint.setFakeBoldText(true);
            }
            if ((fakeStyle & 2) != 0) {
                paint.setTextSkewX(-0.25f);
            }
            paint.setTypeface(this.typeface);
        }
    }

    @Hide
    public static class SpanMark {
        public int markEnd = -1;
        public final int markStart;
        public final Object span;

        public SpanMark(Object span, int markStart) {
            this.span = span;
            this.markStart = markStart;
        }

        public String toString() {
            return this.span.getClass() + " " + this.markStart + " -> " + this.markEnd;
        }
    }

    @Hide
    public static class VerticalAlignedSpan extends MetricAffectingSpan {
        int shift;

        public VerticalAlignedSpan(int shift) {
            this.shift = shift;
        }

        public void updateDrawState(TextPaint tp) {
            tp.baselineShift += this.shift;
        }

        public void updateMeasureState(TextPaint tp) {
            tp.baselineShift += this.shift;
        }
    }

    private LinkedList<SpanMark> spanOpenings() {
        return (LinkedList) AbsObjectWrapper.getExtraTags(getObject()).get("marks");
    }

    public CSBuilder Initialize() {
        setObject(new C00281());
        AbsObjectWrapper.getExtraTags(getObject()).put("marks", new LinkedList());
        return this;
    }

    public CSBuilder Append(CharSequence Text) {
        ((SpannableStringBuilder) getObject()).append(Text);
        return this;
    }

    public CSBuilder Underline() {
        return open(new UnderlineSpan());
    }

    public CSBuilder Clickable(final BA ba, String EventName, final Object Tag) {
        final String eventName = EventName.toLowerCase(BA.cul) + "_click";
        return open(new ClickableSpan() {
            public void onClick(View widget) {
                ba.raiseEventFromUI(CSBuilder.this.getObject(), eventName, Tag);
            }

            public void updateDrawState(TextPaint ds) {
            }
        });
    }

    public CSBuilder Alignment(Alignment Alignment) {
        return open(new Standard(Alignment));
    }

    public CSBuilder Bold() {
        return open(new StyleSpan(1));
    }

    @Hide
    public CSBuilder open(Object span) {
        spanOpenings().add(new SpanMark(span, ((SpannableStringBuilder) getObject()).length()));
        return this;
    }

    public CSBuilder Pop() {
        LinkedList<SpanMark> marks = spanOpenings();
        SpanMark sm = (SpanMark) marks.removeLast();
        sm.markEnd = ((SpannableStringBuilder) getObject()).length();
        marks.addFirst(sm);
        if (((SpanMark) marks.getLast()).markEnd != -1) {
            Iterator it = marks.iterator();
            while (it.hasNext()) {
                SpanMark sm2 = (SpanMark) it.next();
                ((SpannableStringBuilder) getObject()).setSpan(sm2.span, sm2.markStart, sm2.markEnd, 0);
            }
            marks.clear();
        }
        return this;
    }

    public CSBuilder PopAll() {
        LinkedList<SpanMark> marks = spanOpenings();
        while (marks.size() > 0) {
            Pop();
        }
        return this;
    }

    public CSBuilder Color(int Color) {
        return open(new ForegroundColorSpan(Color));
    }

    public CSBuilder BackgroundColor(int Color) {
        return open(new BackgroundColorSpan(Color));
    }

    public CSBuilder Size(int Size) {
        return open(new AbsoluteSizeSpan(Size, true));
    }

    public CSBuilder RelativeSize(float Proportion) {
        return open(new RelativeSizeSpan(Proportion));
    }

    public CSBuilder Typeface(Typeface Typeface) {
        return open(new CustomTypefaceSpan(Typeface));
    }

    public CSBuilder Strikethrough() {
        return open(new StrikethroughSpan());
    }

    public CSBuilder VerticalAlign(@Pixel int Shift) {
        return open(new VerticalAlignedSpan(Shift));
    }

    public CSBuilder Image(Bitmap Bitmap, @Pixel int Width, @Pixel int Height, boolean Baseline) {
        int i = 0;
        BitmapDrawable bd = new BitmapDrawable(BA.applicationContext.getResources(), Bitmap);
        bd.setBounds(0, 0, Width, Height);
        if (Baseline) {
            i = 1;
        }
        return open(new ImageSpan(bd, i)).Append("_").Pop();
    }

    public CSBuilder ScaleX(float Proportion) {
        return open(new ScaleXSpan(Proportion));
    }

    public void EnableClickEvents(TextView Label) {
        Label.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public int getLength() {
        return ((SpannableStringBuilder) getObject()).length();
    }

    public String ToString() {
        return ((SpannableStringBuilder) getObject()).toString();
    }

    @Hide
    public String toString() {
        return ToString();
    }

    @Hide
    public Object[] debug(int limit, boolean[] outShouldAddReflectionFields) {
        Object[] res = new Object[]{"Length", Integer.valueOf(getLength()), "ToString", ToString()};
        outShouldAddReflectionFields[0] = true;
        return res;
    }
}
