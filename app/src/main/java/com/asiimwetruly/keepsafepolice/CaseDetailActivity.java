package com.asiimwetruly.keepsafepolice;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asiimwetruly.keepsafepolice.utils.GetCurTime;
import com.asiimwetruly.keepsafepolice.utils.GetTimeAgo;
import com.asiimwetruly.keepsafepolice.utils.Handy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class CaseDetailActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView description, distict, date, reported, pho,repby,pro,happened,contact;
    String id;

    TextView mark;

    private static final String TAG = "CaseDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case_detail);
        Toolbar toolbar = findViewById(R.id.touch_outsidee);
        setSupportActionBar(toolbar);
        mark=findViewById(R.id.mark);





        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id = getIntent().getStringExtra("id");
        repby=findViewById(R.id.repby);
        pro=findViewById(R.id.pro);
        happened=findViewById(R.id.hapen);
        init();
        final TextView from=findViewById(R.id.region);

        FirebaseFirestore.getInstance().collection("cases").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
           if (!documentSnapshot.exists()){
               mark.setVisibility(View.GONE);
           }
            }
        });


        FirebaseFirestore.getInstance().collection("cases").document(id).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
try {
    description.setText(documentSnapshot.get("description").toString());


}catch (IllegalStateException ex){
    startActivity(new Intent(getBaseContext(),MainActivity.class));
    Toast.makeText(CaseDetailActivity.this, "Case closed", Toast.LENGTH_SHORT).show();
    finish();
}
                distict.setText(documentSnapshot.get("district").toString());
                contact=findViewById(R.id.reporterphone);
                from.setText(documentSnapshot.get("reportedFrom").toString());
                contact.setText(documentSnapshot.get("phone").toString());
                reported.setText(GetTimeAgo.getTimeAgo((Long) documentSnapshot.get("reported"),getBaseContext()));
                happened.setText(documentSnapshot.get("happened").toString());
                pro.setText(documentSnapshot.get("province").toString());
                repby.setText(documentSnapshot.get("reported_by").toString());
 mark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
 FirebaseFirestore.getInstance().collection("cases").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                            String key = FirebaseFirestore.getInstance().collection("closed_cases").document().getId();
                                            Map<String, Object> stringMap = new HashMap<>();
                                            stringMap.put("phone", documentSnapshot.get("phone").toString());
                                            stringMap.put("id", key);
                                            stringMap.put("happened", documentSnapshot.get("happened").toString());
                                            stringMap.put("reportedFrom", documentSnapshot.get("reported_by").toString());
                                            stringMap.put("description", documentSnapshot.get("description").toString());
                                            stringMap.put("district", documentSnapshot.get("district").toString());
                                            stringMap.put("province", documentSnapshot.get("province").toString());
                                            stringMap.put("closedat", System.currentTimeMillis());
                                            stringMap.put("info", "The case was closed successfully");
                                            stringMap.put("closedby", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                            stringMap.put("closedbyId", FirebaseAuth.getInstance().getCurrentUser().getUid());

                                            FirebaseFirestore.getInstance().collection("closed_cases").document(key).set(stringMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    telluser();
                                                    Toast.makeText(CaseDetailActivity.this, "Case closed", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(CaseDetailActivity.this, "Failed to close case :"+e.getMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }
                                    });







                    }
                });

            }
        });


    }

    private void telluser() {
        FirebaseFirestore.getInstance().collection("cases").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getBaseContext(), "Case has beenclosed", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getBaseContext(),MainActivity.class));
                mark.setVisibility(View.GONE);
            }
        });

    }

    private void init() {
        description = findViewById(R.id.description);
        distict = findViewById(R.id.district);
        reported = findViewById(R.id.reported);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cased, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.check:
                markAsChecked(id);
        }
        return super.onOptionsItemSelected(item);
    }

    private void markAsChecked(String id) {
        Log.d(TAG, "markAsChecked: marking case as resolved");
    }
}
