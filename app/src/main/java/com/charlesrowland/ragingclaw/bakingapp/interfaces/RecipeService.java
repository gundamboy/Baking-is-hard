package com.charlesrowland.ragingclaw.bakingapp.interfaces;

import com.charlesrowland.ragingclaw.bakingapp.model.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeService {

    @GET("baking.json")
    Call<ArrayList<Recipe>> getRecipes();
}
