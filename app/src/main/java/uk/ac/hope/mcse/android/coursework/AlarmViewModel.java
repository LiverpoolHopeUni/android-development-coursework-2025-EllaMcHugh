package uk.ac.hope.mcse.android.coursework;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class AlarmViewModel extends AndroidViewModel {
    private final AlarmFileStorage storage;
    private final MutableLiveData<List<Alarm>> alarms = new MutableLiveData<>();

    public AlarmViewModel(@NonNull Application application) {
        super(application);
        storage = new AlarmFileStorage(application.getApplicationContext());
        List<Alarm> loaded = storage.loadAlarms();
        alarms.setValue(loaded != null ? loaded : new ArrayList<>());
    }
    public  LiveData<List<Alarm>> getAlarms() {
        return alarms;
    }
    public void addAlarm(Alarm alarm){
        List<Alarm> current = alarms.getValue();
        if (current == null) {
            current = new ArrayList<>();
        }
        current.add(alarm);
        storage.saveAlarms(current);
        alarms.setValue(current);
        }

    public void deleteAlarm(Alarm alarm) {
        List<Alarm> current = alarms.getValue();
        if (current != null && current.remove(alarm)){
            storage.saveAlarms(current);
            alarms.setValue(current);

        }
    }
    public void updateAlarms(List<Alarm> updated) {
        storage.saveAlarms(updated);
        alarms.setValue(updated);
    }
}
