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

    EditText username, password, repassword;
    Button signup, signin;
    DatabaseHelper DB;


    ///////////////////////////
    TextView textViewRestApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        repassword = (EditText)findViewById(R.id.repassword);

        signup = (Button)findViewById(R.id.button_signup);
        signin = (Button)findViewById(R.id.button_signin);
        DB = new DatabaseHelper(this);

        RetrofitHelper retrofitHelper = new RetrofitHelper();

    ///////////////////////////////
    textViewRestApi = findViewById(R.id.textViewRESTAPI);

//////////////////////////////
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://192.168.1.47:80/")
//                .addConverterFactory(MoshiConverterFactory.create())
//                .build();

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
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String repass = repassword.getText().toString();

                if(user.equals("") || pass.equals("") || repass.equals("")) {
                    Toast.makeText(MainActivity.this,"Please enter all the fields", Toast.LENGTH_SHORT).show(); // this bit is ok
                }
                if(pass.equals(repass)){
                    apiService.register(""+user,""+pass).enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(MainActivity.this,"Registration was successful",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                                startActivity(intent);
                            } else {
                                Log.d("LoginPage","Failed to log in. Only to be seen in logcat");
                                Toast.makeText(MainActivity.this,"something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }

//
//
//
//
//                    if(pass.equals(repass)) {
//                        Boolean checkuser = DB.checkusername(user); // this needs to be updated
//                        if(checkuser==false){
//                            Boolean insert = DB.insertData(user,pass); //this needs to be updated
//                            if(insert==true) {
//                                Toast.makeText(MainActivity.this,"Registered successfully",Toast.LENGTH_SHORT).show(); // this is probably ok
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
