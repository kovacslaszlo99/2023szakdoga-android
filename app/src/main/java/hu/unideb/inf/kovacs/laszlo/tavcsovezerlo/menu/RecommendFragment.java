package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RecommendFragment extends Fragment{
    private final Context ct;
    public AutoCompleteTextView autoCompleteTextView;
    public ArrayAdapter<String> adapterItems;

    public MainActivity mainActivity;
    public RecommendFragment(Context ct) {
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
        return ct.getString(R.string.aj_nl);
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recommend, container, false);
        autoCompleteTextView = rootView.findViewById(R.id.auto_complete_text_Month);

        String[] items = {"Január", "Február", "Március", "Április", "Május", "Június", "Július", "Augusztus", "Szeptember", "Október", "November", "December"};

        adapterItems = new ArrayAdapter<String>(ct, R.layout.list_item, items);
        autoCompleteTextView.setText(items[mainActivity.getNowMount()-1]);
        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(ct, "Item: " + item, Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }
}