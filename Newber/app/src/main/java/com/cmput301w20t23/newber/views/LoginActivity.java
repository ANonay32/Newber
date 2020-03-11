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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * The Android Activity that handles user login.
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private UserController userController;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseDatabase.getInstance().getReference("users")
                    .child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String role = dataSnapshot.child("role").getValue(String.class);
                    System.out.println(role);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        else {
            setContentView(R.layout.activity_login);

            // hide action bar
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();

            userController = new UserController(this);
        }
    }

    /**
     * Initiates logging in when the appropriate button is clicked.
     *
     * @param view the button that was clicked
     */
    public void login(View view) {

        email = ((EditText)(findViewById(R.id.email_login))).getText().toString();
        password = ((EditText)(findViewById(R.id.password_login))).getText().toString();

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
                        Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                        finish();
                    }
                    // if login was unsuccessful
                    else {
                        Log.w("MYTAG", "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Invalid credentials",
                                Toast.LENGTH_SHORT).show();
                        // clear email and password fields
                        ((EditText)(findViewById(R.id.email_login))).setText("");
                        ((EditText)(findViewById(R.id.password_login))).setText("");
                    }
                }
            });
        }
    }

    /**
     * Switches to SignUpActivity when the appropriate link is clicked.
     *
     * @param view the TextView link that was clicked
     */
    public void signUp(View view) {
        Intent signUpIntent = new Intent(getBaseContext(), SignUpActivity.class);
        signUpIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(signUpIntent);
        finish();
    }

}
