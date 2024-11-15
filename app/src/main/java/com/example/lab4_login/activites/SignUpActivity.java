package com.example.lab4_login.activites;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab4_login.R;
import com.example.lab4_login.databinding.ActivitySignInBinding;
import com.example.lab4_login.databinding.ActivitySignUpBinding;
import com.example.lab4_login.utilities.Constants;
import com.example.lab4_login.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    private PreferenceManager preferenceManager;
    private String encodeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    /**
     * Methood to set the listeners for the sign up button
     * and the sign in text
     */
    private void setListeners() {

        binding.textSignIn.setOnClickListener(v -> onBackPressed());

        binding.buttonSignUp.setOnClickListener(v -> {
            if(isValidSignUp()){
                SignUp();
            }
        });

        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }


    /**
     * Method to display a Toast message
     * @param message The message displayed in the Toast Popup
     */
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_SHORT).show();
    }

    /**
     * Method to store the login information into the Firebase Storage
     */
    private void SignUp(){
        //check loading
        loading(true);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String,String> user = new HashMap<>();

        user.put(Constants.KEY_NAME,binding.inputName.getText().toString());
        user.put(Constants.KEY_EMAIL,binding.inputEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString());

        user.put(Constants.KEY_IMAGE,encodeImage);

        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                    preferenceManager.putString(Constants.KEY_NAME,binding.inputName.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE,encodeImage);

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }).addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());

                });
        
    }

    /**
     * Method to store the profile image as a bitmap for the database
     * @param bitmap the BitMap that will contain the image
     * @return A string value that is the Image profile encoded
     */
    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight()*previewWidth/bitmap.getWidth();

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewHeight,previewWidth,false);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    //changes the add image button into the image
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->{
                if(result.getResultCode() == RESULT_OK){
                    Uri imageUri = result.getData().getData();
                    try{
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        binding.imageProfile.setImageBitmap(bitmap);
                        binding.textAddImage.setVisibility(View.GONE);
                        encodeImage = encodeImage(bitmap);
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
            }
    );

    /**
     * Method to check if all of the boxes have valid information
     * @return Returns true if all boxes are valid, false if there is an error in a box
     */
    private Boolean isValidSignUp(){
        if(binding.inputName.getText().toString().isEmpty()){
            showToast("Please enter your Name");
            return false;
        }else if(encodeImage == null){
            showToast("Please Select a profile Image");
            return false;
        }else if(binding.inputEmail.getText().toString().isEmpty()){
            showToast("Please enter your Email");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            showToast("Please enter a valid Email");
            return false;
        }else if(binding.inputPassword.getText().toString().isEmpty()){
            showToast("Please enter your Password");
            return false;
        }else if(binding.confirmPassword.getText().toString().isEmpty()){
            showToast("Please confirm your Password");
            return false;
        }if(!binding.inputPassword.getText().toString().equals(binding.confirmPassword.getText().toString())){
            showToast("Password and Confirm Password must be the same");
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
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.buttonSignUp.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}