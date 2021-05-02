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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

//    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private ProgressDialog p;
    private Button Loginbtn,Phonebtn;
    private EditText UserEmail,UserPassword;
    private TextView needAccount,ForgetPasswordLink;

    //d
    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

//        currentUser = mAuth.getCurrentUser();

        InitializeFields();

        needAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToRegisterActivity();
            }
        });
        Loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowUserToLogin();
            }
        });
    }

    private void allowUserToLogin() {
        final String email = UserEmail.getText().toString();
        final String password = UserPassword.getText().toString();

        p.setTitle("Loading");
        p.setMessage("please wait...");
        p.setCancelable(false);
        p.setCanceledOnTouchOutside(false);

        if (TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this, "Fields should not be empty", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this,"Fields should not be empty",Toast.LENGTH_SHORT).show();
        }else {
            p.show();
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                //d
                                String currentUserIDTk = mAuth.getCurrentUser().getUid();
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                UsersRef.child(currentUserIDTk).child("device_token")
                                        .setValue(deviceToken)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    Log.d("signIn", "signInWithEmail:success");
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    if (user != null){
                                                        sendUserToMainActivity();
                                                    }
                                                    p.dismiss();
                                                }
                                            }
                                        });
                                //d

                            }else {
                                // If sign in fails, display a message to the user.
                                p.dismiss();
                                Log.w("signIn", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void InitializeFields() {
        Loginbtn = (Button)findViewById(R.id.login_button);
//        Phonebtn = (Button)findViewById(R.id.phone_login_button);
        UserEmail = (EditText)findViewById(R.id.login_email);
        UserPassword = (EditText)findViewById(R.id.login_password);
        needAccount = (TextView)findViewById(R.id.need_an_account_link);
        ForgetPasswordLink = (TextView)findViewById(R.id.forget_password);

        p = new ProgressDialog(LoginActivity.this);


    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (currentUser != null){
//            sendUserToMainActivity();
//        }
//    }

    private void sendUserToMainActivity() {
        Intent loginIntent = new Intent(LoginActivity.this,MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    private void sendUserToRegisterActivity() {

        Intent RegisterIntent = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(RegisterIntent);
        finish();
    }
}
