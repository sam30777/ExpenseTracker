package com.example.android.expensetracker;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.expensetracker.adapters.ExpensePerTimeAdapter;
import com.example.android.expensetracker.adapters.ExpenseTypeAdapter;
import com.example.android.expensetracker.data.ExpenseParticularDay;
import com.example.android.expensetracker.data.ExpenseTypeData;
import com.example.android.expensetracker.data.UpdateDataUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ByTimeExpense extends AppCompatActivity implements ExpensePerTimeAdapter.ListItemInterface {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String d;
    private Dialog dialog;
    private ChildEventListener childEventListener;
    private ExpensePerTimeAdapter expensePerTimeAdapter;
    private ArrayList<ExpenseTypeData> expense_Type_List = new ArrayList<>();
    private String expense_type = "";
    private Integer expense_image = 0;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference recentExpenseRefrence;

    public static ArrayList<ExpenseTypeData> setSpinnerList() {
        ArrayList<ExpenseTypeData> arrayList = new ArrayList<>();
        if (arrayList.isEmpty()) {
            arrayList.add(new ExpenseTypeData("Home rent", R.drawable.icons8_house_24));
            arrayList.add(new ExpenseTypeData("Gym", R.drawable.icons8_barbell_48));
            arrayList.add(new ExpenseTypeData("Booze", R.drawable.icons8_beer_48));
            arrayList.add(new ExpenseTypeData("Clothes", R.drawable.icons8_clothes_48));
            arrayList.add(new ExpenseTypeData("Movie", R.drawable.icons8_movie_projector_48));
            arrayList.add(new ExpenseTypeData("Bus", R.drawable.icons8_trolleybus_48));
            arrayList.add(new ExpenseTypeData("Outing", R.drawable.icons8_airplane_take_off_48));
            arrayList.add(new ExpenseTypeData("Food", R.drawable.icons8_food_80));
            arrayList.add(new ExpenseTypeData("Gas", R.drawable.icons8_gas_station_48));
            arrayList.add(new ExpenseTypeData("HouseHold Gas", R.drawable.icons8_fire_48));
            arrayList.add(new ExpenseTypeData("Grooming", R.drawable.icons8_barbershop_48));
        }
        return arrayList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_by_time_expense);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        d = intent.getStringExtra("date");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        FloatingActionButton floatingActionButton = findViewById(R.id.floating_add_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUi();
            }
        });
        readData();
    }

    private void updateUi() {

        dialog = new Dialog(ByTimeExpense.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_expense_pop_up);
        UpdateDataUtils.updatePopUpSavings(dialog);

        final Spinner spinner = dialog.findViewById(R.id.payment_method_spinner);
        final ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(ByTimeExpense.this, R.array.payment_method, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        dialog.show();
        ImageView but = dialog.findViewById(R.id.dialog_to_doalog);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expense_Type_List = setSpinnerList();
                final Dialog dial = new Dialog(ByTimeExpense.this);
                final ExpenseTypeAdapter expenseTypeAdapter = new ExpenseTypeAdapter(ByTimeExpense.this, expense_Type_List);
                dial.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dial.setContentView(R.layout.expense_type_popup);
                GridView gridView = dial.findViewById(R.id.expense_type_grid);
                gridView.setAdapter(expenseTypeAdapter);
                dial.show();
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        ExpenseTypeData expenseTypeSpinnerDat = expense_Type_List.get(i);
                        expense_type = expenseTypeSpinnerDat.getExpe_type_name();
                        expense_image = expenseTypeSpinnerDat.getExp_type_image();
                        TextView textView = dialog.findViewById(R.id.expense_type_temp_text);
                        textView.setText(expense_type);
                        ImageView imageView = dialog.findViewById(R.id.expense_type_temp_image);
                        imageView.setImageResource(expense_image);
                        expenseTypeAdapter.clear();
                        dial.dismiss();
                    }
                });
            }
        });

        Button button = dialog.findViewById(R.id.done_updating_expense_per_time);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference = firebaseDatabase.getReference().child(FirebaseAuth.getInstance().getUid()).child("expense_by_date_list").child(d).child("expense_by_time_list");
                EditText editText = dialog.findViewById(R.id.expense_amount_per_time);
                String expAmount = editText.getText().toString();
                String paymentMethod = spinner.getSelectedItem().toString();
                Date date = new Date();

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String currentDateTimeString = sdf.format(date);
                String id = databaseReference.push().getKey();
                if (expAmount.isEmpty() || expense_type.isEmpty() || expense_image == 0) {
                    UpdateDataUtils.showEmptyWarning(dialog, ByTimeExpense.this);
                } else {
                    ExpenseParticularDay expenseParticularDay = new ExpenseParticularDay(expAmount, expense_type, currentDateTimeString, expense_image, paymentMethod, id, d);
                    recentExpenseRefrence = firebaseDatabase.getReference().child(FirebaseAuth.getInstance().getUid()).child("expense_by_date_list").child(d).child("Recent");
                    recentExpenseRefrence.setValue(expenseParticularDay);

                    databaseReference.child(id).setValue(expenseParticularDay);
                    UpdateDataUtils.updateTotalSavings(expAmount, "add", "0");
                    UpdateDataUtils.updateTotalExpense(expAmount, "add", "0");
                    UpdateDataUtils.updatePerDayTotal(expAmount, d, "add", "0");
                    editText.clearComposingText();
                    dialog.dismiss();
                }


            }
        });


    }

    private void readData() {
        final ArrayList<ExpenseParticularDay> expenseParticularDays = new ArrayList<>();
        DatabaseReference dref = firebaseDatabase.getReference().child(FirebaseAuth.getInstance().getUid()).child("expense_by_date_list").child(d).child("expense_by_time_list");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ExpenseParticularDay expenseParticularDay = dataSnapshot.getValue(ExpenseParticularDay.class);
                Toast.makeText(ByTimeExpense.this, expenseParticularDay.getEspense_type(), Toast.LENGTH_SHORT).show();

                expenseParticularDays.add(0, expenseParticularDay);
                RecyclerView recyclerview = findViewById(R.id.by_time_expense_details);
                recyclerview.setLayoutManager(new LinearLayoutManager(ByTimeExpense.this, LinearLayoutManager.VERTICAL, false));
                recyclerview.setHasFixedSize(true);

                expensePerTimeAdapter = new ExpensePerTimeAdapter(ByTimeExpense.this, expenseParticularDays, ByTimeExpense.this);
                recyclerview.setAdapter(expensePerTimeAdapter);

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

        dref.addChildEventListener(childEventListener);
    }

    @Override
    public void onItemClicked(int listitemindex) {

    }


}
