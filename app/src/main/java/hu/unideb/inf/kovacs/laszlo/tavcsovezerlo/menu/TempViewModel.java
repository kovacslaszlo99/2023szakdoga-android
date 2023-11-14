package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TempViewModel extends AndroidViewModel {
    private TempRepository tempRepository;
    private final LiveData<List<Temp>> listLiveData;
    public TempViewModel(Application application){
        super(application);
        tempRepository = new TempRepository(application);
        listLiveData = tempRepository.getAllTemp();
    }
    public LiveData<List<Temp>> getAllTempsFromVm(){
        return listLiveData;
    }
    public void insertTemp(Temp temp){
        tempRepository.insertTemp(temp);
    }

    public LiveData<List<Temp>> getDay(String day1, String day2){
        return tempRepository.getTempDay(day1, day2);
    }

    public LiveData<List<Temp>> getTimesDay(String day){
        return tempRepository.getTimesDay(day);
    }

    public LiveData<List<String>> getAllDays(){
        return tempRepository.getAllDay();
    }

    public void clearTemp(){
        tempRepository.clearTemp();
    }
}
