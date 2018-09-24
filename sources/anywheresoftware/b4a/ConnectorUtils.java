package anywheresoftware.b4a;

import android.graphics.Color;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.HashMap;

public class ConnectorUtils {
    public static final byte BOOL = (byte) 5;
    public static final byte CACHED_STRING = (byte) 9;
    public static final byte COLOR = (byte) 6;
    public static final byte ENDOFMAP = (byte) 4;
    public static final byte FLOAT = (byte) 7;
    public static final byte INT = (byte) 1;
    public static final byte MAP = (byte) 3;
    public static final byte NULL = (byte) 12;
    public static final byte RECT32 = (byte) 11;
    public static final byte SCALED_INT = (byte) 8;
    public static final byte STRING = (byte) 2;
    private static Charset charset = Charset.forName("UTF8");
    private static ThreadLocal<ByteBuffer> myBb = new C00061();

    /* renamed from: anywheresoftware.b4a.ConnectorUtils$1 */
    class C00061 extends ThreadLocal<ByteBuffer> {
        C00061() {
        }

        protected ByteBuffer initialValue() {
            ByteBuffer bbW = ByteBuffer.allocate(51200);
            bbW.order(ByteOrder.LITTLE_ENDIAN);
            return bbW;
        }
    }

    public static ByteBuffer startMessage(byte message) {
        ByteBuffer bbW = (ByteBuffer) myBb.get();
        bbW.clear();
        bbW.put(message);
        return bbW;
    }

    public static void sendMessage(ConnectorConsumer consumer) {
        if (consumer != null) {
            byte[] b;
            ByteBuffer bbW = (ByteBuffer) myBb.get();
            bbW.flip();
            if (consumer.shouldAddPrefix()) {
                b = new byte[(bbW.limit() + 4)];
                int size = b.length - 4;
                bbW.get(b, 4, size);
                for (int i = 0; i <= 3; i++) {
                    b[i] = (byte) (size & 255);
                    size >>= 8;
                }
            } else {
                b = new byte[bbW.limit()];
                bbW.get(b);
            }
            consumer.putTask(b);
        }
    }

    public static void writeInt(int i) {
        ((ByteBuffer) myBb.get()).putInt(i);
    }

    public static void writeFloat(float f) {
        ((ByteBuffer) myBb.get()).putFloat(f);
    }

    public static void mark() {
        ((ByteBuffer) myBb.get()).mark();
    }

    public static void resetToMark() {
        ((ByteBuffer) myBb.get()).reset();
    }

    public static boolean writeString(String str) {
        if (str == null) {
            str = "";
        }
        if (str.length() > 700) {
            str = str.substring(0, 699) + "......";
        }
        ByteBuffer bbW = (ByteBuffer) myBb.get();
        int pos = bbW.position();
        ByteBuffer bb = charset.encode(str);
        if (bbW.remaining() - bb.remaining() < 1000) {
            return false;
        }
        bbW.putInt(0);
        bbW.put(bb);
        bbW.putInt(pos, (bbW.position() - pos) - 4);
        return true;
    }

    public static int readInt(DataInputStream in) throws IOException {
        return Integer.reverseBytes(in.readInt());
    }

    public static short readShort(DataInputStream in) throws IOException {
        return Short.reverseBytes(in.readShort());
    }

    public static String readString(DataInputStream in) throws IOException {
        int size = readInt(in);
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        in.readFully(bb.array());
        bb.limit(size);
        return charset.decode(bb).toString();
    }

    private static String readCacheString(DataInputStream din, String[] cache) throws IOException {
        if (cache == null) {
            return readString(din);
        }
        return cache[readInt(din)];
    }

    public static HashMap<String, Object> readMap(DataInputStream in, String[] cache) throws IOException {
        HashMap<String, Object> props = new HashMap();
        while (true) {
            Object value;
            String key = readCacheString(in, cache);
            byte b = in.readByte();
            if (b != (byte) 1) {
                if (b != (byte) 9) {
                    if (b != (byte) 2) {
                        if (b != (byte) 7) {
                            if (b != (byte) 3) {
                                if (b != (byte) 5) {
                                    if (b != (byte) 6) {
                                        if (b != NULL) {
                                            if (b != RECT32) {
                                                break;
                                            }
                                            value = new int[]{readShort(in), readShort(in), readShort(in), readShort(in)};
                                        } else {
                                            value = null;
                                        }
                                    } else {
                                        value = Integer.valueOf(Color.argb(in.readUnsignedByte(), in.readUnsignedByte(), in.readUnsignedByte(), in.readUnsignedByte()));
                                    }
                                } else {
                                    boolean z;
                                    if (in.readByte() == (byte) 1) {
                                        z = true;
                                    } else {
                                        z = false;
                                    }
                                    value = Boolean.valueOf(z);
                                }
                            } else {
                                value = readMap(in, cache);
                            }
                        } else {
                            value = Float.valueOf(Float.intBitsToFloat(readInt(in)));
                        }
                    } else {
                        value = readString(in);
                    }
                } else {
                    value = readCacheString(in, cache);
                }
            } else {
                value = Integer.valueOf(readInt(in));
            }
            props.put(key, value);
        }
        if (b == (byte) 4) {
            return props;
        }
        throw new RuntimeException("unknown type");
    }
}
