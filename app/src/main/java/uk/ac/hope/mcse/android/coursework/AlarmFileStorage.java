package uk.ac.hope.mcse.android.coursework;

import android.content.Context;

//import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AlarmFileStorage {
    private static final String FILENAME = "alarms.json";
    private final Context ctx;
    private final Gson gson = new Gson();

    public AlarmFileStorage(Context context) {
        this.ctx = context.getApplicationContext();
    }

    public List<Alarm> loadAlarms() {
        try (FileInputStream fis = ctx.openFileInput(FILENAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            Type listType = new TypeToken<List<Alarm>>(){}.getType();
            List<Alarm> list = gson.fromJson(sb.toString(), listType);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            // File not found or read error → return empty list
            return new ArrayList<>();
        }
    }

    public void saveAlarms(List<Alarm> alarms) {
        String json = gson.toJson(alarms);
        try (FileOutputStream fos = ctx.openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
            fos.write(json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addAlarm(Alarm alarm) {
        List<Alarm> all = loadAlarms();
        all.add(alarm);
        saveAlarms(all);
    }

    public void deleteAlarm(Alarm alarm) {
        List<Alarm> all = loadAlarms();
        if (all.remove(alarm)) {
            saveAlarms(all);
        }
    }

    public void updateAlarms(List<Alarm> alarms) {
        saveAlarms(alarms);
    }
}
