package com.example.android.expensetracker.data;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import com.example.android.expensetracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Santosh on 27-03-2018.
 */

public class UpdateDataUtils {
   private static DatabaseReference  perDayTotalExpense = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child("expense_by_date_list");
   private static DatabaseReference overAllExpense = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child("expense_final");
   private static DatabaseReference overAllSavings = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child("savings_final");

    public static  void updateTotalSavings(String exp,final String action,final String previous){
        final String temp=exp;
        overAllSavings.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer saving=Integer.parseInt(dataSnapshot.getValue(String.class));
                Integer expInt=Integer.parseInt(temp);
                Integer newSaving=0;

                if(action.equals("add")){
                    newSaving=saving-expInt;
                }
                else if(action.equals("replace")){
                    Integer previousInt=Integer.parseInt(previous);
                    saving=saving+previousInt;
                    newSaving=saving-expInt;

                }else if(action.equals("delet")){
                    newSaving=saving+expInt;
                }
                overAllSavings.setValue(String.valueOf(newSaving));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public static  void updateTotalExpense(String exp,final String action,final String previous){
        final String temp=exp;
        overAllExpense.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer last_expense=Integer.parseInt(dataSnapshot.getValue(String.class));
                Integer expInt=Integer.parseInt(temp);
                Integer newExpense=0;
                if(action.equals("add")){
                    newExpense=last_expense+expInt;
                }
                else if(action.equals("replace")){
                    Integer previousInt=Integer.parseInt(previous);
                    last_expense=last_expense-previousInt;
                    newExpense=last_expense+expInt;

                }else if(action.equals("delet")){
                    newExpense=last_expense-expInt;
                }
                overAllExpense.setValue(String.valueOf(newExpense));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static  void updatePerDayTotal(String exp, String date, final String action, final String previous){
        final String temp=exp;
     final  DatabaseReference  perDayTotalExpense = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child("expense_by_date_list").child(date).child("expense_that_day");

        perDayTotalExpense.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Integer last_expense=Integer.parseInt(dataSnapshot.getValue(String.class));
                Integer expInt=Integer.parseInt(temp);
                Integer newExpense=0;
                if(action.equals("add")){
                newExpense=last_expense+expInt;
              }
                else if(action.equals("replace")){
                    Integer previousInt=Integer.parseInt(previous);
                    last_expense=last_expense-previousInt;
                    newExpense=last_expense+expInt;

                }else if(action.equals("delet")){
                    newExpense=last_expense-expInt;
                }
                perDayTotalExpense.setValue(String.valueOf(newExpense));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static  void updatePopUpSavings(Dialog d){
        final TextView saving_temp=d.findViewById(R.id.savings_value_while_adding_expense);
        final TextView message=d.findViewById(R.id.user_message);
        overAllSavings.addListenerForSingleValueEvent(new ValueEventListener() {@Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Integer s=Integer.parseInt(dataSnapshot.getValue(String.class));
            if(s>0){
                message.setText("You are under budget feel free to spend.");
            }
            saving_temp.setText(String.valueOf(s));
        }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public static  void showEmptyWarning(final Dialog d,final Context context){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage("Some of the fields are empty\nData will not be saved!");
        builder.setPositiveButton("Complete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                d.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();

    }
}
