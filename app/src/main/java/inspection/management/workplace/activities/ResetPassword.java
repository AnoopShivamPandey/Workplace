package inspection.management.workplace.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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

public class ResetPassword extends AppCompatActivity {
    @BindView(R.id.new_password)
    EditText new_password;
    @BindView(R.id.repeat_new_password)
    EditText repeat_new_password;
    @BindView(R.id.resetBtn)
    Button resetBtn;
    SharedPreferences loginPref;
    SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private ProgressDialog loadingDialog;
    private GetApiParameter apiParameter;
    private UserData ud;
    private String u_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        apiParameter = new GetApiParameter();
        ud = new UserData(ResetPassword.this);
        ud = ud.readFromFile();
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("please wait loading....");
        loadingDialog.setCancelable(false);
    }
    @OnClick({R.id.resetBtn})
    public void resetCall(View view) {
        // TODO call server...
        switch (view.getId()) {
            case R.id.resetBtn:
                if (ud!=null){
                    resetPassword();
                }
                else {
                    Toast.makeText(this, "first login your account", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private void resetPassword() {


            if (new_password.length() == 0) {
                new_password.setError("Field required");
                new_password.setFocusable(true);

            } else if (repeat_new_password.length() == 0) {
                repeat_new_password.setError("Field required");
                repeat_new_password.setFocusable(true);

            } else {
               if (new_password.getText().toString().equals(repeat_new_password.getText().toString())){
                   //Toast.makeText(this, "matched", Toast.LENGTH_SHORT).show();
                   loadingDialog.show();
                   resetAPICall(new_password.getText().toString());
               }
               else {
                   Toast.makeText(this, "Both password not matched", Toast.LENGTH_SHORT).show();
               }
            }
        }

    private void resetAPICall(String userNewPass) {
            String RESET_PASS_URL = apiParameter.getApiUrl() +"reset_password";
            Log.e("@@RESET_PASS_URL@@", RESET_PASS_URL);
            Log.e("@@RESET_P_URL Detail@@", "userID = "+ud.master_user_id+" & "+"userNewPassword = "+userNewPass);
            final StringRequest loginRequest = new StringRequest(Request.Method.POST, RESET_PASS_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.e("@RESET_PASS Response@", response.toString());
                    //Toast.makeText(WorkplaceLogin.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                        if (response.length()>0) {
                            try {
                                JSONObject result = new JSONObject(response);
                                String STATUS = result.getString("status");
                                String MSG = result.getString("message");
                                if (STATUS.equalsIgnoreCase("true")) {
                                    Toast.makeText(ResetPassword.this, "" + MSG, Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ResetPassword.this, WorkplaceLogin.class));
                                    finish();
                                }
                            }
                            catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            loadingDialog.dismiss();
                        }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (InternetConnectivity.isNetworkConnected(ResetPassword.this)) {
                        error.printStackTrace();
                        Log.e("@Volley Error@", "Error: " + error + "\nStatus Code " + error.networkResponse.statusCode + "\nCause " + error.getCause() + "\nnetworkResponse " + error.networkResponse.data.toString() + "\nmessage" + error.getMessage());
                        AlertDialog.Builder networkAlertDialog = new AlertDialog.Builder(ResetPassword.this);
                        networkAlertDialog.setCancelable(true);
                        networkAlertDialog.setTitle("Server Error");
                        networkAlertDialog.setMessage("Server does not response try again...");
                        AlertDialog alertDialog = networkAlertDialog.create();
                        alertDialog.show();
                        loadingDialog.dismiss();
                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(ResetPassword.this, "check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                }
            })

            {//adding parameters to the request
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    Log.e("@u_id@", "getParams"+ud.master_user_id);
                    params.put("id", ud.master_user_id);
                    params.put("m_password", userNewPass);
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
            MySingleton.getInstance(ResetPassword.this).addToRequestQueue(loginRequest);

    }

}
