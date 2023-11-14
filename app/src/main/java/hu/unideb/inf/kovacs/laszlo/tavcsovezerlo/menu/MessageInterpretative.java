package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import android.annotation.SuppressLint;
import android.content.Context;

public class MessageInterpretative {

    private final MainActivity mainActivity;
    public final Context ct;

    public MessageInterpretative(Context ct, MainActivity mainActivity){
        this.mainActivity = mainActivity;
        this.ct = ct;
    }

    public void receive(String s){
        String row = s.replace("\n", "");
        String[] data = row.split(";");
        switch (data[0]) {
            case "battery":
                float level = Float.parseFloat(data[1]);
                mainActivity.setBattery(level);
                break;
            case "temp": {
                Double temp = Double.parseDouble(data[1]);
                mainActivity.setTemp(temp);
                break;
            }
            case "hum": {
                Double hum = Double.parseDouble(data[1]);
                mainActivity.setHum(hum);
                break;
            }
            case "obj":
                String[] obj = data[1].split(",");
                switch (obj[0]) {
                    case "m":
                        mainActivity.setObj(obj[0].toUpperCase() + Integer.parseInt(obj[1]));
                        break;
                    case "ngc":
                    case "ic":
                        mainActivity.setObj(obj[0].toUpperCase() + obj[1]);
                        break;
                    case "mel":
                    case "tr":
                    case "cr":
                        String cat = obj[0].substring(0, 1).toUpperCase() + obj[0].substring(1);
                        mainActivity.setObj(cat + Integer.parseInt(obj[1]));
                        break;
                    case "coor":
                        mainActivity.setObj(obj[1] + ", " + obj[2]);
                        break;
                    default:
                        mainActivity.setObj(obj[1]);
                        break;
                }
                break;
            case "coordinate":
                char c = 65474;
                char d = 65456;
                mainActivity.setCoordinate(data[1].replace(c +""+ d, "°"));
                break;
            case "datetime":
                String now = mainActivity.getNowDateTimeUTC(false, true);
                mainActivity.setTime(data[1], now);
                break;
            case "data": {
                //2023-06-12 12:30:45,21.5,51.5,2
                String[] record = data[1].split(",");
                String dateTime = record[0];
                Double temp = Double.parseDouble(record[1]);
                Double hum = Double.parseDouble(record[2]);
                Integer utc = Integer.parseInt(record[3]);
                addTempDbItem(dateTime, temp, hum, utc);
                break;
            }
            case "get":{
                switch (data[1]){
                    case "gps":
                        sendGPSData(mainActivity.longitude, mainActivity.latitude);
                        break;
                    case "datetime":
                        sendNowDateTime();
                        break;
                }
                break;
            }
            case "tracking":{
                boolean new_trackin_status = data[1].equals("on");
                mainActivity.tracking = new_trackin_status;
                mainActivity.mainFragment.setTrackingSwitch(new_trackin_status);
                break;
            }
            case "camera":{
                switch (data[1]) {
                    case "expo":
                        double expo = Double.parseDouble(data[2]);
                        mainActivity.cameraFragment.setCameraExpoTime(expo);
                        break;
                    case "sleep":
                        int sleep = Integer.parseInt(data[2]);
                        mainActivity.cameraFragment.setCameraSleepTime(sleep);
                        break;
                    case "piece":
                        int piece = Integer.parseInt(data[2]);
                        mainActivity.cameraFragment.setCameraPieceImage(piece);
                        break;
                    case "start":
                        mainActivity.cameraFragment.setStatus(true,Integer.parseInt(data[2]));
                        break;
                    case "stop":
                        mainActivity.cameraFragment.setStatus(false, Integer.parseInt(data[2]));
                        break;
                    case "mirrorlookup":
                        int offset = Integer.parseInt(data[2]);
                        mainActivity.cameraFragment.setCameraOffset(offset);
                        break;
                    case "starttime":
                        if(data.length >= 3) {
                            String startTime = data[2];
                            mainActivity.cameraFragment.setCameraStartTime(startTime);
                        }
                        break;
                }
                break;
            }
            case "currentgoto":
                mainActivity.settingsFragment.setGotoCurrent(Integer.parseInt(data[1]));
                break;
            case "currenttracking":
                mainActivity.settingsFragment.setTrackingCurrent(Integer.parseInt(data[1]));
                break;
            case "trackingdir":
                mainActivity.settingsFragment.setTrackingDIR((data[1].equals("true")));
                break;
            case "radir":
                mainActivity.settingsFragment.setRaDIR(data[1].equals("true"));
                break;
            case "decdir":
                mainActivity.settingsFragment.setDecDIR(data[1].equals("true"));
                break;
            case "timesiftdir":
                mainActivity.settingsFragment.setTimeSiftDIR(data[1].equals("true"));
                break;
        }
    }

