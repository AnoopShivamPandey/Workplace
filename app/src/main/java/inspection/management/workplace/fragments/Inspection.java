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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import inspection.management.workplace.Adapters.ActionAdapter;
import inspection.management.workplace.Adapters.InpectionAdapter;
import inspection.management.workplace.CheckInternetConnection.InternetConnectivity;
import inspection.management.workplace.GetApiParameter;
import inspection.management.workplace.MySingleton;
import inspection.management.workplace.R;
import inspection.management.workplace.utils.ActionSiteWork;
import inspection.management.workplace.utils.InspectionSiteWork;
import inspection.management.workplace.utils.UserData;

/**
 * A simple {@link Fragment} subclass.
 */
public class Inspection extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.inspection_recycler)
    RecyclerView inspection_recycler;
    @BindView(R.id.no_data_found_txt_view_id)
    TextView no_data_found_txt_view_id;
    private UserData ud;
    private String u_id;
    private ArrayList<InspectionSiteWork> inspectionSiteWorkArrayList = new ArrayList<>();
    private GetApiParameter apiParameter;
    ProgressDialog loadingDialog;
    InpectionAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public Inspection() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_inspection, container, false);
        ButterKnife.bind(this, root);
        ud = new UserData(getContext());
        ud = ud.readFromFile();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        inspection_recycler.setLayoutManager(layoutManager);
        apiParameter = new GetApiParameter();
        loadingDialog = new ProgressDialog(getContext());
        loadingDialog.setMessage("please wait loading....");
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        if (ud != null) {
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
                    getInspection();
                }
            });
        }


        return root;
    }

    private void getInspection() {
        mSwipeRefreshLayout.setRefreshing(true);
        String INSPECTION_URL = apiParameter.getApiUrl() + "employee_inspections/"+ud.master_user_id;
        Log.e("@@INSPECTION_URL@@", INSPECTION_URL);
        final StringRequest loginRequest = new StringRequest(Request.Method.GET, INSPECTION_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("@INSPECTION Response@", response.toString());
                //Toast.makeText(WorkplaceLogin.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                inspectionSiteWorkArrayList.clear();
                loadingDialog.dismiss();
                if (response.length() > 0) {
                    try {
                        JSONObject result = new JSONObject(response);
                        String STATUS = result.getString("status");
                        if (STATUS.equalsIgnoreCase("true")) {
                            JSONArray dataJSONObject = result.getJSONArray("data");
                            if (dataJSONObject.length() > 0) {
                                for (int i = 0; i < dataJSONObject.length(); i++) {

                                    JSONObject data = dataJSONObject.getJSONObject(i);
                                    String inspection_id = data.getString("inspection_id");
                                    String employee_name = data.getString("employee_name");
                                    String start_date = data.getString("start_date");
                                    String end_date = data.getString("end_date");
                                    String topic_name = data.getString("topic_name");
                                    String topic_id = data.getString("topic_id");
                                    String status = data.getString("status");
                                    String location = data.getString("location");
                                    String is_delete = data.getString("is_delete");
                                    String project = data.getString("Project");
                                    String name = data.getString("name");
                                    String question_color = data.getString("question_color");

                                    inspectionSiteWorkArrayList.add(new InspectionSiteWork(status, inspection_id, project, employee_name, start_date,
                                            end_date, topic_name, location, is_delete, topic_id,name,question_color));
                                }
                                adapter = new InpectionAdapter(getContext(), inspectionSiteWorkArrayList);
                                inspection_recycler.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        } else if (STATUS.equalsIgnoreCase("false")) {
                            String MSG = result.getString("message");
                            no_data_found_txt_view_id.setVisibility(View.VISIBLE);
                            no_data_found_txt_view_id.setText("" + MSG);
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    // Stopping swipe refresh
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (InternetConnectivity.isNetworkConnected(getContext())) {
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
        getInspection();
    }
}
