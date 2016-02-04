package com.michaelcarrano.androidgamessdksample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.amazon.ags.api.AmazonGamesCallback;
import com.amazon.ags.api.AmazonGamesClient;
import com.amazon.ags.api.AmazonGamesFeature;
import com.amazon.ags.api.AmazonGamesStatus;
import com.amazon.ags.api.achievements.AchievementsClient;
import com.amazon.ags.api.leaderboards.LeaderboardsClient;
import com.amazon.ags.api.overlay.PopUpLocation;
import com.michaelcarrano.androidgamessdksample.games.Achievement;
import com.michaelcarrano.androidgamessdksample.games.LeaderBoard;

import java.util.EnumSet;

/**
 * Created by michaelcarrano on 2/3/16.
 */
public class BaseActivity extends AppCompatActivity implements LeaderBoard, Achievement {

    private AmazonGamesClient mAmazonGamesClient;
    private AchievementsClient mAchievementsClient;
    private LeaderboardsClient mLeaderboardsClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        initAmazonClients();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAmazonGamesClient != null) {
            mAmazonGamesClient.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAmazonGamesClient != null) {
            mAmazonGamesClient.shutdown();
        }
    }

    private void initAmazonClients() {
        AmazonGamesClient.initialize(this, new AmazonGamesCallback() {
            @Override
            public void onServiceReady(AmazonGamesClient amazonGamesClient) {
                Log.e("AmazonBase", "onServiceReady");
                mAmazonGamesClient = amazonGamesClient;
                mAmazonGamesClient.setPopUpLocation(PopUpLocation.TOP_CENTER);
                mAchievementsClient = mAmazonGamesClient.getAchievementsClient();
                mLeaderboardsClient = mAmazonGamesClient.getLeaderboardsClient();
            }

            @Override
            public void onServiceNotReady(AmazonGamesStatus amazonGamesStatus) {
                Log.e("AmazonBase", "onServiceNotReady - " + amazonGamesStatus.name());
            }
        }, EnumSet.of(AmazonGamesFeature.Achievements, AmazonGamesFeature.Leaderboards));
    }

    public void onShowLeaderboard() {
        if (AmazonGamesClient.isInitialized()) {
            mLeaderboardsClient.showLeaderboardOverlay(getString(R.string.leaderboard_id));
        } else {
            initAmazonClients();
        }
    }

    public void onShowAchievement() {
        if (AmazonGamesClient.isInitialized()) {
            mAchievementsClient.showAchievementsOverlay();
        } else {
            initAmazonClients();
        }
    }

    @Override
    public void onUpdateAchievement(int i) {
        if (AmazonGamesClient.isInitialized()) {
            mAchievementsClient.updateProgress(getString(R.string.achievement_id), i);
        } else {
            initAmazonClients();
        }
    }

    @Override
    public void onUpdateLeaderBoard(int i) {
        if (AmazonGamesClient.isInitialized()) {
            mLeaderboardsClient.submitScore(getString(R.string.leaderboard_id), i);
        } else {
            initAmazonClients();
        }
    }
}
