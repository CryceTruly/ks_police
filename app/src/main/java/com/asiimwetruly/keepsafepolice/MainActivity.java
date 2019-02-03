package com.asiimwetruly.keepsafepolice;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asiimwetruly.keepsafepolice.utils.Utils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {
FirebaseAuth auth;
    private static final String TAG = "MainActivity";

    RecyclerView recyclerView;
    ProgressBar progressBar;
FirestoreRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        initToolbar();

        if (user==null){
            Log.d(TAG, "onCreate: ");

            goToLogin();
        }else {
            init();
            configureAdapter();
        }
    }

    private void initToolbar() {
        Toolbar toolbar=findViewById(R.id.tolbar);

    setSupportActionBar(toolbar);}

    private void configureAdapter() {
        com.google.firebase.firestore.Query query;



            query = FirebaseFirestore.getInstance()
                    .collection("cases").orderBy("reported", Query.Direction.DESCENDING);

        //todo afix for location on phones probaby build a serve with no failure issues.whereEqualTo("locality", Preferences.getLocality(getContext()));


        FirestoreRecyclerOptions<Case> options = new FirestoreRecyclerOptions.Builder<Case>()
                .setQuery(query, Case.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<Case, CaseHolder>(options) {
            @Override
            public void onBindViewHolder(@NonNull final CaseHolder holder, final int position, final Case model) {
                final String key = getSnapshots().getSnapshot(position).getId();

                // Create a storage reference from our app
holder.setDescription(model.getDescription());
holder.setPhone(model.getPhone());
holder.setTown(model.getDistrict());
holder.setReported(Utils.timestampToTime(model.getReported()));
holder.setRegion(model.getReportedFrom());
progressBar.setVisibility(View.GONE);


holder.view.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent i=new Intent(getBaseContext(),CaseDetailActivity.class);
        i.putExtra("id",key);
        startActivity(i);
    }
});


            }

            @Override
            public CaseHolder onCreateViewHolder(ViewGroup group, int i) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.single_case, group, false);

                return new CaseHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);

    }

    private void init() {
        recyclerView=findViewById(R.id.mainlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        progressBar=findViewById(R.id.progress_bar);
        
        progressBar.setVisibility(View.VISIBLE);
        
    }

    private void goToLogin() {
        Log.d(TAG, "goToLogin: ");
        Intent intent=new Intent(this,AuthActivity.class);
intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.logout){
            AuthUI.getInstance().signOut(getBaseContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    goToLogin();
                }
            });
        }

        if (item.getItemId()==R.id.noti){
          startActivity(new Intent(getBaseContext(),NotificationsActivity.class));
                }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        adapter.startListening();
        super.onStart();
    }

    @Override
    protected void onStop() {
        adapter.stopListening();
        super.onStop();
    }


    public static class CaseHolder extends RecyclerView.ViewHolder{
View view;
        public CaseHolder(View itemView) {
            super(itemView);
            view=itemView;
        }

        public void setReported(String reported){
            TextView textView=view.findViewById(R.id.reported);
            textView.setText(String.valueOf(reported));
        }
        public void setTown(String dustr){
            TextView dist=view.findViewById(R.id.district);
            dist.setText(dustr);
        }

        public void setDescription(String description1){
            TextView des=view.findViewById(R.id.description);
            des.setText(description1);
        }

        public void setRegion(String region){
            TextView reg=view.findViewById(R.id.region);
            reg.setText(region);
        }

        public void setPhone(String ph){
            TextView pho=view.findViewById(R.id.reporterphone);
            pho.setText(ph);
        }
    }
}
