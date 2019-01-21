package com.charlesrowland.ragingclaw.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.charlesrowland.ragingclaw.bakingapp.adapters.IngredientAdapter;
import com.charlesrowland.ragingclaw.bakingapp.model.Ingredient;
import com.charlesrowland.ragingclaw.bakingapp.model.Recipe;
import com.charlesrowland.ragingclaw.bakingapp.utils.AllMyConstants;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsActivity extends AppCompatActivity {
    private ArrayList<Recipe> mRecipeArrayList;
    private List<Ingredient> mIngredientList = new ArrayList<>();
    private IngredientAdapter mIngredientAdapter;

    @BindView(R.id.ingredient_list) RecyclerView mIngredientRecyclerView;
    @BindView(R.id.start_baking_button) Button mStartBakingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient_list);

        ButterKnife.bind(this);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent ingredientIntent = getIntent();

        if (ingredientIntent != null && ingredientIntent.hasExtra(AllMyConstants.RECIPE_INTENT_EXTRA)) {
            mRecipeArrayList = ingredientIntent.getParcelableArrayListExtra(AllMyConstants.RECIPE_INTENT_EXTRA);
            mIngredientList = mRecipeArrayList.get(0).getIngredients();
            actionBar.setTitle(getString(R.string.title_ingredients_list, mRecipeArrayList.get(0).getName()));
        }

        assert mIngredientRecyclerView != null;
        setupRecyclerView(mIngredientRecyclerView);

        mStartBakingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), StepActivity.class);
                intent.putParcelableArrayListExtra(AllMyConstants.RECIPE_INTENT_EXTRA, mRecipeArrayList);
                startActivity(intent);
            }
        });
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
        mLayoutManager = new LinearLayoutManager(IngredientsActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        mIngredientAdapter = new IngredientAdapter(this, (ArrayList<Ingredient>) mIngredientList);
        recyclerView.setAdapter(mIngredientAdapter);
    }
}
