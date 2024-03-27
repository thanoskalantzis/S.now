package project.snow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * ControlEditProductActivity Class.
 *
 * This the class is the one used when a business desires to edit a specific product from business menu.
 *
 * This class is also used to entirely delete a specific product.
 * The is only one condition, to delete a specified product:
 * The product should not be contained inside an active order.
 * If the product is contained inside an active order, the business must simply try again later.
 *
 * @author thanoskalantzis
 */
public class ControlEditProductActivity extends AppCompatActivity{
    //Class variables.
    //url_edit_product_name variable is actually the url which corresponds to the php file for parsing the new product name.
    private String url_edit_product_name;
    //url_edit_description variable is actually the url which corresponds to the php file for parsing the new product description.
    private String url_edit_description;
    //url_edit_product_price variable is actually the url which corresponds to the php file for parsing the new product price.
    private String url_edit_product_price;
    //url_get_orders_afm_table variable is actually the url which corresponds to the php file for getting all orders of the specified business.
    private String url_get_orders_afm_table;
    //url_delete_product variable is actually the url which corresponds to the php file for parsing the request to delete the specified product.
    private String url_delete_product;
    private Business business;
    private Product product;
    private TextView productName;
    private TextView productDescription;
    private TextView productPrice;
    private Button controlEditName;
    private Button controlEditDescription;
    private Button controlEditPrice;
    private Button controlEditDelete;
    private TextView tvShowNumbers;

