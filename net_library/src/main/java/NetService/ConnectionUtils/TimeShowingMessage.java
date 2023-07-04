package NetService.ConnectionUtils;



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeShowingMessage extends ChatMessageAbstract{
    public String timeString;
    public long timeStamp;

    public TimeShowingMessage(long timeStamp) {
        this.timeStamp = timeStamp;

        SimpleDateFormat format;
        if (isSameDay(timeStamp)) {
            format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        }else{
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        }
        timeString = format.format(new Date(timeStamp));
    }
    public static boolean isSameDay(long timestamp) {
        Calendar calendar1 = Calendar.getInstance(); // 当前时间的Calendar对象
        Calendar calendar2 = Calendar.getInstance(); // 时间戳对应的Calendar对象
        calendar2.setTimeInMillis(timestamp);

        int year1 = calendar1.get(Calendar.YEAR);
        int month1 = calendar1.get(Calendar.MONTH);
        int day1 = calendar1.get(Calendar.DAY_OF_MONTH);

        int year2 = calendar2.get(Calendar.YEAR);
        int month2 = calendar2.get(Calendar.MONTH);
        int day2 = calendar2.get(Calendar.DAY_OF_MONTH);

        return (year1 == year2) && (month1 == month2) && (day1 == day2);
    }
}
