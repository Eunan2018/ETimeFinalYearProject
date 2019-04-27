package com.eunan.tracey.etimefinalyearproject.invoice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.FridayDate;
import com.eunan.tracey.etimefinalyearproject.MessageModel;
import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.employer.EmpImage;
import com.eunan.tracey.etimefinalyearproject.employer.EmployerProfileActivity;
import com.eunan.tracey.etimefinalyearproject.employer.EmployerTimeSheetActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used to display invoice data to the employer
 */

public class EmployerInvoiceActivity extends AppCompatActivity {
    private static final String TAG = "EmployerInvoiceActivity";
    // Firebase
    private DatabaseReference invoiceRef, historyRef, declineRef;
    private TextClock textClock;
    // Class
    private String employeeId, currentUserId;
    private InvoiceModel invoiceModel;
    private List<InvoiceModel> invoiceModelList;
    private MessageModel messageModel;
    // UI
    private TextView txtHrRate, txtTotal, txtHrs, txtProject,txtComments;
    private Button btnAttach;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private DatabaseReference commentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        toolbar = findViewById(R.id.time_sheet_app_bar);
        setSupportActionBar(toolbar);
        Drawable dr = ContextCompat.getDrawable(this, R.drawable.timesheet);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
        getSupportActionBar().setLogo(d);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(EmployerInvoiceActivity.this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        textClock = new TextClock(this);
        txtHrRate = findViewById(R.id.text_view_emp_inv_hr);
        txtHrs = findViewById(R.id.text_view_emp_inv_hrs);
        txtTotal = findViewById(R.id.text_view_emp_total_inv);
        txtProject = findViewById(R.id.text_view_proj_inv_emp);
        txtComments = findViewById(R.id.text_view_emp_inv_comments);
        btnAttach = findViewById(R.id.button_inv_attach);
        // Firebase
        invoiceRef = FirebaseDatabase.getInstance().getReference().child("Invoice");
        invoiceRef.keepSynced(true);
        historyRef = FirebaseDatabase.getInstance().getReference().child("HistoryInvoice");
        declineRef = FirebaseDatabase.getInstance().getReference().child("HistoryInvoice");
        commentsRef = FirebaseDatabase.getInstance().getReference().child("Comments");
        commentsRef.keepSynced(true);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }
        // Class
        employeeId = getIntent().getStringExtra("id");
        invoiceModelList = new ArrayList<>();
        readInvoiceData();
        // UI
        Button btnAccept = findViewById(R.id.button_accept_inv);
        Button btnDecline = findViewById(R.id.button_decline_inv);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(EmployerInvoiceActivity.this);
                alert.setTitle("Invoice");
                alert.setMessage("£" +  String.valueOf(invoiceModel.getTotal()));
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        progressDialog.show();
                        uploadInvoice();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alert.show();
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(EmployerInvoiceActivity.this);
                final EditText edittext = new EditText(getApplicationContext());
                alert.setTitle("Reason");
                alert.setView(edittext);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String message = edittext.getText().toString();
                        messageModel = new MessageModel(message, "default");
                        declineRef.child(employeeId).setValue(messageModel);
                        Log.d(TAG, "onClick: text: " + message);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();
            }
        });

        textClock.setText(textClock.getText().toString());

        btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployerInvoiceActivity.this, EmpImage.class);
                intent.putExtra("key1", currentUserId);
                intent.putExtra("key2", employeeId);
                startActivity(intent);
            }
        });
    }

    /**
     * Method used to upload invoice data to firebase
     */
    private void uploadInvoice() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.cancel();
                historyRef.child(employeeId).child(FridayDate.fridayDate()).setValue(invoiceModel);
                Toast.makeText(EmployerInvoiceActivity.this, "Invoice uploaded", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EmployerInvoiceActivity.this, EmployerProfileActivity.class));
            }
        }, 1000);
    }

    /**
     * Method to read invoice data from firebase and store it in a list
     */
    private void readInvoiceData() {

        invoiceRef.child(employeeId).child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String hrs = String.valueOf(childSnapshot.child("hrs").getValue());
                    if (!hrs.equals("null")) {
                        String project = String.valueOf(childSnapshot.child("project").getValue());
                        String rate = String.valueOf(childSnapshot.child("rate").getValue());
                        String total = String.valueOf(childSnapshot.child("total").getValue());
                        invoiceModel = new InvoiceModel(project, hrs, rate, total);
                        invoiceModelList.add(invoiceModel);
                        printInvoice(invoiceModelList);
                    }
                }
                commentsRef.child(employeeId).child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            String comments = String.valueOf(ds.child("message").getValue());
                            txtComments.setText(comments);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EmployerInvoiceActivity.this, String.valueOf(databaseError), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @param invoiceModelList used to display invoice data to the UI
     */
    private void printInvoice(@NonNull List<InvoiceModel> invoiceModelList) {
        Log.d(TAG, "printTimeSheet: " + invoiceModelList.size());
        for (InvoiceModel invoice : invoiceModelList) {
            txtProject.setText(invoice.getProject());
            txtTotal.setText("£" +  invoice.getTotal());
            txtHrs.setText(invoice.getHrs());
            txtHrRate.setText("£" + invoice.getRate());
        }

    }

}
