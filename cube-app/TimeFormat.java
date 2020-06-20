import java.text.DecimalFormat;

public class TimeFormat {
    private  int minutes;
    private DecimalFormat m;
    private DecimalFormat s;
    private DecimalFormat d;
    
    public TimeFormat(double time) {
        m = new DecimalFormat("00");
        d = new DecimalFormat("0.00");
        s = new DecimalFormat("00.00");
    }
    
    public String toMinutes(double seconds) {
        minutes = (int) (seconds / 60);
        seconds = seconds - minutes * 60;
        String min = m.format(minutes);
        String sec = s.format(seconds);
        return String.format("%s:%s", min, sec);
    }
    
    public String toSeconds(double seconds) {
        return d.format(seconds);
    }
    
    
}
