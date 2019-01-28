package com.eunan.tracey.etimefinalyearproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class ProjectDialog extends AppCompatDialogFragment {

    private EditText editTextProject;
    private EditText editTextLocation;
    private ProjectDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_project_dialog,null);

        builder.setView(view)
                .setTitle("Create Project")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String project = editTextProject.getText().toString();
                        String location = editTextLocation.getText().toString();
                    listener.applyData(project,location);
                    }
                });
        editTextProject = view.findViewById(R.id.editTextLoginEmail);
        editTextLocation = view.findViewById(R.id.editTexttLoginPaswordPassword);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ProjectDialogListener) context;
        } catch (ClassCastException e) {
           throw new ClassCastException(context.toString() + " must implement ProjectDialogListener");
        }
    }

    public interface ProjectDialogListener{
        void applyData(String project,String location);
    }
}
