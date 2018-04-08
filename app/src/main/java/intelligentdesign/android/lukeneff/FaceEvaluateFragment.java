package intelligentdesign.android.lukeneff;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.vision.face.FaceDetector.ALL_CLASSIFICATIONS;
import static com.google.android.gms.vision.face.FaceDetector.ALL_LANDMARKS;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FaceEvaluateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FaceEvaluateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FaceEvaluateFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String EXTRA_FILE_URI = "com.intelligentdesign.lukeneff.file_uri";
    private ImageView mImageView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Bitmap baseImage;
    private OnFragmentInteractionListener mListener;
    private TextView mTextView;

    public FaceEvaluateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FaceEvaluateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FaceEvaluateFragment newInstance(String param1, String param2) {
        FaceEvaluateFragment fragment = new FaceEvaluateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_face_evaluate, container, false);
        mTextView = (TextView) v.findViewById(R.id.happy);
        mImageView = (ImageView) v.findViewById(R.id.imageView2);

        //pull the bitmap(uri) from the intent that started this activity
        Intent photoIntent = getActivity().getIntent();
        Uri uri = photoIntent.getParcelableExtra(EXTRA_FILE_URI);
        //try to grab the bitmap from the URI
        try {
            baseImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            mImageView.setImageBitmap(baseImage);
        }
        catch(IOException e) {
            e.printStackTrace();
            Log.e("CHECK", "File not found");

        }
        //set up an object that can draw on the bitmap, indicating a face was detected
        Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.GREEN);
        myRectPaint.setStyle(Paint.Style.STROKE);
        Bitmap holdCanvas = Bitmap.createBitmap(baseImage.getWidth(),baseImage.getHeight(),Bitmap.Config.RGB_565);
        Canvas drawOn = new Canvas(holdCanvas);
        drawOn.drawBitmap(baseImage, 0, 0, null);

        //Build the face detector.
        //Note that having landmarks enabled and classifications enabled is required to evaluate smiling probability, the key to happiness detection
        FaceDetector faceDetector = new
                FaceDetector.Builder(getActivity()).setTrackingEnabled(false).setClassificationType(ALL_CLASSIFICATIONS).setLandmarkType(ALL_LANDMARKS)
                .build();
        //pop if broken face detector
        if(!faceDetector.isOperational()){
            new AlertDialog.Builder(v.getContext()).setMessage("Could not set up the face detector!").show();

        }
        //Evaluate the array of faces returned from the face detector.

        //This application is focused on one face, but can detect multiple. The last face detected will be the marker for happiness level
        Frame frame = new Frame.Builder().setBitmap(baseImage).build();
        SparseArray<Face> faces = faceDetector.detect(frame);
        for(int i=0; i<faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            drawOn.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);

            float happiness = thisFace.getIsSmilingProbability();
            String hold = "Happiness Level: ";
            mTextView.setText(hold +String.valueOf(happiness));

            // Create a new history point and add it to the History Singleton and FireBase
            History history = new History(happiness);
            HistoryLab.getInstance().addHistory(history);
        }
        // Casting twice so that the parameter is right before we send it to the method.
        //writeHappinessToDatabase(Double.valueOf(String.valueOf(mTextView.getText())));
        mImageView.setImageDrawable(new BitmapDrawable(getActivity().getResources(), holdCanvas));
        if(baseImage!=null)
        {

        }
        //return the inflated view
        return v;
    }

//    private void writeHappinessToDatabase(float happiness) {
//        // Setup
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String key = mDatabaseReference.child("data-point").push().getKey();
//
//        // Building the new DataPoint object for FireBase
//        History fBDataPoint = new History(happiness);
//        Map<String, Object> dataPointValues = fBDataPoint.toMap();
//
//        // How we'll be putting stuff into the database. This object shows the path for where
//        // our new object should be inserted.
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/users/" + user.getUid() + "/data-points/" + key, dataPointValues);
//        mDatabaseReference.updateChildren(childUpdates);
//
//    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
