package com.asiimwetruly.keepsafepolice;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asiimwetruly.keepsafepolice.utils.GetCurTime;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class NotificationsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    private static final String TAG = "NotificationsActivity";
    FirebaseRecyclerAdapter adapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        recyclerView = findViewById(R.id.mainlist);
        progressBar = findViewById(R.id.pro);
toolbar=findViewById(R.id.tb);
setSupportActionBar(toolbar);

getSupportActionBar().setDisplayHomeAsUpEnabled(true);
recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("report_cases");
query.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists()){
            Snackbar.make(recyclerView,"NO REPORTED CASES ",Snackbar.LENGTH_INDEFINITE).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});
        FirebaseRecyclerOptions<
                Notification> options =
                new FirebaseRecyclerOptions.Builder<Notification>()
                        .setQuery(query, Notification.class)
                        .build();


        adapter = new FirebaseRecyclerAdapter<Notification, NotiHolder>(options) {
            @Override
            public NotiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_noti, parent, false);
                return new NotiHolder(view);
            }

            @Override
            protected void onBindViewHolder(NotiHolder holder, int position, final Notification notification) {
               holder.setText(notification.getDescription().concat(" ...").concat(" from ").concat(notification.getDistrict()));
                 holder.setTime(notification.getReported());
                progressBar.setVisibility(View.GONE);
            }
        };
recyclerView.setAdapter(adapter);

    }

    public static class NotiHolder extends RecyclerView.ViewHolder {

        View view;

        public NotiHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setText(String text){
            TextView textView=view.findViewById(R.id.newdesc);
            textView.setText(text);

        }
        public void setTime(long text){
            TextView textView=view.findViewById(R.id.date);
textView.setText(GetCurTime.toDateTime(text));
        }

    }

    @Override
    protected void onStart() {
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        super.onStart();
    }

    @Override
    protected void onStop() {
        adapter.stopListening();
        super.onStop();
    }
}

