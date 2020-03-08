package com.cmput301w20t23.newber.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.UserController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private UserController userController;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        // hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        userController = new UserController(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // check if user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    public void login(View view) {

        email = ((EditText)(findViewById(R.id.emailLogin))).getText().toString();
        password = ((EditText)(findViewById(R.id.passwordLogin))).getText().toString();

        // if user has input values in both email and password fields
        if (userController.isLoginValid(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // if login was successful
                    if (task.isSuccessful()) {
                        Log.d("MYTAG", "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        // transition to main screen after log in
                        Intent mainIntent = new Intent(getBaseContext(), RiderMainActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                        finish();
                    }
                    // if login was unsuccessful
                    else {
                        Log.w("MYTAG", "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Incorrect username or password",
                                Toast.LENGTH_SHORT).show();
                        // clear email and password fields
                        ((EditText)(findViewById(R.id.emailLogin))).setText("");
                        ((EditText)(findViewById(R.id.passwordLogin))).setText("");
                    }
                }
            });
        }
    }

    public void signUp(View view) {
        Intent signUpIntent = new Intent(getBaseContext(), SignUpActivity.class);
        signUpIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(signUpIntent);
        finish();
    }

}
