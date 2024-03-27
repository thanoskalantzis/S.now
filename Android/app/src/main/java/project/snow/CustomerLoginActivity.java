package project.snow;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * BusinessLoginActivity Class.
 *
 * This class is used to control and verify the login process of each customer.
 *
 * @author thanoskalantzis
 */
public class CustomerLoginActivity extends AppCompatActivity {
    //Class variables.
    //url_get_customers variable is actually the url which corresponds to the php file for getting all currently registered customers.
    private String url_get_customers;
    private static Customer currentCustomer;
    private static String email;
    private static String password;
    private Button customerLoginButton;
    private TextView customerRegisterButton;
    private static JSONObject jsonObject;
    //Just some more variables declarations.
    int toastDuration = Toast.LENGTH_SHORT;
    Toast myToast;
    View myToastView;
    TextView myTextView;

    /**
     * The following method is essential and necessary due to the fact that CustomerLoginActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        //Initialize url_get_customers.
        url_get_customers = getString(R.string.BASE_URL).concat("/connect/getcustomers.php");

        //Setting the title of the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Είσοδος Χρήστη");
        }

        //Here follows the process for verifying or not customer login.
        customerLoginButton = (Button) findViewById(R.id.customerLoginButton);
        customerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EditText emailGiven = (EditText) findViewById(R.id.customerLoginEmailInput);
                    email = String.valueOf(emailGiven.getText().toString().trim());

                    EditText passwordGiven = (EditText) findViewById(R.id.customerLoginPasswordInput);
                    password = String.valueOf(passwordGiven.getText().toString().trim());

                    JSONParser jsonParser=new JSONParser();

                    //params is the dynamic array of those parameters which will be used to send data to our online database.
                    List<NameValuePair> params = new ArrayList<NameValuePair>();

                    //Using the necessary params to access online database.
                    params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                    params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                    params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                    params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                    jsonObject = jsonParser.makeHttpRequest(url_get_customers, "GET", params);

                    if(emailIsValid(email)){
                        loginValidation();
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
                } catch (Exception e1) {
                    myToast = Toast.makeText(view.getContext(),"Συμπληρώστε πρώτα σωστά τα κατάλληλα πεδία και προσπαθήστε ξανά", Toast.LENGTH_LONG);
                    myToastView = myToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter
                    myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be editted
                    myTextView = myToastView.findViewById(android.R.id.message);
                    myTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                    myToast.show();
                    e1.printStackTrace();
                }
            }
        });

        //Here follows the button for a new customer registration.
        customerRegisterButton = (TextView) findViewById(R.id.customerRegisterButton1);
        customerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerLoginActivity.this, CustomerRegisterActivity.class);
                startActivity(intent);
                finish();
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
     * The following method is the one responsible for the customer login validation process.
     *
     * @throws Exception
     */
    private void loginValidation() throws Exception {
        ArrayList<String> firstNameList=new ArrayList<String>();
        ArrayList<String> lastNameList=new ArrayList<String>();
        ArrayList<String> emailList=new ArrayList<String>();
        ArrayList<String> passwordList=new ArrayList<String>();
        ArrayList<Long> phoneList=new ArrayList<Long>();

        int successFlag = jsonObject.getInt("success");
        if(successFlag==1){
            JSONArray customersArray = jsonObject.getJSONArray("customers");

            for (int i = 0; i < customersArray.length(); i++) {
                firstNameList.add(customersArray.getJSONObject(i).getString("first_name"));
                lastNameList.add(customersArray.getJSONObject(i).getString("last_name"));
                emailList.add(customersArray.getJSONObject(i).getString("email"));
                passwordList.add(customersArray.getJSONObject(i).getString("password"));
                phoneList.add(customersArray.getJSONObject(i).getLong("phone"));
            }

            boolean customerExists = false;
            int indexOfCustomer = -1;
            for(int j=0; j<emailList.size(); j++) {
                if (email.equals(emailList.get(j)) && password.equals(passwordList.get(j))) {
                  customerExists = true;
                    indexOfCustomer = j;
                    break;
                }
            }

            if(customerExists){
                myToast = Toast.makeText(this,"Επιτυχής σύνδεση\n"+firstNameList.get(indexOfCustomer)+" "+lastNameList.get(indexOfCustomer), toastDuration);
                myToastView = myToast.getView();

                //Gets the actual oval background of the Toast then sets the colour filter
                myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                //Gets the TextView from the Toast so it can be editted
                myTextView = myToastView.findViewById(android.R.id.message);
                myTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

                myToast.show();

                currentCustomer = new Customer();
                currentCustomer.setFirstName(firstNameList.get(indexOfCustomer));
                currentCustomer.setLastName(lastNameList.get(indexOfCustomer));
                currentCustomer.setEmail(emailList.get(indexOfCustomer));
                currentCustomer.setPassword(passwordList.get(indexOfCustomer));
                currentCustomer.setPhone(phoneList.get(indexOfCustomer));

                Intent intent = new Intent(CustomerLoginActivity.this, BusinessSelectionActivity.class);
                intent.putExtra("customer", currentCustomer);
                startActivity(intent);

                finish();
            }else{
                myToast = Toast.makeText(this,"Λανθασμένα στοιχεία εισόδου.\nΠροσπαθήστε ξανά", Toast.LENGTH_LONG);
                myToastView = myToast.getView();

                //Gets the actual oval background of the Toast then sets the colour filter
                myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                //Gets the TextView from the Toast so it can be editted
                myTextView = myToastView.findViewById(android.R.id.message);
                myTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                myToast.show();
            }
        }else{
            myToast = Toast.makeText(this,"Κανένας εγγεγραμμένος πελάτης στο σύστημα",Toast.LENGTH_LONG);
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
        Intent intent=new Intent(CustomerLoginActivity.this, MainActivity.class);
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
