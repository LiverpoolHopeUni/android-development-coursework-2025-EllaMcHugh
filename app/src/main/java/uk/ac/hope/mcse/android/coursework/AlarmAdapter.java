package uk.ac.hope.mcse.android.coursework;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmAdapter extends ListAdapter<Alarm, AlarmAdapter.VH> {

    public interface Listener{
        void onToggle(@NonNull Alarm alarm, boolean enabled);
        void onDelete(@NonNull Alarm alarm);
    }

    private final Listener listener;

    public AlarmAdapter(@NonNull Listener listener){
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Alarm> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Alarm>() {
                @Override
                public boolean areItemsTheSame(@NonNull Alarm oldItem, @NonNull Alarm newItem) {
                    return oldItem.hour == newItem.hour && oldItem.minute == newItem.minute;
                }

                @Override
                public boolean areContentsTheSame(@NonNull Alarm oldItem, @NonNull Alarm newItem) {
                    return oldItem.hour == newItem.hour && oldItem.minute == newItem.minute && oldItem.enabled == newItem.enabled;
                }
            };
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item, parent, false);
        return  new VH(v);

    }
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Alarm alarm = getItem(position);
        holder.bind(alarm);

    }

    class VH extends RecyclerView.ViewHolder {
        private final TextView tvTime;
        private final Switch swEnabled;
        private final ImageButton btnDelete;

        public VH(@NonNull View itemView){
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvAlarmTime);
            swEnabled = itemView.findViewById(R.id.swEnabled);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(@NonNull Alarm alarm) {
            String time = String.format("%02d:%02d", alarm.hour, alarm.minute);
            tvTime.setText(time);
            swEnabled.setOnCheckedChangeListener(null);
            swEnabled.setChecked(alarm.enabled);
            swEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
                listener.onToggle(alarm, isChecked);
            });
            btnDelete.setOnClickListener(v ->{
                listener.onDelete(alarm);
            });
        }
    }

}

