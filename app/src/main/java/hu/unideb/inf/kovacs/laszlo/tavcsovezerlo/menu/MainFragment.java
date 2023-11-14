package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class MainFragment extends Fragment {
    private final Context ct;
    public TextView con_status;
    public Button conButton;

    public String statusTextViewText;

    public Integer statusTextViewColor;

    public String conButtonText;

    private MainActivity mainActivity = null;

    public Float battery = null;

    public TextView batteryTextView;
    public TextView tempTextView;
    public TextView humTextView;
    public TextView objTextView;
    public TextView coordinateTextView;

    public Button inditButton;
    public Button felButton;
    public Button leButton;
    public Button jobbraButton;
    public Button balraButton;

    public SeekBar setSpeedSeekBar;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public Switch trackingSwitch;

    public AutoCompleteTextView searchAutoCompleteTextView;

    public String coordinate = null;
    public Double temp = null;
    public Double hum = null;
    public String obj = null;

    public ArrayList<String> objList = new ArrayList<>();

    public MainFragment(Context ct) {
        this.ct = ct;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setStatusTextView(String s, int color){
        if(con_status != null) {
            con_status.setText(s);
            con_status.setTextColor(color);
        }
        statusTextViewText = s;
        statusTextViewColor = color;

    }

    @SuppressLint("SetTextI18n")
    public void setBatteryValue(Float level){
        if(batteryTextView != null){
            batteryTextView.setText(level + "V");
        }
        battery = level;
    }

    @SuppressLint("SetTextI18n")
    public void setTempValue(Double temp){
        if(tempTextView != null){
            tempTextView.setText(temp + "°C");
        }
        this.temp = temp;
    }

    @SuppressLint("SetTextI18n")
    public void setHumValue(Double hum){
        if(humTextView != null){
            humTextView.setText(hum + "%");
        }
        this.hum = hum;
    }


    public void setConButton(String s){
        if(conButton != null) {
            conButton.setText(s);
        }
        conButtonText = s;
    }

    public void setObj(String s){
        if(objTextView != null){
            objTextView.setText(s);
        }
        obj = s;
    }

    public void setCoordinate(String s){
        if(coordinateTextView != null){
            coordinateTextView.setText(s);
        }
        coordinate = s;
    }

    public void init(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @SuppressLint("DefaultLocale")
    public void generalObjList(){
        for (int i = 1; i <= 110; i++) {
            objList.add("M"+i);
        }
        for (int i = 1; i <= 7840; i++){
            objList.add("NGC"+i);
        }
        for (int i = 1; i <= 6386; i++){
            objList.add("IC"+i);
        }
        for (int i = 1; i <= 370; i++){
            objList.add("B"+i);
        }
        for (int i = 1; i <= 471; i++){
            objList.add("Cr"+i);
        }
        for (int i = 1; i <= 254; i++){
            objList.add("Mel"+i);
        }
        for (int i = 1; i <= 109; i++){
            objList.add("C"+i);
        }
    }

    public void goButtonOnclick(View view){
        mainActivity.messageInterpretative.sendObjHub(searchAutoCompleteTextView.getText().toString());
        searchAutoCompleteTextView.setText("");
        searchAutoCompleteTextView.clearFocus();
        searchAutoCompleteTextView.setKeyboardNavigationCluster(false);
        keyBoardClosed(view);
    }

    public void keyBoardClosed(View view){
        InputMethodManager imm = (InputMethodManager)mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @NonNull
    @Override
    public String toString() {
        return ct.getString(R.string.ir_ny_t);
    }

    public void setTrackingSwitch(boolean status){
        if(trackingSwitch != null){
            trackingSwitch.setChecked(status);
        }
    }

    @SuppressLint({"SetTextI18n", "MissingInflatedId", "ClickableViewAccessibility"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        con_status = rootView.findViewById(R.id.bt_con_textview);
        conButton = rootView.findViewById(R.id.bt_con_button);
        felButton = rootView.findViewById(R.id.fel_button);
        jobbraButton = rootView.findViewById(R.id.jobb_button);
        leButton = rootView.findViewById(R.id.le_button);
        balraButton = rootView.findViewById(R.id.bal_button);
        felButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mainActivity.messageInterpretative.sendManual("dec","up", true);

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mainActivity.messageInterpretative.sendManual("dec","up", false);
            }
            return true;
        });
        leButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mainActivity.messageInterpretative.sendManual("dec","down", true);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mainActivity.messageInterpretative.sendManual("dec","down", false);
            }
            return true;
        });
        jobbraButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mainActivity.messageInterpretative.sendManual("ra","up", true);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mainActivity.messageInterpretative.sendManual("ra","up", false);
            }
            return true;
        });
        balraButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mainActivity.messageInterpretative.sendManual("ra","down", true);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mainActivity.messageInterpretative.sendManual("ra","down", false);
            }
            return true;
        });
        leButton = rootView.findViewById(R.id.le_button);
        jobbraButton = rootView.findViewById(R.id.jobb_button);
        balraButton = rootView.findViewById(R.id.bal_button);
        setSpeedSeekBar = rootView.findViewById(R.id.setSpeedSeekBar);
        setSpeedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //Toast.makeText(ct, "Állapot: " + i, Toast.LENGTH_SHORT).show();
                mainActivity.messageInterpretative.sendManualSpeed(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        batteryTextView = rootView.findViewById(R.id.toltotseg_textView);
        tempTextView = rootView.findViewById(R.id.temp_textView);
        humTextView = rootView.findViewById(R.id.hum_textView);
        objTextView = rootView.findViewById(R.id.beal_obj_textView);
        coordinateTextView = rootView.findViewById(R.id.ra_dec_textView);
        searchAutoCompleteTextView = rootView.findViewById(R.id.obj_keres_editText);
        inditButton = rootView.findViewById(R.id.indit_button);
        trackingSwitch = rootView.findViewById(R.id.trackingSwitch);
        trackingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mainActivity.tracking = isChecked;
            mainActivity.messageInterpretative.sendTrackingStatusUpdate(isChecked);
        });
        inditButton.setOnClickListener(this::goButtonOnclick);
        if(objList.isEmpty()){
            generalObjList();
        }
        searchAutoCompleteTextView.setAdapter(new ArrayAdapter<String>(ct, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, objList));
        searchAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                keyBoardClosed(inditButton);
            }
        });



        if(statusTextViewText != null && statusTextViewColor != null){
            con_status.setText(statusTextViewText);
            con_status.setTextColor(statusTextViewColor);
        }
        if(conButtonText !=  null){
            conButton.setText(conButtonText);
        }
        if(battery != null){
            batteryTextView.setText(battery + "V");
        }
        if(temp != null){
            tempTextView.setText(temp + "°C");
        }
        if(hum != null){
            humTextView.setText(hum + "%");
        }
        if(obj != null){
            objTextView.setText(obj);
        }
        if(coordinate != null){
            coordinateTextView.setText(coordinate);
        }

        conButton.setOnClickListener(v -> mainActivity.btCon());
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}