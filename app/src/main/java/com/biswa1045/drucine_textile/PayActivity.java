package com.biswa1045.drucine_textile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PayActivity extends AppCompatActivity {
    String price,count;
    TextView pay_total_count,pay_total,pay_final,amountpay;
    RadioGroup radioGroup_pay;
    RadioButton selected;
    boolean network=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        checkConnection();
        radioGroup_pay = findViewById(R.id.radio_group);

        pay_total = findViewById(R.id.pay_total);
        pay_total_count = findViewById(R.id.pay_total_count);
        pay_final = findViewById(R.id.pay_final);
        amountpay = findViewById(R.id.amountpay);
        Bundle extra = getIntent().getExtras();
        if(extra != null){
             price  = extra.getString("price");
             count  = extra.getString("count");
             pay_total.setText("₹ "+price);
             amountpay.setText("₹ "+price);
             pay_total_count.setText("Total "+"("+count+" items"+")");
             pay_final.setText("PAY "+price);

        }
        findViewById(R.id.payment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(network==true){
                    try{
                        if (extra !=null && radioGroup_pay.getCheckedRadioButtonId() != -1){
                            int selectedid = radioGroup_pay.getCheckedRadioButtonId();
                            selected = findViewById(selectedid);
                            String selected_str = selected.getText().toString();
                            if (selected_str.equals("Google Pay")){

                                //first succ tranc
                                //then save data to common order list and user list and order data to prepaid
                                //if failed nothing is happening

                                paymentusinggpay();
                                Toast.makeText(PayActivity.this, selected_str+"google pay selected ", Toast.LENGTH_SHORT).show();

                            }else
                            if(selected_str=="Paytm"){

                            }else if(selected_str=="Cash on Delivery"){
                                //then save data to common order list and user list and order data to COD

                            }else{
                                Toast.makeText(PayActivity.this, selected_str+"Some thing went wrong. Try again later", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            if(extra ==null){
                                Toast.makeText(PayActivity.this, "Some thing went worng,try again later", Toast.LENGTH_SHORT).show();

                            }else{
                                if( radioGroup_pay.getCheckedRadioButtonId() == -1){
                                    Toast.makeText(PayActivity.this, "Select one payment method", Toast.LENGTH_SHORT).show();

                                }else{
                                    Toast.makeText(PayActivity.this, "Some thing went worng,try again later", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    }catch (Exception e){

                    }
                }else{
                    Toast.makeText(PayActivity.this, "Connect Internet Connection", Toast.LENGTH_SHORT).show();

                }


            }
        });






        findViewById(R.id.pay_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(PayActivity.this,CartActivity.class);
                startActivity(intent3);
                finish();
            }
        });
    }
    private void paymentusinggpay(){
        String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa","soumyaprakash90900@okhdfcbank")  //upi id
                .appendQueryParameter("pn","Drucine")  //company name
                .appendQueryParameter("tn","payment for testing")  //note
                .appendQueryParameter("am","1")  //amount
                .appendQueryParameter("cu","INR")  //currency
        .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setData(uri);
        intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
        try{
            startActivityForResult(intent,101);
        }catch (Exception e){
            Toast.makeText(PayActivity.this, "Google pay app not found", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101){
            if(resultCode==RESULT_OK){
                if(data!=null){
                    String value = price;
                    ArrayList<String> list = new ArrayList<>();
                    list.add(value);
                   // Toast.makeText(PayActivity.this, "Payment Sucessful", Toast.LENGTH_SHORT).show();
                    getstatus(list.get(0));
                }
            }else{
                Toast.makeText(PayActivity.this, "Payment failed", Toast.LENGTH_SHORT).show();

            }
        }


    }
   private void getstatus(String data){
        boolean ispaymentcancel = false;
        boolean ispaymentsucc = false;
       boolean ispaymentfail = false;
        String value[] = data.split("&");
        for(int i = 0;i<=value.length;i++){
            String chechstring[] = value[i].split("=");
            if(chechstring.length>=2){
                if (chechstring[0].toLowerCase().equals("status")){
                    if (chechstring[1].equals("success")){
                        ispaymentsucc=true;
                    }

                }
            }else{
                ispaymentcancel=true;
            }

        }
        if (ispaymentsucc)  {
            Toast.makeText(PayActivity.this, "Payment Sucessfull", Toast.LENGTH_SHORT).show();

        }else if(ispaymentcancel){
            Toast.makeText(PayActivity.this, "Payment canceld", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(PayActivity.this, "Payment failed", Toast.LENGTH_SHORT).show();
        }

   }
    public void checkConnection() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

            }
        } else {
            Toast.makeText(this, "No Network Connection", Toast.LENGTH_SHORT).show();
            network = false;
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(PayActivity.this,CartActivity.class);
        startActivity(intent2);
        finish();
    }
}