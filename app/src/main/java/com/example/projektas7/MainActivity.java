package com.example.projektas7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainActivity extends AppCompatActivity {

    EditText name,surname,email,username, password, repassword;
    Button signup, signin;
    DatabaseHelper DB;


    ///////////////////////////
    TextView textViewRestApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText)findViewById(R.id.name);
        surname = (EditText)findViewById(R.id.surname);
        email = (EditText)findViewById(R.id.email);
        username = (EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        repassword = (EditText)findViewById(R.id.repassword);

        signup = (Button)findViewById(R.id.button_signup);
        signin = (Button)findViewById(R.id.button_signin);
        DB = new DatabaseHelper(this);

        RetrofitHelper retrofitHelper = new RetrofitHelper();


    textViewRestApi = findViewById(R.id.textViewRESTAPI);



        ApiService apiService = retrofitHelper.retrofitHelp().create(ApiService.class);

        Call<ResponseBody> call = apiService.getHome();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    textViewRestApi.setText("Code:"+response.code());
                    return;
                }
                ResponseBody result = response.body();

                    try {
                        Log.d("LoginPage",result.string());
                        textViewRestApi.setText("RETROFIT WORKS. THIS TEXT CAN BE UPDATED IN TRY CATCH PART");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                textViewRestApi.setText(t.getMessage());
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameOfUser = name.getText().toString();
                String surnameOfUser = surname.getText().toString();
                String emailOfUser = email.getText().toString();
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String repass = repassword.getText().toString();

                if(user.equals("") || pass.equals("") || repass.equals("")) {
                    Toast.makeText(MainActivity.this,"Please enter all the mandatory fields", Toast.LENGTH_SHORT).show(); // this bit is ok
                }
                //TODO need to check when registering if the username is not taken already. Could this be done by modyfing the login code?
                if(pass.equals(repass)){
                    apiService.register(
                            ""+nameOfUser,
                            ""+surnameOfUser,
                            ""+emailOfUser,
                            ""+user,
                            ""+pass).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.d("REGISTRATION","SOMETHING ATLEAST WORKS");
                            Toast.makeText(MainActivity.this,"Registration was successful",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                            startActivity(intent);

                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
        }});

//        signup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String user = username.getText().toString();
//                String pass = password.getText().toString();
//                String repass = repassword.getText().toString();
//
//                if(user.equals("") || pass.equals("") || repass.equals("")) {
//                    Toast.makeText(MainActivity.this,"Please enter all the fields", Toast.LENGTH_SHORT).show();
//                }else{
//                    if(pass.equals(repass)) {
//                        Boolean checkuser = DB.checkusername(user);
//                        if(checkuser==false){
//                            Boolean insert = DB.insertData(user,pass);
//                            if(insert==true) {
//                                Toast.makeText(MainActivity.this,"Registered successfully",Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
//                                startActivity(intent);
//                            }else{
//                                Toast.makeText(MainActivity.this,"Registration failed", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                        else{
//                            Toast.makeText(MainActivity.this,"User already exists. Please sign in",Toast.LENGTH_SHORT).show();
//                        }
//                    }else{
//                        Toast.makeText(MainActivity.this,"Passwords not matching", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//            }
//        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
