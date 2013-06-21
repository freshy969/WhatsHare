package it.mb.whatshare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * Activity started when tapping on the notification coming from GCM that
 * forwards the shared content to Whatsapp by means of an Intent.
 * 
 * @author Michele Bonazza
 */
public class SendToWhatsappActivity extends FragmentActivity {

    /**
     * The preference key used to store the user preference over whether showing
     * the missing Whatsapp dialog every time content is shared.
     */
    public static final String HIDE_MISSING_WHATSAPP_KEY = "hideMissingWhatsappDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onNewIntent(android.content.Intent)
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Utils.debug("SendToWhatsappActivity.onNewIntent()");
        if (intent != null) {
            if (intent.getExtras() != null) {
                String message = intent.getStringExtra("message");
                if (message != null) {
                    if (MainActivity.isWhatsappInstalled(this)) {
                        startActivity(createIntent(message));
                    } else {
                        if (isMissingWhatsappDialogHidden()) {
                            startActivity(SendToAppActivity
                                    .createPlainIntent(intent
                                            .getStringExtra("message")));
                        } else {
                            Dialogs.whatsappMissing(this, intent);
                        }
                    }
                } else {
                    Utils.debug("QUE?");
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    private Intent createIntent(String message) {
        Intent i = new Intent(android.content.Intent.ACTION_SEND);
        i.setPackage(MainActivity.WHATSAPP_PACKAGE);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, message);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Log.d("main", "this is the message: " + message);
        return i;
    }

    private boolean isMissingWhatsappDialogHidden() {
        SharedPreferences pref = getSharedPreferences("it.mb.whatshare",
                Context.MODE_PRIVATE);
        return pref.getBoolean(HIDE_MISSING_WHATSAPP_KEY, false);
    }

}
