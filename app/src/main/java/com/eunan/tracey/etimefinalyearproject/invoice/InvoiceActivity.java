package com.eunan.tracey.etimefinalyearproject.invoice;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.employer.EmployerWeek;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Class used to display invoice data th the employer
 */

public class InvoiceActivity extends AppCompatActivity {
    private static final String TAG = "InvoiceActivity";
    @SuppressLint("SimpleDateFormat")
    DateFormat displayDateFormat;
    // Firebase
    private DatabaseReference invoiceRef;
    private DatabaseReference historyRef;
    private String employeeId;
    private String currentUserId;
    InvoiceModel invoiceModel;
    private List<InvoiceModel> invoiceModelList;

    // UI
    private TextView txtInvoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        invoiceModelList = new ArrayList<>();
        txtInvoice = findViewById(R.id.text_view_display_inv);
        Button btnAccept = findViewById(R.id.button_accept_inv);
        // Firebase
        invoiceRef = FirebaseDatabase.getInstance().getReference().child("Invoice");
        historyRef = FirebaseDatabase.getInstance().getReference().child("HistoryInvoice");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }
        employeeId = getIntent().getStringExtra("employeeId");

        readInvoiceData();
        displayDateFormat = new SimpleDateFormat("EEE, MMM d");
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadInvoice();
            }
        });
    }

    private void uploadInvoice() {
        Calendar friday = Calendar.getInstance();
        friday.setTime(new Date());
        friday.set(Calendar.DAY_OF_MONTH, friday.get(Calendar.DAY_OF_MONTH)-38);
//        String pushId = invoiceRef.push().getKey();
//        if (pushId != null) {
            historyRef.child(employeeId).child(displayDateFormat.format(friday.getTime())).setValue(invoiceModel);
            Toast.makeText(InvoiceActivity.this, "Invoice uploaded", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Error uploading invoice. Try again later", Toast.LENGTH_SHORT).show();
//        }
    }

    /**
     * Method to read invoice data from firebase and store it in a list
     */
    private void readInvoiceData() {

        invoiceRef.child(employeeId).child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String date = String.valueOf(dataSnapshot.child("date").getValue());
                String hrs = String.valueOf(dataSnapshot.child("hrs").getValue());
                String name = String.valueOf(dataSnapshot.child("name").getValue());
                String project = String.valueOf(dataSnapshot.child("project").getValue());
                String rate = String.valueOf(dataSnapshot.child("rate").getValue());
                String total = String.valueOf(dataSnapshot.child("total").getValue());
                invoiceModel = new InvoiceModel(name, date, project, hrs, rate, total);
                invoiceModelList.add(invoiceModel);
                printTimeSheet(invoiceModelList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(InvoiceActivity.this, String.valueOf(databaseError), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * @param invoiceModelList used to display invoice data to the UI
     */
    private void printTimeSheet(List<InvoiceModel> invoiceModelList) {
        StringBuilder builder = new StringBuilder();
        for (InvoiceModel invoice : invoiceModelList) {
            builder.append("").append(invoice.getName()).append("\n").append("Hours: " + invoice.getHrs()).append("hrs").append("\n").
                    append("Project: " + invoice.getProject()).append("\n").append("Hourly Rate: " + invoice.getRate()).append("\n")
                    .append("Total: " + invoice.getTotal());
        }
        txtInvoice.setText(builder.toString());
        Log.d(TAG, "printTimeSheet: \n" + builder.toString());
    }

}
