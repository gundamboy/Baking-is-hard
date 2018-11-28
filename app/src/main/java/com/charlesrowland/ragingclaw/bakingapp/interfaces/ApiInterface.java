package com.charlesrowland.ragingclaw.bakingapp.interfaces;

import com.charlesrowland.ragingclaw.bakingapp.model.Recipe;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("baking.json")
    Call<Recipe> getRecipes();
}
