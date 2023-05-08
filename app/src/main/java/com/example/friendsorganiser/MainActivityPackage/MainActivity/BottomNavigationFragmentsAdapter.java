package com.example.friendsorganiser.MainActivityPackage.MainActivity;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.friendsorganiser.MainActivityPackage.AppointmentsPackage.AppointmentFragment;
import com.example.friendsorganiser.MainActivityPackage.ChatsPackage.AllChats.RecentChats;
import com.example.friendsorganiser.MainActivityPackage.FriendsPackage.FriendsList.FriendFragment;

public class BottomNavigationFragmentsAdapter extends FragmentStateAdapter {
    public BottomNavigationFragmentsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AppointmentFragment();
            case 1:
                return new RecentChats();
            case 2:
                return new FriendFragment();
        }
        return new AppointmentFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
