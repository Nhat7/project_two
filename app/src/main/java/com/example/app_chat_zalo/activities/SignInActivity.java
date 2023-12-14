package com.example.app_chat_zalo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
//import android.widget.Toast;

import com.example.app_chat_zalo.databinding.ActivitySignInBinding;
import com.example.app_chat_zalo.utilities.Constants;
import com.example.app_chat_zalo.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestore;

//import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
    }

    private void setListener(){
        binding.txtCreateNewAccount.setOnClickListener(view ->
                startActivities(new Intent[]{new Intent(getApplicationContext(), SignUpActivity.class)}));
        binding.btnSignIn.setOnClickListener(v -> {
            if (isValidSignInDetail()){
                signIn();
            }
        });
    }

    private void signIn(){
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_PHONE, binding.txtPhone.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.txtPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Unable to sign in");
                    }
                });
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.btnSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.btnSignIn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

//    private void addDataToFirestore(){
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        HashMap<String, Object> data = new HashMap<>();
//        data.put("fist_name", "Thuy");
//        data.put("last_name", "Van");
//        db.collection("users")
//                .add(data)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(getApplicationContext(), "Data Inserted", Toast.LENGTH_LONG).show();
//                })
//
//                .addOnFailureListener(runnable -> {
//                    Toast.makeText(getApplicationContext(), runnable.getMessage(), Toast.LENGTH_LONG).show();
//                });
//    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),  message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetail(){
        if(binding.txtPhone.getText().toString().trim().isEmpty()){
            showToast("Enter Phone");
            return false;
        } else if(!Patterns.PHONE.matcher(binding.txtPhone.getText().toString()).matches()){
            showToast("Enter valid phone");
            return false;
        } else if(binding.txtPassword.getText().toString().trim().isEmpty()){
            showToast("Enter password");
            return false;
        } else {
            return true;
        }
    }
}