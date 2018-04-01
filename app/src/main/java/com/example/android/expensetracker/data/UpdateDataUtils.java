package com.example.android.expensetracker.data;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import com.example.android.expensetracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Santosh on 27-03-2018.
 */

public class UpdateDataUtils {
    private static FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public static void updateTotalSavings(String exp, final String action, final String previous, final Context context) {
        final String temp = exp;
        final DatabaseReference overAllSavings = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid()).child(context.getString(R.string.final_savings));

        overAllSavings.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer saving = Integer.parseInt(dataSnapshot.getValue(String.class));
                Integer expInt = Integer.parseInt(temp);
                Integer newSaving = 0;

                if (action.equals(context.getString(R.string.util_action_add))) {
                    newSaving = saving - expInt;
                } else if (action.equals(context.getString(R.string.util_Action_replace))) {
                    Integer previousInt = Integer.parseInt(previous);
                    saving = saving + previousInt;
                    newSaving = saving - expInt;

                } else if (action.equals(context.getString(R.string.util_action_delet))) {
                    newSaving = saving + expInt;
                }
                overAllSavings.setValue(String.valueOf(newSaving));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void updateTotalExpense(String exp, final String action, final String previous, final Context context) {
        final String temp = exp;
        final DatabaseReference overAllExpense = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid()).child(context.getString(R.string.final_expense));

        overAllExpense.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer last_expense = Integer.parseInt(dataSnapshot.getValue(String.class));
                Integer expInt = Integer.parseInt(temp);
                Integer newExpense = 0;
                if (action.equals(context.getString(R.string.util_action_add))) {
                    newExpense = last_expense + expInt;
                } else if (action.equals(context.getString(R.string.util_Action_replace))) {
                    Integer previousInt = Integer.parseInt(previous);
                    last_expense = last_expense - previousInt;
                    newExpense = last_expense + expInt;

                } else if (action.equals(context.getString(R.string.util_action_delet))) {
                    newExpense = last_expense - expInt;
                }
                overAllExpense.setValue(String.valueOf(newExpense));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void updatePerDayTotal(String exp, String date, final String action, final String previous, final Context context) {
        final String temp = exp;
        final DatabaseReference perDayTotalExpense = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid()).child(context.getString(R.string.expense_bydate_key)).child(date).child(context.getString(R.string.expense_particular_day));

        perDayTotalExpense.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Integer last_expense = Integer.parseInt(dataSnapshot.getValue(String.class));
                Integer expInt = Integer.parseInt(temp);
                Integer newExpense = 0;
                if (action.equals(context.getString(R.string.util_action_add))) {
                    newExpense = last_expense + expInt;
                } else if (action.equals(context.getString(R.string.util_Action_replace))) {
                    Integer previousInt = Integer.parseInt(previous);
                    last_expense = last_expense - previousInt;
                    newExpense = last_expense + expInt;

                } else if (action.equals(context.getString(R.string.util_action_delet))) {
                    newExpense = last_expense - expInt;
                }
                perDayTotalExpense.setValue(String.valueOf(newExpense));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void updatePopUpSavings(Dialog d, final Context context) {
        final TextView saving_temp = d.findViewById(R.id.savings_value_while_adding_expense);
        final TextView message = d.findViewById(R.id.user_message);
        final DatabaseReference overAllSavings = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid()).child("savings_final");
        overAllSavings.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer s = Integer.parseInt(dataSnapshot.getValue(String.class));
                if (s > 0) {
                    message.setText(context.getString(R.string.user_message_budget));
                }
                saving_temp.setText(String.valueOf(s));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void showEmptyWarning(final Dialog d, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.user_message));
        builder.setPositiveButton(context.getString(R.string.complete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(context.getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                d.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
