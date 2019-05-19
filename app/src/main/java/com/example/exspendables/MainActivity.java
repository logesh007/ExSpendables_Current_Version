package com.example.exspendables;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.util.SparseBooleanArray;

import com.mynameismidori.currencypicker.CurrencyPicker;
import com.mynameismidori.currencypicker.CurrencyPickerListener;

import org.w3c.dom.Text;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    DatabaseHandler dbPinTable;     // database table for storing PIN
    DatabaseCategories dbCategories;
    private Button btnSavePin;      // access Save PIN button during First time user login
    private boolean isEntryDateClicked = false;
    private boolean isEndDateClicked = false;
    public static String oldValue;


    // main() method of UI
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        dbPinTable = new DatabaseHandler(this);
        Cursor cursor = dbPinTable.getPinData();

        // check if a value of PIN exists in dbTable - PIN
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                //launch authenticate PIN page
                setContentView(R.layout.login_page);

            } else {
                // if PIN does not exist, launch first time login page
                setContentView(R.layout.first_time_login);
                btnSavePin = (Button) findViewById(R.id.set_btn);
                btnSavePin.setOnClickListener(this);
            }
        }
    }


    // Event handler for button "Enter Income"
    public void openIncomePage(View view) {
        setContentView(R.layout.income);

        Button button = (Button) findViewById(R.id.selectDate);
        button.setOnClickListener(new View.OnClickListener() {
            public void checkButtonStat(){
                isEntryDateClicked = true;
                isEndDateClicked = false;
            }
            @Override
            public void onClick(View v) {
                checkButtonStat();
                DialogFragment datePicker = new com.example.exspendables.DatePicker();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

    }

    // Event handler for button "Enter Expense"
    public void openExpensePage(View view) {
        setContentView(R.layout.expense);

        DatabaseCategories dbCategories;
        dbCategories = new DatabaseCategories(this);
        // Populate Category DDLB
        Spinner categoryddlb = (Spinner) findViewById(R.id.categoryddlb);
        List<String> categorylist = dbCategories.getData();

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,categorylist);
        categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categoryddlb.setAdapter(categoryAdapter);

        /*for(int i = 0;i < categorylist.size();i++){
            categoryAdapter.add(categorylist.get(i).toString());
            categoryAdapter.notifyDataSetChanged();
        }*/

        Button button = (Button) findViewById(R.id.selectDate);
        button.setOnClickListener(new View.OnClickListener() {
            public void checkButtonStat(){
                isEntryDateClicked = true;
                isEndDateClicked = false;
            }
            @Override
            public void onClick(View v) {
                checkButtonStat();
                DialogFragment datePicker = new com.example.exspendables.DatePicker();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        DatabaseCurrency databaseCurrency = new DatabaseCurrency(this);
        Cursor currCode = databaseCurrency.getData();

        if(currCode != null){
            if(currCode.moveToFirst()){
                String currencySavedInDB = currCode.getString(0).toString();
                TextView code = (TextView) findViewById(R.id.currencyCode);
                code.setText(currencySavedInDB);
            }
        }


    }

    public void onSetCurrency(View view){
        final DatabaseCurrency databaseCurrency = new DatabaseCurrency(this);
        final CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");  // dialog title
        picker.setListener(new CurrencyPickerListener() {
            @Override
            public void onSelectCurrency(String name, String code, String symbol, int flagDrawableResID) {
                // Implement your code here
                closeOptionsMenu();
                String currency_selected = code.toString();

                // save this value to DB so that it can be displayed next to Amount
                Cursor currencyData = databaseCurrency.getData();
                if(currencyData != null){
                    if(currencyData.moveToFirst()){
                        String currencySavedInDB = currencyData.getString(0).toString();
                        databaseCurrency.modifyData(currency_selected,currencySavedInDB);
                    }
                    else{
                        databaseCurrency.addData(currency_selected);
                    }
                }
            }
        });
        picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
    }


    public void openSummaryPage(View view) {
        setContentView(R.layout.summary);

        Button button = (Button) findViewById(R.id.selectDate);
        button.setOnClickListener(new View.OnClickListener() {
            public void checkButtonStat(){
                isEntryDateClicked = true;
                isEndDateClicked = false;
            }
            @Override
            public void onClick(View v) {
                checkButtonStat();
                DialogFragment datePicker = new com.example.exspendables.DatePicker();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });


        Button buttonEndDate = (Button) findViewById(R.id.selecEndtDate);
        buttonEndDate.setOnClickListener(new View.OnClickListener() {
            public void checkButtonStat(){
                isEntryDateClicked = false;
                isEndDateClicked = true;
            }

            @Override
            public void onClick(View v) {
                checkButtonStat();
                DialogFragment datePicker = new com.example.exspendables.DatePicker();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    public void showSummary(View view){

        TextView startDate = (TextView) findViewById(R.id.entryDate);
        String startDateValue = startDate.getText().toString();

        TextView endDate = (TextView) findViewById(R.id.endDate);
        String endDateValue = endDate.getText().toString();

        DatabaseIncomeExpense databaseIncomeExpense = new DatabaseIncomeExpense(this);
        Cursor cursor = databaseIncomeExpense.getData(startDateValue,endDateValue);

        List<DatabaseIncomeExpense> expenseDetails = new ArrayList<DatabaseIncomeExpense>();

        cursor.moveToFirst();
        int i = 0;
        while (!cursor.isAfterLast()){
            databaseIncomeExpense.category      = cursor.getString(0);
            databaseIncomeExpense.startDate     = cursor.getString(1);
            databaseIncomeExpense.endDate       = cursor.getString(2);
            databaseIncomeExpense.amount        = cursor.getString(3);
            databaseIncomeExpense.code          = cursor.getString(4);
            databaseIncomeExpense.paymentMethod = cursor.getString(5);
            databaseIncomeExpense.note          = cursor.getString(6);
            databaseIncomeExpense.indicator     = cursor.getString(7);

            expenseDetails.add(databaseIncomeExpense);
            i++;
            cursor.moveToNext();
        }

        StringBuilder builder = new StringBuilder();
        for(DatabaseIncomeExpense expense: expenseDetails){
            builder.append(expense.getCategory()).append(";")
                    .append(expense.getStartDate()).append(";")
                    .append(expense.getEndDate()).append(";")
                    .append(expense.getAmount()).append(";")
                    .append(expense.getCode()).append(";")
                    .append(expense.getPaymentMethod()).append(";")
                    .append(expense.getNote()).append("_");
        }

        builder.toString();
        String st = new String(builder);
        String[] rows  = st.split("_");

        TableLayout tableLayout = (TableLayout)findViewById(R.id.tab);
        tableLayout.removeAllViews();
        String row;
        for(int rowCount =0;rowCount<rows.length;rowCount++){
         //   Log.d("Rows",rows[i]);
            row  = rows[rowCount];
            TableRow tableRow = new TableRow(getApplicationContext());
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            final String[] cols = row.split(";");

            Handler handler = null;

            for (int j = 0; j < cols.length; j++) {
                final String col = cols[j];
                TextView columsView = new TextView(getApplicationContext());
                columsView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                //columsView.setTextColor(android.R.color.black);
                columsView.setText(String.format("%7s", col));
                //Log.d("Cols", String.format("%7s", col));
                tableRow.addView(columsView);

            }
            tableLayout.addView(tableRow);
        }
    }

    public void openSettingsPage(View view){
        setContentView(R.layout.settings);

        Button categoryDisplay = (Button) findViewById(R.id.edit_categories);
        categoryDisplay.setOnClickListener(this);

        Button changePinBtn = (Button) findViewById(R.id.changePin);
        changePinBtn.setOnClickListener(this);

    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        month = month + 1;

        String currentDateString = year + "-" + month + "-" + dayOfMonth; //DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());

        if(isEntryDateClicked == true){
            TextView entryDate = (TextView) findViewById(R.id.entryDate);
            entryDate.setText(currentDateString);
        }
        else if(isEndDateClicked == true){
            TextView endDate = (TextView) findViewById(R.id.endDate);
            endDate.setText(currentDateString);
        }
    }

    public void saveExpense(View view) {

        Spinner category = (Spinner) findViewById(R.id.categoryddlb);
        String categoryValue = category.toString();


        TextView startDate = (TextView) findViewById(R.id.entryDate);
        String dummy = startDate.getText().toString();
        Date startDateValue = Date.valueOf(startDate.getText().toString());

        TextView endDate = (TextView) findViewById(R.id.endDate);
        Date endDateValue = Date.valueOf(endDate.getText().toString());

        EditText amount = (EditText) findViewById(R.id.amount);
        int amountValue = Integer.valueOf(amount.getText().toString());

        TextView code = (TextView) findViewById(R.id.currencyCode);
        String codeValue = "EUR";//code.getText().toString();

        Spinner paymentMethod = (Spinner) findViewById(R.id.paymList);
        String paymMethodValue = paymentMethod.toString();

        EditText note = (EditText) findViewById(R.id.optionalNote);
        String noteValue = note.getText().toString();

        String indicatorValue = "Expense";

        DatabaseIncomeExpense databaseIncomeExpense = new DatabaseIncomeExpense(this);
        databaseIncomeExpense.addData(categoryValue,startDateValue,endDateValue,amountValue,
                codeValue,paymMethodValue,noteValue,indicatorValue);

    }

    public void onLogin(View view){
        // get the value of PIN from Database
        // verify against the currently entered user PIN
        // if same redirect to Income/Expense/Summary page
        // else display an error message

        EditText pinToAuth = (EditText) findViewById(R.id.pinTextBox);
        String pinToCheck = pinToAuth.getText().toString();

        dbPinTable = new DatabaseHandler(this);
        Cursor cursor = dbPinTable.getPinData();

        // check if a value of PIN exists in dbTable - PIN
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String pinSavedInDB = cursor.getString(0).toString();
                if (pinSavedInDB.equals(pinToCheck)) {
                    setContentView(R.layout.income_or_expense);
                }
                else
                {
                    TextView incorrectPin = (TextView) findViewById(R.id.incorrectPin);
                    incorrectPin.setText("PIN entered is wrong, please check");
                }
            }
        }
    }

    @Override
    public void onClick(View v) {

        DialogFragment datePicker = new com.example.exspendables.DatePicker();

        switch (v.getId()){
            case R.id.set_btn:
                Toast.makeText(this,"Button clicked",Toast.LENGTH_SHORT).show();
                EditText pinValue = (EditText) findViewById(R.id.set_Pin_textbox);
                EditText reEnterPinValue = (EditText) findViewById(R.id.confirm_Pin_textbox);

                String pin = pinValue.getText().toString();
                String pinToConfirm = reEnterPinValue.getText().toString();

                if(pin.equals(pinToConfirm)){
                    dbPinTable = new DatabaseHandler(this);
                    dbPinTable.addData(pin);
                    // Redirect to activity where user is prompted to
                    // 1. Enter income 2. Expense 3. View summary
                    setContentView(R.layout.income_or_expense);

                }
                else
                {
                    // PIN does not match, display an error message next to Text box
                    TextView pin_no_match = (TextView)findViewById(R.id.pin_no_match);
                    pin_no_match.setText("PIN do not match, please re-enter");

                }
                break;

            case R.id.edit_categories:

                setContentView(R.layout.category_popup);

                ListView categoryList = (ListView) findViewById(R.id.categorylist);
                Button deleteCategory = (Button) findViewById(R.id.deleteCategoryBtn);
                Button modifyCategory = (Button) findViewById(R.id.modifyCategoryBtn);
                Button addCategory = (Button) findViewById(R.id.addCategoryBtn);

                dbCategories = new DatabaseCategories(this);
                // Populate Category List View
                List<String> categorylist = dbCategories.getData();
                String[] values = new String[categorylist.size()];
                for(int i = 0; i < categorylist.size();i++){
                    values[i] = categorylist.get(i).toString();
                }
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>
                        (this,android.R.layout.simple_list_item_multiple_choice,values);
                categoryList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                categoryList.setAdapter(categoryAdapter);

                deleteCategory.setOnClickListener(this);
                modifyCategory.setOnClickListener(this);
                addCategory.setOnClickListener(this);
                break;

            case  R.id.deleteCategoryBtn:
                dbCategories = new DatabaseCategories(this);
                categoryList = (ListView) findViewById(R.id.categorylist);
                SparseBooleanArray checked = categoryList.getCheckedItemPositions();
                ArrayList<String> selectedItems = new ArrayList<String>();

                categorylist = dbCategories.getData();
                values = new String[categorylist.size()];
                for(int i = 0; i < categorylist.size();i++){
                    values[i] = categorylist.get(i).toString();
                }
                categoryAdapter = new ArrayAdapter<String>
                        (this,android.R.layout.simple_list_item_multiple_choice,values);

                for (int i = 0; i < checked.size(); i++) {
                    // Item position in adapter
                    int position = checked.keyAt(i);
                    // Add sport if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i)) {
                        //          String dummy1 = selectedItems.get(i).toString();
                        //         String dummy2 = selectedItems.get(position).toString();
                        dbCategories.deleteData(categoryAdapter.getItem(position).toString());
                    }
                }

                //refresh the list with new value
                categoryList = (ListView) findViewById(R.id.categorylist);

                dbCategories = new DatabaseCategories(this);
                // Populate Category List View
                categorylist = dbCategories.getData();
                values = new String[categorylist.size()];
                for(int i = 0; i < categorylist.size();i++){
                    values[i] = categorylist.get(i).toString();
                }

                categoryAdapter = new ArrayAdapter<String>
                        (this,android.R.layout.simple_list_item_multiple_choice,values);
                categoryList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                categoryList.setAdapter(categoryAdapter);
                break;


            case R.id.addCategoryBtn:
                // create a layout with a EditText and OK button on click on "add"
                setContentView(R.layout.add_category);
                Button okAddCategory = (Button) findViewById(R.id.btnOKAddCategory);
                okAddCategory.setOnClickListener(this);
                break;

            case R.id.btnOKAddCategory:
                dbCategories = new DatabaseCategories(this);
                EditText newCategoryValue = (EditText) findViewById(R.id.add_cat_textbox);
                dbCategories.addData(newCategoryValue.getText().toString());
                setContentView(R.layout.category_popup);

                //refresh the list with new value
                categoryList = (ListView) findViewById(R.id.categorylist);

                deleteCategory = (Button) findViewById(R.id.deleteCategoryBtn);
                modifyCategory = (Button) findViewById(R.id.modifyCategoryBtn);
                addCategory = (Button) findViewById(R.id.addCategoryBtn);

                deleteCategory.setOnClickListener(this);
                modifyCategory.setOnClickListener(this);
                addCategory.setOnClickListener(this);

                dbCategories = new DatabaseCategories(this);
                // Populate Category List View
                categorylist = dbCategories.getData();
                values = new String[categorylist.size()];
                for(int i = 0; i < categorylist.size();i++){
                    values[i] = categorylist.get(i).toString();
                }

                categoryAdapter = new ArrayAdapter<String>
                        (this,android.R.layout.simple_list_item_multiple_choice,values);
                categoryList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                categoryList.setAdapter(categoryAdapter);
                break;

            case R.id.modifyCategoryBtn:
                // create a layout with a EditText and OK button on click on "Modify"

                dbCategories = new DatabaseCategories(this);
                categoryList = (ListView) findViewById(R.id.categorylist);
                checked = categoryList.getCheckedItemPositions();
                //selectedItems = new ArrayList<String>();

                checked = categoryList.getCheckedItemPositions();
                //selectedItems = new ArrayList<String>();

                categorylist = dbCategories.getData();
                values = new String[categorylist.size()];
                for(int i = 0; i < categorylist.size();i++){
                    values[i] = categorylist.get(i).toString();
                }
                categoryAdapter = new ArrayAdapter<String>
                        (this,android.R.layout.simple_list_item_multiple_choice,values);

                for (int i = 0; i < checked.size(); i++) {
                    // Item position in adapter
                    int position = checked.keyAt(i);
                    // Add sport if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i)) {
                        //          String dummy1 = selectedItems.get(i).toString();
                        //         String dummy2 = selectedItems.get(position).toString();
                        oldValue = categoryAdapter.getItem(position).toString();
                        break;
                    }
                }

                setContentView(R.layout.modify_category);
                EditText valueToBeModified = (EditText) findViewById(R.id.modify_cat_textbox);
                valueToBeModified.setText(oldValue);

                Button okModifyButton = (Button) findViewById(R.id.btnOKModifyCategory);
                okModifyButton.setOnClickListener(this);
                break;

            case R.id.btnOKModifyCategory:

                EditText newValue = (EditText) findViewById(R.id.modify_cat_textbox);
                dbCategories = new DatabaseCategories(this);
                dbCategories.modifyData(newValue.getText().toString(),oldValue);

                setContentView(R.layout.category_popup);
                //refresh the list with new value
                categoryList = (ListView) findViewById(R.id.categorylist);

                deleteCategory = (Button) findViewById(R.id.deleteCategoryBtn);
                modifyCategory = (Button) findViewById(R.id.modifyCategoryBtn);
                addCategory = (Button) findViewById(R.id.addCategoryBtn);

                deleteCategory.setOnClickListener(this);
                modifyCategory.setOnClickListener(this);
                addCategory.setOnClickListener(this);

                dbCategories = new DatabaseCategories(this);
                // Populate Category List View
                categorylist = dbCategories.getData();
                values = new String[categorylist.size()];
                for(int i = 0; i < categorylist.size();i++){
                    values[i] = categorylist.get(i).toString();
                }
                categoryAdapter = new ArrayAdapter<String>
                        (this,android.R.layout.simple_list_item_multiple_choice,values);
                categoryList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                categoryList.setAdapter(categoryAdapter);
                break;

            case R.id.changePin:
                setContentView(R.layout.change_pin);

                Button changePinOkBtn = (Button) findViewById(R.id.change_pin_ok);
                changePinOkBtn.setOnClickListener(this);
                break;

            case R.id.change_pin_ok:

                pinValue = (EditText) findViewById(R.id.set_Pin_textbox);
                reEnterPinValue = (EditText) findViewById(R.id.confirm_Pin_textbox);

                pin = pinValue.getText().toString();
                pinToConfirm = reEnterPinValue.getText().toString();


                if (pin.equals(pinToConfirm)) {
                    dbPinTable = new DatabaseHandler(this);
                    Cursor cursor = dbPinTable.getPinData();

                    // check if a value of PIN exists in dbTable - PIN
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            String pinSavedInDB = cursor.getString(0).toString();
                            dbPinTable.modifyData(pin, pinSavedInDB);

                            setContentView(R.layout.settings);
                            Button categoryDisplay = (Button) findViewById(R.id.edit_categories);
                            categoryDisplay.setOnClickListener(this);

                            Button changePinBtn = (Button) findViewById(R.id.changePin);
                            changePinBtn.setOnClickListener(this);

                            break;
                        }
                    }
                }
                else{
                    // PIN does not match, display an error message next to Text box
                    TextView pin_no_match = (TextView)findViewById(R.id.pin_no_match);
                    pin_no_match.setText("PIN do not match, please re-enter");
                    break;
                }
        }
    }

    public void backToHome(View view) {
        setContentView(R.layout.income_or_expense);
    }

    public void backToSettings(View view){
        setContentView(R.layout.settings);

        Button categoryDisplay = (Button) findViewById(R.id.edit_categories);
        categoryDisplay.setOnClickListener(this);

        Button changePinBtn = (Button) findViewById(R.id.changePin);
        changePinBtn.setOnClickListener(this);
    }

    public void backToCategory(View view){
        setContentView(R.layout.category_popup);

        Button deleteCategory = (Button) findViewById(R.id.deleteCategoryBtn);
        Button modifyCategory = (Button) findViewById(R.id.modifyCategoryBtn);
        Button addCategory = (Button) findViewById(R.id.addCategoryBtn);

        deleteCategory = (Button) findViewById(R.id.deleteCategoryBtn);
        modifyCategory = (Button) findViewById(R.id.modifyCategoryBtn);
        addCategory = (Button) findViewById(R.id.addCategoryBtn);

        deleteCategory.setOnClickListener(this);
        modifyCategory.setOnClickListener(this);
        addCategory.setOnClickListener(this);
    }
}

