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
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.util.Log;

import static java.util.Calendar.*;
import static android.app.AlarmManager.*;

public class MuteTimeActivity extends Activity
{
        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.main);

                TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
                timePicker.setIs24HourView(true);

                Switch switchButton = (Switch) findViewById(R.id.switchButton);
                switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                        // get app state
                                        State state = getState();
                                        //schedule mute/un-mute receivers
                                        scheduleIntents(state);
                                }
                                else {
                                        //cancel the  pending-intents that will (un) mute the music-volume
                                        cancelScheduledIntents();
                                }
                        }
                });
        }

        public State getState() {
                TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
                Spinner frequencySpinner = (Spinner) findViewById(R.id.timeFrequencySpinner);
                Spinner durationSpinner = (Spinner) findViewById(R.id.timeDurationSpinner);

                Calendar cal = Calendar.getInstance();
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                cal.set(HOUR_OF_DAY, hour);
                cal.set(MINUTE, minute);
                cal.set(SECOND, 0);
                Date date = cal.getTime();

                int duration = Integer.parseInt(
                                durationSpinner.getSelectedItem().toString());
                State.Frequency frequency = State.Frequency.valueOf(
                                frequencySpinner.getSelectedItem().toString());
                return new State(date, duration, frequency);
        }

        public void scheduleIntents(State state) {
                Date muteTime = state.startTime();
                Date unmuteTime = state.endTime();
                
                Log.i("MuteTimeActivity", "scheduling alarm to mute/unmute "+ muteTime +", "+ unmuteTime + " with frequency "+ state.frequency);

                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int currentVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

                //create the receiver objects
                Intent muteIntentAlarm = new Intent(this, MuteReceiver.class);
                Intent unMuteIntentAlarm = new Intent(this, UnMuteReceiver.class);
                unMuteIntentAlarm.putExtra("previousVolume", currentVolume);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                //set the mute time alarm
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, MUTE_INTENT_CODE, muteIntentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
                
                long repeatInterval = -1; 
                switch(state.frequency) {
                    case Hourly:
                        repeatInterval = INTERVAL_HOUR;
                        break;
                    case Daily:
                        repeatInterval = INTERVAL_DAY;
                        break;
                    case None:
                        repeatInterval = 0;
                        break;
                    default:
                        throw new RuntimeException("Unimplemented frequency "+ state.frequency);
                }

                if (repeatInterval > 0) {
                    //set the mute alarm time
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                                muteTime.getTime(),
                                repeatInterval,
                                pendingIntent);

                    //set the un-mute alarm time
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                                unmuteTime.getTime(),
                                repeatInterval,
                                PendingIntent.getBroadcast(this, UN_MUTE_INTENT_CODE, unMuteIntentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
                }
                else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP,
                                muteTime.getTime(),
                                pendingIntent);

                    alarmManager.set(AlarmManager.RTC_WAKEUP,
                                unmuteTime.getTime(),
                                PendingIntent.getBroadcast(this, UN_MUTE_INTENT_CODE, unMuteIntentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

                }

                Toast.makeText(this, "Mute set for "+ format(muteTime) +", un-mute set for "+ format(unmuteTime) + " with frequency "+ state.frequency, Toast.LENGTH_LONG).show();
        }
        
        public void cancelScheduledIntents() {
                Log.i("MuteTimeActivity", "Cancelling mute and un-mute intents.");
                Intent muteIntent = new Intent(this, MuteReceiver.class);
                Intent unmuteIntent = new Intent(this, UnMuteReceiver.class);

                PendingIntent pendingMute = PendingIntent.getBroadcast(this, MUTE_INTENT_CODE, muteIntent, 0);
                PendingIntent pendingUnMute = PendingIntent.getBroadcast(this, UN_MUTE_INTENT_CODE, unmuteIntent, 0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingMute);
                alarmManager.cancel(pendingUnMute);

                Toast.makeText(this, "Scheduled mute-times are cancelled.", Toast.LENGTH_LONG).show();
        }

        private static final int  MUTE_INTENT_CODE = 0x1234;
        private static final int  UN_MUTE_INTENT_CODE = 0x2345;

        private static String format(Date date) {
                return DATE_FORMAT.format(date);
        }
        private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
}
