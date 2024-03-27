package project.snow;

import android.os.StrictMode;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * JSONParser class.
 *
 * Since, this is one of the most important classes of the project,
 * we will like to comment some information about how communications are established,
 * but also about how data are parsed to our database server and the way on how get get data responses from it.
 *
 * So, we would like to mention that this project is constructed so that to handle data stored online.
 * For that purpose and for testing purposes, we have created a free account on a free web host server.
 *
 * All data is stored inside a MySql Database,
 * and we get and send data to this Database by communicating with php files, which are also stored online.
 *
 * Here follows a short analysis on how our app communicates with the server:
 * Each time that a connection needs to be made, then we declare the "communication type",
 * meaning that we declare whether we are executing an HTTP GET Request or an HTTP POST Request.
 * After that, a connection is established.
 * All data read from HTTP GET Request are of JSON Format,
 * while when we execute an HTTP POST Request we construct the URL accordingly, so as to contain all necessary data to store into our Database.
 * Finally, as far as the internal way we use communication data is concerned, we convert, store and use them as JSONObject objects.
 * (We handle this information using JSON Objects by using the class JSONObject provided by the package: org.json.JSONObject).
 * So, it is crucial and for that particular reason we use a JSONParser class which is responsible from the communication process described above.
 *
 * For more details and information please read project report.
 *
 * @author thanoskalantzis
 */
public class JSONParser {

    //Response from the HTTP Request.
    InputStream httpResponseStream = null;
    //JSON Response String to create JSONObject.
    String jsonString = "";

    //Method to issue HTTP Request, parse JSON result and return JSONObject.
    public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params) {

        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            //Get a HTTP client.
            DefaultHttpClient httpClient = new DefaultHttpClient();
            //If required HTTP method is POST.
            if (method.equals("POST")) {
                //Create a HTTP POST object.
                HttpPost httpPost = new HttpPost(url);
                //Encode the passed parameters into the HTTP Request.
                httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                //Execute the request and fetch HTTP response.
                HttpResponse httpResponse = httpClient.execute(httpPost);
                //Extract the result from the response.
                HttpEntity httpEntity = httpResponse.getEntity();
                //Open the result as an input stream for parsing.
                httpResponseStream = httpEntity.getContent();
            }
            //Else if it is GET.
            else if (method.equals("GET")) {
                if(params!=null) {
                    //Format the parameters correctly for HTTP transmission.
                    String paramString = URLEncodedUtils.format(params, "UTF-8");
                    //Add parameters to url in GET format.
                    url += "?" + paramString;
                }
                //Execute the Request.
                HttpGet httpGet = new HttpGet(url);
                //Execute the request and fetch HTTP response
                HttpResponse httpResponse = httpClient.execute(httpGet);
                //Extract the result from the response.
                HttpEntity httpEntity = httpResponse.getEntity();
                //Open the result as an input stream for parsing.
                httpResponseStream = httpEntity.getContent();
            }
            //Catch Possible Exceptions.
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //Create buffered reader for the httpResponceStream.
            BufferedReader httpResponseReader = new BufferedReader( new InputStreamReader(httpResponseStream, "UTF-8") );
            //String to hold current line from httpResponseReader.
            String line = null;
            //Clear jsonString.
            jsonString = "";
            //While there is still more response to read.
            while ((line = httpResponseReader.readLine()) != null) {
                //Add line to jsonString.
                jsonString += (line + "\n");
            }
            //Close Response Stream.
            httpResponseStream.close();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        try {
            //Create jsonObject from the jsonString and return it.
            JSONObject jsonObject=new JSONObject(jsonString);
            return jsonObject;
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
            //Return null if _in_ error.
            return null;
        }
    }
}