package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    private final Context ct;
    public ListView historyListView;
    public Button historyDeleteButton;
    public ArrayList<HistoryTest> historyTests = new ArrayList<>();

    private MainActivity mainActivity = null;

    public HistoryFragment(Context ct) {
        this.ct = ct;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public String toString() {
        return ct.getString(R.string.el_zm_nyek);
    }

    public void init(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }


    public void updateListView(){
        HistoryAdapter adapter = new HistoryAdapter(ct, historyTests);
        historyListView.setAdapter(adapter);
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        historyListView = rootView.findViewById(R.id.historyListView);
        historyDeleteButton = rootView.findViewById(R.id.historyDeleteButton);
        historyDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        updateListView();
        return rootView;
    }

    public void openDialog(){
        DialogWindow dialogWindow = new DialogWindow(this);
        dialogWindow.show(getParentFragmentManager(), "dialog");
    }

    public void clearHistoryDB(){
        mainActivity.historyViewModel.clearHistory();
    }
}