package anywheresoftware.b4a.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.B4ARunnable;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.objects.IntentWrapper;
import anywheresoftware.b4a.phone.Phone.PhoneId;
import java.util.HashMap;
import java.util.Map.Entry;

@ShortName("PhoneEvents")
public class PhoneEvents {
    private BA ba;
    private BroadcastReceiver br;
    private String ev;
    private HashMap<String, ActionHandler> map = new HashMap();

    private abstract class ActionHandler {
        public String action;
        public String event;
        public int resultCode;

        public abstract void handle(Intent intent);

        private ActionHandler() {
        }

        protected void send(Intent intent, Object[] args) {
            Object[] o;
            if (args == null) {
                o = new Object[1];
            } else {
                o = new Object[(args.length + 1)];
                System.arraycopy(args, 0, o, 0, args.length);
            }
            o[o.length - 1] = AbsObjectWrapper.ConvertToWrapper(new IntentWrapper(), intent);
            if (BA.debugMode) {
                BA.handler.post(new B4ARunnable() {
                    public void run() {
                        PhoneEvents.this.ba.raiseEvent(this, new StringBuilder(String.valueOf(PhoneEvents.this.ev)).append(ActionHandler.this.event).toString(), o);
                    }
                });
            } else {
                PhoneEvents.this.ba.raiseEvent(this, new StringBuilder(String.valueOf(PhoneEvents.this.ev)).append(this.event).toString(), o);
            }
        }
    }

    @ShortName("SmsInterceptor")
    public static class SMSInterceptor {
        private BA ba;
        private BroadcastReceiver br;
        private String eventName;

        public void Initialize(String EventName, BA ba) {
            Initialize2(EventName, ba, 0);
        }

        public void ListenToOutgoingMessages() {
            final Uri content = Uri.parse("content://sms");
            BA.applicationContext.getContentResolver().registerContentObserver(content, true, new ContentObserver(new Handler()) {
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    Cursor cursor = BA.applicationContext.getContentResolver().query(content, null, null, null, null);
                    if (cursor.moveToNext()) {
                        String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
                        int type = cursor.getInt(cursor.getColumnIndex("type"));
                        if (protocol == null && type == 2) {
                            SMSInterceptor.this.ba.raiseEvent(null, new StringBuilder(String.valueOf(SMSInterceptor.this.eventName)).append("_messagesent").toString(), Integer.valueOf(cursor.getInt(cursor.getColumnIndex("_id"))));
                            cursor.close();
                        }
                    }
                }
            });
        }

