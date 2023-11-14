package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class SettingsFragment extends Fragment {
    private final Context ct;
    private EditText man_hossz;
    private EditText man_szel;
    public double longitude, latitude;
    private Button gps_mod_button;
    private Button timeSyncButton;
    private Button telescopGetTimeButton;
    private Boolean gps_auto_mod;
    private TextView gps_mod_textView;
    private TextView telescopeTimeTextView;
    private TextView phoneTimeTextView;
    private EditText gotoCurrentEditTextNumber;
    private EditText trackingCurrentEditTextNumber;
    private Switch trackingDIR_Switch;
    private Switch raDIR_switch;
    private Switch decDIR_Switch;
    private Switch timeSiftDIR_Switch;
    public String telescopTime = null;
    public String phonTime = null;

    public MainActivity mainActivity = null;
    public Integer gotoCurrent;
    public Integer trackingCurrent;
    public Boolean trackingDIR;
    public Boolean raDIR;
    public Boolean decDIR;
    public Boolean timeSiftDIR;

    private Button datebaseClearButton;


    public SettingsFragment(Context ct, double longitude, double latitude) {
        this.ct = ct;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void init(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public void setTime(String telescop, String phone){
        if(telescopeTimeTextView != null && phoneTimeTextView != null){
            telescopeTimeTextView.setText(telescop);
            phoneTimeTextView.setText(phone);
        }
        telescopTime = telescop;
        phonTime = phone;
    }

    public void setGotoCurrent(int current){
        if(gotoCurrentEditTextNumber != null){
            gotoCurrentEditTextNumber.setText(current + "");
        }
        gotoCurrent = current;
    }

    public void setTrackingCurrent(int current){
        if(trackingCurrentEditTextNumber != null){
            trackingCurrentEditTextNumber.setText(current + "");
        }
        trackingCurrent = current;
    }

    public void setTrackingDIR(boolean dir){
        if(trackingDIR_Switch != null){
            trackingDIR_Switch.setChecked(dir);
        }
        trackingDIR = dir;
    }

    public void setRaDIR(boolean dir){
        if(raDIR_switch != null){
            raDIR_switch.setChecked(dir);
        }
        raDIR = dir;
    }

    public void setDecDIR(boolean dir){
        if(decDIR != null){
            decDIR_Switch.setChecked(dir);
        }
        decDIR = dir;
    }

    public void setTimeSiftDIR(boolean dir){
        if(timeSiftDIR_Switch != null){
            timeSiftDIR_Switch.setChecked(dir);
        }
        timeSiftDIR = dir;
    }

    @SuppressLint("SetTextI18n")
    private void mod_chnage(View view) {
        gps_auto_mod = !gps_auto_mod;
        if (!gps_auto_mod){
            enableEditText(man_hossz);
            enableEditText(man_szel);
            gps_mod_button.setText("Auto");
            gps_mod_textView.setText("Manuális");
        }else {
            disableEditText(man_hossz);
            disableEditText(man_szel);
            gps_mod_button.setText("Manuális");
            gps_mod_textView.setText("Auto");
        }
    }

    public void clearTempDB(){
        mainActivity.tempViewModel.clearTemp();
    }

    private void enableEditText(EditText editText) {
        editText.setEnabled(true);
    }

    private void disableEditText(EditText editText) {
        editText.setEnabled(false);
    }

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        man_hossz = rootView.findViewById(R.id.hossz_editTextNumber);
        man_szel = rootView.findViewById(R.id.szel_editTextNumber);
        man_hossz.setText(String.valueOf(longitude));
        man_szel.setText(String.valueOf(latitude));
        gps_mod_button = rootView.findViewById(R.id.gps_mod_button);
        gps_mod_textView = rootView.findViewById(R.id.gps_mod_textView);
        telescopeTimeTextView = rootView.findViewById(R.id.tavcso_ido_textView);
        phoneTimeTextView = rootView.findViewById(R.id.telefon_ido_textView);
        gps_mod_button.setOnClickListener(this::mod_chnage);
        timeSyncButton = rootView.findViewById(R.id.ido_szinc_button);
        timeSyncButton.setOnClickListener(v -> mainActivity.messageInterpretative.sendNowDateTime());
        telescopGetTimeButton = rootView.findViewById(R.id.ido_lekeres_button);
        telescopGetTimeButton.setOnClickListener(v -> mainActivity.messageInterpretative.sendGetNowDateTime());
        datebaseClearButton = rootView.findViewById(R.id.datebaseClearButton);
        datebaseClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        gotoCurrentEditTextNumber = rootView.findViewById(R.id.gotoCurrentEditTextNumber);
        trackingCurrentEditTextNumber = rootView.findViewById(R.id.trackingCurrentEditTextNumber);
        trackingDIR_Switch = rootView.findViewById(R.id.trackingDIR_Switch);
        raDIR_switch = rootView.findViewById(R.id.raDIR_switch);
        decDIR_Switch = rootView.findViewById(R.id.decDIR_Switch);
        timeSiftDIR_Switch = rootView.findViewById(R.id.timeSiftDIR_Switch);
        Button settingsSaveButton = rootView.findViewById(R.id.settingsSaveButton);
        settingsSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.messageInterpretative.sendCurrentGOTO(Integer.parseInt(gotoCurrentEditTextNumber.getText().toString()));
                mainActivity.messageInterpretative.sendCurrentTracking(Integer.parseInt(trackingCurrentEditTextNumber.getText().toString()));
                mainActivity.messageInterpretative.sendTrackingDIR(trackingDIR_Switch.isChecked());
                mainActivity.messageInterpretative.sendRaDIR(raDIR_switch.isChecked());
                mainActivity.messageInterpretative.sendDecDIR(decDIR_Switch.isChecked());
                mainActivity.messageInterpretative.sendTimeSiftDIR(timeSiftDIR_Switch.isChecked());
                Toast.makeText(ct, "Mentés kész", Toast.LENGTH_SHORT).show();
            }
        });

        if (gps_auto_mod == null) {
            gps_auto_mod = true;
        }

        if (!gps_auto_mod){
            enableEditText(man_hossz);
            enableEditText(man_szel);
            gps_mod_button.setText("Auto");
            gps_mod_textView.setText("Manuális");
        }else{
            disableEditText(man_hossz);
            disableEditText(man_szel);
        }

        if(phonTime != null && telescopTime != null){
            telescopeTimeTextView.setText(telescopTime);
            phoneTimeTextView.setText(phonTime);
        }

        if(gotoCurrent != null){
            setGotoCurrent(gotoCurrent);
        }

        if(trackingCurrent != null){
            setTrackingCurrent(trackingCurrent);
        }

        if(trackingDIR != null){
            setTrackingDIR(trackingDIR);
        }

        if(raDIR != null){
            setRaDIR(raDIR);
        }

        if(decDIR != null){
            setDecDIR(decDIR);
        }

        if(timeSiftDIR != null){
            setTimeSiftDIR(timeSiftDIR);
        }

        return rootView;
    }

    public void openDialog(){
        DialogWindowTemp dialogWindow = new DialogWindowTemp(this);
        dialogWindow.show(getParentFragmentManager(), "dialog");
    }

    @NonNull
    @Override
    public String toString() {
        return ct.getString(R.string.be_ll_t_sok);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
