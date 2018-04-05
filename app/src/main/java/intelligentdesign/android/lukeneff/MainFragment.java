package intelligentdesign.android.lukeneff;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.util.List;

/**
 * Created by brianlutz on 3/23/18.
 */

public class MainFragment extends Fragment {

    // Intent request IDs
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_DETECTION = 3;

    // Extras and FileProvider
    private static final String FILE_PROVIDER =
            "com.intelligentdesign.lukeneff.fileprovider";
    private static final String EXTRA_FILE_URI = "com.intelligentdesign.lukeneff.file_uri";

    // Android UI/Layout stuff
    private Button mTakePhotoButton;
    private Button mLogOutButton;
    private Button mViewHistoryButton;
    private File mPhotoFile;

    // Firebase stuff
    private DatabaseReference mDatabase;
    private FirebaseUser mUser;

    // Android-Graph stuff
    private GraphView mGraphView;
    private LineGraphSeries<DataPoint> mSeries;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        // Finding Views
        mGraphView = (GraphView) v.findViewById(R.id.graph);
        mTakePhotoButton = (Button) v.findViewById(R.id.take_photo_button);
        mLogOutButton = (Button) v.findViewById(R.id.log_out);
        mViewHistoryButton = (Button) v.findViewById(R.id.view_history);
        mPhotoFile = PhotoLab.get(getContext()).getPhotoFile();

        // Instantiating FireBase stuff
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mSeries = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0,0),
                new DataPoint(1,0),
                new DataPoint(2,0),
                new DataPoint(3,0),
                new DataPoint(4,0),
                new DataPoint(5,0),
                new DataPoint(6,0),
                new DataPoint(7,0),
                new DataPoint(8,0),
                new DataPoint(9,0)
        });

        mGraphView.addSeries(mSeries);

        Query graphData = mDatabase.child("users").child(mUser.getUid()).child("data-points");

        graphData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mGraphView.removeAllSeries();

                DataPoint[] points = new DataPoint[(int)dataSnapshot.getChildrenCount()];

                int i = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    History point = snapshot.getValue(History.class);
                    points[i] = new DataPoint(i,(double)point.getHappiness()*100);
                    i++;
                }

                mSeries = new LineGraphSeries<>(points);
                mGraphView.addSeries(mSeries);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        PackageManager packageManager = getContext().getPackageManager();

        // Intent to start the camera. This doesn't change (ever), hence why it's final.
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Checking if we can get permissions to access both the camera and the file we store at
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mTakePhotoButton.setEnabled(canTakePhoto);

        // Take Photo OnClickListener
        mTakePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = FileProvider.getUriForFile(getActivity(), FILE_PROVIDER, mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager()
                        .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        // Log Out Button
        mLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), SignInActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        // View History Button
        mViewHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), HistoryListActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(), FILE_PROVIDER, mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            updatePhotoView();
            Intent i = new Intent(getContext(), FaceEvaluateActivity.class);
            i.putExtra(EXTRA_FILE_URI, uri);
            startActivityForResult(i, REQUEST_DETECTION);
        }
    }
}
