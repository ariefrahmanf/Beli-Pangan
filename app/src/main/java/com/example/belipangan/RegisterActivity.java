package com.example.belipangan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.belipangan.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etNama, etNoTelp;

    String email, password, nama, noTelp, userID;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbReference;
    User pengguna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        dbReference = database.getReference("user");

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etNama= findViewById(R.id.etNama);
        etNoTelp = findViewById(R.id.etNoTelpon);
    }

    public void register(View view) {
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        nama = etNama.getText().toString().trim();
        noTelp = etNoTelp.getText().toString().trim();

        if(email.length() == 0){
            etEmail.setError("Email harus di isi!");
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Email tidak valid");
        }
        else if(password.length() == 0){
            etPassword.setError("Password harus di isi!");
        }
        else if(nama.length() == 0){
            etNama.setError("Nama harus di isi!");
        }
        else if(noTelp.length() == 0){
            etNoTelp.setError("No. Telpon harus di isi!");
        }
        else{
            registerUser(email, password);
        }
    }

    private void registerUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("sign-up", "createUserWithEmail:success");

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            pengguna = new User(nama, email, noTelp);
                            updateUI(firebaseUser);

                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w("sign-up fail", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser firebaseUser){
//        String keyId = dbReference.push().getKey();
        String keyId = firebaseUser.getUid();
        dbReference.child(keyId).setValue(pengguna);

        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

}