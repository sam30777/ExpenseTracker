package com.example.android.expensetracker.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.expensetracker.R;
import com.example.android.expensetracker.data.ExpenseTypeData;

import java.util.ArrayList;

/**
 * Created by Santosh on 17-03-2018.
 */

public class ExpenseTypeAdapter extends ArrayAdapter<ExpenseTypeData> {


    public ExpenseTypeAdapter(@NonNull Context context, ArrayList<ExpenseTypeData> resource) {
        super(context, 0, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.expense_type_spinner_item, parent, false);
            ImageView imageView = convertView.findViewById(R.id.spinner_image);
            TextView textView1 = convertView.findViewById(R.id.spinner_text);
            ExpenseTypeData expData = getItem(position);
            imageView.setImageResource(expData.getExp_type_image());
            textView1.setText(expData.getExpe_type_name());
        }
        return convertView;
    }


}
