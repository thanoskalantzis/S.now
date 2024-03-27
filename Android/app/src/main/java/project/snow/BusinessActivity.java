package project.snow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * BusinessActivity Class.
 *
 * This class actually provides the ability to get some major work done (regarding data entries) directly to the business itself.
 * Here, each business is able to:
 * 1. Add a new product,
 * 2. Select and edit an existing product (edit product name, edit product description, edit product price, or even delete the product),
 * 3. Add a new working employee to the business staff,
 * 4. Remove an employee from the current business staff,
 * 5. Alter (Edit) the number of available tables (increase them, or even decrease them).
 *
 * @author thanoskalantzis
 */
public class BusinessActivity extends AppCompatActivity {
    //Class variables.
    //url_get_working_employees variable is actually the url which corresponds to the php file for getting all working employees from working_employees table (located on our database server).
    private String url_get_working_employees;
    //url_add_working_employee variable is actually the url which corresponds to the php file for adding a new employee as a working employee.
    //Employee must have registered himself/herself before being added by the business itself.
    private String url_add_working_employee;
    //url_remove_working_employee variable is actually the url which corresponds to the php file for removing an employee from the current business staff.
    private String url_remove_working_employee;
    //url_alter_number_tables variable is actually the url which corresponds to the php file for altering (editing) the number of available tables.
    private String url_alter_number_tables;
    private ImageView beersImage;
    private ImageView coffeeImage;
    private Button newProductButton;
    private Button editProductButton;
    private Button addWorkingEmployeeButton;
    private Button removeWorkingEmployee;
    private Button alterNumberTableButton;
    private Business business;


