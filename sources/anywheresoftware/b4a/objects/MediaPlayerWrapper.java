package anywheresoftware.b4a.objects;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.objects.streams.File;
import java.io.FileInputStream;
import java.io.IOException;

@ShortName("MediaPlayer")
public class MediaPlayerWrapper {
    protected String eventName;
    @Hide
    public MediaPlayer mp;

    public void Initialize() throws IllegalArgumentException, IllegalStateException, IOException {
        this.mp = new MediaPlayer();
    }

    public boolean IsInitialized() {
        return this.mp != null;
    }

    public void Initialize2(final BA ba, String EventName) throws IllegalArgumentException, IllegalStateException, IOException {
        Initialize();
        this.eventName = EventName.toLowerCase(BA.cul);
        this.mp.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                ba.raiseEvent(MediaPlayerWrapper.this, new StringBuilder(String.valueOf(MediaPlayerWrapper.this.eventName)).append("_complete").toString(), new Object[0]);
            }
        });
    }

    public void Load(String Dir, String FileName) throws IllegalArgumentException, IllegalStateException, IOException {
        this.mp.reset();
        loadAfterReset(Dir, FileName);
    }

    private void loadAfterReset(String Dir, String FileName) throws IllegalArgumentException, IllegalStateException, IOException {
        AssetFileDescriptor fd;
        if (Dir.equals(File.getDirAssets())) {
            if (File.virtualAssetsFolder != null) {
                loadAfterReset(File.virtualAssetsFolder, File.getUnpackedVirtualAssetFile(FileName));
                return;
            }
            fd = BA.applicationContext.getAssets().openFd(FileName.toLowerCase(BA.cul));
            if (fd.getDeclaredLength() < 0) {
                this.mp.setDataSource(fd.getFileDescriptor());
            } else {
                this.mp.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getDeclaredLength());
            }
        } else if (Dir.startsWith(File.getDirInternal()) || Dir.equals(File.getDirInternalCache())) {
            this.mp.setDataSource(new FileInputStream(new java.io.File(Dir, FileName)).getFD());
        } else if (Dir.equals(File.ContentDir)) {
            fd = BA.applicationContext.getContentResolver().openAssetFileDescriptor(Uri.parse(FileName), "r");
            if (fd.getDeclaredLength() < 0) {
                this.mp.setDataSource(fd.getFileDescriptor());
            } else {
                this.mp.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getDeclaredLength());
            }
        } else {
            this.mp.setDataSource(new java.io.File(Dir, FileName).toString());
        }
        this.mp.prepare();
    }

    public boolean getLooping() {
        return this.mp.isLooping();
    }

    public void setLooping(boolean value) {
        this.mp.setLooping(value);
    }

    public void Play() {
        this.mp.start();
    }

    public void Stop() {
        this.mp.reset();
    }

    public void Pause() {
        this.mp.pause();
    }

    public int getDuration() {
        return this.mp.getDuration();
    }

    public int getPosition() {
        return this.mp.getCurrentPosition();
    }

    public void setPosition(int value) {
        this.mp.seekTo(value);
    }

    public void SetVolume(float Left, float Right) {
        this.mp.setVolume(Left, Right);
    }

    public boolean IsPlaying() {
        return this.mp.isPlaying();
    }

    public void Release() {
        this.mp.release();
    }
}
