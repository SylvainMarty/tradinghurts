package co.guidap.tradinghurts.persistence;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "record")
public class Record {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int mId;

    @NonNull
    @ColumnInfo(name = "phone_number")
    private String mPhoneNumber;

    @ColumnInfo(name = "conversation")
    private String mConversation;

    public Record(@NonNull String phoneNumber) {
        this.mPhoneNumber = phoneNumber;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
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
}
