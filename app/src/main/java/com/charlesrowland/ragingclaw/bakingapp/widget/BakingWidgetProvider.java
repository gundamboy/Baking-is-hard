package com.charlesrowland.ragingclaw.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.charlesrowland.ragingclaw.bakingapp.IngredientsActivity;
import com.charlesrowland.ragingclaw.bakingapp.R;
import com.charlesrowland.ragingclaw.bakingapp.model.Ingredient;
import com.charlesrowland.ragingclaw.bakingapp.model.Recipe;
import com.charlesrowland.ragingclaw.bakingapp.utils.AllMyConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class BakingWidgetProvider extends AppWidgetProvider {
    public static List<Ingredient> mIngredientList;
    public static ImageView backbutton;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget_provider);


        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String mJsonResult = sharedPreferences.getString(AllMyConstants.RECIPE_JSON_RESULT_STATE, "No Data");

        // Service Intent
        Intent serviceIntent = new Intent(context, BakingWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.putExtra(AllMyConstants.RECIPE_JSON_RESULT_STATE, mJsonResult);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.baking_widget_list, serviceIntent);

        // Application Intent
        Intent appIntent = new Intent(context, IngredientsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, appIntent, 0);
        views.setPendingIntentTemplate(R.id.baking_widget_list, pendingIntent);
        views.setEmptyView(R.id.baking_widget_list, R.id.empty_view);

        // Click Intent
        Intent onItemClickIntent = new Intent(context, BakingWidgetProvider.class);
        onItemClickIntent.setData(Uri.parse(onItemClickIntent.toUri(Intent.URI_INTENT_SCHEME)));

        PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0, onItemClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.baking_widget_list, onClickPendingIntent);

        Intent onBackButtonPressedIntent = new Intent(context, BakingWidgetProvider.class);
        onBackButtonPressedIntent.setAction("backAction");
        onBackButtonPressedIntent.putExtra("backbutton", "backbutton");
        PendingIntent onBackClickPendingIntent = PendingIntent.getBroadcast(context, 0, onBackButtonPressedIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.back_button, onBackClickPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.v("fart onReceive");

        try {

            if(intent != null && intent.getAction() != null) {

                if (intent.hasExtra("backbutton")) {
                    Timber.e("fart in the provider: back button was pressed");
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, BakingWidgetProvider.class));

                    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget_provider);
                    views.setViewVisibility(R.id.back_button, View.VISIBLE);
                    views.setTextViewText(R.id.widget_title, context.getResources().getString(R.string.appwidget_text));

                    for (int appWidgetId : appWidgetIds) {
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.baking_widget_list);
                        appWidgetManager.updateAppWidget(appWidgetId, views);
                    }

                    Intent reloadIntent = new Intent();
                    views.setOnClickFillInIntent(R.id.back_button, reloadIntent);

                    //mIngredientList.clear();
                } else {

                    if (intent.hasExtra(AllMyConstants.WIDGET_INGREDIENTS)) {
                        Timber.v("fart intent.hasExtra(AllMyConstants.WIDGET_INGREDIENTS)");
                    } else {
                        Timber.v("fart intent is missing the extra");
                    }

                    if (intent.getAction().equals(AllMyConstants.WIDGET_INGREDIENT_ACTION) && intent.getStringExtra(AllMyConstants.WIDGET_INGREDIENTS) != null) {
                        Timber.v("fart intent has action and the extra is not null");
                        if (intent.hasExtra(AllMyConstants.WIDGET_INGREDIENTS)) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<List<Ingredient>>() {
                            }.getType();

                            String mJsonResult = intent.getStringExtra(AllMyConstants.WIDGET_INGREDIENTS);
                            String recipe_name = intent.getStringExtra(AllMyConstants.RECIPE_NAME_EXTRA);
                            mIngredientList = gson.fromJson(mJsonResult, type);
                            Timber.v("fart size test: %s", mIngredientList.size());

                            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, BakingWidgetProvider.class));

                            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget_provider);
                            views.setViewVisibility(R.id.back_button, View.VISIBLE);
                            views.setTextViewText(R.id.widget_title, recipe_name);


                            for (int appWidgetId : appWidgetIds) {
                                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.baking_widget_list);
                                appWidgetManager.updateAppWidget(appWidgetId, views);
                            }
                        }


                    } else {
                        Timber.v("fart intent is null or does not have action!");
                        mIngredientList.clear();
                    }
                }
            } else {
                Timber.v("fart onReceive: intent is null");
            }

        } catch(NullPointerException e) {
            e.printStackTrace();
            Timber.v("fart something failed onReceive");
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

