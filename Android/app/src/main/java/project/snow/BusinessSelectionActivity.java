package project.snow;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
 * BusinessSelectionActivity Class.
 *
 * This class is used so as the customer to choose the business he/she wants to order from.
 *
 * @author thanoskalantzis
 */
public class BusinessSelectionActivity extends AppCompatActivity {
    //Class variables.
    //url_all_businesses variable is actually the url which corresponds to the php file for getting all registered businesses.
    //Those businesses will be listed on screen so as for the user to select.
    private String url_all_businesses;
    private static Customer currentCustomer;
    private SwipeRefreshLayout refreshLayout;
    private SearchView businessSearchInput;
    private static BusinessSelectionListAdapter businessSelectionListAdapter;
    private static JSONObject businessesJSONObject;
    private static ArrayList<Business> businessesList;
    private static RecyclerView recyclerView;

    /**
     * The following method is essential and necessary due to the fact that BusinessRegisterActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_selection);

        //Initialize url_all_businesses.
        url_all_businesses = getString(R.string.BASE_URL).concat("/connect/getbusinesses.php");

        //Getting parsed data from the previously active activity.
        currentCustomer = (Customer) getIntent().getSerializableExtra("customer");

        //Setting the title of the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Λίστα Καταστημάτων");
        }

        try{
            //Call businessSelectionActivityTask.
            businessSelectionActivityTask();
        }catch (Exception e){
            e.printStackTrace();
        }

        //Show all registered businesses on screen so as for the user to select from where he/she wants to order from.
        recyclerView = (RecyclerView) findViewById(R.id.businessSelectionRecyclerView);
        businessSelectionListAdapter = new BusinessSelectionListAdapter(currentCustomer, businessesList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(businessSelectionListAdapter);

        refreshLayout = findViewById(R.id.businessSelectionRefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try{
                    //Call businessSelectionActivityTask
                    businessSelectionActivityTask();
                }catch (Exception e){
                    e.printStackTrace();
                }

                businessSelectionListAdapter = new BusinessSelectionListAdapter(currentCustomer, businessesList);
                recyclerView.setAdapter(businessSelectionListAdapter);
                businessSelectionListAdapter.notifyDataSetChanged();

                refreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Here, we will use 2 types of menus.
     * 1. We will create a search bar in order for the customer to be able to search for the business he/she wants to order from. (We will create this search bar on the action bar of the activity),
     * 2. We will create an item menu, which when selected will show customer profile. (After that, customer will be able to edit some major account information).
     *
     * @param menu
     * @return super.onCreateOptionsMenu(menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer_account, menu);
        getMenuInflater().inflate(R.menu.menu_business_search, menu);

        MenuItem menuItem = menu.findItem(R.id.businessSearch);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint(getResources().getString(R.string.businessSearchToolbarText));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                businessSelectionListAdapter.getFilter().filter(newText);

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * The function below is used to identify which item the user selected from the given menu.
     * Here, there is only 1 available item,
     * which when selected will show customer profile.
     * (After that, customer will be able to edit some major account information).
     *
     * @param item the item selected.
     * @return true always.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.displayCustomerAccount:
                Intent intent = new Intent(BusinessSelectionActivity.this, CustomerAccountActivity.class);
                intent.putExtra("currentCustomer", currentCustomer);
                startActivity(intent);

                finish();
                break;
        }
        return true;
    }

    /**
     * The following method is main task of this class.
     * During the execution time, it actually calls some other major functions and methods do get the work done.
     * All other functions and methods are listed below.
     *
     * @throws Exception
     */
    private void businessSelectionActivityTask() throws Exception{
        //Just some variables declarations.
        Toast myToast;
        View myToastView;
        TextView myTextView;

        businessesJSONObject=null;
        businessesList=null;

        //Get all registered businesses from online database server.
        int successFlag = getAllBusinesses();

        if(successFlag == 1){
            //Add all businesses to dynamic ArrayList<Business>.
            businessesList = new ArrayList<Business>();
            createBusinessesList();
        }else{
            myToast = Toast.makeText(this, "Καμία εγγεγραμμένη επιχείρηση", Toast.LENGTH_LONG);
            myToastView = myToast.getView();

            //Gets the actual oval background of the Toast then sets the colour filter
            myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

            //Gets the TextView from the Toast so it can be editted
            myTextView = myToastView.findViewById(android.R.id.message);
            myTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

            myToast.show();
        }
    }

