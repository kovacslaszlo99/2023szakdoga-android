package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Date;

public class CameraFragment extends Fragment{
    private final Context ct;
    public MainActivity mainActivity;
    private Button cameraStartButton;
    private Button cameraStopButton;
    private EditText expoTimeEditText;
    private EditText sleepTimeEditText;
    private EditText pieceEditText;
    private TextView cameraImagesTextView;
    private TextView startTimeTextView;
    private TextView endTimeTextView;
    private TextView allTimeTextView;
    private TextView allExpoTimeTextView;
    private ProgressBar cameraImagesProcessBar;
    private Button setAlarmButton;
    private EditText mirrorLookUpEditText;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch mirrorLookUpSwitch;
    private EditText alarmSetOffsetEditText;

    public double cameraExpoTime = 0;
    public int cameraSleepTime = 0;
    public int cameraPieceImage = 0;
    public int mirrorLookUpOffset = 0;
    public boolean start = false;
    public int status = 0;
    public double allTimeSecond = 0;
    public double allExpoTime = 0;

    public String cameraStartTime = "";
    public CameraFragment(Context ct) {
        this.ct = ct;
    }

    public void init(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public String toString() {
        return ct.getString(R.string.kamera);
    }

    @SuppressLint("SetTextI18n")
    public void setProcessStatus(int max, int status){
        if(cameraImagesTextView != null && cameraImagesProcessBar != null){
            cameraImagesTextView.setText(max + "/" + status);
            cameraImagesProcessBar.setProgress(status);
            cameraImagesProcessBar.setMax(max);
        }
    }

    public void setCameraExpoTime(double expo){
        cameraExpoTime = expo;
        if (expoTimeEditText != null){
            expoTimeEditText.setText(String.valueOf(expo));
        }
    }

    public void setCameraSleepTime(int sleep){
        cameraSleepTime = sleep;
        if(sleepTimeEditText != null){
            sleepTimeEditText.setText(String.valueOf(sleep));
        }
    }

    public void setCameraPieceImage(int piece){
        cameraPieceImage = piece;
        if(pieceEditText != null){
            pieceEditText.setText(String.valueOf(piece));
        }
    }

    public void setCameraOffset(int offset){
        mirrorLookUpOffset = offset;
        if(mirrorLookUpEditText != null && mirrorLookUpSwitch != null && offset != 0){
            mirrorLookUpEditText.setText(String.valueOf(offset));
            mirrorLookUpSwitch.setChecked(true);
        }
    }

    public long timeToLong(String time){
        return ((Long.parseLong(time.split(":")[0]) * 60*60) +
                (Long.parseLong(time.split(":")[1]) * 60) +
                (Long.parseLong(time.split(":")[2]))) *1000;
    }

    public void setCameraStartTime(String startTime){
        cameraStartTime = startTime;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());
        allTimeSecond = (cameraExpoTime + cameraSleepTime + mirrorLookUpOffset) * cameraPieceImage;
        allExpoTime = cameraExpoTime * cameraPieceImage;
        Date date = new Date();
        date.setTime(timeToLong(startTime) - 60*60*1000);
        double newTimeInMillis = (date.getTime() + ((allTimeSecond - cameraSleepTime)  * 1000));
        Date newTime = new Date((long) newTimeInMillis);
        if(startTimeTextView != null){
            startTimeTextView.setText(startTime);
            endTimeTextView.setText(dateFormat.format(newTime));
            allTimeTextView.setText(timeFormat(allTimeSecond));
            allExpoTimeTextView.setText(timeFormat(allExpoTime));
        }
    }

    public void setStatus(boolean start, int status){
        this.start = start;
        this.status = status;
        setStatusEnable(start);
        setProcessStatus(cameraPieceImage, this.status);
    }

    public void setStatusEnable(boolean status){
        if(expoTimeEditText != null && sleepTimeEditText != null && pieceEditText != null && cameraStartButton != null && cameraStopButton != null) {
            expoTimeEditText.setEnabled(!status);
            sleepTimeEditText.setEnabled(!status);
            pieceEditText.setEnabled(!status);
            cameraStartButton.setEnabled(!status);
            cameraStopButton.setEnabled(status);
        }
    }

