package com.example.angel.parkpanda;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Angel on 7/26/2016.
 */
public class MySplashScreen extends Activity implements View.OnClickListener{

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private CallbackManager callbackmanager;
    Firebase fireref;
    FirebaseAuth fireauth;
    ProgressDialog progress;

    EditText et_email;
    EditText et_password;
    Button btn_firelogin;
    Button btn_facebooklogin;
    TextView tv_skip;
    String FIRE_URL = "https://parkpanda-1372.firebaseio.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mysplashscreen);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Firebase.setAndroidContext(getApplicationContext());


        fireref = new Firebase(FIRE_URL);
        fireauth = FirebaseAuth.getInstance();

        et_email=(EditText)findViewById(R.id.txt_splash_email);
        et_password=(EditText)findViewById(R.id.txt_splash_pass);
        btn_facebooklogin=(Button)findViewById(R.id.btn_splash_fblog);
        btn_firelogin=(Button)findViewById(R.id.btn_splash_sign);
        tv_skip=(TextView)findViewById(R.id.txt_splash_skip);


        btn_facebooklogin.setOnClickListener(this);
        btn_firelogin.setOnClickListener(this);
        tv_skip.setOnClickListener(this);

        MultiDex.install(this);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if(currentapiVersion>=23)
            checkAndRequestPermissions();
        else
        {
            if(checkAlradyLogin()==1) {
                Intent intent = new Intent(MySplashScreen.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        }

    }

    public int checkAlradyLogin() {
        int result = 0;
        if (fireauth.getCurrentUser() != null) {
            String ad = fireauth.getCurrentUser().getUid();
            if (ad != null) {
                result = 1;
                LOGININFO.flag = 10;
            }
            else {
                result = 2;
                LOGININFO.carlicensenumber = " ";
                LOGININFO.firstname = " ";
                LOGININFO.email = " ";
                LOGININFO.flag = 1;
                LOGININFO.lastname = " ";
            }
        } else {
            result = 3;
            LOGININFO.carlicensenumber = " ";
            LOGININFO.firstname = " ";
            LOGININFO.email = " ";
            LOGININFO.flag = 1;
            LOGININFO.lastname = " ";
        }
        return result;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

    }


    public  boolean checkField()
    {
        int result=0;
        if(!(et_email.getText().toString().contains("@")))
        {
            et_email.setError("incorrect");
            result=1;
        }
        if(et_password.length()<1)
        {
            et_password.setError("incorrect");
            result=2;
        }
        if(result>0)
            return false;
        else return true;
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermissions() {


        int permissionCheckSTOR =checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheckLOC = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        if ((permissionCheckLOC == PackageManager.PERMISSION_DENIED) || (permissionCheckSTOR == PackageManager.PERMISSION_DENIED)) {
            this.requestPermissions(new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    130);
        }
        else if ((permissionCheckSTOR == PackageManager.PERMISSION_GRANTED) && (permissionCheckLOC == PackageManager.PERMISSION_GRANTED)) {
            if(checkAlradyLogin()==1)
            {
                Intent intent = new Intent(MySplashScreen.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int flag=1;
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 130) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        flag=flag*1;
                    } else {
                        flag=flag*0;
                    }
                }
                else if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        flag=flag*1;
                    } else {
                        flag=flag*0;
                    }
                }
            }

            if(flag==1)
            {
                Intent intent = new Intent(MySplashScreen.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_splash_sign)
        {
            if(checkField()==true)
            {
                FireBaseLogin();
            }
        }
        else if(id==R.id.btn_splash_fblog)
        {
            onFblogin();
        }
        else if(id==R.id.txt_splash_skip)
        {
            Intent intent = new Intent(MySplashScreen.this, MainActivity.class);
            finish();
            startActivity(intent);
        }
    }

    public void FireBaseLogin()
    {
        String strPass=et_password.getText().toString();
        final String strEmail=et_email.getText().toString();
        progress=ProgressDialog.show(this, "", "Waiting...", true, false);
        fireauth.signInWithEmailAndPassword(strEmail, strPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            progress.dismiss();
                            et_password.setText("");
                        } else {
                            DatabaseReference mDatabase;
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            String userId =fireauth.getCurrentUser().getUid().toString();
                            mDatabase.child("profiles").child(userId).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                                            FIRE_USER user = dataSnapshot.getValue(FIRE_USER.class);
                                            if((user.getFirstname() !=null) && (user.getLastname() !=null))
                                            {
                                                LOGININFO.email = strEmail;
                                                LOGININFO.firstname = user.getFirstname();
                                                LOGININFO.lastname = user.getLastname();
                                                LOGININFO.carlicensenumber = user.getcarlicensenumber();
                                                LOGININFO.flag = 10;


                                                progress.dismiss();
                                                Intent intent = new Intent(MySplashScreen.this, MainActivity.class);
                                                finish();
                                                startActivity(intent);
                                            }
                                            else {
                                                progress.dismiss();
                                                et_password.setText("");
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            et_password.setText("");
                                            progress.dismiss();
                                        }
                                    });
                        }
                    }
                });
    }
    public void onFblogin()
    {
        String strfddd="ddd";
        callbackmanager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","user_photos","public_profile"));
        LoginManager.getInstance().registerCallback(callbackmanager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        System.out.println("Success");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response.getError() != null) {
                                            System.out.println("ERROR");
                                        } else {
                                            System.out.println("Success");
                                            try {
                                                String jsonresult = String.valueOf(json);
                                                System.out.println("JSON Result"+jsonresult);
                                                String str_email = json.getString("email");
                                                String str_id = json.getString("id");
                                                String str_firstname = json.getString("first_name");
                                                String str_lastname = json.getString("last_name");

                                                LOGININFO.firstname=str_firstname;
                                                LOGININFO.lastname=str_lastname;
                                                LOGININFO.email=str_email;
                                                //LOGININFO.carlicensenumber="";
                                                LOGININFO.flag=10;

                                                onFacebookAccessTokenChange(loginResult.getAccessToken());
                                            } catch (JSONException e) {
                                                e.printStackTrace();

                                            }
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields","id,first_name,last_name,email,gender, birthday");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }
                    @Override
                    public void onCancel() {
                        Log.d("INFO","On cancel");
                    }
                    @Override
                    public void onError(FacebookException error)
                    {

                        Log.d("INFO",error.toString());

                    }
                });
    }

    private void onFacebookAccessTokenChange(AccessToken token) {
        progress=ProgressDialog.show(this, "", "Waiting...", true, false);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fireauth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            progress.dismiss();
                        }
                        else
                        {
                            FIRE_USER account = new FIRE_USER();
                            String strr=task.getResult().getUser().getUid().toString();
                            //  account.setcarlicensenumber(LOGININFO.carlicensenumber);
                            account.setFirstname(LOGININFO.firstname);
                            account.setLastname(LOGININFO.lastname);
                            account.setUid(strr);

                            DatabaseReference mDatabase;
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("profiles").child(strr).setValue(account);

                            progress.dismiss();

                            Intent intent = new Intent(MySplashScreen.this, MainActivity.class);
                            finish();
                            startActivity(intent);

                        }
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackmanager.onActivityResult(requestCode, resultCode, data);
    }
}

