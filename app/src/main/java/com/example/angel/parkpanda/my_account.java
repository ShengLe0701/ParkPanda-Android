package com.example.angel.parkpanda;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link my_account.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link my_account#newInstance} factory method to
 * create an instance of this fragment.
 */
public class my_account extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    String strFIREPATH="https://parkpanda-1372.firebaseio.com/";
    Firebase firebaseref;
    FirebaseAuth auth;


    EditText et_firstname,et_lastname,et_email,et_carno;
    Button btn_save,btn_logout;
    TextView tv_changepass;
    public my_account() {
        // Required empty public constructor
    }

    public static my_account newInstance(String param1, String param2) {
        my_account fragment = new my_account();
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

        View view=inflater.inflate(R.layout.fragment_my_account, container, false);
        initGUI(view);

        Firebase.setAndroidContext(getContext());
        auth=FirebaseAuth.getInstance();
        firebaseref=new Firebase(strFIREPATH);

        return view;

    }
    public  void initGUI(View view)
    {
        et_firstname= (EditText) view.findViewById(R.id.txt_myaccount_firstname);
        et_lastname= (EditText) view.findViewById(R.id.txt_myaccount_latname);
        et_email= (EditText) view.findViewById(R.id.txt_myaccount_email);
        et_carno= (EditText) view.findViewById(R.id.txt_myaccount_carno);
        btn_save= (Button) view.findViewById(R.id.btn_myaccount_done);
        btn_logout= (Button) view.findViewById(R.id.btn_myaccount_signout);
        tv_changepass= (TextView) view.findViewById(R.id.txt_myaccount_changepass);

        et_email.setKeyListener((KeyListener) et_email.getTag());

          //  et_email.seted


            et_firstname.setText(LOGININFO.firstname);
            et_lastname.setText(LOGININFO.lastname);
            et_carno.setText(LOGININFO.carlicensenumber);
            et_email.setText(LOGININFO.email);


        btn_save.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        tv_changepass.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if(id==R.id.btn_myaccount_signout)
        {
           if(LOGININFO.flag==10)
           {
               LOGININFO.email = "";
               LOGININFO.firstname ="";
               LOGININFO.lastname = "";
               LOGININFO.carlicensenumber = "";
               LOGININFO.flag = 0;

               LoginManager.getInstance().logOut();
               firebaseref.unauth();
               auth.signOut();
               FirebaseAuth.getInstance().signOut();


               Intent intent = new Intent(getActivity(), MySplashScreen.class);
                getActivity().finish();
               startActivity(intent);
           }

        }
        else if(id==R.id.btn_myaccount_done)
        {

            String str_firname=et_firstname.getText().toString();
            String str_lastname=et_lastname.getText().toString();
            String str_carno=et_carno.getText().toString();


            FIRE_USER account = new FIRE_USER();
            String strr=auth.getCurrentUser().getUid();
            account.setcarlicensenumber(str_carno);
            account.setFirstname(str_firname);
            account.setLastname(str_lastname);
            account.setUid(strr);

            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("profiles").child(strr).setValue(account);

            Toast.makeText(getContext(), "Successfully Saved.", Toast.LENGTH_LONG).show();
        }
        else if(id==R.id.txt_myaccount_changepass)
        {
           /* Fragment fragment = new change_password();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction FT = fragmentManager.beginTransaction();
            FT.setCustomAnimations(R.anim.enter_from_right,
                    R.anim.exit_to_left);
            FT.replace(R.id.flContent, fragment).commit();*/
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
