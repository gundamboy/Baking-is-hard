package com.charlesrowland.ragingclaw.bakingapp;


import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.charlesrowland.ragingclaw.bakingapp.model.Recipe;
import com.charlesrowland.ragingclaw.bakingapp.model.Step;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {
    private SimpleExoPlayer mSimplePlayer;
    private DefaultBandwidthMeter mBandwidthMeter;
    private TrackSelection.Factory mVideoTrackSelectionFactory;
    private TrackSelector mTrackSelector;
    private DataSource.Factory mDataSourceFactory;
    private MediaSource mVideoSource;

    private int mStepNumber = 0;
    private Recipe mCurrentRecipe;
    private Step mCurrentStep;
    private Uri mVideoUri;
    private boolean mShoulpPlayWhenReady = true;
    private long mPlayerPosition;
    private int mWindowIndex;

    @BindView(R.id.step_description) TextView mStepDescription;
    @BindView(R.id.iv_video_placeholder) ImageView mVideoPlaceHolder;
    @BindView(R.id.player_view) SimpleExoPlayerView mPlayerView;

    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.video_fragment_layout, container, false);
        ButterKnife.bind(this, fragmentView);






//        mStepDescription.setText(mCurrentStep.getDescription());
//
//        android.app.ActionBar actionBar = getActivity().getActionBar();
//        if (actionBar != null) {
//            actionBar.setTitle(getString(R.string.title_step_detail, mCurrentRecipe.getName(), String.valueOf(mStepNumber)));
//        }



        return fragmentView;
    }

    public void initializePlayer(Uri videoUrl) {
        if (mSimplePlayer == null) {
            // Create a default TrackSelector
            mBandwidthMeter = new DefaultBandwidthMeter();
            mVideoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(mBandwidthMeter);
            mTrackSelector = new DefaultTrackSelector(mVideoTrackSelectionFactory);

            // Create the player
            mSimplePlayer = ExoPlayerFactory.newSimpleInstance(getContext(), mTrackSelector);

            // Bind the player to the view


        }
    }


}
