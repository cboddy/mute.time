package im.boddy.MuteTime3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.Toast;
import android.util.Log;

import java.util.Date;

public class UnMuteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        int previousVolume= intent.getIntExtra("previousVolume", 100);
        //set stream music to previous value if still muted
        int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (streamVolume == 0) {
            Toast.makeText(context, "Volume Un-muted @ " + new Date(), Toast.LENGTH_LONG).show();
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, previousVolume, 0);
        }
    }
}
