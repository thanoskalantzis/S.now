package project.snow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * BusinessAccountActivity Class.
 *
 * This class is actually responsible showing, editing and even deleting a business account.
 * Here, each business is able to:
 * 1. Change name,
 * 2. Change address,
 * 3. Change postal code,
 * 4. Change email address,
 * 5. Change phone,
 * 6. Change password,
 * 7. Delete the entire business account (and of course all business data linked to this particular account - such as, business products, business orders, business employees).
 *
 * @author thanoskalantzis
 */
public class BusinessAccountActivity extends AppCompatActivity{
    //Class variables.
    //url_edit_business_account variable is actually the url which corresponds to the php file for controlling, modifying (editing) and even deleting a business account.
    private String url_edit_business_account;
    private static Business business;
    private TextView idName;
    private TextView afm;
    private TextView address;
    private TextView postal;
    private TextView email;
    private TextView phone;
    private Button changeName;
    private Button changeAddress;
    private Button changePostal;
    private Button changeEmail;
    private Button changePassword;
    private Button changePhone;
    private Button deleteBusiness;

    /**
     * The following method is essential and necessary due to the fact that BusinessAccountActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_account);

        //Initialize url_edit_business_account.
        url_edit_business_account = getString(R.string.BASE_URL).concat("/connect/editbusinessaccount.php");

        //Getting parsed data from the previously active activity.
        business = (Business) getIntent().getSerializableExtra("business");

        //Setting the title of the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Λογαρισμός Επιχείρησης");
        }

        idName = findViewById(R.id.declarationIdName);
        idName.setText(business.getBusinessIdName());

        afm = findViewById(R.id.declarationAfmBusiness);
        afm.setText(String.valueOf(business.getAfmBusiness()));

        address = findViewById(R.id.declarationAddressBusiness);
        address.setText(business.getAddress());

        postal = findViewById(R.id.declarationPostalBusiness);
        postal.setText(String.valueOf(business.getPostalCode()));

        email = findViewById(R.id.declarationEmailBusiness);
        email.setText(business.getEmail());

        phone = findViewById(R.id.declarationPhoneBusiness);
        phone.setText(String.valueOf(business.getPhone()));

        //Here follows the process for changing business password.
        changePassword = (Button) findViewById(R.id.changePasswordBusiness);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Αλλαγή Κωδικού Λογαριασμού");
                alertDialogBuilder.setMessage("Ο τρέχον κωδικός που θα πληκτρολογήσετε πρέπει να είναι ο κωδικός που χρησιμοποιείται αυτή τη περίοδο\n");

                //Set up the input.
                final EditText oldPassword = new EditText(context);

                InputFilter[] FilterArrayA = new InputFilter[1];
                FilterArrayA[0] = new InputFilter.LengthFilter(15);
                oldPassword.setFilters(FilterArrayA);

                oldPassword.setHint("Εισάγετε τρέχων κωδικό");

                //Specify the type of input expected.
                oldPassword.setInputType(InputType.TYPE_CLASS_TEXT);

                layout.addView(oldPassword);

                final EditText newPassword = new EditText(context);

                InputFilter[] FilterArrayΒ = new InputFilter[1];
                FilterArrayΒ[0] = new InputFilter.LengthFilter(15);
                newPassword.setFilters(FilterArrayΒ);

                newPassword.setHint("Εισάγετε νέο κωδικό");

                //Specify the type of input expected.
                newPassword.setInputType(InputType.TYPE_CLASS_TEXT);

                layout.addView(newPassword);

                alertDialogBuilder.setView(layout);

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

                        //Getting old password from input.
                        String oldPasswordString = oldPassword.getText().toString();
                        //Getting new password from input.
                        String newPasswordString = newPassword.getText().toString();

                        //In order for a business to successfully change password, then it must also provide correctly the old password first.
                        if(oldPasswordString.equals(business.getPassword())){
                            if(passwordIsValid(newPasswordString)){
                                //params is the dynamic array of those parameters which will be used to send data to our online database.
                                List<NameValuePair> params = new ArrayList<NameValuePair>();

                                //In this situation, if case==1 then we actually declare to the corresponding php file that we are trying to change the password of the corresponding business.
                                params.add(new BasicNameValuePair("case", "1"));
                                params.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));
                                params.add(new BasicNameValuePair("new_password", newPasswordString));

                                //Using the necessary params to access to online database.
                                params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                                params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                                params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                                params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                                //Creating a new JSONParser instance (object) which is responsible from the communication process.
                                JSONParser jsonParser = new JSONParser();
                                //Executing HTTP POST Request, with the given parameters, so as to successfully change password of the corresponding business.
                                JSONObject jsonObject = jsonParser.makeHttpRequest(url_edit_business_account, "POST", params);

                                try {
                                    int successFlag = jsonObject.getInt("success");

                                    if(successFlag == 1){
                                        business.setPassword(newPasswordString);

                                        myToast = Toast.makeText(myContext, "Ο κωδικός άλλαξε επιτυχώς", toastDuration);
                                        myView = myToast.getView();

                                        //Gets the actual oval background of the Toast then sets the colour filter.
                                        myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                                        //Gets the TextView from the Toast so it can be edited.
                                        myToastTextView = myView.findViewById(android.R.id.message);
                                        myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.black));

                                        myToast.show();

                                        Intent intent = new Intent(BusinessAccountActivity.this, BusinessAccountActivity.class);
                                        intent.putExtra("business", business);
                                        startActivity(intent);

                                        finish();
                                    }else{
                                        myToast = Toast.makeText(myContext, "Αποτυχία αλλαγής κωδικού", toastDuration);
                                        myView = myToast.getView();

                                        //Gets the actual oval background of the Toast then sets the colour filter.
                                        myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                        //Gets the TextView from the Toast so it can be edited.
                                        myToastTextView = myView.findViewById(android.R.id.message);
                                        myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                                        myToast.show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else{
                                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BusinessAccountActivity.this);
                                alertDialogBuilder.setTitle("Λανθασμένη μορφή κωδικού");
                                alertDialogBuilder.setMessage("Ο κωδικός που θα επιλέξετε:\n1. Πρέπει να αποτελείται από 6 έως 15 χαρακτήρες\n2. Δεν πρέπει να περιλαμβάνει κενά\nΠρέπει να περιλαμβάνει:\n3. Τουλάχιστον 1 μικρό γράμμα a-z\n4. Τουλάχιστον 1 αριθμό 0-9\n5. Τουλάχιστον 1 ειδικό χαρακτήρα (@, #, %, &, !, $, ...)");

                                //Set up the buttons.
                                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                alertDialogBuilder.show();
                            }
                        }else{
                            myToast = Toast.makeText(myContext, "Λανθασμένος τρέχων κωδικός", toastDuration);
                            myView = myToast.getView();

                            //Gets the actual oval background of the Toast then sets the colour filter
                            myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                            //Gets the TextView from the Toast so it can be editted
                            myToastTextView = myView.findViewById(android.R.id.message);
                            myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                            myToast.show();
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

        //Here follows the process for changing business name.
        changeName = (Button) findViewById(R.id.editBusinessName);
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setTitle("Αλλαγή ονόματος επιχείρησης");
                alertDialogBuilder.setMessage("Πληκτρολογήστε το νέο όνομα της επιχείρησής σας");

                //Set up the input.
                final EditText inputTextDialog = new EditText(view.getContext());

                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(100);
                inputTextDialog.setFilters(FilterArray);

                inputTextDialog.setHint("Νέο όνομα");

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

                        //Getting the new business name from input.
                        String newIdName = inputTextDialog.getText().toString();

                        //params is the dynamic array of those parameters which will be used to send data to our online database.
                        List<NameValuePair> params = new ArrayList<NameValuePair>();

                        //In this situation, if case==2 then we actually declare to the corresponding php file that we are trying to change the name of the corresponding business.
                        params.add(new BasicNameValuePair("case", "2"));
                        params.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));
                        params.add(new BasicNameValuePair("new_id_name", newIdName));

                        //Using the necessary params to access to online database.
                        params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                        params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                        params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                        params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                        //Creating a new JSONParser instance (object) which is responsible from the communication process.
                        JSONParser jsonParser = new JSONParser();
                        //Executing HTTP POST Request, with the given parameters, so as to successfully change name of the corresponding business.
                        JSONObject jsonObject = jsonParser.makeHttpRequest(url_edit_business_account, "POST", params);

                        try {
                            int successFlag = jsonObject.getInt("success");

                            if(successFlag == 1){
                                business.setBusinessIdName(newIdName);

                                myToast = Toast.makeText(myContext, "Το όνομα της επιχείρησής σας άλλαξε επιτυχώς", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter.
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be edited.
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.black));

                                myToast.show();

                                //The following is actually used just to reload the screen after the change that took placed.
                                Intent intent = new Intent(BusinessAccountActivity.this, BusinessAccountActivity.class);
                                intent.putExtra("business", business);
                                startActivity(intent);

                                finish();
                            }else{
                                myToast = Toast.makeText(myContext, "Αποτυχία αλλαγής ονόματος επιχείρησης", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter.
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be edited.
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                                myToast.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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

        //Here follows the process for changing business address.
        changeAddress = (Button) findViewById(R.id.editBusinessAddress);
        changeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setTitle("Αλλαγή διεύθυνσης επιχείρησης");
                alertDialogBuilder.setMessage("(Προτεινόμενη μορφή: Οδός Αριθμός, Πόλη)");

                //Set up the input.
                final EditText inputTextDialog = new EditText(view.getContext());

                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(150);
                inputTextDialog.setFilters(FilterArray);

                inputTextDialog.setHint("Νέα διεύθυνση");

                //Specify the type of input expected.
                inputTextDialog.setInputType(InputType.TYPE_CLASS_TEXT);
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

                        //Getting new business address from input.
                        String newAddress = inputTextDialog.getText().toString();

                        //params is the dynamic array of those parameters which will be used to send data to our online database.
                        List<NameValuePair> params = new ArrayList<NameValuePair>();

                        //In this situation, if case==3 then we actually declare to the corresponding php file that we are trying to change the current address of the corresponding business.
                        params.add(new BasicNameValuePair("case", "3"));
                        params.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));
                        params.add(new BasicNameValuePair("new_address", newAddress));

                        //Using the necessary params to access to online database.
                        params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                        params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                        params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                        params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                        //Creating a new JSONParser instance (object) which is responsible from the communication process.
                        JSONParser jsonParser = new JSONParser();
                        //Executing HTTP POST Request, with the given parameters, so as to successfully change the current address of the corresponding business.
                        JSONObject jsonObject = jsonParser.makeHttpRequest(url_edit_business_account, "POST", params);

                        try {
                            int successFlag = jsonObject.getInt("success");

                            if(successFlag == 1){
                                business.setAddress(newAddress);

                                myToast = Toast.makeText(myContext, "Η διεύθυνση της επιχείρησής σας άλλαξε επιτυχώς", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter.
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be edited.
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.black));

                                myToast.show();

                                //The following is actually used just to reload the screen after the change that took placed.
                                Intent intent = new Intent(BusinessAccountActivity.this, BusinessAccountActivity.class);
                                intent.putExtra("business", business);
                                startActivity(intent);

                                finish();
                            }else{
                                myToast = Toast.makeText(myContext, "Αποτυχία αλλαγής διεύθυνσης επιχείρησης", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter.
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be edited.
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                                myToast.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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

        //Here follows the process for changing business postal code.
        changePostal = (Button) findViewById(R.id.editBusinessPostal);
        changePostal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setTitle("Αλλαγή ταχυδρομικού κώδικα επιχείρησης");
                alertDialogBuilder.setMessage("(Πλήθος χαρακτήρων: 5)");

                //Set up the input.
                final EditText inputTextDialog = new EditText(view.getContext());

                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(5);
                inputTextDialog.setFilters(FilterArray);

                inputTextDialog.setHint("Νέος ταχυδρομικός κώδικας");

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

                        //Getting new postal code from input.
                        String newPostalCodeString = inputTextDialog.getText().toString().trim();

                        if(newPostalCodeString.length()==5){
                            int newPostalCode = Integer.parseInt(newPostalCodeString);

                            //params is the dynamic array of those parameters which will be used to send data to our online database.
                            List<NameValuePair> params = new ArrayList<NameValuePair>();

                            //In this situation, if case==4 then we actually declare to the corresponding php file that we are trying to change the current postal code of this business.
                            params.add(new BasicNameValuePair("case", "4"));
                            params.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));
                            params.add(new BasicNameValuePair("new_postal", String.valueOf(newPostalCode)));

                            //Using the necessary params to access to online database.
                            params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                            params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                            params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                            params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                            //Creating a new JSONParser instance (object) which is responsible from the communication process.
                            JSONParser jsonParser = new JSONParser();
                            //Executing HTTP POST Request, with the given parameters, so as to successfully change the current postal code of the corresponding business.
                            JSONObject jsonObject = jsonParser.makeHttpRequest(url_edit_business_account, "POST", params);

                            try {
                                int successFlag = jsonObject.getInt("success");

                                if(successFlag == 1){
                                    business.setPostalCode(newPostalCode);

                                    myToast = Toast.makeText(myContext, "Ο ταχυδρομικός κώδικας της επιχείρησής σας άλλαξε επιτυχώς", toastDuration);
                                    myView = myToast.getView();

                                    //Gets the actual oval background of the Toast then sets the colour filter.
                                    myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                                    //Gets the TextView from the Toast so it can be edited.
                                    myToastTextView = myView.findViewById(android.R.id.message);
                                    myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.black));

                                    myToast.show();

                                    //The following is actually used just to reload the screen after the change that took placed.
                                    Intent intent = new Intent(BusinessAccountActivity.this, BusinessAccountActivity.class);
                                    intent.putExtra("business", business);
                                    startActivity(intent);

                                    finish();
                                }else{
                                    myToast = Toast.makeText(myContext, "Αποτυχία αλλαγής ταχυδρομικού κώδικα επιχείρησης", toastDuration);
                                    myView = myToast.getView();

                                    //Gets the actual oval background of the Toast then sets the colour filter.
                                    myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                    //Gets the TextView from the Toast so it can be edited.
                                    myToastTextView = myView.findViewById(android.R.id.message);
                                    myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                                    myToast.show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{
                            myToast = Toast.makeText(myContext, "Ο ταχυδρομικός κώδικας πρέπει να έχει μήκος 5 χαρακτήρων", toastDuration);
                            myView = myToast.getView();

                            //Gets the actual oval background of the Toast then sets the colour filter.
                            myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                            //Gets the TextView from the Toast so it can be edited.
                            myToastTextView = myView.findViewById(android.R.id.message);
                            myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                            myToast.show();
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

        //Here follows the process for changing business email.
        changeEmail = (Button) findViewById(R.id.editBusinessEmail);
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setTitle("Αλλαγή email επιχείρησης");
                alertDialogBuilder.setMessage("Πληκτρολογήστε το νέο email της επιχείρησής σας");

                //Set up the input.
                final EditText inputTextDialog = new EditText(view.getContext());

                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(320);
                inputTextDialog.setFilters(FilterArray);

                inputTextDialog.setHint("Νέο email");

                //Specify the type of input expected.
                inputTextDialog.setInputType(InputType.TYPE_CLASS_TEXT);
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

                        String newEmailAddress = inputTextDialog.getText().toString().trim();

                        if(emailIsValid(newEmailAddress)){
                            //params is the dynamic array of those parameters which will be used to send data to our online database.
                            List<NameValuePair> params = new ArrayList<NameValuePair>();

                            //In this situation, if case==6 then we actually declare to the corresponding php file that we are trying to change the current email address of this business
                            params.add(new BasicNameValuePair("case", "6"));
                            params.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));
                            params.add(new BasicNameValuePair("new_email", newEmailAddress));

                            //Using the necessary params to access to online database.
                            params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                            params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                            params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                            params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                            //Creating a new JSONParser instance (object) which is responsible from the communication process.
                            JSONParser jsonParser = new JSONParser();
                            //Executing HTTP POST Request, with the given parameters, so as to successfully change the email of the corresponding business.
                            JSONObject jsonObject = jsonParser.makeHttpRequest(url_edit_business_account, "POST", params);

                            try {
                                int successFlag = jsonObject.getInt("success");

                                if(successFlag == 1){
                                    business.setEmail(newEmailAddress);

                                    myToast = Toast.makeText(myContext, "Το email της επιχείρησής σας άλλαξε επιτυχώς", toastDuration);
                                    myView = myToast.getView();

                                    //Gets the actual oval background of the Toast then sets the colour filter.
                                    myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                                    //Gets the TextView from the Toast so it can be edited.
                                    myToastTextView = myView.findViewById(android.R.id.message);
                                    myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.black));

                                    myToast.show();

                                    //The following is actually used just to reload the screen after the change that took placed.
                                    Intent intent = new Intent(BusinessAccountActivity.this, BusinessAccountActivity.class);
                                    intent.putExtra("business", business);
                                    startActivity(intent);

                                    finish();
                                }else{
                                    myToast = Toast.makeText(myContext, "Αποτυχία αλλαγής email επιχείρησης", toastDuration);
                                    myView = myToast.getView();

                                    //Gets the actual oval background of the Toast then sets the colour filter.
                                    myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                    //Gets the TextView from the Toast so it can be edited.
                                    myToastTextView = myView.findViewById(android.R.id.message);
                                    myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                                    myToast.show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{
                            myToast = Toast.makeText(myContext, "Έχετε δώσει email σε μη αποδεκτή μορφή!", toastDuration);
                            myView = myToast.getView();

                            //Gets the actual oval background of the Toast then sets the colour filter.
                            myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                            //Gets the TextView from the Toast so it can be edited.
                            myToastTextView = myView.findViewById(android.R.id.message);
                            myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                            myToast.show();
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

        //Here follows the process for changing business phone.
        changePhone = (Button) findViewById(R.id.editBusinessPhone);
        changePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setTitle("Αλλαγή αριθμού τηλεφώνου επιχείρησης");
                alertDialogBuilder.setMessage("(Πλήθος χαρακτήρων: 10)");

                //Set up the input.
                final EditText inputTextDialog = new EditText(view.getContext());

                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(10);
                inputTextDialog.setFilters(FilterArray);

                inputTextDialog.setHint("Νέος αριθμός τηλεφώνου");

                //Specify the type of input expected.
                inputTextDialog.setInputType(InputType.TYPE_CLASS_NUMBER);
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

                        //Getting new phone from input.
                        String newPhoneNumberString = inputTextDialog.getText().toString().trim();

                        if(newPhoneNumberString.length()==10){
                            long newPhoneNumber = (long) Long.parseLong(newPhoneNumberString);

                            //params is the dynamic array of those parameters which will be used to send data to our online database.
                            List<NameValuePair> params = new ArrayList<NameValuePair>();

                            //In this situation, if case==5 then we actually declare to the corresponding php file that we are trying to change the current phone number of this business
                            params.add(new BasicNameValuePair("case", "5"));
                            params.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));
                            params.add(new BasicNameValuePair("new_phone", String.valueOf(newPhoneNumber)));

                            //Using the necessary params to access online database.
                            params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                            params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                            params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                            params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                            //Creating a new JSONParser instance (object) which is responsible from the communication process.
                            JSONParser jsonParser = new JSONParser();
                            //Executing HTTP POST Request, with the given parameters, so as to successfully change the phone of the corresponding business.
                            JSONObject jsonObject = jsonParser.makeHttpRequest(url_edit_business_account, "POST", params);

                            try {
                                int successFlag = jsonObject.getInt("success");

                                if(successFlag == 1){
                                    business.setPhone(newPhoneNumber);

                                    myToast = Toast.makeText(myContext, "Το τηλέφωνο της επιχείρησής σας άλλαξε επιτυχώς", toastDuration);
                                    myView = myToast.getView();

                                    //Gets the actual oval background of the Toast then sets the colour filter.
                                    myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                                    //Gets the TextView from the Toast so it can be edited.
                                    myToastTextView = myView.findViewById(android.R.id.message);
                                    myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.black));

                                    myToast.show();

                                    //The following is actually used just to reload the screen after the change that took placed.
                                    Intent intent = new Intent(BusinessAccountActivity.this, BusinessAccountActivity.class);
                                    intent.putExtra("business", business);
                                    startActivity(intent);

                                    finish();
                                }else{
                                    myToast = Toast.makeText(myContext, "Αποτυχία αλλαγής αριθμού τηλεφώνου επιχείρησης", toastDuration);
                                    myView = myToast.getView();

                                    //Gets the actual oval background of the Toast then sets the colour filter.
                                    myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                    //Gets the TextView from the Toast so it can be edited.
                                    myToastTextView = myView.findViewById(android.R.id.message);
                                    myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                                    myToast.show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{
                            myToast = Toast.makeText(myContext, "Ο αριθμός τηλεφώνου πρέπει να έχει μήκος 10 χαρακτήρων", toastDuration);
                            myView = myToast.getView();

                            //Gets the actual oval background of the Toast then sets the colour filter.
                            myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                            //Gets the TextView from the Toast so it can be edited.
                            myToastTextView = myView.findViewById(android.R.id.message);
                            myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                            myToast.show();
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

        //Here follows the process for deleting the entire business account.
        deleteBusiness = (Button) findViewById(R.id.deleteEntireBusiness);
        deleteBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Διαγραφή Λογαριασμού");
                alertDialogBuilder.setMessage("Προσοχή, ο λογαριασμός σας θα διαγραφεί ολοκληρωτικά!\n\nΣυνέχεια διαγραφής;\n");

                //Set up the buttons.
                alertDialogBuilder.setPositiveButton("NAI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Just some variables declarations
                        Context myContext = getApplicationContext();
                        int toastDuration = Toast.LENGTH_LONG;
                        Toast myToast;
                        View myView;
                        TextView myToastTextView;

                        //params is the dynamic array of those parameters which will be used to send data to our online database.
                        List<NameValuePair> params = new ArrayList<NameValuePair>();

                        //In this situation, if case==7 then we actually declare to the corresponding php file that we are trying to delete the entire account of the corresponding business.
                        params.add(new BasicNameValuePair("case", "7"));
                        params.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));

                        //Using the necessary params to access online database.
                        params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                        params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                        params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                        params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                        //Creating a new JSONParser instance (object) which is responsible from the communication process.
                        JSONParser jsonParser = new JSONParser();
                        //Executing HTTP POST Request, with the given parameters, so as to successfully delete the entire corresponding business.
                        JSONObject jsonObject = jsonParser.makeHttpRequest(url_edit_business_account, "POST", params);

                        try {
                            int successFlag = jsonObject.getInt("success");

                            if(successFlag == 1){
                                myToast = Toast.makeText(myContext, "Ο λογαριασμός σας διαγράφηκε επιτυχώς\nΛυπούμαστε που αποχωρείτε...", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter.
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.yellowDark), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be edited.
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.black));

                                myToast.show();

                                //The following is used so as to terminate/finish/close the current activity and exit this program flow by going back to the Main Activity.
                                Intent intent = new Intent(BusinessAccountActivity.this, MainActivity.class);
                                startActivity(intent);

                                finish();
                            }else{
                                myToast = Toast.makeText(myContext, "Αποτυχία διαγραφής λογαριασμού\nΠαρακαλούμε επικοινωνήστε μαζί μας!", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter.
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be edited.
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                                myToast.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
     * A function to check email format.
     *
     * @param emailToCheck is the given email to check.
     * @return true if the given email is valid, or false otherwise.
     */
    private static boolean emailIsValid(String emailToCheck) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return emailToCheck.matches(regex);
    }

    /**
     * A function to check password format.
     *
     * @param passwordToCheck the password given to check.
     * @return true if password is valid, or false otherwise.
     */
    public static boolean passwordIsValid(String passwordToCheck) {
        //Check if password length is between 6 and 15 characters.
        if ( !( (passwordToCheck.length() >= 6) && (passwordToCheck.length() <= 15) ) ) {
            return false;
        }

        //Check if password contains space.
        if (passwordToCheck.contains(" ")) {
            return false;
        }

        if (true) {
            boolean containsNumber = false;

            //Check if password contains at least one digit from 0 to 9.
            for (int i = 0; i <= 9; i++) {

                //Convert int to String.
                String str1 = Integer.toString(i);

                if (passwordToCheck.contains(str1)) {
                    containsNumber = true;
                    break;
                }
            }
            if ( !containsNumber ) {
                return false;
            }
        }

        //Check if password contains at least one or more special characters from the list below.
        if (!(passwordToCheck.contains("@") || passwordToCheck.contains("#")
                || passwordToCheck.contains("!") || passwordToCheck.contains("~")
                || passwordToCheck.contains("$") || passwordToCheck.contains("%")
                || passwordToCheck.contains("^") || passwordToCheck.contains("&")
                || passwordToCheck.contains("*") || passwordToCheck.contains("(")
                || passwordToCheck.contains(")") || passwordToCheck.contains("-")
                || passwordToCheck.contains("+") || passwordToCheck.contains("/")
                || passwordToCheck.contains(":") || passwordToCheck.contains(".")
                || passwordToCheck.contains(", ") || passwordToCheck.contains("<")
                || passwordToCheck.contains(">") || passwordToCheck.contains("?")
                || passwordToCheck.contains("|"))) {
            return false;
        }

        /*
        if (true) {
            int count = 0;

            //checking capital letters
            for (int i = 65; i <= 90; i++) {

                //type casting
                char c = (char)i;

                String str1 = Character.toString(c);
                if (passwordToCheck.contains(str1)) {
                    count = 1;
                }
            }
            if (count == 0) {
                return false;
            }
        }
       */

        if (true) {
            boolean containsSmallLetter = false;

            //Checking small letters.
            for (int i = 90; i <= 122; i++) {

                //Type casting.
                char c = (char)i;
                String str1 = Character.toString(c);

                if (passwordToCheck.contains(str1)) {
                    containsSmallLetter = true;
                }
            }
            if ( !containsSmallLetter ) {
                return false;
            }
        }

        //If password is valid then true will be returned.
        return true;
    }

    /**
     * The commands inside the following method are executed when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BusinessAccountActivity.this, BusinessActivity.class);
        intent.putExtra("business", business);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    /**
     * Whenever the activity is destroyed (finished/closed) then the following method will be called.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}