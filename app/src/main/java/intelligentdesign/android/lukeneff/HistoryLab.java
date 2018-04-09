package intelligentdesign.android.lukeneff;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brianlutz on 4/7/18.
 */

public class HistoryLab {
    private static HistoryLab sHistoryLab;
    private static List<History> mHistories;
    private DatabaseReference mDatabase;
    private FirebaseUser mUser;

    /**
     * Get an instance of the HistoryLab class or make a new one if it doesn't exist.
     * @return an instance of the HistoryLab object holding all of the current user's data.
     */
    public static HistoryLab getInstance() {
        if (sHistoryLab == null) {
            sHistoryLab = new HistoryLab();
        }
        return sHistoryLab;
    }

    /**
     * Generic constructor for the HistoryLab singleton class. Pulls all new data from fireBase
     * and makes a local copy for faster loading.
     */
    private HistoryLab() {
        // FireBase initialization
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // List initialization
        mHistories = new ArrayList<>();
    }

    /**
     * Deletes a given history from the cloud and local storage assets.
     * @param history The history to be deleted.
     */
    public void deleteHistory(History history) {
        // NOT YET IMPLEMENTED.
    }

    /**
     * Gets a list of histories from the HistoryLab singleton object.
     * @return a list of all histories, in sync with what's present in remote storage.
     */
    public List<History> getHistories() {
        return mHistories;
    }

    public void getHistoriesAsynchronously(final MainFragment mainFragment) {
        if (mHistories.size() > 0) {
            // Assuming that if its size is greater than zero, it's up to date with the remote DB.
            mainFragment.updateGraph(mHistories);
        } else {
            // Querying data to populate singleton
            Query graphData = mDatabase.child("users").child(mUser.getUid()).child("data-points");

            // Executing on returned query data. This add the History Snapshots to the singleton's list
            // and synchronize with what we have residing on FireBase.
            graphData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        History history = snapshot.getValue(History.class);
                        mHistories.add(history);
                    }
                    mainFragment.updateGraph(mHistories);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
    /**
     * Adds a given History object to the remote FireBase database and the local mHistories copy.
     * @param history the history to be added to both local and cloud stores.
     */
    public void addHistory(History history) {
        // Setup
        String key = mDatabase.child("data-point").push().getKey();

        // Building the new DataPoint object for FireBase
        Map<String, Object> dataPointValues = history.toMap();

        // How we'll be putting stuff into the database. This object shows the path for where
        // our new object should be inserted.
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + mUser.getUid() + "/data-points/" + key, dataPointValues);
        mDatabase.updateChildren(childUpdates);

        // Finally, add it to the local copy of our user history data.
        mHistories.add(history);
    }

    /**
     * De-initializes the HistoryLab singleton object so that when a user signs out their data
     * is removed from local storage in the app.
     */
    public void deleteHistoriesAndDeinitialize() {
        // FireBase connections
        mDatabase = null;
        mUser = null;

        // Local stuff
        mHistories.clear();
        sHistoryLab = null;
    }
}
