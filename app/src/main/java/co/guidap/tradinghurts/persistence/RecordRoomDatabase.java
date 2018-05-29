package co.guidap.tradinghurts.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

@Database(entities = {Record.class}, version = 1)
public abstract class RecordRoomDatabase extends RoomDatabase {

    public abstract RecordDao recordDao();

    private static RecordRoomDatabase INSTANCE;

    public static RecordRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RecordRoomDatabase.class) {
                if (INSTANCE == null) {
                    //Room.inMemoryDatabaseBuilder(context, RecordRoomDatabase.class)
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        RecordRoomDatabase.class, "record_database"
                    )
                    .addMigrations(getMigrations())
                    .build();
                }
            }
        }
        return INSTANCE;
    }

    private static Migration[] getMigrations() {
        return new Migration[]{};
    }

}
