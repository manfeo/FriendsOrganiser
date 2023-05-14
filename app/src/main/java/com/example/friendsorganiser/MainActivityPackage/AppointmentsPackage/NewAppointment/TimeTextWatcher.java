package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.NewAppointment;

import android.icu.util.Calendar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.time.LocalDateTime;

public class TimeTextWatcher implements TextWatcher {

    private String currentTime = "";
    private String hhmm = "HHmm";
    private EditText currentEditText;

    public TimeTextWatcher(EditText currentEditText){
        this.currentEditText = currentEditText;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!s.toString().equals(currentTime)) {
            //Deleting all unnecessary symbols
            String clean = s.toString().replaceAll("[^\\d.]|\\.:", "");
            String cleanC = currentTime.replaceAll("[^\\d.]|\\.:", "");

            int cleanDateLength = clean.length();
            int selectionPosition = cleanDateLength;
            for (int i = 2; i <= cleanDateLength && i < 6; i += 2) {
                selectionPosition++;
            }
            //Fix for pressing delete next to a forward slash
            if (clean.equals(cleanC)) selectionPosition--;

            if (clean.length() < 4){
                clean = clean + hhmm.substring(clean.length());
            }else{

                int hour = Integer.parseInt(clean.substring(0,2));
                int minute = Integer.parseInt(clean.substring(2, 4));

                hour = Math.min(hour, 23);
                minute = Math.min(minute, 59);

                clean = String.format("%02d%02d", hour, minute);
            }
            clean = String.format("%s:%s", clean.substring(0, 2),
                    clean.substring(2, 4));

            selectionPosition = Math.max(selectionPosition, 0);
            currentTime = clean;
            currentEditText.setText(currentTime);
            currentEditText.setSelection(Math.min(selectionPosition, currentTime.length()));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }
}
