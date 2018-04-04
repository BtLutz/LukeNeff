package intelligentdesign.android.lukeneff;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.vision.CameraSource;
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
import java.util.Date;
import java.util.List;

/**
 * Created by brianlutz on 3/23/18.
 */

public class MainFragment extends Fragment {

    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_DETECTION = 3;
    private static final String FILE_PROVIDER =
            "com.intelligentdesign.lukeneff.fileprovider";
    private static final String EXTRA_FILE_URI = "com.intelligentdesign.lukeneff.file_uri";

//    private ImageView mPhotoTaken;
    private Button mTakePhotoButton;
    private Button mLogOutButton;
    private Button mViewHistoryButton;
    private File mPhotoFile;
    private DatabaseReference mDatabase;
    private FirebaseUser mUser;
    private GraphView mGraphView;
    private LineGraphSeries<DataPoint> mSeries;

    /* Control Flow:
       1. Fragment is started
       2. Fragment sends implicit intent to Camera
       3.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

//        mPhotoTaken = (ImageView) v.findViewById(R.id.photo_taken);
        mGraphView = (GraphView) v.findViewById(R.id.graph);
        mTakePhotoButton = (Button) v.findViewById(R.id.take_photo_button);
        mLogOutButton = (Button) v.findViewById(R.id.log_out);
        mViewHistoryButton = (Button) v.findViewById(R.id.view_history);
        mPhotoFile = PhotoLab.get(getContext()).getPhotoFile();


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
                    FireBaseDataPoint point = snapshot.getValue(FireBaseDataPoint.class);
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

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mTakePhotoButton.setEnabled(canTakePhoto);

        /* Setting OnClickListeners */
        // Take Photo Button
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
                Intent intent = new Intent(getContext(), GraphActivity.class);
                startActivity(intent);
            }
        });

//        updatePhotoView();
        return v;
    }

//    public void updatePhotoView() {
//        if (mPhotoTaken == null || !mPhotoFile.exists()) {
//            // do nothing!
//        } else {
//            Bitmap bitmap = PictureUtilities.getScaledBitmap(mPhotoFile.getPath(), getActivity());
//            mPhotoTaken.setImageBitmap(bitmap);
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(), FILE_PROVIDER, mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            updatePhotoView();
            Intent i = new Intent(getContext(), FaceEvaluateActivity.class); // Modify activity name here
            i.putExtra(EXTRA_FILE_URI, uri);
            startActivityForResult(i, REQUEST_DETECTION);
        }
    }
}
