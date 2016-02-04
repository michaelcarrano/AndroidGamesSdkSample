package com.michaelcarrano.androidgamessdksample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.michaelcarrano.androidgamessdksample.games.Achievement;
import com.michaelcarrano.androidgamessdksample.games.LeaderBoard;

/**
 * Created by michaelcarrano on 2/3/16.
 */
public class BaseActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LeaderBoard, Achievement {

    private static final String SHOW_GOOGLE_PLAY_GAMES = "SHOW_GOOGLE_PLAY_GAMES";
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mSignInClicked = false;
    private boolean mAutoStartSignInFlow = true;
    private Boolean mLaunchLeaderboard;

    private GoogleApiClient mGoogleApiClient;

    private SharedPreferences mSharedPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Create the Google Api Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean show = mSharedPrefs.getBoolean(SHOW_GOOGLE_PLAY_GAMES, true);
        if (show) {
            mGoogleApiClient.connect();
            SharedPreferences.Editor editor = mSharedPrefs.edit();
            editor.putBoolean(SHOW_GOOGLE_PLAY_GAMES, false);
            editor.commit();
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private void reconnect(Boolean b) {
        mSignInClicked = true;
        mGoogleApiClient.connect();
        mLaunchLeaderboard = b;
    }

    public void onShowLeaderboard() {
        if (mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient), RC_UNUSED);
        } else {
            reconnect(true);
        }
    }

    public void onShowAchievement() {
        if (mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), RC_UNUSED);
        } else {
            reconnect(false);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("GoogleApiClient", "onConnected() called. Sign in successful!");
        if (mLaunchLeaderboard != null && mLaunchLeaderboard == true) {
            onShowLeaderboard();
        } else if (mLaunchLeaderboard != null && mLaunchLeaderboard == false) {
            onShowAchievement();
        }
        mLaunchLeaderboard = null;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("GoogleApiClient", "onConnectionSuspended() called. Trying to reconnect.");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("GoogleApiClient", "onConnectionFailed() called, result: " + connectionResult);

        if (mResolvingConnectionFailure) {
            Log.d("GoogleApiClient", "onConnectionFailed() ignoring connection failure; already resolving.");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
                    connectionResult, RC_SIGN_IN, getString(R.string.unknown_error));
        }
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (responseCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(this, requestCode, responseCode, R.string.unknown_error);
            }
        }

        if (requestCode == RC_UNUSED) {
            if (responseCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onUpdateAchievement(int i) {
        if (mGoogleApiClient.isConnected()) {
            Games.Achievements.unlock(mGoogleApiClient, getString(R.string.achievement_id));
        } else {
            reconnect(null);
        }
    }

    @Override
    public void onUpdateLeaderBoard(int i) {
        if (mGoogleApiClient.isConnected()) {
            Games.Leaderboards.submitScore(
                    mGoogleApiClient,
                    getString(R.string.leaderboard_id),
                    i
            );
        } else {
            reconnect(null);
        }
    }
}
