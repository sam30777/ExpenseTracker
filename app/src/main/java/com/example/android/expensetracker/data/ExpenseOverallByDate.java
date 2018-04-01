package com.example.android.expensetracker.data;

/**
 * Created by Santosh on 15-03-2018.
 */

public class ExpenseOverallByDate {
    private String day;
    private String month;
    private String dayName;
    private String expense_that_day;

    public ExpenseOverallByDate() {
    }

    public ExpenseOverallByDate(String a, String b, String c, String d) {
        this.day = a;
        this.month = b;
        this.dayName = c;
        this.expense_that_day = d;

    }

    public String getDay() {
        return day;
    }

    public void setDay(String a) {
        this.day = a;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String b) {
        this.month = b;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String c) {
        this.dayName = c;
    }

    public String getExpense_that_day() {
        return expense_that_day;
    }

    public void setExpense_that_day(String d) {
        this.expense_that_day = d;
    }

}
