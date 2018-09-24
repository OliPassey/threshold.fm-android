package anywheresoftware.b4a.phone;

import android.content.ContentValues;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore.Audio.Media;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.IOnActivityResult;
import anywheresoftware.b4a.objects.streams.File;

@ShortName("RingtoneManager")
public class RingtoneManagerWrapper {
    public static final int TYPE_ALARM = 4;
    public static final int TYPE_NOTIFICATION = 2;
    public static final int TYPE_RINGTONE = 1;
    private IOnActivityResult ion;

    public String GetContentDir() {
        return File.ContentDir;
    }

    public String AddToMediaStore(String Dir, String FileName, String Title, boolean IsAlarm, boolean IsNotification, boolean IsRingtone, boolean IsMusic) {
        java.io.File k = new java.io.File(Dir, FileName);
        ContentValues values = new ContentValues();
        values.put("_data", k.getAbsolutePath());
        values.put("title", Title);
        values.put("mime_type", "audio/*");
        values.put("is_ringtone", Boolean.valueOf(IsRingtone));
        values.put("is_notification", Boolean.valueOf(IsNotification));
        values.put("is_alarm", Boolean.valueOf(IsAlarm));
        values.put("is_music", Boolean.valueOf(IsMusic));
        return BA.applicationContext.getContentResolver().insert(Media.getContentUriForPath(k.getAbsolutePath()), values).toString();
    }

    public void SetDefault(int Type, String Uri) {
        RingtoneManager.setActualDefaultRingtoneUri(BA.applicationContext, Type, Uri.parse(Uri));
    }

    public String GetDefault(int Type) {
        Uri u = RingtoneManager.getDefaultUri(Type);
        if (u == null) {
            return "";
        }
        return u.toString();
    }

    public void Play(BA ba, String Uri) {
        Ringtone r = RingtoneManager.getRingtone(ba.context, Uri.parse(Uri));
        if (r != null) {
            r.play();
        }
    }

    public void DeleteRingtone(String Uri) {
        BA.applicationContext.getContentResolver().delete(Uri.parse(Uri), null, null);
    }

    public void ShowRingtonePicker(final BA ba, String EventName, int Type, boolean IncludeSilence, String ChosenRingtone) {
        final String eventName = EventName.toLowerCase(BA.cul);
        Intent i = new Intent("android.intent.action.RINGTONE_PICKER");
        i.putExtra("android.intent.extra.ringtone.TYPE", Type);
        i.putExtra("android.intent.extra.ringtone.SHOW_SILENT", IncludeSilence);
        Uri def = RingtoneManager.getDefaultUri(Type);
        if (def != null) {
            i.putExtra("android.intent.extra.ringtone.DEFAULT_URI", def);
        }
        if (ChosenRingtone.length() > 0) {
            i.putExtra("android.intent.extra.ringtone.EXISTING_URI", Uri.parse(ChosenRingtone));
        }
        this.ion = new IOnActivityResult() {
            public void ResultArrived(int resultCode, Intent intent) {
                String uri = null;
                if (resultCode == -1 && intent != null) {
                    Uri u = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
                    uri = u == null ? "" : u.toString();
                }
                RingtoneManagerWrapper.this.ion = null;
                if (uri != null) {
                    ba.raiseEvent(RingtoneManagerWrapper.this, eventName + "_pickerresult", Boolean.valueOf(true), uri);
                } else {
                    ba.raiseEvent(RingtoneManagerWrapper.this, eventName + "_pickerresult", Boolean.valueOf(false), "");
                }
            }
        };
        ba.startActivityForResult(this.ion, i);
    }
}
