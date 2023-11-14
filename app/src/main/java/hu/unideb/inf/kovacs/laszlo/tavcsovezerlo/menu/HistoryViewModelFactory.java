package hu.unideb.inf.kovacs.laszlo.tavcsovezerlo.menu;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class HistoryViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    public HistoryViewModelFactory(Application application){
        this.application = application;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new HistoryViewModel(application);
    }
}
