package intelligentdesign.android.lukeneff;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import java.text.NumberFormat;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
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

    // FireBase stuff
    private DatabaseReference mDatabase;
    private FirebaseUser mUser;

    // Android-Graph stuff
    private GraphView mGraphView;

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

        // Jank code for initializing a graph when a user has no data.
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
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

        // Format graph x and y axis values
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(0);
        mGraphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf,nf));

        // Add padding for axis numbers
        GridLabelRenderer glr = mGraphView.getGridLabelRenderer();
        glr.setPadding(32); // should allow for 3 digits to fit on screen

        mGraphView.addSeries(series);

        HistoryLab.getInstance().getHistoriesAsynchronously(this);

        // Initialize the camera stuff
        initializeCameraIntentAndListener();
        initializeHistoryAndLogOutListeners();

        return v;
    }

    public void updateGraph(List<History> histories) {
        DataPoint[] dataPoints = new DataPoint[(int) histories.size()];

        // Clearing the graph in the odd chance there was still data there
        if (histories.size() > 0) {
            mGraphView.removeAllSeries();
        }

        // Looping through the data we got from our singleton to get it ready to plot on the graph.
        for (int i = 0; i < histories.size(); i++) {
            History history = histories.get(i);
            DataPoint dataPoint = new DataPoint(i, (double) history.getHappiness() * 100);
            dataPoints[i] = dataPoint;
        }

        // Finally, plotting all of the data we got from our singleton object.
        LineGraphSeries<DataPoint> dataPointSeries = new LineGraphSeries<>(dataPoints);
        mGraphView.addSeries(dataPointSeries);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(), FILE_PROVIDER, mPhotoFile);
            Intent i = new Intent(getContext(), FaceEvaluateActivity.class);
            i.putExtra(EXTRA_FILE_URI, uri);
            startActivityForResult(i, REQUEST_DETECTION);
        }
    }

    private void initializeCameraIntentAndListener() {
        // I'm like 75% sure that the packageManager is the android OS API for saving files
        // to and from local storage on the device, but please don't quote me on that.
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
    }

    private void initializeHistoryAndLogOutListeners() {
        // Log Out Button
        mLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                HistoryLab.getInstance().deleteHistoriesAndDeinitialize();
                mGraphView.removeAllSeries();
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
    }
}
