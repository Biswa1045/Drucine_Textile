package com.biswa1045.drucine_textile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartActivity extends AppCompatActivity {
    @BindView(R.id.progress_bar_cart)
    ProgressBar progressBar;

    @BindView(R.id.cart_recycle)
    RecyclerView rcv;

    private FirestoreRecyclerAdapter adapter;
    private FirebaseFirestore db;
   // DocumentReference documentReference;
    FirebaseUser user;
    String userid,cont_fs;
    //LinearLayout cart_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

       // cart_layout =(LinearLayout) findViewById(R.id.buttom_cart);
       // rcv = findViewById(R.id.cart_recycle);
        findViewById(R.id.buttom_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cont_fs != ""){
                    Intent intent = new Intent(CartActivity.this,SummaryActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                //    Snackbar snackbar = Snackbar.make(cart_layout,"Update your Address",Snackbar.LENGTH_LONG).setAction("Update",
                 //           new View.OnClickListener() {
                 //               @Override
                 //               public void onClick(View v) {
                 //                   Intent up = new Intent(CartActivity.this,ProfileActivity.class);
                 //                   startActivity(up);
                  //              }
                  //          });
                  //  snackbar.show();
                }
            }
        });
        findViewById(R.id.back_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(CartActivity.this,HomeActivity.class);
                startActivity(intent3);
                finish();
            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();


        ButterKnife.bind(this);
        db = FirebaseFirestore.getInstance();
        init();


        getcartList();

      //  documentReference = db.collection("Textile_users").document(userid).collection("address").document("data");
        db.collection("Textile_users").document(userid).collection("address").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()){


                    cont_fs = documentChange.getDocument().getData().get("contact").toString();

                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(CartActivity.this, HomeActivity.class);
        startActivity(intent2);
        finish();
    }
        //

    private void init() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1);
        rcv.setLayoutManager(gridLayoutManager);

        //   db = FirebaseFirestore.getInstance();
    }


    private void getcartList() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        userid = user.getUid();
        Query query = db.collection("Textile_users").document(userid).collection("cart");

        FirestoreRecyclerOptions<retrieveModel> response = new FirestoreRecyclerOptions.Builder<retrieveModel>()
                .setQuery(query, retrieveModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<retrieveModel, CartActivity.FriendsHolder>(response) {
            @Override
            public void onBindViewHolder(CartActivity.FriendsHolder holder, int position, retrieveModel model) {
                progressBar.setVisibility(View.GONE);

              //  String qq = model.getVideoUri();
                //  int download =model.getDownload();
                //  int view = model.getView();
                String cart_name_str = model.getStyle(); holder.cart_name.setText(cart_name_str);
                String cart_size_str = model.getSize(); holder.cart_size.setText(cart_size_str);
                String cart_quantity_str = model.getQuantity(); holder.cart_quantity.setText(cart_quantity_str);
                String cart_price_str = model.getPrice(); holder.cart_price.setText(cart_price_str);
                String del_str = model.getDel(); holder.cart_del.setText(del_str);

                Glide.with(getApplicationContext())
                        .load(model.getDesign_img())
                     //   .error(R.drawable.back_l)
                        .into(holder.cart_img);
                holder.cart_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.collection("Textile_users").document(userid).collection("cart")
                                .document(model.getId())
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){

                                            Toast.makeText(CartActivity.this, "removed", Toast.LENGTH_SHORT).show();
                                            adapter.notifyDataSetChanged();
                                            rcv.setAdapter(adapter);
                                        }
                                    }
                                });
                    }
                });
                holder.cart_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }

            @Override
            public CartActivity.FriendsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.cart_item, group, false);

                return new CartActivity.FriendsHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        rcv.setAdapter(adapter);


    }


    public class FriendsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cart_img)
        ImageView cart_img;

        @BindView(R.id.cart_name)
        TextView cart_name;

        @BindView(R.id.cart_price)
        TextView cart_price;

        @BindView(R.id.cart_quantity)
        TextView cart_quantity;

        @BindView(R.id.cart_size)
        TextView cart_size;

        @BindView(R.id.cart_remove)
        Button cart_remove;

         @BindView(R.id.cart_delivery)
         TextView cart_del;

        public FriendsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}





