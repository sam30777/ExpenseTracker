package com.example.android.expensetracker;


import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
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
    private String d;
    private int RC_SIGN_IN = 1;
    private String job_tag = "my_job_tag";
    private SharedPreferences settingsPref;
    private InterstitialAd mInterstitialAd;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (arrayList.size() > 0) {
            arrayList.clear();
        }
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
        if (arrayList.size() > 0) {
            arrayList.clear();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, getString(R.string.test_add_key));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.test_add_key));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        drawerLayout = findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.logout) {
                    FirebaseAuth.getInstance().signOut();
                    if (arrayList.size() > 0) {
                        arrayList.clear();
                    }
                    return false;
                } else if (item.getItemId() == R.id.settings) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    return false;
                }

                return false;
            }
        });
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
                                    .setLogo(R.drawable.expenselogo)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);

                }
            }
        };


        settingsPref = PreferenceManager.getDefaultSharedPreferences(this);

        Boolean bool = settingsPref.getBoolean(getString(R.string.notificationReminder), true);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));
        if (bool) {


            Job myJob = dispatcher.newJobBuilder()
                    .setService(NotificationDispatcher.class)
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(true)
                    .setTag(job_tag)
                    .setTrigger(Trigger.executionWindow(14400, 14400))
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    .setReplaceCurrent(false)
                    .build();

            dispatcher.mustSchedule(myJob);

        } else {

            dispatcher.cancel(job_tag);
        }


    }


    private void readdata() {
        final DatabaseReference userIdRefrence = firebaseDatabase.getReference();
        childevenLisntener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                TextView textView = findViewById(R.id.salary_value);
                textView.setText(user.getSalary_final());
                TextView textView1 = findViewById(R.id.savings_value);
                textView1.setText(user.getSavings_final());
                TextView textView2 = findViewById(R.id.expene_value);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(MainActivity.this, getString(R.string.greetings) + firebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, R.string.signinfailed, Toast.LENGTH_SHORT).show();
                finish();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
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
                String country = spinner.getSelectedItem().toString();
                salary = salary_edit_text.getText().toString();
                SharedPreferences.Editor editor = settingsPref.edit();
                editor.putString(getString(R.string.monthlySalary), salary);
                editor.apply();
                User user = new User(salary, salary, "0", country);
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
                if (dataSnapshot.hasChild(getString(R.string.salary_key))) {
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
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        TextView name = header.findViewById(R.id.user_name);
        TextView email = header.findViewById(R.id.user_mail_id);
        ImageView imageView = header.findViewById(R.id.circular_user_image);
        name.setText(fbuser.getDisplayName());
        email.setText(fbuser.getEmail());
        if (fbuser.getPhotoUrl() != null) {
            Uri uri = Uri.parse(fbuser.getPhotoUrl().toString());
            Picasso.with(MainActivity.this).load(uri).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.profile);
        }


        updateUi();
    }

    private void updateRecycler() {

        Date date = new Date();
        final String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
        final String day = (String) DateFormat.format("dd", date); // 20
        final String monthString = (String) DateFormat.format("MMM", date); // Jun
        d = dayOfTheWeek + "-" + day + "-" + monthString;
        final DatabaseReference dataref = databaseRefrence.child(getString(R.string.expense_bydate_key));
        final ChildEventListener child = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ExpenseOverallByDate expenseOverallByDate = dataSnapshot.getValue(ExpenseOverallByDate.class);
                arrayList.add(expenseOverallByDate);
                RecyclerView recyclerview = findViewById(R.id.main_recycler);
                recyclerview.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
                recyclerview.setHasFixedSize(true);
                overallDataAdapter = new OverallDataAdapter(arrayList, MainActivity.this, MainActivity.this);
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
                    String salaryDate = settingsPref.getString(getString(R.string.salaryUpdate), "1");
                    Date date = new Date();
                    final String day = (String) DateFormat.format(getString(R.string.date_format), date);
                    if (day.equals(salaryDate)) {
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String monthlySalary = settingsPref.getString(getString(R.string.monthlySalary), getString(R.string.zero));
                        DatabaseReference salaryRef = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid()).child(getString(R.string.salary_key));
                        DatabaseReference expenseRef = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid()).child(getString(R.string.expensE_final));
                        DatabaseReference savingsRef = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid()).child(getString(R.string.savings_final));
                        salaryRef.setValue(monthlySalary);
                        expenseRef.setValue(getString(R.string.zero));
                        savingsRef.setValue(monthlySalary);
                    }
                    ExpenseOverallByDate expenseOverallByDate = new ExpenseOverallByDate(day, monthString, dayOfTheWeek, getString(R.string.zero));


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
        ExpenseOverallByDate exp = arrayList.get(listItemIndex);
        String dayOfTheWeek = exp.getDayName();
        String day = exp.getDay();
        String monthString = exp.getMonth();
        String date = dayOfTheWeek + "-" + day + "-" + monthString;
        Intent intent = new Intent(MainActivity.this, ByTimeExpense.class);
        intent.putExtra(getString(R.string.intentPassingDate), date);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle();
            startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }


        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d(getString(R.string.log_tag), getString(R.string.log_message));
        }

    }

}

