package intelligentdesign.android.lukeneff;

import android.support.v4.app.Fragment;

/**
 * Created by brianlutz on 4/4/18.
 */

public class HistoryListActivity extends SingleFragmentActivity {

    public Fragment createFragment() {
        return new HistoryListFragment();
    }
}
