package intelligentdesign.android.lukeneff;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.vision.CameraSource;
/**
 * Created by brianlutz on 3/23/18.
 */

public class MainFragment extends Fragment {
    private ImageView mCameraSource = null;

    private Button mTakePhoto;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        mCameraSource = (ImageView) v.findViewById(R.id.preview);
        mTakePhoto = (Button) v.findViewById(R.id.take_photo_button);
        return v;
    }
}
