package com.biswa1045.drucine_textile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SummaryActivity extends AppCompatActivity {
    @BindView(R.id.progress_bar_summ_cart)
    ProgressBar progressBar;


    @BindView(R.id.summ_recycle)
    RecyclerView rcv;

    int count;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseFirestore db;
    double price;
    TextView summ_price,summ_total;
    String userid;
    FirebaseFirestore fStore;
    String name_fs,hostel_fs,room_fs,cont_fs;
    TextView name,name2,cont,roomno,hostel;
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        name = findViewById(R.id.name_summ);
        name2 = findViewById(R.id.name_summ2);
        cont = findViewById(R.id.mob_summ);
        roomno  = findViewById(R.id.room_summ);
        hostel = findViewById(R.id.hostel_summ);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fStore = FirebaseFirestore.getInstance();
        userid = user.getUid();

        summ_price = findViewById(R.id.summ_price);
        summ_total = findViewById(R.id.summ_total);

        findViewById(R.id.buttom_summary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SummaryActivity.this,PayActivity.class);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.summary_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(SummaryActivity.this,CartActivity.class);
                startActivity(intent3);
                finish();
            }
        });

        ButterKnife.bind(this);
        init();
        getcartList();



            DocumentReference documentReference = fStore.collection("Textile_users").document(userid).collection("address").document("data");
            fStore.collection("Textile_users").document(userid).collection("address").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for (DocumentChange documentChange : value.getDocumentChanges()){

                        name_fs = documentChange.getDocument().getData().get("username").toString();
                        room_fs = documentChange.getDocument().getData().get("roomno").toString();
                        hostel_fs = documentChange.getDocument().getData().get("hostel").toString();
                        cont_fs = documentChange.getDocument().getData().get("contact").toString();
                        name.setText( name_fs);
                        hostel.setText(hostel_fs);
                        cont.setText( cont_fs);
                        roomno.setText( room_fs);
                        name2.setText(name_fs);
                    }
                }
            });



        findViewById(R.id.summ_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SummaryActivity.this,PayActivity.class);
                in.putExtra("price",Double.toString(price));
                in.putExtra("count",Integer.toString(count));
                startActivity(in);
            }
        });
        findViewById(R.id.summary_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ine = new Intent(SummaryActivity.this,ProfileActivity.class);
                startActivity(ine);
                finish();
            }
        });

    }

    private void init() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1);
        rcv.setLayoutManager(gridLayoutManager);

        db = FirebaseFirestore.getInstance();
    }
    private void getcartList() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String userid = user.getUid();
        Query query = db.collection("Textile_users").document(userid).collection("cart");

        FirestoreRecyclerOptions<retrieveModel> response = new FirestoreRecyclerOptions.Builder<retrieveModel>()
                .setQuery(query, retrieveModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<retrieveModel, SummaryActivity.FriendsHolder>(response) {
            @Override
            public void onBindViewHolder(SummaryActivity.FriendsHolder holder, int position, retrieveModel model) {
                progressBar.setVisibility(View.GONE);

                count = adapter.getItemCount();
                String cart_name_str = model.getStyle(); holder.cart_name.setText(cart_name_str);
                String cart_size_str = model.getSize(); holder.cart_size.setText(cart_size_str);
                String cart_quantity_str = model.getQuantity(); holder.cart_quantity.setText(cart_quantity_str);
                String cart_price_str = model.getPrice(); holder.cart_price.setText(cart_price_str);

                Glide.with(getApplicationContext())
                        .load(model.getDesign_img())
                        //   .error(R.drawable.back_l)
                        .into(holder.cart_img);

                price += Double.parseDouble(cart_price_str);
                summ_price.setText("₹"+(int) price);
                summ_total.setText("₹"+(int) price);

            }

            @Override
            public SummaryActivity.FriendsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.summ_cart_item, group, false);

                return new SummaryActivity.FriendsHolder(view);
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

        @BindView(R.id.summ_cart_img)
        ImageView cart_img;

        @BindView(R.id.summ_cart_name)
        TextView cart_name;

        @BindView(R.id.summ_cart_price)
        TextView cart_price;

        @BindView(R.id.summ_cart_quantity)
        TextView cart_quantity;

        @BindView(R.id.summ_cart_size)
        TextView cart_size;




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

    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(SummaryActivity.this,CartActivity.class);
        startActivity(intent2);
        finish();
    }
}