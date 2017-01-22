package im.boddy.MuteTime3;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import java.util.*;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import static java.util.Calendar.*;
import android.util.Log;

public class MuteTimeActivity extends Activity
{
		/** Called when the activity is first created. */
		@Override
		public void onCreate(Bundle savedInstanceState)
		{
				super.onCreate(savedInstanceState);
				setContentView(R.layout.main);
				Calendar cal = Calendar.getInstance();
				cal.set(SECOND, 0);
                int curMin = cal.get(MINUTE);

                int muteMin = curMin+2 % 60;
                int unmuteMin = curMin+3 %60;
                //roll hour
                if (muteMin < curMin)
                        cal.set(HOUR,  cal.get(HOUR)+1 % 24);
				cal.set(MINUTE, muteMin);				 
				Date muteTime = cal.getTime();
                if (unmuteMin < muteMin)
                        cal.set(HOUR,  cal.get(HOUR)+1 % 24);
				cal.set(MINUTE, unmuteMin);
				Date unmuteTime = cal.getTime();
	 
				scheduleAlarms(muteTime, unmuteTime);
		}

		public void scheduleAlarms(Date  muteTime, Date unmuteTime) {
                
                Log.i("MuteTimeActivity", "scheduling alarm to mute/unmute "+ muteTime +", "+ unmuteTime);
				AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				int currentVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

				//create the receiver objects
				Intent muteIntentAlarm = new Intent(this, MuteReceiver.class);
				Intent unMuteIntentAlarm = new Intent(this, UnMuteReceiver.class);
				unMuteIntentAlarm.putExtra("previousVolume", currentVolume);

				AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

				//set the mute time alarm
				PendingIntent pendingIntent = PendingIntent.getBroadcast(this, MUTE_INTENT_CODE, muteIntentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
								muteTime.getTime(),
								HOUR_MS,
								pendingIntent);

				//set the un-mute alarm time
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
								unmuteTime.getTime(),
								HOUR_MS,
								PendingIntent.getBroadcast(this, UN_MUTE_INTENT_CODE, unMuteIntentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

				Toast.makeText(this, "Mute set for "+ format(muteTime) +", un-mute set for "+ format(unmuteTime), Toast.LENGTH_LONG).show();
		}

		private static final int  DAY_MS = 24 * 60 * 60 * 1000;
		private static final int  HOUR_MS = 60 * 60 * 1000;
		private static final int  MUTE_INTENT_CODE = 0x1234;
		private static final int  UN_MUTE_INTENT_CODE = 0x2345;

		private static String format(Date date) {
				return DATE_FORMAT.format(date);
		}
		private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
}
