package co.guidap.tradinghurts.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.guidap.tradinghurts.R;
import co.guidap.tradinghurts.persistence.Record;

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.RecordViewHolder> {

    class RecordViewHolder extends RecyclerView.ViewHolder {
        private final TextView wordItemView;

        private RecordViewHolder(View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.textView);
        }
    }

    private final LayoutInflater mInflater;
    private List<Record> mRecords; // Cached copy of words

    public RecordListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new RecordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecordViewHolder holder, int position) {
        if (mRecords != null) {
            Record current = mRecords.get(position);
            holder.wordItemView.setText(current.getPhoneNumber());
        } else {
            // Covers the case of data not being ready yet.
            holder.wordItemView.setText(R.string.no_record_registered);
        }
    }

    public void setRecords(List<Record> words){
        mRecords = words;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mRecords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mRecords != null)
            return mRecords.size();
        else return 0;
    }

}
