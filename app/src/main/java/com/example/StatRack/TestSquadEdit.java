package com.example.StatRack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.StatRack.databinding.ActivitySquadEditBinding;
import com.example.StatRack.fragment.MyPlayersFragment;
import com.example.StatRack.fragment.MyTopPlayersFragment;

public class TestSquadEdit extends AppCompatActivity {

    private static final String TAG = "TestSquadEdit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySquadEditBinding binding = ActivitySquadEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Create the adapter that will return a fragment for each section
        FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            private final Fragment[] mFragments = new Fragment[]{
                    new MyPlayersFragment(),
                    new MyTopPlayersFragment(),
            };
            private final String[] mFragmentNames = new String[]{
                    getString(R.string.heading_squad),
                    getString(R.string.heading_my_top_players)
            };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };
        // Set up the ViewPager with the sections adapter.
        binding.container.setAdapter(mPagerAdapter);
        binding.tabs.setupWithViewPager(binding.container);

        // Button launches NewPlayerActivity
        binding.newPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestSquadEdit.this, NewPlayerActivity.class));
            }
        });
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(TestSquadEdit.this,message,Toast.LENGTH_SHORT).show();
    }

    public void backToMenu(){
        Intent intent = new Intent(TestSquadEdit.this, MenuActivity.class);
        startActivity(intent);
    }
}
