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
import static java.lang.System.in;
import static org.junit.Assert.assertEquals;

/**
 * Created by Luke Neff on 4/5/2018.
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class FireBaseSignInTest extends ActivityUnitTestCase {
    @Rule
    public ActivityTestRule<SignInActivity> rule  = new  ActivityTestRule<SignInActivity>(SignInActivity.class){
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

    public FireBaseSignInTest() {
        super(SignInActivity.class);
    }
    @Test
    public void testHello(){
        String email = "foo@bar.com";
        String password = "foobar";


        SignInActivity signInTest = rule.getActivity();
        signInTest.getmAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(signInTest, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String result  = "Fail";
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        //hideProgressDialog();

                        if (task.isSuccessful()) {
                            result = "Success";
                            Log.d(TAG,"Success");
                        } else {
                            Log.d(TAG,"Fail");
                        }
                        assertEquals("Success", result);
                    }
                });

    }
}
