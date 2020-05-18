package com.example.StatRack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.StatRack.databinding.ActivityNotesViewBinding;
import com.example.StatRack.fragment.MyNotesFragment;
import com.example.StatRack.fragment.MyTopNotesFragment;

public class TestNotesView extends AppCompatActivity {

    private static final String TAG = "TestNotesView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNotesViewBinding binding = ActivityNotesViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Create the adapter that will return a fragment for each section
        FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            private final Fragment[] mFragments = new Fragment[]{
                    new MyNotesFragment(),
                    new MyTopNotesFragment(),
            };
            private final String[] mFragmentTitles = new String[]{
                    getString(R.string.heading_notes),
                    getString(R.string.heading_my_top_notes)
            };

            @Override
            public Fragment getItem(int description) {
                return mFragments[description];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int description) {
                return mFragmentTitles[description];
            }
        };
        // Set up the ViewPager with the sections adapter.
        binding.container.setAdapter(mPagerAdapter);
        binding.tabs.setupWithViewPager(binding.container);

        // Button launches NewNoteActivity
        binding.newNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestNotesView.this, NewNoteActivity.class));
            }
        });
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(TestNotesView.this,message,Toast.LENGTH_SHORT).show();
    }

    public void backToMenu(){
        Intent intent = new Intent(TestNotesView.this, MenuActivity.class);
        startActivity(intent);
    }
}
