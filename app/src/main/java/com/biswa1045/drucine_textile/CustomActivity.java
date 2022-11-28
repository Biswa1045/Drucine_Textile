package com.biswa1045.drucine_textile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomActivity extends AppCompatActivity {
    ValueEventListener listener;
    ArrayAdapter<String> adapter_style,adapter_printtype,adapter_material, adapter_size;
    ArrayList<String> spinnerData_style,spinnerData_printtype,spinnerData_material, spinnerData_size;
    String style_str,printtype_str,material_str, size_str,quantity_str;
    DatabaseReference databaseReference1,databaseReference2,databaseReference3,databaseReference4;
    CheckBox frontprint_chk,backprint_chk,sideprint_chk;
    Spinner style_spinner,printtype_spinner,material_spinner, size_spinner;
    FirebaseFirestore fStore;
    String userid,firebase_img_uri1,firebase_img_uri2,remark_str;
    ImageView img1,img2;
    TextView price_custom;
    EditText quantity_custom,remark;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 10,PICK_IMAGE_REQUEST2 = 11;
    String frontprint_str,backprint_str,sideprint_str;
    private Uri img_uri1,img_uri2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        storageReference = FirebaseStorage.getInstance().getReference("images");
        frontprint_chk = findViewById(R.id.frontprint_chk);
        backprint_chk = findViewById(R.id.backprint_chk);
        sideprint_chk = findViewById(R.id.sideprint_chk);
        price_custom = findViewById(R.id.price_custom);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        quantity_custom = findViewById(R.id.quantity_custom);
        fStore = FirebaseFirestore.getInstance();

        remark = findViewById(R.id.remark_custom);
        style_spinner = findViewById(R.id.style_spinner);
        printtype_spinner = findViewById(R.id.printtype_spinner);
        material_spinner = findViewById(R.id.material_spinner);
        size_spinner = findViewById(R.id.size_spinner);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        userid = user.getUid();
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Textile").child("style");
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Textile").child("printtype");
        databaseReference3 = FirebaseDatabase.getInstance().getReference().child("Textile").child("material");
        databaseReference4 = FirebaseDatabase.getInstance().getReference().child("Textile").child("size");

        spinnerData_style= new ArrayList<>();
        spinnerData_printtype= new ArrayList<>();
        spinnerData_material= new ArrayList<>();
        spinnerData_size = new ArrayList<>();

        adapter_style = new ArrayAdapter<String>(CustomActivity.this,
                android.R.layout.simple_spinner_dropdown_item,spinnerData_style);

        adapter_printtype = new ArrayAdapter<String>(CustomActivity.this,
                android.R.layout.simple_spinner_dropdown_item,spinnerData_printtype);
        adapter_material = new ArrayAdapter<String>(CustomActivity.this,
                android.R.layout.simple_spinner_dropdown_item,spinnerData_material);
        adapter_size = new ArrayAdapter<String>(CustomActivity.this,
                android.R.layout.simple_spinner_dropdown_item, spinnerData_size);


        style_spinner.setAdapter(adapter_style);
        printtype_spinner.setAdapter(adapter_printtype);
        material_spinner.setAdapter(adapter_material);
        size_spinner.setAdapter(adapter_size);

        add_style();
        add_printtype();
        add_material();
        add_size();

        findViewById(R.id.calldirect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(CustomActivity.this,DirectActivity.class);
                startActivity(k);
                finish();
            }
        });
        findViewById(R.id.buttom_custom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                style_str = style_spinner.getSelectedItem().toString();
                printtype_str = printtype_spinner.getSelectedItem().toString();
                material_str = material_spinner.getSelectedItem().toString();
                size_str = size_spinner.getSelectedItem().toString();
                quantity_str = quantity_custom.getText().toString();
                remark_str = remark.getText().toString();
                try{
                    if(!style_str.equals("")&&!printtype_str.equals("")&&!material_str.equals("")&&!size_str.equals("")){
                        if(frontprint_chk.isChecked()){
                            frontprint_str="c";
                        }
                        else{
                            frontprint_str="nc";
                        }
                        if(backprint_chk.isChecked()){
                            backprint_str="c";
                        }
                        else {
                            backprint_str="nc";
                        }
                        if(sideprint_chk.isChecked()){
                            sideprint_str="c";
                        }
                        else {
                            sideprint_str="nc";
                        }






                            //upload order
                            Date time = Calendar.getInstance().getTime();
                            if(img_uri1 != null){
                                ProgressDialog progressDialog
                                        = new ProgressDialog(CustomActivity.this);
                                progressDialog.setTitle("Uploading...");
                                progressDialog.show();
                                StorageReference ref =storageReference.child("textile_cart").child("img1"+time+"."+getfileExt1(img_uri1));
                                ref.putFile(img_uri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        if(img_uri2 == null){
                                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    firebase_img_uri1 = uri.toString();

                                                            //upload data

                                                            String cart_str = fStore.collection("Textile_users").document(userid).collection("cart").document().getId();
                                                            DocumentReference documentReference_cart = fStore.collection("Textile_users").document(userid).collection("cart").document(cart_str);
                                                            Map<String, Object> cart_data = new HashMap<>();
                                                            cart_data.put("style", style_str);
                                                            cart_data.put("material",material_str);
                                                            cart_data.put("printtype",printtype_str);
                                                            cart_data.put("size",size_str);
                                                            cart_data.put("frontprint",frontprint_str);
                                                            cart_data.put("backprint",backprint_str);
                                                            cart_data.put("sideprint",sideprint_str);
                                                            cart_data.put("design_img",firebase_img_uri1);
                                                            cart_data.put("extra_img","");
                                                            cart_data.put("quantity",quantity_str);
                                                            cart_data.put("price","");
                                                            cart_data.put("time",time);
                                                            cart_data.put("id",cart_str);
                                                            cart_data.put("remark",remark_str);
                                                            cart_data.put("jerseyno","");
                                                             cart_data.put("del","delivery in 7 days");


                                                            documentReference_cart.set(cart_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(getApplicationContext(),"Product add to cart",Toast.LENGTH_SHORT).show();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(getApplicationContext(),"Check Your Internet Connection",Toast.LENGTH_SHORT).show();
                                                                }
                                                            });



                                                }

                                            });
                                        }else {
                                            StorageReference ref2 =storageReference.child("textile_cart").child("img2"+time+"."+getfileExt2(img_uri2));

                                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    firebase_img_uri1 = uri.toString();
                                                    ref2.putFile(img_uri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                            ref2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    firebase_img_uri2 = uri.toString();

                                                                    //upload data

                                                                    String cart_str = fStore.collection("Textile_users").document(userid).collection("cart").document().getId();
                                                                    DocumentReference documentReference_cart = fStore.collection("Textile_users").document(userid).collection("cart").document(cart_str);
                                                                    Map<String, Object> cart_data = new HashMap<>();
                                                                    cart_data.put("style", style_str);
                                                                    cart_data.put("material",material_str);
                                                                    cart_data.put("printtype",printtype_str);
                                                                    cart_data.put("size",size_str);
                                                                    cart_data.put("frontprint",frontprint_str);
                                                                    cart_data.put("backprint",backprint_str);
                                                                    cart_data.put("sideprint",sideprint_str);
                                                                    cart_data.put("design_img",firebase_img_uri1);
                                                                    cart_data.put("extra_img",firebase_img_uri2);
                                                                    cart_data.put("quantity",quantity_str);
                                                                    cart_data.put("price","");
                                                                    cart_data.put("time",time);
                                                                    cart_data.put("id",cart_str);
                                                                    cart_data.put("remark",remark_str);
                                                                    cart_data.put("jerseyno","");
                                                                    cart_data.put("del","delivery in 7 days");
                                                                    documentReference_cart.set(cart_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            progressDialog.dismiss();
                                                                            Toast.makeText(getApplicationContext(),"Product add to cart",Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            progressDialog.dismiss();
                                                                        }
                                                                    });














                                                                }
                                                            });


                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext()," Image2 not upload",Toast.LENGTH_SHORT).show();
                                                            progressDialog.dismiss();

                                                        }
                                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                            double progress
                                                                    = (100.0
                                                                    * snapshot.getBytesTransferred()
                                                                    / snapshot.getTotalByteCount());
                                                            progressDialog.setMessage(
                                                                    "Uploaded "
                                                                            + (double)progress + "%");
                                                            progressDialog.setCanceledOnTouchOutside(false);
                                                        }
                                                    });

                                                }

                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(CustomActivity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                        }




                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext()," Image not upload, Try Again Later",Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();

                                            }
                                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                        double progress
                                                = (100.0
                                                * snapshot.getBytesTransferred()
                                                / snapshot.getTotalByteCount());
                                        progressDialog.setMessage(
                                                "Uploaded "
                                                        + (double)progress + "%");
                                        progressDialog.setCanceledOnTouchOutside(false);
                                    }
                                });
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"select image to upload",Toast.LENGTH_SHORT).show();
                                //    loadingDialog.dismiss();

                            }

















                    }else{
                        Toast.makeText(CustomActivity.this, "Fill Every Required details", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(CustomActivity.this,  e+"", Toast.LENGTH_SHORT).show();
                }



            }
        });
        findViewById(R.id.img1_l).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage1();
            }
        });
        findViewById(R.id.img2_l).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage2();
            }
        });
        findViewById(R.id.back_custom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in2 = new Intent(CustomActivity.this,HomeActivity.class);
                startActivity(in2);
                finish();
            }
        });
    }

    public void add_style(){

        listener = databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item : snapshot.getChildren()){
                    spinnerData_style.add(item.getValue().toString());

                }
                adapter_style.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void add_printtype(){

        listener = databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item : snapshot.getChildren()){
                    spinnerData_printtype.add(item.getValue().toString());

                }
                adapter_printtype.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void add_material(){

        listener = databaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item : snapshot.getChildren()){
                    spinnerData_material.add(item.getValue().toString());

                }
                adapter_material.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void add_size(){

        listener = databaseReference4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item : snapshot.getChildren()){
                    spinnerData_size.add(item.getValue().toString());

                }
                adapter_size.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void SelectImage1()
    {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }
    private void SelectImage2()
    {

        Intent intent2 = new Intent();
        intent2.setType("image/*");
        intent2.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent2, "Select Image from here..."), PICK_IMAGE_REQUEST2);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            img_uri1 = data.getData();
            img1.setImageURI(img_uri1);
          //  img1.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_camera));
        }
        if(requestCode == PICK_IMAGE_REQUEST2 && resultCode == RESULT_OK && data != null && data.getData() != null){
            img_uri2 = data.getData();
            img2.setImageURI(img_uri2);
           // img2.setImageDrawable(img_uri2);
        }

    }
    private String getfileExt1(Uri imageuri1) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageuri1));
    }
    private String getfileExt2(Uri imageuri2) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageuri2));
    }
    /*
    private void uploadimage(){
      //  loadingDialog.show();
        Date time = Calendar.getInstance().getTime();
        if(img_uri1 != null){
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref =storageReference.child("textile_cart").child("img1"+time+"."+getfileExt1(img_uri1));
            StorageReference ref2 =storageReference.child("textile_cart").child("img2"+time+"."+getfileExt2(img_uri2));
            ref.putFile(img_uri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref2.putFile(img_uri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    firebase_img_uri1 = uri.toString();


                                                    ref2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            firebase_img_uri2 = uri.toString();

                                                            //upload data

                                                            String cart_str = fStore.collection("Textile_users").document(userid).collection("cart").document().getId();
                                                            DocumentReference documentReference_cart = fStore.collection("Textile_users").document(userid).collection("cart").document(cart_str).collection("data").document();
                                                            Map<String, Object> cart_data = new HashMap<>();
                                                            cart_data.put("style", style_str);
                                                            cart_data.put("material",material_str);
                                                            cart_data.put("printtype",printtype_str);
                                                            cart_data.put("size",size_str);
                                                            cart_data.put("frontprint",frontprint_str);
                                                            cart_data.put("backprint",backprint_str);
                                                            cart_data.put("sideprint",sideprint_str);
                                                            cart_data.put("design_img",firebase_img_uri1);
                                                            cart_data.put("extra_img",firebase_img_uri2);
                                                            cart_data.put("quantity",quantity_custom);
                                                            cart_data.put("price",price_custom);
                                                            cart_data.put("time",time);
                                                            documentReference_cart.set(cart_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Toast.makeText(getApplicationContext(),"Product add to cart",Toast.LENGTH_SHORT).show();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                }
                                                            });














                                                        }
                                                    });




                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext()," Image2 not upload",Toast.LENGTH_SHORT).show();
                                //    loadingDialog.dismiss();

                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    double progress
                                            = (100.0
                                            * snapshot.getBytesTransferred()
                                            / snapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (double)progress + "%");
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext()," Image not upload",Toast.LENGTH_SHORT).show();
                          //  loadingDialog.dismiss();

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress
                            = (100.0
                            * snapshot.getBytesTransferred()
                            / snapshot.getTotalByteCount());
                    progressDialog.setMessage(
                            "Uploaded "
                                    + (double)progress + "%");
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(),"Uri not found",Toast.LENGTH_SHORT).show();
        //    loadingDialog.dismiss();

        }
    }


     */
    @Override
    public void onBackPressed() {

        Intent in = new Intent(CustomActivity.this,HomeActivity.class);
        startActivity(in);
        finish();
    }
}