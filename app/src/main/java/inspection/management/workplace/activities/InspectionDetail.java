package inspection.management.workplace.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inspection.management.workplace.BuildConfig;
import inspection.management.workplace.CheckInternetConnection.InternetConnectivity;
import inspection.management.workplace.FileCompressor;
import inspection.management.workplace.GetApiParameter;
import inspection.management.workplace.MySingleton;
import inspection.management.workplace.R;

public class InspectionDetail extends AppCompatActivity {
    ProgressDialog loadingDialog;
    GetApiParameter apiParameter;
    private String status;
    private String Project;
    private String ins_id, topic_id, is_delete;
    private String topic, s_date, e_date, location, employe_name;
    Bundle data;
    @BindView(R.id.d_inspection)
    TextView d_inspection;
    @BindView(R.id.d_inspection_loc)
    TextView d_inspection_loc;
    @BindView(R.id.d_inspection_sd)
    TextView d_inspection_sd;
    @BindView(R.id.d_inspection_ed)
    TextView d_inspection_ed;
    @BindView(R.id.btn1)
    Button btn1;
    @BindView(R.id.btn2)
    Button btn2;
    @BindView(R.id.btn3)
    Button btn3;
    @BindView(R.id.btn4)
    Button btn4;
    @BindView(R.id.btn5)
    Button btn5;
    @BindView(R.id.imagetwo)
    ImageView imagetwo;
    @BindView(R.id.imageone)
    ImageView imageone;
    @BindView(R.id.Q_1)
    TextView Q_1;
    @BindView(R.id.Q_2)
    TextView Q_2;
    @BindView(R.id.Q_3)
    TextView Q_3;
    @BindView(R.id.Q_4)
    TextView Q_4;
    @BindView(R.id.Q_5)
    TextView Q_5;
    @BindView(R.id.camera)
    ImageView camera;
    @BindView(R.id.imagesLinear)
    LinearLayout imagesLinear;
    private String pic_Path_one,pic_Path_two;
    private static final int BEFORE_CAMERA_REQUEST = 11;
    private static final int AFTER_CAMERA_REQUEST = 22;
    private Uri FilePathUri;
    private Bitmap bitmap;
    String question1="",question2="",question3="",question4="",question5="";
    private File mPhotoFileOne,mPhotoFile,mPhotoFileTwo;
    private FileCompressor mCompressor;
    String selected_Color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_detail);
        ButterKnife.bind(this);
        mCompressor = new FileCompressor(this);
        getSupportActionBar().setTitle("Inspection Detail");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        apiParameter = new GetApiParameter();
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("please wait loading....");
        loadingDialog.setCancelable(false);
        data = getIntent().getExtras();
        if (data != null) {
            status = data.getString("status");
            Project = data.getString("Project");
            ins_id = data.getString("ins_id");
            e_date = data.getString("end_date");
            s_date = data.getString("start_date");
            location = data.getString("location");
            topic = data.getString("topic");
            employe_name = data.getString("employee_name");
            topic_id = data.getString("topic_id");
            is_delete = data.getString("is_delete");
            Log.e("Data", "Selected Action Data =" + status + " " + Project + " " + ins_id + " " + e_date + " " + s_date + " " + location + " " + employe_name + " " + topic + " topic_id = " + topic_id);
            d_inspection.setText(": " + topic);
            d_inspection_sd.setText(": " + s_date);
            d_inspection_ed.setText(": " + e_date);
            d_inspection_loc.setText(": " + location);
            if (status != null && status.equals("0")) {
                btn1.setEnabled(false);
                btn2.setEnabled(false);
                btn3.setEnabled(false);
                btn4.setEnabled(false);
                btn5.setEnabled(false);
            }
            else {
                checkPermissions(11);
            }
            getTopicQuestions(topic_id);
        }
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status != null && status.equals("0")) {
                    Toast.makeText(InspectionDetail.this, "Already Updated Inspection", Toast.LENGTH_SHORT).show();
                }
                else {
                    checkPermissions(22);
               }
            }
        });
    }
    @OnClick({R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,R.id.btn5})
    public void questionClick(View view){
        switch (view.getId()){
            case R.id.btn1:
//                btn1.setBackgroundColor(Integer.parseInt("#ffff4444"));
//                btn1.setTextColor(Color.WHITE);
                loadingDialog.show();
                String s_q_1;
                if (question1.equals("")){
                    s_q_1 = "no question against this topic";
                    selected_Color = "#FF4444";
                }
                else {
                    s_q_1 = question1;
                    selected_Color = "#FF4444";
                }
                uploadResponse(s_q_1);
                break;
            case R.id.btn2:
                loadingDialog.show();
                String s_q_2;
                if (question1.equals("")){
                    s_q_2 = "no question against this topic";
                    selected_Color = "#CC0000";
                }
                else {
                    s_q_2 = question2;
                    selected_Color = "#CC0000";
                }
                uploadResponse(s_q_2);
                break;
            case R.id.btn3:
                loadingDialog.show();
                String s_q_3;
                if (question1.equals("")){
                    s_q_3 = "no question against this topic";
                    selected_Color = "#99CC00";
                }
                else {
                    s_q_3 = question3;
                    selected_Color = "#99CC00";
                }
                uploadResponse(s_q_3);
                break;
            case R.id.btn4:
                loadingDialog.show();
                String s_q_4;
                if (question1.equals("")){
                    s_q_4 = "no question against this topic";
                    selected_Color = "#669900";
                }
                else {
                    s_q_4 = question4;
                    selected_Color = "#669900";

                }
                uploadResponse(s_q_4);
                break;
            case R.id.btn5:
                loadingDialog.show();
                String s_q_5;
                if (question1.equals("")){
                    s_q_5 = "no question against this topic";
                    selected_Color = "#FFBB33";
                }
                else {
                    s_q_5 = question5;
                    selected_Color = "#FFBB33";
                }
                uploadResponse(s_q_5);
                break;
        }

    }
    private void getTopicQuestions(String topic_id) {
        String Question_Url = apiParameter.getApiUrl() + "topic/" + topic_id;
        Log.e("@@Question_Url@@", Question_Url);
        final StringRequest loginRequest = new StringRequest(Request.Method.GET, Question_Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("@Question Response@", response.toString());
                //Toast.makeText(WorkplaceLogin.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                //inspectionSiteWorkArrayList.clear();
                loadingDialog.dismiss();
                if (response.length() > 0) {
                    try {
                        JSONObject result = new JSONObject(response);
                        String STATUS = result.getString("status");
                        if (STATUS.equalsIgnoreCase("true")) {
                            JSONObject data = result.getJSONObject("data");
                            String topic_id = data.getString("topic_id");
                            String topic_name = data.getString("topic_name");
                             question1 = data.getString("question1");
                             question2 = data.getString("question2");
                             question3 = data.getString("question3");
                             question4 = data.getString("question4");
                             question5 = data.getString("question5");
                            String is_delete = data.getString("is_delete");
                            Q_1.setText("" + question1);
                            Q_2.setText("" + question2);
                            Q_3.setText("" + question3);
                            Q_4.setText("" + question4);
                            Q_5.setText("" + question5);

                        } else if (STATUS.equalsIgnoreCase("false")) {
                            String MSG = result.getString("message");
                            question1 = "";
                            question2 = "";
                            question3 = "";
                            question4 = "";
                            question5 = "";
                            //no_data_found_txt_view_id.setVisibility(View.VISIBLE);
                            //no_data_found_txt_view_id.setText("" + MSG);
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    // Stopping swipe refresh
                    // mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (InternetConnectivity.isNetworkConnected(InspectionDetail.this)) {
                    loadingDialog.dismiss();
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(InspectionDetail.this, "check your internet connection and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        loginRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(InspectionDetail.this).addToRequestQueue(loginRequest);
    }

    private void uploadResponse(String selectedQuestion) {
        String INSPECT_UPD_URL = apiParameter.getApiUrl() + "update_inspection";
        Log.e("@@INSPECT_UPD_URL@@", INSPECT_UPD_URL);
        Log.e("@@selected question@@", selectedQuestion);
        Log.e("@@selected color@@", selected_Color);
        if (pic_Path_one !=null && pic_Path_two!=null) {
            //String INSPECT_UPD_URL = apiParameter.getApiUrl() + "update_inspection";
            //Log.e("@@INSPECT_UPD_URL@@", INSPECT_UPD_URL);
            //File imageFile = new File(pic_Path_one);
            //File imageFile2 = new File(pic_Path_two);
            AndroidNetworking.upload(INSPECT_UPD_URL)
                    .addMultipartFile("image_1", mPhotoFileOne)
                    .addMultipartFile("image_2", mPhotoFileTwo)
                    .addMultipartParameter("id", ins_id)
                    .addMultipartParameter("Project", Project)
                    .addMultipartParameter("status", "ok")
                    .addMultipartParameter("end_date", e_date)
                    .addMultipartParameter("start_date", s_date)
                    .addMultipartParameter("employee_name", employe_name)
                    .addMultipartParameter("location", location)
                    .addMultipartParameter("topic_id", topic_id)
                    .addMultipartParameter("question", selectedQuestion)
                    .addMultipartParameter("question_color", selected_Color)
                    .setPriority(Priority.HIGH)
                    .build()
                    .setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                            float progress = (float) (bytesUploaded / totalBytes * 100);
                        }
                    })
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("@@Server Response@@", response);
                            loadingDialog.dismiss();
                            //NotifyUserRegister();
                            if (response.length() > 0) {
                                try {
                                    JSONObject result = new JSONObject(response);
                                    String STATUS = result.getString("status");
                                    if (STATUS.equalsIgnoreCase("true")) {
                                        String message = result.getString("message");
                                        Toast.makeText(InspectionDetail.this, ""+message, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(InspectionDetail.this, HomeActivity.class));
                                    }
                                    else {
                                        Toast.makeText(InspectionDetail.this, "The inspection not updated something wrong", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            if (InternetConnectivity.isNetworkConnected(InspectionDetail.this)) {
                                //progressDialog.dismiss();

                                loadingDialog.dismiss();
                                anError.printStackTrace();
                                Toast.makeText(InspectionDetail.this, getResources().getString(R.string.str_error_u), Toast.LENGTH_SHORT).show();
                            } else {
                                anError.printStackTrace();
                                loadingDialog.dismiss();
                                Toast.makeText(InspectionDetail.this, getResources().getString(R.string.str_connection), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
        else {
            loadingDialog.dismiss();
            Toast.makeText(this, "please add two pictures for updating inspection", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkPermissions(int rc) {
        Dexter.withActivity(InspectionDetail.this)
                .withPermissions(
                        Manifest.permission.CAMERA
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            dispatchTakePictureIntent(rc);
                            //Toast.makeText(SignUp.this, "All permission granted", Toast.LENGTH_SHORT).show();
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            new AlertDialog.Builder(InspectionDetail.this)
                                    .setTitle("Permission Required")
                                    .setMessage("These permission is mandatory to select pictures in this app")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivityForResult(intent, 51);
                                        }
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

 /*   private void showPictureDialog() {
        //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(cameraIntent, BEFORE_CAMERA_REQUEST);
        final CharSequence[] items = {getString(R.string.str_choose_from_existing),
                getString(R.string.str_cancel)};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.str_select_action));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.str_choose_from_existing))) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 2);
                } else if (items[item].equals(getString(R.string.str_cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            if (resultCode== Activity.RESULT_OK) {
                try {
                    imagesLinear.setVisibility(View.VISIBLE);
                    mPhotoFileOne = mCompressor.compressToFile(mPhotoFile);
                    pic_Path_one = mPhotoFileOne.getAbsolutePath();
                    Log.e("@URI PATH 1st@", "onActivityResult:" + pic_Path_one);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Glide.with(InspectionDetail.this)
                        .load(mPhotoFileOne)
                        .placeholder(R.drawable.workplacelogo)
                        .into(imageone);
            }
            else if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "User Cancel Take Camera Image", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == 22){
            if (resultCode== Activity.RESULT_OK) {
                try {
                    mPhotoFileTwo = mCompressor.compressToFile(mPhotoFile);
                    pic_Path_two = mPhotoFileTwo.getAbsolutePath();
                    Log.e("@URI PATH 2nd@", "onActivityResult:" + pic_Path_two);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Glide.with(InspectionDetail.this)
                        .load(mPhotoFileTwo)
                        .placeholder(R.drawable.workplacelogo)
                        .into(imagetwo);
            }
            else if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "User Cancel Take Camera Image", Toast.LENGTH_SHORT).show();
            }
        }
        /*if (resultCode == Activity.RESULT_OK) {
           if (requestCode == BEFORE_CAMERA_REQUEST)
                onCaptureImageResult(data);
        }*/
        /*if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            pic_Path_one = getPath(InspectionDetail.this, uri);
            if (pic_Path_one != null) {
                Log.e("@Picture Path 1@", pic_Path_one);
                //txt_path.setText(picturePath);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    //userProfileImage.setImageBitmap(bitmap);
                    Log.e("bitmap", "onActivityResult: " + bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("eror", "onActivityResult: " + e);
                }
                Glide.with(InspectionDetail.this).load(uri).placeholder(R.drawable.workplacelogo).into(imagetwo);

            }
        }
        else if (requestCode == 3){
            Uri uri = data.getData();
            pic_Path_two = getPath(InspectionDetail.this, uri);
            if (pic_Path_two != null) {
                Log.e("@Picture Path 2@", pic_Path_one);
                //txt_path.setText(picturePath);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    //userProfileImage.setImageBitmap(bitmap);
                    Log.e("bitmap", "onActivityResult: " + bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("eror", "onActivityResult: " + e);
                }
                Glide.with(InspectionDetail.this).load(uri).placeholder(R.drawable.workplacelogo).into(imageone);

            }
        }*/
    }

  /*  public String getPath(Context context, Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }*/
    /*private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("@PATH@", "onCaptureImageResult: "+ destination.getPath() );
        //imagetwo.setImageBitmap(thumbnail);
        Glide.with(InspectionDetail.this).load(destination.getPath()).placeholder(R.drawable.workplacelogo).into(imagetwo);
        //cvCouponPic.setImageBitmap(thumbnail);
    }*/

    /*********************************************************************************/

                        /*** Take Image from Camera ***/

    /*********************************************************************************/
    /**
     * Create file with current timestamp name
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        return mFile;
    }
    /**
     * Capture image from camera
     */
    private void dispatchTakePictureIntent(int RC) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                mPhotoFile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, RC);
            }
        }
    }


}
