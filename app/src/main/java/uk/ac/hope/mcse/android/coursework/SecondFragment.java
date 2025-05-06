package uk.ac.hope.mcse.android.coursework;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.service.controls.actions.FloatAction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import static android.app.NotificationManager.IMPORTANCE_HIGH;
import androidx.core.app.NotificationCompat;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import uk.ac.hope.mcse.android.coursework.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {
    private static final String CHANNEL_ID = "timer_channel";
    private static final String CHANNEL_NAME = "Timer Notifications";
    private TextView tvCountdown;
    private Button btnStart;
    private Button btnPause;
    private Button btnReset;
    private CountDownTimer timer;
    private long millisLeft = 0;
    private boolean running = false;
    private static final long DEFAULT_DURATION_MS = 60_000;
    private Button btnOneMinute;
    private Button btnFiveMinutes;
    private Button btnFifteenMinutes;
    private Button btnCustom;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_second, container, false);

        tvCountdown = root.findViewById(R.id.tvCountdown);
        btnStart = root.findViewById(R.id.btnStart);
        btnPause = root.findViewById(R.id.btnPause);
        btnReset = root.findViewById(R.id.btnReset);

        btnOneMinute = root.findViewById(R.id.btnOneMinute);
        btnFiveMinutes = root.findViewById(R.id.btnFiveMinutes);
        btnFifteenMinutes = root.findViewById(R.id.btnFifteenMinutes);
        btnCustom = root.findViewById(R.id.btnCustom);

        btnOneMinute.setOnClickListener(v -> setTimerMinutes(1));
        btnFiveMinutes.setOnClickListener(v -> setTimerMinutes(5));
        btnFifteenMinutes.setOnClickListener(v -> setTimerMinutes(15));

        btnCustom.setOnClickListener(v -> showCustomTimerDialog());

        Button btnBack = root.findViewById(R.id.button_second);
        btnBack.setOnClickListener(v-> {
            NavHostFragment.findNavController(SecondFragment.this)
                    .navigate(R.id.action_SecondFragment_to_FirstFragment);
        });

        updateDisplay(DEFAULT_DURATION_MS);
        millisLeft = DEFAULT_DURATION_MS;

        btnStart.setOnClickListener(v -> {
            if (!running) {
                startTimer();
            }
        });

        btnPause.setOnClickListener(v -> {
            if (running) {
                pauseTimer();
            }
        });

        btnReset.setOnClickListener(v -> {
            resetTimer();
        });

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        if (fab !=null) {
            fab.setVisibility(View.GONE);
        }
        return root;
    }

    private void startTimer() {
        running = true;
        long startTime = millisLeft > 0 ? millisLeft : DEFAULT_DURATION_MS;
        timer = new CountDownTimer(startTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisLeft = millisUntilFinished;
                updateDisplay(millisLeft);
            }

            @Override
            public  void onFinish() {
                running = false;
                millisLeft = 0;
                updateDisplay(millisLeft);
                sendTimerFinishedNotification();
            }
        }.start();
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.cancel();
        }
        running = false;
    }

    private void resetTimer() {
        if (timer != null) {
            timer.cancel();
        }
        running = false;
        millisLeft = DEFAULT_DURATION_MS;
        updateDisplay(millisLeft);
    }
    private void updateDisplay(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long remSeconds = seconds % 60;
        tvCountdown.setText(String.format("%02d:%02d", minutes, remSeconds));
    }

    private void setTimerMinutes(int minutes) {
        millisLeft = minutes * 60_000L;
        updateDisplay(millisLeft);
    }

    private void showCustomTimerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Set Custom Timer");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        EditText etMin = new EditText(requireContext());
        etMin.setHint("Min");
        etMin.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        etMin.setLayoutParams(lp);
        layout.addView(etMin);

        EditText etSec = new EditText(requireContext());
        etSec.setHint("Sec");
        etSec.setInputType(InputType.TYPE_CLASS_NUMBER);
        etSec.setLayoutParams(lp);
        layout.addView(etSec);

        builder.setView(layout);

        builder.setPositiveButton("Set", (dialog, which) -> {
            String minStr =etMin.getText().toString();
            String secStr = etSec.getText().toString();
            int minutes = minStr.isEmpty() ? 0 : Integer.parseInt(minStr);
            int seconds = secStr.isEmpty() ? 0 : Integer.parseInt(secStr);

            // Update millisLeft and display
            millisLeft = (minutes * 60 + seconds) * 1000L;
            updateDisplay(millisLeft);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void createNotificationChannel() {
        NotificationManager nm = (NotificationManager)
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel chan = nm.getNotificationChannel(CHANNEL_ID);
        if (chan == null) {
            chan = new NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME, IMPORTANCE_HIGH);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            chan.setSound(alarmSound, null);
            chan.enableVibration(true);
            nm.createNotificationChannel(chan);
        }
    }

    private void sendTimerFinishedNotification() {
        createNotificationChannel();
        NotificationManager nm = (NotificationManager)
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                requireContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Timer Finished")
                .setContentText("Your countdown has completed.")
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        nm.notify(1, builder.build());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer !=null) {
            timer.cancel();

        }
    }

}

