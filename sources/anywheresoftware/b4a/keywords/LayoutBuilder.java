package anywheresoftware.b4a.keywords;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.ConnectorUtils;
import anywheresoftware.b4a.DynamicBuilder;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import anywheresoftware.b4a.objects.CustomViewWrapper;
import anywheresoftware.b4a.objects.ViewWrapper;
import anywheresoftware.b4a.objects.streams.File;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Hide
public class LayoutBuilder {
    private static double autoscale;
    private static HashMap<String, WeakReference<MapAndCachedStrings>> cachedLayouts = new HashMap();
    private static LayoutValues chosen;
    private static HashMap<String, Field> classFields;
    private static String currentClass;
    private static List<CustomViewWrapper> customViewWrappers;
    private static double screenSize = 0.0d;
    private static BA tempBA;
    private static HashMap<String, Object> viewsToSendInShellMode;

    @Hide
    public interface DesignerTextSizeMethod {
        float getTextSize();

        void setTextSize(float f);
    }

    @Hide
    public static class LayoutHashMap<K, V> extends LinkedHashMap<K, V> {
        public V get(Object key) {
            V v = super.get(key);
            if (v != null) {
                return v;
            }
            throw new RuntimeException("Cannot find view: " + key.toString() + "\nAll views in script should be declared.");
        }
    }

    public static class LayoutValuesAndMap {
        public final LayoutValues layoutValues;
        public final LinkedHashMap<String, ViewWrapperAndAnchor> map;

        public LayoutValuesAndMap(LayoutValues layoutValues, LinkedHashMap<String, ViewWrapperAndAnchor> map) {
            this.layoutValues = layoutValues;
            this.map = map;
        }
    }

    private static class MapAndCachedStrings {
        public final String[] cachedStrings;
        public final HashMap<String, Object> map;

        public MapAndCachedStrings(HashMap<String, Object> map, String[] cachedStrings) {
            this.map = map;
            this.cachedStrings = cachedStrings;
        }
    }

    @Hide
    public static class ViewWrapperAndAnchor {
        public static int BOTH = 2;
        public static int BOTTOM = 1;
        public static int LEFT = 0;
        public static int RIGHT = 1;
        public static int TOP = 0;
        public int bottom;
        public int hanchor;
        public final View parent;
        public int ph;
        public int pw;
        public int right;
        public int vanchor;
        public final ViewWrapper<?> vw;

        public ViewWrapperAndAnchor(ViewWrapper<?> vw, View parent) {
            this.vw = vw;
            this.parent = parent;
        }
    }

