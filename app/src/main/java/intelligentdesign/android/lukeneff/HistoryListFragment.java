package intelligentdesign.android.lukeneff;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import java.util.List;

import static android.text.format.DateFormat.*;

/**
 * Created by brianlutz on 4/4/18.
 */

public class HistoryListFragment extends Fragment {

    // RecyclerView Components
    private RecyclerView mHistoryRecyclerView;
    private HistoryAdapter mHistoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("HistoryListView", "Entered");
        View v = inflater.inflate(R.layout.fragment_history_list, container, false);

        mHistoryRecyclerView = (RecyclerView) v.findViewById(R.id.history_recycler_view);
        mHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();

        return v;

    }

    private void updateUI() {
        List<History> histories = HistoryLab.getInstance().getHistories();
        if (histories.size() > 0) {
            mHistoryAdapter = new HistoryAdapter(histories);
            mHistoryRecyclerView.setAdapter(mHistoryAdapter);
        }
    }

    private class HistoryHolder extends RecyclerView.ViewHolder {
        private History mHistory;
        private TextView mHistoryDateTextView;
        private TextView mHistoryScoreTextView;

        public HistoryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_history, parent, false));

            mHistoryDateTextView = (TextView) itemView.findViewById(R.id.history_date);
            mHistoryScoreTextView = (EditText) itemView.findViewById(R.id.history_score);
        }

        public void bind(History history) {
            mHistory = history;

            String formattedDate = format("hh:mm a \nEEE, MMM d yyyy",
                    history.getDate()).toString();
            String formattedScore = Float.toString(history.getHappiness());

            mHistoryDateTextView.setText(formattedDate);
            mHistoryScoreTextView.setText(formattedScore);

            mHistoryScoreTextView.setEnabled(false);
        }
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryHolder> {

        private List<History> mHistories;

        public HistoryAdapter(List<History> histories) {
            mHistories = histories;
        }

        @Override
        public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new HistoryHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(HistoryHolder holder, int position) {
            History history = mHistories.get(position);
            holder.bind(history);
        }

        @Override
        public int getItemCount() {
            return mHistories.size();
        }
    }
}
