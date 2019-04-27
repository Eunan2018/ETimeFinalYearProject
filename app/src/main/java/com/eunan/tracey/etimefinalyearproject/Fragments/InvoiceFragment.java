package com.eunan.tracey.etimefinalyearproject.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.eunan.tracey.etimefinalyearproject.MessageModel;
import com.eunan.tracey.etimefinalyearproject.R;
import com.eunan.tracey.etimefinalyearproject.employee.EmployeeProjectModel;
import com.eunan.tracey.etimefinalyearproject.invoice.InvoiceModel;
import com.eunan.tracey.etimefinalyearproject.salary.SalaryCalculator;
import com.eunan.tracey.etimefinalyearproject.timesheet.TimeSheetBuilder;
import com.eunan.tracey.etimefinalyearproject.timesheet.TimeSheetModel;
import com.eunan.tracey.etimefinalyearproject.timesheet.Weekday;
import com.eunan.tracey.etimefinalyearproject.upload.UploadActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.exoplayer.C;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.regex.Pattern;


public class InvoiceFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "InvoiceFragment";
    private RecyclerView recyclerView;
    // Firebase
    private DatabaseReference assignedRef, invoiceRef, commentsRef;
    private ProgressDialog progressDialog;
    private String userId, projectName, employerKey;
    private String hrs = "";
    private String rate = "";
    private Pattern pos = Pattern.compile("^\\d*\\.?\\d+$");
    public static LinkedHashMap<String, InvoiceModel> invoiceMap = new LinkedHashMap<>();
    private final ArrayList<Integer> selectionList = new ArrayList<>();
    private ImageView imageView;
    private TextView txtTotal;
    private EditText edtHrs, edtRate,edtComments;
    double total;
    @SuppressLint("SimpleDateFormat")
    private DateFormat displayDateFormat;

    public InvoiceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseReference employerRef = FirebaseDatabase.getInstance().getReference("Employer");
        employerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    employerKey = childSnapshot.getKey();
                    Log.d(TAG, "onDataChange: employer key" + employerKey);
                    for (DataSnapshot postChildSnapshot : childSnapshot.getChildren()) {
                        String employeeKey = postChildSnapshot.getKey();
                        if (TextUtils.equals(employeeKey, userId)) {
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + String.valueOf(databaseError));
                Toast.makeText(getContext(), String.valueOf(databaseError), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_invoice, container, false);

        TextClock textClock = new TextClock(getContext());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        // Inflate the layout for this fragment
        recyclerView = v.findViewById(R.id.recycler_view_invoice);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        displayDateFormat = new SimpleDateFormat("EEE, MMM d", Locale.UK);
        // Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();
        assignedRef = FirebaseDatabase.getInstance().getReference("EmployeeProjects");
        commentsRef = FirebaseDatabase.getInstance().getReference("Comments");
        txtTotal = v.findViewById(R.id.text_view_total);
        edtHrs = v.findViewById(R.id.edit_text_hrs_inv);
        edtRate = v.findViewById(R.id.edit_text_rate_inv);
        edtComments = v.findViewById(R.id.edit_text_inv_comments);
        textClock.setText(textClock.getText().toString());
        Button btnCalculate = v.findViewById(R.id.button_calculate_inv);
        final Button btnSubmit = v.findViewById(R.id.button_send_invoice);
        Button btnCancel = v.findViewById(R.id.button_cancel_invoice);

        Calendar friday = Calendar.getInstance();
        friday.setTime(new Date());
        friday.set(Calendar.DAY_OF_MONTH, friday.get(Calendar.DAY_OF_MONTH));
        invoiceRef = FirebaseDatabase.getInstance().getReference().child("Invoice");
        imageView = v.findViewById(R.id.image_view_inv);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UploadActivity.class);
                intent.putExtra("key2", userId);
                intent.putExtra("key1", employerKey);
                startActivity(intent);

            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: submit " + selectionList.size());
                Log.d(TAG, "onClick: submit " + selectionList.toString());
                if (selectionList.get(0).equals(1) && !hrs.isEmpty() && !rate.isEmpty() && total != 0) {
                    Log.d(TAG, "onClick: " + String.valueOf(total).isEmpty() + " " + total);
                    Log.d(TAG, "onClick: " + String.valueOf(total).isEmpty());
                    progressDialog.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnSubmit.setEnabled(false);
                            btnSubmit.setBackground(getResources().getDrawable(R.drawable.round_button_gray));
                            Toast.makeText(getContext(), "Cannot send another invoice for 20 minutes", Toast.LENGTH_SHORT).show();
                            new CountDownTimer(20000, 10) { //Set Timer for 20 seconds
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    Toast.makeText(getContext(), "Send again ", Toast.LENGTH_SHORT).show();
                                    btnSubmit.setEnabled(true);
                                    btnSubmit.setBackground(getResources().getDrawable(R.drawable.round_button));
                                }
                            }.start();

                            progressDialog.cancel();
                            uploadInvoice();
                        }
                    }, 1000);
                } else {
                    Toast.makeText(getContext(), "Must complete all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateInvoice();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invoiceMap.clear();
                total = 0;
                projectName = "";
                edtHrs.setText("");
                edtRate.setText("");
                txtTotal.setText("Total");
                edtHrs.setFocusable(true);
                selectionList.set(0, 0);
            }
        });
        return v;
    }

    private void uploadInvoice() {
        Calendar friday = Calendar.getInstance();
        friday.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        InvoiceModel invoiceModel = new InvoiceModel(projectName, hrs, rate, String.valueOf(total));
        invoiceMap.put(displayDateFormat.format(friday.getTime()), invoiceModel);
        MessageModel messageModel = new MessageModel(edtComments.getText().toString(), "");
        String pushId = commentsRef.push().getKey();
        commentsRef.child(userId).child(employerKey).child(pushId).setValue(messageModel);
        invoiceRef.child(userId).child(employerKey).setValue(invoiceMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                total = 0;
                projectName = "";
                edtHrs.setText("");
                edtRate.setText("");
                txtTotal.setText("Total");
                selectionList.add(0, 0);
            }
        });

    }

    private void calculateInvoice() {
        Log.d(TAG, "calculateInvoice: " + selectionList.size());
        Log.d(TAG, "calculateInvoice: " + selectionList.toString());
        hrs = edtHrs.getText().toString();
        rate = edtRate.getText().toString();
        if (hrs.isEmpty() || !pos.matcher(hrs).matches()) {
            Toast.makeText(getContext(), "Hours cannot be empty.", Toast.LENGTH_SHORT).show();
            edtHrs.setFocusable(true);
        } else if (rate.isEmpty() || !pos.matcher(rate).matches()) {
            Toast.makeText(getContext(), "Rate cannot be empty.", Toast.LENGTH_SHORT).show();
            edtRate.setFocusable(true);
        } else {
            total = SalaryCalculator.calculateSalary(Double.parseDouble(hrs), Double.parseDouble(rate), 1);
            txtTotal.setText("£" + String.valueOf((total)));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance().getReference().child("EmployeeProjects")
                .child(userId);
        FirebaseRecyclerOptions<EmployeeProjectModel> options =
                new FirebaseRecyclerOptions.Builder<EmployeeProjectModel>()
                        .setQuery(query, EmployeeProjectModel.class)
                        .build();

        final FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<EmployeeProjectModel, EmployeeViewHolder>(options) {

            @Override
            public EmployeeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder: starts");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.project_layout_employee, parent, false);
                return new EmployeeViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final EmployeeViewHolder employeeViewHolder, final int position, @NonNull final EmployeeProjectModel employee) {
                Log.d(TAG, "onBindViewHolder: starts");
                selectionList.add(0, 0);
                Log.d(TAG, "onBindViewHolder: " + selectionList.size());
                final String userId = getRef(position).getKey();
                if (userId != null) {
                    assignedRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                String name = String.valueOf(postSnapshot.getValue());
                                employeeViewHolder.setName(name);
                                Log.d(TAG, "onDataChange: Winner " + postSnapshot.getValue());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: " + String.valueOf(databaseError));
                            Toast.makeText(getContext(), String.valueOf(databaseError), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Error userId is null", Toast.LENGTH_SHORT).show();
                }

                employeeViewHolder.setName(employee.getProject());

                employeeViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectionList.get(0).equals(0)) {
                            Log.d(TAG, "onClick: viewHolder" + selectionList.size());
                            employeeViewHolder.view.setBackgroundColor(Color.GRAY);
                            projectName = employeeViewHolder.getName();
                            selectionList.set(0, 1);
                            Log.d(TAG, "onClick: viewHolder" + selectionList.size());
                        } else {
                            Toast.makeText(getContext(), projectName + " already " +
                                    "selected.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                employeeViewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        selectionList.set(0, 0);
                        Log.d(TAG, "onLongClick: viewHolder" + selectionList.size());
                        employeeViewHolder.view.setBackgroundColor(Color.TRANSPARENT);
                        projectName = "";
                        return true;
                    }
                });
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }


    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        private final static String TAG = "EmployeeViewHolder";
        View view;
        String pName;

        public EmployeeViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "EmployeeViewHolder: starts");
            view = itemView;
        }


        public void setName(String name) {
            TextView userName = view.findViewById(R.id.textview_title);
            userName.setText(name);
            pName = name;
        }

        public String getName() {
            return pName;
        }
    }
}
