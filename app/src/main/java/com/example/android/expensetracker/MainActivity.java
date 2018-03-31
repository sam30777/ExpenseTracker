package com.example.android.expensetracker;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.expensetracker.adapters.OverallDataAdapter;
import com.example.android.expensetracker.data.ExpenseOverallByDate;
import com.example.android.expensetracker.data.User;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;



public class MainActivity extends AppCompatActivity implements OverallDataAdapter.ListItemClickListner {

    private static String salary;
    private Dialog dialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseRefrence;
    private ChildEventListener childevenLisntener;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> arrayAdapter;
    private EditText salary_edit_text;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private OverallDataAdapter overallDataAdapter;
    private ArrayList<ExpenseOverallByDate> arrayList = new ArrayList<>();
    private  String d;
    private int  RC_SIGN_IN=1;
    private String job_tag="my_job_tag";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseRefrence = firebaseDatabase.getReference().child(firebaseUser.getUid());
                    updateUserInfo(firebaseUser);

                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setLogo(R.drawable.logo)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);

                }
            }
        };

        firebaseAuth.addAuthStateListener(authStateListener);

       final FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));

                Job myJob = dispatcher.newJobBuilder()
                        .setService(NotificationDispatcher.class)
                        .setLifetime(Lifetime.FOREVER)
                        .setRecurring(true)
                        .setTag(job_tag)
                        .setTrigger(Trigger.executionWindow(10,15))
                        .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                        .setReplaceCurrent(false)
                        .build();
                dispatcher.mustSchedule(myJob);
                dispatcher.cancel(job_tag);
            }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void readdata() {
        final DatabaseReference userIdRefrence = firebaseDatabase.getReference();
        childevenLisntener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
            TextView textView=findViewById(R.id.salary_value);
            textView.setText(user.getSalary_final());
            TextView textView1=findViewById(R.id.savings_value);
            textView1.setText(user.getSavings_final());
            TextView textView2=findViewById(R.id.expene_value);
            textView2.setText(user.getExpense_final());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        userIdRefrence.addChildEventListener(childevenLisntener);

    }

    private void launchSalaryUpdateDialog() {
        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup);
        dialog.show();
        spinner = dialog.findViewById(R.id.country_spinner);
        arrayAdapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.currency_array, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        salary_edit_text = dialog.findViewById(R.id.edit_text_salary);
        Button button = dialog.findViewById(R.id.done_updating);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String country=spinner.getSelectedItem().toString();
                salary = salary_edit_text.getText().toString();
                User user = new User(salary, salary, "0",country);
                databaseRefrence.setValue(user);
                dialog.dismiss();
                readdata();
                updateRecycler();

            }
        });
    }

    private void updateUi() {

        databaseRefrence.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("salary_final")) {
                    readdata();
                    updateRecycler();
                } else {
                    launchSalaryUpdateDialog();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void updateUserInfo(FirebaseUser fbuser) {
        NavigationView navigationView =findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        TextView name = header.findViewById(R.id.user_name);
        TextView email = header.findViewById(R.id.user_mail_id);
        ImageView imageView = header.findViewById(R.id.circular_user_image);
        name.setText(fbuser.getDisplayName());
        email.setText(fbuser.getEmail());
        Uri uri = Uri.parse(fbuser.getPhotoUrl().toString());
        Picasso.with(MainActivity.this).load(uri).into(imageView);
        updateUi();
    }

    private void updateRecycler() {

        Date date = new Date();
        final String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
        final String day = (String) DateFormat.format("dd", date); // 20
        final String monthString = (String) DateFormat.format("MMM", date); // Jun
        d = dayOfTheWeek + "-" + day + "-" + monthString;
        final DatabaseReference dataref = databaseRefrence.child("expense_by_date_list");
        final ChildEventListener child = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ExpenseOverallByDate expenseOverallByDate = dataSnapshot.getValue(ExpenseOverallByDate.class);
                arrayList.add(0,expenseOverallByDate);
                RecyclerView recyclerview = findViewById(R.id.main_recycler);
                recyclerview.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
                recyclerview.setHasFixedSize(true);
                overallDataAdapter = new OverallDataAdapter(arrayList, MainActivity.this,MainActivity.this);
                recyclerview.setAdapter(overallDataAdapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
              overallDataAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        dataref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(d)) {
                    dataref.addChildEventListener(child);

                } else {
                    ExpenseOverallByDate expenseOverallByDate = new ExpenseOverallByDate(day, monthString, dayOfTheWeek, "0");
                    dataref.child(d).setValue(expenseOverallByDate);
                    updateRecycler();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onItemClicked(int listItemIndex) {
        ExpenseOverallByDate exp=arrayList.get(listItemIndex);
        String dayOfTheWeek=exp.getDayName();
        String day=exp.getDay();
        String monthString=exp.getMonth();
        String date=dayOfTheWeek + "-" + day + "-" + monthString;
        Intent intent =new Intent(MainActivity.this,ByTimeExpense.class);
        intent.putExtra("date",date);
        startActivity(intent);
    }

}