    /**
     * The following method is essential and necessary due to the fact that ControlEditProductActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_edit_product);

        //Initialize url_edit_product_name.
        url_edit_product_name = getString(R.string.BASE_URL).concat("/connect/editproductname.php");
        //Initialize url_edit_description.
        url_edit_description = getString(R.string.BASE_URL).concat("/connect/editproductdescription.php");
        //Initialize url_edit_product_price.
        url_edit_product_price = getString(R.string.BASE_URL).concat("/connect/editproductprice.php");
        //Initialize url_get_orders_afm_table.
        url_get_orders_afm_table = getString(R.string.BASE_URL).concat("/connect/getorders.php");
        //Initialize url_delete_product.
        url_delete_product = getString(R.string.BASE_URL).concat("/connect/deleteproduct.php");

        //Getting some information from the previously active activity.
        Intent intent = getIntent();
        business = (Business) getIntent().getSerializableExtra("business");
        product = (Product) getIntent().getSerializableExtra("product");

        //Setting title of the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Τροποποίηση προϊόντος");
        }

        productName = (TextView) findViewById(R.id.controlEditProductName);
        productName.setText(product.getProductName());
        productDescription = (TextView) findViewById(R.id.controlEditProductDescription);
        productDescription.setText(product.getDescription());
        productPrice = (TextView) findViewById(R.id.controlEditProductPrice);
        productPrice.setText(String.valueOf(" "+ product.getPrice()+"€"));

        //Here follows the process of editing product name.
        controlEditName = (Button) findViewById(R.id.controlEditNameButton);
        controlEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setTitle("Εισάγετε Νέο Όνομα Προϊόντος");
                alertDialogBuilder.setMessage("(Πλήθος χαρακτήρων: 50)");

                //Set up the input
                final EditText inputTextDialog = new EditText(view.getContext());
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(50);
                inputTextDialog.setFilters(FilterArray);

                inputTextDialog.setHint("Πληκτρολογήστε νέο όνομα");

                //Specify the type of input expected
                inputTextDialog.setInputType(InputType.TYPE_CLASS_TEXT);
                alertDialogBuilder.setView(inputTextDialog);

                //Set up the buttons
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Just some variables declarations
                        Context myContext = getApplicationContext();
                        int toastDuration = Toast.LENGTH_LONG;
                        Toast myToast;
                        View myView;
                        TextView myToastTextView;

                        String inputString = inputTextDialog.getText().toString();
                        inputString = inputString.trim();

                        List<NameValuePair> params1 = new ArrayList<NameValuePair>();

                        params1.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));
                        params1.add(new BasicNameValuePair("product_id", String.valueOf(product.getProductId())));
                        params1.add(new BasicNameValuePair("name_new", inputString));
                        //params1.add(new BasicNameValuePair("name_old", product.getProductName()));

                        //Using the necessary params to access online database.
                        params1.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                        params1.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                        params1.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                        params1.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                        JSONParser jsonParser = new JSONParser();
                        JSONObject jsonObject = jsonParser.makeHttpRequest(url_edit_product_name, "POST", params1);

                        try {
                            int successFlag = jsonObject.getInt("success");

                            if(successFlag == 1){
                                product.setProductName(inputString);

                                myToast = Toast.makeText(myContext, "Το όνομα άλλαξε επιτυχώς", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be editted
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.black));

                                myToast.show();

                                Intent i = new Intent(ControlEditProductActivity.this, ControlEditProductActivity.class);
                                i.putExtra("business", business);
                                i.putExtra("product", product);
                                startActivity(i);

                                finish();
                            }else{
                                myToast = Toast.makeText(myContext, "Αποτυχία αλλαγής ονόματος", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be editted
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                                myToast.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
            }
        });

        //Here follows the process of editing product description.
        controlEditDescription = (Button) findViewById(R.id.controlEditDescriptionButton);
        controlEditDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setTitle("Εισάγετε Νέα Περιγραφή Προϊόντος");
                alertDialogBuilder.setMessage("(Πλήθος χαρακτήρων: 200)");


                //Set up the input
                final EditText inputTextDialog = new EditText(view.getContext());

                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(200);
                inputTextDialog.setFilters(FilterArray);

                inputTextDialog.setHint("Πληκτρολογήστε νέα περιγραφή");

                //Specify the type of input expected
                inputTextDialog.setInputType(InputType.TYPE_CLASS_TEXT);
                alertDialogBuilder.setView(inputTextDialog);

                //Set up the buttons
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Just some variables declarations
                        Context myContext = getApplicationContext();
                        int toastDuration = Toast.LENGTH_LONG;
                        Toast myToast;
                        View myView;
                        TextView myToastTextView;

                        String inputString = inputTextDialog.getText().toString();
                        inputString = inputString.trim();

                        List<NameValuePair> params2 = new ArrayList<NameValuePair>();

                        params2.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));
                        params2.add(new BasicNameValuePair("product_id", String.valueOf(product.getProductId())));
                        params2.add(new BasicNameValuePair("description", inputString));

                        //Using the necessary params to access online database.
                        params2.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                        params2.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                        params2.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                        params2.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                        JSONParser jsonParser = new JSONParser();
                        JSONObject jsonObject = jsonParser.makeHttpRequest(url_edit_description, "POST", params2);

                        try {
                            int successFlag = jsonObject.getInt("success");

                            if(successFlag == 1){
                                product.setDescription(inputString);

                                myToast = Toast.makeText(myContext, "Η περιγραφή άλλαξε επιτυχώς", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be editted
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.black));

                                myToast.show();

                                Intent i = new Intent(ControlEditProductActivity.this, ControlEditProductActivity.class);
                                i.putExtra("business", business);
                                i.putExtra("product", product);
                                startActivity(i);

                                finish();
                            }else{
                                myToast = Toast.makeText(myContext, "Αποτυχία αλλαγής περιγραφής", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be editted
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                                myToast.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
            }
        });

        //Here follows the process of editing product price.
        controlEditPrice = (Button) findViewById(R.id.controlEditPriceButton);
        controlEditPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.HORIZONTAL);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Nέα Τιμή Προϊόντος");
                alertDialogBuilder.setMessage("Εισάγετε στα παρακάτω πεδία τη νέα τιμή για το προϊόν αυτό");

                //Set up the input
                final EditText intPartInput = new EditText(context);

                InputFilter[] FilterArrayA = new InputFilter[1];
                FilterArrayA[0] = new InputFilter.LengthFilter(3);
                intPartInput.setFilters(FilterArrayA);

                intPartInput.setHint("Ακέραιο μέρος");

                //Specify the type of input expected
                intPartInput.setInputType(InputType.TYPE_CLASS_NUMBER);

                layout.addView(intPartInput);

                final TextView dotText = new TextView(context);
                dotText.setText(".");
                dotText.setTextSize(14);
                layout.addView(dotText);

                final EditText decimalPartInput = new EditText(context);

                InputFilter[] FilterArrayΒ = new InputFilter[1];
                FilterArrayΒ[0] = new InputFilter.LengthFilter(2);
                decimalPartInput.setFilters(FilterArrayΒ);

                decimalPartInput.setHint("Δεκαδικό μέρος");

                //Specify the type of input expected
                decimalPartInput.setInputType(InputType.TYPE_CLASS_NUMBER);

                layout.addView(decimalPartInput);

                alertDialogBuilder.setView(layout);

                //Set up the buttons
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Just some variables declarations
                        Context myContext = getApplicationContext();
                        int toastDuration = Toast.LENGTH_LONG;
                        Toast myToast;
                        View myView;
                        TextView myToastTextView;

                        String inputStringΑ = intPartInput.getText().toString().trim();
                        int intPart = Integer.parseInt(inputStringΑ);

                        String inputStringB = decimalPartInput.getText().toString().trim();
                        int decimalPart = Integer.parseInt(inputStringB);

                        double givenPrice= intPart + (double)(((double) decimalPart)/100);

                        List<NameValuePair> params3 = new ArrayList<NameValuePair>();

                        params3.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));
                        params3.add(new BasicNameValuePair("product_id", String.valueOf(product.getProductId())));
                        params3.add(new BasicNameValuePair("price_new", String.valueOf(givenPrice)));

                        //Using the necessary params to access online database.
                        params3.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                        params3.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                        params3.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                        params3.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                        JSONParser jsonParser = new JSONParser();
                        JSONObject jsonObject = jsonParser.makeHttpRequest(url_edit_product_price, "POST", params3);

                        try {
                            int successFlag = jsonObject.getInt("success");

                            if(successFlag == 1){
                                product.setPrice(givenPrice);

                                myToast = Toast.makeText(myContext, "Η τιμή άλλαξε επιτυχώς", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be editted
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.black));

                                myToast.show();

                                Intent i = new Intent(ControlEditProductActivity.this, ControlEditProductActivity.class);
                                i.putExtra("business", business);
                                i.putExtra("product", product);
                                startActivity(i);

                                finish();
                            }else{
                                myToast = Toast.makeText(myContext, "Αποτυχία αλλαγής τιμής", toastDuration);
                                myView = myToast.getView();

                                //Gets the actual oval background of the Toast then sets the colour filter
                                myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                //Gets the TextView from the Toast so it can be editted
                                myToastTextView = myView.findViewById(android.R.id.message);
                                myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                                myToast.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
            }
        });

        //Here follows the process of deleting the specified product from business menu.
        controlEditDelete = (Button) findViewById(R.id.deleteProductButton);
        controlEditDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                alertDialogBuilder.setTitle("Διαγραφή προϊόντος");
                alertDialogBuilder.setMessage("Το προϊόν θα διαγραφεί ολοκληρωτικά!\n\nΣυνέχεια διαγραφής;\n");

                //Set up the buttons
                alertDialogBuilder.setPositiveButton("ΝΑΙ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Just some variables declarations
                        Context myContext = getApplicationContext();
                        int toastDuration = Toast.LENGTH_LONG;
                        Toast myToast;
                        View myView;
                        TextView myToastTextView;

                        //The product will only be deleted if deleteFlag stays true
                        boolean deleteFlag = true;

                        List<NameValuePair> params4 = new ArrayList<NameValuePair>();

                        params4.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));

                        //Using the necessary params to access online database.
                        params4.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                        params4.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                        params4.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                        params4.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                        JSONParser jsonParser1=new JSONParser();
                        JSONObject jsonObject1 = jsonParser1.makeHttpRequest(url_get_orders_afm_table, "GET", params4);

                        ArrayList<FinalOrder> finalOrdersList = null;

                        try {
                            int successFlag1 = jsonObject1.getInt("success");

                            if (successFlag1 == 1) {
                                ArrayList<Integer> productIdList = new ArrayList<Integer>();
                                ArrayList<Integer> categoryIdList = new ArrayList<Integer>();
                                ArrayList<String> productNameList = new ArrayList<String>();
                                ArrayList<String> descriptionList = new ArrayList<String>();
                                ArrayList<Double> priceList = new ArrayList<Double>();
                                ArrayList<Integer> quantityList = new ArrayList<Integer>();
                                ArrayList<Integer> numberTableList = new ArrayList<Integer>();
                                ArrayList<String> dateTimeList = new ArrayList<String>();
                                ArrayList<Boolean> isActive = new ArrayList<Boolean>();

                                JSONArray ordersJSONArray = jsonObject1.getJSONArray("orders");

                                for (int i = 0; i < ordersJSONArray.length(); i++) {
                                    productIdList.add(ordersJSONArray.getJSONObject(i).getInt("product_id"));
                                    categoryIdList.add(ordersJSONArray.getJSONObject(i).getInt("category_id"));
                                    productNameList.add(ordersJSONArray.getJSONObject(i).getString("product_name"));
                                    descriptionList.add(ordersJSONArray.getJSONObject(i).getString("description"));
                                    priceList.add(ordersJSONArray.getJSONObject(i).getDouble("price"));
                                    quantityList.add(ordersJSONArray.getJSONObject(i).getInt("quantity"));
                                    numberTableList.add(ordersJSONArray.getJSONObject(i).getInt("number_table"));
                                    dateTimeList.add(ordersJSONArray.getJSONObject(i).getString("date_time"));
                                    if (ordersJSONArray.getJSONObject(i).getInt("isActive") == 1) {
                                        isActive.add(true);
                                    } else {
                                        isActive.add(false);
                                    }
                                }

                                int numberOfOrderItems = productIdList.size();
                                finalOrdersList = new ArrayList<FinalOrder>();

                                ArrayList<Integer> uniqueNumberTableList = new ArrayList<Integer>();

                                //Here we figure out how many tables placed successfully an order
                                int tempNumberTable = -1;
                                for (int j = 0; j < numberOfOrderItems; j++) {
                                    tempNumberTable = numberTableList.get(j);
                                    if (!uniqueNumberTableList.contains(tempNumberTable)) {
                                        uniqueNumberTableList.add(numberTableList.get(j));
                                    }
                                }

                                //For each different table that successfully placed an order create a FinalOrder object
                                tempNumberTable = -1;
                                for (int k = 0; k < uniqueNumberTableList.size(); k++) {

                                    tempNumberTable = uniqueNumberTableList.get(k);
                                    FinalOrder finalOrder = new FinalOrder();

                                    for (int t = 0; t < numberOfOrderItems; t++) {
                                        if (tempNumberTable == numberTableList.get(t)) {
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
                                            if (finalOrder.getIsActive()) {
                                                finalOrder.setOrderStatus("Εκκρεμεί");
                                            } else {
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
                                    for(int p=0; p<finalOrdersList.size(); p++){
                                        if(finalOrdersList.get(p).getIsActive()){
                                            tempFinalOrdersList.add(finalOrdersList.get(p));
                                        }
                                    }
                                    finalOrdersList= tempFinalOrdersList;

                                    deleteFlag = true;
                                    for(int z=0; z<finalOrdersList.size(); z++) {
                                        int subSize = finalOrdersList.get(z).getOrderItemList().size();
                                        for(int x = 0; x<subSize; x++){
                                            if(finalOrdersList.get(z).getOrderItemList().get(x).getProduct().getProductId() == product.getProductId()){
                                                deleteFlag = false;
                                                break;
                                            }
                                        }
                                    }
                                }else{
                                    deleteFlag = true;
                                }
                            } else {
                                deleteFlag = true;
                            }
                        }catch (Exception e1){
                            e1.printStackTrace();
                        }

                        //The product will only be deleted if deleteFlag becomes true
                        if(deleteFlag) {
                            int IDofProductToDelete = product.getProductId();

                            List<NameValuePair> params5 = new ArrayList<NameValuePair>();
                            params5.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));
                            params5.add(new BasicNameValuePair("product_id", String.valueOf(IDofProductToDelete)));

                            //Using the necessary params to access online database.
                            params5.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                            params5.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                            params5.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                            params5.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                            JSONParser jsonParser2 = new JSONParser();
                            JSONObject jsonObject2 = jsonParser2.makeHttpRequest(url_delete_product, "POST", params5);

                            int successFlag2 = 0;
                            try {
                                successFlag2 = jsonObject2.getInt("success");

                                if(successFlag2 == 1){
                                    myToast = Toast.makeText(myContext, "Το προϊόν διαγράφηκε επιτυχώς", toastDuration);
                                    myView = myToast.getView();

                                    //Gets the actual oval background of the Toast then sets the colour filter
                                    myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                                    //Gets the TextView from the Toast so it can be editted
                                    myToastTextView = myView.findViewById(android.R.id.message);
                                    myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.black));

                                    myToast.show();

                                    Intent i = new Intent(ControlEditProductActivity.this, EditProductsActivity.class);
                                    i.putExtra("business", business);
                                    startActivity(i);

                                    finish();
                                }else{
                                    myToast = Toast.makeText(myContext, "Αποτυχία διαγραφής προϊόντος", toastDuration);
                                    myView = myToast.getView();

                                    //Gets the actual oval background of the Toast then sets the colour filter
                                    myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                    //Gets the TextView from the Toast so it can be editted
                                    myToastTextView = myView.findViewById(android.R.id.message);
                                    myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                                    myToast.show();
                                }
                            } catch (JSONException e2) {
                                e2.printStackTrace();
                            }
                        }else{
                            final AlertDialog.Builder addEmployeeErrorAlert = new AlertDialog.Builder(ControlEditProductActivity.this);
                            addEmployeeErrorAlert.setTitle("Αποτυχία διαγραφής προϊόντος");
                            addEmployeeErrorAlert.setMessage("Το προϊόν που επιθυμείτε να διαγράψετε είναι \"ενεργό\" (δηλαδή εμπεριέχεται σε ενεργή παραγγελία αυτή τη στιγμή).\n\nΠροσέξτε, ότι για να διαγράψετε ένα προϊόν πρέπει αυτό να μην είναι \"ενεργό\" (όπως αυτό ορίστηκε προηγουμένως).\n\nΠροσπαθήστε αργότερα.");
                            addEmployeeErrorAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            addEmployeeErrorAlert.show();
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
            }
        });
    }

    /**
     * The commands inside the following method are executed when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
       Intent intent=new Intent(ControlEditProductActivity.this, EditProductsActivity.class);
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
    }
}