package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryAdapter extends BaseAdapter {
    private final LayoutInflater inflter;
    private final ArrayList<HistoryTest> list;

    public HistoryAdapter(Context applicationContext, ArrayList<HistoryTest> list) {
        this.list = list;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.historylist, null);
        TextView name = view.findViewById(R.id.historyname);
        TextView date = view.findViewById(R.id.historydate);
        TextView dateutc = view.findViewById(R.id.historydateutc);
        name.setText(list.get(i).name);
        date.setText(list.get(i).date);
        dateutc.setText(list.get(i).dateUTC);
        return view;
    }
}