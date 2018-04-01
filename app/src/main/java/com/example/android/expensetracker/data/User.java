package com.example.android.expensetracker.data;

/**
 * Created by Santosh on 12-03-2018.
 */

public class User {
    private String salary_final;
    private String savings_final;
    private String expense_final;
    private String country;

    public User() {
    }

    public User(String a, String b, String c, String d) {
        this.salary_final = a;
        this.savings_final = b;
        this.expense_final = c;
        this.country = d;

    }

    public String getCountry() {

        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSalary_final() {
        return salary_final;
    }

    public void setSalary_final(String salary_final) {
        this.salary_final = salary_final;
    }

    public String getSavings_final() {
        return savings_final;
    }

    public void setSavings_final(String savings_final) {
        this.savings_final = savings_final;
    }

    public String getExpense_final() {
        return expense_final;
    }

    public void setExpense_final(String expense_final) {
        this.expense_final = expense_final;
    }
}