    public void sendNowDateTime(){
        String now = mainActivity.getNowDateTimeUTC(true, true);
        String sendData = "datetime;" + now;
        mainActivity.btSend(sendData);
    }

    public void sendGPSData(Double longitude, Double latitude){
        int offsetInHours = mainActivity.getUTCOffest();
        String sendData = "gps;" + latitude + "," + longitude + "," + offsetInHours;
        mainActivity.btSend(sendData);
    }

    public void addTempDbItem(String dateTime, Double temp, Double hum, Integer utc){
        Temp tempModel = new Temp();
        tempModel.setTemp(temp);
        tempModel.setHum(hum);
        String[] dateTimeArray = dateTime.split(" ");
        tempModel.setDateUTC(dateTimeArray[0]);
        tempModel.setTimeUTC(dateTimeArray[1]);
        tempModel.setUTCOffset(utc);
        mainActivity.tempViewModel.insertTemp(tempModel);
    }

    public void addHistoryDbItem(String name){
        History history = new History();
        history.setName(name);
        history.setDateTimeUTC(mainActivity.getNowDateTimeUTC(false,false));
        history.setUTCOffset(mainActivity.getUTCOffest());
        mainActivity.historyViewModel.insertHistory(history);
    }

    @SuppressLint("DefaultLocale")
    public void sendObjHub(String obj){
        if(mainActivity.mChat.getState() == Bluetooth.STATE_CONNECTED) {
            addHistoryDbItem(obj);
            mainActivity.tracking = true;
            sendTrackingStatusUpdate(true);
            mainActivity.mainFragment.setTrackingSwitch(true);
            mainActivity.mainFragment.setObj(obj);
        }
        String data = obj.toLowerCase();
        if(stringContainsItemFromList(data, mainActivity.getStarCon())){
            sendObj("vstar", data);
        }else if(data.contains("mel")){
            String[] catalog = data.split("mel");
            sendObj("mel", String.format("%03d", Integer.parseInt(catalog[1])));
        }else if(data.contains("m")){
            String[] catalog = data.split("m");
            sendObj("m", String.format("%03d", Integer.parseInt(catalog[1])));
        }else if(data.contains("ngc")){
            String[] catalog = data.split("ngc");
            sendObj("ngc", String.format("%04d", Integer.parseInt(catalog[1])));
        }else if(data.contains("ic")){
            String[] catalog = data.split("ic");
            sendObj("ic", String.format("%04d", Integer.parseInt(catalog[1])));
        }else if(data.contains("b")){
            String[] catalog = data.split("b");
            sendObj("b", String.format("%03d", Integer.parseInt(catalog[1])));
        }else if(data.contains("cr")){
            String[] catalog = data.split("cr");
            sendObj("cr", String.format("%03d", Integer.parseInt(catalog[1])));
        }else{
            sendObj("coor", data);
        }
    }

    public static boolean stringContainsItemFromList(String inputStr, String[] items)
    {
        for(int i =0; i < items.length; i++)
        {
            if(inputStr.contains(items[i]))
            {
                return true;
            }
        }
        return false;
    }

