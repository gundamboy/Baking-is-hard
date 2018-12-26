package com.charlesrowland.ragingclaw.bakingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.charlesrowland.ragingclaw.bakingapp.model.Recipe;
import com.charlesrowland.ragingclaw.bakingapp.model.Step;
import com.charlesrowland.ragingclaw.bakingapp.utils.AllMyConstants;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * An activity representing a single Step detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link StepActivity}.
 */
public class StepDetailActivity extends AppCompatActivity {
    private ArrayList<Recipe> mRecipeArrayList;
    private ArrayList<Step> mStepList;
    private int mStepNumber = 0;
    private Recipe mCurrentRecipe;
    private Step mCurrentStep;
    private Uri mVideoUri;
    private boolean mShouldPlayWhenReady = true;
    private long mPlayerPosition;
    private int mWindowIndex;

    private String mVideoThumbnail;
    private Bitmap mVideoThumbnailImage;

    private SimpleExoPlayer mSimplePlayer;
    private DefaultBandwidthMeter mBandwidthMeter;
    private TrackSelection.Factory mVideoTrackSelectionFactory;
    private TrackSelector mTrackSelector;
    private DataSource.Factory mDataSourceFactory;
    private MediaSource mVideoSource;

    @BindView(R.id.step_description) TextView mStepDescription;
    @BindView(R.id.iv_video_placeholder) ImageView mVideoPlaceHolder;
    @BindView(R.id.player_view) PlayerView mPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        Timber.v("we are here");

        ButterKnife.bind(this);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent recipeIntent = getIntent();

        if (recipeIntent.hasExtra(AllMyConstants.RECIPE_ARRAYLIST_STATE)) {
            mRecipeArrayList = recipeIntent.getParcelableArrayListExtra(AllMyConstants.RECIPE_ARRAYLIST_STATE);
            mCurrentRecipe = mRecipeArrayList.get(0);
            mStepList = (ArrayList<Step>) mCurrentRecipe.getSteps();
            mStepNumber = recipeIntent.getIntExtra(AllMyConstants.ARG_ITEM_ID, 0);
            mCurrentStep = mStepList.get(mStepNumber);
            mVideoUri = Uri.parse(mCurrentStep.getVideoURL());
            mStepDescription.setText(mCurrentStep.getDescription());

            Timber.i("fart: video url: %s", mCurrentStep.getVideoURL());
            Timber.i("fart: description: %s", mCurrentStep.getDescription());


            if (actionBar != null) {
                actionBar.setTitle(getString(R.string.title_step_detail, mCurrentRecipe.getName(), String.valueOf(mStepNumber)));
            }

        }

        if (savedInstanceState != null) {
            mCurrentStep = savedInstanceState.getParcelable(AllMyConstants.STEP_SINGLE);
            mShouldPlayWhenReady = savedInstanceState.getBoolean(AllMyConstants.STEP_PLAY_WHEN_READY);
            mPlayerPosition = savedInstanceState.getLong(AllMyConstants.STEP_VIDEO_POSITION);
            mWindowIndex = savedInstanceState.getInt(AllMyConstants.STEP_PLAY_WINDOW_INDEX);
            mVideoUri = Uri.parse(savedInstanceState.getString(AllMyConstants.STEP_URI));
        } else {

        }

        Timber.v("onCreate mPlayerPosition: %s", mPlayerPosition);
        initializePlayer(mVideoUri);
    }

    public void initializePlayer(Uri videoUrl) {
        Timber.i("fart: initializePlayer");
        if (mSimplePlayer == null) {
            Timber.i("fart: mSimplePlayer is null");
            // Create a default TrackSelector
            mBandwidthMeter = new DefaultBandwidthMeter();
            mVideoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(mBandwidthMeter);
            mTrackSelector = new DefaultTrackSelector(mVideoTrackSelectionFactory);

            // Create the player
            mSimplePlayer = ExoPlayerFactory.newSimpleInstance(this, mTrackSelector);

            // Bind the player to the view
            mPlayerView.setPlayer(mSimplePlayer);
            mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);

            // Produce DataSource instance through which data is loaded
            mDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)), mBandwidthMeter);

            // MediaSource - the video being played
            mVideoSource = new ExtractorMediaSource.Factory(mDataSourceFactory).createMediaSource(mVideoUri);

            if (mPlayerPosition != C.TIME_UNSET) {
                mSimplePlayer.seekTo(mPlayerPosition);
            }

            // Prepare the player
            mSimplePlayer.prepare(mVideoSource);

        }
    }

    public void releasePlayer() {
        if (mSimplePlayer != null) {
            mSimplePlayer.stop();
            mSimplePlayer.release();
            mSimplePlayer = null;
            mDataSourceFactory = null;
            mVideoSource = null;
            mVideoTrackSelectionFactory = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer(mVideoUri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || mSimplePlayer == null) {
            initializePlayer(mVideoUri);
        }
        if(mSimplePlayer != null){
            Timber.v("on resume mPlayerPosition: %s", mPlayerPosition);
            if(mPlayerPosition > 0) {
                mSimplePlayer.setPlayWhenReady(mShouldPlayWhenReady);
                mPlayerView.hideController();
            }

            mSimplePlayer.seekTo(mPlayerPosition);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mSimplePlayer != null){
            updateStartPosition();
            if (Util.SDK_INT <= 23) {
                releasePlayer();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mSimplePlayer != null){
            updateStartPosition();
            if (Util.SDK_INT > 23) {
                releasePlayer();
            }
        }
    }

    private void updateStartPosition() {
        if (mSimplePlayer != null) {
            mShouldPlayWhenReady = mSimplePlayer.getPlayWhenReady();
            mWindowIndex = mSimplePlayer.getCurrentWindowIndex();
            mPlayerPosition = mSimplePlayer.getCurrentPosition();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        updateStartPosition();
        outState.putString(AllMyConstants.STEP_URI, mCurrentStep.getVideoURL());
        outState.putParcelable(AllMyConstants.STEP_SINGLE, mCurrentStep);
        outState.putLong(AllMyConstants.STEP_VIDEO_POSITION, mPlayerPosition);
        outState.putBoolean(AllMyConstants.STEP_PLAY_WHEN_READY, mShouldPlayWhenReady);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //

            Intent intent = new Intent(this, StepDetailActivity.class);
            intent.putParcelableArrayListExtra(AllMyConstants.RECIPE_INTENT_EXTRA, mRecipeArrayList);
            navigateUpTo(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
