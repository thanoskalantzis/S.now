package project.snow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
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
 * ProductsSelectionActivity class.
 *
 * This class is the one responsible for placing all products onto the screen so as for the user to be able to select them, and place them into his / her basket,
 * and also, this class is responsible to understand user choices about products (which products he / she indeed selected).
 *
 * @author thanoskalantzis
 */
public class ProductsSelectionActivity extends AppCompatActivity {
    //Class variables.
    //url_get_products_afm_table variable is actually the url which corresponds to the php file for getting all products a specified business.
    private String url_get_products_afm_table;
    private SwipeRefreshLayout refreshLayout;
    private static ProductsSelectionListAdapter productsSelectionListAdapter;
    private static JSONObject productsJSONObject;
    private static ArrayList<Product> productsList;
    private static RecyclerView screenListView;
    private static Business businessSelected;
    private static Customer currentCustomer;
    private static Button basketButton;
    private int lastPosition;
    private static ArrayList<OrderItem> productsSelectionListItems;
    //Just some more class variables declarations
    private Toast myToast;
    private View myToastView;
    private TextView myToastTextView;

    /**
     * The following method is essential and necessary due to the fact that ProductsSelectionActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_selection);

        //Initialize url_get_products_afm_table.
        url_get_products_afm_table = getString(R.string.BASE_URL).concat("/connect/getproducts.php");

        //Getting parsed data from previously active activity.
        Intent intent = getIntent();
        businessSelected = (Business) getIntent().getSerializableExtra("business");
        currentCustomer = (Customer) getIntent().getSerializableExtra("customer");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Κατάλογος - " + businessSelected.getBusinessIdName());
        }

        try {
            productsSelectionActivityTask();

            //Initially show all available products
            screenListView = (RecyclerView) findViewById(R.id.productsSelectionRecyclerView);
            screenListView.setHasFixedSize(true);

            productsSelectionListItems = new ArrayList<>();
            for(int i=0; i<productsList.size(); i++){
                productsSelectionListItems.add(new OrderItem(productsList.get(i), 0));
            }

            basketButton = (Button) findViewById(R.id.basketButton);
            basketButton.setBackgroundResource(R.drawable.basketbutton);

            productsSelectionListAdapter = new ProductsSelectionListAdapter(currentCustomer, businessSelected, basketButton);
            productsSelectionListAdapter.setProductsList(productsSelectionListItems);

            productsSelectionListAdapter.setHasStableIds(true);
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
            screenListView.setAdapter(productsSelectionListAdapter);

        } catch (Exception e) {
            myToast = Toast.makeText(this,"Κανένα διαθέσιμο προϊόν στο συγκεκριμένο κατάστημα", Toast.LENGTH_LONG);
            myToastView = myToast.getView();

            //Gets the actual oval background of the Toast then sets the colour filter
            myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.yellowDark), PorterDuff.Mode.SRC_IN);

            //Gets the TextView from the Toast so it can be editted
            myToastTextView = myToastView.findViewById(android.R.id.message);
            myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

            myToast.show();

            e.printStackTrace();
        }

        /*
        refreshLayout = findViewById(R.id.productsSelectionRefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try{
                    //Call productsSelectionActivityTask
                    productsSelectionActivityTask();
                }catch (Exception e){
                    e.printStackTrace();
                }

                basketButton.setText("0");
                productsSelectionListAdapter = new ProductsSelectionListAdapter(refreshLayout.getContext(), R.layout.list_format_products_selection, businessSelected, productsList, basketButton);
                recyclerView.setAdapter(productsSelectionListAdapter);
                productsSelectionListAdapter.notifyDataSetChanged();

                refreshLayout.setRefreshing(false);
            }
        });
        */
    }

    /**
     * The following method is main task of this class.
     * During the execution time, it actually calls some other major functions and methods do get the work done.
     * All other functions and methods are listed below.
     *
     * @throws Exception
     */
    private void productsSelectionActivityTask() throws Exception{
        productsJSONObject=null;
        productsList=null;

        //Get all products of that business from online database
        int successFlag = getAllProducts();

        if(successFlag == 1){
            //Add all those products to dynamic ArrayList<Product>
            productsList = new ArrayList<Product>();
            createProductsList();
        }else{
            Toast myToast = Toast.makeText(this, "Κανένα εγγεγραμμένο προϊόν", Toast.LENGTH_LONG);

            View myToastView = myToast.getView();

            //Gets the actual oval background of the Toast then sets the colour filter
            myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

            //Gets the TextView from the Toast so it can be editted
            TextView myToastTextView = myToastView.findViewById(android.R.id.message);
            myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

            myToast.show();

            Intent intent = new Intent(ProductsSelectionActivity.this, BusinessSelectionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("customer", currentCustomer);
            startActivity(intent);
            finish();
        }
    }

    /**
     * The following method is actually the one responsible for getting all products from our online database server.
     *
     * @return 1 if all products successfully returned, 0 otherwise.
     * @throws Exception
     */
    private int getAllProducts() throws Exception{
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("afm_business", String.valueOf(businessSelected.getAfmBusiness())));

        //Using the necessary params to access online database.
        params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
        params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
        params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
        params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

        JSONParser jsonParser=new JSONParser();
        productsJSONObject = jsonParser.makeHttpRequest(url_get_products_afm_table, "GET", params);

        return productsJSONObject.getInt("success");
    }

    /**
     * Here follows the method, for creating the products list, out of which the customer will select all products he/she wants to order.
     *
     * @throws Exception
     */
    private void createProductsList() throws Exception{
        ArrayList<Integer> productIdList=new ArrayList<Integer>();
        ArrayList<String> productNameList=new ArrayList<String>();
        ArrayList<Integer> categoryIdList=new ArrayList<Integer>();
        ArrayList<String> descriptionList=new ArrayList<String>();
        ArrayList<Double> priceList=new ArrayList<Double>();

        JSONArray productsJSONArray = productsJSONObject.getJSONArray("products");

        for (int i = 0; i < productsJSONArray.length(); i++) {
            productIdList.add(productsJSONArray.getJSONObject(i).getInt("product_id"));
            productNameList.add(productsJSONArray.getJSONObject(i).getString("product_name"));
            categoryIdList.add(productsJSONArray.getJSONObject(i).getInt("category_id"));
            descriptionList.add(productsJSONArray.getJSONObject(i).getString("description"));
            priceList.add(productsJSONArray.getJSONObject(i).getDouble("price"));
        }

        int numberOfProducts=productIdList.size();

        for(int j=0; j<numberOfProducts; j++){
            Product product=new Product();
            product.setProductId(productIdList.get(j));
            product.setCategoryId(categoryIdList.get(j));
            product.setProductName(productNameList.get(j));
            product.setDescription(descriptionList.get(j));
            product.setPrice(priceList.get(j));

            productsList.add(product);
        }
    }

    private void showCategoryBasedProducts(int category){

        productsSelectionListItems = new ArrayList<>();
        for(int i=0; i<productsList.size(); i++){
            if(productsList.get(i).getCategoryId()==category) {
                productsSelectionListItems.add(new OrderItem(productsList.get(i), 0));
            }
        }

        productsSelectionListAdapter.setProductsList(productsSelectionListItems);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = this.getMenuInflater();
        menuInflater.inflate(R.menu.menu_product_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast myToast;
        View myToastView;
        TextView myToastTextView;
        switch(item.getItemId()){
            case R.id.Coffees:
                try{
                    showCategoryBasedProducts(1);
                }catch (Exception e1){
                    myToast = Toast.makeText(this, "Δεν υπάρχουν καφέδες στον κατάλογο", Toast.LENGTH_LONG);

                    myToastView = myToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter
                    myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.yellowDark), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be editted
                    myToastTextView = myToastView.findViewById(android.R.id.message);
                    myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

                    myToast.show();
                    e1.printStackTrace();
                }
                break;

            case R.id.Brews:
                try{
                    showCategoryBasedProducts(2);
                }catch (Exception e2){
                    myToast = Toast.makeText(this, "Δεν υπάρχουν ροφήματα στον κατάλογο", Toast.LENGTH_LONG);

                    myToastView = myToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter
                    myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.yellowDark), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be editted
                    myToastTextView = myToastView.findViewById(android.R.id.message);
                    myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

                    myToast.show();
                    e2.printStackTrace();
                }
                break;

            case R.id.Beverages:
                try{
                    showCategoryBasedProducts(3);
                }catch (Exception e3){
                    myToast = Toast.makeText(this, "Δεν υπάρχουν αναψυκτικά στον κατάλογο", Toast.LENGTH_LONG);

                    myToastView = myToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter
                    myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.yellowDark), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be editted
                    myToastTextView = myToastView.findViewById(android.R.id.message);
                    myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

                    myToast.show();
                    e3.printStackTrace();
                }
                break;

            case R.id.Beers:
                try{
                    showCategoryBasedProducts(4);
                }catch (Exception e4){
                    myToast = Toast.makeText(this, "Δεν υπάρχουν μπύρες στον κατάλογο", Toast.LENGTH_LONG);

                    myToastView = myToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter
                    myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.yellowDark), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be editted
                    myToastTextView = myToastView.findViewById(android.R.id.message);
                    myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

                    myToast.show();
                    e4.printStackTrace();
                }
                break;

            case R.id.Wines:
                try{
                    showCategoryBasedProducts(5);
                }catch (Exception e5){
                    myToast = Toast.makeText(this, "Δεν υπάρχουν κρασιά/ούζο/τσίπουρο/ρακές... στον κατάλογο", Toast.LENGTH_LONG);

                    myToastView = myToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter
                    myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.yellowDark), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be editted
                    myToastTextView = myToastView.findViewById(android.R.id.message);
                    myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

                    myToast.show();
                    e5.printStackTrace();
                }

                break;

            case R.id.Drinks:
                try{
                    showCategoryBasedProducts(6);
                }catch (Exception e6){
                    myToast = Toast.makeText(this, "Δεν υπάρχουν ποτά στον κατάλογο", Toast.LENGTH_LONG);

                    myToastView = myToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter
                    myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.yellowDark), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be editted
                    myToastTextView = myToastView.findViewById(android.R.id.message);
                    myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

                    myToast.show();
                    e6.printStackTrace();
                }
                break;

            case R.id.Cocktails:
                try{
                    showCategoryBasedProducts(7);
                }catch (Exception e7){
                    myToast = Toast.makeText(this, "Δεν υπάρχουν κοκτέιλ στον κατάλογο", Toast.LENGTH_LONG);

                    myToastView = myToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter
                    myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.yellowDark), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be editted
                    myToastTextView = myToastView.findViewById(android.R.id.message);
                    myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

                    myToast.show();
                    e7.printStackTrace();
                }

                break;

            case R.id.NonAlcoholicBeverages:
                try{
                    showCategoryBasedProducts(8);
                }catch (Exception e8){
                    myToast = Toast.makeText(this, "Δεν υπάρχουν μη αλκοολούχα ποτά στον κατάλογο", Toast.LENGTH_LONG);

                    myToastView = myToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter
                    myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.yellowDark), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be editted
                    myToastTextView = myToastView.findViewById(android.R.id.message);
                    myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

                    myToast.show();
                    e8.printStackTrace();
                }
                break;

            case R.id.Snacks:
                try{
                    showCategoryBasedProducts(9);
                }catch (Exception e9){
                    myToast = Toast.makeText(this, "Δεν υπάρχουν snacks στον κατάλογο", Toast.LENGTH_LONG);

                    myToastView = myToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter
                    myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.yellowDark), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be editted
                    myToastTextView = myToastView.findViewById(android.R.id.message);
                    myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

                    myToast.show();
                    e9.printStackTrace();
                }

                break;

            case R.id.Food:
                try{
                    showCategoryBasedProducts(10);
                }catch (Exception e10){
                    myToast = Toast.makeText(this, "Δεν υπάρχουν φαγητά στον κατάλογο", Toast.LENGTH_LONG);

                    myToastView = myToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter
                    myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.yellowDark), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be editted
                    myToastTextView = myToastView.findViewById(android.R.id.message);
                    myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

                    myToast.show();
                    e10.printStackTrace();
                }
                break;

            case R.id.Desserts:
                try{
                    showCategoryBasedProducts(11);
                }catch (Exception e11){
                    myToast = Toast.makeText(this, "Δεν υπάρχουν γλυκά στον κατάλογο", Toast.LENGTH_LONG);

                    myToastView = myToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter
                    myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.yellowDark), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be editted
                    myToastTextView = myToastView.findViewById(android.R.id.message);
                    myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

                    myToast.show();
                    e11.printStackTrace();
                }

                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ProductsSelectionActivity.this, BusinessSelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("customer", currentCustomer);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor e = getPrefs.edit();
        e.putInt("lastPosition", lastPosition);
        e.apply();
    }
}
