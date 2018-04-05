package intelligentdesign.android.lukeneff;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by brianlutz on 3/27/18.
 */

public class History {

    public float getHappiness() {
        return happiness;
    }

    public Date getDate() {
        Date d = new Date(date);
        return d;
    }

    private long date;
    private float happiness;

    public History() {
        //For DataSnapshot
    }

    public History(float happiness) {
        this.date = generateDate();
        this.happiness = happiness;
    }

    private long generateDate() {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        return now.getTime();
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("happiness", happiness);

        return result;
    }
}
