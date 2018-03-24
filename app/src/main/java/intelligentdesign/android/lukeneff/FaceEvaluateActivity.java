package intelligentdesign.android.lukeneff;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FaceEvaluateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_evaluate);
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.fragment_hold);
        if (f == null) {
            f = new FaceEvaluateFragment();
            fm.beginTransaction().add(R.id.fragment_hold, f).commit();
        }
    }
}
