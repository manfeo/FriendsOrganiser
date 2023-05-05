package com.example.friendsorganiser.RegistrationLogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.friendsorganiser.MainActivityPackage.MainActivity.MainActivity;
import com.example.friendsorganiser.Models.LoginModel;
import com.example.friendsorganiser.Models.RegisterModel;
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
    private ActivityRegisterLoginBinding binding;
    private RegisterLoginActivityViewModel registerLoginActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerLoginActivityViewModel = new ViewModelProvider(this).get(RegisterLoginActivityViewModel.class);
        registerLoginActivityViewModel.init();

        setBinding();
        setObservers();
    }

    private void setBinding(){
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fl_registerLogin, new LoginFragment(), "LoginFragment");
        ft.commit();

        binding.btMovingButton.setOnClickListener(view -> {
            String directionOfAnimation = binding.btMovingButton.getText().toString();
            switchLoginRegistration(directionOfAnimation);
        });
    }

    private void setObservers(){
        registerLoginActivityViewModel.getIsRegisterLoading().observe(this, this::loadingRegistration);
        registerLoginActivityViewModel.getIsLoginLoading().observe(this, this::loadingLogin);
        registerLoginActivityViewModel.getLoginModel().observe(this, this::handleLogin);
        registerLoginActivityViewModel.getRegisterModel().observe(this, this::handleRegister);
    }

    private void registerNewUser(String name, String surname, String dateOfBirth, String email, String password) {
        registerLoginActivityViewModel.registerUser(name, surname, dateOfBirth, email, password);
    }

    private void loginUser(String email, String password, boolean rememberMe){
        registerLoginActivityViewModel.loginUser(email, password, rememberMe);
    }

    private void handleRegister(RegisterModel registerModel){
        if (registerModel.isRegisterValid()){
            switchLoginRegistration("Вход");
        } else {
            Toast.makeText(this, "Unable to register", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLogin(LoginModel loginModel){
        if (loginModel.isLoginValid()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Unable to login", Toast.LENGTH_SHORT).show();
        }
    }

    private void switchLoginRegistration(String key){

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