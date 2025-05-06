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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class FirstFragment extends Fragment {
    private AlarmAdapter adapter;
    private AlarmViewModel model;
    private RecyclerView rvAlarms;
    private View tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_first, container, false);

        rvAlarms = root.findViewById(R.id.rvAlarms);
        tvEmpty = root.findViewById(R.id.tvEmpty);
        rvAlarms.setLayoutManager(new LinearLayoutManager(requireContext()));

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
        rvAlarms.setAdapter(adapter);

        model = new ViewModelProvider(requireActivity()).get(AlarmViewModel.class);
        model.getAlarms().observe(getViewLifecycleOwner(), alarms -> {
            if (alarms == null || alarms.isEmpty()) {
                rvAlarms.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                rvAlarms.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
                adapter.submitList(new ArrayList<>(alarms));
            }

        });

        View button = root.findViewById(R.id.button_first);
        button.setOnClickListener(v-> {
            NavHostFragment.findNavController(FirstFragment.this)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment);

        });
        return root;
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        if (fab !=null) {
            fab.setVisibility(View.VISIBLE);
        }
    }

}
