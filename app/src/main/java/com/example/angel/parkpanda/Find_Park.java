package com.example.angel.parkpanda;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class Find_Park extends ListActivity implements View.OnClickListener{




    private AlphabetListAdapter adapter = new AlphabetListAdapter();
    private GestureDetector mGestureDetector;
    private List<Object[]> alphabet = new ArrayList<Object[]>();
    private HashMap<String, Integer> sections = new HashMap<String, Integer>();
    private int sideIndexHeight;
    private static float sideIndexX;
    private static float sideIndexY;
    private int indexListSize;
    ListView listView ;
    List section_flag;
    List mySearchResult;
    List tempCityName;
    String strFIREPATH="https://parkpanda-1372.firebaseio.com/cityinfos";
    Firebase fireref;
    FirebaseAuth fireauth;

    private HashMap <String,CITYINFO> mCityInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find__park);

        mCityInfo = new HashMap<String,CITYINFO>();

        Firebase.setAndroidContext(getApplicationContext());
        fireref= new Firebase(strFIREPATH);//your firebase url
        fireauth=FirebaseAuth.getInstance();



        //tempCityName.add("32");
        fireref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                tempCityName=new ArrayList();
                for (DataSnapshot messageSnapshot: snapshot.getChildren()) {
                    String cinfokey=messageSnapshot.getKey();

                    Log.d("###",cinfokey);
                    Log.d("###",messageSnapshot.getValue().toString());
                    CITYINFO cinfo=messageSnapshot.getValue(CITYINFO.class);
                    cinfo.setId(cinfokey);
                    Log.d("#",","+cinfo.getLon()+cinfo.getLat()+cinfo.getName()+",");
                    String str_name=cinfo.getName();
                    tempCityName.add(str_name);
                    //tempCityName.add("322323");

                    mCityInfo.put(str_name,cinfo);
                }

                filterMyList("");
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });


        getListView().setClickable(true);

        ImageView iv_precv=(ImageView)findViewById(R.id.findpark_toolbar_prev);
        iv_precv.setOnClickListener(this);


        mGestureDetector = new GestureDetector(this, new SideIndexGestureListener());

        mySearchResult=new ArrayList();
        section_flag=new ArrayList();


        EditText mm=(EditText)findViewById(R.id.find_park_search);
        mm.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                EditText dt=(EditText)findViewById(R.id.find_park_search);
                filterMyList(dt.getText().toString());

            }


            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });


        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String str;
                if(parent.getItemAtPosition(position).toString().contains("Item")) {
                    str = mySearchResult.get(position).toString();

                    CITYINFO setcityinfo = mCityInfo.get(str);

                    //Log.d("###---###",""+dsf.getLon()+dsf.getLat()+dsf.getId()+"");
                    Intent intent=new Intent();
                    intent.putExtra("MESSAGE",setcityinfo.getId());

                    intent.putExtra("Lat",setcityinfo.getLat());
                    intent.putExtra("Lon",setcityinfo.getLon());

                    setResult(2,intent);
                    finish();//finishing activity

                }
            }

        });



    }
    private long lastPressedTime;
    private static final int PERIOD = 2000;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return false;
    }

    public void updateList() {
        LinearLayout sideIndex = (LinearLayout) findViewById(R.id.sideIndex);
        sideIndex.removeAllViews();
        indexListSize = alphabet.size();
        if (indexListSize < 1) {
            return;
        }

        int indexMaxSize = (int) Math.floor(sideIndex.getHeight() / 20);
        int tmpIndexListSize = indexListSize;
        while (tmpIndexListSize > indexMaxSize) {
            tmpIndexListSize = tmpIndexListSize / 2;
        }
        double delta;
        if (tmpIndexListSize > 0) {
            delta = indexListSize / tmpIndexListSize;
        } else {
            delta = 1;
        }

        TextView tmpTV;
        for (double i = 1; i <= indexListSize; i = i + delta) {
            Object[] tmpIndexItem = alphabet.get((int) i - 1);
            String tmpLetter = tmpIndexItem[0].toString();

            {
                tmpTV = new TextView(this);
                tmpTV.setText(tmpLetter);
                tmpTV.setGravity(Gravity.CENTER);
                tmpTV.setTextSize(13);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                tmpTV.setLayoutParams(params);
                sideIndex.addView(tmpTV);
            }
        }

        sideIndexHeight = sideIndex.getHeight();

        sideIndex.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // now you know coordinates of touch
                sideIndexX = event.getX();
                sideIndexY = event.getY();

                // and can display a proper item it country list
                displayListItem();

                return false;
            }
        });
    }

    public void displayListItem() {
        LinearLayout sideIndex = (LinearLayout) findViewById(R.id.sideIndex);
        sideIndexHeight = sideIndex.getHeight();
        // compute number of pixels for every side index item
        double pixelPerIndexItem = (double) sideIndexHeight / indexListSize;

        // compute the item index for given event position belongs to
        int itemPosition = (int) (sideIndexY / pixelPerIndexItem);

        // get the item (we can do it since we know item index)
        if (itemPosition < alphabet.size()) {
            Object[] indexItem = alphabet.get(itemPosition);
            int subitemPosition = sections.get(indexItem[0]);
            getListView().setSelection(subitemPosition);
        }
    }
    void filterMyList(String sr)
    {
        alphabet.clear();
        mySearchResult.clear();
        List countries1 = populateCountries();
        List countries = new ArrayList();
        if(sr.length()>0) {
            for (int i = 0; i < countries1.size(); i++) {
                if (countries1.get(i).toString().toLowerCase().contains(sr.toLowerCase())) {
                    countries.add(countries1.get(i));
                }
            }
        }
        else
        {
            countries = populateCountries();
        }
        Collections.sort(countries);
        List rows = new ArrayList();
        int start = 0;
        int end = 0;
        String previousLetter = null;
        Object[] tmpIndexItem = null;
        Pattern numberPattern = Pattern.compile("[0-9]");

        for (Object country : countries) {
            String firstLetter = ((String) country).substring(0, 1);
            {
                if (previousLetter != null && !firstLetter.equals(previousLetter)) {
                    end = rows.size() - 1;
                    tmpIndexItem = new Object[3];
                    tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
                    tmpIndexItem[1] = start;
                    tmpIndexItem[2] = end;
                    alphabet.add(tmpIndexItem);
                    start = end + 1;
                }
                if (!firstLetter.equals(previousLetter)) {
                    rows.add(new AlphabetListAdapter.Section(firstLetter));
                    mySearchResult.add(firstLetter);
                    sections.put(firstLetter, start);
                }
                mySearchResult.add(country);
                rows.add(new AlphabetListAdapter.Item((String) country));
                previousLetter = firstLetter;
            }
        }
        if (previousLetter != null) {
            tmpIndexItem = new Object[3];
            tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
            tmpIndexItem[1] = start;
            tmpIndexItem[2] = rows.size() - 1;
            alphabet.add(tmpIndexItem);
        }
        adapter.setRows(rows);
        setListAdapter(adapter);
        updateList();
    }
    private List populateCountries() {
        List countries = new ArrayList();
       // mCityInfo.get
        for(int i=0;i<tempCityName.size();i++) {
            Log.d("#######",tempCityName.get(i).toString());
            countries.add(tempCityName.get(i).toString());
        }




        return countries;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void onClick(View view) {
        int id=view.getId();
        if(id==R.id.findpark_toolbar_prev)
        {


            Intent intent=new Intent();
            intent.putExtra("MESSAGE","null");
            setResult(2,intent);

            finish();//finishing activity
        }
    }
    class SideIndexGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            sideIndexX = sideIndexX - distanceX;
            sideIndexY = sideIndexY - distanceY;
            if (sideIndexX >= 0 && sideIndexY >= 0) {
                displayListItem();
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
    @Override
    public void onBackPressed()
    {
            //return true;
    }
}
