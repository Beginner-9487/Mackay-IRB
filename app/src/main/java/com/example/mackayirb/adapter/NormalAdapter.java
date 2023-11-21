package com.example.mackayirb.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mackayirb.R;

import java.util.ArrayList;

public class NormalAdapter extends RecyclerView.Adapter<NormalAdapter.DataViewHolder> {

    View view;

    ArrayList<String> strings = new ArrayList<>();
    ArrayList<Integer> colorTexts = new ArrayList<>();
    ArrayList<Integer> colorBackgrounds = new ArrayList<>();

    public int LayoutId() {
        return R.layout.listitem_normal;
    }

    Float fontSize = null;
    public void setFontSize(Float fontSize) {
        this.fontSize = fontSize;
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
        if(fontSize != null) {
            holder.textView.setTextSize(fontSize);
        }

        // Log.d("position: " + String.valueOf(position));
        holder.textView.setText(strings.get(position));
        if(colorTexts.get(position) != null) {
            holder.textView.setTextColor(colorTexts.get(position).intValue());
        }
        if(colorBackgrounds.get(position) != null) {
            holder.textView.setBackgroundColor(colorBackgrounds.get(position).intValue());
        }
    }

    public void add(String string, Integer colorText, Integer colorBackground) {
        strings.add(string);
        colorTexts.add(colorText);
        colorBackgrounds.add(colorBackground);
    }

    public void set(ArrayList<String> strings, ArrayList<Integer> colorTexts, ArrayList<Integer> colorBackgrounds) {
        this.strings = strings;
        this.colorTexts = colorTexts;
        this.colorBackgrounds = colorBackgrounds;
    }

    public void clear() {
        this.strings.clear();
        this.colorTexts.clear();
        this.colorBackgrounds.clear();
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