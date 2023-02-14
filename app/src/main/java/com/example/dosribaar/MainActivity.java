package com.example.dosribaar;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.dosribaar.Fragments.AddFragment;
import com.example.dosribaar.Fragments.HomeFragment;
import com.example.dosribaar.Fragments.Notification2Fragment;
import com.example.dosribaar.Fragments.ProfileFragment;
import com.example.dosribaar.Fragments.SearchFragment;
import com.example.dosribaar.databinding.ActivityMainBinding;
import com.iammert.library.readablebottombar.ReadableBottomBar;

public class MainActivity extends AppCompatActivity {

    static Bundle args;
    String ID;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ID = MemoryData.getName(getApplicationContext());
        args = new Bundle();
        args.putString("ID", ID);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HomeFragment hf = new HomeFragment();
        hf.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, hf);
        transaction.commit();


        binding.readableBottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int i) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (i) {

                    case 0:
                        HomeFragment hf = new HomeFragment();
                        hf.setArguments(args);
                        transaction.replace(R.id.container, hf);
                        break;

                    case 1:
                        SearchFragment sf = new SearchFragment();
                        sf.setArguments(args);
                        transaction.replace(R.id.container, sf);
                        break;
                    case 2:

                        AddFragment af = new AddFragment();
                        af.setArguments(args);
                        transaction.replace(R.id.container, af);
                        break;

                    case 3:
                        Notification2Fragment nf = new Notification2Fragment();
                        nf.setArguments(args);
                        transaction.replace(R.id.container, nf);
                        break;

                    case 4:

                        ProfileFragment pf = new ProfileFragment();
                        pf.setArguments(args);
                        transaction.replace(R.id.container, pf);
                        break;
                }
                transaction.commit();
            }
        });


    }

}