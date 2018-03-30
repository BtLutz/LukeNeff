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

public class FireBaseDataPoint {

    private String x;
    private double y;

    public FireBaseDataPoint () {
        //For DataSnapshot
    }

    public FireBaseDataPoint(double y) {
        this.x = getDate();
        this.y = y;
    }

    private String getDate() {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        return now.toString();
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("x", x);
        result.put("y", y);

        return result;
    }
}
