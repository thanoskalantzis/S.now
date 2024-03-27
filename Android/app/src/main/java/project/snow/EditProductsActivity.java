package project.snow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
 * EditProductsActivity Class.
 *
 * This class is actually responsible for just showing all business products so as each business to able able to select a product and move on to the product edit phase.
 *
 * @author thanoskalantzis
 */
public class EditProductsActivity extends AppCompatActivity {
    //Class variables.
    //url_get_products_afm_table variable is actually the url which corresponds to the php file getting all products of a specified business.
    private String url_get_products_afm_table;
    //Just some more class variables.
    private static EditProductsListAdapter editProductsListAdapter;
    private static JSONObject productsJSONObject;
    private static ArrayList<Product> productsList;
    private static RecyclerView screenListView;
    private int lastPosition;
    private Business business;
    private Toast myToast;
    private View myToastView;
    private TextView myToastTextView;

    /**
     * The following method is essential and necessary due to the fact that EditProductsActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_products);

        //Initialize url_get_products_afm_table.
        url_get_products_afm_table = getString(R.string.BASE_URL).concat("/connect/getproducts.php");

        //Getting parsed information (data) from previously active activity.
        Intent intent = getIntent();
        business = (Business) getIntent().getSerializableExtra("business");

        //Setting the title of the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Λίστα προϊόντων");
        }

        try {
            editProductsActivityTask();

            //Show
            screenListView = (RecyclerView) findViewById(R.id.editProductsRecyclerView);
            screenListView.setHasFixedSize(true);

            editProductsListAdapter = new EditProductsListAdapter(business, productsList);
            editProductsListAdapter.setHasStableIds(true);
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
            screenListView.setAdapter(editProductsListAdapter);

        } catch (Exception e) {
            myToast = Toast.makeText(this,"Δεν έχετε προσθέσει ακόμα προϊόντα", Toast.LENGTH_LONG);
            myToastView = myToast.getView();

            //Gets the actual oval background of the Toast then sets the colour filter
            myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.yellowDark), PorterDuff.Mode.SRC_IN);

            //Gets the TextView from the Toast so it can be editted
            myToastTextView = myToastView.findViewById(android.R.id.message);
            myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

            myToast.show();

            e.printStackTrace();
        }
    }

    /**
     * This is the method figures out whether there are or whether there are not products stored online for the specified business.
     *
     * If the there are products stored online, the corresponding function inserts them into a dynamic list.
     *
     * @throws Exception
     */
    private void editProductsActivityTask() throws Exception{
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
        }
    }

    /**
     * Function that communicates with database server and gets the response.
     *
     * @return
     * @throws Exception
     */
    private int getAllProducts() throws Exception{
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));

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
     * Here follows the method that creates the dynamic products list (if there are products stored online).
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

    /**
     * The commands inside the following method are executed when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditProductsActivity.this, BusinessActivity.class);
        intent.putExtra("business", business);
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
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor e = getPrefs.edit();
        e.putInt("lastPosition", lastPosition);
        e.apply();
    }
}

