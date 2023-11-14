package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Temp {
    @PrimaryKey(autoGenerate = true)
    private int itemID;

    private String dateUTC;
    private String timeUTC;
    private int UTCOffset;
    private double temp;
    private double hum;

    @Override
    public String toString() {
        return "{id=" + itemID + ", temp=" + temp + ", hum=" + hum + ", dateUTC=" + dateUTC + ", timeUTC=" + timeUTC + ", UTCOffset='" + UTCOffset + "'}\n";
    }

    public String getDateUTC() {
        return dateUTC;
    }

    public String getTimeUTC() {
        return timeUTC;
    }

    public void setDateUTC(String dateUTC) {
        this.dateUTC = dateUTC;
    }

    public void setTimeUTC(String timeUTC) {
        this.timeUTC = timeUTC;
    }

    public void setUTCOffset(int UTCOffset) {
        this.UTCOffset = UTCOffset;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public void setHum(double hum) {
        this.hum = hum;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public int getUTCOffset() {
        return UTCOffset;
    }


    public int getItemID() {
        return itemID;
    }

    public double getHum() {
        return hum;
    }

    public double getTemp() {
        return temp;
    }

}
