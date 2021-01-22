package inspection.management.workplace.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import inspection.management.workplace.Adapters.NotificationAdapter;
import inspection.management.workplace.CheckInternetConnection.InternetConnectivity;
import inspection.management.workplace.GetApiParameter;
import inspection.management.workplace.MySingleton;
import inspection.management.workplace.R;
import inspection.management.workplace.activities.ResetPassword;
import inspection.management.workplace.utils.NotificationSiteWork;
import inspection.management.workplace.utils.UserData;

/**
 * A simple {@link Fragment} subclass.
 */
public class Notification extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.notification_recycler)
    RecyclerView notification_recycler;
    private UserData ud;
    private String u_id;
    @BindView(R.id.no_data_found_txt_view_id)
    TextView no_data_found_txt_view_id;
    private ArrayList<NotificationSiteWork> notificationSiteWorks = new ArrayList<>();
    private GetApiParameter apiParameter;
    ProgressDialog loadingDialog;
    NotificationAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public Notification() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_notification, container, false);
        ButterKnife.bind(this, root);
        ud = new UserData(getContext());
        ud = ud.readFromFile();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        notification_recycler.setLayoutManager(layoutManager);
        apiParameter = new GetApiParameter();
        loadingDialog = new ProgressDialog(getContext());
        loadingDialog.setMessage("please wait loading....");
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        if (ud!=null) {
            //loadingDialog.show();
            /**
             * Showing Swipe Refresh animation on activity create
             * As animation won't start on onCreate, post runnable is used
             */
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                    // Fetching data from server
                    getNotificationData();
                }
            });
        }

        return root;
    }

    private void getNotificationData() {
        mSwipeRefreshLayout.setRefreshing(true);
        String NOTIFY_URL = apiParameter.getApiUrl() +"notification";
        Log.e("@@NOTIFY_URL@@", NOTIFY_URL);
        final StringRequest loginRequest = new StringRequest(Request.Method.GET, NOTIFY_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("@NOTIFY_URL Response@", response.toString());
                //Toast.makeText(WorkplaceLogin.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                notificationSiteWorks.clear();
                if (response.length()>0) {
                    try {
                        JSONObject result = new JSONObject(response);
                        String STATUS = result.getString("status");
                        if (STATUS.equalsIgnoreCase("true")) {
                            JSONArray data = result.getJSONArray("data");
                            if (data.length() > 0) {
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject dataJSONObject = data.getJSONObject(i);
                                    String notification_id = dataJSONObject.getString("notification_id");
                                    String notification = dataJSONObject.getString("notification");
                                    String sent_date = dataJSONObject.getString("sent_date");
                                    notificationSiteWorks.add(new NotificationSiteWork(notification_id,notification,sent_date));
                                }
                                adapter = new NotificationAdapter(getContext(),notificationSiteWorks);
                                notification_recycler.setAdapter(adapter);
                            }
                        }
                        else if (STATUS.equalsIgnoreCase("false")){
                            //String MSG  = result.getString("message");
                            no_data_found_txt_view_id.setVisibility(View.VISIBLE);
                            no_data_found_txt_view_id.setText("No notification found");
                        }

                    }
                    catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                }
                // Stopping swipe refresh
                mSwipeRefreshLayout.setRefreshing(false);
                loadingDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (InternetConnectivity.isNetworkConnected(getContext())){
                    error.printStackTrace();
                    Log.e("@Volley Error@", "Error: " + error + "\nStatus Code " + error.networkResponse.statusCode + "\nCause " + error.getCause() + "\nnetworkResponse " + error.networkResponse.data.toString() + "\nmessage" + error.getMessage());
                    AlertDialog.Builder networkAlertDialog = new AlertDialog.Builder(getContext());
                    networkAlertDialog.setCancelable(true);
                    networkAlertDialog.setTitle("Server Error");
                    networkAlertDialog.setMessage("Server does not response try again...");
                    AlertDialog alertDialog = networkAlertDialog.create();
                    alertDialog.show();
                    loadingDialog.dismiss();
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(getContext(), "check your internet connection and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        loginRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(getContext()).addToRequestQueue(loginRequest);
    }

    @Override
    public void onRefresh() {
        getNotificationData();
    }
}
