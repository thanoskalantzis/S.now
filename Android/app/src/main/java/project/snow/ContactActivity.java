package project.snow;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * ContactActivity Class.
 *
 * This the class allows anyone (not only system users, but whoever has downloaded our app) to send us messages.
 * It is actually responsible for accepting, controlling, verifying and sending messages to our online database.
 *
 * @author thanoskalantzis
 */
public class ContactActivity extends AppCompatActivity {
    //Class variables.
    //url_contact_us variable is actually the url which corresponds to the php file for parsing user messages into our online database.
    private String url_contact_us;
    private static JSONObject messageObject;
    private String firstName;
    private String lastName;
    private String email;
    private long phone;
    private String title;
    private String message;
    private Button contactButton;


    /**
     * The following method is essential and necessary due to the fact that ContactActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        //Initialize url_contact_us.
        url_contact_us = getString(R.string.BASE_URL).concat("/connect/contact.php");

        //Setting title of action bar.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Επικοινωνία");
        }

        //Here starts the process for accepting, controlling, verifying and sending messages to our online database.
        contactButton = (Button) findViewById(R.id.contactButton2);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast myToast;
                View myToastView;
                TextView myToastTextView;

                try{
                    EditText firstNameGiven = (EditText) findViewById(R.id.contactFirstName);
                    firstName = (String) String.valueOf(firstNameGiven.getText().toString().trim());

                    EditText lastNameGiven = (EditText) findViewById(R.id.contactLastName);
                    lastName = (String) String.valueOf(lastNameGiven.getText().toString().trim());

                    EditText emailGiven = (EditText) findViewById(R.id.contactEmail);
                    email = (String) String.valueOf(emailGiven.getText().toString().trim());

                    EditText phoneGiven = (EditText) findViewById(R.id.contactPhone);
                    phone = (Long) Long.parseLong(phoneGiven.getText().toString().trim());

                    EditText titleGiven = (EditText) findViewById(R.id.contactTitle);
                    title = (String) String.valueOf(titleGiven.getText().toString().trim());

                    MultiAutoCompleteTextView messageGiven = (MultiAutoCompleteTextView) findViewById(R.id.contactMessage);
                    message = (String) String.valueOf(messageGiven.getText().toString().trim());

                    //Of course, we check so as all given values matches some standard formats.
                    //If given values do not match the standard formats that we have set, then the system will prompt user to correct all mistakes.
                    if(firstName.length()>0 && lastName.length()>0 && title.length()>0 && message.length()>0 && String.valueOf(phone).length()==10 && phone>0){
                        if(emailIsValid(email)){
                            //params is the dynamic array of those parameters which will be used to send data to our online database.
                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair("first_name", firstName));
                            params.add(new BasicNameValuePair("last_name", lastName));
                            params.add(new BasicNameValuePair("email", email));
                            params.add(new BasicNameValuePair("phone", String.valueOf(phone)));
                            params.add(new BasicNameValuePair("title", title));
                            params.add(new BasicNameValuePair("message", message));

                            //Using the necessary params to access online database.
                            params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                            params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                            params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                            params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                            //Creating a new JSONParser instance (object) which is responsible from the communication process.
                            JSONParser jsonParser=new JSONParser();
                            //Executing HTTP POST Request, with the given parameters, so as to successfully send user message to our online database.
                            messageObject = jsonParser.makeHttpRequest(url_contact_us, "POST", params);

                            int successFlag = messageObject.getInt("success");

                            if(successFlag == 1){
                                myToast = Toast.makeText(ContactActivity.this, "Το μήνυμά σας στάλθηκε επιτυχώς.\nΘα επικοινωνήσουμε σύντομα μαζί σας...", Toast.LENGTH_LONG);
                                myToastView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter.
                                myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be edited.
                                myToastTextView = myToastView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

                                myToast.show();

                                Intent intent = new Intent(ContactActivity.this, ContactActivity.class);
                                startActivity(intent);

                                finish();
                            }else{
                                myToast = Toast.makeText(ContactActivity.this, "Αποτυχία αποστολής μηνύματος", Toast.LENGTH_LONG);
                                myToastView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter.
                                myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be edited.
                                myToastTextView = myToastView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                                myToast.show();
                            }
                        }else{
                            myToast =  Toast.makeText(ContactActivity.this,"Έχετε δώσει email σε μη αποδεκτή μορφή!",Toast.LENGTH_LONG);
                            myToastView = myToast.getView();

                            //Gets the actual oval background of the Toast then sets the colour filter.
                            myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                            //Gets the TextView from the Toast so it can be edited.
                            myToastTextView = myToastView.findViewById(android.R.id.message);
                            myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                            myToast.show();
                        }
                    }else{
                        myToast =  Toast.makeText(ContactActivity.this,"Συμπληρώστε πρώτα σωστά τα κατάλληλα πεδία και προσπαθήστε ξανά", Toast.LENGTH_LONG);
                        myToastView = myToast.getView();

                        //Gets the actual oval background of the Toast then sets the colour filter.
                        myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                        //Gets the TextView from the Toast so it can be edited.
                        myToastTextView = myToastView.findViewById(android.R.id.message);
                        myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                        myToast.show();
                    }
                }catch (Exception e){
                    myToast = Toast.makeText(ContactActivity.this, "Αποτυχία αποστολής μηνύματος", Toast.LENGTH_SHORT);
                    myToastView = myToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter.
                    myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be edited.
                    myToastTextView = myToastView.findViewById(android.R.id.message);
                    myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                    myToast.show();

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
     * The commands inside the following method are executed when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ContactActivity.this, AboutActivity.class);
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
