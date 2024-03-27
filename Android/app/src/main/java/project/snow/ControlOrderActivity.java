package project.snow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * ControlOrderActivity Class.
 *
 * This class is used so as an employee to control (handle) a specific order.
 * Here, an employee has 2 options:
 * 1. to mark the order as completed,
 * 2. to entirely cancel the order.
 *
 * @author thanoskalantzis
 */
public class ControlOrderActivity extends AppCompatActivity {
    //Class variables.
    //url_modify_condition_of_order variable is actually the url which corresponds to the php file for handling (controlling) a request to modify order.
    //Such a request could either be a request to declare the order as completed, or a request to cancel the entire order.
    private String url_modify_condition_of_order;
    private RecyclerView screenListView;
    private ControlOrderListAdapter controlOrderListAdapter;
    private int lastPosition;
    private Button orderCompletedButton;
    private Button orderCanceledButton;
    private TextView controlOrderFinalPrice;
    private static ArrayList<OrderItem> myOrderItemList;
    private static ArrayList<Business> businessesList;
    private static Employee currentEmployee;
    private static int afmEmployee;
    private static Business businessToSendBack;

    /**
     * The following method is essential and necessary due to the fact that ControlOrderActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        //Initialize url_modify_condition_of_order.
        url_modify_condition_of_order = getString(R.string.BASE_URL).concat("/connect/modifyconditionoforder.php");

        //Getting some information from previously active activity.
        final FinalOrder finalOrderSelected = (FinalOrder) getIntent().getSerializableExtra("finalOrder");
        final Business business = (Business) getIntent().getSerializableExtra("business");

        businessToSendBack = business;

        //Getting some information from previously active activity.
        businessesList = (ArrayList<Business>) getIntent().getSerializableExtra("businessesWorkingForList");
        currentEmployee = (Employee) getIntent().getSerializableExtra("currentEmployee");
        afmEmployee = (int) getIntent().getSerializableExtra("afm_employee");

        //Setting the layout based on the option the current employee made.
        if(finalOrderSelected.getIsActive()){
            setContentView(R.layout.activity_control_active_order);
        }else{
            setContentView(R.layout.activity_control_completed_order);
        }

        //Setting title of the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Τραπέζι: " + finalOrderSelected.getNumberTable());
        }

        myOrderItemList = finalOrderSelected.getOrderItemList();
        Log.i("dt", finalOrderSelected.getDateTime());
        //Show
        if(finalOrderSelected.getIsActive()){
            screenListView = (RecyclerView) findViewById(R.id.controlActiveOrderRecyclerView);

            //Here follows the process to mark the order as completed.
            orderCompletedButton = (Button) findViewById(R.id.orderCompletedButton);
            orderCompletedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();

                    //If case == 1 then the command which updates the condition of the order from "pending" to "completed" will be executed (inside the modifyconditionoforder.php)
                    params.add(new BasicNameValuePair("case", "1"));
                    params.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));
                    params.add( new BasicNameValuePair("number_table", String.valueOf( finalOrderSelected.getNumberTable()) ) );
                    params.add(new BasicNameValuePair("date_time", finalOrderSelected.getDateTime()));

                    //Using the necessary params to access online database.
                    params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                    params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                    params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                    params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                    JSONParser jsonParser=new JSONParser();
                    JSONObject jsonObject = jsonParser.makeHttpRequest(url_modify_condition_of_order, "POST", params);

                    try {
                        int successFlag = jsonObject.getInt("success");

                        //Some variables declarations
                        Toast errorToast;
                        View myErrorView;
                        TextView errorTextView;

                        if(successFlag == 1){
                            int toastDuration = Toast.LENGTH_SHORT;
                            errorToast = Toast.makeText(view.getContext(), "Παραγγελία Ολοκληρώθηκε", toastDuration);

                            myErrorView = errorToast.getView();

                            //Gets the actual oval background of the Toast then sets the colour filter
                            myErrorView.getBackground().setColorFilter(myErrorView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                            //Gets the TextView from the Toast so it can be editted
                            errorTextView = myErrorView.findViewById(android.R.id.message);
                            errorTextView.setTextColor(myErrorView.getContext().getResources().getColor(R.color.black));

                            errorToast.show();

                            Intent i1 = new Intent(ControlOrderActivity.this, DisplayOrdersDetailsActivity.class);
                            i1.putExtra("Business", business);
                            i1.putExtra("businessesWorkingForList", businessesList);
                            i1.putExtra("currentEmployee", currentEmployee);
                            i1.putExtra("afm_employee", afmEmployee);
                            startActivity(i1);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            //Here follows the process to mark the order as canceled.
            orderCanceledButton = (Button) findViewById(R.id.orderCanceledButton);
            orderCanceledButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();

                    //If case == 2 then the command which deletes the order will be executed (inside the modifyconditionoforder.php)
                    params.add(new BasicNameValuePair("case", "2"));
                    params.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));
                    params.add( new BasicNameValuePair("number_table", String.valueOf( finalOrderSelected.getNumberTable()) ) );
                    params.add(new BasicNameValuePair("date_time", finalOrderSelected.getDateTime()));

                    //Using the necessary params to access online database.
                    params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                    params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                    params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                    params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                    JSONParser jsonParser=new JSONParser();
                    JSONObject jsonObject = jsonParser.makeHttpRequest(url_modify_condition_of_order, "POST", params);

                    try {
                        int successFlag = jsonObject.getInt("success");

                        //Some variables declarations
                        Toast errorToast;
                        View myErrorView;
                        TextView errorTextView;

                        if(successFlag == 1){
                            int toastDuration = Toast.LENGTH_SHORT;
                            errorToast = Toast.makeText(view.getContext(), "Παραγγελία Ακυρώθηκε Επιτυχώς", toastDuration);

                            myErrorView = errorToast.getView();

                            //Gets the actual oval background of the Toast then sets the colour filter
                            myErrorView.getBackground().setColorFilter(myErrorView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                            //Gets the TextView from the Toast so it can be editted
                            errorTextView = myErrorView.findViewById(android.R.id.message);
                            errorTextView.setTextColor(myErrorView.getContext().getResources().getColor(R.color.black));

                            errorToast.show();

                            Intent i2 = new Intent(ControlOrderActivity.this, DisplayOrdersDetailsActivity.class);
                            i2.putExtra("Business", business);
                            i2.putExtra("businessesWorkingForList", businessesList);
                            i2.putExtra("currentEmployee", currentEmployee);
                            i2.putExtra("afm_employee", afmEmployee);
                            startActivity(i2);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            screenListView = (RecyclerView) findViewById(R.id.controlCompletedOrderRecyclerView);
        }
        screenListView.setHasFixedSize(true);



        double finalPriceOfOrder = 0;
        for(int i=0; i<myOrderItemList.size(); i++){
            finalPriceOfOrder = finalPriceOfOrder + ( myOrderItemList.get(i).getQuantity() * myOrderItemList.get(i).getProduct().getPrice() );
        }

        if(finalOrderSelected.getIsActive()){
            controlOrderFinalPrice = (TextView) findViewById(R.id.controlActiveOrderFinalPrice);
        }else{
            controlOrderFinalPrice = (TextView) findViewById(R.id.controlCompletedOrderFinalPrice);
        }
        controlOrderFinalPrice.setText("Σύνολο: " + String.valueOf(finalPriceOfOrder) + " €");

        controlOrderListAdapter = new ControlOrderListAdapter(myOrderItemList);
        controlOrderListAdapter.setHasStableIds(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        //retrieve last position on start
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        lastPosition = getPrefs.getInt("lastPosition", 0);
        screenListView.scrollToPosition(lastPosition);
        screenListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastPosition = linearLayoutManager.findFirstVisibleItemPosition();
            }
        });

        screenListView.setLayoutManager(linearLayoutManager);
        screenListView.setAdapter(controlOrderListAdapter);
    }

    //TODO chech again the following method
    /**
     * The commands inside the following method are executed when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ControlOrderActivity.this, DisplayOrdersDetailsActivity.class);
        intent.putExtra("businessesWorkingForList", businessesList);
        intent.putExtra("currentEmployee", currentEmployee);
        intent.putExtra("afm_employee", afmEmployee);
        intent.putExtra("Business", businessToSendBack);
        startActivity(intent);
        finish();
        super.onBackPressed();

        //return;
    }

    /**
     * Whenever the activity is destroyed (finished/closed) then the following method will be called.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor e = getPrefs.edit();
        e.putInt("lastPosition", lastPosition);
        e.apply();
    }
}
