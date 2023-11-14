package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogWindowTemp extends AppCompatDialogFragment {

    public SettingsFragment settingsFragment;

    public DialogWindowTemp(SettingsFragment s){
        this.settingsFragment = s;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder bilder = new AlertDialog.Builder(getActivity());
        bilder.setTitle("Hömérséklet adatbázis törlése")
                .setMessage("Biztos törölni szeretnéd a tárolt hömérséklet adatokat?")
                .setPositiveButton("Törlés", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        settingsFragment.clearTempDB();
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
