package com.example.projektas7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    private static final String USER = "user";

    EditText username, password;
    Button btnLogin;
    DatabaseHelper DB;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username=(EditText)findViewById(R.id.username_logPg);
        password = (EditText)findViewById(R.id.password_logPg);
        btnLogin = (Button)findViewById(R.id.button_signin_logPg);
        DB = new DatabaseHelper(this);

        sharedPref = getPreferences(Context.MODE_PRIVATE);




        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = username.getText().toString();
                String pass = password.getText().toString();

                if(user.equals("") || pass.equals("")) {
                    Toast.makeText(LoginActivity.this,"Please enter all the fields", Toast.LENGTH_SHORT).show();
                }else{
                    Boolean checkuserpass = DB.checkusernamepassword(user, pass);
                    if(checkuserpass) {
                        Toast.makeText(LoginActivity.this,"Sign in is successful",Toast.LENGTH_SHORT).show();
                        saveUser(user, pass);
                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(LoginActivity.this,"Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void saveUser(String user, String password) {

        SharedPreferences.Editor editor = sharedPref.edit();
        //editor.putInt(ID, user.getId());
        editor.putString(USER, user);
        editor.putString(PASSWORD, password);
        //editor.putString(VARDAS, user.getVardas());
        //editor.putInt(AMZIUS, user.getAmzius());
        editor.commit();

    }
}
