package co.guidap.tradinghurts.callrecorder;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Date;

import co.guidap.tradinghurts.persistence.Record;
import co.guidap.tradinghurts.persistence.RecordRepository;

public class RecordPersistenceHandler extends Handler {

    private static final String TAG = "RecordPersisterThread";
    private RecordRepository mRecordRepository;
    private Record mRecord;

    public RecordPersistenceHandler(Looper looper, Context context) {
        super(looper);
        mRecordRepository = new RecordRepository(context);
    }

    @Override
    public void handleMessage(Message msg) {
        // Si le record n'existe pas, on le créé en base
        if (mRecord == null) {
            String phoneNumber = (String) msg.obj;
            mRecord = new Record(phoneNumber, new Date());
            long id = mRecordRepository.insert(mRecord);
            // On met à jour son id pour qu'il soit reconnu
            // comme une ligne existante dans la table
            mRecord.setId(id);
            return;
        }

        String newConversation = (String) msg.obj;
        mRecord.setConversation(newConversation);
        mRecordRepository.update(mRecord);
    }
}
