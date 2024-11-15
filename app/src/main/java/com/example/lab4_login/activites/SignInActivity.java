package com.example.lab4_login.activites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab4_login.databinding.ActivitySignInBinding;
import com.example.lab4_login.utilities.Constants;
import com.example.lab4_login.utilities.PreferenceManager;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    /**
     * method with the on click listeners for the sign in button and create account text
     */
    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),SignUpActivity.class)));

        binding.buttonSignIn.setOnClickListener(v -> {

            if(isValidSignIn()){
            SignIn();

            }
        });
    }

    /**
     * Method to display a message as a Toast message
     * @param message the message displayed on the Toast popup
     */
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
    }

    /**
     * Method that reads from the Firestore Database to authenticate logins
     */
    private void SignIn() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful() && task.getResult() != null&& task.getResult().getDocuments().size()>0){
                       DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                       preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                       preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                       preferenceManager.putString(Constants.KEY_NAME,documentSnapshot.getString(Constants.KEY_NAME));
                       preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));

                       showToast("Successful Login");
                   }else{
                       loading(false);
                       showToast("Unable to Sign In");
                   }
                });
    }


    /**
     * A method that returns a boolean value depending on the information that is typed into the sign in boxes
     * @return returns true if all values are valid, false if there is an error with the info in the boxes
     */
    private boolean isValidSignIn() {
        if(binding.inputEmail.getText().toString().isEmpty()){
            showToast("Please enter your Email");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            showToast("Please enter a valid Email");
            return false;
        }else if(binding.inputPassword.getText().toString().isEmpty()){
            showToast("Please enter your Password");
            return false;
        }else{
            return true;
        }
    }

    /**
     * Method to change the button to the loading icon and back to the button
     * @param isLoading Boolean value that is either true - for loading or false - for not loading
     */
    private void loading(Boolean isLoading){
        if(isLoading){
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.buttonSignIn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }




}