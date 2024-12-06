package com.example.lab4_login.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab4_login.databinding.ActivityMainBinding;
import com.example.lab4_login.utilities.Constants;
import com.example.lab4_login.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        getToken();
        setListeners();
    }

    /**
     * Sets the listeners for the buttons on screen
     */
    private void setListeners(){
        binding.imagesSignOut.setOnClickListener(v -> signOut());
        binding.fabNewChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), UserActivity.class)));
    }

    /**
     * Loads the details of the user for the header
     */
    private void loadUserDetails(){
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    /**
     * Displays toast messages
     * @param message message displayed by toast
     */
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
    }

    /**
     * Gets the FCM token of the user in the database
     */
    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    /**
     * Updates the token of the user when logging in
     * @param token the token that will be passed into firebase
     */
    private void updateToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
                .addOnSuccessListener(unused -> showToast("Token Updated successfully"))
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }

    /**
     * Signs the user out of the program and removes token from firebase
     */
    private void signOut(){
        showToast("Signing Out.....");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                    finish();
                }).addOnFailureListener(e -> showToast("Unable to sign out"));

    }
}