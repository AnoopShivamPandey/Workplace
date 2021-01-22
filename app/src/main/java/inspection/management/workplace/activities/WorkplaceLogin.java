package inspection.management.workplace.activities;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inspection.management.workplace.CheckInternetConnection.InternetConnectivity;
import inspection.management.workplace.GetApiParameter;
import inspection.management.workplace.MySingleton;
import inspection.management.workplace.R;
import inspection.management.workplace.utils.UserData;
public class WorkplaceLogin extends AppCompatActivity {
    @BindView(R.id.loginEmail)
    EditText username;
    @BindView(R.id.loginPass)
    EditText password;
    @BindView(R.id.loginBtn)
    Button loginBtn;
    @BindView(R.id.forget)
    TextView forget_password;
   /* @BindView(R.id.eye_text)
    TextView textView;*/
    private String userName;
    private String userPassword;
    SharedPreferences loginPref;
    SharedPreferences.Editor loginPrefsEditor;
    Boolean saveLogin;
    ProgressDialog loadingDialog;
    GetApiParameter apiParameter;
    String pType;
    private String token_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_workplace_login);
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        token_id = FirebaseInstanceId.getInstance().getToken();
        Log.d("abc","abc"+token_id);
     //   Toast.makeText(getApplicationContext(),"token"+token_id,Toast.LENGTH_LONG).show();

//        if (token_id!=null){
//            Intent intent=new Intent(getApplicationContext(),WorkplaceLogin.class);
//            startActivity(intent);
//        }
//

        ButterKnife.bind(this);

        apiParameter = new GetApiParameter();
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("please wait loading....");
        loadingDialog.setCancelable(false);
        loginPref = getSharedPreferences("loginPref", Context.MODE_PRIVATE);
        loginPrefsEditor = loginPref.edit();
        saveLogin = loginPref.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            startActivity(new Intent(WorkplaceLogin.this, HomeActivity.class));
            finish();
        }
        /*textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pType == "passwordtext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)") {
                    pType = "passwordtext.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)";
                    password.setTransformationMethod(null);
                    if (password.getText().length() > 0)
                        password.setSelection(password.getText().length());
                    textView.setBackgroundResource(R.drawable.eye1);
                } else {
                    pType = "passwordtext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)";
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    if (password.getText().length() > 0)
                        password.setSelection(password.getText().length());
                    textView.setBackgroundResource(R.drawable.eye);
                }
            }
        });*/

    }


    @OnClick({R.id.loginBtn,R.id.forget})
    public void submit(View view) {
            // TODO call server...
            switch (view.getId()) {
                case R.id.loginBtn:
                    //startActivity(new Intent(WorkplaceLogin.this,HomeActivity.class));
                    attemptLogin();
                    break;

                case R.id.forget:
                    startActivity(new Intent(WorkplaceLogin.this, ResetPassword.class));
                    break;
            }
    }

    private void attemptLogin() {
        if (username.length() == 0) {
            username.setError("Enter Name");
            username.setFocusable(true);

        } else if (password.length() == 0) {
            password.setError("Enter password");
            password.setFocusable(true);
        } else {
            userName = username.getText().toString();
            userPassword = password.getText().toString();
            loadingDialog.show();
                loginPerform(userName, userPassword);
        }
    }

    private void loginPerform(String userName, String userPassword) {
        String LOGIN_URL = apiParameter.getApiUrl() +"login";
        //String LOGIN_URL = "https://psi.legalbreakup.com/api/notification/1";
        //String LOGIN_URL = "https://psi.legalbreakup.com/Api/login";
        Log.e("@@LOGIN URL@@", LOGIN_URL);
        Log.e("@@LOGIN DETAIL@@", "userEmail = "+userName+" & "+"userPassword = "+userPassword);
        final StringRequest loginRequest = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("@@Login Response@@", response.toString());
                //Toast.makeText(WorkplaceLogin.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                if (response.length()>0) {
                    try {
                        JSONObject result = new JSONObject(response);
                        String STATUS = result.getString("status");
                        String MSG = result.getString("message");
                        if (STATUS.equalsIgnoreCase("true")){
                            JSONArray data = result.getJSONArray("data");

                            if (data.length()>0){
                                for (int i = 0; i < data.length(); i++) {

                                    UserData ud = new UserData(WorkplaceLogin.this);
                                    JSONObject subCategory = data.getJSONObject(i);
                                    String master_user_id = subCategory.getString("master_user_id");
                                    String m_first_name = subCategory.getString("m_first_name");
                                    String m_last_name = subCategory.getString("m_last_name");
                                    String m_user_name = subCategory.getString("m_user_name");
                                    String m_password = subCategory.getString("m_password");
                                    String m_pic = subCategory.getString("m_pic");
                                    String is_delete = subCategory.getString("is_delete");
                                    /*
                                    *****************************
                                       Save Login Data into File
                                    * ***************************
                                    */
                                    ud.master_user_id = master_user_id+"";
                                    ud.fname = m_first_name+"";
                                    ud.lname = m_last_name+"";
                                    ud.is_del = is_delete+"";
                                    ud.password = m_password+"";
                                    ud.imageUri = m_pic+"";
                                    ud.uname = m_user_name+"";
                                    ud.saveToFile(ud);
                                    /*
                                    ***********************************************
                                       Save Login Detail into SharedPreferences
                                    ***********************************************
                                    */
                                    loginPrefsEditor.putBoolean("saveLogin", true);
                                    loginPrefsEditor.commit();
                                        Toast.makeText(WorkplaceLogin.this, ""+MSG, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(WorkplaceLogin.this, HomeActivity.class));
                                        finish();


                                }
                            }
                        }
                        else if (STATUS.equalsIgnoreCase("false")){
                            AlertDialog.Builder networkAlertDialog = new AlertDialog.Builder(WorkplaceLogin.this);
                            networkAlertDialog.setCancelable(true);
                            networkAlertDialog.setTitle("OOPS!");
                            networkAlertDialog.setMessage(MSG);
                            AlertDialog alertDialog = networkAlertDialog.create();
                            alertDialog.show();

                        }
                        loadingDialog.dismiss();
                    }
                    catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (InternetConnectivity.isNetworkConnected(WorkplaceLogin.this)) {
                    error.printStackTrace();
                  //  Log.e("@Volley Error@", "Error: " + error + "\nStatus Code " + error.networkResponse.statusCode + "\nCause " + error.getCause() + "\nnetworkResponse " + error.networkResponse.data.toString() + "\nmessage" + error.getMessage());
                    AlertDialog.Builder networkAlertDialog = new AlertDialog.Builder(WorkplaceLogin.this);
                    networkAlertDialog.setCancelable(true);
                    networkAlertDialog.setTitle("Server Error");
                    networkAlertDialog.setMessage("Server does not response try again...");
                    AlertDialog alertDialog = networkAlertDialog.create();
                    alertDialog.show();
                    loadingDialog.dismiss();
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(WorkplaceLogin.this, "check your internet connection and try again", Toast.LENGTH_SHORT).show();
                }
            }
        })

        {//adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("m_user_name", userName);
                params.put("m_password", userPassword);
                params.put("user_token", token_id);
                return params;
            }
           /* @Override public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> customHeader = new HashMap<String, String>();
                customHeader.put("Content-Type","application/json");
                return customHeader;
            }*/
        };
        loginRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(WorkplaceLogin.this).addToRequestQueue(loginRequest);
    }
}
