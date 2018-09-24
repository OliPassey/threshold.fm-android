package anywheresoftware.b4a.phone;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.objects.collections.List;
import anywheresoftware.b4a.objects.collections.Map;
import anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper;
import anywheresoftware.b4a.phone.ContactsWrapper.Contact;
import java.io.ByteArrayInputStream;
import java.util.HashMap;

@ShortName("Contacts2")
public class Contacts2Wrapper {
    private static final String[] people_projection = new String[]{"times_contacted", "last_time_contacted", "display_name", "has_phone_number", "starred", "_id", "photo_id"};
    private static final String[] phone_projection = new String[]{"is_primary", "data1", "contact_id"};

    protected static class Contact2 extends Contact {
        private int photoId;

        Contact2(String displayName, String phoneNumber, boolean starred, int id, String notes, int timesContacted, long lastTimeContacted, String name, int photoId) {
            super(displayName, phoneNumber, starred, id, notes, timesContacted, lastTimeContacted, name);
            this.photoId = photoId;
        }

        public BitmapWrapper GetPhoto() {
            Cursor photo = BA.applicationContext.getContentResolver().query(Data.CONTENT_URI, null, "_id=" + this.photoId, null, null);
            BitmapWrapper bw = null;
            if (photo.moveToNext()) {
                byte[] b = photo.getBlob(photo.getColumnIndex("data15"));
                if (b != null) {
                    bw = new BitmapWrapper();
                    bw.Initialize2(new ByteArrayInputStream(b));
                }
            }
            photo.close();
            return bw;
        }

        public Map GetEmails() {
            Cursor emails = BA.applicationContext.getContentResolver().query(Email.CONTENT_URI, new String[]{"data1", "data2"}, "contact_id = " + this.Id, null, null);
            Map m = new Map();
            m.Initialize();
            while (emails.moveToNext()) {
                m.Put(emails.getString(0), Integer.valueOf(emails.getInt(1)));
            }
            emails.close();
            return m;
        }

        public Map GetPhones() {
            Cursor phones = BA.applicationContext.getContentResolver().query(Phone.CONTENT_URI, new String[]{"data1", "data2"}, "contact_id = " + this.Id, null, null);
            Map m = new Map();
            m.Initialize();
            while (phones.moveToNext()) {
                m.Put(phones.getString(0), Integer.valueOf(phones.getInt(1)));
            }
            phones.close();
            return m;
        }
    }

    public List GetAll(boolean IncludePhoneNumber, boolean IncludeNotes) {
        return getAllContacts(null, null, IncludePhoneNumber, IncludeNotes);
    }

    public Contact GetById(int Id, boolean IncludePhoneNumber, boolean IncludeNotes) {
        List l = getAllContacts("_id = ?", new String[]{String.valueOf(Id)}, IncludePhoneNumber, IncludeNotes);
        if (l.getSize() == 0) {
            return null;
        }
        return (Contact) l.Get(0);
    }

    public List FindByMail(String Email, boolean Exact, boolean IncludePhoneNumber, boolean IncludeNotes) {
        String sel;
        String args;
        ContentResolver cr = BA.applicationContext.getContentResolver();
        if (Exact) {
            sel = " = ?";
            args = Email;
        } else {
            sel = " LIKE ?";
            args = "%" + Email + "%";
        }
        Cursor crsr = cr.query(Email.CONTENT_URI, new String[]{"contact_id"}, "data1" + sel, new String[]{args}, null);
        StringBuilder sb = new StringBuilder();
        while (crsr.moveToNext()) {
            for (int i = 0; i < crsr.getColumnCount(); i++) {
                sb.append(crsr.getString(0)).append(",");
            }
        }
        int count = crsr.getCount();
        crsr.close();
        if (count == 0) {
            List l = new List();
            l.Initialize();
            return l;
        }
        sb.setLength(sb.length() - 1);
        return getAllContacts("_id IN (" + sb.toString() + ")", null, IncludePhoneNumber, IncludeNotes);
    }

    public List FindByName(String Name, boolean Exact, boolean IncludePhoneNumber, boolean IncludeNotes) {
        if (Exact) {
            return getAllContacts("display_name = ?", new String[]{Name}, IncludePhoneNumber, IncludeNotes);
        }
        return getAllContacts("display_name LIKE ?", new String[]{"%" + Name + "%"}, IncludePhoneNumber, IncludeNotes);
    }

    public void GetContactsAsync(BA ba, String EventName, String Query, String[] Arguments, boolean IncludePhoneNumber, boolean IncludeNotes) {
        final String str = Query;
        final String[] strArr = Arguments;
        final boolean z = IncludePhoneNumber;
        final boolean z2 = IncludeNotes;
        final BA ba2 = ba;
        final String str2 = EventName;
        BA.submitRunnable(new Runnable() {
            public void run() {
                List res = Contacts2Wrapper.this.GetContactsByQuery(str, strArr, z, z2);
                ba2.raiseEventFromDifferentThread(this, this, 0, new StringBuilder(String.valueOf(str2.toLowerCase(BA.cul))).append("_complete").toString(), true, new Object[]{res});
            }
        }, this, 0);
    }

    public List GetContactsByQuery(String Query, String[] Arguments, boolean IncludePhoneNumber, boolean IncludeNotes) {
        if (Query.length() == 0) {
            Query = null;
        }
        return getAllContacts(Query, Arguments, IncludePhoneNumber, IncludeNotes);
    }

    private List getAllContacts(String selection, String[] args, boolean includePhone, boolean includeNotes) {
        Cursor crsr = BA.applicationContext.getContentResolver().query(Contacts.CONTENT_URI, people_projection, selection, args, null);
        List l = new List();
        l.Initialize();
        HashMap<String, Integer> m = new HashMap();
        for (int col = 0; col < crsr.getColumnCount(); col++) {
            m.put(crsr.getColumnName(col), Integer.valueOf(col));
        }
        while (crsr.moveToNext()) {
            String notes;
            String phoneNumber = "";
            String notes2 = "";
            int id = crsr.getInt(((Integer) m.get("_id")).intValue());
            if (includePhone) {
                if (crsr.getInt(((Integer) m.get("has_phone_number")).intValue()) != 0) {
                    Cursor phones = BA.applicationContext.getContentResolver().query(Phone.CONTENT_URI, phone_projection, "contact_id = " + id, null, null);
                    while (phones.moveToNext()) {
                        phoneNumber = phones.getString(phones.getColumnIndex("data1"));
                        if (phones.getInt(phones.getColumnIndex("is_primary")) != 0) {
                            break;
                        }
                    }
                    phones.close();
                }
            }
            if (includeNotes) {
                Cursor notesC = BA.applicationContext.getContentResolver().query(Data.CONTENT_URI, new String[]{"data1"}, "contact_id = " + id + " AND " + "mimetype" + " = ?", new String[]{"vnd.android.cursor.item/note"}, null);
                notes = notes2;
                while (notesC.moveToNext()) {
                    notes = notesC.getString(0);
                }
                notesC.close();
            } else {
                notes = notes2;
            }
            String string = crsr.getString(((Integer) m.get("display_name")).intValue());
            boolean z = crsr.getInt(((Integer) m.get("starred")).intValue()) > 0;
            l.Add(new Contact2(string, phoneNumber, z, id, notes, crsr.getInt(((Integer) m.get("times_contacted")).intValue()), crsr.getLong(((Integer) m.get("last_time_contacted")).intValue()), crsr.getString(((Integer) m.get("display_name")).intValue()), crsr.getInt(((Integer) m.get("photo_id")).intValue())));
        }
        crsr.close();
        return l;
    }
}
