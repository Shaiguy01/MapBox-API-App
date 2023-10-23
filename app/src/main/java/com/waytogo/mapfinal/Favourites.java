package com.waytogo.mapfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class Favourites extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        listView = findViewById(R.id.listView);

        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("Umhlanga Arch");
        arrayList.add("Durban Beach");
        arrayList.add("Westville Country Club");
        arrayList.add("The Bergthiel Museum");
        arrayList.add("Pavillion Shopping Mall");

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);


        BottomNavigationView bnv = findViewById(R.id.bottomNavigationView);

        bnv.setSelectedItemId(R.id.favourites);

        bnv.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.settings:
                        startActivity(new Intent(getApplicationContext(),Settings.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.favourites:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.info:
                        startActivity(new Intent(getApplicationContext(),aboutpage.class));
                        overridePendingTransition(0,0);
                        return true;
            }
                return false;
            }
        });
}
}