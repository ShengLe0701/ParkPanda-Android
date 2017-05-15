package com.example.angel.parkpanda;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.AuthResultHandler;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

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
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import static com.example.angel.parkpanda.R.string.FIREBASE_URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link log_in.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link log_in#newInstance} factory method to
 * create an instance of this fragment.
 */
public class log_in extends Fragment implements  View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    EditText et_email,et_password;
    Button tv_fb_login,tv_signup;
    Button bt_login;

    TextView tv_forgot;

    String strFIREPATH="https://parkpanda-1372.firebaseio.com/";
    Firebase fireref;
    FirebaseAuth fireauth;
    ProgressDialog progress;
    private CallbackManager callbackmanager;

    public log_in() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment log_in.
     */
    // TODO: Rename and change types and number of parameters
    public static log_in newInstance(String param1, String param2) {
        log_in fragment = new log_in();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_log_in, container, false);
        initGUI(view);
        fireref.setAndroidContext(getContext());
        fireref= new Firebase(strFIREPATH);//your firebase url
        fireauth=FirebaseAuth.getInstance();
        return view;
    }

    public boolean checkField()
    {
        int result=10;
        if(!(et_email.getText().toString().contains("@")))
        {
            result++;
            et_email.setError("incorrect");
        }
        if(et_password.length()<1)
        {
            result++;
            et_password.setError("incorrect");
        }

        if(result==10) return true;
        else return false;
    }
    @Override
    public void onClick(View view) {
        int id=view.getId();
        if(id==R.id.txt_forgot_password)
        {
            if(!(et_email.getText().toString().contains("@")))
            {
                et_email.setError("incorrect");
                return;
            }
            else
            {
              //  Firebase ref = new Firebase(strFIREPATH);
                String varemail=et_email.getText().toString();
                Toast.makeText(getContext(),varemail, Toast.LENGTH_SHORT).show();

                fireauth.sendPasswordResetEmail(varemail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        }
        else if(id==R.id.btn_login_login)
        {
            if(checkField())  FireBaseLogin();
        }
        else if(id==R.id.iv_login_fb)
        {
            onFblogin();
        }
        else if(id==R.id.txt_login_signup)
        {
            Fragment fragment = new create_new_account();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction FT = fragmentManager.beginTransaction();
            FT.setCustomAnimations(R.anim.enter_from_right,
                    R.anim.exit_to_left);
            FT.replace(R.id.flContent, fragment).commit();
        }
    }

    public void gotoMyAccount(){
        Fragment fragment = new my_account();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction FT = fragmentManager.beginTransaction();
        FT.setCustomAnimations(R.anim.enter_from_right,
                R.anim.exit_to_left);
        FT.replace(R.id.flContent, fragment).commit();
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void initGUI(View view)
    {
        et_email= (EditText) view.findViewById(R.id.txt_login_carnumber);
        et_password=(EditText) view.findViewById(R.id.txt_login_password);
        tv_fb_login= (Button) view.findViewById(R.id.iv_login_fb);
        bt_login= (Button) view.findViewById(R.id.btn_login_login);
        tv_signup= (Button) view.findViewById(R.id.txt_login_signup);

        tv_signup.setOnClickListener(this);
        bt_login.setOnClickListener(this);
        tv_fb_login.setOnClickListener(this);

        tv_forgot=(TextView)view.findViewById(R.id.txt_forgot_password);
        tv_forgot.setOnClickListener(this);
        FacebookSdk.sdkInitialize(getContext());
    }
    public void gotoShowMyAccount()
    {
        Fragment fragment = new my_account();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction FT = fragmentManager.beginTransaction();
        FT.setCustomAnimations(R.anim.enter_from_right,
                R.anim.exit_to_left);
        FT.replace(R.id.flContent, fragment).commit();
    }
    private void onFblogin()
    {
        callbackmanager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","user_photos","public_profile"));
        LoginManager.getInstance().registerCallback(callbackmanager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        progress = ProgressDialog.show(getContext(), "",
                                "Wating....", true);
                        System.out.println("Success");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response.getError() != null) {
                                            System.out.println("ERROR");
                                            progress.dismiss();
                                        } else {
                                            System.out.println("Success");
                                            progress.dismiss();
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
                                                progress.dismiss();
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
                        progress.dismiss();
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackmanager.onActivityResult(requestCode, resultCode, data);
    }
    private void onFacebookAccessTokenChange(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fireauth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
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

                            gotoShowMyAccount();

                        }
                    }
                });
    }
    public void FireBaseLogin()
    {
        progress = ProgressDialog.show(getContext(), "",
                "Wating....", true);
        String strPass=et_password.getText().toString();
        final String strEmail=et_email.getText().toString();
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
                                                gotoMyAccount();
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
}
