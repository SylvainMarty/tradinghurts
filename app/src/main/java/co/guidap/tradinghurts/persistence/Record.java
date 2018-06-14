package co.guidap.tradinghurts.persistence;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.util.Date;

import co.guidap.tradinghurts.persistence.type.DateTypeConverter;

@Entity(tableName = "record")
public class Record {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @NonNull
    @ColumnInfo(name = "phone_number")
    private String mPhoneNumber;

    @ColumnInfo(name = "conversation")
    private String mConversation;

    @ColumnInfo(name = "date")
    @TypeConverters({DateTypeConverter.class})
    private Date mDate;

    public Record(@NonNull String phoneNumber, Date date) {
        this.mPhoneNumber = phoneNumber;
        this.mDate = date;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    @NonNull
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(@NonNull String phoneNumber) {
        this.mPhoneNumber = phoneNumber;
    }

    public String getConversation() {
        return mConversation;
    }

    public void setConversation(String conversation) {
        this.mConversation = conversation;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }
}