    /**
     * The following method is actually the one responsible for getting all businesses from our online database server.
     *
     * @return 1 if all businesses successfully returned, 0 otherwise.
     * @throws Exception
     */
    private int getAllBusinesses() throws Exception{
        //Creating a new JSONParser instance (object) which is responsible from the communication process.
        JSONParser jsonParser=new JSONParser();

        //params is the dynamic array of those parameters which will be used to send data to our online database.
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        //Using the necessary params to access online database.
        params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
        params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
        params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
        params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

        //Executing HTTP GET Request, without any parameter, so as to successfully get all registered businesses.
        businessesJSONObject = jsonParser.makeHttpRequest(url_all_businesses, "GET", params);

        return businessesJSONObject.getInt("success");
    }

    /**
     * Here follows the method, for creating the businesses list, out of which the customer will select the business where he/she wants to order from.
     *
     * @throws Exception
     */
    private void createBusinessesList() throws Exception{
        ArrayList<String> emailList=new ArrayList<String>();
        ArrayList<String> passwordList=new ArrayList<String>();
        ArrayList<String> businessIdNameList=new ArrayList<String>();
        ArrayList<Integer> afmBusinessList=new ArrayList<Integer>();
        ArrayList<Long> phoneList=new ArrayList<Long>();
        ArrayList<String> addressList=new ArrayList<String>();
        ArrayList<Integer> postalCodeList=new ArrayList<Integer>();
        ArrayList<Integer> numberTablesList=new ArrayList<Integer>();

        JSONArray businessesJSONArray = businessesJSONObject.getJSONArray("businesses");

        for (int i = 0; i < businessesJSONArray.length(); i++) {
            emailList.add(businessesJSONArray.getJSONObject(i).getString("email"));
            passwordList.add(businessesJSONArray.getJSONObject(i).getString("password"));
            businessIdNameList.add(businessesJSONArray.getJSONObject(i).getString("business_id_name"));
            afmBusinessList.add(businessesJSONArray.getJSONObject(i).getInt("afm_business"));
            phoneList.add(businessesJSONArray.getJSONObject(i).getLong("phone"));
            addressList.add(businessesJSONArray.getJSONObject(i).getString("address"));
            postalCodeList.add(businessesJSONArray.getJSONObject(i).getInt("postal_code"));
            numberTablesList.add(businessesJSONArray.getJSONObject(i).getInt("number_tables"));
        }

        int numberOfRegisteredBusinesses=afmBusinessList.size();

        for(int j=0; j<numberOfRegisteredBusinesses; j++){
            Business business=new Business();
            business.setEmail(emailList.get(j));
            business.setPassword(passwordList.get(j));
            business.setBusinessIdName(businessIdNameList.get(j));
            business.setAfmBusiness(afmBusinessList.get(j));
            business.setPhone(phoneList.get(j));
            business.setAddress(addressList.get(j));
            business.setPostalCode(postalCodeList.get(j));
            business.setNumberTables(numberTablesList.get(j));

            businessesList.add(business);
        }
    }

    private void showCustomerAccount(){}

    /**
     * The commands inside the following method are executed when the back button is pressed.
     * Here, the user will be recommended to press the back button twice if he/she wants to go back to the previous activity.
     */
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent=new Intent(BusinessSelectionActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            super.onBackPressed();

            //return;
        }

        this.doubleBackToExitPressedOnce = true;

        Toast myToast = Toast.makeText(this, "Πατήστε ξανά για έξοδο από την εφαρμογή", Toast.LENGTH_SHORT);
        View myToastView = myToast.getView();

        //Gets the actual oval background of the Toast then sets the colour filter.
        myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.yellowDark), PorterDuff.Mode.SRC_IN);

        //Gets the TextView from the Toast so it can be edited.
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
