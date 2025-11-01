package com.example.attendanceproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendanceproject.Model.Note;
import com.example.attendanceproject.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notesList;
    private OnNoteClickListener onNoteClickListener;
    private Context context;
    private SimpleDateFormat inputFormat;
    private SimpleDateFormat outputFormat;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }

    public NoteAdapter(Context context, List<Note> notesList, OnNoteClickListener listener) {
        this.context = context;
        this.notesList = notesList;
        this.onNoteClickListener = listener;
        this.inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_notes, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notesList.get(position);
        holder.noteText.setText(note.getTitle());
        holder.timeText.setText(formatDate(note.getCreatedAt()));
        holder.descriptionText.setText(note.getContenu());

        holder.cardView.setOnClickListener(v -> {
            if (onNoteClickListener != null) {
                onNoteClickListener.onNoteClick(note);
            }
        });
    }

    private String formatDate(String originalDate) {
        try {
            Date date = inputFormat.parse(originalDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return originalDate;
        }
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public void updateNotes(List<Note> newNotes) {
        notesList = newNotes;
        notifyDataSetChanged();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView noteText, timeText, descriptionText;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.note_container);
            noteText = itemView.findViewById(R.id.noteText);
            timeText = itemView.findViewById(R.id.timeText);
            descriptionText = itemView.findViewById(R.id.description_note);
        }
    }
}