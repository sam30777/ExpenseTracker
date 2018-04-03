package com.example.android.expensetracker;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.android.expensetracker.data.ExpenseParticularDay;
import com.example.android.expensetracker.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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


    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (final int appWidgetId : appWidgetIds) {

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                final DatabaseReference databseRef = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid());
                databseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.expense_app_widget);
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            remoteViews.setViewVisibility(R.id.loginWarning,View.GONE);
                            remoteViews.setTextViewText(R.id.salary_value_widget, user.getSalary_final());
                            remoteViews.setTextViewText(R.id.savings_value_widget, user.getSavings_final());
                            remoteViews.setTextViewText(R.id.expene_value_widget, user.getExpense_final());
                            remoteViews.setViewVisibility(R.id.noExpenseWidget,View.GONE);
                            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Intent intent = new Intent(context, MainActivity.class);
                final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                Date date = new Date();
                final String dayOfTheWeek = (String) DateFormat.format(context.getString(R.string.day_format), date);
                final String day = (String) DateFormat.format(context.getString(R.string.day), date);
                final String monthString = (String) DateFormat.format(context.getString(R.string.mmm), date);
                final String d = dayOfTheWeek + "-" + day + "-" + monthString;

                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid()).child(context.getString(R.string.expense_bydate_key)).child(d);

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(context.getString(R.string.recent_key))) {
                            final DatabaseReference recentRef = databaseReference.child(context.getString(R.string.recent_key));
                            recentRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot data) {
                                    ExpenseParticularDay expenseParticularDay = data.getValue(ExpenseParticularDay.class);
                                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.expense_app_widget);
                                    if (expenseParticularDay != null) {
                                        remoteViews.setOnClickPendingIntent(R.id.add_expense_widget, pendingIntent);
                                        remoteViews.setTextViewText(R.id.expense_type_text_field, expenseParticularDay.getEspense_type());
                                        remoteViews.setImageViewResource(R.id.expense_type_image, expenseParticularDay.getExpense_type_image_resource_id());
                                        remoteViews.setTextViewText(R.id.expense_amount_text, expenseParticularDay.getExpense());
                                        remoteViews.setTextViewText(R.id.payment_method_per_time, expenseParticularDay.getPayment_method());
                                        remoteViews.setTextViewText(R.id.payment_time, expenseParticularDay.getExpense_time());
                                        remoteViews.setViewVisibility(R.id.tempRelative, View.VISIBLE);
                                        remoteViews.setViewVisibility(R.id.noExpenseWidget, View.INVISIBLE);


                                    } else {
                                        remoteViews.setViewVisibility(R.id.tempRelative, View.GONE);
                                        remoteViews.setTextViewText(R.id.noExpenseWidget,context.getString(R.string.no_Expense_warning));
                                        remoteViews.setViewVisibility(R.id.noExpenseWidget, View.VISIBLE);

                                        }
                                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews);


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.expense_app_widget);
                            remoteViews.setViewVisibility(R.id.tempRelative,View.GONE);
                            remoteViews.setTextViewText(R.id.noExpenseWidget,context.getString(R.string.no_Expense_warning));
                            remoteViews.setViewVisibility(R.id.noExpenseWidget, View.VISIBLE);
                            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }else{
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.expense_app_widget);
                remoteViews.setViewVisibility(R.id.noExpenseWidget, View.GONE);
                remoteViews.setViewVisibility(R.id.recent_Text,View.GONE);
                remoteViews.setViewVisibility(R.id.loginWarning,View.VISIBLE);
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }


        }
    }
}




