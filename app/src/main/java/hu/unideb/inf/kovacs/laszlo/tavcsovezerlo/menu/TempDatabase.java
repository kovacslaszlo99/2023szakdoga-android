package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Temp.class}, version = 6, exportSchema = false)
abstract public class TempDatabase extends RoomDatabase {

    public abstract TempDAO tempDAO();

    private static volatile TempDatabase tempDatabase;
    private static final int NUMBER_OF_THREADS = 3;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    static TempDatabase getDatabase(final Context context) {
        if (tempDatabase == null) {
            synchronized (TempDatabase.class) {
                if (tempDatabase == null) {
                    tempDatabase = Room.databaseBuilder(context.getApplicationContext(),
                                    TempDatabase.class, "telescope_control_database_temp")
                            .build();
                }
            }
        }
        return tempDatabase;
    }

}
