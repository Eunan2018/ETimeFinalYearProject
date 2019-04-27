package com.eunan.tracey.etimefinalyearproject.History;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.eunan.tracey.etimefinalyearproject.invoice.EmployerInvoiceActivity;
import com.eunan.tracey.etimefinalyearproject.invoice.InvoiceModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayInvoiceHistory extends AppCompatActivity {


    private static final String TAG = "EmployerInvoiceActivity";
    // Firebase
    private DatabaseReference historyRef;

    // Class
    private String currentUserId;
    private String date;
    private InvoiceModel invoiceModel;
    private List<InvoiceModel> invoiceModelList;
    private TextView txtHrRate, txtTotal, txtHrs, txtProject;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_invoice_history);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        toolbar = findViewById(R.id.time_sheet_app_bar);
        setSupportActionBar(toolbar);
        Drawable dr = ContextCompat.getDrawable(this, R.drawable.timesheet);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 100, true));
        getSupportActionBar().setLogo(d);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtHrRate = findViewById(R.id.text_view_emp_inv_hr);
        txtHrs = findViewById(R.id.text_view_emp_inv_hrs);
        txtTotal = findViewById(R.id.text_view_emp_total_inv);
        txtProject = findViewById(R.id.text_view_proj_inv_emp);

        date = getIntent().getStringExtra("date");
        historyRef = FirebaseDatabase.getInstance().getReference().child("HistoryInvoice");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }
        invoiceModelList = new ArrayList<>();
        readInvoiceData();
    }



    /**
     * Method to read invoice data from firebase and store it in a list
     */
    private void readInvoiceData() {
        historyRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    if(TextUtils.equals(childSnapshot.getKey(),date)) {
                        String hrs = String.valueOf(childSnapshot.child("hrs").getValue());
                        String project = String.valueOf(childSnapshot.child("project").getValue());
                        String rate = String.valueOf(childSnapshot.child("rate").getValue());
                        String total = String.valueOf(childSnapshot.child("total").getValue());
                        invoiceModel = new InvoiceModel(project, hrs, rate, total);
                        invoiceModelList.add(invoiceModel);
                        printInvoice(invoiceModelList);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DisplayInvoiceHistory.this, String.valueOf(databaseError), Toast.LENGTH_SHORT).show();
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
