package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

public class ControllerFirstConnectStatusUpdateThread implements Runnable{
    private boolean running = false;
    private MainActivity mainActivity = null;

    @Override
    public void run() {
        while (true) {
            while (running) {
                try {
                    Thread.sleep(2000);
                    myFunction();
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
        mainActivity.messageInterpretative.sendGetObj();
        mainActivity.messageInterpretative.sendGetCoordinata();
        mainActivity.messageInterpretative.sendGetTracking();
        mainActivity.messageInterpretative.sendGetCameraExpo();
        mainActivity.messageInterpretative.sendGetCameraPiece();
        mainActivity.messageInterpretative.sendGetCameraSleep();
        mainActivity.messageInterpretative.sendGetCameraStatus();
        mainActivity.messageInterpretative.sendGetCameraMirror();
        mainActivity.messageInterpretative.sendGetCameraStartTime();
        mainActivity.messageInterpretative.sendGetCurrentGOTO();
        mainActivity.messageInterpretative.sendGetCurrentTracking();
        mainActivity.messageInterpretative.sendGetTrackingDIR();
        mainActivity.messageInterpretative.sendGetRaDIR();
        mainActivity.messageInterpretative.sendGetDecDIR();
        mainActivity.messageInterpretative.sendGetTimeSiftDIR();
        stop();
    }
}
