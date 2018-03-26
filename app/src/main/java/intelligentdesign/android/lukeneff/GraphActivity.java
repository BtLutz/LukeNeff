package intelligentdesign.android.lukeneff;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by brianlutz on 3/26/18.
 */

public class GraphActivity extends SingleFragmentActivity {

    protected Fragment createFragment() {
        return new GraphFragment();
    }
}
