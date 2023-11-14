package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class History {

    @PrimaryKey(autoGenerate = true)
    private int itemID;
    private String name;
    private String dateTimeUTC;
    private int UTCOffset;

    @Override
    public String toString() {
        return "{id=" + itemID + ", name=" + name + ", dateTimeUTC=" + dateTimeUTC + ", UTCOffset='" + UTCOffset + "'}\n";
    }

    public int getItemID() {
        return itemID;
    }

    public int getUTCOffset() {
        return UTCOffset;
    }

    public String getDateTimeUTC() {
        return dateTimeUTC;
    }

    public String getName() {
        return name;
    }

    public void setDateTimeUTC(String dateTimeUTC) {
        this.dateTimeUTC = dateTimeUTC;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUTCOffset(int UTCOffset) {
        this.UTCOffset = UTCOffset;
    }
}
