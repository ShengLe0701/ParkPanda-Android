package com.example.angel.parkpanda;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.renderscript.Double2;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.BraintreePaymentActivity;
import com.braintreepayments.api.PaymentRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import cz.msebera.android.httpclient.Header;

public class paymentInterface extends AppCompatActivity implements View.OnClickListener {


    String BRAINTREE_SERVER="http://ec2-54-254-198-176.ap-southeast-1.compute.amazonaws.com:3000";
    String BRAINTREE_TOKEN;
    String BRAINTREE_AMOUNT;
    String BRAINTREE_PARKNAME;

    int PAYMENT_FLAG=0;
    TextView tv_parkname;
    TextView tv_parktime;
    TextView tv_parkinfo;
    TextView tv_price;
    TextView tv_error;
    TextView tv_money;
    ImageView iv_navigation;
    ImageView iv_payment_prev;
    Button btn_reserve;
    MyMarker marker;
    private long lastPressedTime;
    private static final int PERIOD = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_interface);

        tv_parkname=(TextView)findViewById(R.id.parkinfo_name);
        tv_parktime=(TextView)findViewById(R.id.parkinfo_time);
        tv_parkinfo=(TextView)findViewById(R.id.parkinfo_info);
        tv_money=(TextView)findViewById(R.id.detail_money);
        tv_price=(TextView)findViewById(R.id.parkinfo_price);
        final ImageView iv_im=(ImageView)findViewById(R.id.parkinfo_image);

        btn_reserve=(Button)findViewById(R.id.parkinfo_reserve_button);
        iv_payment_prev=(ImageView)findViewById(R.id.payment_toolbar_prev);
        iv_payment_prev.setOnClickListener(this);
        iv_navigation=(ImageView)findViewById(R.id.parkinfo_navigation);
        iv_navigation.setOnClickListener(this);



        btn_reserve.setOnClickListener(this);
         marker= (MyMarker) getIntent().getSerializableExtra("infokey");

        String pound = "\u00a3";

        BRAINTREE_AMOUNT=marker.getmPrice();
        tv_price.setText(marker.getmPrice()+" "+pound+"/Hour");
        tv_parkname.setText(marker.getmName()+"\n"+marker.getmAddress());
        BRAINTREE_PARKNAME=marker.getmName();
        tv_parktime.setText(marker.getmTime());
        tv_parkinfo.setText(marker.getmDesc());
        tv_money.setText("Free Spots  "+marker.getmFreeSpace()+"/"+marker.getmTotalSpace());

        String strpath=marker.getmParkImage();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://parkpanda-1372.appspot.com/"+"/"+strpath);


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        final int width = displaymetrics.widthPixels;

        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bMap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                iv_im.setImageBitmap(Bitmap.createScaledBitmap(bMap, width, bMap.getHeight(), false));
                //iv_im.setImageBitmap(null);
                //iv_im.setImageBitmap(bMap);
                // Use the bytes to display the image
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });









    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1003) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentMethodNonce paymentMethodNonce = data.getParcelableExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                String nonce = paymentMethodNonce.getNonce();
                postNonceToServer(nonce);
            }
        }
        else if(requestCode==3)
        {
            if(PAYMENT_FLAG==1)
            {
                finish();
            }
            else
            {

            }
        }
    }
    public void setPaymentForXX(final String strAmount, final String strParkName)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(BRAINTREE_SERVER+"/token", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String clientToken) {


                JSONObject mainObject = null;
                try {
                    mainObject = new JSONObject(clientToken);
                    BRAINTREE_TOKEN = mainObject.getString("clientToken");
                    //BRAINTREE_TOKEN=uniObject.toString();
                    PaymentRequest paymentRequest = new PaymentRequest()
                            .clientToken(BRAINTREE_TOKEN);
                    String pound = "\u00a3";
                    paymentRequest.primaryDescription(strParkName+": "+pound+strAmount+"/h");

                    paymentRequest.submitButtonText("Pay "+strAmount+pound+" PURCHASE");
                    startActivityForResult(paymentRequest.getIntent(getApplicationContext()), 1003);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    public void postNonceToServer(String nonce) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("amount", BRAINTREE_AMOUNT);
        params.put("payment_method_nonce", nonce);
        client.post(BRAINTREE_SERVER+"/payment", params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            PAYMENT_FLAG=1;
                            Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(getApplicationContext(), "Payment Failure", Toast.LENGTH_LONG).show();
                            PAYMENT_FLAG=0;
                    }
                }
        );
    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.parkinfo_reserve_button)
        {
            if(LOGININFO.flag==10)
            {
                setPaymentForXX(BRAINTREE_AMOUNT,BRAINTREE_PARKNAME);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "You have to login first.", Toast.LENGTH_LONG).show();
            }
        }
        else if(id==R.id.payment_toolbar_prev)
        {
            finish();
        }
        else if(id==R.id.parkinfo_navigation)
        {
            LatLng target_pos=new LatLng(Double.valueOf(marker.getmLat()).doubleValue(), Double.valueOf(marker.getmLon()).doubleValue()) ;
            LatLng origional_pos=new LatLng(TRACKPOSITION.src_Pos.latitude, TRACKPOSITION.src_Pos.longitude) ;

            String str = "saddr=" + origional_pos.latitude + "," + origional_pos.longitude;
            String str1 = "&daddr=" + target_pos.latitude + "," + target_pos.longitude;
            Log.d("##---##", str + str1);

            Intent intent = new Intent(getApplicationContext(), my_google_map.class);
            intent.putExtra("GPSDATA", str + str1);
            intent.putExtra("TARDATALAT", String.valueOf(target_pos.latitude));
            intent.putExtra("TARDATALONG", String.valueOf(target_pos.longitude));
            startActivityForResult(intent, 3);// Activity is started with requestCode 2
        }
    }
}
