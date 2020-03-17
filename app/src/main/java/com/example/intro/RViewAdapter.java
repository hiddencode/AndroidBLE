package com.example.intro;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class RViewAdapter extends RecyclerView.Adapter<RViewAdapter.RViewHolder> {
    private String[] mDataset;
    public static class RViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public RViewHolder(TextView view){
            super(view);
            textView = view;
        }
    }

    public RViewAdapter(String[] rDataset){
        mDataset = rDataset;
    }

    @Override
    public RViewAdapter.RViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        TextView view = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_bluetooth,parent,false);
        RViewHolder viewHolder = new RViewHolder(view);
        return viewHolder;
    };

    @Override
    public void onBindViewHolder(RViewHolder holder, int pos){
        holder.textView.setText(mDataset[pos]);
    };

    /* Size of current dataset*/
    @Override
    public int getItemCount(){
        return mDataset.length;
    }


}
