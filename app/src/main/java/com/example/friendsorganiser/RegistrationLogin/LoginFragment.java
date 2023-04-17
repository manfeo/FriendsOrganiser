package com.example.friendsorganiser.RegistrationLogin;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.friendsorganiser.R;
import com.example.friendsorganiser.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    public interface OnDataPass{
        void onLoginDataPass(String email, String password, Boolean rememberMe);
    }

    OnDataPass dataPasser;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dataPasser = (LoginFragment.OnDataPass) context;
    }

    public void passLoginData(String email, String password, Boolean rememberMe){
        dataPasser.onLoginDataPass(email, password, rememberMe);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.btLogin.setOnClickListener(v -> {
            passLoginData();
        });
        super.onViewCreated(view, savedInstanceState);
    }

    public void setLoading(){
        binding.btLogin.setVisibility(View.INVISIBLE);
        binding.pbLoadingLogin.setVisibility(View.VISIBLE);
    }

    public void disableLoading(){
        binding.btLogin.setVisibility(View.VISIBLE);
        binding.pbLoadingLogin.setVisibility(View.INVISIBLE);
    }

    private void passLoginData() {
        String email = binding.etLoginEmail.getText().toString();
        String password = binding.etLoginPassword.getText().toString();
        Boolean rememberMe = binding.cbRememberMe.isChecked();
        passLoginData(email, password, rememberMe);
    }
}