package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDAO {
    @Insert
    void insertListItem(History h);

    @Query("SELECT * FROM history ORDER BY itemID DESC")
    LiveData<List<History>> getAllItems();

    @Query("DELETE FROM history")
    void clear();



}
