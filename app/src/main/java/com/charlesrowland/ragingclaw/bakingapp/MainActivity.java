package com.charlesrowland.ragingclaw.bakingapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.charlesrowland.ragingclaw.bakingapp.adapters.RecipeAdapter;
import com.charlesrowland.ragingclaw.bakingapp.interfaces.RecipeService;
import com.charlesrowland.ragingclaw.bakingapp.model.Recipe;
import com.charlesrowland.ragingclaw.bakingapp.network.RecipeClient;
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

    @BindView(R.id.recipeRecyclerView) RecyclerView mRecipeRecyclerView;
    @BindView(R.id.emptyViews) View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // only log if this is not a production build
        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }

        if (savedInstanceState != null) {
            // TODO: add save/restore instancestate methods and implement them
        } else {
            if (NetworkUtils.isOnline(this)) {
                mRecipeService = new RecipeClient().mRecipeService;
                new FetchRecipesAsync().execute();
            } else {
                showEmptyView();
            }
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
                mRecipeAdapter = new RecipeAdapter(MainActivity.this, mRecipeList, mRecipeJsonResult);

                RecyclerView.LayoutManager mLayoutManager;
                mLayoutManager = new LinearLayoutManager(MainActivity.this);
                mRecipeRecyclerView.setLayoutManager(mLayoutManager);
                mRecipeRecyclerView.setAdapter(mRecipeAdapter);

            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Timber.e("OH CRAP! ERROR: %s", t.toString());
            }
        });
    }
}
