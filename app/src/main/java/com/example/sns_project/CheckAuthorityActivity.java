package com.example.sns_project;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sns_project.Posts.PostInfo;
import com.example.sns_project.Posts.WritePostActivity;
import com.example.sns_project.SignLogins.LoginActivity;
import com.example.sns_project.SignLogins.MemberInitActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class CheckAuthorityActivity extends BasicActivity {
    private static final String TAG = "CheckAuthorityActivity";
    private static DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkauthority);
        final String[] CurrentName = new String[1];
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // ??????????????? ??????????????? ??????
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        if (document.exists()) {
                            mDatabase.child("Users").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                    }
                                    else {
                                        CurrentName[0] = (String) document.getData().get("name");
                                        mDatabase.child("Users").child(user.getUid()).child("??????").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String str = dataSnapshot.getValue().toString();
                                                String[] array = str.split("/");
                                                if(dataSnapshot.getValue().toString().equals("?????? ??????") ){

                                                } else if(array[0].equals("??????????????????")){
                                                    DialogClick(array[1], array[2]);
                                                }
                                                else{
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError error) {
                                                // Failed to read value
                                                Log.w(TAG, "Failed to read value.", error.toException());
                                            }
                                        });
                                    }
                                }
                            });

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        TextView AuthorityEmailText = (TextView) findViewById(R.id.AuthorityEmailText);
        AuthorityEmailText.setText("????????? ????????? : " + user.getEmail());

        // ?????? ?????????
        findViewById(R.id.AuthoritySendButton).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.AuthoritySendButton:
                    EditText ParentId = (EditText)findViewById(R.id.AuthorityParentId);
                    SendData(ParentId.getText().toString());
                    startToast(ParentId.getText().toString()+"?????? ??????????????? ??????");
                    finish();
                    break;
            }
        }
    };

    private void SendData(String ParentId){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String[] ChildName = new String[1];

        DocumentReference docRefChild = db.collection("Users").document(user.getUid());
        docRefChild.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        if (document.exists()) {
                            ChildName[0] = (String) document.getData().get("name");
                            db.collection("Users")
                                    .whereEqualTo("email", ParentId)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    mDatabase.child("Users")
                                                            .child(document.getData().get("uidCode").toString())
                                                            .child("??????")
                                                            .setValue("??????????????????/"+ChildName[0]+"/"+user.getUid());
                                                }
                                            } else {
                                                Log.d("???????????????", "No such document");
                                            }
                                        }
                                    });
                        } else {
                            Log.d("???????????????", "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    public void DialogClick(String name, String uidCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("????????????").setMessage(name+"?????? ????????? ?????? ????????? ?????????????????????????");
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Okay(name, uidCode);
                Toast.makeText(getApplicationContext(), "Yeah!!",Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "Try again",Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void Okay(String name, String UidCode){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DocumentReference docRef = db.collection("Users").document(user.getUid());

        db.collection("Users")
                .whereEqualTo("uidCode", UidCode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mDatabase.child("Users").child(user.getUid()).child("????????? ??????").setValue(UidCode);
                                mDatabase.child("Users").child(user.getUid()).child("??????").setValue(name+"??? ?????????");
                                mDatabase.child("Users").child(UidCode).child("????????? ??????").setValue(user.getUid());
                            }
                        } else {
                            Log.d("???????????????", "No such document");
                        }
                    }
                });

      docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
          @Override
         public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if(document != null){
                  if (document.exists()) {
                      mDatabase.child("Users").child(UidCode).child("??????").setValue(document.getData().get("name")+"??? ?????????");
                      } else {
                         Log.d("?????????", "No such document");
                     }
                 }
             } else {
                   Log.d(TAG, "get failed with ", task.getException());
              }
           }
     });


    }


    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
