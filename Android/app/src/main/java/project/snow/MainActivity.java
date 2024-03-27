package project.snow;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * This is the class of the Main Activity of this project.
 *
 * This class is actually responsible for creating the "main menu" of the project
 * and also handles accordingly each action made by the user.
 *
 * Since, this is the first class of the project,
 * we would like to mention that this project is constructed so that to handle data stored online.
 * For that purpose and for testing purposes, we have created a free account on a free web host server.
 *
 * All data is stored inside a MySql Database,
 * and we get and send data to this Database by communicating with php files, which are also stored online.
 *
 * Here follows a short analysis on how our app communicates with the server:
 * Each time that a connection needs to be made, then we declare the "communication type",
 * meaning that we declare whether we are executing an HTTP GET Request or an HTTP POST Request.
 * After that, a connection is established.
 * All data read from HTTP GET Request are of JSON Format,
 * while when we execute an HTTP POST Request we construct the URL accordingly, so as to contain all necessary data to store into our Database.
 * Finally, as far as the internal way we use communication data is concerned, we convert, store and use them as JSONObject objects.
 * (We handle this information using JSON Objects by using the class JSONObject provided by the package: org.json.JSONObject).
 * So, it is crucial and for that particular reason we use a JSONParser class which is responsible from the communication process described above.
 *
 * From this class, someone is able to navigate successfully to the corresponding interface he/she desires based on the account type.
 * There are 3 account types based on whether the user of the app is (1) a customer, (2) a business or (3) an employee of a particular business.
 * This app handles each case (account type) separately.
 * We could actually think of the tasks of each case (account type) as being processed, controlled and handled by a different program flow.
 *
 * @author thanoskalantzis
 */
public class MainActivity extends AppCompatActivity {
    //Class variables.
    private ImageView beersImage;
    private ImageView coffeeImage;
    private Button customerButton;
    private Button businessButton;
    private Button employeeButton;
    private Button infoAndContactButton;

    /**
     * The following method is essential and necessary due to the fact that MainActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting the title of the action bar.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Οι δικές σου S.now εμπειρίες!");
        }

        //Getting display metrics (width & height) of the particular screen of the device that runs our app.
        //The reason we do so, is so as each component to be exactly placed accordingly to the dimensions of each screen.
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        //From now on we set and control the GUI (Graphical User Interface).
        beersImage = (ImageView) findViewById(R.id.beersImage);
        beersImage.getLayoutParams().height=height/3;

        coffeeImage = (ImageView) findViewById(R.id.coffeeImage);
        coffeeImage.getLayoutParams().height = height/6;

        //Setting and controlling "menu buttons"
        infoAndContactButton = (Button) findViewById(R.id.infoAndContactButton);
        infoAndContactButton.setBackgroundResource(R.drawable.infoimage);
        infoAndContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Whenever the infoAndContactButton is pressed then a new activity is created.
                //In this case, the activity which is created corresponds to the AboutActivity class.
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                //By using the following command, we actually declare to the system not to store any activity history and actually to delete (clear) all activity history from stack.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mainMenuLinearLayout);
        linearLayout.setPadding(0, height/5, 0, 0);

        customerButton = (Button) findViewById(R.id.customerButton);
        customerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Whenever the customerButton is pressed then a new activity is created.
                //In this case, the activity which is created corresponds to the CustomerLoginActivity class.
                Intent intent = new Intent(MainActivity.this, CustomerLoginActivity.class);
                //By using the following command, we actually declare to the system not to store any activity history and actually to delete (clear) all activity history from stack.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        businessButton = (Button) findViewById(R.id.businessButton);
        businessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Whenever the businessButton is pressed then a new activity is created.
                //In this case, the activity which is created corresponds to the BusinessLoginActivity class.
                Intent intent = new Intent(MainActivity.this, BusinessLoginActivity.class);
                //By using the following command, we actually declare to the system not to store any activity history and actually to delete (clear) all activity history from stack.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        employeeButton = (Button) findViewById(R.id.employeeButton);
        employeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Whenever the employeeButton is pressed then a new activity is created.
                //In this case, the activity which is created corresponds to the EmployeeLoginActivity class.
                Intent intent = new Intent(MainActivity.this, EmployeeLoginActivity.class);
                //By using the following command, we actually declare to the system not to store any activity history and actually to delete (clear) all activity history from stack.
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * The commands inside the following method are executed when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    /**
     * Whenever the activity is destroyed (finished/closed) then the following method will be called.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
    }
}
