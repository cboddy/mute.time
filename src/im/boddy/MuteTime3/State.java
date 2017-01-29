package im.boddy.MuteTime3;
import java.util.*;


public class State {
    private static final int MINUTE_MS = 1000 * 60;
    public enum Frequency {
        None,
        Hourly,
        Daily;
    }

    final Date date;
    final int duration;
    final Frequency frequency;

    public State(Date date, int duration, Frequency frequency)  {
            this.date = new Date(date.getTime());
            this.duration = duration;
            this.frequency = frequency;
    } 
    
    public Date startTime() {
        return new Date(date.getTime());
    }

    public Date endTime() {
        long time = date.getTime() + duration * MINUTE_MS;
        return new Date(time);
    }
}