    /**
     * The following method is essential and necessary due to the fact that BusinessActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);

        //Initialize url_get_working_employees.
        url_get_working_employees = getString(R.string.BASE_URL).concat("/connect/getworkingemployees.php");
        //Initialize url_add_working_employee.
        url_add_working_employee = getString(R.string.BASE_URL).concat("/connect/addworkingemployee.php");
        //Initialize url_remove_working_employee.
        url_remove_working_employee = getString(R.string.BASE_URL).concat("/connect/removeworkingemployee.php");
        //Initialize url_alter_number_tables.
        url_alter_number_tables = getString(R.string.BASE_URL).concat("/connect/alternumbertables.php");

        //Getting parsed data from the previously active activity.
        business = (Business) getIntent().getSerializableExtra("business");

        //Setting the title of the action bar.
        ActionBar actionBar = getSupportActionBar();
            if(actionBar != null) {
            actionBar.setTitle("Υπηρεσίες Καταστήματος");
        }

        //Getting display metrics (width & height) of the particular screen of the device that runs our app.
        //The reason we do so, is so as each component to be exactly placed accordingly to the dimensions of each screen.
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        beersImage = (ImageView) findViewById(R.id.beersImage2);
        beersImage.getLayoutParams().height=height/3;

        coffeeImage = (ImageView) findViewById(R.id.coffeeImage2);
        coffeeImage.getLayoutParams().height = height/6;

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.businessMenuLinearLayout);
        linearLayout.setPadding(0, height/5, 0, 0);

        //Here follows the process for adding a new product.
        newProductButton = (Button) findViewById(R.id.newProductButton);
        newProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Whenever the newProductButton is pressed then a new activity is created.
                //In this case, the activity which is created corresponds to the NewProductActivity class.
                Intent intent = new Intent(BusinessActivity.this, NewProductActivity.class);
                //Placing some extra information material for the next activity which is about to be activated.
                intent.putExtra("business", business);
                startActivity(intent);
                finish();
            }
        });

        //Here follows the process for editing an already existing product.
        editProductButton = (Button) findViewById(R.id.editProductsButton);
        editProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Whenever the editProductButton is pressed then a new activity is created.
                //In this case, the activity which is created corresponds to the EditProductsActivity class.
                Intent intent = new Intent(BusinessActivity.this, EditProductsActivity.class);
                //Placing some extra information material for the next activity which is about to be activated.
                intent.putExtra("business", business);
                startActivity(intent);
                finish();
            }
        });

        //Here follows the process for adding a new working employee to the business staff.
        addWorkingEmployeeButton = (Button) findViewById(R.id.addWorkingEmployeeButton);
        addWorkingEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setTitle("Προσθήκη εργαζομένου καταστήματος");
                alertDialogBuilder.setMessage("(Πλήθος χαρακτήρων: 9)");

                //Set up the input.
                final EditText inputTextDialog = new EditText(view.getContext());

                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(9);
                inputTextDialog.setFilters(FilterArray);

                inputTextDialog.setHint("ΑΦΜ εργαζομένου");

                //Specify the type of input expected.
                inputTextDialog.setInputType(InputType.TYPE_CLASS_NUMBER);
                alertDialogBuilder.setView(inputTextDialog);

                //Set up the buttons.
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try{
                            int newWorkingEmployeeAfm = Integer.parseInt(inputTextDialog.getText().toString().trim());

                            //Just some variables declarations
                            Context myContext = getApplicationContext();
                            int toastDuration = Toast.LENGTH_LONG;
                            Toast myToast;
                            View myView;
                            TextView myToastTextView;

                            //params is the dynamic array of those parameters which will be used to send data to our online database.
                            List<NameValuePair> params1 = new ArrayList<NameValuePair>();

                            params1.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));
                            params1.add(new BasicNameValuePair("afm_employee", String.valueOf(newWorkingEmployeeAfm)));

                            //Configure necessary params to access to online database.
                            params1.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                            params1.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                            params1.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                            params1.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                            //Creating a new JSONParser instance (object) which is responsible from the communication process.
                            JSONParser jsonParser = new JSONParser();
                            //Executing HTTP POST Request, with the given parameters, so as to successfully add a new working employee to the business staff.
                            JSONObject jsonObject = jsonParser.makeHttpRequest(url_add_working_employee, "POST", params1);

                            int successFlag = jsonObject.getInt("success");

                            if(successFlag == 1){

                                myToast = Toast.makeText(myContext, "Επιτυχής προσθήκη νέου εργαζομένου", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter.
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be edited.
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.black));

                                myToast.show();
                            }else{
                                final AlertDialog.Builder addEmployeeErrorAlert = new AlertDialog.Builder(BusinessActivity.this);
                                addEmployeeErrorAlert.setTitle("Αποτυχία προσθήκης εργαζομένου");
                                addEmployeeErrorAlert.setMessage("Δεν βρήκαμε αυτό το ΑΦΜ εργαζομένου στη Βάση Δεδομένων μας.\nΠροσέξτε, ότι για να προσθέσετε έναν εργαζόμενο στο εργατικό δυναμικό της επιχείρησής σας, πρέπει πρώτα εκείνος να έχει εγγραφεί στην αντίστοιχη περιοχή της εφαρμογής.");
                                addEmployeeErrorAlert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                addEmployeeErrorAlert.show();
                            }
                        }catch (Exception e1){
                            final AlertDialog.Builder addEmployeeErrorAlert = new AlertDialog.Builder(BusinessActivity.this);
                            addEmployeeErrorAlert.setTitle("Αποτυχία προσθήκης εργαζομένου");
                            addEmployeeErrorAlert.setMessage("Δεν βρήκαμε αυτό το ΑΦΜ εργαζομένου στη Βάση Δεδομένων μας.\nΠροσέξτε, ότι για να προσθέσετε έναν εργαζόμενο στο εργατικό δυναμικό της επιχείρησής σας, πρέπει πρώτα εκείνος να έχει εγγραφεί στην αντίστοιχη περιοχή της εφαρμογής.");
                            addEmployeeErrorAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            addEmployeeErrorAlert.show();

                            e1.printStackTrace();
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("ΑΚΥΡΩΣΗ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialogBuilder.show();
            }
        });

        //Here follows the process for removing an employee from the current business staff.
        removeWorkingEmployee = (Button) findViewById(R.id.removeWorkingEmployee);
        removeWorkingEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setTitle("Αφαίρεση εργαζομένου");
                alertDialogBuilder.setMessage("(Πλήθος χαρακτήρων: 9)");

                //Set up the input.
                final EditText inputTextDialog = new EditText(view.getContext());

                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(9);
                inputTextDialog.setFilters(FilterArray);

                inputTextDialog.setHint("ΑΦΜ εργαζομένου");

                //Specify the type of input expected.
                inputTextDialog.setInputType(InputType.TYPE_CLASS_NUMBER);
                alertDialogBuilder.setView(inputTextDialog);

                //Set up the buttons.
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Just some variables declarations
                        Context myContext = getApplicationContext();
                        int toastDuration = Toast.LENGTH_LONG;
                        Toast myToast;
                        View myView;
                        TextView myToastTextView;

                        try{
                            boolean isWorkingEmployee = false;

                            //To successfully remove a working employee from the current business staff, then this working employee must first be indeed a working employee for that particular business.
                            //So, we get all working employees from working_employees table (located in our database server) to compare.
                            //Creating a new JSONParser instance (object) which is responsible from the communication process.
                            JSONParser jsonParser1 = new JSONParser();

                            //params is the dynamic array of those parameters which will be used to send data to our online database.
                            List<NameValuePair> params2 = new ArrayList<NameValuePair>();

                            //Configure necessary params to access to online database.
                            params2.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                            params2.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                            params2.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                            params2.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                            //Executing HTTP GET Request, without any parameter, so as to get all currently working employees.
                            JSONObject jsonObject1 = jsonParser1.makeHttpRequest(url_get_working_employees, "GET", params2);

                            int successFlag1 = jsonObject1.getInt("success");
                            if(successFlag1 == 1){
                                JSONArray workingArray = jsonObject1.getJSONArray("working_employees");

                                //Get all currently working employees.
                                ArrayList<Integer> workingEmployeesAfmList = new ArrayList<Integer>();
                                ArrayList<Integer> businessesAfmList = new ArrayList<Integer>();

                                for (int i = 0; i < workingArray.length(); i++) {
                                    workingEmployeesAfmList.add(workingArray.getJSONObject(i).getInt("afm_employee"));
                                    businessesAfmList.add(workingArray.getJSONObject(i).getInt("afm_business"));
                                }

                                //Figure out if given AFM (of the employee that the business wants to remove) indeed corresponds to already successfully registered working employee.
                                int newWorkingEmployeeAfm = Integer.parseInt(inputTextDialog.getText().toString().trim());
                                for(int j=0; j<workingEmployeesAfmList.size(); j++){
                                    if(workingEmployeesAfmList.get(j) == newWorkingEmployeeAfm){
                                        isWorkingEmployee = true;
                                        break;
                                    }
                                }

                                if(isWorkingEmployee) {
                                    //Delete working employee.

                                    //params is the dynamic array of those parameters which will be used to send data to our online database.
                                    List<NameValuePair> params3 = new ArrayList<NameValuePair>();

                                    params3.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));
                                    params3.add(new BasicNameValuePair("afm_employee", String.valueOf(newWorkingEmployeeAfm)));

                                    //Using the necessary params to access to online database.
                                    params3.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                                    params3.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                                    params3.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                                    params3.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                                    //Creating a new JSONParser instance (object) which is responsible from the communication process.
                                    JSONParser jsonParser2 = new JSONParser();
                                    //Executing HTTP POST Request, with the given parameters, so as to successfully remove working employee from the business staff.
                                    JSONObject jsonObject2 = jsonParser2.makeHttpRequest(url_remove_working_employee, "POST", params3);

                                    int successFlag2 = jsonObject2.getInt("success");

                                    if (successFlag2 == 1) {

                                        myToast = Toast.makeText(myContext, "Επιτυχής αφαίρεση εργαζομένου", toastDuration);
                                        myView = myToast.getView();

                                        //Gets the actual oval background of the Toast then sets the colour filter.
                                        myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                                        //Gets the TextView from the Toast so it can be edited.
                                        myToastTextView = myView.findViewById(android.R.id.message);
                                        myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.black));

                                        myToast.show();
                                    } else {
                                        myToast = Toast.makeText(myContext, "Αποτυχία αφαίρεσης εργαζομένου", toastDuration);
                                        myView = myToast.getView();

                                        //Gets the actual oval background of the Toast then sets the colour filter.
                                        myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                        //Gets the TextView from the Toast so it can be edited.
                                        myToastTextView = myView.findViewById(android.R.id.message);
                                        myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                                        myToast.show();
                                    }
                                }else{
                                    myToast = Toast.makeText(myContext, "Αποτυχία αφαίρεσης εργαζομένου", toastDuration);
                                    myView = myToast.getView();

                                    //Gets the actual oval background of the Toast then sets the colour filter.
                                    myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                    //Gets the TextView from the Toast so it can be edited.
                                    myToastTextView = myView.findViewById(android.R.id.message);
                                    myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                                    myToast.show();
                                }
                            }else{
                                myToast = Toast.makeText(myContext, "Αποτυχία αφαίρεσης εργαζομένου", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter.
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be edited.
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                                myToast.show();
                            }

                        }catch (Exception e2){
                            final AlertDialog.Builder addEmployeeErrorAlert = new AlertDialog.Builder(BusinessActivity.this);
                            addEmployeeErrorAlert.setTitle("Αποτυχία αφαίρεσης εργαζομένου");
                            addEmployeeErrorAlert.setMessage("Δεν βρήκαμε αυτό το ΑΦΜ εργαζομένου στη Βάση Δεδομένων μας.\nΠροσέξτε, ότι για να αφαιρέσετε έναν εργαζόμενο θα πρέπει πρώτα αυτός να ανήκει στο εργατικό δυναμικό της επιχείρησής σας.\n(Δηλαδή, θα πρέπει πρώτα:\n1. να έχει εγγραφεί εκείνος στην αντίστοιχη περιοχή της εφαρμογής,\n2. να τον έχετε προσθέσει και εσείς ως εργαζόμενο στην επιχείρηση σας).");
                            addEmployeeErrorAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            addEmployeeErrorAlert.show();

                            e2.printStackTrace();
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("ΑΚΥΡΩΣΗ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialogBuilder.show();
            }
        });

        //Here follows the process for altering (editing) the number of available tables.
        alterNumberTableButton = (Button) findViewById(R.id.alterNumberTablesButton1);
        alterNumberTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View finalView = view;

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setTitle("Αλλαγή αριθμού τραπεζιών καταστήματος");
                alertDialogBuilder.setMessage("Τρέχων αριθμός τραπεζιών: "+business.getNumberTables());

                //Set up the input.
                final EditText inputTextDialog = new EditText(view.getContext());

                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(3);
                inputTextDialog.setFilters(FilterArray);

                inputTextDialog.setHint("Νέος αριθμός τραπεζιών");

                //Specify the type of input expected.
                inputTextDialog.setInputType(InputType.TYPE_CLASS_TEXT);
                alertDialogBuilder.setView(inputTextDialog);

                //Set up the buttons.
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Just some more variables declarations.
                        Context myContext = getApplicationContext();
                        int toastDuration = Toast.LENGTH_LONG;
                        Toast myToast;
                        View myView;
                        TextView myToastTextView;

                        try{
                            int newNumberTables = Integer.parseInt(inputTextDialog.getText().toString().trim());

                            //params
                            List<NameValuePair> params4 = new ArrayList<NameValuePair>();

                            params4.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));
                            params4.add(new BasicNameValuePair("number_tables", String.valueOf(newNumberTables)));

                            //Using the necessary params to access to online database.
                            params4.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                            params4.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                            params4.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                            params4.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                            //Creating a new JSONParser instance (object) which is responsible from the communication process.
                            JSONParser jsonParser = new JSONParser();
                            //Executing HTTP POST Request, with the given parameters, so as to successfully alter (edit) the number of the available tables.
                            JSONObject jsonObject = jsonParser.makeHttpRequest(url_alter_number_tables, "POST", params4);

                            int successFlag = jsonObject.getInt("success");

                            if(successFlag == 1){
                                business.setNumberTables(newNumberTables);

                                myToast = Toast.makeText(myContext, "Επιτυχής αλλαγή αριθμού τραπεζιών", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter.
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be edited.
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.black));

                                myToast.show();
                            }else{
                                myToast = Toast.makeText(myContext, "Αποτυχία αλλαγής αριθμού τραπεζιών", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter.
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be edited.
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                                myToast.show();
                            }
                        }catch (Exception e3){
                            myToast =  Toast.makeText(finalView.getContext(),"Συμπληρώστε πρώτα τα κατάλληλα πεδία και προσπαθήστε ξανά",Toast.LENGTH_LONG);
                            View myToastView = myToast.getView();

                            //Gets the actual oval background of the Toast then sets the colour filter.
                            myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                            //Gets the TextView from the Toast so it can be edited.
                            myToastTextView = myToastView.findViewById(android.R.id.message);
                            myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                            myToast.show();

                            e3.printStackTrace();
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("ΑΚΥΡΩΣΗ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialogBuilder.show();
            }
        });
    }

    /**
     * The following function creates the menu list of this particular activity.
     *
     * Here, the menu list contains only 1 item, which is the a button in order for the business to access the business account (through BusinessAccountActivity class).
     *
     * @param menu the menu given, which corresponds to the menu created inside menu_business_account xml file.
     * @return super.onCreateOptionsMenu(menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_business_account, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * The following function is used to identify the item selected from the menu described above.
     *
     * @param item the item selected.
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.displayBusinessAccount:
                Intent intent = new Intent(BusinessActivity.this, BusinessAccountActivity.class);
                intent.putExtra("business", business);
                startActivity(intent);

                finish();
                break;
        }
        return true;
    }

    /**
     * The commands inside the following method are executed when the back button is pressed.
     * Here, the user will be recommended to press the back button twice if he/she wants to go back to the previous activity.
     */
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(BusinessActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }

        this.doubleBackToExitPressedOnce = true;


        Toast myToast = Toast.makeText(this, "Πατήστε ξανά για έξοδο από την εφαρμογή", Toast.LENGTH_SHORT);
        View myToastView = myToast.getView();

        //Gets the actual oval background of the Toast then sets the colour filter
        myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.yellowDark), PorterDuff.Mode.SRC_IN);

        //Gets the TextView from the Toast so it can be editted
        TextView myTextView = myToastView.findViewById(android.R.id.message);
        myTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

        myToast.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }

    /**
     * Whenever the activity is destroyed (finished/closed) then the following method will be called.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}