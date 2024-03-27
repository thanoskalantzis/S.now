package project.snow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * OrderActivity class.
 *
 * This class is the one responsible for placing a new order.
 *
 * @author thanoskalantzis
 */
public class OrderActivity extends AppCompatActivity {
    //Class variables.
    //url_add_order_item variable is actually the url which corresponds to the php file responsible for placing the order into the database.
    private String url_add_order_item;
    private static Business businessSelected;
    private static Customer currentCustomer;
    private ArrayList<OrderItem> entireList;
    private RecyclerView screenListView;
    private static double finalSumAmount;
    private Button submitOrderButton;
    private OrderListAdapter orderListAdapter;
    private int lastPosition;

    /**
     * The following method is essential and necessary due to the fact that OrderActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        //Initialize url_add_order_item.
        url_add_order_item = getString(R.string.BASE_URL).concat("/connect/addorder.php");

        //Getting parsed data from previously active activity.
        Intent intent = getIntent();
        currentCustomer = (Customer) getIntent().getSerializableExtra("customer");
        businessSelected = (Business) getIntent().getSerializableExtra("businessSelected");
        entireList = (ArrayList<OrderItem>) getIntent().getSerializableExtra("entireList");

        final ArrayList<OrderItem> ordersList = new ArrayList<OrderItem>();
        for(int i=0; i<entireList.size(); i++){
            if(entireList.get(i).getQuantity()>0){
                ordersList.add(new OrderItem(entireList.get(i).getProduct(), entireList.get(i).getQuantity()));
            }
        }

        finalSumAmount=0;
        int tempQuantity;
        double tempPrice;
        for(int i=0; i<ordersList.size(); i++){
            tempQuantity=ordersList.get(i).getQuantity();
            tempPrice=ordersList.get(i).getProduct().getPrice();
            finalSumAmount=finalSumAmount+(tempQuantity*tempPrice);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Σύνολο Παραγγελίας: "+finalSumAmount+" €");
        }

        //Show
        screenListView = (RecyclerView) findViewById(R.id.orderRecyclerView);
        screenListView.setHasFixedSize(true);

        //Here follows the process of submitting a new order.
        submitOrderButton = (Button) findViewById(R.id.submitOrderButton);
        submitOrderButton.setBackgroundResource(R.drawable.sendorderbutton);
        submitOrderButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try{
                    final Business finalBusinessSelected = businessSelected;
                    final ArrayList<OrderItem> finalOrdersList = ordersList;

                    if(finalOrdersList.size()>0){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                        alertDialogBuilder.setTitle("Εισάγετε Αριθμό Επιβεβαίωσης Τοποθεσίας");
                        alertDialogBuilder.setMessage("Σαρώστε το QRCode μπροστά σας και αντιγράψτε τον αριθμό που εμφανίζεται\n(Πλήθος χαρακτήρων: 12)\n");

                        //Set up the input
                        final EditText inputTextDialog = new EditText(view.getContext());
                        InputFilter[] FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(12);
                        inputTextDialog.setFilters(FilterArray);

                        inputTextDialog.setHint("Αριθμός που εμφανίζεται");

                        //Specify the type of input expected
                        inputTextDialog.setInputType(InputType.TYPE_CLASS_NUMBER);
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

                                String inputKeyString;
                                int afmBusinessInKey;
                                int numberTablesInKey;
                                try{
                                    inputKeyString = inputTextDialog.getText().toString();
                                    afmBusinessInKey = Integer.parseInt(inputKeyString.substring(0, 9));
                                    numberTablesInKey = Integer.parseInt(inputKeyString.substring(10));

                                    if( inputKeyString.length()==12 && afmBusinessInKey==finalBusinessSelected.getAfmBusiness() && numberTablesInKey <= businessSelected.getNumberTables() ){
                                        //A java calendar instance
                                        Calendar calendar = Calendar.getInstance();
                                        //Get a java.util.Date from the calendar instance.
                                        java.util.Date now = calendar.getTime();
                                        //A Java current time (now) instance
                                        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
                                        //Calendar cal = Calendar.getInstance();
                                        String myDateTime = currentTimestamp.toString();
                                        myDateTime = (String) myDateTime.substring(0, myDateTime.indexOf('.'));

                                        final String myDateTimeString = new String(myDateTime);

                                        JSONParser jsonParser;
                                        JSONObject jsonObject;
                                        for(int i=0; i<finalOrdersList.size(); i++){
                                            List<NameValuePair> params = new ArrayList<NameValuePair>();

                                            params.add(new BasicNameValuePair("afm_business", String.valueOf(finalBusinessSelected.getAfmBusiness())));
                                            params.add(new BasicNameValuePair("product_id", String.valueOf(finalOrdersList.get(i).getProduct().getProductId())));
                                            params.add(new BasicNameValuePair("category_id", String.valueOf(finalOrdersList.get(i).getProduct().getCategoryId())));
                                            params.add(new BasicNameValuePair("product_name", String.valueOf(finalOrdersList.get(i).getProduct().getProductName())));
                                            params.add(new BasicNameValuePair("description", String.valueOf(finalOrdersList.get(i).getProduct().getDescription())));
                                            params.add(new BasicNameValuePair("price", String.valueOf(finalOrdersList.get(i).getProduct().getPrice())));
                                            params.add(new BasicNameValuePair("quantity", String.valueOf(finalOrdersList.get(i).getQuantity())));
                                            params.add(new BasicNameValuePair("number_table", String.valueOf(numberTablesInKey)));
                                            params.add(new BasicNameValuePair("date_time", (String) myDateTimeString));
                                            params.add(new BasicNameValuePair("isActive", "1"));

                                            //Using the necessary params to access online database.
                                            params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                                            params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                                            params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                                            params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                                            jsonParser = new JSONParser();
                                            jsonObject = jsonParser.makeHttpRequest(url_add_order_item, "POST", params);
                                        }

                                        myToast = Toast.makeText(myContext, "Η παραγγελία στάλθηκε επιτυχώς!\n\nΣας ευχαριστούμε για την εμπιστοσύνη σας!", toastDuration);
                                        myView = myToast.getView();

                                        //Gets the actual oval background of the Toast then sets the colour filter
                                        myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                                        //Gets the TextView from the Toast so it can be editted
                                        myToastTextView = myView.findViewById(android.R.id.message);
                                        myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.black));

                                        myToast.show();

                                        Intent i = new Intent(OrderActivity.this, BusinessSelectionActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.putExtra("customer", currentCustomer);
                                        startActivity(i);

                                        finish();
                                    }else {
                                        CharSequence error = "Μη αποδεκτός κωδικός.\nΣαρώστε ξανά το QRCode μπροστά σας και προσπαθήστε ξανά";

                                        myToast = Toast.makeText(myContext, error, toastDuration);
                                        myView = myToast.getView();

                                        //Gets the actual oval background of the Toast then sets the colour filter
                                        myView.getBackground().setColorFilter(myView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                        //Gets the TextView from the Toast so it can be editted
                                        myToastTextView = myView.findViewById(android.R.id.message);
                                        myToastTextView.setTextColor(myView.getContext().getResources().getColor(R.color.white));

                                        myToast.show();
                                    }
                                }catch (Exception innerException) {
                                    Toast myExceptionToast = Toast.makeText(myContext, "Εισάγετε πρώτα τον Αριθμό Επιβεβαίωσης Τοποθεσίας και προσπαθήστε ξανά", Toast.LENGTH_LONG);
                                    View myExceptionView = myExceptionToast.getView();

                                    //Gets the actual oval background of the Toast then sets the colour filter
                                    myExceptionView.getBackground().setColorFilter(myExceptionView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                                    //Gets the TextView from the Toast so it can be editted
                                    TextView myToastExceptionView = myExceptionView.findViewById(android.R.id.message);
                                    myToastExceptionView.setTextColor(myExceptionView.getContext().getResources().getColor(R.color.white));

                                    myExceptionToast.show();

                                    innerException.printStackTrace();
                                }
                            }
                        });
                        alertDialogBuilder.setNegativeButton("Ακύρωση", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertDialogBuilder.show();
                    }else{
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                        alertDialogBuilder.setTitle("Κενή παραγγελία");
                        alertDialogBuilder.setMessage("Προσθέστε πρώτα προϊόντα στην παραγγελία σας και προσπαθήστε ξανά\n");

                        //Set up the buttons
                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertDialogBuilder.show();
                    }
                }catch (Exception e){

                    Toast myExceptionToast = Toast.makeText(view.getContext(), "Εισάγετε πρώτα τον Αριθμό Επιβεβαίωσης Τοποθεσίας και προσπαθήστε ξανά", Toast.LENGTH_LONG);
                    View myExceptionView = myExceptionToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter
                    myExceptionView.getBackground().setColorFilter(myExceptionView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be editted
                    TextView myToastExceptionView = myExceptionView.findViewById(android.R.id.message);
                    myToastExceptionView.setTextColor(myExceptionView.getContext().getResources().getColor(R.color.white));

                    myExceptionToast.show();

                    e.printStackTrace();
                }
            }
        });

        orderListAdapter = new OrderListAdapter(businessSelected, ordersList);
        orderListAdapter.setHasStableIds(true);
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
        screenListView.setAdapter(orderListAdapter);
    }

    //@Override
    //public void onBackPressed() {
    //    super.onBackPressed();
    //}


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
