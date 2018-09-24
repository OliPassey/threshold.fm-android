package anywheresoftware.b4a.objects;

import anywheresoftware.b4a.BA.DesignerName;

public abstract class String2 {
    @DesignerName("CharAt")
    public abstract char charAt(int i);

    @DesignerName("CompareTo")
    public abstract int compareTo(String str);

    @DesignerName("Contains")
    public abstract boolean contains(String str);

    @DesignerName("EndsWith")
    public abstract boolean endsWith(String str);

    @DesignerName("EqualsIgnoreCase")
    public abstract boolean equalsIgnoreCase(String str);

    @DesignerName("GetBytes")
    public abstract byte[] getBytes(String str);

    @DesignerName("IndexOf")
    public abstract int indexOf(String str);

    @DesignerName("IndexOf2")
    public abstract int indexOf(String str, int i);

    @DesignerName("LastIndexOf")
    public abstract int lastIndexOf(String str);

    @DesignerName("LastIndexOf2")
    public abstract int lastIndexOf(String str, int i);

    @DesignerName("Length")
    public abstract int length();

    @DesignerName("Replace")
    public abstract String replace(String str, String str2);

    @DesignerName("StartsWith")
    public abstract boolean startsWith(String str);

    @DesignerName("SubString")
    public abstract String substring(int i);

    @DesignerName("SubString2")
    public abstract String substring(int i, int i2);

    @DesignerName("ToLowerCase")
    public abstract String toLowerCase();

    @DesignerName("ToUpperCase")
    public abstract String toUpperCase();

    @DesignerName("Trim")
    public abstract String trim();
}
