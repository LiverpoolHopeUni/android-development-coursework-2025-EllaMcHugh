package uk.ac.hope.mcse.android.coursework;

import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import java.util.Calendar;

import uk.ac.hope.mcse.android.coursework.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.app.TimePickerDialog;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
import android.content.BroadcastReceiver;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private AlarmViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        model = new ViewModelProvider(this).get(AlarmViewModel.class);

        binding.fab.setOnClickListener(v -> {
            if (navController.getCurrentDestination().getId() == R.id.FirstFragment) {
                Calendar now = Calendar.getInstance();
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minute = now.get(Calendar.MINUTE);

                // build & show the dialog
                TimePickerDialog picker = new TimePickerDialog(
                        MainActivity.this,
                        (view, hourOfDay, minuteOfHour) -> {
                            scheduleAlarm(hourOfDay, minuteOfHour);
                            model.addAlarm(new Alarm(hourOfDay, minuteOfHour, true));
                        },
                        hour,
                        minute,
                        true
                );
                picker.setTitle("Set Alarm Time");
                picker.show();
            }
        });
    }

    private void scheduleAlarm(int hourOfDay, int minuteOfHour) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
                this,
                hourOfDay * 100 + minuteOfHour,    // unique requestCode if you like
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        alarmTime.set(Calendar.MINUTE, minuteOfHour);
        alarmTime.set(Calendar.SECOND, 0);
        if (alarmTime.before(Calendar.getInstance())) {
            alarmTime.add(Calendar.DATE, 1);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && !am.canScheduleExactAlarms()) {
            startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
            return;
        }

        try {
            am.setExact(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime.getTimeInMillis(),
                    pi
            );
        } catch (SecurityException e) {
            Toast.makeText(
                    this,
                    "Cannot schedule exact alarm – please enable in settings",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

}