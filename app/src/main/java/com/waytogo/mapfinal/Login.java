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

public class Login extends AppCompatActivity {

    //variable
    EditText username2;
    EditText password2;
    Button loginbtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //type cast
        username2 = findViewById(R.id.username2);
        password2 = findViewById(R.id.password2);
        loginbtn = findViewById(R.id.btnLogin);
        mAuth = FirebaseAuth.getInstance();
    }
    public void loginOnClick (View view){

        try{
            String email = username2.getText().toString().trim()+"@way2go.com";
            String pass = password2.getText().toString().trim();

            //housekeeping
            if(TextUtils.isEmpty(email)){
                Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
                username2.requestFocus();
                return;
            }

            if(TextUtils.isEmpty(pass)){
                Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
                password2.requestFocus();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if(task.isSuccessful()){
                        Toast.makeText(Login.this, "Logging in", Toast.LENGTH_SHORT).show();

                        //clear tbs
                        username2.setText("");
                        password2.setText("");
                        username2.requestFocus();

                        //take to next screen
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(Login.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        catch (Exception ex) {
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
        }
    }
    public void toReg(View view){
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }
}