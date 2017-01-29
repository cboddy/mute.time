package im.boddy.MuteTime3;
import java.util.*;


public class State {
    public enum Frequency {
        None,
        Hourly,
        Daily;
    }
    private final Date date;
    private final int duration;
    private final Frequency frequency;

    public State(Date date, int duration, Frequency frequency)  {
            this.date = new Date(date.getTime());
            this.duration = duration;
            this.frequency = frequency;
    } 
}
