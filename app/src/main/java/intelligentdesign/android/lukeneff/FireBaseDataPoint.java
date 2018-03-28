package intelligentdesign.android.lukeneff;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by brianlutz on 3/27/18.
 */

public class FireBaseDataPoint implements DataPointInterface {

    private double x;
    private double y;

    public FireBaseDataPoint(Date x, double y) {
        this.x = x.getTime();
        this.y = y;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("x", x);
        result.put("y", y);

        return result;
    }
}
