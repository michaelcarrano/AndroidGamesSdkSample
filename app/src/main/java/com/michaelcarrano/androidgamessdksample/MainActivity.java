package com.michaelcarrano.androidgamessdksample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class MainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button showLeaderBoard = (Button) findViewById(R.id.show_leaderboard);
        showLeaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowLeaderboard();
            }
        });

        Button updateLeaderBoard = (Button) findViewById(R.id.update_leaderboard);
        updateLeaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateLeaderBoard(new Random().nextInt());
            }
        });

        Button showAchievement = (Button) findViewById(R.id.show_achievement);
        showAchievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowAchievement();
            }
        });

        Button updateAchievement = (Button) findViewById(R.id.update_achievement);
        updateAchievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateAchievement(new Random().nextInt());
            }
        });
    }
}
