package com.example.mohamedabdelaziz.marketstore;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        ArrayList<Comments> commentsArrayList;
        DBHelper db = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
        commentsArrayList = db.GetComments();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        if (commentsArrayList.size() > 0) {
            views.setTextViewText(R.id.tv, commentsArrayList.get(commentsArrayList.size() - 1).name);
            views.setTextViewText(R.id.tv2, commentsArrayList.get(commentsArrayList.size() - 1).comment);
        } else {
            views.setTextViewText(R.id.tv, context.getString(R.string.no_comments));
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
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

