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

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            if(intent != null && intent.getAction() != null) {
                Timber.e("fart the intent action: %s", intent.getAction());
                if (intent.getAction().equals(AllMyConstants.WIDGET_INGREDIENT_ACTION) && intent.getStringExtra(AllMyConstants.WIDGET_INGREDIENTS) != null) {
                    if (intent.hasExtra(AllMyConstants.WIDGET_INGREDIENTS)) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<Ingredient>>() {
                        }.getType();

                        String mJsonResult = intent.getStringExtra(AllMyConstants.WIDGET_INGREDIENTS);
                        String recipe_name = intent.getStringExtra(AllMyConstants.RECIPE_NAME_EXTRA);
                        mIngredientList = gson.fromJson(mJsonResult, type);

                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, BakingWidgetProvider.class));

                        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget_provider);
                        views.setTextViewText(R.id.widget_title, recipe_name);

                        for (int appWidgetId : appWidgetIds) {
                            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.baking_widget_list);
                        }
                        appWidgetManager.updateAppWidget(appWidgetIds, views);
                    }

                } else if(intent.getAction().equals(AllMyConstants.WIDGET_BACKBUTTON_ACTION)) {
                    mIngredientList.clear();
                    Timber.e("fart going back");
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, BakingWidgetProvider.class));
                    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget_provider);
                    for (int appWidgetId : appWidgetIds) {
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.baking_widget_list);
                    }
                    appWidgetManager.updateAppWidget(appWidgetIds, views);
                }
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
        Timber.e("fart onUpdate");
        for (int appWidgetId : appWidgetIds) {

            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget_provider);

            // Service Intent
            Intent serviceIntent = new Intent(context, BakingWidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(R.id.baking_widget_list, serviceIntent);

            // Click Intent
            Intent onItemClickIntent = new Intent(context, BakingWidgetProvider.class);
            onItemClickIntent.setData(Uri.parse(onItemClickIntent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0, onItemClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.baking_widget_list, onClickPendingIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.baking_widget_list);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
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

