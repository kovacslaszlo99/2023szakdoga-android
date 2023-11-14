package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TempDAO {

    @Insert
    void insertListItem(Temp t);

    @Query("SELECT * FROM `temp`")
    LiveData<List<Temp>> getAllItems();

    @Query("SELECT * FROM `temp` WHERE dateUTC LIKE :day1 OR dateUTC LIKE :day2")
    LiveData<List<Temp>> getDay(String day1, String day2);

    @Query("SELECT * FROM `temp` WHERE dateUTC LIKE :day")
    LiveData<List<Temp>> getTimesDay(String day);

    @Query("SELECT dateUTC FROM `temp` GROUP BY dateUTC ORDER BY dateUTC DESC")
    LiveData<List<String>> getAllDays();

    @Query("DELETE FROM `temp`")
    void clear();
}
