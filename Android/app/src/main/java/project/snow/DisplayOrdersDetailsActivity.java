package project.snow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * DisplayOrdersDetailsActivity Class.
 *
 * This class is used so as an employee to be able to select a specific order (so as to later control it) from the entire list of orders.
 *
 * @author thanoskalantzis
 */
public class DisplayOrdersDetailsActivity extends AppCompatActivity {
    //Class variables.
    //url_get_orders_afm_table variable is actually the url which corresponds to the php file for getting all orders of the current business that the current employee works for.
    private String url_get_orders_afm_table;
    //url_remove_working_employee variable is actually the url which corresponds to the php file for deleting workplace.
    private String url_remove_working_employee;
    private static JSONObject jsonObject;
    private RecyclerView screenListView;
    private DisplayOrdersDetailsListAdapter displayOrdersListAdapter;
    private int lastPosition;
    private static ArrayList<FinalOrder> finalOrdersList;
    private static Business business;
    private static Employee currentEmployee;
    private static int afmEmployee;
    private ActionBar actionBar;
    private static ArrayList<Business> businessesList;

    /**
     * The following method is essential and necessary due to the fact that DisplayOrdersDetailsActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_orders_details);

        //Initialize url_get_orders_afm_table.
        url_get_orders_afm_table = getString(R.string.BASE_URL).concat("/connect/getorders.php");
        //Initialize url_remove_working_employee.
        url_remove_working_employee = getString(R.string.BASE_URL).concat("/connect/removeworkingemployee.php");

        //Getting parsed data from the previously active activity.
        Intent intent = getIntent();
        business = (Business) getIntent().getSerializableExtra("Business");
        businessesList = (ArrayList<Business>) getIntent().getSerializableExtra("businessesWorkingForList");
        currentEmployee = (Employee) getIntent().getSerializableExtra("currentEmployee");
        afmEmployee = (int) getIntent().getSerializableExtra("afm_employee");

        actionBar = getSupportActionBar();

        displayOrders(true);

    }

    /**
     * Function responsible for creating the menu list at this point.
     *
     * @param menu
     * @return true always.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_display_orders_xwork, menu);
        return true;
    }

    /**
     * The function bellow is the one used so as the program to control what happens whenever the current employee selects a menu item.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.displayActiveOrders:
                displayOrders(true);
                break;

            case R.id.displayCompletedOrders:
                displayOrders(false);
                break;

            case R.id.deleteWorkplace:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Διαγραφή χώρου εργασίας");
                alertDialogBuilder.setMessage("Επιλέξτε \"ΝΑΙ\", αν σταματήσατε να εργάζεστε σε αυτήν την επιχείρηση.\n\nΠροσοχή, η ενέργεια δεν μπορεί να αναιρεθεί!\n");

                //Set up the buttons
                alertDialogBuilder.setPositiveButton("NAI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Just some variables declarations
                        Context myContext = getApplicationContext();
                        int toastDuration = Toast.LENGTH_LONG;
                        Toast myToast;
                        View myView;
                        TextView myToastTextView;

                        try{
                            //Delete working employee
                            List<NameValuePair> params1 = new ArrayList<NameValuePair>();

                            params1.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));
                            params1.add(new BasicNameValuePair("afm_employee", String.valueOf(afmEmployee)));

                            //Using the necessary params to access online database.
                            params1.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                            params1.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                            params1.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                            params1.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                            JSONParser jsonParser = new JSONParser();
                            JSONObject jsonObject = jsonParser.makeHttpRequest(url_remove_working_employee, "POST", params1);

                            int successFlag = jsonObject.getInt("success");

                            if (successFlag == 1) {

                                myToast = Toast.makeText(myContext, "Σας αφαιρέσαμε επιτυχώς από το εργατικό δυναμικό της επιχείρησης \""+business.getBusinessIdName()+"\"", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be editted
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.black));

                                myToast.show();

                                int indexOfXworkplace = -1;
                                for(int i=0; i<businessesList.size(); i++){
                                    if(businessesList.get(i).getAfmBusiness() == business.getAfmBusiness()){
                                        indexOfXworkplace = i;
                                        break;
                                    }
                                }
                                businessesList.remove(indexOfXworkplace);

                                ArrayList<Integer> finalBusinessesList = new ArrayList<Integer>();
                                for(int j=0; j<businessesList.size(); j++){
                                    finalBusinessesList.add(businessesList.get(j).getAfmBusiness());
                                }

                                Intent intent = new Intent(DisplayOrdersDetailsActivity.this, WorkSelectionActivity.class);
                                intent.putExtra("businessesWorkingForList", finalBusinessesList);
                                intent.putExtra("currentEmployee", currentEmployee);
                                intent.putExtra("afm_employee", afmEmployee);
                                startActivity(intent);

                                finish();
                            } else {
                                myToast = Toast.makeText(myContext, "Αποτυχία αφαίρεσης από το εργατικό δυναμικό της επιχείρησης \""+business.getBusinessIdName()+"\".\nΠαρακαλούμε επικοινωνήστε μαζί μας", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be editted
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                                myToast.show();
                            }
                        }catch (Exception e2){
                            final AlertDialog.Builder addEmployeeErrorAlert = new AlertDialog.Builder(DisplayOrdersDetailsActivity.this);
                            addEmployeeErrorAlert.setTitle("Αποτυχία αφαίρεσης από το εργατικό δυναμικό");
                            addEmployeeErrorAlert.setMessage("Λόγος αποτυχίας:\nΣας αφαίρεσε πρώτα η επιχείρηση.\n\nΘα ήταν χαρά μας να σας έχουμε ξανά σύντομα κοντά μας!");
                            addEmployeeErrorAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            addEmployeeErrorAlert.show();

                            e2.printStackTrace();

                            Intent intent = new Intent(DisplayOrdersDetailsActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("ΑΚΥΡΩΣΗ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialogBuilder.show();
                break;

        }
        return true;
    }

    /**
     * Here follows the method for deciding which orders will be shown back on screen.
     *
     * @param isActiveFlag is the parameter based on which the program will decide whether to show active or incactive orders.
     */
    private void displayOrders(boolean isActiveFlag){
        if (actionBar != null) {
            if(isActiveFlag){
                actionBar.setTitle("Ενεργές Παραγγελίες");
            }else {
                actionBar.setTitle("Ολοκληρωμένες Παραγγελίες");
            }
        }

        try {
            createDisplayOrdersList(isActiveFlag);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Here is the method responsible for creating the list of orders about to be shown on screen.
     *
     * @param isActiveFlag if isActiveFlag == true, then the program will show only the active orders, else if isActiveFlag == false, then the program will show only the inactive orders.
     * @throws Exception
     */
    private void createDisplayOrdersList(boolean isActiveFlag) throws Exception{
        //Declaring some variables
        int toastDuration = Toast.LENGTH_LONG;
        Toast errorToast;
        View myErrorView;
        TextView errorTextView;

        List<NameValuePair> params2 = new ArrayList<NameValuePair>();

        params2.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));

        //Using the necessary params to access online database.
        params2.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
        params2.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
        params2.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
        params2.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

        JSONParser jsonParser=new JSONParser();
        jsonObject = jsonParser.makeHttpRequest(url_get_orders_afm_table, "GET", params2);

        int successFlag = jsonObject.getInt("success");

        if(successFlag == 1){
            ArrayList<Integer> productIdList=new ArrayList<Integer>();
            ArrayList<Integer> categoryIdList=new ArrayList<Integer>();
            ArrayList<String> productNameList=new ArrayList<String>();
            ArrayList<String> descriptionList=new ArrayList<String>();
            ArrayList<Double> priceList=new ArrayList<Double>();
            ArrayList<Integer> quantityList=new ArrayList<Integer>();
            ArrayList<Integer> numberTableList=new ArrayList<Integer>();
            ArrayList<String> dateTimeList=new ArrayList<String>();
            ArrayList<Boolean> isActive=new ArrayList<Boolean>();

            JSONArray ordersJSONArray = jsonObject.getJSONArray("orders");

            for (int i = 0; i < ordersJSONArray.length(); i++) {
                productIdList.add(ordersJSONArray.getJSONObject(i).getInt("product_id"));
                categoryIdList.add(ordersJSONArray.getJSONObject(i).getInt("category_id"));
                productNameList.add(ordersJSONArray.getJSONObject(i).getString("product_name"));
                descriptionList.add(ordersJSONArray.getJSONObject(i).getString("description"));
                priceList.add(ordersJSONArray.getJSONObject(i).getDouble("price"));
                quantityList.add(ordersJSONArray.getJSONObject(i).getInt("quantity"));
                numberTableList.add(ordersJSONArray.getJSONObject(i).getInt("number_table"));
                dateTimeList.add(ordersJSONArray.getJSONObject(i).getString("date_time"));
                if(ordersJSONArray.getJSONObject(i).getInt("isActive")==1){
                    isActive.add(true);
                }else{
                    isActive.add(false);
                }
            }

            int numberOfOrderItems=productIdList.size();
            finalOrdersList = new ArrayList<FinalOrder>();

            ArrayList<Integer> uniqueNumberTableList = new ArrayList<Integer>();

            //Here we figure out how many tables placed successfully an order
            int tempNumberTable = -1;
            for (int j = 0; j < numberOfOrderItems; j++) {
                tempNumberTable = numberTableList.get(j);
                if( !uniqueNumberTableList.contains(tempNumberTable)){
                    uniqueNumberTableList.add(numberTableList.get(j));
                }
            }

            //For each different table that successfully placed an order create a FinalOrder object
            tempNumberTable = -1;
            for(int k=0; k<uniqueNumberTableList.size(); k++){

                tempNumberTable = uniqueNumberTableList.get(k);
                FinalOrder finalOrder = new FinalOrder();

                for(int t=0; t<numberOfOrderItems; t++){
                    if( tempNumberTable==numberTableList.get(t) ){
                        finalOrder.setNumberTable(tempNumberTable);

                        OrderItem tempOrderItem = new OrderItem();

                        Product tempProduct = new Product();
                        tempProduct.setProductId(productIdList.get(t));
                        tempProduct.setCategoryId(categoryIdList.get(t));
                        tempProduct.setProductName(productNameList.get(t));
                        tempProduct.setDescription(descriptionList.get(t));
                        tempProduct.setPrice(priceList.get(t));

                        tempOrderItem.setProduct(tempProduct);
                        tempOrderItem.setQuantity(quantityList.get(t));

                        finalOrder.setDateTime(dateTimeList.get(t));
                        finalOrder.setIsActive(isActive.get(t));
                        if(finalOrder.getIsActive()){
                            finalOrder.setOrderStatus("Εκκρεμεί");
                        }else{
                            finalOrder.setOrderStatus("Ολοκληρώθηκε");
                        }
                        finalOrder.setIsActive(isActive.get(t));
                        finalOrder.getOrderItemList().add(tempOrderItem);
                    }
                }
                finalOrdersList.add(finalOrder);
            }

            if(finalOrdersList.size()>0){
                ArrayList<FinalOrder> tempFinalOrdersList = new ArrayList<FinalOrder>();
                if(isActiveFlag){
                    for(int p=0; p<finalOrdersList.size(); p++){
                        if(finalOrdersList.get((p)).getIsActive()){
                            tempFinalOrdersList.add(finalOrdersList.get(p));
                        }
                    }
                }else{
                    for(int p=0; p<finalOrdersList.size(); p++){
                        if( !finalOrdersList.get((p)).getIsActive() ){
                            tempFinalOrdersList.add(finalOrdersList.get(p));
                        }
                    }
                }
                finalOrdersList= tempFinalOrdersList;

                //Show
                screenListView = (RecyclerView) findViewById(R.id.displayOrdersRecyclerView);
                screenListView.setHasFixedSize(true);

                displayOrdersListAdapter = new DisplayOrdersDetailsListAdapter(currentEmployee, afmEmployee, businessesList, finalOrdersList, business);
                displayOrdersListAdapter.setHasStableIds(true);
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
                screenListView.setAdapter(displayOrdersListAdapter);
            }else{
                errorToast = Toast.makeText(this, "Δεν υπάρχουν άλλες παραγγελίες", toastDuration);

                myErrorView = errorToast.getView();

                //Gets the actual oval background of the Toast then sets the colour filter
                myErrorView.getBackground().setColorFilter(myErrorView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                //Gets the TextView from the Toast so it can be editted
                errorTextView = myErrorView.findViewById(android.R.id.message);
                errorTextView.setTextColor(myErrorView.getContext().getResources().getColor(R.color.white));

                errorToast.show();
            }
        }else{
            errorToast = Toast.makeText(this, "Δεν υπάρχουν άλλες παραγγελίες", toastDuration);

            myErrorView = errorToast.getView();

            //Gets the actual oval background of the Toast then sets the colour filter
            myErrorView.getBackground().setColorFilter(myErrorView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

            //Gets the TextView from the Toast so it can be editted
            errorTextView = myErrorView.findViewById(android.R.id.message);
            errorTextView.setTextColor(myErrorView.getContext().getResources().getColor(R.color.white));

            errorToast.show();
        }
    }

    /**
     * Here is what happes if this Activity is to be restarted.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        reload();
    }

    private void reload(){
        displayOrders(true);
    }

    /**
     * The commands inside the following method are executed when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        ArrayList<Integer> afmBusinessesWorkingFor = new ArrayList<Integer>();
        for(int i=0; i<businessesList.size(); i++){
            afmBusinessesWorkingFor.add(businessesList.get(i).getAfmBusiness());
        }
        Intent intent=new Intent(DisplayOrdersDetailsActivity.this, WorkSelectionActivity.class);
        intent.putExtra("businessesWorkingForList", afmBusinessesWorkingFor);
        intent.putExtra("currentEmployee", currentEmployee);
        intent.putExtra("afm_employee", afmEmployee);
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
