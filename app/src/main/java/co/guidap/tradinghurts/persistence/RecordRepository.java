package co.guidap.tradinghurts.persistence;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

public class RecordRepository {

    private RecordDao mRecordDao;
    private LiveData<List<Record>> mAllRecords;

    public RecordRepository(Context context) {
        RecordRoomDatabase db = RecordRoomDatabase.getDatabase(context);
        mRecordDao = db.recordDao();
        mAllRecords = mRecordDao.getAllRecords();
    }
    public void insert (Record record) {
        new InsertAsyncTask(mRecordDao).execute(record);
    }

    public void update (Record record) {
        new UpdateAsyncTask(mRecordDao).execute(record);
    }

    public LiveData<List<Record>> getAllRecords() {
        return mAllRecords;
    }

    private static class InsertAsyncTask extends AsyncTask<Record, Void, Void> {

        private RecordDao mAsyncTaskDao;

        InsertAsyncTask(RecordDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Record... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<Record, Void, Void> {

        private RecordDao mAsyncTaskDao;

        UpdateAsyncTask(RecordDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Record... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }
}
