package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class HistoryRepository {
    HistoryDatabase historyDatabase;
    HistoryDAO historyDAO;
    private LiveData<List<History>> listHistory;

    public HistoryRepository(Application application){
        historyDatabase = HistoryDatabase.getDatabase(application);
        historyDAO = historyDatabase.historyDAO();
        listHistory = historyDAO.getAllItems();
    }

    public void insertHistory(History history){
        HistoryDatabase.databaseWriteExecutor.execute(() -> historyDAO.insertListItem(history));
    }
    public LiveData<List<History>> getAllHistory(){
        return listHistory;
    }

    public void clearHistory(){
        HistoryDatabase.databaseWriteExecutor.execute(() -> historyDAO.clear());
    }
}
