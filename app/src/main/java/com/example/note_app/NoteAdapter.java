package com.example.note_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//Adapter cho Recycle View để hiển thị note ra trang main.java
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> implements Filterable {
    private ArrayList<Note> listnotes;
    private int layoutID;
    private Activity context;
    private ArrayList<Note> listnoteaf;
    RecyclerView rcv_note;

    public NoteAdapter(Activity context, int layoutID, ArrayList<Note> notes){
        this.listnotes=notes;
        this.context=context;
        this.layoutID=layoutID;
        listnoteaf= new ArrayList<>(listnotes);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView= inflater.inflate(R.layout.item_note, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = listnotes.get(position);

        holder.notetitle.setText(note.getNote_title());
        holder.noteday.setText(note.getNote_day()+"");
        // Set trạng thái của imageView dựa vào trạng thái yêu thích của note
        if (note.isFavorite()) {
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
        //holder.cbFavorite.setChecked(note.isFavorite());
        holder.itemView.setOnClickListener((v)->{
            Intent intent = new Intent(context, note_take.class);
            intent.putExtra("NOTE_TITLE", note.getNote_title().toString());
            intent.putExtra("NOTE_CONTENT", note.getNote_content().toString());
            intent.putExtra("NOTE_DATE", note.getNote_day().toString());
            intent.putExtra("IS_FAVORITE", note.isFavorite());
            intent.putExtra("NOTE_ID", note.getNote_id());
            context.startActivity(intent);
        });
    }
    public void sortNotesByFavorite() {
        Collections.sort(listnotes, new Comparator<Note>() {
            @Override
            public int compare(Note note1, Note note2) {
                // Sắp xếp theo trạng thái yêu thích giảm dần (true sẽ lên đầu)
                return Boolean.compare(note2.isFavorite(), note1.isFavorite());
            }
        });
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return listnotes.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private View itemview;
        public TextView notetitle;
        public TextView noteday;
        public CheckBox cbFavorite;
        public ImageView imageView;
        public ViewHolder(View itemview){
            super(itemview);
            itemview=itemview;
            notetitle=itemview.findViewById(R.id.tv_notetitle);
            noteday=itemview.findViewById(R.id.tv_noteday);
            cbFavorite = itemview.findViewById(R.id.cbFavorite);
            imageView = itemview.findViewById(R.id.imageView);
        }
    }
    @Override
    public Filter getFilter(){
        return listnotesFilter;
    }
    private Filter listnotesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Note> filterList = new ArrayList<>();

            if(constraint==null || constraint.length() == 0){
                filterList.addAll(listnoteaf);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Note item : listnoteaf){
                    if(item.getNote_title().toLowerCase().contains(filterPattern)){
                        filterList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values= filterList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listnotes.clear();
            listnotes.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

}