    private void sendObj(String type, String name){
        String sendData = "obj;" + type + "," + name;
        mainActivity.btSend(sendData);
        sendGetCoordinata();
    }

    public void sendCamera(double expo, int sleep, int piece, int offset, String starTime){
        String sendExpoData = "camera;expo;" + expo;
        String sendSleepData = "camera;sleep;" + sleep;
        String sendPieceData = "camera;piece;" + piece;
        String sendOffsetData = "camera;mirrorlookup;" + offset;
        String sendStartTimeData = "camera;starttime;" + starTime;
        mainActivity.btSend(sendExpoData);
        mainActivity.btSend(sendSleepData);
        mainActivity.btSend(sendPieceData);
        mainActivity.btSend(sendOffsetData);
        mainActivity.btSend(sendStartTimeData);
    }

    public void sendGetCurrentGOTO(){
        sendGet("currentgoto");
    }

    public void sendGetCurrentTracking(){
        sendGet("currenttracking");
    }

    public void sendGetTrackingDIR(){
        sendGet("trackingdir");
    }

    public void sendGetRaDIR(){
        sendGet("radir");
    }

    public void sendGetDecDIR(){
        sendGet("decdir");
    }

    public void sendGetTimeSiftDIR(){
        sendGet("timesiftdir");
    }

    public void sendCurrentGOTO(int current){
        sendSet("currentgoto;" + current);
    }

    public void sendCurrentTracking(int current){
        sendSet("currenttracking;" + current);
    }

    public void sendTrackingDIR(boolean dir){
        sendSet("trackingdir;" + ((dir)? "true": "false"));
    }

    public void sendRaDIR(boolean dir){
        sendSet("radir;" + ((dir)? "true": "false"));
    }

    public void sendDecDIR(boolean dir){
        sendSet("decdir;" + ((dir)? "true": "false"));
    }

    public void sendTimeSiftDIR(boolean dir){
        sendSet("timesiftdir;" + ((dir)? "true": "false"));
    }


    private void sendSet(String set){
        mainActivity.btSend("set;" + set);
    }

    public void sendCameraStatus(boolean status){
        String sendData = "camera;" + ((status)? "start" : "stop");
        mainActivity.btSend(sendData);
    }

    public void sendManual(String type, String dir, boolean status){
        String sendData = "manual;" + type + "," + dir + "," + ((status)? "on": "off");
        mainActivity.btSend(sendData);
    }

    public void sendManualSpeed(int speed){
        String sendData = "manual;speed," + speed;
        mainActivity.btSend(sendData);
    }

    public void sendTrackingStatusUpdate(boolean tracking){
        String sendData = "tracking;" + ((tracking)? "on" : "off");
        mainActivity.btSend(sendData);
    }

    public void sendGetCameraMirror(){
        sendGet("camera,mirrorlookup");
    }
    public void sendGetCameraStartTime(){
        sendGet("camera,starttime");
    }
    public void sendGetCameraExpo(){
        sendGet("camera,expo");
    }
    public void sendGetCameraSleep(){
        sendGet("camera,sleep");
    }
    public void sendGetCameraPiece(){
        sendGet("camera,piece");
    }
    public void sendGetCameraStatus(){
        sendGet("camera,status");
    }

    public void sendGetNowDateTime(){
        sendGet("datetime");
    }

    public void sendGetTracking(){
        sendGet("tracking");
    }

    public void sendGetTemp(){
        sendGet("temp");
    }

    public void sendGetHum(){
        sendGet("hum");
    }

    public void sendGetBattery(){
        sendGet("battery");
    }

    public void sendGetObj(){
        sendGet("obj");
    }

    public void sendGetCoordinata(){
        sendGet("coordinata");
    }

    public void sendGetData(String lastTime){
        sendGet("data;" + lastTime);
    }

    public void sendGetData(){
        sendGet("data;all");
    }

    private void sendGet(String type){
        String sendData = "get;" + type;
        mainActivity.btSend(sendData);
    }
}
