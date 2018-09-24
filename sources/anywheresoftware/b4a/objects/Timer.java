package anywheresoftware.b4a.objects;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.CheckForReinitialize;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.Msgbox;

@ShortName("Timer")
public class Timer implements CheckForReinitialize {
    private BA ba;
    private boolean enabled = false;
    private String eventName;
    private long interval;
    private ParentReference myRef = new ParentReference();
    private int relevantTimer = 0;

    static class ParentReference {
        public Timer timer;

        ParentReference() {
        }
    }

    static class TickTack implements Runnable {
        private final BA ba;
        private final int currentTimer;
        private final ParentReference parent;

        public TickTack(int currentTimer, ParentReference parent, BA ba) {
            this.currentTimer = currentTimer;
            this.parent = parent;
            this.ba = ba;
        }

        public void run() {
            Timer parentTimer = this.parent.timer;
            if (parentTimer != null && this.currentTimer == parentTimer.relevantTimer) {
                BA.handler.postDelayed(this, parentTimer.interval);
                if (!this.ba.isActivityPaused() && !Msgbox.msgboxIsVisible()) {
                    this.ba.raiseEvent2(parentTimer, false, parentTimer.eventName, true, new Object[0]);
                }
            }
        }
    }

    public void Initialize(BA ba, String EventName, long Interval) {
        this.interval = Interval;
        this.ba = ba;
        this.eventName = EventName.toLowerCase(BA.cul) + "_tick";
    }

    public boolean IsInitialized() {
        return this.ba != null;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setInterval(long Interval) {
        if (this.interval != Interval) {
            this.interval = Interval;
            if (this.enabled) {
                stopTicking();
                startTicking();
            }
        }
    }

    public long getInterval() {
        return this.interval;
    }

    private void startTicking() {
        BA.handler.postDelayed(new TickTack(this.relevantTimer, this.myRef, this.ba), this.interval);
    }

    public void setEnabled(boolean Enabled) {
        if (Enabled != this.enabled) {
            if (Enabled) {
                this.myRef.timer = this;
                if (this.interval <= 0) {
                    throw new IllegalStateException("Interval must be larger than 0.");
                }
                startTicking();
            } else {
                this.myRef.timer = null;
                stopTicking();
            }
            this.enabled = Enabled;
        }
    }

    private void stopTicking() {
        this.relevantTimer++;
    }
}
