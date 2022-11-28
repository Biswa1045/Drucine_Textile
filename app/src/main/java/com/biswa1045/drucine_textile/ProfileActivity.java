package com.biswa1045.drucine_textile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProfileActivity extends AppCompatActivity {
    TextView email,name;
    ImageView user_img;
    Dialog dialog;
    String userid;
  //  DatabaseReference reference;
    DocumentReference documentReference;
    FirebaseFirestore fStore;
    String name_fs,room_fs,hostel_fs,cont_fs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Random random = new Random();
        random.nextInt(4);

         dialog = new Dialog(this);
         email = findViewById(R.id.user_email);
         name = findViewById(R.id.user_name);
         user_img =  findViewById(R.id.user_img);
//       DocumentReference documentReference = fStore.collection("Textile_users").document(userid).collection("address").document();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            String username = user.getDisplayName();
            String email_str = user.getEmail();
            Uri img = user.getPhotoUrl();
            email.setText(email_str);
            name.setText(username);
            user_img.setImageURI(img);
        }
        fStore = FirebaseFirestore.getInstance();
        userid = user.getUid();
      //  reference = FirebaseDatabase.getInstance().getReference().child("user").child(userid);
        findViewById(R.id.address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showpopup();
            }
        });
        findViewById(R.id.back_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ProfileActivity.this,HomeActivity.class);
                startActivity(in);
                finish();
            }
        });
         documentReference = fStore.collection("Textile_users").document(userid).collection("address").document("data");
        fStore.collection("Textile_users").document(userid).collection("address").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()){

                    name_fs = documentChange.getDocument().getData().get("username").toString();
                    room_fs = documentChange.getDocument().getData().get("roomno").toString();
                    hostel_fs = documentChange.getDocument().getData().get("hostel").toString();
                    cont_fs = documentChange.getDocument().getData().get("contact").toString();

                }
            }
        });
    }
    public void showpopup(){
        ImageView cancel_dailog;
        EditText username,hostel_name,contno,room_no;
        dialog.setContentView(R.layout.address_dialog);

        username = dialog.findViewById(R.id.name);
        hostel_name = dialog.findViewById(R.id.hostel_name);
        contno = dialog.findViewById(R.id.contno);
        room_no = dialog.findViewById(R.id.room_no);

        username.setText( name_fs);
        hostel_name.setText(hostel_fs);
        contno.setText( cont_fs);
        room_no.setText( room_fs);

        cancel_dailog = dialog.findViewById(R.id.cancel_dailog);
        cancel_dailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username_str =  username.getText().toString();
                String hostel_str =  hostel_name.getText().toString();
                String room_str =  room_no.getText().toString();
                String cont_str =  contno.getText().toString();
                if(username_str.equals("")) {
                    username.setError("Enter name");

                }else if (hostel_str.equals("")) {
                    hostel_name.setError("Enter hostel name");

                }else if(room_str.equals("")) {
                    room_no.setError("Enter room no");

                }else if(cont_str.equals("")) {
                    contno.setError("Enter contact no");

                }else {
                    Map<String, Object> user_add = new HashMap<>();
                    user_add.put("username", username_str);
                    user_add.put("hostel",hostel_str);
                    user_add.put("roomno",room_str);
                    user_add.put("contact",cont_str);
                    documentReference.set(user_add).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(ProfileActivity.this, "Address Saved", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Address not saved", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}