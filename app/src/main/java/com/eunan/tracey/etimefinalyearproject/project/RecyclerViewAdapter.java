package com.eunan.tracey.etimefinalyearproject.project;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private Context context;
    private List<ProjectModel> listItems;

    private DatabaseReference projectRef;
    private FirebaseAuth currentUser;
    private String currentUserId;

    public RecyclerViewAdapter(Context context, List<ProjectModel> item) {
        Log.d(TAG, "RecyclerViewAdapter: called");
        this.context = context;
        this.listItems = item;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder: starts");

        currentUser = FirebaseAuth.getInstance();
        currentUserId = currentUser.getUid();
        projectRef = FirebaseDatabase.getInstance().getReference().child("Projects").child(currentUserId);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.project_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        Log.d(TAG, "onBindViewHolder: called");
        final ProjectModel itemList = listItems.get(i);
        holder.title.setText(itemList.getProjectName());
        holder.location.setText(itemList.getProjectLocation());
        holder.description.setText(itemList.getProjectDescription());
        holder.projectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MaintainProject.class);
                intent.putExtra("title", itemList.getProjectName());
                intent.putExtra("location", itemList.getProjectLocation());
                intent.putExtra("description", itemList.getProjectDescription());
                intent.putExtra("timestamp",itemList.getProjectTimestamp());
                context.startActivity(intent);
                Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show();
            }
        });

        holder.optionsDigit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.optionsDigit);
                popupMenu.inflate(R.menu.options_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_item_save:
                                Toast.makeText(context, "Saved", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.menu_item_delete:
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                Query projectQuery = ref.child("Projects").child(currentUserId).
                                        orderByChild("projectLocation").equalTo(itemList.getProjectName());
                                projectQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                                            ds.getRef().removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
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
        public TextView title;
        public TextView location;
        public TextView description;
        public TextView optionsDigit;
        public RelativeLayout projectLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textview_title);
            location = itemView.findViewById(R.id.textview_location);
            description = itemView.findViewById(R.id.textview_description);
            optionsDigit = itemView.findViewById(R.id.textview_options_digit);
            projectLayout = itemView.findViewById(R.id.project_layout_view);
        }
    }
}
