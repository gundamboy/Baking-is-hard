package com.charlesrowland.ragingclaw.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
    private Context mContent;
    private Intent mIntent;
    private ArrayList<Recipe> mRecipeList;
    private List<Ingredient> mIngredientList;
    private String mJsonResult;

    public BakingWidgetAdapter(Context mContent, Intent mIntent) {
        this.mContent = mContent;
        this.mIntent = mIntent;
        //this.mRecipeList = getRecipeList();
    }

    private ArrayList<Recipe> getRecipeList() {
        mRecipeList = null;
        SharedPreferences sharedPreferences;
        if ((sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContent)) != null) {
            mJsonResult = sharedPreferences.getString(AllMyConstants.RECIPE_JSON_STATE, "No Data");
            Timber.v("fart mJsonResult: %s", "");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Recipe>>() {}.getType();
            //mRecipeList = gson.fromJson(mJsonResult, type);


        }
        return mRecipeList;
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
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(mContent.getPackageName(), R.layout.widget_list_item);

        Timber.v("we got this far");
        //remoteViews.setTextViewText(R.id.name, mRecipeList.get(position).getName());

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
