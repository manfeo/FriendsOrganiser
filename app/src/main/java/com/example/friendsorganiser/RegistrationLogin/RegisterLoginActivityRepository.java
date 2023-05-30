package com.example.friendsorganiser.RegistrationLogin;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.friendsorganiser.Models.LoginModel;
import com.example.friendsorganiser.Models.RegisterModel;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterLoginActivityRepository {

    private static RegisterLoginActivityRepository instance;
    private DatabaseReference databaseReference;
    private FirebaseAuth currentAuth;
    private PreferenceManager preferenceManager;

    public static RegisterLoginActivityRepository getInstance(){
        if (instance == null)
            instance = new RegisterLoginActivityRepository();
        return instance;
    }

    public void init(){
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentAuth = FirebaseAuth.getInstance();
        preferenceManager = PreferenceManager.getInstance();
    }

    public void registerUser(RegisterModel registerModel, OnRegistrationCallback onRegistrationCallback){
        currentAuth.createUserWithEmailAndPassword(registerModel.getEmail(), registerModel.getPassword()).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uriToSetData = task.getResult().getUser().getUid();
                        handleRegistration(registerModel, uriToSetData);
                        onRegistrationCallback.onRegisterCallback(registerModel);
                    } else{
                        onRegistrationCallback.onRegisterCallback(registerModel);
                        Log.d("register", task.getException().toString());
                    }
                });
    }

    private void handleRegistration(RegisterModel registerModel, String uriToSetData) {
        registerModel.setIsRegisterValid(true);

        HashMap<String, Object> mapToSendToFirebase = new HashMap<>();
        mapToSendToFirebase.put(Constants.KEY_NAME, registerModel.getName());
        mapToSendToFirebase.put(Constants.KEY_SURNAME, registerModel.getSurname());
        mapToSendToFirebase.put(Constants.KEY_DATE_OF_BIRTH, registerModel.getDateOfBirth());
        mapToSendToFirebase.put(Constants.KEY_EMAIL, registerModel.getEmail());
        mapToSendToFirebase.put(Constants.KEY_PASSWORD, registerModel.getPassword());

        databaseReference.child(Constants.KEY_DATABASE_USERS).child(uriToSetData).setValue(mapToSendToFirebase).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        preferenceManager.putString(Constants.KEY_USER_ID, uriToSetData);
                        preferenceManager.putString(Constants.KEY_NAME, registerModel.getName());
                        preferenceManager.putString(Constants.KEY_SURNAME, registerModel.getSurname());
                    } else {
                        String errorMessage = task.getException().toString();
                        Log.d("register", errorMessage);
                    }
                });
    }

    public void loginUser(LoginModel loginModel, OnLoginCallback onLoginCallback){
        currentAuth.signInWithEmailAndPassword(loginModel.getEmail(), loginModel.getPassword()).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uriToLogin = task.getResult().getUser().getUid();
                        handleLogin(loginModel, uriToLogin, onLoginCallback);
                    } else{
                        Log.d("login", task.getException().toString());
                        onLoginCallback.onLoginCallback(loginModel);
                    }
                });
    }

    private void handleLogin(LoginModel loginModel, String uriToLogin, OnLoginCallback onLoginCallback){
        loginModel.setLoginValid(true);
        if (loginModel.isRememberMe()) {
            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
        }

        databaseReference.child(Constants.KEY_DATABASE_USERS).child(uriToLogin).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userName = snapshot.child(Constants.KEY_NAME).getValue().toString();
                        String userSurname = snapshot.child(Constants.KEY_SURNAME).getValue().toString();

                        preferenceManager.putString(Constants.KEY_USER_ID, uriToLogin);
                        preferenceManager.putString(Constants.KEY_NAME, userName);
                        preferenceManager.putString(Constants.KEY_SURNAME, userSurname);
                        if (snapshot.hasChild(Constants.KEY_IMAGE)) {
                            String photoUri = snapshot.child(Constants.KEY_IMAGE).getValue().toString();
                            preferenceManager.putString(Constants.KEY_IMAGE, photoUri);
                        }
                        onLoginCallback.onLoginCallback(loginModel);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("login", error.toString());
                    }
                });
    }
}
