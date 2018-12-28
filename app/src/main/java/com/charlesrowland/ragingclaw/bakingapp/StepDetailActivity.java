package com.charlesrowland.ragingclaw.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.charlesrowland.ragingclaw.bakingapp.model.Recipe;
import com.charlesrowland.ragingclaw.bakingapp.utils.AllMyConstants;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import timber.log.Timber;

/**
 * An activity representing a single Step detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link StepActivity}.
 */
public class StepDetailActivity extends AppCompatActivity {
    private ArrayList<Recipe> mRecipeArrayList;
    private int mStepNumber = 0;
    private Recipe mCurrentRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            mRecipeArrayList = savedInstanceState.getParcelableArrayList(AllMyConstants.RECIPE_ARRAYLIST_STATE);
            mCurrentRecipe = savedInstanceState.getParcelable(AllMyConstants.CURRENT_RECIPE);
            mStepNumber = savedInstanceState.getInt(AllMyConstants.STEP_NUMBER);
        } else {
            Intent recipeIntent = getIntent();

            if (recipeIntent.hasExtra(AllMyConstants.RECIPE_ARRAYLIST_STATE)) {
                mRecipeArrayList = recipeIntent.getParcelableArrayListExtra(AllMyConstants.RECIPE_ARRAYLIST_STATE);
                mCurrentRecipe = mRecipeArrayList.get(0);
                mStepNumber = recipeIntent.getIntExtra(AllMyConstants.STEP_NUMBER, 0);
                Bundle bundle = new Bundle();
                bundle.putInt(AllMyConstants.STEP_NUMBER, mStepNumber);
                bundle.putParcelableArrayList(AllMyConstants.RECIPE_ARRAYLIST_STATE, mRecipeArrayList);

                final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                VideoFragment videoFragment = new VideoFragment();
                videoFragment.setArguments(bundle);
                transaction.add(R.id.step_detail_container, videoFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }

        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.title_step_detail, mCurrentRecipe.getName(), String.valueOf(mStepNumber)));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(AllMyConstants.RECIPE_ARRAYLIST_STATE, mRecipeArrayList);
        outState.putParcelable(AllMyConstants.CURRENT_RECIPE, mCurrentRecipe);;
        outState.putInt(AllMyConstants.STEP_NUMBER, mStepNumber);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            goBackToSteps();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBackToSteps();
    }

    private void goBackToSteps() {
        Intent intent = new Intent(this, StepDetailActivity.class);
        intent.putParcelableArrayListExtra(AllMyConstants.RECIPE_INTENT_EXTRA, mRecipeArrayList);
        navigateUpTo(intent);
    }
}
