package com.example.note_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    private ArrayList<Reminder> listremind;
    private List<Reminder> reminderList;

    public ReminderAdapter(List<Reminder> reminderList) {
        this.reminderList = reminderList;
        listremind = new ArrayList<>(reminderList);
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView tvDate;
        public TextView tvTime;
        public ImageView imageView;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_remindertitle);
            tvDate = itemView.findViewById(R.id.tv_reminderday);
            tvTime = itemView.findViewById(R.id.tv_reminderTime);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public void bind(Reminder reminder) {
            tvTitle.setText(reminder.getTitle());
            tvDate.setText(reminder.getDate());
            tvTime.setText(reminder.getTime());
        }
    }
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);
        holder.bind(reminder);
        holder.imageView.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (mListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(adapterPosition);
                }
            }
        });
    }
    public Filter getFilter(){
        return listreminderFilter;
    }
    private Filter listreminderFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Reminder> filterList = new ArrayList<>();

            if(constraint==null || constraint.length() == 0){
                filterList.addAll(listremind);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Reminder item : listremind){
                    if(item.getTitle().toLowerCase().contains(filterPattern)){
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
            reminderList.clear();
            reminderList.addAll((ArrayList<Reminder>) results.values);
            notifyDataSetChanged();
        }
    };
}


