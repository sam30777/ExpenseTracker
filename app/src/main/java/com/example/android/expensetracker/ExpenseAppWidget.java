package com.example.android.expensetracker;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.expensetracker.data.ExpenseParticularDay;
import com.example.android.expensetracker.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class ExpenseAppWidget extends AppWidgetProvider {
       private ExpenseParticularDay expenseParticularDay;
    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);



          final   RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.expense_app_widget);


            Date date = new Date();
            final String dayOfTheWeek = (String) DateFormat.format("EEEE", date);
            final String day = (String) DateFormat.format("dd", date);
            final String monthString = (String) DateFormat.format("MMM", date);
            String  d = dayOfTheWeek + "-" + day + "-" + monthString;

            DatabaseReference recentDataRefrence=FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child("expense_by_date_list").child(d);
            recentDataRefrence.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                   expenseParticularDay=dataSnapshot.getValue(ExpenseParticularDay.class);
                    views.setTextViewText(R.id.expense_amount_text,expenseParticularDay.getExpense());
                    views.setImageViewResource(R.id.expense_type_image,expenseParticularDay.getExpense_type_image_resource_id());
                    views.setTextViewText(R.id.expense_type_text_field,expenseParticularDay.getEspense_type());
                    views.setTextViewText(R.id.payment_time,expenseParticularDay.getExpense_time());
                    views.setTextViewText(R.id.payment_method_per_time,expenseParticularDay.getPayment_method());

                   Toast.makeText(context,expenseParticularDay.getEspense_type(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            views.setOnClickPendingIntent(R.id.add_expense_widget, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


}

