package com.example.android.expensetracker.data;

/**
 * Created by Santosh on 13-03-2018.
 */

public class ExpenseParticularDay {
    private String expense;
    private String espense_type;
    private String expense_time;
    private String payment_method;
    private Integer expense_type_image_resource_id;
    private String push_Id;
    private String date;

    public ExpenseParticularDay(String a, String b, String c, Integer d, String z, String puid, String da) {
        this.expense = a;
        this.espense_type = b;
        this.expense_time = c;
        this.payment_method = z;
        this.expense_type_image_resource_id = d;
        this.push_Id = puid;
        this.date = da;
    }

    public ExpenseParticularDay() {
    }

    public String getPush_Id() {
        return push_Id;
    }

    public void setPush_Id(String a) {
        this.push_Id = a;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String da) {
        this.date = da;
    }

    public String getExpense() {

        return expense;
    }

    public void setExpense(String expense) {
        this.expense = expense;
    }

    public String getEspense_type() {
        return espense_type;
    }

    public void setEspense_type(String espense_type) {
        this.espense_type = espense_type;
    }

    public String getExpense_time() {
        return expense_time;
    }

    public void setExpense_time(String expense_time) {
        this.expense_time = expense_time;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public Integer getExpense_type_image_resource_id() {
        return expense_type_image_resource_id;
    }

    public void setExpense_type_image_resource_id(Integer expense_type_image_resource_id) {
        this.expense_type_image_resource_id = expense_type_image_resource_id;
    }
}
