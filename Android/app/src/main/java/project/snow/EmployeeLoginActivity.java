package project.snow;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * EmployeeLoginActivity Class.
 *
 * This class is used to control and verify the login process of each employee.
 *
 * @author thanoskalantzis
 */
public class EmployeeLoginActivity extends AppCompatActivity {
    //Class variables.
    //url_get_all_employees variable is actually the url which corresponds to the php file for getting all currently registered employees.
    private String url_get_all_employees;
    //url_get_working_employees variable is actually the url which corresponds to the php file for getting all currently working employees.
    private String url_get_working_employees;
    //Just some more class variables.
    private int afm;
    private String password;
    private Button employeeLoginButton;
    private TextView employeeRegisterButton;
    private static JSONObject allRegisteredEmployees;
    private static JSONObject currentlyWorkingEmployees;
    Toast myToast;
    View myToastView;
    TextView myToastTextView;

    /**
     * The following method is essential and necessary due to the fact that EmployeeLoginActivity class extends AppCompatActivity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_login);

        //Initialize url_get_all_employees.
        url_get_all_employees = getString(R.string.BASE_URL).concat("/connect/getallemployees.php");
        //Initialize url_get_working_employees.
        url_get_working_employees = getString(R.string.BASE_URL).concat("/connect/getworkingemployees.php");

        //Setting the title of the action bar.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("Είσοδος Εργαζομένου");
        }

        //Here follows the process of verifying the login process of an employee.
        employeeLoginButton = (Button) findViewById(R.id.employeeLoginButton);
        employeeLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EditText afmGiven = (EditText) findViewById(R.id.employeeLoginAfmInput);
                    afm = (int) Integer.parseInt(afmGiven.getText().toString().trim());

                    EditText passwordGiven = (EditText) findViewById(R.id.employeeLoginPasswordInput);
                    password = String.valueOf(passwordGiven.getText().toString().trim());

                    //params is the dynamic array of those parameters which will be used to send data to our online database.
                    List<NameValuePair> params = new ArrayList<NameValuePair>();

                    //Using the necessary params to access online database.
                    params.add(new BasicNameValuePair("DB_SERVER", getString(R.string.DATABASE_SERVER)));
                    params.add(new BasicNameValuePair("DB_NAME", getString(R.string.DATABASE_NAME)));
                    params.add(new BasicNameValuePair("DB_USER", getString(R.string.DATABASE_USERNAME)));
                    params.add(new BasicNameValuePair("DB_PASSWORD", getString(R.string.DATABASE_PASSWORD)));

                    JSONParser jsonParser1=new JSONParser();
                    allRegisteredEmployees = jsonParser1.makeHttpRequest(url_get_all_employees, "GET", params);

                    JSONParser jsonParser2=new JSONParser();
                    currentlyWorkingEmployees = jsonParser2.makeHttpRequest(url_get_working_employees, "GET", params);

                   loginValidation();

                } catch (Exception e1) {
                    myToast = Toast.makeText(view.getContext(), "Συμπληρώστε πρώτα σωστά τα κατάλληλα πεδία και προσπαθήστε ξανά", Toast.LENGTH_LONG);

                    myToastView = myToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter
                    myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be editted
                    myToastTextView = myToastView.findViewById(android.R.id.message);
                    myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                    myToast.show();

                    e1.printStackTrace();
                }

            }
        });

        //Here follows the button for a new employee registration.
        employeeRegisterButton = (TextView) findViewById(R.id.employeeRegisterButton1);
        employeeRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployeeLoginActivity.this, EmployeeRegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * The follow method is responsible for verifying or not the login of a particular employee.
     *
     * @throws Exception
     */
    private void loginValidation() throws Exception {
        /*  Just some variables declarations
         *  The following variables are later used as control variables inside if statements
         */
        boolean employeeExists = false;
        boolean employeeIsCurrentlyWorking = false;

        //Other variables declarations
        int indexOfEmployee = -1;

        //Get all registered employees
        ArrayList<Integer> allEmployeesAfmList = new ArrayList<Integer>();
        ArrayList<String> firstNameList = new ArrayList<String>();
        ArrayList<String> lastNameList = new ArrayList<String>();
        ArrayList<String> emailList = new ArrayList<String>();
        ArrayList<String> passwordList = new ArrayList<String>();
        ArrayList<Long> phoneList = new ArrayList<Long>();

        /*  If there is at least 1 individual registered as an employee to our system, the successFlag1 will be 1.
         *  successFlag1 will be 0 instead.
         */
        int successFlag1 = allRegisteredEmployees.getInt("success");

        /*  If there is at least 1 employee (that has successfully registered to our system) declared by any business as a working employee then successFlag2 will be 1.
         *  successFlag2 will be 0 instead.
         */
        int successFlag2 = currentlyWorkingEmployees.getInt("success");

        /*  If there is at least 1 individual registered as an employee to our system, the successFlag1 will be 1.
         *  successFlag1 will be 0 instead.
         */
        if (successFlag1 == 1) {
            JSONArray allEmployeesArray = allRegisteredEmployees.getJSONArray("all_employees");

            for (int i = 0; i < allEmployeesArray.length(); i++) {
                allEmployeesAfmList.add(allEmployeesArray.getJSONObject(i).getInt("afm_employee"));
                firstNameList.add(allEmployeesArray.getJSONObject(i).getString("first_name"));
                lastNameList.add(allEmployeesArray.getJSONObject(i).getString("last_name"));
                emailList.add(allEmployeesArray.getJSONObject(i).getString("email"));
                passwordList.add(allEmployeesArray.getJSONObject(i).getString("password"));
                phoneList.add(allEmployeesArray.getJSONObject(i).getLong("phone"));
            }

            for (int j = 0; j < allEmployeesAfmList.size(); j++) {
                if ((afm == allEmployeesAfmList.get(j)) && password.equals(passwordList.get(j))) {
                    indexOfEmployee = j;
                    employeeExists = true;
                    break;
                }
            }

            if(employeeExists){
                /*  If there is at least 1 employee (that has successfully registered to our system) declared by any business as a working employee then successFlag2 will be 1.
                 *  successFlag2 will be 0 instead.
                 */
                if(successFlag2 == 0){
                    myToast = Toast.makeText(this, "Έχετε εγγραφεί επιτυχώς στο σύστημα ως εργαζόμενος.\nΌμως, δεν εργάζεστε σε κάποια συνεργαζόμενη επιχείρηση αυτήν την στιγμή*", Toast.LENGTH_LONG);

                    myToastView = myToast.getView();

                    //Gets the actual oval background of the Toast then sets the colour filter
                    myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                    //Gets the TextView from the Toast so it can be editted
                    myToastTextView = myToastView.findViewById(android.R.id.message);
                    myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                    myToast.show();

                    return;
                }else{
                    JSONArray workingArray = currentlyWorkingEmployees.getJSONArray("working_employees");

                    //Get all currently working employees
                    ArrayList<Integer> workingEmployeesAfmList = new ArrayList<Integer>();
                    ArrayList<Integer> businessesAfmList = new ArrayList<Integer>();

                    for (int k = 0; k < workingArray.length(); k++) {
                        workingEmployeesAfmList.add(workingArray.getJSONObject(k).getInt("afm_employee"));
                        businessesAfmList.add(workingArray.getJSONObject(k).getInt("afm_business"));
                    }

                    ArrayList<Integer> businessesWorkingForList = new ArrayList<Integer>();

                    for(int p=0; p<workingEmployeesAfmList.size(); p++){
                        if(afm == workingEmployeesAfmList.get(p)){
                            businessesWorkingForList.add(businessesAfmList.get(p));
                            employeeIsCurrentlyWorking = true;
                        }
                    }

                    if(employeeIsCurrentlyWorking){
                        myToast = Toast.makeText(this, "Επιτυχής σύνδεση\n" + firstNameList.get(indexOfEmployee) + " " + lastNameList.get(indexOfEmployee), Toast.LENGTH_SHORT);

                        myToastView = myToast.getView();

                        //Gets the actual oval background of the Toast then sets the colour filter
                        myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                        //Gets the TextView from the Toast so it can be editted
                        myToastTextView = myToastView.findViewById(android.R.id.message);
                        myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

                        myToast.show();

                        int indexWorkingEmployee = -1;
                        for(int x=0; x<allEmployeesAfmList.size(); x++){
                            if(allEmployeesAfmList.get(x) == afm){
                                indexWorkingEmployee = x;
                                break;
                            }
                        }
                        Employee employee = new Employee();
                        employee.setAfmEmployee(allEmployeesAfmList.get(indexWorkingEmployee));
                        employee.setFirstName(firstNameList.get(indexWorkingEmployee));
                        employee.setLastName(lastNameList.get(indexWorkingEmployee));
                        employee.setEmail(emailList.get(indexWorkingEmployee));
                        employee.setPassword(passwordList.get(indexWorkingEmployee));
                        employee.setPhone(phoneList.get(indexWorkingEmployee));

                        Intent intent = new Intent(EmployeeLoginActivity.this, WorkSelectionActivity.class);
                        intent.putExtra("businessesWorkingForList", businessesWorkingForList);
                        intent.putExtra("currentEmployee", employee);
                        intent.putExtra("afm_employee", afm);
                        startActivity(intent);

                        finish();
                        //return;
                    }else {
                        myToast = Toast.makeText(this, "Έχετε εγγραφεί επιτυχώς στο σύστημα ως εργαζόμενος.\nΌμως, δεν εργάζεστε σε κάποια συνεργαζόμενη επιχείρηση αυτήν την στιγμή*", Toast.LENGTH_LONG);

                        myToastView = myToast.getView();

                        //Gets the actual oval background of the Toast then sets the colour filter
                        myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                        //Gets the TextView from the Toast so it can be editted
                        myToastTextView = myToastView.findViewById(android.R.id.message);
                        myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                        myToast.show();

                        return;
                    }
                }
            }else{
                myToast = Toast.makeText(this, "Λανθασμένα στοιχεία εισόδου.\nΠροσπαθήστε ξανά", Toast.LENGTH_LONG);

                myToastView = myToast.getView();

                //Gets the actual oval background of the Toast then sets the colour filter
                myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

                //Gets the TextView from the Toast so it can be editted
                myToastTextView = myToastView.findViewById(android.R.id.message);
                myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

                myToast.show();
            }

        }else{
            myToast = Toast.makeText(this, "Κανένας εγγεγραμμένος εργαζόμενος στο σύστημα αυτή τη στιγμή", Toast.LENGTH_LONG);

            myToastView = myToast.getView();

            //Gets the actual oval background of the Toast then sets the colour filter
            myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_IN);

            //Gets the TextView from the Toast so it can be editted
            myToastTextView = myToastView.findViewById(android.R.id.message);
            myToastTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.white));

            myToast.show();
        }

        employeeExists = false;
        employeeIsCurrentlyWorking = false;
    }

    /**
     * The commands inside the following method are executed when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(EmployeeLoginActivity.this, MainActivity.class);
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
