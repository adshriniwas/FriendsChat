package com.friends.friendschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private Button createAccountButton;
    private EditText regEmail,regPassword,regFname,regLname,regConfPassword;
    private TextView alreadyAccount;
    private RadioButton radioMale,radioFemale;
    private String Gender = "";
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        InitializeFields();

        alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendUserToLoginActivity();

            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }
        });
    }

    private void createNewAccount() {
        if (radioMale.isChecked()){
            Gender = "Male";
        }
        if (radioMale.isChecked()){
            Gender = "Female";
        }
        final String email = regEmail.getText().toString();
        final String password = regPassword.getText().toString();
        final String confpassword = regConfPassword.getText().toString();
        final String fname = regFname.getText().toString();
        final String lname = regLname.getText().toString();

        p.setTitle("Loading");
        p.setMessage("please wait...");
        p.setCancelable(false);
        p.setCanceledOnTouchOutside(false);

        if (TextUtils.isEmpty(email)){
            Toast.makeText(RegisterActivity.this, "Fields should not be empty", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this,"Fields should not be empty",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(confpassword)){
            Toast.makeText(RegisterActivity.this, "Fields should not be empty", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(fname)){
            Toast.makeText(RegisterActivity.this, "Fields should not be empty", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(lname)){
            Toast.makeText(RegisterActivity.this, "Fields should not be empty", Toast.LENGTH_SHORT).show();
        }else if (!password.equals(confpassword)){
            Toast.makeText(RegisterActivity.this, "Password didnt matched", Toast.LENGTH_SHORT).show();
        }else if (Gender == ""){
            Toast.makeText(RegisterActivity.this, "Gender is not checked", Toast.LENGTH_SHORT).show();
        }else {
            p.show();
            mAuth.createUserWithEmailAndPassword(email, confpassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
//                                String currentUserID = mAuth.getCurrentUser().getUid();
//                                StudentInfo informationp = new StudentInfo(
//                                        fname,
//                                        lname,
//                                        email,
//                                        confpassword,
//                                        Gender
//                                );
                                DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                Map RegisterBody = new HashMap();
                                RegisterBody.put("fname",fname);
                                RegisterBody.put("lname", lname);
                                RegisterBody.put("email", email);
                                RegisterBody.put("password", confpassword);
                                RegisterBody.put("gender", Gender);
                                RegisterBody.put("device_token", deviceToken);
                                        dref.updateChildren(RegisterBody)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {


                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    updateUI(user);
                                                    p.dismiss();
                                                }
                                            }
                                        });
                            } else {
                                // If sign in fails, display a message to the user.
                                p.dismiss();
                                Log.w("CreateUser", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            // ...
                        }
                    });
        }
    }

    private void sendUserToLoginActivity() {
        Intent LoginIntent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(LoginIntent);
        finish();
    }

    private void InitializeFields() {
        alreadyAccount= findViewById(R.id.already_have_an_account_link);
        createAccountButton = findViewById(R.id.register_button);
        regEmail = findViewById(R.id.register_email);
        regPassword = findViewById(R.id.register_password);
        regFname = findViewById(R.id.register_firstname);
        regLname = findViewById(R.id.register_lastname);
        regConfPassword = findViewById(R.id.register_conf_pass);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);

        p = new ProgressDialog(RegisterActivity.this);

    }
    private void updateUI(FirebaseUser user) {
        //update ui code here
        if (user != null) {
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendUserToLoginActivity();
    }
}
