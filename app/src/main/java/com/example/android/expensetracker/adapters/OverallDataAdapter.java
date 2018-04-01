package com.example.android.expensetracker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.expensetracker.R;
import com.example.android.expensetracker.data.ExpenseOverallByDate;
import com.example.android.expensetracker.data.ExpenseParticularDay;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Santosh on 13-03-2018.
 */

public class OverallDataAdapter extends RecyclerView.Adapter<OverallDataAdapter.ViewHolder> {
    private ArrayList<ExpenseOverallByDate> arrayList;
    private Context context;
    private ListItemClickListner listItemClickListner;

    public OverallDataAdapter(ArrayList<ExpenseOverallByDate> arrayList, Context context, ListItemClickListner listItemClickListner) {
        super();
        this.arrayList = arrayList;
        this.context = context;
        this.listItemClickListner = listItemClickListner;
    }

    @Override
    public int getItemCount() {
        if (arrayList != null) return arrayList.size();
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.per_day_expense_data, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ExpenseOverallByDate expenseOverallByDate = arrayList.get(position);
        holder.day_name_text.setText(expenseOverallByDate.getDayName());
        holder.month_text.setText(expenseOverallByDate.getMonth());
        holder.day_text.setText(expenseOverallByDate.getDay());
        holder.expense_that_day_text.setText(expenseOverallByDate.getExpense_that_day());
        String date = expenseOverallByDate.getDayName() + "-" + expenseOverallByDate.getDay() + "-" + expenseOverallByDate.getMonth();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child(context.getString(R.string.expense_bydate_key)).child(date);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(context.getString(R.string.recent_key))) {
                    readData(holder, expenseOverallByDate);
                } else {

                    holder.noExpense.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void readData(final ViewHolder holder, ExpenseOverallByDate expenseOverallByDate) {
        String date = expenseOverallByDate.getDayName() + "-" + expenseOverallByDate.getDay() + "-" + expenseOverallByDate.getMonth();
        DatabaseReference recentListener = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child(context.getString(R.string.expense_bydate_key)).child(date).child(context.getString(R.string.recent_key));

        recentListener.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ExpenseParticularDay expenseParticularDay = dataSnapshot.getValue(ExpenseParticularDay.class);
                if (expenseParticularDay != null) {
                    holder.expense_amount_text.setText(expenseParticularDay.getExpense());
                    holder.expense_type_text.setText(expenseParticularDay.getEspense_type());
                    holder.imageView.setImageResource(expenseParticularDay.getExpense_type_image_resource_id());
                    holder.expense_time.setText(expenseParticularDay.getExpense_time());
                    holder.payment_method.setText(expenseParticularDay.getPayment_method());
                    holder.relativeLayout.setVisibility(View.VISIBLE);
                } else {
                    holder.relativeLayout.setVisibility(View.GONE);
                    holder.noExpense.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public interface ListItemClickListner {
        void onItemClicked(int listItemIndex);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView day_text;
        public TextView month_text;
        public TextView day_name_text;
        public TextView expense_that_day_text;
        public TextView expense_time;
        public TextView expense_type_text;
        public ImageView imageView;
        public TextView expense_amount_text;
        public TextView payment_method;
        public TextView optionsText;
        public TextView noExpense;
        public RelativeLayout relativeLayout;

        // public TextView expense_type_latest;
        // public  TextView expense_amount_latest;
        public ViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relative_layout);
            day_text = itemView.findViewById(R.id.day);
            noExpense = itemView.findViewById(R.id.no_Expense);
            month_text = itemView.findViewById(R.id.month);
            expense_that_day_text = itemView.findViewById(R.id.expenditure_text);
            day_name_text = itemView.findViewById(R.id.day_name);
            imageView = itemView.findViewById(R.id.expense_type_image);
            expense_amount_text = itemView.findViewById(R.id.expense_amount_text);
            expense_type_text = itemView.findViewById(R.id.expense_type_text_field);
            expense_time = itemView.findViewById(R.id.payment_time);
            payment_method = itemView.findViewById(R.id.payment_method_per_time);
            optionsText = itemView.findViewById(R.id.menu_options);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {


            int position = getAdapterPosition();
            listItemClickListner.onItemClicked(position);
        }
    }
}
