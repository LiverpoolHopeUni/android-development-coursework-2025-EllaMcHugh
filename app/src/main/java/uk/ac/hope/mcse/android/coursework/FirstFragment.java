package uk.ac.hope.mcse.android.coursework;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class FirstFragment extends Fragment {
    private AlarmAdapter adapter;
    private AlarmViewModel model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_first, container, false);

        RecyclerView rv = root.findViewById(R.id.rvAlarms);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AlarmAdapter(new AlarmAdapter.Listener() {
            @Override
            public void onToggle(@NonNull Alarm alarm, boolean enabled) {
                alarm.enabled = enabled;
                List<Alarm> current = model.getAlarms().getValue();
                if (current != null) {
                    model.updateAlarms(current);
                }
            }

            @Override
            public void onDelete(@NonNull Alarm alarm) {
                model.deleteAlarm(alarm);

            }
        });
        rv.setAdapter(adapter);

        model = new ViewModelProvider(requireActivity()).get(AlarmViewModel.class);
        model.getAlarms().observe(getViewLifecycleOwner(), alarms -> {
            adapter.submitList(new ArrayList<>(alarms));
        });
        return root;
    }

}
