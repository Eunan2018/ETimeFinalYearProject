package com.eunan.tracey.etimefinalyearproject;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ListAdapters extends RecyclerView.Adapter {
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.project_list,viewGroup,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
       ((ListViewHolder)viewHolder).bindView(i);
    }

    @Override
    public int getItemCount() {
        return Data.x.length;
    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView location;
        private TextView projectName;

        public ListViewHolder(View v){
            super(v);

            projectName = v.findViewById(R.id.textViewp);
            location = v.findViewById(R.id.textViewl);
        }

        public void bindView(int position){
            location.setText(Data.x[position]);
            projectName.setText(Data.y[position]);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
