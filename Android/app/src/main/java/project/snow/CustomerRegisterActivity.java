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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * CustomerRegisterActivity Class.
 *
 * This class is used to control the registration process of a new customer.
 *
 * @author thanoskalantzis
 */
public class CustomerRegisterActivity extends AppCompatActivity {
    //Class variables.
    //url_register_customer variable is actually the url which corresponds to the php file for registering a new customer.
    private String url_register_customer;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Long phone;
    private Button customerRegisterButton;

    //Just some more variables declarations
    private Toast myToast;
    private View myToastView;
    private TextView myTextView;

    /**
     * The following method is essential and necessary due to the fact that CustomerRegisterActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);

        //Initialize url_register_customer.
        url_register_customer = getString(R.string.BASE_URL).concat("/connect/addcustomer.php");

        //Setting the title of the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Εγγραφή Χρήστη");
        }

        //Here follows the process for registering a new customer.
        customerRegisterButton = (Button) findViewById(R.id.customerRegisterButton2);
        customerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    EditText firstNameGiven = (EditText) findViewById(R.id.customerFirstNameInput);
                    firstName = (String) firstNameGiven.getText().toString().trim();

                    EditText lastNameGiven = (EditText) findViewById(R.id.customerLastNameInput);
                    lastName = (String) lastNameGiven.getText().toString().trim();

                    EditText emailGiven = (EditText) findViewById(R.id.customerRegisterEmailInput);
                    email = (String) emailGiven.getText().toString().trim();

                    EditText passwordGiven = (EditText) findViewById(R.id.customerRegisterPasswordInput);
                    password = (String) passwordGiven.getText().toString().trim();

                    EditText phoneGiven=(EditText) findViewById(R.id.customerRegisterPhoneInput);
                    phone = (long) Long.parseLong(phoneGiven.getText().toString().trim());

                    //Of course, we check so as all given values matches some standard formats.
                    //If given values do not match the standard formats that we have set, then the system will prompt customer to correct all mistakes.
                    if( firstName.length()>0 && lastName.length()>0 && (phone>0 && String.valueOf(phone).length()==10) ){
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
                    myToast =  Toast.makeText(view.getContext(),"Συμπληρώστε πρώτα σωστά τα κατάλληλα πεδία και προσπαθήστε ξανά", Toast.LENGTH_LONG);
                    myToastView = myToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter
                    myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be editted
                    myTextView = myToastView.findViewById(android.R.id.message);
                    myTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                    myToast.show();

                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Here follows a function to check whether an email is valid or not.
     *
     * @param emailToCheck the given email we want to check.
     * @return  true if the email is valid, false otherwise.
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

            //Checking if password contains any small letters.
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
     * A method to execute, control and validate the registration process of a new customer.
     *
     * @throws Exception
     */
    private void newRegistration() throws Exception{
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("first_name", firstName));
        params.add(new BasicNameValuePair("last_name", lastName));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("phone", String.valueOf(phone)));

        //Using the necessary params to access online database.
        params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
        params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
        params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
        params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

        JSONParser jsonParser1=new JSONParser();
        JSONObject jsonObject1 = jsonParser1.makeHttpRequest(url_register_customer, "POST", params);

        int successFlag = jsonObject1.getInt("success");
        if(successFlag==1){
            myToast = Toast.makeText(this,"Επιτυχής εγγραφή\n"+firstName+" "+lastName, Toast.LENGTH_SHORT);
            myToastView = myToast.getView();

            //Gets the actual oval background of the Toast then sets the colour filter
            myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

            //Gets the TextView from the Toast so it can be editted
            TextView errorTextView = myToastView.findViewById(android.R.id.message);
            errorTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

            myToast.show();

            Customer customer = new Customer();
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setEmail(email);
            customer.setPassword(password);
            customer.setPhone(phone);

            Intent intent = new Intent(CustomerRegisterActivity.this, BusinessSelectionActivity.class);
            intent.putExtra("customer", customer);
            startActivity(intent);

            finish();
        }else{
            myToast = Toast.makeText(this,"Μη επιτυχής εγγραφή", Toast.LENGTH_LONG);
            myToastView = myToast.getView();

            //Gets the actual oval background of the Toast then sets the colour filter
            myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

            //Gets the TextView from the Toast so it can be editted
            TextView errorTextView = myToastView.findViewById(android.R.id.message);
            errorTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

            myToast.show();
        }
    }

    /**
     * The commands inside the following method are executed when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(CustomerRegisterActivity.this, CustomerLoginActivity.class);
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
