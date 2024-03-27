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
 * This class is used to control and verify the login process of each business.
 *
 * @author thanoskalantzis
 */
public class BusinessLoginActivity extends AppCompatActivity{
    //Class variables.
    //url_get_businesses variable is actually the url which corresponds to the php file for getting all currently registered businesses.
    private String url_get_businesses;
    private int afm;
    private String password;
    private Button businessLoginButton;
    private TextView businessRegisterButton;
    private static JSONObject jsonObject;
    //Just some more variables declarations
    private Toast myToast;
    private View myToastView;
    private TextView myTextView;

    /**
     * The following method is essential and necessary due to the fact that BusinessLoginActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_login);

        //Initialize url_get_businesses.
        url_get_businesses = getString(R.string.BASE_URL).concat("/connect/getbusinesses.php");

        //Setting the title of the action bar.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Είσοδος Επιχείρησης");
        }

        //Here follows the process for verifying or not business login.
        businessLoginButton = (Button) findViewById(R.id.businessLoginButton);
        businessLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EditText afmGiven = (EditText) findViewById(R.id.businessLoginAfmInput);
                    afm = (int) Integer.parseInt(afmGiven.getText().toString().trim());

                    EditText passwordGiven = (EditText) findViewById(R.id.businessLoginPasswordInput);
                    password = String.valueOf(passwordGiven.getText().toString().trim());

                    //Creating a new JSONParser instance (object) which is responsible from the communication process.
                    JSONParser jsonParser=new JSONParser();

                    //params is the dynamic array of those parameters which will be used to send data to our online database.
                    List<NameValuePair> params = new ArrayList<NameValuePair>();

                    //Using the necessary params to access to online database.
                    params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                    params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                    params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                    params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                    //Executing HTTP GET Request, without any parameter, so as to successfully verify if the business which tries to login is indeed registered or not.
                    jsonObject = jsonParser.makeHttpRequest(url_get_businesses, "GET", params);

                    loginValidation();
                } catch (Exception e1) {
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

        //Here follows the button for a new business registration.
        businessRegisterButton = (TextView) findViewById(R.id.businessRegisterButton1);
        businessRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Whenever the businessRegisterButton is pressed then a new activity is created.
                //In this case, the activity which is created corresponds to the BusinessRegisterActivity class.
                Intent intent = new Intent(BusinessLoginActivity.this, BusinessRegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * The follow method is responsible for verifying or not the login of a particular business.
     *
     * @throws Exception
     */
    private void loginValidation() throws Exception {
        ArrayList<String> emailList=new ArrayList<String>();
        ArrayList<String> passwordList=new ArrayList<String>();
        ArrayList<String> businessIdNameList=new ArrayList<String>();
        ArrayList<Integer> afmBusinessList=new ArrayList<Integer>();
        ArrayList<Long> phoneList=new ArrayList<Long>();
        ArrayList<String> addressList=new ArrayList<String>();
        ArrayList<Integer> postalCodeList=new ArrayList<Integer>();
        ArrayList<Integer> numberTablesList=new ArrayList<Integer>();

        JSONArray businessesArray = jsonObject.getJSONArray("businesses");

        int successFlag = jsonObject.getInt("success");
        if(successFlag ==  1){
            for (int i = 0; i < businessesArray.length(); i++) {
                emailList.add(businessesArray.getJSONObject(i).getString("email"));
                passwordList.add(businessesArray.getJSONObject(i).getString("password"));
                businessIdNameList.add(businessesArray.getJSONObject(i).getString("business_id_name"));
                afmBusinessList.add(businessesArray.getJSONObject(i).getInt("afm_business"));
                phoneList.add(businessesArray.getJSONObject(i).getLong("phone"));
                addressList.add(businessesArray.getJSONObject(i).getString("address"));
                postalCodeList.add(businessesArray.getJSONObject(i).getInt("postal_code"));
                numberTablesList.add(businessesArray.getJSONObject(i).getInt("number_tables"));
            }

            boolean businessExists = false;
            int indexOfBusiness = -1;
            for(int j=0; j<afmBusinessList.size(); j++){
                if( (afm==afmBusinessList.get(j)) && password.equals(passwordList.get(j)) ){
                    businessExists=true;
                    indexOfBusiness = j;
                    break;
                }
            }

            if(businessExists){
                myToast = Toast.makeText(this,"Επιτυχής σύνδεση επιχείρησης\n\""+businessIdNameList.get(indexOfBusiness)+"\"\nΑΦΜ: "+afm, Toast.LENGTH_SHORT);
                myToastView = myToast.getView();

                //Gets the actual oval background of the Toast then sets the colour filter.
                myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                //Gets the TextView from the Toast so it can be edited.
                myTextView = myToastView.findViewById(android.R.id.message);
                myTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

                myToast.show();

                //If business is logged in successfully, then we create a Business object.
                Business business = new Business();
                business.setEmail(emailList.get(indexOfBusiness));
                business.setPassword(passwordList.get(indexOfBusiness));
                business.setBusinessIdName(businessIdNameList.get(indexOfBusiness));
                business.setAfmBusiness(afmBusinessList.get(indexOfBusiness));
                business.setPhone(phoneList.get(indexOfBusiness));
                business.setAddress(addressList.get(indexOfBusiness));
                business.setPostalCode(postalCodeList.get(indexOfBusiness));
                business.setNumberTables(numberTablesList.get(indexOfBusiness));

                //If business is logged in successfully, then we create a new activity and redirect the program flow to that particular activity.
                //The activity which we mention above corresponds to the BusinessActivity class.
                Intent intent = new Intent(BusinessLoginActivity.this, BusinessActivity.class);
                intent.putExtra("business", business);
                startActivity(intent);

                finish();
            }else{
                myToast =  Toast.makeText(this,"Λανθασμένα στοιχεία εισόδου.\nΠροσπαθήστε ξανά",Toast.LENGTH_LONG);
                myToastView = myToast.getView();

                //Gets the actual oval background of the Toast then sets the colour filter.
                myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                //Gets the TextView from the Toast so it can be edited.
                myTextView = myToastView.findViewById(android.R.id.message);
                myTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                myToast.show();
            }
        }else{
            myToast = Toast.makeText(this,"Καμία εγγεγραμμένη επιχείρηση στο σύστημα",Toast.LENGTH_LONG);
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
        Intent intent=new Intent(BusinessLoginActivity.this, MainActivity.class);
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
