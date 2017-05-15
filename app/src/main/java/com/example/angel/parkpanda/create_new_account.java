package com.example.angel.parkpanda;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.text.Text;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firebase_core.*;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link create_new_account.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link create_new_account#newInstance} factory method to
 * create an instance of this fragment.
 */
public class create_new_account extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private Firebase firebaseRef;

    String strFIREPATH="https://parkpanda-1372.firebaseio.com/";
    Button btn_save;
    EditText txt_carno,txt_repass,txt_email,txt_firstname,txt_lastname,txt_pasword;
    private FirebaseAuth firebaseAuth;





    public create_new_account() {

                // Required empty public constructor
    }

    public static create_new_account newInstance(String param1, String param2) {
        create_new_account fragment = new create_new_account();
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
        View view=inflater.inflate(R.layout.fragment_create_new_account, container, false);


        Firebase.setAndroidContext(getContext());
        firebaseRef = new Firebase(strFIREPATH);
        firebaseAuth = FirebaseAuth.getInstance();

        initGUI(view);
        return view;
    }
    public void initGUI(View view)
    {
        btn_save= (Button) view.findViewById(R.id.btn_newaccount_save);
        txt_carno= (EditText) view.findViewById(R.id.txt_newaccount_carnumber);
        txt_repass= (EditText) view.findViewById(R.id.txt_newaccount_confirmpassword);
        txt_email= (EditText) view.findViewById(R.id.txt_newaccount_email);
        txt_firstname= (EditText) view.findViewById(R.id.txt_newaccount_firstname);
        txt_lastname= (EditText) view.findViewById(R.id.txt_newaccount_lastname);
        txt_pasword= (EditText) view.findViewById(R.id.txt_newaccount_password);


        btn_save.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();


        if(id==R.id.btn_newaccount_save)
        {
            if(checkField())
            {
                sendDataToDataBase();



            }
        }
    }
    public boolean checkField()
    {
        int int_result=10;

        if(txt_carno.length()<1){txt_carno.setError("incorrect");int_result++;}
        if(txt_firstname.length()<1){txt_firstname.setError("incorrect");int_result++;}
        if(txt_lastname.length()<1){txt_lastname.setError("incorrect");int_result++;}
        if(!(txt_email.getText().toString().contains("@"))){txt_email.setError("incorrect");int_result++;}

        if(txt_pasword.length()<8){txt_pasword.setError("incorrect");int_result++;}
        if(txt_repass.length()<8){txt_repass.setError("incorrect");int_result++;}

        if((txt_pasword.length()>7)&&(txt_repass.length()>7))
        {
            String st1=txt_pasword.getText().toString();
            String st2=txt_repass.getText().toString();

            if(st1.equals(st2)){}
            else
            {
                txt_repass.setError("incorrect");
                int_result++;
            }
        }


        if(int_result==10) return true;
        else return  false;

    }
    public  void sendDataToDataBase(){



        LOGININFO.carlicensenumber=txt_carno.getText().toString();
        LOGININFO.email=txt_email.getText().toString();
        LOGININFO.firstname=txt_firstname.getText().toString();
        LOGININFO.lastname=txt_lastname.getText().toString();
        String strPass=txt_pasword.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(LOGININFO.email,strPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FIRE_USER account = new FIRE_USER();
                            String strr=task.getResult().getUser().getUid().toString();
                            account.setcarlicensenumber(LOGININFO.carlicensenumber);
                            account.setFirstname(LOGININFO.firstname);
                            account.setLastname(LOGININFO.lastname);
                            account.setUid(strr);

                            DatabaseReference mDatabase;
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("profiles").child(strr).setValue(account);
                            LOGININFO.flag=10;
                            Fragment fragment = new my_account();
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction FT = fragmentManager.beginTransaction();
                            FT.setCustomAnimations(R.anim.enter_from_right,
                                    R.anim.exit_to_left);
                            FT.replace(R.id.flContent, fragment).commit();
                        } else {

                            LOGININFO.flag=0;
                            LOGININFO.carlicensenumber="";
                            LOGININFO.email="";
                            LOGININFO.firstname="";
                            LOGININFO.lastname="";
                            String strPass="";
                            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getContext());
                            dlgAlert.setTitle("Email already exist.");
                            dlgAlert.setPositiveButton("OK", null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                        }
                    }
                });
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
