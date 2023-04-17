package com.example.friendsorganiser.RegistrationLogin;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.friendsorganiser.R;
import com.example.friendsorganiser.databinding.FragmentRegisterBinding;

public class RegisterFragment extends Fragment {

    public FragmentRegisterBinding binding;

    public interface OnDataPass{
        void onRegisterDataPass(String name, String surname, String dateOfBirth,
                                String email, String password);
    }

    OnDataPass dataPasser;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }

    public void passRegisterData(String name, String surname, String dateOfBirth,
                                 String email, String password){
        dataPasser.onRegisterDataPass(name, surname, dateOfBirth, email, password);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        binding.btRegister.setOnClickListener(v -> {
            collectRegisterData();
        });

        super.onViewCreated(view, savedInstanceState);
    }

    public void setLoading(){
        binding.btRegister.setVisibility(View.INVISIBLE);
        binding.pbLoadingRegistration.setVisibility(View.VISIBLE);
    }

    public void disableLoading(){
        binding.btRegister.setVisibility(View.VISIBLE);
        binding.pbLoadingRegistration.setVisibility(View.INVISIBLE);
    }

    private void collectRegisterData() {
        String name = binding.etRegisterName.getText().toString();
        String surname = binding.etRegisterSurname.getText().toString();
        String birthDate = binding.etRegisterBirthDate.getText().toString();
        String email = binding.etRegisterEmail.getText().toString();
        String password = binding.etRegisterPassword.getText().toString();


        passRegisterData(name, surname, birthDate, email, password);
    }
}