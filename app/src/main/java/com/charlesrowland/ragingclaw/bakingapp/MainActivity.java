package com.charlesrowland.ragingclaw.bakingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.preference.PreferenceManager;
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

/*
Notes from Matthew on bugs he found:

Check your steps with no video...
If I'm in Landscape on a phone, and a video is playing I cannot see the printed steps and can't navigate to the next step.
If I rotate from landscape to portrait, the view only updates _after_ the video is done playing, and then there is no preview (just the player controls)

I'm saying from that view I have to navigate out to go to the next step

But I can NEVER see the actual printed steps if there is a vide

CharlesRowland.ProjectCoach [11:19 AM]
cause its 'full'

TheBaileyBrew.ProjectCoach [11:19 AM]
I get that.... but it defeats the purpose of having printed instructions.

CharlesRowland.ProjectCoach [11:20 AM]
i feel like if you are in landscape, you want a bigger video. without it being full like that, you cant see the video in frame, its under the fold. exoplayer doesnt have a full screen button out of the box. i couldnt get it to work. this was my solution

TheBaileyBrew.ProjectCoach [11:20 AM]
Several of the videos don't have words, so you're watching a video of mixing ingredients but never hear what they are

CharlesRowland.ProjectCoach [11:21 AM]
the other solution i guess is to use the tablet layout
 */

public class MainActivity extends AppCompatActivity {
    private RecipeService mRecipeService;
    private RecipeAdapter mRecipeAdapter;
    private ArrayList<Recipe> mRecipeList = new ArrayList<>();
    private String mRecipeJsonResult;
    private Boolean mTwoPane = false;
    private Parcelable mRecipeState;
    private RecyclerView.LayoutManager mLayoutManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


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

        sharedPreferences = getSharedPreferences(AllMyConstants.SHARED_PREFS, Context.MODE_PRIVATE);

        mLayoutManager = new LinearLayoutManager(MainActivity.this);

        if (savedInstanceState != null) {
            mRecipeList = savedInstanceState.getParcelableArrayList(AllMyConstants.RECIPE_ARRAYLIST_STATE);
            mRecipeJsonResult = savedInstanceState.getString(AllMyConstants.RECIPE_JSON_RESULT_STATE);
            attachAdapter();
            HideEmptyView();
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

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                editor = sharedPreferences.edit();
                Gson gsonIngList = new Gson();
                String json = gsonIngList.toJson(mRecipeList);

                editor.putString(AllMyConstants.RECIPE_JSON_RESULT_STATE, json);
                editor.apply();
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
        mRecipeRecyclerView.setLayoutManager(mLayoutManager);
        mRecipeAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mRecipeState = mLayoutManager.onSaveInstanceState();
        outState.putParcelable(AllMyConstants.RECIPE_RECYCLER_STATE, mRecipeState);
        outState.putString(AllMyConstants.RECIPE_JSON_RESULT_STATE, mRecipeJsonResult);

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

    // TODO: implement on resume method

    @Override
    protected void onResume() {
        super.onResume();
    }
}
