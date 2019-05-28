package com.example.alvin.vstsmobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity  {

    private EditText editTextName;
    private EditText editTextpassword;
    private Button buttonlogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if (SharedPrefManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(this,MainActivity.class));
            finish();
            return;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        editTextName = findViewById(R.id.login_name);
        editTextpassword = findViewById(R.id.login_password);
        buttonlogin = findViewById(R.id.login_button);
        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });
        progressDialog = new ProgressDialog(this);




    }

    private void checkLogin(){
        final String name = editTextName.getText().toString().trim();
        final String password = editTextpassword.getText().toString().trim();
        progressDialog.setMessage("Verifying");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")){
                                String id = jsonObject.getString("id");
                                System.out.println(id);
                                SharedPrefManager.getInstance(getApplicationContext())
                                        .userLogin(id,name);
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();

                            }else {
                                Toast.makeText(getApplicationContext(),"Sorry! Incorrect Username or password",Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name",name);
                params.put("password",password);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);


    }
}
