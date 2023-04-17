package com.example.friendsorganiser.MainActivityPackage.MainActivity;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.friendsorganiser.R;
import com.google.android.material.navigation.NavigationBarView;

public class BottomNavigationListener extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private ViewPager2 viewPager2;

    public BottomNavigationListener(ViewPager2 viewPager2){
        this.viewPager2 = viewPager2;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_appointments: {
                viewPager2.setCurrentItem(0);
                break;
            }
            case R.id.item_chats: {
                viewPager2.setCurrentItem(1);
                break;
            }
            case R.id.item_friends: {
                viewPager2.setCurrentItem(2);
                break;
            }
        }
        return true;
    }
}
