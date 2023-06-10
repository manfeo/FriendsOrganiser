package com.example.friendsorganiser.RegistrationLogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.friendsorganiser.MainActivityPackage.MainActivity.MainActivity;
import com.example.friendsorganiser.Models.LoginModel;
import com.example.friendsorganiser.Models.RegisterModel;
import com.example.friendsorganiser.R;
import com.example.friendsorganiser.databinding.ActivityRegisterLoginBinding;

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
        //Load registration fragment
        if (key.equals("Регистрация")){
            //Moving button to bottom of screen
            ConstraintLayout.LayoutParams constraintLayoutParams = (ConstraintLayout.LayoutParams) binding.vSwitcherBackground.getLayoutParams();
            constraintLayoutParams.verticalBias = (float)0.9;
            AutoTransition autoTransition = new AutoTransition();
            autoTransition.setDuration(1000);
            TransitionManager.beginDelayedTransition(binding.vSwitcherBackground, autoTransition);
            binding.vSwitcherBackground.setLayoutParams(constraintLayoutParams);
            //Changing fragment and text on layout
            binding.btMovingButton.setText("Вход");
            binding.tvAlreadyHaveAccount.setText("Уже есть аккаунт в\nприложении?");
            ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            ft.replace(R.id.fl_registerLogin, new RegisterFragment(), "RegisterFragment");
            ft.commit();
        //Load login fragment
        } else{
            //Moving button to top of screen
            ConstraintLayout.LayoutParams constraintLayoutParams = (ConstraintLayout.LayoutParams) binding.vSwitcherBackground.getLayoutParams();
            constraintLayoutParams.verticalBias = (float)0.07;
            AutoTransition autoTransition = new AutoTransition();
            autoTransition.setDuration(1000);
            TransitionManager.beginDelayedTransition(binding.vSwitcherBackground, autoTransition);
            binding.vSwitcherBackground.setLayoutParams(constraintLayoutParams);
            //Changin fragment and text on layout
            binding.btMovingButton.setText("Регистрация");
            binding.tvAlreadyHaveAccount.setText("Ещё нет аккаунта в\nприложении");
            ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            ft.replace(R.id.fl_registerLogin, new LoginFragment(), "LoginFragment");
            ft.commit();
        }
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