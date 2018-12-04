package com.charlesrowland.ragingclaw.bakingapp;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.View;

import com.charlesrowland.ragingclaw.bakingapp.adapters.RecipeAdapter;
import com.charlesrowland.ragingclaw.bakingapp.interfaces.RecipeService;
import com.charlesrowland.ragingclaw.bakingapp.model.Recipe;
import com.charlesrowland.ragingclaw.bakingapp.network.RecipeClient;
import com.charlesrowland.ragingclaw.bakingapp.utils.AllMyConstants;
import com.charlesrowland.ragingclaw.bakingapp.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private RecipeService mRecipeService;
    private RecipeAdapter mRecipeAdapter;
    private ArrayList<Recipe> mRecipeList = new ArrayList<>();
    private String mRecipeJsonResult;
    private Boolean mTwoPane;
    private Parcelable mRecipeState;
    private RecyclerView.LayoutManager mLayoutManager;

    @BindView(R.id.recipeRecyclerView) RecyclerView mRecipeRecyclerView;
    @BindView(R.id.emptyViews) View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.recipes_header_text));
        ButterKnife.bind(this);

        // only log if this is not a production build
        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }

        if (!NetworkUtils.isOnline(this)) {
            showEmptyView();
            return;
        }

        if (findViewById(R.id.recipeListImageView) != null) {
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        if (savedInstanceState != null) {
            mRecipeList = savedInstanceState.getParcelableArrayList(AllMyConstants.RECIPE_ARRAYLIST_STATE);
            mRecipeJsonResult = savedInstanceState.getString(AllMyConstants.RECIPE_JSON_RESULT_STATE);
            attachAdapter();
        } else {
            mRecipeService = new RecipeClient().mRecipeService;
            new FetchRecipesAsync().execute();
        }

    }

    private void showEmptyView() {
        // hide the RecyclerView
        mRecipeRecyclerView.setVisibility(View.GONE);

        // show the emptyView
        mEmptyView.setVisibility(View.VISIBLE);
    }

    private void HideEmptyView() {
        // show the RecyclerView
        mRecipeRecyclerView.setVisibility(View.VISIBLE);

        // hide the emptyView
        mEmptyView.setVisibility(View.GONE);
    }

    // an async task to fetch the food
    private class FetchRecipesAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            fetchRecipes();
            return null;
        }
    }

    private void fetchRecipes() {
        Call<ArrayList<Recipe>> call = mRecipeService.getRecipes();

        call.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                mRecipeList = response.body();
                mRecipeJsonResult = new Gson().toJson(response.body());
                mRecipeAdapter = new RecipeAdapter(MainActivity.this, mRecipeList, mRecipeJsonResult, mTwoPane);
                mLayoutManager = new LinearLayoutManager(MainActivity.this);
                mRecipeRecyclerView.setLayoutManager(mLayoutManager);
                mRecipeRecyclerView.setHasFixedSize(true);
                mRecipeRecyclerView.setAdapter(mRecipeAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Timber.e("OH CRAP! ERROR: %s", t.toString());
            }
        });
    }

    private void attachAdapter() {
        mRecipeAdapter = new RecipeAdapter(MainActivity.this, mRecipeList, mRecipeJsonResult, mTwoPane);
        mRecipeRecyclerView.setAdapter(mRecipeAdapter);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mRecipeState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(AllMyConstants.RECIPE_RECYCLER_STATE, mRecipeState);
        outState.putString(AllMyConstants.RECIPE_JSON_RESULT_STATE, mRecipeJsonResult);;

        if (mRecipeList != null) {
            outState.putParcelableArrayList(AllMyConstants.RECIPE_ARRAYLIST_STATE, new ArrayList<>(mRecipeList));
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mRecipeState = savedInstanceState.getParcelable(AllMyConstants.RECIPE_RECYCLER_STATE);
        mRecipeJsonResult = savedInstanceState.getString(AllMyConstants.RECIPE_JSON_RESULT_STATE);

        if (savedInstanceState.getParcelableArrayList(AllMyConstants.RECIPE_ARRAYLIST_STATE) != null) {
            mRecipeList = savedInstanceState.getParcelableArrayList(AllMyConstants.RECIPE_ARRAYLIST_STATE);
        }
    }
}
