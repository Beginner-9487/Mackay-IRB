package com.example.mackayirb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mackayirb.R;
import com.example.mackayirb.utils.Log;

import java.util.ArrayList;

public class NormalAdapter extends RecyclerView.Adapter<NormalAdapter.DataViewHolder> {

    View view;

    ArrayList<String> strings = new ArrayList<>();
    ArrayList<Integer> colors = new ArrayList<>();

    public int LayoutId() {
        return R.layout.listitem_normal;
    }

    @NonNull
    @Override
    public NormalAdapter.DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(LayoutId(), parent, false);
        NormalAdapter.DataViewHolder viewHolder = new NormalAdapter.DataViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NormalAdapter.DataViewHolder holder, int position) {
        Log.d("position: " + String.valueOf(position));
        holder.textView.setText(strings.get(position));
        holder.textView.setTextColor(colors.get(position).intValue());
    }

    public void add(String string, int color) {
        strings.add(string);
        colors.add(color);
    }

    public void set(ArrayList<String> strings, ArrayList<Integer> colors) {
        this.strings = strings;
        this.colors = colors;
    }

    public void clear() {
        this.strings.clear();
        this.colors.clear();
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public DataViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.string);
        }
    }
}