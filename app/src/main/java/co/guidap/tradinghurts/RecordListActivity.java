package co.guidap.tradinghurts;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

import co.guidap.tradinghurts.persistence.Record;
import co.guidap.tradinghurts.persistence.RecordViewModel;
import co.guidap.tradinghurts.ui.RecordListAdapter;

public class RecordListActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_GRANTED = 200;

    private RecordViewModel mRecordViewModel;
    private String [] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAPTURE_AUDIO_OUTPUT,
            //Manifest.permission.READ_PRIVILEGED_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_GRANTED);
        setContentView(R.layout.activity_record_list);
        setTitle(R.string.record_list_title);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final RecordListAdapter adapter = new RecordListAdapter(this, new RecordListAdapter.Callback() {
            @Override
            public void onRecordSelected(Record record) {
                Intent intent = new Intent(RecordListActivity.this, RecordDetailsActivity.class);
                intent.putExtra(RecordDetailsActivity.EXTRA_RECORD_IDENTIFIER, record.getId());
                startActivityForResult(intent, RecordDetailsActivity.REQUEST_SHOW_DETAILS);
            }
        });
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
