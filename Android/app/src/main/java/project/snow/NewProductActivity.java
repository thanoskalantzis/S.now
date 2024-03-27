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
import android.view.MenuItem;
import android.widget.PopupMenu;
import java.util.ArrayList;
import java.util.List;

/**
 * NewProductActivity class.
 *
 * This class is the one responsible for controlling the new product registration process.
 *
 * @author thanoskalantzis
 */
public class NewProductActivity extends AppCompatActivity {
    //Class variables.
    //url_add_new_product variable is actually the url which corresponds to the php file for registering (adding, creating) a new product row to the corresponding table of products.
    private String url_add_new_product;
    //Just some more variables declarations.
    private Business business;
    private String newProductIdNameInput;
    private String newDescriptionInput;
    private int newProductPriceA;
    private int newProductPriceB;
    private double newProductPrice;
    private Button selectCategoryButton;
    private Button addNewProductButton;
    private static int productCategory;
    private Toast myToast;
    private View myToastView;
    private TextView myToastTextView;

    /**
     * The following method is essential and necessary due to the fact that NewProductActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        //Initialize url_add_new_product.
        url_add_new_product = getString(R.string.BASE_URL).concat("/connect/addproduct.php");

        //Getting parsed information from previously active activity.
        business = (Business) getIntent().getSerializableExtra("business");

        //Setting the title of the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Νέο Προϊόν");
        }

        TextView priceExplained = (TextView) findViewById(R.id.newProductPriceExplained);
        priceExplained.setText(new String(" "+"(Υπόδειγμα μορφής: 12.50€)"));

        //Here follows the category selection process.
        selectCategoryButton = (Button) findViewById(R.id.selectCategoryButton);
        selectCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(NewProductActivity.this, selectCategoryButton);

                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_product_category, popup.getMenu());

                //Registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {
                        String categorySelected = String.valueOf(item.getTitle());
                        categorySelected = categorySelected.trim();

                        if(categorySelected.equals("Καφέδες")){
                            selectCategoryButton.setText("Κατηγορία: Καφέδες");
                            productCategory=1;
                        }else if(categorySelected.equals("Ροφήματα/Χυμοί")){
                            selectCategoryButton.setText("Κατηγορία: Ροφήματα/Χυμοί");
                            productCategory=2;
                        }else if(categorySelected.equals("Αναψυκτικά")){
                            selectCategoryButton.setText("Κατηγορία: Αναψυκτικά");
                            productCategory=3;
                        }else if(categorySelected.equals("Μπύρες")){
                            selectCategoryButton.setText("Κατηγορία: Μπύρες");
                            productCategory=4;
                        }else if(categorySelected.equals("Κρασιά/Ούζο/Τσίπουρο...")){
                            selectCategoryButton.setText("Κατηγορία: Κρασιά/Ούζο...");
                            productCategory=5;
                        }else if(categorySelected.equals("Ποτά")){
                            selectCategoryButton.setText("Κατηγορία: Ποτά");
                            productCategory=6;
                        }else if(categorySelected.equals("Κοκτέιλ")){
                            selectCategoryButton.setText("Κατηγορία: Κοκτέιλ");
                            productCategory=7;
                        }else if(categorySelected.equals("Μη αλκοολούχα ποτά")){
                            selectCategoryButton.setText("Κατηγορία: Μη αλκοολούχα ποτά");
                            productCategory=8;
                        }else if(categorySelected.equals("Snacks")){
                            selectCategoryButton.setText("Κατηγορία: Snacks");
                            productCategory=9;
                        }else if(categorySelected.equals("Φαγητό")){
                            selectCategoryButton.setText("Κατηγορία: Φαγητό");
                            productCategory=10;
                        }else if(categorySelected.equals("Γλυκά")){
                            selectCategoryButton.setText("Κατηγορία: Γλυκά");
                            productCategory=11;
                        }
                        return true;
                    }
                });

                popup.show();   //Showing popup menu
            }
        }); //Closing the setOnClickListener method

        //Here follows the process of adding (creating) a new product row (inside the corresponding products table).
        addNewProductButton = (Button) findViewById(R.id.addNewProductButton);
        addNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    EditText nameIdGiven = (EditText) findViewById(R.id.newProductIdNameInput);
                    newProductIdNameInput = (String) String.valueOf(nameIdGiven.getText().toString().trim());

                    MultiAutoCompleteTextView descriptionGiven = (MultiAutoCompleteTextView) findViewById(R.id.newDescriptionInput);
                    newDescriptionInput = (String) String.valueOf(descriptionGiven.getText().toString().trim());

                    EditText priceGivenA = (EditText) findViewById(R.id.newProductPriceAinput);
                    EditText priceGivenB = (EditText) findViewById(R.id.newProductPriceBinput);

                    // DecimalFormat decimalFormat = new DecimalFormat("###.##");
                    // Double tempPrice = Double.parseDouble(decimalFormat.format(priceGiven.getText().toString().trim()));

                    newProductPriceA = (int) Integer.parseInt(priceGivenA.getText().toString().trim());
                    newProductPriceB = (int) Integer.parseInt(priceGivenB.getText().toString().trim());
                    newProductPrice = newProductPriceA + (double)(((double) newProductPriceB)/100);
                    //Adding the new product to online Database
                    addNewProduct();
                }catch (Exception e){
                    myToast =  Toast.makeText(view.getContext(),"Συμπληρώστε πρώτα τα κατάλληλα πεδία και προσπαθήστε ξανά",Toast.LENGTH_LONG);
                    myToastView = myToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter
                    myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be editted
                    myToastTextView = myToastView.findViewById(android.R.id.message);
                    myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                    myToast.show();
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Here follows the task that gets the significant work done.
     *
     * @throws Exception
     */
    private void addNewProduct() throws Exception{
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("afm_business", String.valueOf(business.getAfmBusiness())));
        params.add(new BasicNameValuePair("category_id", String.valueOf(productCategory)));
        params.add(new BasicNameValuePair("product_name", newProductIdNameInput));
        params.add(new BasicNameValuePair("description", newDescriptionInput));
        params.add(new BasicNameValuePair("price", String.valueOf(newProductPrice)));

        //Using the necessary params to access online database.
        params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
        params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
        params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
        params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

        JSONParser jsonParser=new JSONParser();
        JSONObject jsonObject = jsonParser.makeHttpRequest(url_add_new_product, "POST", params);

        int successFlag = jsonObject.getInt("success");
        if(successFlag==1){
            myToast = Toast.makeText(this,"Επιτυχία προσθήκης νέου προϊόντος",Toast.LENGTH_LONG);

            myToastView = myToast.getView();

            //Gets the actual oval background of the Toast then sets the colour filter
            myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

            //Gets the TextView from the Toast so it can be editted
            myToastTextView = myToastView.findViewById(android.R.id.message);
            myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

            myToast.show();

            Intent intent = new Intent(NewProductActivity.this, NewProductActivity.class);
            intent.putExtra("business", business);
            startActivity(intent);
            finish();
        }else{
            myToast = Toast.makeText(this,"Αποτυχία προσθήκης νέου προϊόντος",Toast.LENGTH_LONG);

            myToastView = myToast.getView();

            //Gets the actual oval background of the Toast then sets the colour filter
            myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

            //Gets the TextView from the Toast so it can be editted
            myToastTextView = myToastView.findViewById(android.R.id.message);
            myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

            myToast.show();
        }
    }

    /**
     * The commands inside the following method are executed when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(NewProductActivity.this, BusinessActivity.class);
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
