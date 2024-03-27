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
 * BusinessLoginActivity Class.
 *
 * This class is used to control the registration process of a new business.
 *
 * @author thanoskalantzis
 */
public class BusinessRegisterActivity extends AppCompatActivity {
    //Class variables.
    private String email;
    private String password;
    private String businessUniqueIdName;
    private int afm;
    private long phone;
    private String address;
    private int postalCode;
    private int numberTables;
    private Button businessRegisterButton;
    private Business business;
    //url_add_business variable is actually the url which corresponds to the php file for registering a new business.
    private String url_add_business;

    /*
    * If the new business registration process is successful,
    * then we will also use the following url to create all necessary tables for that particular business.
    *
    * Those tables, are: productsAFM and ordersAFM tables.
    * AFM OF COURSE WILL BE AUTOMATICALLY REPLACED BY THE REAL AFM NUMBER OF THE PARTICULAR BUSINESS,
    * WHICH WILL BE SEND TO OUR DATABASE SERVER THROUGH AN HTTP POST REQUEST.
    */
    private String url_new_tables_for_new_business;
    //Just some more variables declarations
    private Toast myToast;
    private View myToastView;
    private TextView myTextView;

    /**
     * The following method is essential and necessary due to the fact that BusinessRegisterActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_register);
		
		//Initialize url_add_business.
		url_add_business = getString(R.string.BASE_URL).concat("/connect/addbusiness.php");
		//Initialize url_new_tables_for_new_business.
		url_new_tables_for_new_business = getString(R.string.BASE_URL).concat("/connect/newtablesfornewbusiness.php");

        //Setting the title of the action bar.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Εγγραφή Επιχείρησης");
        }

        //Here follows the process for registering a new business.
        businessRegisterButton = (Button) findViewById(R.id.businessRegisterButton2);
        businessRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    EditText emailGiven = (EditText) findViewById(R.id.businessRegisterEmailInput);
                    email = (String) emailGiven.getText().toString().trim();

                    EditText passwordGiven = (EditText) findViewById(R.id.businessRegisterPasswordInput);
                    password = (String) passwordGiven.getText().toString().trim();

                    EditText businessUniqueIdNameGiven = (EditText) findViewById(R.id.businessUniqueIdName);
                    businessUniqueIdName = (String) businessUniqueIdNameGiven.getText().toString().trim();

                    EditText afmGiven = (EditText) findViewById(R.id.businessRegisterAfmInput);
                    afm = (int) Integer.parseInt(afmGiven.getText().toString().trim());

                    EditText phoneGiven=(EditText) findViewById(R.id.businessRegisterPhoneInput);
                    phone = (long) Long.parseLong(phoneGiven.getText().toString().trim());

                    EditText addressGiven=(EditText) findViewById(R.id.addressInput);
                    address = (String) addressGiven.getText().toString().trim();

                    EditText postalCodeGiven=(EditText) findViewById(R.id.postalCodeInput);
                    postalCode = (int) Integer.parseInt(postalCodeGiven.getText().toString().trim());

                    EditText numberTablesGiven=(EditText) findViewById(R.id.numberTablesInput);
                    numberTables = (int) Integer.parseInt(numberTablesGiven.getText().toString().trim());

                    //Of course, we check so as all given values matches some standard formats.
                    //If given values do not match the standard formats that we have set, then the system will prompt business owner to correct all mistakes.
                    if( afm>0 && (String.valueOf(afm).length()==9) && (postalCode>0 && String.valueOf(postalCode).length()==5) && (phone>0 && String.valueOf(phone).length()==10) && address.length()>0 ){
                        if(emailIsValid(email)){
                            if(passwordIsValid(password)){
                                //If the new business provides valid every information asked by our system, then the newRegistration method will be called.
                                newRegistration();

                                //After the new business registration process is successful, then we will redirect the business to a new activity corresponding BusinessActivity class.
                                Intent intent = new Intent(BusinessRegisterActivity.this, BusinessActivity.class);
                                intent.putExtra("business", business);
                                startActivity(intent);

                                finish();
                            }else {
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
                        }else {
                            myToast =  Toast.makeText(view.getContext(),"Έχετε δώσει email σε μη αποδεκτή μορφή!",Toast.LENGTH_LONG);
                            myToastView = myToast.getView();

                            //Gets the actual oval background of the Toast then sets the colour filter.
                            myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                            //Gets the TextView from the Toast so it can be edited.
                            myTextView = myToastView.findViewById(android.R.id.message);
                            myTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                            myToast.show();
                        }
                    }else{
                        myToast =  Toast.makeText(view.getContext(),"Συμπληρώστε πρώτα σωστά τα κατάλληλα πεδία και προσπαθήστε ξανά",Toast.LENGTH_LONG);
                        myToastView = myToast.getView();

                        //Gets the actual oval background of the Toast then sets the colour filter.
                        myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                        //Gets the TextView from the Toast so it can be edited.
                        myTextView = myToastView.findViewById(android.R.id.message);
                        myTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                        myToast.show();
                    }
                }catch (Exception e1){
                    myToast =  Toast.makeText(view.getContext(),"Συμπληρώστε πρώτα σωστά τα κατάλληλα πεδία και προσπαθήστε ξανά",Toast.LENGTH_LONG);
                    myToastView = myToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter.
                    myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be edited.
                    myTextView = myToastView.findViewById(android.R.id.message);
                    myTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                    myToast.show();
                    e1.printStackTrace();
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

            //Checking if password contains small letters.
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
     * A method to execute, control and validate the registration process of a new business.
     *
     * @throws Exception
     */
    private void newRegistration() throws Exception{
        //params1 is the dynamic array of those parameters which will be used to send data to our online database.
        List<NameValuePair> params1 = new ArrayList<NameValuePair>();
        params1.add(new BasicNameValuePair("email", email));
        params1.add(new BasicNameValuePair("password", password));
        params1.add(new BasicNameValuePair("business_id_name", businessUniqueIdName));
        params1.add(new BasicNameValuePair("afm_business", String.valueOf(afm)));
        params1.add(new BasicNameValuePair("phone", String.valueOf(phone)));
        params1.add(new BasicNameValuePair("address", address));
        params1.add(new BasicNameValuePair("postal_code", String.valueOf(postalCode)));
        params1.add(new BasicNameValuePair("number_tables", String.valueOf(numberTables)));

        //Using the necessary params to access to online database.
        params1.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
        params1.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
        params1.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
        params1.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

        //Creating a new JSONParser instance (object) which is responsible from the communication process.
        JSONParser jsonParser1=new JSONParser();
        //Executing HTTP POST Request, with the given parameters, so as to successfully register the new business.
        JSONObject jsonObject1 = jsonParser1.makeHttpRequest(url_add_business, "POST", params1);

        int successFlag = jsonObject1.getInt("success");
        if(successFlag==1){
            business = new Business();
            business.setEmail(email);
            business.setPassword(password);
            business.setBusinessIdName(businessUniqueIdName);
            business.setAfmBusiness(afm);
            business.setPhone(phone);
            business.setAddress(address);
            business.setPostalCode(postalCode);
            business.setNumberTables(numberTables);

            myToast = Toast.makeText(this,"Επιτυχής εγγραφή επιχείρησης:\n\""+businessUniqueIdName+"\"\nΑΦΜ: "+afm, Toast.LENGTH_LONG);
            myToastView = myToast.getView();

            //Gets the actual oval background of the Toast then sets the colour filter.
            myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

            //Gets the TextView from the Toast so it can be edited.
            TextView errorTextView = myToastView.findViewById(android.R.id.message);
            errorTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

            myToast.show();

            String afmToString = new String(String.valueOf(afm).trim());

            //params2 is the dynamic array of those parameters which will be used to send data to our online database.
            List<NameValuePair> params2 = new ArrayList<NameValuePair>();
            params2.add(new BasicNameValuePair("afm_business", afmToString));

            //Using the necessary params to access to online database.
            params2.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
            params2.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
            params2.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
            params2.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

            //Creating a new JSONParser instance (object) which is responsible from the communication process.
            JSONParser jsonParser2 = new JSONParser();
            //Executing HTTP POST Request, with the given parameters, so as to successfully create all necessary tables for the newly registered business.
            JSONObject jsonObject2 = jsonParser2.makeHttpRequest(url_new_tables_for_new_business, "POST", params2);
        }else{
            myToast = Toast.makeText(this,"Μη επιτυχής εγγραφή", Toast.LENGTH_LONG);
            myToastView = myToast.getView();

            //Gets the actual oval background of the Toast then sets the colour filter.
            myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

            //Gets the TextView from the Toast so it can be edited.
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
        Intent intent=new Intent(BusinessRegisterActivity.this, BusinessLoginActivity.class);
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
