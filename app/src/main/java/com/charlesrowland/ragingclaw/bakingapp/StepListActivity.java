package com.charlesrowland.ragingclaw.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.ActionBar;
import android.view.MenuItem;

import com.charlesrowland.ragingclaw.bakingapp.adapters.StepAdapter;
import com.charlesrowland.ragingclaw.bakingapp.model.Ingredient;
import com.charlesrowland.ragingclaw.bakingapp.model.Recipe;
import com.charlesrowland.ragingclaw.bakingapp.model.Step;
import com.charlesrowland.ragingclaw.bakingapp.utils.AllMyConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * An activity representing a list of Steps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link StepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */

// TODO: make PagerAdapter and setup tabs for steps/ingredients
// TODO: video player fragment
// TODO: video player
public class StepListActivity extends AppCompatActivity {

    private ArrayList<Recipe> mRecipeArrayList;
    private List<Ingredient> mIngredientList = new ArrayList<>();
    private List<Step> mStepList = new ArrayList<>();
    private StepAdapter mStepAdapter;

    private boolean mTwoPane;

    @BindView(R.id.step_list) RecyclerView mStepsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_list);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.step_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        Intent recipeIntent = getIntent();

        if (recipeIntent != null && recipeIntent.hasExtra(AllMyConstants.RECIPE_INTENT_EXTRA)) {
            mRecipeArrayList = recipeIntent.getParcelableArrayListExtra(AllMyConstants.RECIPE_INTENT_EXTRA);
            mIngredientList = mRecipeArrayList.get(0).getIngredients();
            mStepList = mRecipeArrayList.get(0).getSteps();
            actionBar.setTitle(getString(R.string.title_step_list, mRecipeArrayList.get(0).getName()));
        }

        assert mStepsRecyclerView != null;
        setupRecyclerView(mStepsRecyclerView);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        RecyclerView.LayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(StepListActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        mStepAdapter = new StepAdapter(this, (ArrayList<Step>) mStepList, mRecipeArrayList, mTwoPane);
        recyclerView.setAdapter(mStepAdapter);
    }
}