    public String timeFormat(double time){
        int hours = (int) (time / 3600);
        int minutes = (int) ((time % 3600) / 60);
        int seconds = (int) (time % 60);
        String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return formattedTime;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        cameraStartButton = rootView.findViewById(R.id.cameraStartButton);
        cameraStopButton = rootView.findViewById(R.id.cameraStopButton);
        expoTimeEditText = rootView.findViewById(R.id.expoTime_editTextNumber);
        sleepTimeEditText = rootView.findViewById(R.id.sleepTime_editTextNumber);
        pieceEditText = rootView.findViewById(R.id.piece_EditTextNumber);
        cameraImagesProcessBar = rootView.findViewById(R.id.cameraImagesProgressBar);
        cameraImagesTextView = rootView.findViewById(R.id.cameraImagesTextView);
        sleepTimeEditText.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "9999")});
        pieceEditText.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "9999999")});
        startTimeTextView = rootView.findViewById(R.id.startTimeTextView);
        endTimeTextView = rootView.findViewById(R.id.endTimeTextView);
        allTimeTextView = rootView.findViewById(R.id.allTimeTextView);
        allExpoTimeTextView = rootView.findViewById(R.id.allExpoTimeTextView);
        setAlarmButton = rootView.findViewById(R.id.setAlarmButton);
        mirrorLookUpEditText = rootView.findViewById(R.id.mirrorLookUpEditTextNumber);
        mirrorLookUpSwitch = rootView.findViewById(R.id.mirrorLookUpSwitch);
        alarmSetOffsetEditText = rootView.findViewById(R.id.alarmSetOffsetEditTextNumber);
        mirrorLookUpSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                mirrorLookUpEditText.setEnabled(true);
            }else{
                mirrorLookUpEditText.setEnabled(false);
            }
        });
        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View view) {
                int h = Integer.parseInt(endTimeTextView.getText().toString().split(":")[0]);
                int m = Integer.parseInt(endTimeTextView.getText().toString().split(":")[1]);
                int s = Integer.parseInt(endTimeTextView.getText().toString().split(":")[2]);
                double time = ((s + (m*60) + (h*60*60))- (Double.parseDouble(alarmSetOffsetEditText.getText().toString())*60));
                String time_str = timeFormat(time);
                Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                intent.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(time_str.split(":")[0]));
                intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(time_str.split(":")[1]));
                intent.putExtra(AlarmClock.EXTRA_MESSAGE, "Fotó sorozat lejárt");
                if(intent.resolveActivity(ct.getPackageManager()) != null) {
                    startActivity(intent);
                }else{
                    Toast.makeText(ct, "Hiba: Nincs olyan alkalmazás, amely támogatja ezt a műveletet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cameraStartButton.setOnClickListener(view -> {
            mirrorLookUpOffset = (mirrorLookUpSwitch.isChecked())? Integer.parseInt(mirrorLookUpEditText.getText().toString()) : 0;
            cameraExpoTime = Double.parseDouble(String.valueOf(expoTimeEditText.getText()));
            cameraSleepTime = Integer.parseInt(sleepTimeEditText.getText().toString());
            cameraPieceImage = Integer.parseInt(pieceEditText.getText().toString());
            start = true;
            setStatusEnable(true);
            setProcessStatus(cameraPieceImage, status);
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getDefault());
            startTimeTextView.setText(dateFormat.format(currentDate));
            cameraStartTime = dateFormat.format(currentDate);
            allTimeSecond = (cameraExpoTime + cameraSleepTime + mirrorLookUpOffset) * cameraPieceImage;
            allExpoTime = cameraExpoTime * cameraPieceImage;
            double newTimeInMillis = (currentDate.getTime() + ((allTimeSecond - cameraSleepTime)  * 1000));
            Date newTime = new Date((long) newTimeInMillis);
            endTimeTextView.setText(dateFormat.format(newTime));
            allTimeTextView.setText(timeFormat(allTimeSecond));
            allExpoTimeTextView.setText(timeFormat(allExpoTime));
            mainActivity.messageInterpretative.sendCamera(cameraExpoTime, cameraSleepTime, cameraPieceImage, mirrorLookUpOffset, dateFormat.format(currentDate));
            mainActivity.messageInterpretative.sendCameraStatus(true);
        });
        cameraStopButton.setOnClickListener(view -> {
            start = false;
            setStatusEnable(false);
            mainActivity.messageInterpretative.sendCameraStatus(false);
        });
        if(start){
            setStatusEnable(true);
        }else{
            setStatusEnable(false);
        }
        if(cameraSleepTime != 0){
            setCameraSleepTime(cameraSleepTime);
        }
        if(cameraPieceImage != 0){
            setCameraPieceImage(cameraPieceImage);
            setProcessStatus(cameraPieceImage, this.status);
        }
        if(cameraExpoTime != 0){
            setCameraExpoTime(cameraExpoTime);

        }
        if(!cameraStartTime.equals("")){
            setCameraStartTime(cameraStartTime);
        }
        if(mirrorLookUpOffset != 0){
            setCameraOffset(mirrorLookUpOffset);
        }
        return rootView;
    }
}
