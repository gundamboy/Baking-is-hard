package com.charlesrowland.ragingclaw.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.charlesrowland.ragingclaw.bakingapp.R;
import com.charlesrowland.ragingclaw.bakingapp.model.Ingredient;
import com.charlesrowland.ragingclaw.bakingapp.model.Recipe;
import com.charlesrowland.ragingclaw.bakingapp.utils.AllMyConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class BakingWidgetAdapter implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Intent mIntent;
    private ArrayList<Recipe> mRecipeList;
    private String mJsonResult;
    private String mIngredientsJson;
    private List<Ingredient> ingredientList;

    public BakingWidgetAdapter(Context mContext, Intent mIntent) {
        this.mContext = mContext;
        this.mIntent = mIntent;
        this.mRecipeList = getRecipeList();
    }

    private ArrayList<Recipe> getRecipeList() {

        if (mIntent.hasExtra("backbutton")) {
            Timber.e("fart back button came through to adapter");
        } else {
            Timber.e("fart back button did not through to adapter");
        }


        mRecipeList = null;
        SharedPreferences sharedPreferences;
        if ((sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)) != null) {
            mJsonResult = sharedPreferences.getString(AllMyConstants.RECIPE_JSON_RESULT_STATE, "No Data");

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Recipe>>() {}.getType();
            mRecipeList = gson.fromJson(mJsonResult, type);

        }
        return mRecipeList;
    }

    private String ingredientsToJson(List<Ingredient> ingredients) {
        Gson gsonIngList = new Gson();
        String json = gsonIngList.toJson(ingredients);
        return json;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        mRecipeList = getRecipeList();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (BakingWidgetProvider.mIngredientList != null) {
            return BakingWidgetProvider.mIngredientList.size();
        } else if (mRecipeList == null) {
            return 0;
        } else {
            return mRecipeList.size();
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

        if (BakingWidgetProvider.mIngredientList != null && BakingWidgetProvider.mIngredientList.size() > 0) {
//            remoteViews = null;
//            remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredients_list_item);

            String thisIngredient = BakingWidgetProvider.mIngredientList.get(position).getIngredient();
            String quantity = String.valueOf(BakingWidgetProvider.mIngredientList.get(position).getQuantity());
            String measure = String.valueOf(BakingWidgetProvider.mIngredientList.get(position).getMeasure());

            remoteViews.setTextViewText(R.id.name, thisIngredient);
            remoteViews.setTextViewText(R.id.servings, "Amount: " + " " + String.valueOf(quantity) + " " + measure);
            remoteViews.setViewVisibility(R.id.totalIngredients, View.GONE);

        } else {
            ingredientList = mRecipeList.get(position).getIngredients();

            String servingsText = mContext.getResources().getString(R.string.servings_text) + " " + String.valueOf(mRecipeList.get(position).getServings());
            String ingredientsText = mContext.getResources().getString(R.string.ingredients_text) + " " + String.valueOf(ingredientList.size());

            remoteViews.setTextViewText(R.id.name, mRecipeList.get(position).getName());
            remoteViews.setTextViewText(R.id.servings, servingsText);
            remoteViews.setTextViewText(R.id.totalIngredients, ingredientsText);

            mIngredientsJson = ingredientsToJson(ingredientList);

            Intent ingredientsIntent = new Intent();
            ingredientsIntent.setAction(AllMyConstants.WIDGET_INGREDIENT_ACTION);
            //ingredientsIntent.putParcelableArrayListExtra(AllMyConstants.WIDGET_INGREDIENTS, (ArrayList<? extends Parcelable>) ingredientList);
            ingredientsIntent.putExtra(AllMyConstants.RECIPE_NAME_EXTRA, mRecipeList.get(position).getName());
            ingredientsIntent.putExtra(AllMyConstants.WIDGET_INGREDIENTS, mIngredientsJson);

            remoteViews.setOnClickFillInIntent(R.id.name, ingredientsIntent);
        }
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
