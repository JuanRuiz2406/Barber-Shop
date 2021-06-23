package com.example.barbershop.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.barbershop.controllers.Screen1;
import com.example.barbershop.controllers.Screen3;
import com.example.barbershop.controllers.Screen4;

public class FragmentAdapter extends FragmentStateAdapter {
    public FragmentAdapter(@NonNull @org.jetbrains.annotations.NotNull FragmentManager fragmentManager,
                           @NonNull @org.jetbrains.annotations.NotNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 1:
                return new Screen3();
            case 2:
                return new Screen4();
        }
        return new Screen1();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
