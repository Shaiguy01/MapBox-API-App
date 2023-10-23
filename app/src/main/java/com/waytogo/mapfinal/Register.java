package com.waytogo.mapfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    //variables
    EditText username;
    EditText password;
    EditText confpassword;
    Button btnReg;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //type cast variables
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confpassword= findViewById(R.id.confpassword);
        btnReg = findViewById(R.id.btnReg);
        mAuth = FirebaseAuth.getInstance();
    }

    //Reg method
    public void registerOnClick (View view)
    {
        if(view.getId()==R.id.btnReg)
        {
            String Username = username.getText().toString().trim()+"@way2go.com";
            String Password = password.getText().toString().trim();
            String Confirmpass = confpassword.getText().toString().trim();

            if (TextUtils.isEmpty(Username)){
                Toast.makeText(this, "This field cannot be blank", Toast.LENGTH_SHORT).show();
            }

            if (TextUtils.isEmpty(Password)) {
                Toast.makeText(this, "This field cannot be blank", Toast.LENGTH_SHORT).show();
            }

            if (TextUtils.isEmpty(Confirmpass)) {
                Toast.makeText(this, "This field cannot be blank", Toast.LENGTH_SHORT).show();
            }

            //password range -> not shorter than 6

            if (Password.length()<6 || Confirmpass.length()<6 ){
                Toast.makeText(this, "Password cannot be shorter than 6 characters", Toast.LENGTH_SHORT).show();

            }

            if(Confirmpass.equals(Password)){
                //bring in firebase
                //create user

                mAuth.createUserWithEmailAndPassword(Username,Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {
                            Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            //allow to log in
                            Intent intent = new Intent(Register.this,Login.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(Register.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //to go to login page immediately
    public  void toLogin (View view){
        Intent intent = new Intent(Register.this,Login.class);
        startActivity(intent);
    }
}