package com.example.android.expensetracker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.firebase.jobdispatcher.JobParameters;

/**
 * Created by Santosh on 29-03-2018.
 */

public class NotificationDispatcher extends com.firebase.jobdispatcher.JobService {
    public NotificationDispatcher() {
        super();
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 100, i, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.reminder))
                .setContentText(getString(R.string.add_expense_notify))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.expenselogo);
        notificationManager.notify(100, builder.build());
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
