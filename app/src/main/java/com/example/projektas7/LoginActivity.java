package com.example.projektas7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String EMAIL ="email";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    EditText username, password;
    Button btnLogin;
    DatabaseHelper DB;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username=(EditText)findViewById(R.id.username_logPg);
        password = (EditText)findViewById(R.id.password_logPg);
        btnLogin = (Button)findViewById(R.id.button_signin_logPg);
        DB = new DatabaseHelper(this);

//        sharedPref = getPreferences(Context.MODE_PRIVATE);
        sharedPref = getSharedPreferences("UserData",MODE_PRIVATE);



        RetrofitHelper retrofitHelper = new RetrofitHelper();
        ApiService apiService = retrofitHelper.retrofitHelp().create(ApiService.class);
        btnLogin.setOnClickListener(v -> {

            String user = username.getText().toString();
            String pass = password.getText().toString();

            if(user.equals("") || pass.equals("")) {
                Toast.makeText(LoginActivity.this,"Please enter all the fields", Toast.LENGTH_SHORT).show();}
            else{

            apiService.login(""+user,""+pass).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();
                    if (response.isSuccessful()) {
                        username.getText().clear();
                        password.getText().clear();
                        saveUser(user);
                        Toast.makeText(LoginActivity.this,"Sign in is successful",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                        startActivity(intent);
                    } else {
                        Log.d("LoginPage","Failed to log in. Only to be seen in logcat");
                        Toast.makeText(LoginActivity.this,"Incorrect credentials", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    t.printStackTrace();
                }
            });}
        });
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String user = username.getText().toString();
//                String pass = password.getText().toString();
//
//                if(user.equals("") || pass.equals("")) {
//                    Toast.makeText(LoginActivity.this,"Please enter all the fields", Toast.LENGTH_SHORT).show();
//                }else{
//                    Boolean checkuserpass = DB.checkusernamepassword(user, pass);
//                    if(checkuserpass) {
//                        Toast.makeText(LoginActivity.this,"Sign in is successful",Toast.LENGTH_SHORT).show();
//                        saveUser(user, pass);
//                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
//                        startActivity(intent);
//                    }else{
//                        Toast.makeText(LoginActivity.this,"Invalid Credentials", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });

    }


    public void saveUser(User user){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ID,String.valueOf(user.getId()));
        editor.putString(NAME,user.getName());
        editor.putString(SURNAME,user.getSurname());
        editor.putString(EMAIL,user.getEmail());
        editor.putString(USERNAME,user.getUsername());
        editor.putString(PASSWORD,user.getPassword());

        editor.commit();
    }
//
    public User getUser(){
        int id = sharedPref.getInt(ID,-1);
        String name = sharedPref.getString(NAME,"");
        String surname = sharedPref.getString(SURNAME,"");
        String email = sharedPref.getString(EMAIL,"");
        String username = sharedPref.getString(USERNAME,"");
        String password = sharedPref.getString(PASSWORD,"");

        User user = new User();

        user.setId(id);
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);

        return user;
    }
}
