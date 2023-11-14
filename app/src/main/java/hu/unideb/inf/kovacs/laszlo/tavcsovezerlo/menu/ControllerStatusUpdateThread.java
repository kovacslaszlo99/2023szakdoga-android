package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

public class ControllerStatusUpdateThread implements Runnable{
    private boolean running = false;
    private MainActivity mainActivity = null;

    @Override
    public void run() {
        while (true) {
            while (running) {
                try {
                    Thread.sleep(2000);
                    myFunction();
                    Thread.sleep(120000); //2 perc
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void init(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public void stop() {
        running = false;
    }

    public void start(){
        running = true;
    }

    private void myFunction() {
        mainActivity.messageInterpretative.sendGetBattery();
        mainActivity.messageInterpretative.sendGetHum();
        mainActivity.messageInterpretative.sendGetTemp();
        mainActivity.messageInterpretative.sendGetCameraStatus();
    }
}
