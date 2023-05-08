package com.labs.jvlb4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Map<String,String>> list = new ArrayList<>();
        for (int i = 0; i < 250; i++) {
            Map<String,String> mp = new HashMap<>();
            mp.put("name","Користувач"+i);
            mp.put("city","Місто"+i);
            list.add(mp);
        }
        UAdapter adapter = new UAdapter(this,list,false);
        ((ListView)findViewById(R.id.BaseLV)).setAdapter(adapter);
    }

}