package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel {
    private HistoryRepository historyRepository;
    private final LiveData<List<History>> listLiveData;
    public HistoryViewModel(Application application){
        super(application);
        historyRepository = new HistoryRepository(application);
        listLiveData = historyRepository.getAllHistory();
    }
    public LiveData<List<History>> getAllHistorysFromVm(){
        return listLiveData;
    }
    public void insertHistory(History history){
        historyRepository.insertHistory(history);
    }

    public void clearHistory(){
        historyRepository.clearHistory();
    }
}
