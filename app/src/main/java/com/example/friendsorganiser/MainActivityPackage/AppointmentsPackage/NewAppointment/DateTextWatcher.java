package com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.NewAppointment;

import android.icu.util.Calendar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

public class DateTextWatcher implements TextWatcher {

    private String currentDate = "";
    private String ddmmyyyy = "DDMMYYYY";
    private LocalDateTime currentTime = LocalDateTime.now();
    private EditText currentEditText;
    private Calendar calendar = Calendar.getInstance();



    public DateTextWatcher(EditText currentEditText){
        this.currentEditText = currentEditText;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!s.toString().equals(currentDate)) {
            //Deleting all unnecessary symbols
            String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
            String cleanC = currentDate.replaceAll("[^\\d.]|\\.", "");

            int cleanDateLength = clean.length();
            int selectionPosition = cleanDateLength;
            for (int i = 2; i <= cleanDateLength && i < 6; i += 2) {
                selectionPosition++;
            }
            //Fix for pressing delete next to a forward slash
            if (clean.equals(cleanC)) selectionPosition--;

            if (clean.length() < 8){
                clean = clean + ddmmyyyy.substring(clean.length());
            }else{
                //This part makes sure that when we finish entering numbers
                //the date is correct, fixing it otherwise
                int day  = Integer.parseInt(clean.substring(0,2));
                int mon  = Integer.parseInt(clean.substring(2,4));
                int year = Integer.parseInt(clean.substring(4,8));

                mon = mon < 1 ? 1 : Math.min(mon, 12);
                calendar.set(Calendar.MONTH, mon - 1);
                year = year > 2100 ? 2100 : Math.max(year, currentTime.getYear());
                calendar.set(Calendar.YEAR, year);

                day = Math.min(day, calendar.getActualMaximum(Calendar.DATE));

                if (year == currentTime.getYear()){
                    if (mon < currentTime.getMonth().getValue()) {
                        mon = currentTime.getMonth().getValue();
                    }
                    if (day < currentTime.getDayOfMonth() && mon <= currentTime.getMonth().getValue())
                        day = currentTime.getDayOfMonth();
                }

                clean = String.format("%02d%02d%02d",day, mon, year);
            }
            clean = String.format("%s/%s/%s", clean.substring(0, 2),
                    clean.substring(2, 4),
                    clean.substring(4, 8));

            selectionPosition = Math.max(selectionPosition, 0);
            currentDate = clean;
            currentEditText.setText(currentDate);
            currentEditText.setSelection(Math.min(selectionPosition, currentDate.length()));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }
}
