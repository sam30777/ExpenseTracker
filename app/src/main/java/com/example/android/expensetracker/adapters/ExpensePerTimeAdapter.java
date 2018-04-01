package com.example.android.expensetracker.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.expensetracker.R;
import com.example.android.expensetracker.data.ExpenseParticularDay;
import com.example.android.expensetracker.data.ExpenseTypeData;
import com.example.android.expensetracker.data.UpdateDataUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Santosh on 16-03-2018.
 */

public class ExpensePerTimeAdapter extends RecyclerView.Adapter<ExpensePerTimeAdapter.ViewHolder> {
    public static Boolean itemChanged;
    public static Dialog dialog;
    private ArrayList<ExpenseParticularDay> expenseParticularDays;
    private Context context;
    private ListItemInterface listItemInterface;
    private String expense_type;
    private Integer expense_image;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            itemChanged = true;
            return false;
        }
    };

    public ExpensePerTimeAdapter(Context context, ArrayList<ExpenseParticularDay> expenseParticularDays, ListItemInterface listItemInterface) {
        super();
        this.expenseParticularDays = expenseParticularDays;
        this.context = context;
        this.listItemInterface = listItemInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.per_day_expense_detail, parent, false);
        return new ExpensePerTimeAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ExpenseParticularDay expenseOverallByDate = expenseParticularDays.get(position);
        holder.expense_amount_text.setText(expenseOverallByDate.getExpense());
        holder.expense_type_text.setText(expenseOverallByDate.getEspense_type());
        holder.imageView.setImageResource(expenseOverallByDate.getExpense_type_image_resource_id());
        holder.expense_time.setText(expenseOverallByDate.getExpense_time());
        holder.payment_method.setText(expenseOverallByDate.getPayment_method());

        holder.optionsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(context, holder.optionsText);
                popupMenu.inflate(R.menu.save_delet);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                        final DatabaseReference databaseref = firebaseDatabase.getReference().child(firebaseUser.getUid()).child(context.getString(R.string.expense_bydate_key)).child(expenseOverallByDate.getDate()).child(context.getString(R.string.expense_by_time_key));
                        if (item.getItemId() == R.id.delet_item) {
                            showDeleteConfirmationDialog(expenseOverallByDate, databaseref, position);
                        } else if (item.getItemId() == R.id.edit_item) {
                            DatabaseReference overAllSavings = firebaseDatabase.getReference().child(firebaseUser.getUid()).child(context.getString(R.string.savings_key));
                            dialog = new Dialog(context);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.add_expense_pop_up);
                            updatePopUpSavings(dialog, overAllSavings);
                            final EditText editText = dialog.findViewById(R.id.expense_amount_per_time);
                            editText.setText(expenseOverallByDate.getExpense());
                            editText.setOnTouchListener(mTouchListener);
                            final ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(context, R.array.payment_method, android.R.layout.simple_spinner_item);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            final Spinner spin = dialog.findViewById(R.id.payment_method_spinner);
                            spin.setAdapter(arrayAdapter);
                            spin.setOnTouchListener(mTouchListener);
                            int spinner_position = arrayAdapter.getPosition(expenseOverallByDate.getPayment_method());
                            spin.setSelection(spinner_position);
                            expense_image = expenseOverallByDate.getExpense_type_image_resource_id();
                            expense_type = expenseOverallByDate.getEspense_type();
                            final ImageView expenseTypeImage = dialog.findViewById(R.id.expense_type_temp_image);
                            expenseTypeImage.setImageResource(expense_image);
                            expenseTypeImage.setOnTouchListener(mTouchListener);
                            final TextView text = dialog.findViewById(R.id.expense_type_temp_text);
                            text.setText(expense_type);
                            dialog.show();
                            ImageView imageview = dialog.findViewById(R.id.dialog_to_doalog);
                            imageview.setOnTouchListener(mTouchListener);
                            imageview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final Dialog dial = new Dialog(context);

                                    final ArrayList<ExpenseTypeData> expData = setSpinnerList();
                                    final ExpenseTypeAdapter expenseTypeAdapter = new ExpenseTypeAdapter(context, expData);
                                    dial.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dial.setContentView(R.layout.expense_type_popup);
                                    GridView gridView = dial.findViewById(R.id.expense_type_grid);
                                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            ExpenseTypeData expenseTypeSpinnerDat = expData.get(i);
                                            expense_type = expenseTypeSpinnerDat.getExpe_type_name();
                                            expense_image = expenseTypeSpinnerDat.getExp_type_image();
                                            text.setText(expense_type);
                                            expenseTypeImage.setImageResource(expense_image);
                                            expenseTypeAdapter.clear();
                                            dial.dismiss();
                                        }
                                    });
                                    gridView.setAdapter(expenseTypeAdapter);
                                    dial.show();
                                }
                            });

                            Button button = dialog.findViewById(R.id.done_updating_expense_per_time);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String previousExpense = expenseOverallByDate.getExpense();
                                    String expAmount = editText.getText().toString();
                                    String paymentType = spin.getSelectedItem().toString();
                                    if (expAmount.isEmpty() || expense_type.isEmpty() || expense_image == null || paymentType.isEmpty()) {
                                        showEmptyWarning(dialog);
                                    } else {
                                        UpdateDataUtils.updatePerDayTotal(expAmount, expenseOverallByDate.getDate(), context.getString(R.string.util_Action_replace), previousExpense, context);
                                        UpdateDataUtils.updateTotalExpense(expAmount, context.getString(R.string.util_Action_replace), expenseOverallByDate.getExpense(), context);
                                        UpdateDataUtils.updateTotalSavings(expAmount, context.getString(R.string.util_Action_replace), expenseOverallByDate.getExpense(), context);
                                        ExpenseParticularDay expenseParticularDay = new ExpenseParticularDay(expAmount, expense_type, expenseOverallByDate.getExpense_time(), expense_image, paymentType, expenseOverallByDate.getPush_Id(), expenseOverallByDate.getDate());
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child(context.getString(R.string.expense_bydate_key)).child(expenseParticularDay.getDate()).child(context.getString(R.string.expense_by_time_key));
                                        databaseReference.child(expenseOverallByDate.getPush_Id()).setValue(expenseParticularDay);

                                        dialog.dismiss();
                                        expenseParticularDays.set(position, expenseParticularDay);
                                        notifyDataSetChanged();
                                        DatabaseReference recentRefrence = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child(context.getString(R.string.expense_bydate_key)).child(expenseParticularDay.getDate()).child(context.getString(R.string.recent_key));
                                        recentRefrence.setValue(expenseParticularDays.get(0));

                                    }

                                }
                            });

                        }
                        return false;
                    }
                });
            }
        });

    }

    private void showDeleteConfirmationDialog(final ExpenseParticularDay expenseOverallByDate, final DatabaseReference databaseReference, final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.delet_warning));
        builder.setPositiveButton(context.getString(R.string.confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                databaseReference.child(expenseOverallByDate.getPush_Id()).removeValue();
                UpdateDataUtils.updatePerDayTotal(expenseOverallByDate.getExpense(), expenseOverallByDate.getDate(), context.getString(R.string.util_action_delet), context.getString(R.string.zero), context);
                UpdateDataUtils.updateTotalSavings(expenseOverallByDate.getExpense(), context.getString(R.string.util_action_delet), context.getString(R.string.zero), context);
                UpdateDataUtils.updateTotalExpense(expenseOverallByDate.getExpense(), context.getString(R.string.util_action_delet), context.getString(R.string.zero), context);
                expenseParticularDays.remove(pos);
                notifyDataSetChanged();
                DatabaseReference recentRefrence = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child(context.getString(R.string.expense_bydate_key)).child(expenseOverallByDate.getDate()).child(context.getString(R.string.recent_key));
                if (expenseParticularDays.size() > 0) {
                    recentRefrence.setValue(expenseParticularDays.get(0));
                } else {
                    recentRefrence.removeValue();
                }


            }
        });
        builder.setNegativeButton(context.getString(R.string.cancel_delet), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private ArrayList<ExpenseTypeData> setSpinnerList() {
        ArrayList<ExpenseTypeData> arrayList = new ArrayList<>();
        if (arrayList.isEmpty()) {
            arrayList.add(new ExpenseTypeData(context.getString(R.string.home), R.drawable.icons8_house_24));
            arrayList.add(new ExpenseTypeData(context.getString(R.string.gym), R.drawable.icons8_barbell_48));
            arrayList.add(new ExpenseTypeData(context.getString(R.string.booze), R.drawable.icons8_beer_48));
            arrayList.add(new ExpenseTypeData(context.getString(R.string.clothes), R.drawable.icons8_clothes_48));
            arrayList.add(new ExpenseTypeData(context.getString(R.string.movie), R.drawable.icons8_movie_projector_48));
            arrayList.add(new ExpenseTypeData(context.getString(R.string.bus), R.drawable.icons8_trolleybus_48));
            arrayList.add(new ExpenseTypeData(context.getString(R.string.outing), R.drawable.icons8_airplane_take_off_48));
            arrayList.add(new ExpenseTypeData(context.getString(R.string.food), R.drawable.icons8_food_80));
            arrayList.add(new ExpenseTypeData(context.getString(R.string.gas), R.drawable.icons8_gas_station_48));
            arrayList.add(new ExpenseTypeData(context.getString(R.string.household), R.drawable.icons8_fire_48));
            arrayList.add(new ExpenseTypeData(context.getString(R.string.grooming), R.drawable.icons8_barbershop_48));
        }
        return arrayList;
    }

    private void updatePopUpSavings(Dialog d, DatabaseReference dref) {
        final TextView saving_temp = d.findViewById(R.id.savings_value_while_adding_expense);
        final TextView message = d.findViewById(R.id.user_message);
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer s = Integer.parseInt(dataSnapshot.getValue(String.class));
                if (s > 0) {
                    message.setText(R.string.user_possitive_message);
                } else {
                    message.setText(R.string.user_negative_message);
                }
                saving_temp.setText(String.valueOf(s));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (expenseParticularDays != null) return expenseParticularDays.size();
        return 0;
    }

    private void showEmptyWarning(final Dialog d) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public interface ListItemInterface {
        void onItemClicked(int listitemindex);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView expense_time;
        public TextView expense_type_text;
        public ImageView imageView;
        public TextView expense_amount_text;
        public TextView payment_method;
        public TextView optionsText;

        // public TextView expense_type_latest;
        // public  TextView expense_amount_latest;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.expense_type_image);
            expense_amount_text = itemView.findViewById(R.id.expense_amount_text);
            expense_type_text = itemView.findViewById(R.id.expense_type_text_field);
            expense_time = itemView.findViewById(R.id.payment_time);
            payment_method = itemView.findViewById(R.id.payment_method_per_time);
            optionsText = itemView.findViewById(R.id.menu_options);

        }

        @Override
        public void onClick(View view) {


            int position = getAdapterPosition();
            listItemInterface.onItemClicked(position);
        }
    }
}
