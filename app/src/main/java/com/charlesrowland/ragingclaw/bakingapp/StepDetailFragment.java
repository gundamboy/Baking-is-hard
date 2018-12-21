package com.charlesrowland.ragingclaw.bakingapp;

import android.app.Activity;

import com.charlesrowland.ragingclaw.bakingapp.model.Recipe;
import com.charlesrowland.ragingclaw.bakingapp.model.Step;
import com.charlesrowland.ragingclaw.bakingapp.utils.AllMyConstants;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import android.os.Bundle;

import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import timber.log.Timber;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.charlesrowland.ragingclaw.bakingapp.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single Step detail screen.
 * This fragment is either contained in a {@link StepListActivity}
 * in two-pane mode (on tablets) or a {@link StepDetailActivity}
 * on handsets.
 */
public class StepDetailFragment extends Fragment {

    private ArrayList<Recipe> mRecipeArrayList;
    private List<Step> mStepList = new ArrayList<>();
    private String mDescription;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.v("in step details fragment");

        if (getArguments().containsKey(AllMyConstants.ARG_ITEM_ID) && getArguments().containsKey(AllMyConstants.RECIPE_ARRAYLIST_STATE)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mRecipeArrayList = getArguments().getParcelableArrayList(AllMyConstants.RECIPE_ARRAYLIST_STATE);
            String recipe_name = mRecipeArrayList.get(0).getName();
            mStepList = mRecipeArrayList.get(0).getSteps();
            String step_number = getArguments().getString(AllMyConstants.ARG_ITEM_ID);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(getString(R.string.title_step_detail, recipe_name, step_number));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.step_detail, container, false);
        // TODO: need to create the step_detail.xml layout
        // TODO add video player fragment
        // TODO: create video player


        return rootView;
    }
}
