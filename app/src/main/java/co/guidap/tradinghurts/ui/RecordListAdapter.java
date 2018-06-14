package co.guidap.tradinghurts.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import co.guidap.tradinghurts.R;
import co.guidap.tradinghurts.persistence.Record;

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.RecordViewHolder> {

    private final LayoutInflater mInflater;
    private Callback mCallback;
    private List<Record> mRecords; // Cached copy of records

    public RecordListAdapter(Context context, @NonNull Callback callback) {
        mInflater = LayoutInflater.from(context);
        mCallback = callback;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new RecordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        if (mRecords != null) {
            Record current = mRecords.get(position);
            holder.phoneNumberView.setText(
                    PhoneNumberUtils.formatNumber(current.getPhoneNumber(), Locale.getDefault().getCountry())
            );
            if (current.getDate() != null) {
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.FRANCE);
                holder.dateView.setText(df.format(current.getDate()));
            }
        } else {
            // Covers the case of data not being ready yet.
            holder.phoneNumberView.setText(R.string.no_record_registered);
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

    class RecordViewHolder extends RecyclerView.ViewHolder {
        private final TextView phoneNumberView;
        private final TextView dateView;

        private RecordViewHolder(View itemView) {
            super(itemView);
            phoneNumberView = itemView.findViewById(R.id.phoneNumberView);
            dateView = itemView.findViewById(R.id.dateView);
            itemView.findViewById(R.id.listItemView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = getAdapterPosition();
                    Record record = mRecords.get(itemPosition);
                    mCallback.onRecordSelected(record);
                }
            });
        }
    }

    public interface Callback {

        void onRecordSelected(Record record);

    }
}
