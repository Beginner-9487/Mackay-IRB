package com.example.mackayirb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mackayirb.R;

import java.util.ArrayList;

public class TwinTextAdapter extends RecyclerView.Adapter<TwinTextAdapter.DataViewHolder> {

    ArrayList<String> strings1 = new ArrayList<>();
    ArrayList<String> strings2 = new ArrayList<>();
    View view;

    public int LayoutId() {
        return R.layout.listitem_twin_text_box;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(LayoutId(), parent, false);
        DataViewHolder viewHolder = new DataViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        DataViewHolder newHolder = holder;
        try {
            newHolder.textLeft.setText(strings1.get(position));
            newHolder.textRight.setText(strings2.get(position));
        } catch (Exception e) {}
    }

    public void addString(String s1, String s2){
        strings1.add(s1);
        strings2.add(s2);
    }

    public void setString(ArrayList<String> s1, ArrayList<String> s2){
        strings1 = s1;
        strings2 = s2;
    }

    @Override
    public int getItemCount() {
        return strings1.size();
    }

    public void clear() {
        strings1.clear();
        strings2.clear();
    }
    class DataViewHolder extends RecyclerView.ViewHolder {
        TextView textLeft;
        TextView textRight;
        public DataViewHolder(View itemView) {
            super(itemView);
            textLeft = itemView.findViewById(R.id.Left);
            textRight = itemView.findViewById(R.id.Right);
        }
    }
}
