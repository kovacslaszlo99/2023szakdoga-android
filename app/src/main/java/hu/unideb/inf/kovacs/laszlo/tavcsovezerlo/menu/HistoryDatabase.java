package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {History.class}, version = 3, exportSchema = false)
abstract public class HistoryDatabase extends RoomDatabase {
    public abstract HistoryDAO historyDAO();

    private static volatile HistoryDatabase historyDatabase;
    private static final int NUMBER_OF_THREADS = 3;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    static HistoryDatabase getDatabase(final Context context) {
        if (historyDatabase == null) {
            synchronized (HistoryDatabase.class) {
                if (historyDatabase == null) {
                    historyDatabase = Room.databaseBuilder(context.getApplicationContext(),
                                    HistoryDatabase.class, "telescope_control_database")
                            .build();
                }
            }
        }
        return historyDatabase;
    }
}
