package com.example.android.expensetracker.data;

/**
 * Created by Santosh on 15-03-2018.
 */

public class ExpenseOverallByDate {
    private String day;
    private String month;
    private String dayName;
    private String expense_that_day;
  //  private String latest_expense_amount;
  //  private String latest_expense_type;
    public ExpenseOverallByDate(){}
    public ExpenseOverallByDate(String a,String b,String c,String d){
        this.day=a;
        this.month=b;
        this.dayName=c;
        this.expense_that_day=d;
       // this.latest_expense_amount=e;
       // this.latest_expense_type=f;
    }
    public void setDay(String a){
        this.day=a;
    }
    public void setMonth(String b){
        this.month=b;
    }
    public void setDayName(String c){
        this.dayName=c;
    }
    public void setExpense_that_day(String d){
        this.expense_that_day=d;
    }
  //  public void setLatest_expense_amount(String e){
  //      this.latest_expense_type=e;
   // }
 //   public void setLatest_expense_type(String f){
 //       this.latest_expense_type=f;
//    }
    public String getDay(){
        return day;
    }
    public String getMonth(){
        return month;
    }
    public String getDayName(){
        return dayName;
    }
    public String getExpense_that_day(){
        return expense_that_day;
    }
  //  public String getLatest_expense_amount(){
  //      return latest_expense_amount;
  //  }
 /*   public String getLatest_expense_type(){
        return latest_expense_type;
    }*/
}
