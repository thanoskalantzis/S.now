package project.snow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * AboutActivity Class.
 *
 * This class is actually responsible for showing a few things about the reason why we created our app.
 *
 * Additionally, this class also shows the server status (whether is online or offline).
 *
 * @author thanoskalantzis
 */
public class AboutActivity extends AppCompatActivity {
    //Class variables.
    //url_server_status variable is actually the url which corresponds to the php file for getting the status of the server.
    private String url_server_status;
    private static JSONObject serverStatusObject;
    private boolean serverStatus;

    /**
     * The following method is essential and necessary due to the fact that AboutActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //Initialize URL.
        url_server_status = getString(R.string.BASE_URL).concat("/connect/serverstatus.php");

        //Setting the title of the action bar.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            //Date & Time here are not essential elements.
            //We were just looking for alternative elements to show.
            //Date & Time is indeed used inside our app during the phase when a customer completes an order.
                //A java calendar instance
                Calendar calendar = Calendar.getInstance();
                //Get a java.util.Date from the calendar instance.
                java.util.Date now = calendar.getTime();
                //A Java current time (now) instance
                java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
                //Calendar cal = Calendar.getInstance();

            actionBar.setTitle("About S.now App");
        }

        Button contactButton1 = (Button) findViewById(R.id.contactButton1);
        contactButton1.setBackgroundResource(R.drawable.contactbutton);
        contactButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Whenever the contactButton1 is pressed then a new activity is created.
                //In this case, the activity which is created corresponds to the ContactActivity class.
                Intent intent = new Intent(AboutActivity.this, ContactActivity.class);
                startActivity(intent);
                finish();
            }
        });

        try{
            //serverStatusText is where the server status (online or offline) indication will be placed.
            TextView serverStatusText = (TextView) findViewById(R.id.serverStatusText);

            //Creating a new JSONParser instance (object) which is responsible from the communication process.
            JSONParser jsonParser=new JSONParser();

            //params is the dynamic array of those parameters which will be used to send data to our online database.
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            //Using the necessary params to access online database.
            params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
            params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
            params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
            params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

            //Executing HTTP GET Request, without any parameter, so as to get the server status.
            serverStatusObject = jsonParser.makeHttpRequest(url_server_status, "GET", params);

            System.out.println("***thanos***");
            System.out.println("***thanos > serverStatusObject > "+serverStatusObject);
            System.out.println("***end***");

            //If the value for the success flag is 1 (flag which is located inside the response from server), then we have successfully gotten the requested data.
            int successFlag = serverStatusObject.getInt("success");

            //Setting text accordingly.
            if(successFlag == 1){
                serverStatusText.setText(" Online");
                serverStatusText.setTextColor(this.getResources().getColor(R.color.green));
                serverStatus = true;
            }else{
                serverStatusText.setText(" Offline");
                serverStatusText.setTextColor(this.getResources().getColor(R.color.red));
                serverStatus = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Whenever the activity is destroyed (finished/closed) then the following method will be called.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
