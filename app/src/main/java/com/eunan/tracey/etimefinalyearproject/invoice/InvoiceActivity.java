package com.eunan.tracey.etimefinalyearproject.invoice;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.FridayDate;
import com.eunan.tracey.etimefinalyearproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to display invoice data th the employer
 */

public class InvoiceActivity extends AppCompatActivity {
    private static final String TAG = "InvoiceActivity";
    // Firebase
    private DatabaseReference invoiceRef;
    private DatabaseReference historyRef;

    ValueEventListener listener;
    // Class
    private String employeeId;
    private String currentUserId;
    private InvoiceModel invoiceModel;
    private List<InvoiceModel> invoiceModelList;
    // UI
    private TextView txtInvoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        // Firebase
        invoiceRef = FirebaseDatabase.getInstance().getReference().child("Invoice");
        historyRef = FirebaseDatabase.getInstance().getReference().child("HistoryInvoice");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }
        // Class
        employeeId = getIntent().getStringExtra("id");
        invoiceModelList = new ArrayList<>();
        readInvoiceData();
        // UI
        txtInvoice = findViewById(R.id.text_view_display_inv);
        Button btnAccept = findViewById(R.id.button_accept_inv);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadInvoice();
            }
        });
    }

    /**
     * Method used to upload invoice data to firebase
     */
    private void uploadInvoice() {
            historyRef.child(employeeId).child(FridayDate.fridayDate()).setValue(invoiceModel);
            Toast.makeText(InvoiceActivity.this, "Invoice uploaded", Toast.LENGTH_SHORT).show();
    }

    /**
     * Method to read invoice data from firebase and store it in a list
     */
    private void readInvoiceData() {

         invoiceRef.child(employeeId).child(currentUserId).addValueEventListener(new ValueEventListener() {
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
    private void printTimeSheet(@NonNull List<InvoiceModel> invoiceModelList) {
        StringBuilder builder = new StringBuilder();
        for (InvoiceModel invoice : invoiceModelList) {
            builder.append("").append(invoice.getName()).append("\n").append("Hours: " + invoice.getHrs()).append("hrs").append("\n").
                    append("Project: " + invoice.getProject()).append("\n").append("Hourly Rate: " + invoice.getRate()).append("\n")
                    .append("Total: " + invoice.getTotal());
        }
        txtInvoice.setText(builder.toString());
        Log.d(TAG, "printTimeSheet: \n" + builder.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
