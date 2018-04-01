package com.example.android.expensetracker.data;

/**
 * Created by Santosh on 17-03-2018.
 */

public class ExpenseTypeData {
    private String expe_type_name;
    private Integer exp_type_image;

    public ExpenseTypeData(String a, Integer b) {
        this.expe_type_name = a;
        this.exp_type_image = b;
    }

    public String getExpe_type_name() {
        return expe_type_name;
    }

    public Integer getExp_type_image() {
        return exp_type_image;
    }
}
