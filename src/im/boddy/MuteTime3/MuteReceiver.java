package im.boddy.MuteTime3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.Toast;
import android.util.Log;

import java.util.Date;

public class MuteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //set stream music volune to zero
        Toast.makeText(context, "Volume Muted @ " + new Date(), Toast.LENGTH_LONG).show();
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
    }
}
