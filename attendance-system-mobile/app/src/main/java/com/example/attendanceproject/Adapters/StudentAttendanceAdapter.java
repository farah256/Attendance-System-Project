package com.example.attendanceproject.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendanceproject.Model.Etudiant;
import com.example.attendanceproject.R;

import java.util.List;

public class StudentAttendanceAdapter extends RecyclerView.Adapter<StudentAttendanceAdapter.StudentViewHolder> {
    private List<Etudiant> students;
    private final OnAttendanceChangeListener attendanceListener;
    private final OnJustificationClickListener justificationListener;

    public interface OnAttendanceChangeListener {
        void onAttendanceChanged(Etudiant student, boolean isPresent);
    }

    public interface OnJustificationClickListener {
        void onJustificationClicked(Etudiant student);
    }

    public StudentAttendanceAdapter(List<Etudiant> students,
                                    OnAttendanceChangeListener attendanceListener,
                                    OnJustificationClickListener justificationListener) {
        this.students = students;
        this.attendanceListener = attendanceListener;
        this.justificationListener = justificationListener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_list, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Etudiant student = students.get(position);
        holder.studentName.setText(student.getNom() + " " + student.getPrenom());

        holder.attendanceCheckbox.setOnCheckedChangeListener(null);
        holder.attendanceCheckbox.setChecked(true); // Default to present
        holder.attendanceCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (attendanceListener != null) {
                attendanceListener.onAttendanceChanged(student, isChecked);
            }
        });

        holder.justificationButton.setOnClickListener(v -> {
            if (justificationListener != null) {
                justificationListener.onJustificationClicked(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public void updateStudents(List<Etudiant> newStudents) {
        this.students = newStudents;
        notifyDataSetChanged();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentName;
        CheckBox attendanceCheckbox;
        ImageView justificationButton;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.nom_etudiant);
            attendanceCheckbox = itemView.findViewById(R.id.mark);
            justificationButton = itemView.findViewById(R.id.add_justification_icon);
        }
    }
}