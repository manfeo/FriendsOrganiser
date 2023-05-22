package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.NewAppointment;

import android.icu.util.Calendar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeTextWatcher implements TextWatcher {

    private String currentTime = "";
    private final String hhmm = "HHmm";
    private EditText currentEditText;
    private EditText dateEditText;
    private LocalDateTime currentDate;

    public TimeTextWatcher(EditText currentEditText, EditText dateEditText){
        this.currentEditText = currentEditText;
        this.dateEditText = dateEditText;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!s.toString().equals(currentTime)) {
            currentDate = LocalDateTime.now();
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

                String selectedDate = dateEditText.getText().toString();
                if (!selectedDate.equals("") && selectedDate.length() == 10){
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate dateChecker = LocalDate.parse(selectedDate, dateTimeFormatter);
                    if (currentDate.getYear() == dateChecker.getYear() &&
                            currentDate.getMonth().getValue() == dateChecker.getMonth().getValue() &&
                            currentDate.getDayOfMonth() == dateChecker.getDayOfMonth()){
                        int currentHour = currentDate.getHour();
                        int currentMinute = currentDate.getMinute();
                        if (hour < currentHour)
                            hour = currentHour;
                        if (hour == currentHour)
                            if (minute < currentMinute)
                                minute = currentMinute;
                        clean = String.format("%02d%02d", hour, minute);
                    }
                }
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
