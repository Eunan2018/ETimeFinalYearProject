package com.eunan.tracey.etimefinalyearproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private Context context;
    private  List<Project> listItems;

    public RecyclerViewAdapter(Context context, List<Project> item) {
        Log.d(TAG, "RecyclerViewAdapter: called");
        this.context = context;
        this.listItems = item;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder: starts");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.project_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        Log.d(TAG, "onBindViewHolder: called");
        final Project itemList = listItems.get(i);
        holder.txtTitle.setText(itemList.getProjectName());
        holder.txtLocation.setText(itemList.getProjectLocation());
        holder.txtDescription.setText(itemList.getProjectDescription());


        holder.txtOptionsDigit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context,holder.txtOptionsDigit);
                popupMenu.inflate(R.menu.options_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_item_save :
                            Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show();
                            break;
                            case R.id.menu_item_delete :
                                Toast.makeText(context, "Deleted", Toast.LENGTH_LONG).show();
                                break;
                                default:
                                    break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        Log.d(TAG, "onBindViewHolder: ends");
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle;
        public TextView txtLocation;
        public TextView txtDescription;
        public TextView txtOptionsDigit;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.textTitle);
            txtLocation = itemView.findViewById(R.id.textLocation);
            txtDescription = itemView.findViewById(R.id.textDescription);
            txtOptionsDigit = itemView.findViewById(R.id.textOptionsDigit);
        }
    }
}
