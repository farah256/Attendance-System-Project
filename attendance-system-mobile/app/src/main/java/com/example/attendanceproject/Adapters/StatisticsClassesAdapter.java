package com.example.attendanceproject.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendanceproject.Model.Classe;
import com.example.attendanceproject.R;

import java.util.List;

public class StatisticsClassesAdapter extends RecyclerView.Adapter<StatisticsClassesAdapter.ClassViewHolder> {

    private List<Classe> classesList;
    private OnClassClickListener listener;

    public interface OnClassClickListener {
        void onClassClick(Classe classe);
    }

    public StatisticsClassesAdapter(List<Classe> classesList, OnClassClickListener listener) {
        this.classesList = classesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.class_static_item, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        Classe classe = classesList.get(position);
        holder.bind(classe, listener);
    }

    @Override
    public int getItemCount() {
        return classesList != null ? classesList.size() : 0;
    }

    public void updateClasses(List<Classe> newClasses) {
        classesList = newClasses;
        notifyDataSetChanged();
    }

    static class ClassViewHolder extends RecyclerView.ViewHolder {
        private TextView classNameText;
        private TextView moduleNameText;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            classNameText = itemView.findViewById(R.id.classe_name_text);
            moduleNameText = itemView.findViewById(R.id.module_name_text);
        }

        public void bind(final Classe classe, final OnClassClickListener listener) {
            classNameText.setText(classe.getNom());
            moduleNameText.setText(classe.getModule());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClassClick(classe);
                }
            });
        }
    }
}