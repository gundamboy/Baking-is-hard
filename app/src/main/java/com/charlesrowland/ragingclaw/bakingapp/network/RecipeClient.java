package com.charlesrowland.ragingclaw.bakingapp.network;

import com.charlesrowland.ragingclaw.bakingapp.interfaces.RecipeService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipeClient {

    // this is the retrofit instantiation class
    public static final String RECIPE_API_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/";
    public final RecipeService mRecipeService;

    public RecipeClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RECIPE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mRecipeService = retrofit.create(RecipeService.class);
    }
}
