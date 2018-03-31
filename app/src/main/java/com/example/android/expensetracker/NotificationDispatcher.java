package com.example.android.expensetracker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;

/**
 * Created by Santosh on 29-03-2018.
 */

public class NotificationDispatcher  extends com.firebase.jobdispatcher.JobService{
    public NotificationDispatcher() {
        super();
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Toast.makeText(getApplicationContext(),"Job sarted",Toast.LENGTH_SHORT).show();
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Intent i=new Intent(getApplicationContext(),ByTimeExpense.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),100,i,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("Hey Keep Updating Expense!")
                .setContentText("Keep updating expense to keep track of your money")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notification);
        notificationManager.notify(100,builder.build());
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