    public static LayoutValuesAndMap loadLayout(String file, BA ba, boolean isActivity, ViewGroup parent, LinkedHashMap<String, ViewWrapperAndAnchor> dynamicTable, boolean d4a) throws IOException {
        IOException e;
        Throwable th;
        Throwable e2;
        try {
            int i;
            int mainWidth;
            int mainHeight;
            tempBA = ba;
            if (!d4a) {
                file = file.toLowerCase(BA.cul);
            }
            if (!file.endsWith(".bal")) {
                file = new StringBuilder(String.valueOf(file)).append(".bal").toString();
            }
            MapAndCachedStrings mcs = null;
            WeakReference<MapAndCachedStrings> cl = (WeakReference) cachedLayouts.get(file);
            if (cl != null) {
                mcs = (MapAndCachedStrings) cl.get();
            }
            DataInputStream dataInputStream = new DataInputStream((InputStream) File.OpenInput(File.getDirAssets(), file).getObject());
            int version = ConnectorUtils.readInt(dataInputStream);
            for (int pos = ConnectorUtils.readInt(dataInputStream); pos > 0; pos = (int) (((long) pos) - dataInputStream.skip((long) pos))) {
            }
            String[] cache = null;
            if (version >= 3) {
                if (mcs != null) {
                    cache = mcs.cachedStrings;
                    ConnectorUtils.readInt(dataInputStream);
                    for (i = 0; i < cache.length; i++) {
                        dataInputStream.skipBytes(ConnectorUtils.readInt(dataInputStream));
                    }
                } else {
                    cache = new String[ConnectorUtils.readInt(dataInputStream)];
                    for (i = 0; i < cache.length; i++) {
                        cache[i] = ConnectorUtils.readString(dataInputStream);
                    }
                }
            }
            int numberOfVariants = ConnectorUtils.readInt(dataInputStream);
            chosen = null;
            LayoutValues device = Common.GetDeviceLayoutValues(ba);
            int variantIndex = 0;
            float distance = Float.MAX_VALUE;
            for (i = 0; i < numberOfVariants; i++) {
                LayoutValues test = LayoutValues.readFromStream(dataInputStream);
                if (chosen == null) {
                    chosen = test;
                    distance = test.calcDistance(device);
                    variantIndex = i;
                } else {
                    float testDistance = test.calcDistance(device);
                    if (testDistance < distance) {
                        chosen = test;
                        distance = testDistance;
                        variantIndex = i;
                    }
                }
            }
            BALayout.setUserScale(chosen.Scale);
            if (isActivity || parent.getLayoutParams() == null) {
                mainWidth = ba.vg.getWidth();
                mainHeight = ba.vg.getHeight();
            } else {
                mainWidth = parent.getLayoutParams().width;
                mainHeight = parent.getLayoutParams().height;
            }
            int animationDuration = 0;
            if (dynamicTable == null) {
                HashMap<String, Object> props;
                Object obj;
                LinkedHashMap<String, ViewWrapperAndAnchor> dynamicTable2 = new LayoutHashMap();
                if (mcs != null) {
                    try {
                        props = mcs.map;
                    } catch (IOException e3) {
                        e = e3;
                        dynamicTable = dynamicTable2;
                        try {
                            throw e;
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    } catch (Exception e4) {
                        e2 = e4;
                        dynamicTable = dynamicTable2;
                        throw new RuntimeException(e2);
                    } catch (Throwable th3) {
                        th = th3;
                        dynamicTable = dynamicTable2;
                        tempBA = null;
                        customViewWrappers = null;
                        throw th;
                    }
                }
                props = ConnectorUtils.readMap(dataInputStream, cache);
                cachedLayouts.put(file, new WeakReference(new MapAndCachedStrings(props, cache)));
                if (ba.eventsTarget == null) {
                    obj = ba.activity;
                } else {
                    obj = ba.eventsTarget;
                }
                loadLayoutHelper(props, ba, obj, parent, isActivity, "variant" + variantIndex, true, dynamicTable2, mainWidth, mainHeight);
                if (BA.isShellModeRuntimeCheck(ba) && viewsToSendInShellMode != null) {
                    BA ba2 = ba;
                    ba2.raiseEvent2(null, true, "SEND_VIEWS_AFTER_LAYOUT", true, viewsToSendInShellMode);
                    viewsToSendInShellMode = null;
                }
                animationDuration = ((Integer) BA.gm(props, "animationDuration", Integer.valueOf(0))).intValue();
                dynamicTable = dynamicTable2;
            }
            dataInputStream.close();
            runScripts(file, chosen, dynamicTable, mainWidth, mainHeight, Common.Density, d4a);
            BALayout.setUserScale(1.0f);
            if (customViewWrappers != null) {
                for (CustomViewWrapper cvw : customViewWrappers) {
                    cvw.AfterDesignerScript();
                }
            }
            animateLayout(dynamicTable, parent, mainWidth, mainHeight, animationDuration);
            LayoutValuesAndMap layoutValuesAndMap = new LayoutValuesAndMap(chosen, dynamicTable);
            tempBA = null;
            customViewWrappers = null;
            return layoutValuesAndMap;
        } catch (IOException e5) {
            e = e5;
        } catch (Exception e6) {
            e2 = e6;
        }
    }

    private static void animateLayout(LinkedHashMap<String, ViewWrapperAndAnchor> views, View parent, int parentWidth, int parentHeight, int duration) {
        if (duration > 0) {
            for (ViewWrapperAndAnchor vwa : views.values()) {
                if (vwa.parent == parent) {
                    int pl = 0;
                    int pt = 0;
                    if (vwa.hanchor == ViewWrapperAndAnchor.RIGHT) {
                        pl = parentWidth;
                    } else if (vwa.hanchor == ViewWrapperAndAnchor.BOTH) {
                        pl = parentWidth / 2;
                    }
                    if (vwa.vanchor == ViewWrapperAndAnchor.BOTTOM) {
                        pt = parentHeight;
                    } else if (vwa.vanchor == ViewWrapperAndAnchor.BOTH) {
                        pt = parentHeight / 2;
                    }
                    ViewWrapper.AnimateFrom((View) vwa.vw.getObject(), duration, pl, pt, 0, 0);
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void runScripts(java.lang.String r10, anywheresoftware.b4a.keywords.LayoutValues r11, java.util.LinkedHashMap<java.lang.String, anywheresoftware.b4a.keywords.LayoutBuilder.ViewWrapperAndAnchor> r12, int r13, int r14, float r15, boolean r16) throws java.lang.IllegalArgumentException, java.lang.IllegalAccessException {
        /*
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "LS_";
        r5.append(r6);
        r3 = 0;
    L_0x000b:
        r6 = r10.length();
        r6 = r6 + -4;
        if (r3 < r6) goto L_0x00b2;
    L_0x0013:
        r6 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r7 = anywheresoftware.b4a.BA.packageName;	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r7 = java.lang.String.valueOf(r7);	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r6.<init>(r7);	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r7 = ".designerscripts.";
        r6 = r6.append(r7);	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r7 = r5.toString();	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r6 = r6.append(r7);	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r6 = r6.toString();	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r1 = java.lang.Class.forName(r6);	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r6 = 0;
        r6 = variantToMethod(r6);	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
        r7 = 4;
        r7 = new java.lang.Class[r7];	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
        r8 = 0;
        r9 = java.util.LinkedHashMap.class;
        r7[r8] = r9;	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
        r8 = 1;
        r9 = java.lang.Integer.TYPE;	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
        r7[r8] = r9;	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
        r8 = 2;
        r9 = java.lang.Integer.TYPE;	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
        r7[r8] = r9;	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
        r8 = 3;
        r9 = java.lang.Float.TYPE;	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
        r7[r8] = r9;	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
        r4 = r1.getMethod(r6, r7);	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
        r6 = 0;
        r7 = 4;
        r7 = new java.lang.Object[r7];	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
        r8 = 0;
        r7[r8] = r12;	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
        r8 = 1;
        r9 = java.lang.Integer.valueOf(r13);	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
        r7[r8] = r9;	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
        r8 = 2;
        r9 = java.lang.Integer.valueOf(r14);	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
        r7[r8] = r9;	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
        r8 = 3;
        r9 = java.lang.Float.valueOf(r15);	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
        r7[r8] = r9;	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
        r4.invoke(r6, r7);	 Catch:{ NoSuchMethodException -> 0x00dd, ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, InvocationTargetException -> 0x00ce }
    L_0x0073:
        r6 = variantToMethod(r11);	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r7 = 4;
        r7 = new java.lang.Class[r7];	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r8 = 0;
        r9 = java.util.LinkedHashMap.class;
        r7[r8] = r9;	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r8 = 1;
        r9 = java.lang.Integer.TYPE;	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r7[r8] = r9;	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r8 = 2;
        r9 = java.lang.Integer.TYPE;	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r7[r8] = r9;	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r8 = 3;
        r9 = java.lang.Float.TYPE;	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r7[r8] = r9;	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r4 = r1.getMethod(r6, r7);	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r6 = 0;
        r7 = 4;
        r7 = new java.lang.Object[r7];	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r8 = 0;
        r7[r8] = r12;	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r8 = 1;
        r9 = java.lang.Integer.valueOf(r13);	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r7[r8] = r9;	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r8 = 2;
        r9 = java.lang.Integer.valueOf(r14);	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r7[r8] = r9;	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r8 = 3;
        r9 = java.lang.Float.valueOf(r15);	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r7[r8] = r9;	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
        r4.invoke(r6, r7);	 Catch:{ ClassNotFoundException -> 0x00db, SecurityException -> 0x00c9, NoSuchMethodException -> 0x00d9, InvocationTargetException -> 0x00ce }
    L_0x00b1:
        return;
    L_0x00b2:
        r0 = r10.charAt(r3);
        r6 = java.lang.Character.isLetterOrDigit(r0);
        if (r6 == 0) goto L_0x00c3;
    L_0x00bc:
        r5.append(r0);
    L_0x00bf:
        r3 = r3 + 1;
        goto L_0x000b;
    L_0x00c3:
        r6 = "_";
        r5.append(r6);
        goto L_0x00bf;
    L_0x00c9:
        r2 = move-exception;
        r2.printStackTrace();
        goto L_0x00b1;
    L_0x00ce:
        r2 = move-exception;
        r6 = new java.lang.RuntimeException;
        r7 = r2.getCause();
        r6.<init>(r7);
        throw r6;
    L_0x00d9:
        r6 = move-exception;
        goto L_0x00b1;
    L_0x00db:
        r6 = move-exception;
        goto L_0x00b1;
    L_0x00dd:
        r6 = move-exception;
        goto L_0x0073;
        */
        throw new UnsupportedOperationException("Method not decompiled: anywheresoftware.b4a.keywords.LayoutBuilder.runScripts(java.lang.String, anywheresoftware.b4a.keywords.LayoutValues, java.util.LinkedHashMap, int, int, float, boolean):void");
    }

    public static void setScaleRate(double rate) {
        double deviceSize = (double) (((float) (tempBA.vg.getWidth() + tempBA.vg.getHeight())) / Common.Density);
        double variantSize = (double) (((float) ((chosen.Width + chosen.Height) - 25)) / chosen.Scale);
        double deviceToLayout = deviceSize / variantSize;
        if (System.getProperty("autoscaleall_old_behaviour", "false").equals("true")) {
            autoscale = 1.0d + (rate * ((double) ((((float) (tempBA.vg.getWidth() + tempBA.vg.getHeight())) / (750.0f * Common.Density)) - 1.0f)));
        } else if (deviceToLayout <= 0.95d || deviceToLayout >= 1.05d) {
            double vscale = 1.0d + (rate * ((variantSize / 750.0d) - 1.0d));
            autoscale = (1.0d + (rate * ((deviceSize / 750.0d) - 1.0d))) / vscale;
        } else {
            autoscale = 1.0d;
        }
        screenSize = 0.0d;
    }

    public static double getScreenSize() {
        if (screenSize == 0.0d) {
            screenSize = (Math.sqrt(Math.pow((double) tempBA.vg.getWidth(), 2.0d) + Math.pow((double) tempBA.vg.getHeight(), 2.0d)) / 160.0d) / ((double) Common.Density);
        }
        return screenSize;
    }

    public static boolean isPortrait() {
        return tempBA.vg.getHeight() >= tempBA.vg.getWidth();
    }

    public static void scaleAll(LinkedHashMap<String, ViewWrapperAndAnchor> views) {
        for (ViewWrapperAndAnchor vwa : views.values()) {
            if (vwa.vw.IsInitialized() && !(vwa.vw instanceof ActivityWrapper)) {
                scaleView(vwa);
            }
        }
    }

    public static void scaleView(ViewWrapperAndAnchor vwa) {
        int newLeft;
        int newWidth;
        int newTop;
        int newHeight;
        ViewWrapper<?> v = vwa.vw;
        int left = v.getLeft();
        int width = v.getWidth();
        int height = v.getHeight();
        int top = v.getTop();
        int pw = (vwa.parent == null || vwa.parent.getLayoutParams() == null) ? vwa.pw : vwa.parent.getLayoutParams().width;
        int ph = (vwa.parent == null || vwa.parent.getLayoutParams() == null) ? vwa.ph : vwa.parent.getLayoutParams().height;
        int right = vwa.right;
        int bottom = vwa.bottom;
        if (vwa.hanchor == ViewWrapperAndAnchor.LEFT) {
            newLeft = (int) ((((double) left) * autoscale) + 0.5d);
            newWidth = ((int) ((((double) (left + width)) * autoscale) + 0.5d)) - newLeft;
        } else {
            if (vwa.hanchor == ViewWrapperAndAnchor.RIGHT) {
                int newRight = (int) ((((double) right) * autoscale) + 0.5d);
                newWidth = ((int) ((((double) (right + width)) * autoscale) + 0.5d)) - newRight;
                newLeft = (pw - newRight) - newWidth;
            } else {
                newLeft = (int) ((((double) left) * autoscale) + 0.5d);
                newWidth = (pw - ((int) ((((double) right) * autoscale) + 0.5d))) - newLeft;
            }
        }
        v.setLeft(newLeft);
        v.setWidth(newWidth);
        if (vwa.vanchor == ViewWrapperAndAnchor.TOP) {
            newTop = (int) ((((double) top) * autoscale) + 0.5d);
            newHeight = ((int) ((((double) (top + height)) * autoscale) + 0.5d)) - newTop;
        } else {
            if (vwa.vanchor == ViewWrapperAndAnchor.BOTTOM) {
                int newBottom = (int) ((((double) bottom) * autoscale) + 0.5d);
                newHeight = ((int) ((((double) (bottom + height)) * autoscale) + 0.5d)) - newBottom;
                newTop = (ph - newBottom) - newHeight;
            } else {
                newTop = (int) ((((double) top) * autoscale) + 0.5d);
                newHeight = (ph - newTop) - ((int) ((((double) bottom) * autoscale) + 0.5d));
            }
        }
        v.setTop(newTop);
        v.setHeight(newHeight);
        if (v instanceof DesignerTextSizeMethod) {
            DesignerTextSizeMethod t = (DesignerTextSizeMethod) v;
            t.setTextSize((float) (((double) t.getTextSize()) * autoscale));
        }
    }

    private static String variantToMethod(LayoutValues lv) {
        String variant;
        if (lv == null) {
            variant = "general";
        } else {
            variant = new StringBuilder(String.valueOf(String.valueOf(lv.Width))).append("x").append(String.valueOf(lv.Height)).append("_").append(BA.NumberToString(lv.Scale).replace(".", "_")).toString();
        }
        return "LS_" + variant;
    }

    private static void loadLayoutHelper(HashMap<String, Object> props, BA ba, Object fieldsTarget, ViewGroup parent, boolean isActivity, String currentVariant, boolean firstCall, HashMap<String, ViewWrapperAndAnchor> dynamicTable, int parentWidth, int parentHeight) throws Exception {
        View o;
        HashMap<String, Object> variant = (HashMap) props.get(currentVariant);
        if (isActivity || !firstCall) {
            ViewGroup act = isActivity ? parent : null;
            props.put("left", variant.get("left"));
            props.put("top", variant.get("top"));
            props.put("width", variant.get("width"));
            props.put("height", variant.get("height"));
            o = (View) DynamicBuilder.build(act, props, false, parent.getContext());
            if (!isActivity) {
                View view;
                String name = ((String) props.get("name")).toLowerCase(BA.cul);
                String cls = (String) props.get("type");
                if (cls.startsWith(".")) {
                    cls = "anywheresoftware.b4a.objects" + cls;
                }
                ViewWrapper ow = (ViewWrapper) Class.forName(cls).newInstance();
                if (isActivity) {
                    view = null;
                } else {
                    view = parent;
                }
                ViewWrapperAndAnchor viewWrapperAndAnchor = new ViewWrapperAndAnchor(ow, view);
                if (variant.containsKey("hanchor")) {
                    viewWrapperAndAnchor.hanchor = ((Integer) variant.get("hanchor")).intValue();
                    viewWrapperAndAnchor.vanchor = ((Integer) variant.get("vanchor")).intValue();
                }
                viewWrapperAndAnchor.pw = parentWidth;
                viewWrapperAndAnchor.ph = parentHeight;
                dynamicTable.put(name, viewWrapperAndAnchor);
                if (classFields == null || currentClass != ba.className) {
                    classFields = new HashMap();
                    currentClass = ba.className;
                    for (Field field : Class.forName(ba.className).getDeclaredFields()) {
                        if (field.getName().startsWith("_")) {
                            classFields.put(field.getName(), field);
                        }
                    }
                }
                Field field2 = (Field) classFields.get("_" + name);
                Object assigningObject = ow;
                if (ow instanceof CustomViewWrapper) {
                    if (customViewWrappers == null) {
                        customViewWrappers = new ArrayList();
                    }
                    customViewWrappers.add((CustomViewWrapper) ow);
                    String cclass = (String) props.get("customType");
                    if (cclass == null || cclass.length() == 0) {
                        throw new RuntimeException("CustomView CustomType property was not set.");
                    }
                    Class<?> customClass;
                    try {
                        customClass = Class.forName(cclass);
                    } catch (ClassNotFoundException cnfe) {
                        int dollar = cclass.lastIndexOf(".");
                        if (dollar > -1) {
                            String corrected = BA.packageName + cclass.substring(dollar);
                            BA.LogInfo("Class not found: " + cclass + ", trying: " + corrected);
                            customClass = Class.forName(corrected);
                        } else {
                            throw cnfe;
                        }
                    }
                    Object customObject = customClass.newInstance();
                    CustomViewWrapper cvw = (CustomViewWrapper) ow;
                    cvw.customObject = customObject;
                    cvw.props = new HashMap(props);
                    assigningObject = customObject;
                } else if (!(field2 == null || field2.getType() == assigningObject.getClass())) {
                    if (BA.debugMode) {
                        Type t = ow.getClass().getGenericSuperclass();
                        if (t instanceof ParameterizedType) {
                            ParameterizedType pt = (ParameterizedType) t;
                            if (pt.getActualTypeArguments().length > 0 && !((Class) ((ParameterizedType) field2.getType().getGenericSuperclass()).getActualTypeArguments()[0]).isAssignableFrom((Class) pt.getActualTypeArguments()[0])) {
                                throw new RuntimeException("Cannot convert: " + ow.getClass() + ", to: " + field2.getType());
                            }
                        }
                    }
                    ObjectWrapper nw = (ObjectWrapper) field2.getType().newInstance();
                    nw.setObject(o);
                    ObjectWrapper assigningObject2 = nw;
                }
                if (BA.isShellModeRuntimeCheck(ba)) {
                    if (viewsToSendInShellMode == null) {
                        viewsToSendInShellMode = new HashMap();
                    }
                    viewsToSendInShellMode.put(name, assigningObject);
                }
                if (field2 != null) {
                    try {
                        field2.set(fieldsTarget, assigningObject);
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Field " + name + " was declared with the wrong type.");
                    }
                }
                ow.setObject(o);
                ow.innerInitialize(ba, ((String) props.get("eventName")).toLowerCase(BA.cul), true);
                parent.addView(o, o.getLayoutParams());
                if (!(viewWrapperAndAnchor.hanchor == 0 && viewWrapperAndAnchor.vanchor == 0)) {
                    ViewWrapper.fixAnchor(parentWidth, parentHeight, viewWrapperAndAnchor);
                }
            }
        } else {
            o = parent;
            parent.setBackgroundDrawable((Drawable) DynamicBuilder.build(parent, (HashMap) props.get("drawable"), false, null));
        }
        HashMap<String, Object> kids = (HashMap) props.get(":kids");
        if (kids != null) {
            int pw = o.getLayoutParams() == null ? 0 : o.getLayoutParams().width;
            int ph = o.getLayoutParams() == null ? 0 : o.getLayoutParams().height;
            for (int i = 0; i < kids.size(); i++) {
                loadLayoutHelper((HashMap) kids.get(String.valueOf(i)), ba, fieldsTarget, (ViewGroup) o, false, currentVariant, false, dynamicTable, pw, ph);
            }
        }
    }
}
