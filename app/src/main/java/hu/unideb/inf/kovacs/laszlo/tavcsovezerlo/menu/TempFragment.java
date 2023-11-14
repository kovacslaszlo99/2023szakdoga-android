package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.net.ParseException;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TempFragment extends Fragment {
    private final Context ct;
    public LineChart lineChartTemp;
    public LineChart lineChartHum;
    public AutoCompleteTextView autoCompleteTextView;
    public ArrayAdapter<String> adapterItems;
    public ArrayList<String> dateList = new ArrayList<>();

    public List<Entry> entriesTemp = new ArrayList<>();
    public List<Entry> entriesHum = new ArrayList<>();

    public MainActivity mainActivity;
    public TempFragment(Context ct) {
        this.ct = ct;
    }

    private Button getDataButton;

    public ArrayList<String> lastDateTimes = new ArrayList<>();

    public Boolean pressedGetDataButton = false;

    public TextView tempMinTextView;
    public TextView tempAvgTextView;
    public TextView tempMaxTextView;
    public TextView humMinTextView;
    public TextView humAvgTextView;
    public TextView humMaxTextView;

    public Boolean init = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void init(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public String toString() {
        return ct.getString(R.string.h_m_rs_klet);
    }

    public void chartSettings(LineChart chart, String name, List<Entry> entries){
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true);
        LineDataSet dataSet = new LineDataSet(entries, name);
        dataSet.setColor(Color.rgb(26, 255, 29));
        dataSet.setValueTextColor(Color.rgb(142, 180, 255));
        dataSet.setCircleColor(Color.rgb(193, 142, 255));
        dataSet.setValueTextSize(10);
        dataSet.setCircleHoleColor(Color.rgb(193, 142, 255));

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        YAxis leftYAxis = chart.getAxisLeft();
        leftYAxis.setTextColor(Color.WHITE);
        YAxis rightYAxis = chart.getAxisRight();
        rightYAxis.setTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new DateAxisValueFormatter());
        xAxis.setLabelRotationAngle(45f);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.WHITE);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.invalidate();
    }

    public String timeReformat(String time) {
        String inputFormat = "HH:mm:ss";
        String outputFormat = "HH:mm";

        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat);
        Date date = null;
        try {
            date = inputDateFormat.parse(time);
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
        return outputDateFormat.format(date);
    }

    public String dateReformat(String dateIn) {
        String inputFormat = "yyyy-MM-dd";
        String outputFormat = "dd";

        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat);
        Date date = null;
        try {
            date = inputDateFormat.parse(dateIn);
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
        return outputDateFormat.format(date);
    }

    @SuppressLint("DefaultLocale")
    public void getData(String day){
        mainActivity.tempViewModel.getDay(day, day).observe(getViewLifecycleOwner(), temps -> {
            entriesHum.clear();
            entriesTemp.clear();
            Double tmax = temps.get(0).getTemp(), tmin = temps.get(0).getTemp(), tsum = 0.0;
            Double hmax = temps.get(0).getHum(), hmin = temps.get(0).getHum(), hsum = 0.0;
            for (Temp item : temps){
                tmax = (item.getTemp()>tmax)? item.getTemp() : tmax;
                tmin = (item.getTemp()<tmin)? item.getTemp() : tmin;
                tsum += item.getTemp();

                hmax = (item.getHum()>hmax)? item.getHum() : hmax;
                hmin = (item.getHum()<hmin)? item.getHum() : hmin;
                hsum += item.getHum();
                entriesTemp.add(new Entry(getXValue(dateReformat(item.getDateUTC()) + " " + timeReformat(item.getTimeUTC())), (float) item.getTemp()));
                entriesHum.add(new Entry(getXValue(dateReformat(item.getDateUTC()) + " " + timeReformat(item.getTimeUTC())), (float) item.getHum()));
            }
            chartSettings(lineChartTemp, "Hőmérséklet", entriesTemp);
            chartSettings(lineChartHum, "Páratartalom", entriesHum);
            tempMinTextView.setText(String.valueOf(tmin));
            tempMaxTextView.setText(String.valueOf(tmax));
            tempAvgTextView.setText(String.format("%,.2f", tsum/temps.size()));

            humMinTextView.setText(String.valueOf(hmin));
            humMaxTextView.setText(String.valueOf(hmax));
            humAvgTextView.setText(String.format("%,.2f", hsum/temps.size()));
        });
    }

    public void getLastDateTimes(String day){
        mainActivity.tempViewModel.getTimesDay(day).observe(getViewLifecycleOwner(), temps -> {
            lastDateTimes.clear();
            for (Temp item : temps){
                lastDateTimes.add(item.getTimeUTC());
            }
            if(pressedGetDataButton) {
                String time = maxTime(lastDateTimes);
                Toast.makeText(ct, "Last date: " + day + " " + time, Toast.LENGTH_SHORT).show();
                mainActivity.messageInterpretative.sendGetData(day + " " + time);
                pressedGetDataButton = false;
            }
        });
    }

    public String maxDate(){
        String maxY = "0";
        String maxH = "0";
        String maxD = "0";
        for (String item : dateList) {
            String[] data = item.split("-");
            if(Integer.parseInt(data[0]) > Integer.parseInt(maxY)){
                maxY = data[0];
                maxH = data[1];
                maxD = data[2];
            }else if(Integer.parseInt(data[0]) == Integer.parseInt(maxY)){
                if(Integer.parseInt(data[1]) > Integer.parseInt(maxH)){
                    maxH = data[1];
                    maxD = data[2];
                }else if(Integer.parseInt(data[1]) == Integer.parseInt(maxH)){
                    if(Integer.parseInt(data[2]) > Integer.parseInt(maxD)){
                        maxD = data[2];
                    }
                }
            }
        }
        return maxY + "-" + maxH + "-" + maxD;
    }

    public String maxTime(ArrayList<String> times){
        String maxH = "0";
        String maxM = "0";
        String maxS = "0";
        for (String item : times) {
            String[] data = item.split(":");
            if(Integer.parseInt(data[0]) > Integer.parseInt(maxH)){
                maxH = data[0];
                maxM = data[1];
                maxS = data[2];
            }else if(Integer.parseInt(data[0]) == Integer.parseInt(maxH)){
                if(Integer.parseInt(data[1]) > Integer.parseInt(maxM)){
                    maxM = data[1];
                    maxS = data[2];
                }else if(Integer.parseInt(data[1]) == Integer.parseInt(maxM)){
                    if(Integer.parseInt(data[2]) > Integer.parseInt(maxS)){
                        maxS = data[2];
                    }
                }
            }
        }
        return maxH + ":" + maxM + ":" + maxS;
    }



    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_temp, container, false);
        lineChartTemp = rootView.findViewById(R.id.reportingChartTemp);
        lineChartHum = rootView.findViewById(R.id.reportingChartHum);
        autoCompleteTextView = rootView.findViewById(R.id.auto_complete_text_date);
        tempMinTextView = rootView.findViewById(R.id.tempMinTextView);
        tempAvgTextView = rootView.findViewById(R.id.tempAvgTextView);
        tempMaxTextView = rootView.findViewById(R.id.tempMaxTextView);
        humMinTextView = rootView.findViewById(R.id.humMinTextView);
        humAvgTextView = rootView.findViewById(R.id.humAvgTextView);
        humMaxTextView = rootView.findViewById(R.id.humMaxTextView);

        adapterItems = new ArrayAdapter<String>(ct, R.layout.list_item, dateList);
        if(!dateList.isEmpty() && !init) {
            autoCompleteTextView.setText(dateList.get(0));
        }
        autoCompleteTextView.setAdapter(adapterItems);
        getDataButton = rootView.findViewById(R.id.getDataButton);


        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                //Toast.makeText(ct, "Item: " + item, Toast.LENGTH_SHORT).show();
                getData(item);
            }
        });

        getDataButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 pressedGetDataButton = true;
                 if(dateList.isEmpty()){
                     Toast.makeText(ct, "Send get data ALL", Toast.LENGTH_SHORT).show();
                     mainActivity.messageInterpretative.sendGetData();
                     pressedGetDataButton = false;
                 }else {
                     String date = maxDate();
                     getLastDateTimes(date);
                 }
             }
        });

        if(!dateList.isEmpty()) {
            getData(dateList.get(0));
        }

        init = true;
        return rootView;
    }

    private float getXValue(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("dd HH:mm", Locale.getDefault());
        try {
            Date date = format.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
        return 0f;
    }

    private class DateAxisValueFormatter extends ValueFormatter {

        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd\nHH:mm", Locale.getDefault());

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return dateFormat.format(new Date((long) value));
        }
    }
}