        public void Initialize2(String EventName, final BA ba, int Priority) {
            this.ba = ba;
            this.eventName = EventName.toLowerCase(BA.cul);
            this.br = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                        Bundle bundle = intent.getExtras();
                        if (bundle != null) {
                            Object[] pduObj = (Object[]) bundle.get("pdus");
                            for (Object obj : pduObj) {
                                SmsMessage sm = SmsMessage.createFromPdu((byte[]) obj);
                                Boolean res = (Boolean) ba.raiseEvent(SMSInterceptor.this, new StringBuilder(String.valueOf(SMSInterceptor.this.eventName)).append("_messagereceived").toString(), sm.getOriginatingAddress(), sm.getMessageBody());
                                if (res != null && res.booleanValue()) {
                                    abortBroadcast();
                                }
                            }
                        }
                    }
                }
            };
            IntentFilter fil = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            fil.setPriority(Priority);
            BA.applicationContext.registerReceiver(this.br, fil);
        }

        public void StopListening() {
            if (this.br != null) {
                BA.applicationContext.unregisterReceiver(this.br);
            }
            this.br = null;
        }
    }

    public PhoneEvents() {
        this.map.put("android.speech.tts.TTS_QUEUE_PROCESSING_COMPLETED", new ActionHandler(this) {
            public void handle(Intent intent) {
                send(intent, null);
            }
        });
        this.map.put("android.net.conn.CONNECTIVITY_CHANGE", new ActionHandler(this) {
            public void handle(Intent intent) {
                NetworkInfo ni = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                String type = ni.getTypeName();
                String state = ni.getState().toString();
                send(intent, new Object[]{type, state});
            }
        });
        this.map.put("android.intent.action.USER_PRESENT", new ActionHandler(this) {
            public void handle(Intent intent) {
                send(intent, null);
            }
        });
        this.map.put("android.intent.action.ACTION_SHUTDOWN", new ActionHandler(this) {
            public void handle(Intent intent) {
                send(intent, null);
            }
        });
        this.map.put("android.intent.action.SCREEN_ON", new ActionHandler(this) {
            public void handle(Intent intent) {
                send(intent, null);
            }
        });
        this.map.put("android.intent.action.SCREEN_OFF", new ActionHandler(this) {
            public void handle(Intent intent) {
                send(intent, null);
            }
        });
        this.map.put("android.intent.action.PACKAGE_REMOVED", new ActionHandler(this) {
            public void handle(Intent intent) {
                send(intent, new Object[]{intent.getDataString()});
            }
        });
        this.map.put("android.intent.action.PACKAGE_ADDED", new ActionHandler(this) {
            public void handle(Intent intent) {
                send(intent, new Object[]{intent.getDataString()});
            }
        });
        this.map.put("android.intent.action.DEVICE_STORAGE_LOW", new ActionHandler(this) {
            public void handle(Intent intent) {
                send(intent, null);
            }
        });
        this.map.put("b4a.smssent", new ActionHandler(this) {
            public void handle(Intent intent) {
                boolean z;
                String msg = "";
                switch (this.resultCode) {
                    case -1:
                        msg = "OK";
                        break;
                    case 1:
                        msg = "GENERIC_FAILURE";
                        break;
                    case 2:
                        msg = "RADIO_OFF";
                        break;
                    case 3:
                        msg = "NULL_PDU";
                        break;
                    case 4:
                        msg = "NO_SERVICE";
                        break;
                }
                Object[] objArr = new Object[3];
                if (this.resultCode == -1) {
                    z = true;
                } else {
                    z = false;
                }
                objArr[0] = Boolean.valueOf(z);
                objArr[1] = msg;
                objArr[2] = intent.getStringExtra("phone");
                send(intent, objArr);
            }
        });
        this.map.put("b4a.smsdelivered", new ActionHandler(this) {
            public void handle(Intent intent) {
                send(intent, new Object[]{intent.getStringExtra("phone")});
            }
        });
        this.map.put("android.intent.action.DEVICE_STORAGE_OK", new ActionHandler(this) {
            public void handle(Intent intent) {
                send(intent, null);
            }
        });
        this.map.put("android.intent.action.BATTERY_CHANGED", new ActionHandler(this) {
            public void handle(Intent intent) {
                boolean plugged;
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 1);
                if (intent.getIntExtra("plugged", 0) > 0) {
                    plugged = true;
                } else {
                    plugged = false;
                }
                send(intent, new Object[]{Integer.valueOf(level), Integer.valueOf(scale), Boolean.valueOf(plugged)});
            }
        });
        this.map.put("android.intent.action.AIRPLANE_MODE", new ActionHandler(this) {
            public void handle(Intent intent) {
                boolean state = intent.getBooleanExtra("state", false);
                send(intent, new Object[]{Boolean.valueOf(state)});
            }
        });
        for (Entry<String, ActionHandler> e : this.map.entrySet()) {
            ((ActionHandler) e.getValue()).action = (String) e.getKey();
        }
    }

    public void InitializeWithPhoneState(BA ba, String EventName, PhoneId PhoneId) {
        this.map.put("android.intent.action.PHONE_STATE", new ActionHandler(this) {
            public void handle(Intent intent) {
                String state = intent.getStringExtra("state");
                String incomingNumber = intent.getStringExtra("incoming_number");
                if (incomingNumber == null) {
                    incomingNumber = "";
                }
                send(intent, new Object[]{state, incomingNumber});
            }
        });
        ((ActionHandler) this.map.get("android.intent.action.PHONE_STATE")).action = "android.intent.action.PHONE_STATE";
        Initialize(ba, EventName);
    }

    public void Initialize(BA ba, String EventName) {
        this.ba = ba;
        this.ev = EventName.toLowerCase(BA.cul);
        StopListening();
        this.br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != null) {
                    ActionHandler ah = (ActionHandler) PhoneEvents.this.map.get(intent.getAction());
                    if (ah != null) {
                        ah.resultCode = getResultCode();
                        ah.handle(intent);
                    }
                }
            }
        };
        IntentFilter f1 = new IntentFilter();
        IntentFilter f2 = null;
        for (ActionHandler ah : this.map.values()) {
            if (ba.subExists(this.ev + ah.event)) {
                if (ah.action == "android.intent.action.PACKAGE_ADDED" || ah.action == "android.intent.action.PACKAGE_REMOVED") {
                    if (f2 == null) {
                        f2 = new IntentFilter();
                        f2.addDataScheme("package");
                    }
                    f2.addAction(ah.action);
                }
                f1.addAction(ah.action);
            }
        }
        BA.applicationContext.registerReceiver(this.br, f1);
        if (f2 != null) {
            BA.applicationContext.registerReceiver(this.br, f2);
        }
    }

    public void StopListening() {
        if (this.br != null) {
            BA.applicationContext.unregisterReceiver(this.br);
        }
        this.br = null;
    }
}
