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
 * EmployeeAccountActivity Class.
 *
 * This class is actually responsible showing, editing and even deleting an employee account.
 * Here, each employee is able to:
 * 1. Change password,
 * 7. Delete the entire employee account (and of course all employee data linked to this particular account).
 *
 * @author thanoskalantzis
 */
public class EmployeeAccountActivity extends AppCompatActivity{
    //Class variables.
    //url_edit_employee_account variable is actually the url which corresponds to the php file for controlling, modifying (editing) and even deleting an employee account.
    private String url_edit_employee_account;
    private static ArrayList<Integer> businessesWorkingForList;
    private static Employee currentEmployee;
    private static int afmEmployee;
    //Just some more class variables.
    private TextView firstName;
    private TextView lastName;
    private TextView afmWorkingEmployee;
    private TextView email;
    private TextView phone;
    private Button changePassword;
    private Button deleteAccount;

    /**
     * The following method is essential and necessary due to the fact that EmployeeAccountActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_account);

        //Initialize url_edit_employee_account.
        url_edit_employee_account = getString(R.string.BASE_URL).concat("/connect/editemployeeaccount.php");

        //Getting parsed data from previously active activity.
        businessesWorkingForList = (ArrayList<Integer>) getIntent().getSerializableExtra("businessesWorkingForList");
        currentEmployee = (Employee) getIntent().getSerializableExtra("currentEmployee");
        afmEmployee = (int) getIntent().getSerializableExtra("afm_employee");

        //Setting the title of the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Λογαρισμός Εργαζομένου");
        }

        firstName = findViewById(R.id.employeeAccountFirstName);
        firstName.setText(currentEmployee.getFirstName());

        lastName = findViewById(R.id.employeeAccountLastName);
        lastName.setText(currentEmployee.getLastName());

        afmWorkingEmployee = findViewById(R.id.workingEmployeeAccountAfm);
        afmWorkingEmployee.setText(String.valueOf(currentEmployee.getAfmEmployee()));

        email = findViewById(R.id.employeeAccountEmail);
        email.setText(currentEmployee.getEmail());

        phone = findViewById(R.id.employeeAccountPhone);
        phone.setText(String.valueOf(currentEmployee.getPhone()));

        //Here follows the process of changing password of employee.
        changePassword = (Button) findViewById(R.id.changePasswordEmployee);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Αλλαγή Κωδικού Λογαριασμού");
                alertDialogBuilder.setMessage("Ο τρέχον κωδικός που θα πληκτρολογήσετε πρέπει να είναι ο κωδικός που χρησιμοποιείται αυτή τη περίοδο\n");

                //Set up the input
                final EditText oldPassword = new EditText(context);

                InputFilter[] FilterArrayA = new InputFilter[1];
                FilterArrayA[0] = new InputFilter.LengthFilter(15);
                oldPassword.setFilters(FilterArrayA);

                oldPassword.setHint("Εισάγετε τρέχων κωδικό");

                //Specify the type of input expected
                oldPassword.setInputType(InputType.TYPE_CLASS_TEXT);

                layout.addView(oldPassword);

                final EditText newPassword = new EditText(context);

                InputFilter[] FilterArrayΒ = new InputFilter[1];
                FilterArrayΒ[0] = new InputFilter.LengthFilter(15);
                newPassword.setFilters(FilterArrayΒ);

                newPassword.setHint("Εισάγετε νέο κωδικό");

                //Specify the type of input expected
                newPassword.setInputType(InputType.TYPE_CLASS_TEXT);

                layout.addView(newPassword);

                alertDialogBuilder.setView(layout);

                //Set up the buttons
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Just some variables declarations
                        Context myContext = getApplicationContext();
                        int toastDuration = Toast.LENGTH_LONG;
                        Toast myToast;
                        View myView;
                        TextView myToastTextView;

                        String oldPasswordString = oldPassword.getText().toString();
                        String newPasswordString = newPassword.getText().toString();

                        if(oldPasswordString.equals(currentEmployee.getPassword())){
                            if(passwordIsValid(newPasswordString)){
                                List<NameValuePair> params1 = new ArrayList<NameValuePair>();

                                //In this situation, if case==1 then we actually declare to the corresponding php file that we are trying to change password of this working employee
                                params1.add(new BasicNameValuePair("case", "1"));
                                params1.add(new BasicNameValuePair("afm_employee", String.valueOf(currentEmployee.getAfmEmployee())));
                                params1.add(new BasicNameValuePair("new_password", newPasswordString));

                                //Using the necessary params to access online database.
                                params1.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                                params1.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                                params1.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                                params1.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                                JSONParser jsonParser = new JSONParser();
                                JSONObject jsonObject = jsonParser.makeHttpRequest(url_edit_employee_account, "POST", params1);

                                try {
                                    int successFlag = jsonObject.getInt("success");

                                    if(successFlag == 1){
                                        currentEmployee.setPassword(newPasswordString);

                                        myToast = Toast.makeText(myContext, "Ο κωδικός άλλαξε επιτυχώς", toastDuration);
                                        myView = myToast.getView();

                                        //Gets the actual oval background of the Toast then sets the colour filter
                                        myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                                        //Gets the TextView from the Toast so it can be editted
                                        myToastTextView = myView.findViewById(android.R.id.message);
                                        myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.black));

                                        myToast.show();
                                    }else{
                                        myToast = Toast.makeText(myContext, "Αποτυχία αλλαγής κωδικού", toastDuration);
                                        myView = myToast.getView();

                                        //Gets the actual oval background of the Toast then sets the colour filter
                                        myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                        //Gets the TextView from the Toast so it can be editted
                                        myToastTextView = myView.findViewById(android.R.id.message);
                                        myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                                        myToast.show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else{
                                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EmployeeAccountActivity.this);
                                alertDialogBuilder.setTitle("Λανθασμένη μορφή κωδικού");
                                alertDialogBuilder.setMessage("Ο κωδικός που θα επιλέξετε:\n1. Πρέπει να αποτελείται από 6 έως 15 χαρακτήρες\n2. Δεν πρέπει να περιλαμβάνει κενά\nΠρέπει να περιλαμβάνει:\n3. Τουλάχιστον 1 μικρό γράμμα a-z\n4. Τουλάχιστον 1 αριθμό 0-9\n5. Τουλάχιστον 1 ειδικό χαρακτήρα (@, #, %, &, !, $, ...)");

                                //Set up the buttons
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

        //Here follows the process of entirely deleting employee account.
        deleteAccount = (Button) findViewById(R.id.deleteEmployeeAccount);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Διαγραφή Λογαριασμού");
                alertDialogBuilder.setMessage("Προσοχή, ο λογαριασμός σας θα διαγραφεί ολοκληρωτικά!\n\nΣυνέχεια διαγραφής;\n");

                //Set up the buttons
                alertDialogBuilder.setPositiveButton("NAI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Just some variables declarations
                        Context myContext = getApplicationContext();
                        int toastDuration = Toast.LENGTH_LONG;
                        Toast myToast;
                        View myView;
                        TextView myToastTextView;

                        List<NameValuePair> params2 = new ArrayList<NameValuePair>();

                        //In this situation, if case==2 then we actually declare to the corresponding php file that we are trying to delete the account of this working employee
                        params2.add(new BasicNameValuePair("case", "2"));
                        params2.add(new BasicNameValuePair("afm_employee", String.valueOf(currentEmployee.getAfmEmployee())));

                        //Using the necessary params to access online database.
                        params2.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                        params2.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                        params2.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                        params2.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                        JSONParser jsonParser = new JSONParser();
                        JSONObject jsonObject = jsonParser.makeHttpRequest(url_edit_employee_account, "POST", params2);

                        try {
                            int successFlag = jsonObject.getInt("success");

                            if(successFlag == 1){
                                myToast = Toast.makeText(myContext, "Ο λογαριασμός σας διαγράφηκε επιτυχώς\nΛυπούμαστε που αποχωρείτε...", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.yellowDark), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be editted
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.black));

                                myToast.show();

                                Intent intent = new Intent(EmployeeAccountActivity.this, MainActivity.class);
                                startActivity(intent);

                                finish();
                            }else{
                                myToast = Toast.makeText(myContext, "Αποτυχία διαγραφής λογαριασμού\nΠαρακαλούμε επικοινωνήστε μαζί μας!", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be editted
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

        //check if password contains space
        if (passwordToCheck.contains(" ")) {
            return false;
        }

        if (true) {
            boolean containsNumber = false;

            //check if password contains at least one digit from 0 to 9
            for (int i = 0; i <= 9; i++) {

                // Convert int to String
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

        //check if password contains at least one or more special characters from the list below.
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

            //checking if password contains any small letters
            for (int i = 90; i <= 122; i++) {

                //Type casting
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

        //if password is valid then true will be returned
        return true;
    }

    /**
     * The commands inside the following method are executed when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EmployeeAccountActivity.this, WorkSelectionActivity.class);
        intent.putExtra("businessesWorkingForList", businessesWorkingForList);
        intent.putExtra("currentEmployee", currentEmployee);
        intent.putExtra("afm_employee", afmEmployee);
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