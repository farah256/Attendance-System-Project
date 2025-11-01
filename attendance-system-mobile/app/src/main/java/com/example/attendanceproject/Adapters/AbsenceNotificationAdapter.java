package com.example.attendanceproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendanceproject.Model.Abscence;
import com.example.attendanceproject.Model.Etudiant;
import com.example.attendanceproject.R;
import com.google.android.material.chip.Chip;

import java.util.List;
import java.util.Map;

public class AbsenceNotificationAdapter extends RecyclerView.Adapter<AbsenceNotificationAdapter.NotificationViewHolder> {

    private Context context;
    private List<Etudiant> etudiantList;
    private Map<String, String> classModuleMap;

    public AbsenceNotificationAdapter(Context context, List<Etudiant> etudiantList, Map<String, String> classModuleMap) {
        this.context = context;
        this.etudiantList = etudiantList;
        this.classModuleMap = classModuleMap;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notif_card, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Etudiant etudiant = etudiantList.get(position);

        holder.nomEtudiant.setText(etudiant.getPrenom() + " " + etudiant.getNom());
        holder.classeModule.setText(classModuleMap.get(etudiant.getClasseId()));
        // Calculate justified and unjustified absences from the absences list
        int nonJustified = 0;
        int justified = 0;

        if (etudiant.getAbsences() != null) {
            for (Abscence absence : etudiant.getAbsences()) {
                if (absence.isJustifiee()) {
                    justified++;
                } else {
                    nonJustified++;
                }
            }
        }

        holder.absentChip.setText(nonJustified + " Absent");
        holder.justifieChip.setText(justified + " Justifié");

        // Optional: Change chip colors based on counts
        if (nonJustified >= 3) {
            holder.absentChip.setChipBackgroundColorResource(R.color.red); // Define in colors.xml
        }
    }

    @Override
    public int getItemCount() {
        return etudiantList.size();
    }

    public void updateData(List<Etudiant> newList) {
        etudiantList = newList;
        notifyDataSetChanged();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView nomEtudiant, classeModule;
        Chip absentChip, justifieChip;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            nomEtudiant = itemView.findViewById(R.id.nom_etudiant);
            classeModule = itemView.findViewById(R.id.classe_module);
            absentChip = itemView.findViewById(R.id.chip);
            justifieChip = itemView.findViewById(R.id.chip2);
        }
    }
}