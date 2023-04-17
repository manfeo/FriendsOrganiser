package com.example.friendsorganiser.RegistrationLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.friendsorganiser.MainActivityPackage.MainActivity.MainActivity;
import com.example.friendsorganiser.R;
import com.example.friendsorganiser.Utilities.Constants;
import com.example.friendsorganiser.Utilities.PreferenceManager;
import com.example.friendsorganiser.databinding.ActivityRegisterLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterLoginActivity extends AppCompatActivity implements RegisterFragment.OnDataPass, LoginFragment.OnDataPass{

    private FragmentTransaction ft;
    private FirebaseAuth currentAuth;
    private DatabaseReference databaseReference;
    private ActivityRegisterLoginBinding binding;
    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setBinding();
    }

    private void setBinding(){
        currentAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_registerLogin, new LoginFragment(), "LoginFragment");
        ft.commit();

        binding.btMovingButton.setOnClickListener(view -> {
            String directionOfAnimation = binding.btMovingButton.getText().toString();
            moveButtonAnimation(directionOfAnimation);
        });
    }

    private void registerNewUser(String name, String surname, String dateOfBirth, String email, String password) {
        loadingRegistration(true);
        currentAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uriToSetData = task.getResult().getUser().getUid();
                        handleRegistration(name, surname, dateOfBirth, email, password, uriToSetData);
                        loadingRegistration(false);
                    } else{
                        loadingRegistration(false);
                        String message = task.getException().toString();
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleRegistration(String name, String surname, String dateOfBirth, String email, String password, String uriToSetData) {
        HashMap<String, Object> mapToSendToFirebase = new HashMap<>();
        mapToSendToFirebase.put(Constants.KEY_NAME, name);
        mapToSendToFirebase.put(Constants.KEY_SURNAME, surname);
        mapToSendToFirebase.put(Constants.KEY_DATE_OF_BIRTH, dateOfBirth);
        mapToSendToFirebase.put(Constants.KEY_EMAIL, email);
        mapToSendToFirebase.put(Constants.KEY_PASSWORD, password);

        databaseReference.child(Constants.KEY_DATABASE_USERS).child(uriToSetData).setValue(mapToSendToFirebase).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Данные успешно загружены", Toast.LENGTH_SHORT).show();
                        preferenceManager.putString(Constants.KEY_USER_ID, uriToSetData);
                        preferenceManager.putString(Constants.KEY_NAME, name);
                        preferenceManager.putString(Constants.KEY_SURNAME, surname);

                        //Add image adding

                    } else {
                        String errorMessage = task.getException().toString();
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
        moveButtonAnimation("Вход");
    }

    private void loginUser(String email, String password, boolean rememberMe){
        loadingLogin(true);
        currentAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uriToLogin = task.getResult().getUser().getUid();
                        handleLogin(rememberMe, uriToLogin);
                        loadingLogin(false);
                    } else{
                        loadingLogin(false);
                        String message = task.getException().toString();
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleLogin(boolean rememberMe, String uriToLogin){
        if (rememberMe) {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void moveButtonAnimation(String key){

        ObjectAnimator animator;

        if (key.equals("Вход")){
            animator = ObjectAnimator.ofFloat(binding.llMovingBar, "translationY",0f);
            binding.btMovingButton.setText("Регистрация");
            binding.tvAlreadyHaveAccount.setText("Ещё нет аккаунта в\nприложении");
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_registerLogin, new LoginFragment(), "LoginFragment");
            ft.commit();
            /*
            new Handler().postDelayed(() -> {
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.register_login_frame_layout, new LoginFragment());
                ft.commit();
            }, 1000);
             */

        } else{
            animator = ObjectAnimator.ofFloat(binding.llMovingBar, "translationY",1375f);
            binding.btMovingButton.setText("Вход");
            binding.tvAlreadyHaveAccount.setText("Уже есть аккаунт в\nприложении?");
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fl_registerLogin, new RegisterFragment(), "RegisterFragment");
            ft.commit();
            /*
            new Handler().postDelayed(() -> {
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.register_login_frame_layout, new RegisterFragment());
                ft.commit();
            }, 1000);
             */
        }
        animator.setDuration(1000);
        animator.start();
    }

    private void loadingRegistration(boolean isLoading){
        RegisterFragment fragment = (RegisterFragment) getSupportFragmentManager().findFragmentByTag("RegisterFragment");
        if (isLoading) {
            fragment.setLoading();
        } else {
            fragment.disableLoading();
        }
    }

    private void loadingLogin(boolean isLoading){
        LoginFragment fragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag("LoginFragment");
        if (isLoading) {
            fragment.setLoading();
        } else {
            fragment.disableLoading();
        }
    }

    @Override
    public void onRegisterDataPass(String name, String surname, String dateOfBirth,
                                   String email, String password) {
        registerNewUser(name, surname, dateOfBirth, email, password);
    }

    @Override
    public void onLoginDataPass(String email, String password, Boolean rememberMe) {
        loginUser(email, password, rememberMe);
    }
}