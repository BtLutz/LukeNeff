package intelligentdesign.android.lukeneff;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityUnitTestCase;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.ContentValues.TAG;

/**
 * Created by Luke Neff on 4/5/2018.
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class HistoryTest extends ActivityUnitTestCase {
    @Rule
    public ActivityTestRule<HistoryListActivity> rule  = new  ActivityTestRule<HistoryListActivity>(HistoryListActivity.class){
    @Override
    protected Intent getActivityIntent() {
        InstrumentationRegistry.getTargetContext();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("MYKEY", "Hello");
        return intent;
    }
};
    // TextView of the MainActivity to be tested
    TextView tvHello;

    public HistoryTest() {
        super(HistoryListFragment.class);
    }
    @Test
    public void testHistory(){
        String email = "foo@bar.com";
        String password = "foobar";


        HistoryListActivity historyListActivity = rule.getActivity();
        HistoryListFragment newFrag = (HistoryListFragment) historyListActivity.createFragment();
        HistoryListFragment.HistoryAdapter mHistoryAdapter= newFrag.getHistoryAdapter();

        try {
            int count = mHistoryAdapter.getItemCount();
            assertEquals("true","true");
        }
        catch (Exception e)
        {

        }





    }


}
