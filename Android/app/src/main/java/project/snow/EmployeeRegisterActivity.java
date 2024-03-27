package project.snow;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * EmployeeRegisterActivity Class.
 *
 * This class is used to control the registration process of a new employee.
 *
 * @author thanoskalantzis
 */
public class EmployeeRegisterActivity extends AppCompatActivity {
    //Class variables.
    //url_add_employee_user variable is actually the url which corresponds to the php file for registering a new employee.
    private String url_add_employee_user;
    private int afm;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private long phone;
    private Button employeeRegisterButton;

    /**
     * The following method is essential and necessary due to the fact that EmployeeRegisterActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_register);

        //Initialize url_add_employee_user.
        url_add_employee_user = getString(R.string.BASE_URL).concat("/connect/addemployeeuser.php");

        //Here follows the process of registering a new employee.
        employeeRegisterButton = (Button) findViewById(R.id.employeeRegisterButton2);
        employeeRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Just some more variables declarations
                Toast myToast;
                View myToastView;
                TextView myTextView;

                try{
                    EditText emailGiven = (EditText) findViewById(R.id.employeeRegisterEmailInput);
                    email = (String) emailGiven.getText().toString().trim();

                    EditText firstNameGiven = (EditText) findViewById(R.id.employeeFirstName);
                    firstName = (String) firstNameGiven.getText().toString().trim();

                    EditText lastNameGiven = (EditText) findViewById(R.id.employeeLastName);
                    lastName = (String) lastNameGiven.getText().toString().trim();

                    EditText afmGiven = (EditText) findViewById(R.id.employeeRegisterAfmInput);
                    afm = (int) Integer.parseInt(afmGiven.getText().toString().trim());

                    EditText passwordGiven = (EditText) findViewById(R.id.employeeRegisterPasswordInput);
                    password = (String) passwordGiven.getText().toString().trim();

                    EditText phoneGiven=(EditText) findViewById(R.id.employeeRegisterPhoneInput);
                    phone = (long) Long.parseLong(phoneGiven.getText().toString().trim());

                    //Of course, we check so as all given values matches some standard formats.
                    //If given values do not match the standard formats that we have set, then the system will prompt employee to correct all mistakes.
                    if( afm>0 && (String.valueOf(afm).length()==9) && firstName.length()>0 && lastName.length()>0 && (phone>0 && String.valueOf(phone).length()==10) ){
                        if(emailIsValid(email)){
                            if(passwordIsValid(password)){
                                newRegistration();
                            }else{
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
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
                            myToast =  Toast.makeText(view.getContext(),"Έχετε δώσει email σε μη αποδεκτή μορφή!",Toast.LENGTH_LONG);
                            myToastView = myToast.getView();

                            //Gets the actual oval background of the Toast then sets the colour filter
                            myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                            //Gets the TextView from the Toast so it can be editted
                            myTextView = myToastView.findViewById(android.R.id.message);
                            myTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                            myToast.show();
                        }
                    }else{
                        myToast =  Toast.makeText(view.getContext(),"Συμπληρώστε πρώτα σωστά τα κατάλληλα πεδία και προσπαθήστε ξανά", Toast.LENGTH_LONG);
                        myToastView = myToast.getView();

                        //Gets the actual oval background of the Toast then sets the colour filter
                        myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                        //Gets the TextView from the Toast so it can be editted
                        myTextView = myToastView.findViewById(android.R.id.message);
                        myTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                        myToast.show();
                    }
                }catch (Exception e){
                    Toast wrongDataToast = Toast.makeText(view.getContext(), "Συμπληρώστε πρώτα σωστά τα κατάλληλα πεδία και προσπαθήστε ξανά", Toast.LENGTH_LONG);

                    View wrongDataView = wrongDataToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter
                    wrongDataView.getBackground().setColorFilter(wrongDataView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be editted
                    TextView wrongDataTextView = wrongDataView.findViewById(android.R.id.message);
                    wrongDataTextView.setTextColor(wrongDataView.getContext().getResources().getColor(R.color.white));

                    wrongDataToast.show();
                    e.printStackTrace();
                }
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
        //check if password length is between 6 and 15 characters.
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

        //check if password contains at least one or more special characters from the list below
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

            //checking if password contains any small letters or not.
            for (int i = 90; i <= 122; i++) {

                // Type Casting
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
     * A method to execute, control and validate the registration process of a new employee.
     *
     * @throws Exception
     */
    private void newRegistration() throws Exception{
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("first_name", firstName));
        params.add(new BasicNameValuePair("last_name", lastName));
        params.add(new BasicNameValuePair("afm_employee", String.valueOf(afm)));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("phone", String.valueOf(phone)));

        //Using the necessary params to access online database.
        params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
        params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
        params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
        params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = jsonParser.makeHttpRequest(url_add_employee_user, "POST", params);

        int successFlag = jsonObject.getInt("success");
        if(successFlag==1){
            Toast successToast =  Toast.makeText(this,"Επιτυχής εγγραφή\n"+firstName+" "+lastName, Toast.LENGTH_SHORT);

            View successView = successToast.getView();

            //Gets the actual oval background of the Toast then sets the colour filter
            successView.getBackground().setColorFilter(successView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

            //Gets the TextView from the Toast so it can be editted
            TextView successTextView = successView.findViewById(android.R.id.message);
            successTextView.setTextColor(successView.getContext().getResources().getColor(R.color.black));

            successToast.show();

            Intent intent = new Intent(EmployeeRegisterActivity.this, EmployeeLoginActivity.class);
            startActivity(intent);

            finish();
        }else{
            Toast errorToast2 = Toast.makeText(this, "Μη επιτυχής εγγραφή εργαζομένου", Toast.LENGTH_LONG);

            View myErrorView2 = errorToast2.getView();

            //Gets the actual oval background of the Toast then sets the colour filter
            myErrorView2.getBackground().setColorFilter(myErrorView2.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

            //Gets the TextView from the Toast so it can be editted
            TextView errorTextView2 = myErrorView2.findViewById(android.R.id.message);
            errorTextView2.setTextColor(myErrorView2.getContext().getResources().getColor(R.color.white));

            errorToast2.show();
        }
    }

    /**
     * The commands inside the following method are executed when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(EmployeeRegisterActivity.this, EmployeeLoginActivity.class);
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
