package co.guidap.tradinghurts;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import co.guidap.tradinghurts.R;
import co.guidap.tradinghurts.persistence.Record;
import co.guidap.tradinghurts.persistence.RecordViewModel;

public class RecordDetailsActivity extends AppCompatActivity {

    final public static int REQUEST_SHOW_DETAILS = 0;
    final public static String EXTRA_RECORD_IDENTIFIER = "record_identifier";

    private RecordViewModel mRecordViewModel;
    private TextView mPhoneNumberView;
    private TextView mDateView;
    private TextView mConversationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_details);
        setTitle(R.string.record_details_title);

        mPhoneNumberView = findViewById(R.id.phoneNumber);
        mDateView = findViewById(R.id.date);
        mConversationView = findViewById(R.id.conversation);
        mRecordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);

        mRecordViewModel.getRecord(getIntent().getIntExtra(EXTRA_RECORD_IDENTIFIER, 0)).observe(this, new Observer<Record>() {
            @Override
            public void onChanged(@Nullable final Record record) {
                if (record == null) {
                    Toast.makeText(RecordDetailsActivity.this, RecordDetailsActivity.this.getString(R.string.record_not_found), Toast.LENGTH_SHORT).show();
                    RecordDetailsActivity.this.finish();
                }

                mPhoneNumberView.setText(
                        PhoneNumberUtils.formatNumber(record.getPhoneNumber(), Locale.getDefault().getCountry())
                );
                
                if (record.getDate() != null) {
                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.FRANCE);
                    mDateView.setText(df.format(record.getDate()));
                }

                mConversationView.setText(record.getConversation());
            }
        });
    }
}
