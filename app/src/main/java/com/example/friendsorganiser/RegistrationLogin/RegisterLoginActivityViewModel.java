package com.example.friendsorganiser.RegistrationLogin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.friendsorganiser.Models.LoginModel;
import com.example.friendsorganiser.Models.RegisterModel;

public class RegisterLoginActivityViewModel extends ViewModel implements OnLoginCallback, OnRegistrationCallback{

    private MutableLiveData<Boolean> isRegisterLoading;
    private MutableLiveData<Boolean> isLoginLoading;
    private MutableLiveData<LoginModel> loginModel;
    private MutableLiveData<RegisterModel> registerModel;
    private RegisterLoginActivityRepository registerLoginActivityRepository;

    public void init(){
        registerLoginActivityRepository = RegisterLoginActivityRepository.getInstance();
        registerLoginActivityRepository.init();
        isRegisterLoading = new MutableLiveData<>();
        isLoginLoading = new MutableLiveData<>();
        loginModel = new MutableLiveData<>();
        registerModel = new MutableLiveData<>();
    }

    public LiveData<Boolean> getIsRegisterLoading(){
        return isRegisterLoading;
    }

    public LiveData<Boolean> getIsLoginLoading(){
        return isLoginLoading;
    }

    public LiveData<LoginModel> getLoginModel(){
        return loginModel;
    }

    public LiveData<RegisterModel> getRegisterModel(){
        return registerModel;
    }

    public void registerUser(String name, String surname, String dateOfBirth, String email, String password){
        isRegisterLoading.setValue(true);
        RegisterModel currentRegisterModel = new RegisterModel(name, surname, dateOfBirth, email, password);
        registerLoginActivityRepository.registerUser(currentRegisterModel, this);
    }

    public void loginUser(String login, String password, boolean rememberMe){
        isLoginLoading.setValue(true);
        LoginModel currentUserLoginModel = new LoginModel(login, password, rememberMe);
        registerLoginActivityRepository.loginUser(currentUserLoginModel, this);
    }

    @Override
    public void onLoginCallback(LoginModel currentLoginModel) {
        loginModel.setValue(currentLoginModel);
        isLoginLoading.setValue(false);
    }

    @Override
    public void onRegisterCallback(RegisterModel currentRegisterModel) {
        registerModel.setValue(currentRegisterModel);
        isRegisterLoading.setValue(false);
    }
}
