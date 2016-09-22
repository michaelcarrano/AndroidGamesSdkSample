package com.michaelcarrano.androidgamessdksample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.michaelcarrano.androidgamessdksample.games.Achievement;
import com.michaelcarrano.androidgamessdksample.games.LeaderBoard;

/**
 * Created by michaelcarrano on 2/3/16.
 */
public class BaseActivity extends BaseGameActivity implements LeaderBoard, Achievement {

    private static final String SHOW_GOOGLE_PLAY_GAMES = "SHOW_GOOGLE_PLAY_GAMES";
    private static final int RC_UNUSED = 5001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (mSharedPrefs.getBoolean(SHOW_GOOGLE_PLAY_GAMES, true)) {
            beginUserInitiatedSignIn();
            mSharedPrefs.edit().putBoolean(SHOW_GOOGLE_PLAY_GAMES, false).apply();
        }
    }

    public void onShowLeaderboard() {
        if (isSignedIn()) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()), RC_UNUSED);
        } else {
            reconnectClient();
        }
    }

    public void onShowAchievement() {
        if (isSignedIn()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), RC_UNUSED);
        } else {
            reconnectClient();
        }
    }

    @Override
    public void onUpdateAchievement(int i) {
        if (isSignedIn()) {
            Games.Achievements.unlock(getApiClient(), getString(R.string.achievement_id));
        } else {
            reconnectClient();
        }
    }

    @Override
    public void onUpdateLeaderBoard(int i) {
        if (isSignedIn()) {
            Games.Leaderboards.submitScore(
                    getApiClient(),
                    getString(R.string.leaderboard_id),
                    i
            );
        } else {
            reconnectClient();
        }
    }

    @Override
    public void onSignInFailed() {
        // TODO: Handle the failed sign in.
    }

    @Override
    public void onSignInSucceeded() {
        // TODO: Handle the successful sign in.
    }
}
