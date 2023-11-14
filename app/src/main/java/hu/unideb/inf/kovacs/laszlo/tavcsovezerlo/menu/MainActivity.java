package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.SET_ALARM;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Context ct;
    public double longitude, latitude;
    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    public MainFragment mainFragment;
    private RecommendFragment recommendFragment;
    public CometFragment cometFragment;
    public CameraFragment cameraFragment;
    public InfoFragment infoFragment;
    public HistoryFragment historyFragment;
    public SettingsFragment settingsFragment;
    private TempFragment tempFragment;
    private Fragment selectedFragment = null;
    public Bluetooth mChat = null;
    private HandlerThread handlerThread;
    private Handler mHandler;
    public MessageInterpretative messageInterpretative;
    public ControllerStatusUpdateThread controllerStatusUpdateThread;
    public ControllerFirstConnectStatusUpdateThread controllerFirstConnectStatusUpdateThread;
    public Thread controllerStatusUpdateThreadThread;
    public Thread controllerFirstConnectStatusUpdateThreadThread;
    public HistoryViewModel historyViewModel = null;
    public TempViewModel tempViewModel = null;
    public boolean firsConnect = true;
    public boolean tracking = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        ct = this;

        historyViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(HistoryViewModel.class);
        tempViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(TempViewModel.class);

        mainFragment = new MainFragment(ct);
        recommendFragment = new RecommendFragment(ct);
        historyFragment = new HistoryFragment(ct);
        settingsFragment = new SettingsFragment(ct, longitude, latitude);
        tempFragment = new TempFragment(ct);
        cometFragment = new CometFragment(ct);
        cameraFragment = new CameraFragment(ct);
        infoFragment = new InfoFragment(ct);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mainFragment)
                .commit();
        setSubTitle(mainFragment.toString());
        messageInterpretative = new MessageInterpretative(this, this);

        handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper()){
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case Bluetooth.STATE_CONNECTED:
                                runOnUiThread(() -> {
                                    mainFragment.setStatusTextView("Kapcsolódva", Color.GREEN);
                                    mainFragment.setConButton("Kapcsolat bontása");
                                    controllerStatusUpdateThread.start();
                                    if(firsConnect){
                                        controllerFirstConnectStatusUpdateThread.start();
                                        firsConnect = false;
                                    }
                                });
                                break;
                            case Bluetooth.STATE_CONNECTING:
                                runOnUiThread(() -> mainFragment.setStatusTextView("Kapcsolódás...", Color.BLUE));
                                break;
                            case Bluetooth.STATE_LISTEN:
                                break;
                            case Bluetooth.STATE_NONE:
                                runOnUiThread(() -> {
                                    mainFragment.setStatusTextView("Nincs kapcsólat", Color.RED);
                                    mainFragment.setConButton("Kapcsolodás");
                                    controllerStatusUpdateThread.stop();
                                });
                                break;
                        }
                        break;
                    case Constants.MESSAGE_WRITE:
                        byte[] writeBuf = (byte[]) msg.obj;
                        String writeMessage = new String(writeBuf);
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Me: " + writeMessage, Toast.LENGTH_SHORT));
                        break;
                    case Constants.MESSAGE_READ:
                        StringBuilder stringBuilder = (StringBuilder) msg.obj;
                        String readMessage = stringBuilder.toString();
                        runOnUiThread(() -> {
                            //Toast.makeText(getApplicationContext(), "Telescop: " + readMessage, Toast.LENGTH_SHORT).show();
                            messageInterpretative.receive(readMessage);
                        });
                        break;
                }
            }
        };
        String[] PERMISSIONS = {
                BLUETOOTH,
                BLUETOOTH_ADMIN,
                BLUETOOTH_CONNECT,
                BLUETOOTH_SERVICE,
                BLUETOOTH_SCAN,
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION,
                INTERNET,
                SET_ALARM
        };
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, ALL_PERMISSIONS_RESULT);
        }else{
            //btOn();
            gpsGetData();
        }
        mChat = new Bluetooth(this, mHandler);
        mainFragment.init(this);
        settingsFragment.init(this);
        historyFragment.init(this);
        recommendFragment.init(this);
        tempFragment.init(this);
        cameraFragment.init(this);
        cometFragment.init(this);
        infoFragment.init(this);
        controllerStatusUpdateThread = new ControllerStatusUpdateThread();
        controllerStatusUpdateThread.init(this);
        controllerStatusUpdateThreadThread = new Thread(controllerStatusUpdateThread);
        controllerStatusUpdateThreadThread.start();
        controllerFirstConnectStatusUpdateThread = new ControllerFirstConnectStatusUpdateThread();
        controllerFirstConnectStatusUpdateThread.init(this);
        controllerFirstConnectStatusUpdateThreadThread = new Thread(controllerFirstConnectStatusUpdateThread);
        controllerFirstConnectStatusUpdateThreadThread.start();

        historyViewModel.getAllHistorysFromVm().observe(this, histortys ->
        {
            historyFragment.historyTests.clear();
            if (histortys != null && !histortys.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                for (History item : histortys) {
                    LocalDateTime dateTime = LocalDateTime.parse(item.getDateTimeUTC(), formatter);
                    LocalDateTime newDateTime = dateTime.plusHours(item.getUTCOffset());
                    String outputDateTime = newDateTime.format(outputFormatter);
                    historyFragment.historyTests.add(new HistoryTest(item.getName(), outputDateTime, item.getDateTimeUTC()));
                }
            }
        });

        tempViewModel.getAllDays().observe(this, dates -> {
            if (dates != null && !dates.isEmpty()){
                tempFragment.dateList.clear();
                tempFragment.dateList.addAll(dates);
            }
        });

    }

    public void setBattery(Float level){
        mainFragment.setBatteryValue(level);
    }

    public void setTemp(Double temp){
        mainFragment.setTempValue(temp);
    }

    public void setHum(Double hum){
        mainFragment.setHumValue(hum);
    }

    public void setObj(String s){
        mainFragment.setObj(s);
    }

    public void setCoordinate(String s){
        mainFragment.setCoordinate(s);
    }

    public void setTime(String telescope, String phone){
        settingsFragment.setTime(telescope, phone);
    }

    public String[] getStarCon(){
        return new String[]{"and", "ant", "aps", "aql", "aqr", "ara", "ari", "aur", "boo", "cae", "cam", "cap", "car", "cas", "cen", "cep", "cet", "cha", "cir", "cma", "cmi", "cnc", "col", "com", "cra", "crb", "crt", "cru", "crv", "cvn", "cyg", "del", "dor", "dra", "equ", "eri", "for", "gem", "gru", "her", "hor", "hya", "hyi", "ind", "lac", "leo", "lep", "lib", "lmi", "lup", "lyn", "lyr", "men", "mic", "mon", "mus", "nor", "oct", "oph", "ori", "pav", "peg", "per", "phe", "pic", "psa", "psc", "pup", "pyx", "ret", "scl", "sco", "sct", "ser", "sex", "sge", "sgr", "tau", "tel", "tra", "tri", "tuc", "uma", "umi", "vel", "vir", "vol", "vul"};
    }

    public int getUTCOffest(){
        TimeZone timeZone = TimeZone.getDefault();
        Calendar calendar = Calendar.getInstance();
        boolean isDST = timeZone.inDaylightTime(calendar.getTime());
        int offsetInMillis = timeZone.getRawOffset();
        if (isDST) {
            offsetInMillis += timeZone.getDSTSavings();
        }
        return offsetInMillis / (60 * 60 * 1000);
    }

    @SuppressLint("SimpleDateFormat")
    public String getNowDateTimeUTC(boolean ardu, boolean second){
        Date currentDate = new Date();
        SimpleDateFormat dateFormat;
        if (!ardu) {
            if (second) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }else {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            }
        }else {
            dateFormat = new SimpleDateFormat("yy,MM,dd,HH,mm,ss");
        }
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(currentDate);
    }

    @SuppressLint("SimpleDateFormat")
    public String getNowDateTime(boolean second){
        Date currentDate = new Date();
        SimpleDateFormat dateFormat;
        if(second) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        else {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(currentDate);
    }

    @SuppressLint("SimpleDateFormat")
    public int getNowMount(){
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        dateFormat.setTimeZone(TimeZone.getDefault());
        return Integer.parseInt(dateFormat.format(currentDate));
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void gpsGetData(){
        locationTrack = new LocationTrack(ct);
        if (locationTrack.canGetLocation()) {
            longitude = locationTrack.getLongitude();
            latitude = locationTrack.getLatitude();
            locationTrack.stopListener();
            settingsFragment.longitude = this.longitude;
            settingsFragment.latitude = this.latitude;
        } else {
            locationTrack.showSettingsAlert();
        }
    }

    public void btCon(){
        btOn();
        if (mChat == null) {
            mChat = new Bluetooth(this, mHandler);
        }
        if (mChat.getState() == Bluetooth.STATE_NONE) {
            mChat.con_telescope();
        }else {
            mChat.disconect();
        }
    }

    public void btSend(String s){
        if (mChat != null && mChat.getState() == Bluetooth.STATE_CONNECTED) {
            mChat.write((s + "\r\n").getBytes(StandardCharsets.UTF_8));
        } else {
            Toast.makeText(getApplicationContext(), "Nincs kapcsolat", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //btOn();
            gpsGetData();
        }
    }

    private void btOn() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            Intent bluetoothRequestIntent = new Intent(this, BluetoothRequestActivity.class);
            startActivity(bluetoothRequestIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChat != null) {
            mChat.stop();
        }
        handlerThread.quitSafely();
        locationTrack.stopListener();
        controllerStatusUpdateThread.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mChat != null) {
            if (mChat.getState() == Bluetooth.STATE_NONE) {
                mChat.start();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setSubTitle(String s){
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(s);
    }

    @SuppressLint("NonConstantResourceId")
    public void onGroupItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_control){
            selectedFragment = mainFragment;
        }else if (id == R.id.nav_recommend) {
            selectedFragment = recommendFragment;
        }else if (id == R.id.nav_temp) {
            selectedFragment = tempFragment;
        } else if (id == R.id.nav_history) {
            selectedFragment = historyFragment;
        } else if (id == R.id.nav_settings) {
            selectedFragment = settingsFragment;
        } else if (id == R.id.nav_comet){
            selectedFragment = cometFragment;
        } else if(id == R.id.nav_photo){
            selectedFragment = cameraFragment;
        } else if(id == R.id.nav_info){
            selectedFragment = infoFragment;
        }

        if (selectedFragment != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            setSubTitle(selectedFragment.toString());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }
    }
}