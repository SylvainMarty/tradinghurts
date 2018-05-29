package co.guidap.tradinghurts;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import co.guidap.tradinghurts.persistence.Record;
import co.guidap.tradinghurts.persistence.RecordViewModel;
import co.guidap.tradinghurts.ui.RecordListAdapter;

public class RecordListActivity extends AppCompatActivity {

    private RecordViewModel mRecordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final RecordListAdapter adapter = new RecordListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);
        mRecordViewModel.getAllRecords().observe(this, new Observer<List<Record>>() {
            @Override
            public void onChanged(@Nullable final List<Record> records) {
                // Update the cached copy of the records in the adapter.
                adapter.setRecords(records);
            }
        });
    }
}
