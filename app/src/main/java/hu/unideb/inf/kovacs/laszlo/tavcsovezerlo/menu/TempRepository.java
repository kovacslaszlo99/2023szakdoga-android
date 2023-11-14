package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TempRepository {

    TempDatabase tempDatabase;
    TempDAO tempDAO;
    private LiveData<List<Temp>> listTemp;

    public LiveData<List<String>> listDays;
    Context app;

    public TempRepository(Application application){
        tempDatabase = TempDatabase.getDatabase(application);
        tempDAO = tempDatabase.tempDAO();
        listTemp = tempDAO.getAllItems();
        listDays = tempDAO.getAllDays();
        app = application;
    }

    public void insertTemp(Temp temp){
        TempDatabase.databaseWriteExecutor.execute(() -> tempDAO.insertListItem(temp));
    }
    public LiveData<List<Temp>> getAllTemp(){
        return listTemp;
    }


    public LiveData<List<Temp>> getTempDay(String day1, String day2){
        return TempDatabase.getDatabase(app).tempDAO().getDay(day1, day2);
    }

    public LiveData<List<Temp>> getTimesDay(String day){
        return TempDatabase.getDatabase(app).tempDAO().getTimesDay(day);
    }

    public LiveData<List<String>> getAllDay(){
        return listDays;
    }

    public void clearTemp(){
        TempDatabase.databaseWriteExecutor.execute(() -> tempDAO.clear());
    }

}
