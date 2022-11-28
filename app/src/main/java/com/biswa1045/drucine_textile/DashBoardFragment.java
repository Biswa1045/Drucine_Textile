package com.biswa1045.drucine_textile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashBoardFragment extends Fragment {
    ViewFlipper viewFlipper;
   // @BindView(R.id.team_recycle)
    RecyclerView rcv;
    ValueEventListener listener;
    Dialog dialog;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseFirestore db;
    FirebaseFirestore fStore;
    String userid;
    String del_str;

    DatabaseReference databaseReference4;
    ArrayAdapter<String> adapter_size;
    ArrayList<String> spinnerData_size;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.dashboard_fragment, container, false);
        dialog = new Dialog(getContext());
        rcv = root.findViewById(R.id.team_recycle);
        fStore = FirebaseFirestore.getInstance();
        viewFlipper = root.findViewById(R.id.viewflipper);
        databaseReference4 = FirebaseDatabase.getInstance().getReference().child("Textile").child("size");
        spinnerData_size = new ArrayList<>();
        adapter_size = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, spinnerData_size);

        int images[] = {R.drawable.ic_launcher_background};
       /*
        for(int i=0;i<images.length;i++){
            flipper(images[i]);

        }

        */
        for(int image:images){
            flipper(image);
        }
        root.findViewById(R.id.custom_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),CustomActivity.class);
                startActivity(intent);

            }
        });


        ButterKnife.bind((Activity) getContext());
        init();
        getcartList();
        return  root;
    }
    public void flipper(int image){
        ImageView imageview = new ImageView(getContext());
        imageview.setBackgroundResource(image);
        viewFlipper.addView(imageview);
        viewFlipper.setFlipInterval(4000);
        viewFlipper.setAutoStart(true);

        viewFlipper.setInAnimation(getContext(),android.R.anim.slide_out_right);
        viewFlipper.setInAnimation(getContext(),android.R.anim.slide_in_left);
    }
    private void init() {

        LinearLayoutManager l = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        rcv.setLayoutManager(l);

        db = FirebaseFirestore.getInstance();
    }
    private void getcartList() {



        Query query = db.collection("Textile_Data").document("custom_order").collection("custom_order");

        FirestoreRecyclerOptions<retrieveModelTeam> response = new FirestoreRecyclerOptions.Builder<retrieveModelTeam>()
                .setQuery(query, retrieveModelTeam.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<retrieveModelTeam, FriendsHolder>(response) {
            @Override
            public void onBindViewHolder(FriendsHolder holder, int position, retrieveModelTeam model) {
                //  progressBar.setVisibility(View.GONE);

                //  count = adapter.getItemCount();
                String team_text_str = model.getName(); holder.team_text.setText(team_text_str);
               // String team_desc_str = model.getDesc();
                // del_str = model.getDel();
                // cart_price_str = model.getPrice();

                Glide.with(getContext())
                        .load(model.getImg())
                        //   .error(R.drawable.back_l)
                        .into(holder.team_img);
                holder.team_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    showpopup_details(model.getImg(),model.getDesc(),model.getDel(),model.getPrice());
                    }
                });


            }

            @Override
            public FriendsHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.team_item, group, false);

                return new FriendsHolder(view);
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

        @BindView(R.id.team_img)
        ImageView team_img;

        @BindView(R.id.team_text)
        TextView team_text;

        @BindView(R.id.team_next)
        ImageView team_next;






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
    public void showpopup_details(String img,String descp,String del,String cart_price_str){



        ImageView cancel_dailog;
        EditText name,no,remarks;
        TextView desc,price;
        Spinner size;
        Button save;

        dialog.setContentView(R.layout.team_dialog);
        name = dialog.findViewById(R.id.deatails_name);
        no = dialog.findViewById(R.id.deatails_no);
        remarks = dialog.findViewById(R.id.deatails_remarks);
        desc = dialog.findViewById(R.id.deatails_desc);
        price = dialog.findViewById(R.id.deatails_price);
        size = dialog.findViewById(R.id.deatails_size);
        save = dialog.findViewById(R.id.deatails_cart);
        size.setAdapter(adapter_size);
        add_size();
        cancel_dailog = dialog.findViewById(R.id.details_cancel);
        desc.setText(descp);
        price.setText("Price - "+cart_price_str);
        cancel_dailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name_str =  name.getText().toString();
                String no_str =  no.getText().toString();
                String remarks_str =  remarks.getText().toString();
                String size_str = size.getSelectedItem().toString();
                if(name_str.equals("")) {
                    name.setError("Enter name");

                }else if (no_str.equals("")) {
                    no.setError("Enter jersey number");


                }else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Date time = Calendar.getInstance().getTime();
                    userid = user.getUid();
                    String cart_str = fStore.collection("Textile_users").document(userid).collection("cart").document().getId();
                    DocumentReference documentReference_cart = fStore.collection("Textile_users").document(userid).collection("cart").document(cart_str);
                    Map<String, Object> cart_data = new HashMap<>();
                    cart_data.put("style", name_str);
                    cart_data.put("material","");
                    cart_data.put("printtype","");
                    cart_data.put("size",size_str);
                    cart_data.put("frontprint","");
                    cart_data.put("backprint","");
                    cart_data.put("sideprint","");
                    cart_data.put("design_img",img);
                    cart_data.put("extra_img","");
                    cart_data.put("quantity","1");
                    cart_data.put("price",cart_price_str);
                    cart_data.put("time",time);
                    cart_data.put("id",cart_str);
                    cart_data.put("remark",remarks_str);
                    cart_data.put("jerseyno",no_str);
                    cart_data.put("del",del);


                    documentReference_cart.set(cart_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(getContext(), "Product added to cart", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Product not saved", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
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
}
