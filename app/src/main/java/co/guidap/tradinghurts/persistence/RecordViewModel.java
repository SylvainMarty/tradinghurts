package co.guidap.tradinghurts.persistence;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class RecordViewModel extends AndroidViewModel {
    private RecordRepository mRepository;
    private LiveData<List<Record>> mAllRecords;

    public RecordViewModel(@NonNull Application application) {
        super(application);
        mRepository = new RecordRepository(application);
        mAllRecords = mRepository.getAllRecords();
    }

    public LiveData<List<Record>> getAllRecords() {
        return mAllRecords;
    }

    public void insert(Record record) {
        mRepository.insert(record);
    }
}
