package project.snow;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * WorkSelectionActivity class.
 *
 * This class is the one responsible for placing all workplaces onto the screen so as for the working employee to be able to select one of them (the one currently wokring for),
 * and also, this class, should be able to understand which workplace was finally selected by the employee.
 *
 * @author thanoskalantzis
 */
public class WorkSelectionActivity extends AppCompatActivity {
    //Class variables.
    //url_all_businesses variable is actually the url which corresponds to the php file for fetching all business from the online database server.
    private String url_all_businesses;
    private static ArrayList<Integer> businessesWorkingForList;
    private static Employee currentEmployee;
    private static int afmEmployee;
    private RecyclerView recyclerView;
    private static WorkSelectionActivityListAdapter workSelectionListAdapter;
    private SwipeRefreshLayout refreshLayout;
    private static JSONObject jsonObject;
    private static ArrayList<Business> businessesList;

    /**
     * The following method is essential and necessary due to the fact that WorkSelectionActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_selection);

        //Initialize url_all_businesses.
        url_all_businesses = getString(R.string.BASE_URL).concat("/connect/getbusinesses.php");

        //Getting parsed data (information) from the previously active activity.
        Intent intent = getIntent();
        businessesWorkingForList = (ArrayList<Integer>) getIntent().getSerializableExtra("businessesWorkingForList");
        currentEmployee = (Employee) getIntent().getSerializableExtra("currentEmployee");
        afmEmployee = (int) getIntent().getSerializableExtra("afm_employee");

        //Setting the title of the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Επιλογή Χώρου Εργασίας");
        }

        try{
            //Call workSelectionTask()
            workSelectionTask();
        }catch (Exception e){
            e.printStackTrace();
        }

        //Show
        recyclerView = (RecyclerView) findViewById(R.id.workSelectionRecyclerView);
        workSelectionListAdapter = new WorkSelectionActivityListAdapter(currentEmployee, afmEmployee, businessesList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(workSelectionListAdapter);

        refreshLayout = findViewById(R.id.workSelectionRefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try{
                    //Call workSelectionTask()
                    workSelectionTask();
                }catch (Exception e){
                    e.printStackTrace();
                }

                workSelectionListAdapter = new WorkSelectionActivityListAdapter(currentEmployee, afmEmployee, businessesList);
                recyclerView.setAdapter(workSelectionListAdapter);
                workSelectionListAdapter.notifyDataSetChanged();

                refreshLayout.setRefreshing(false);
            }
        });

    }

    /**
     * The following method is main task of this class.
     * During the execution time, it actually calls some other major functions and methods do get the work done.
     * All other functions and methods are listed below.
     *
     * @throws Exception
     */
    private void workSelectionTask() throws Exception{
        jsonObject=null;
        businessesList=null;

        //Get all registered businesses from online database
        int successFlag = getAllBusinesses();

        if(successFlag == 1){
            //Add all businesses to dynamic ArrayList<Business>
            businessesList = new ArrayList<Business>();
            createBusinessesList();
        }else{
            int toastDuration = Toast.LENGTH_LONG;
            Toast errorToast = Toast.makeText(this, "Fatal Error", toastDuration);

            View myErrorView = errorToast.getView();

            //Gets the actual oval background of the Toast then sets the colour filter
            myErrorView.getBackground().setColorFilter(myErrorView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

            //Gets the TextView from the Toast so it can be editted
            TextView errorTextView = myErrorView.findViewById(android.R.id.message);
            errorTextView.setTextColor(myErrorView.getContext().getResources().getColor(R.color.white));

            errorToast.show();
            finish();
        }
    }

    /**
     * The following method is actually the one responsible for getting all workplaces from our online database server.
     *
     * @return 1 if all workplaces successfully returned, 0 otherwise.
     * @throws Exception
     */
    private int getAllBusinesses() throws Exception{
        //params is the dynamic array of those parameters which will be used to send data to our online database.
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        //Using the necessary params to access online database.
        params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
        params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
        params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
        params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

        JSONParser jsonParser=new JSONParser();
        jsonObject = jsonParser.makeHttpRequest(url_all_businesses, "GET", params);

        return jsonObject.getInt("success");
    }

    /**
     * Here follows the method, for creating the workplaces list, out of which the employee will select the one workplace he/she is currently working for.
     *
     * @throws Exception
     */
    private void createBusinessesList() throws Exception{
        ArrayList<String> emailList=new ArrayList<String>();
        ArrayList<String> passwordList=new ArrayList<String>();
        ArrayList<String> businessIdNnameList=new ArrayList<String>();
        ArrayList<Integer> afmBusinessList=new ArrayList<Integer>();
        ArrayList<Long> phoneList=new ArrayList<Long>();
        ArrayList<String> addressList=new ArrayList<String>();
        ArrayList<Integer> postalCodeList=new ArrayList<Integer>();
        ArrayList<Integer> numberTablesList=new ArrayList<Integer>();

        JSONArray businessesJSONArray = jsonObject.getJSONArray("businesses");

        for (int i = 0; i < businessesJSONArray.length(); i++) {
            emailList.add(businessesJSONArray.getJSONObject(i).getString("email"));
            passwordList.add(businessesJSONArray.getJSONObject(i).getString("password"));
            businessIdNnameList.add(businessesJSONArray.getJSONObject(i).getString("business_id_name"));
            afmBusinessList.add(businessesJSONArray.getJSONObject(i).getInt("afm_business"));
            phoneList.add(businessesJSONArray.getJSONObject(i).getLong("phone"));
            addressList.add(businessesJSONArray.getJSONObject(i).getString("address"));
            postalCodeList.add(businessesJSONArray.getJSONObject(i).getInt("postal_code"));
            numberTablesList.add(businessesJSONArray.getJSONObject(i).getInt("number_tables"));
        }

        int numberOfRegisteredBusinesses=afmBusinessList.size();

        Business business = null;
        for(int j=0; j<numberOfRegisteredBusinesses; j++){
            business = new Business();
            business.setEmail(emailList.get(j));
            business.setPassword(passwordList.get(j));
            business.setBusinessIdName(businessIdNnameList.get(j));
            business.setAfmBusiness(afmBusinessList.get(j));
            business.setPhone(phoneList.get(j));
            business.setAddress(addressList.get(j));
            business.setPostalCode(postalCodeList.get(j));
            business.setNumberTables(numberTablesList.get(j));

            businessesList.add(business);

        }

        onlyBusinessesWorkingFor();
    }

    private void onlyBusinessesWorkingFor(){
        ArrayList<Business> tempList = new ArrayList<Business>();

        for(int i=0; i<businessesWorkingForList.size(); i++){
            for(int j=0; j<businessesList.size(); j++){
                if((int) businessesWorkingForList.get(i) == (int) businessesList.get(j).getAfmBusiness()){
                    tempList.add(businessesList.get(j));
                }
            }
        }
        businessesList = null;
        businessesList = new ArrayList<Business>();
        businessesList = tempList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_employee_account, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.displayEmployeeAccount:
                Intent i = new Intent(WorkSelectionActivity.this, EmployeeAccountActivity.class);
                i.putExtra("businessesWorkingForList", businessesWorkingForList);
                i.putExtra("currentEmployee", currentEmployee);
                i.putExtra("afm_employee", afmEmployee);
                startActivity(i);
                finish();
                break;
        }
        return true;
    }

    /**
     * The commands inside the following method are executed when the back button is pressed.
     * Here, the employee will be recommended to press the back button twice if he/she wants to go back to the previous activity.
     */
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent=new Intent(WorkSelectionActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();

            //return;
        }

        this.doubleBackToExitPressedOnce = true;

        Toast myToast = Toast.makeText(this, "Πατήστε ξανά για έξοδο από την εφαρμογή", Toast.LENGTH_SHORT);
        View myToastView = myToast.getView();

        //Gets the actual oval background of the Toast then sets the colour filter
        myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.yellowDark), PorterDuff.Mode.SRC_IN);

        //Gets the TextView from the Toast so it can be editted
        TextView myTextView = myToastView.findViewById(android.R.id.message);
        myTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

        myToast.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }

    /**
     * Whenever the activity is destroyed (finished/closed) then the following method will be called.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
