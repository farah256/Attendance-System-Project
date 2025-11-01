package com.example.attendanceproject.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendanceproject.Model.CalendarDay;
import com.example.attendanceproject.R;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private List<CalendarDay> days;
    private OnDayClickListener listener;
    private int selectedPosition = -1;

    public interface OnDayClickListener {
        void onDayClick(CalendarDay day);
    }

    public CalendarAdapter(List<CalendarDay> days, OnDayClickListener listener) {
        this.days = days;
        this.listener = listener;
        setTodayAsSelected();
    }

    private void setTodayAsSelected() {
        for (int i = 0; i < days.size(); i++) {
            if (days.get(i).isToday()) {
                selectedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        CalendarDay day = days.get(position);
        holder.dayNumber.setText(String.valueOf(day.getDayNumber()));
        holder.dayName.setText(day.getDayName());

        // Highlight today's date
        if (day.isToday()) {
            holder.dayNumber.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.purple_2));
            holder.dayName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.purple_2));
        } else {
            holder.dayNumber.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
            holder.dayName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
        }



        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            listener.onDayClick(day);
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dayNumber;
        TextView dayName;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dayNumber = itemView.findViewById(R.id.dayNumber);
            dayName = itemView.findViewById(R.id.dayName);
        }
    }
}