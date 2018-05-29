package co.guidap.tradinghurts.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface RecordDao {
    @Insert
    void insert(Record word);

    @Update
    void update(Record word);

    @Query("DELETE FROM record")
    void deleteAll();

    @Query("SELECT * from record ORDER BY id DESC")
    LiveData<List<Record>> getAllRecords();

    @Query("SELECT * from record WHERE id = :id")
    LiveData<Record> getRecord(int id);
}
