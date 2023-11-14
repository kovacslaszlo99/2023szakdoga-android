package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogWindow extends AppCompatDialogFragment {

    public HistoryFragment historyFragment;

    public DialogWindow(HistoryFragment h){
        this.historyFragment = h;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder bilder = new AlertDialog.Builder(getActivity());
        bilder.setTitle("Elözmények törlése")
                .setMessage("Biztos törölni szeretnéd az elözményeket?")
                .setPositiveButton("Törlés", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        historyFragment.clearHistoryDB();
                        historyFragment.historyTests.clear();
                        historyFragment.updateListView();
                    }
                })
                .setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //
                    }
                });
        return bilder.create();
    }
}
