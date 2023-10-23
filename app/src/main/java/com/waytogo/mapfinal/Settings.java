package com.waytogo.mapfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity {

    Button save;
    Spinner spin ;
    RadioButton rb1;
    RadioButton rb2;
    RadioButton radioWalk;
    RadioButton radioCycle;
    RadioButton radioDrive;

    String dbName = "UserSettings";
    String tmode = "Default";
    String rChoice = "Default";
    String location ="Default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        save =  findViewById(R.id.btnSave);
        spin = findViewById(R.id.spinner);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        radioWalk = findViewById(R.id.rbWalk);
        radioCycle = findViewById(R.id.rbCycle);
        radioDrive = findViewById(R.id.rbDrive);


        //Set dropdown values
        String[] landmarkPrefList = new String[]{"Historical","Modern","Popular"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, landmarkPrefList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        //Set var according to choice
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0) {
                    location = "Historical";
                }
                if (position == 1) {
                    location = "Modern";
                }
                if (position == 2) {
                    location = "Popular";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(), "Please select all the necessary choices!", Toast.LENGTH_LONG).show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get unit conversions
                if (rb1.isChecked()){
                    rChoice = rb1.getText().toString();
                } else  if (rb2.isChecked())
                {
                    rChoice = rb2.getText().toString();
                }

                //Get preferred transport mode
                if (radioWalk.isChecked()){
                    tmode = radioWalk.getText().toString();
                }
                else if(radioCycle.isChecked())
                {
                    tmode = radioCycle.getText().toString();
                } else if(radioDrive.isChecked())
                {
                    tmode = radioDrive.getText().toString();
                }

                //Set values to be mapped to db
                Map<String,Object> map = new HashMap<>();
                map.put("building",location);
                map.put("mode",tmode);
                map.put("unit",rChoice);

                //Get instance, push to db
                FirebaseDatabase.getInstance().getReference().child(dbName).push()
                        .setValue(map)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Settings.this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
                                clearall();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Settings.this, "Error while saving...", Toast.LENGTH_SHORT).show();
                                clearall();
                            }
                        });
            }
        });

        BottomNavigationView bnv = findViewById(R.id.bottomNavigationView);

        bnv.setSelectedItemId(R.id.settings);

        bnv.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.favourites:
                        startActivity(new Intent(getApplicationContext(),Favourites.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.settings:
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

    public void clearall(){
        radioDrive.setSelected(false);
        radioCycle.setSelected(false);
        radioWalk.setSelected(false);
        rb1.setSelected(false);
        rb2.setSelected(false);
    }
}