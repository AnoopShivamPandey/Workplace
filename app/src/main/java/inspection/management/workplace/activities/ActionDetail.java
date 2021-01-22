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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import inspection.management.workplace.BuildConfig;
import inspection.management.workplace.CheckInternetConnection.InternetConnectivity;
import inspection.management.workplace.FileCompressor;
import inspection.management.workplace.GetApiParameter;
import inspection.management.workplace.MySingleton;
import inspection.management.workplace.R;

public class ActionDetail extends AppCompatActivity {

    @BindView(R.id.d_action_edit)
    EditText d_action_edit;
    @BindView(R.id.d_action)
    TextView d_action;
    @BindView(R.id.d_action_done)
    Button d_action_done;
    @BindView(R.id.d_action_loc)
    TextView d_action_loc;
    @BindView(R.id.d_action_sd)
    TextView d_action_sd;
    @BindView(R.id.d_action_ed)
    TextView d_action_ed;
    @BindView(R.id.imageaction)
    ImageView imageaction;
    ProgressDialog loadingDialog;
    GetApiParameter apiParameter;
    private String status;
    private String action;
    private String a_id,topic_id;
    private String topic, s_date,e_date,location,employe_name,topic_name,pic_Path_one;
    Bundle data;
    private Bitmap bitmap;
    private int REQUEST_TAKE_PHOTO=100;
    private File mPhotoFile;
    private FileCompressor mCompressor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_detail);
        ButterKnife.bind(this);
        mCompressor = new FileCompressor(this);
        getSupportActionBar().setTitle("Action Detail");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        apiParameter = new GetApiParameter();
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("please wait loading....");
        loadingDialog.setCancelable(false);
        data = getIntent().getExtras();
        if (data!=null){
            status = data.getString("status");
            action = data.getString("action");
            a_id = data.getString("id");
            e_date = data.getString("end_date");
            s_date = data.getString("start_date");
            location = data.getString("location");
            topic = data.getString("topic");
            topic_id = data.getString("topic_id");
            topic_name = data.getString("topic_name");
            employe_name = data.getString("employee_name");
            d_action.setText(""+topic_name);
            d_action_ed.setText(""+s_date);
            d_action_sd.setText(""+e_date);
            d_action_loc.setText(""+location);
            d_action_edit.setText(""+action);
            checkPermissions();
            Log.e("Data", "Selected Action Data ="+status+" "+action+" "+a_id+" "+e_date+" "+s_date+" "+location+" "+employe_name+" "+topic+" topic_id = "+topic_id);
        }

    }
    @OnClick({R.id.d_action_done})
    public void uploadResponseClick(View v) {
        switch (v.getId()) {
            case R.id.d_action_done:
                if (d_action_edit.getText().toString().equalsIgnoreCase("")){
                    d_action_edit.setError("Field not empty");
                    d_action_edit.setFocusable(true);
                }
                else {
                    loadingDialog.show();
                    uploadResponse();
                }
                break;
        }

    }

    private void uploadResponse() {
        String ACTION_UPD_URL = apiParameter.getApiUrl() +"update_action";
        Log.e("@@ACTION_UPD_URL@@", ACTION_UPD_URL);
        if (pic_Path_one !=null) {
            //String INSPECT_UPD_URL = apiParameter.getApiUrl() + "update_inspection";
            //Log.e("@@INSPECT_UPD_URL@@", INSPECT_UPD_URL);
            //File imageFile = new File(pic_Path_one);
            //File imageFile2 = new File(pic_Path_two);
            AndroidNetworking.upload(ACTION_UPD_URL)
                    .addMultipartFile("image", mPhotoFile)
                    .addMultipartParameter("id", a_id)
                    .addMultipartParameter("topic_name", topic)
                    .addMultipartParameter("status", "ok")
                    .addMultipartParameter("end_date", e_date)
                    .addMultipartParameter("start_date", s_date)
                    .addMultipartParameter("employee_name", employe_name)
                    .addMultipartParameter("location", location)
                    .addMultipartParameter("topic", topic_id)
                    .addMultipartParameter("action", d_action_edit.getText().toString())
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
                                        Toast.makeText(ActionDetail.this, ""+message, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ActionDetail.this, HomeActivity.class));
                                    }
                                    else {
                                        Toast.makeText(ActionDetail.this, "The action not updated something wrong", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            if (InternetConnectivity.isNetworkConnected(ActionDetail.this)) {
                                //progressDialog.dismiss();

                                loadingDialog.dismiss();
                                anError.printStackTrace();
                                Toast.makeText(ActionDetail.this, getResources().getString(R.string.str_error_u), Toast.LENGTH_SHORT).show();
                            } else {
                                anError.printStackTrace();
                                loadingDialog.dismiss();
                                Toast.makeText(ActionDetail.this, getResources().getString(R.string.str_connection), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
        else {
            loadingDialog.dismiss();Toast.makeText(this, "select a picture to continue", Toast.LENGTH_SHORT).show();
        }
        /*final StringRequest loginRequest = new StringRequest(Request.Method.POST, ACTION_UPD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("@ACTION_UPD Response@", response.toString());
                //Toast.makeText(WorkplaceLogin.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                if (response.length()>0) {
                    try {
                        JSONObject result = new JSONObject(response);
                        String STATUS = result.getString("status");
                        String MSG = result.getString("message");
                        if (STATUS.equals("true")){
                            String data = result.getString("data");
                            Toast.makeText(ActionDetail.this, ""+MSG, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ActionDetail.this, HomeActivity.class));
                        }
                        else {
                            Toast.makeText(ActionDetail.this, ""+MSG, Toast.LENGTH_SHORT).show();
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
                if (InternetConnectivity.isNetworkConnected(ActionDetail.this)) {
                    error.printStackTrace();
                    Log.e("@Volley Error@", "Error: " + error + "\nStatus Code " + error.networkResponse.statusCode + "\nCause " + error.getCause() + "\nnetworkResponse " + error.networkResponse.data.toString() + "\nmessage" + error.getMessage());
                    AlertDialog.Builder networkAlertDialog = new AlertDialog.Builder(ActionDetail.this);
                    networkAlertDialog.setCancelable(true);
                    networkAlertDialog.setTitle("Server Error");
                    networkAlertDialog.setMessage("Server does not response try again...");
                    AlertDialog alertDialog = networkAlertDialog.create();
                    alertDialog.show();
                    loadingDialog.dismiss();
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(ActionDetail.this, "check your internet connection and try again", Toast.LENGTH_SHORT).show();
                }
            }
        })

        {//adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", a_id);
                params.put("topic", topic_id);
                params.put("action", d_action_edit.getText().toString());
                params.put("status", "ok");
                params.put("end_date", e_date);
                params.put("start_date", s_date);
                params.put("employee_name", employe_name);
                params.put("location", location);
                params.put("topic_name", topic);
                return params;
            }
        };
        loginRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(ActionDetail.this).addToRequestQueue(loginRequest);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void checkPermissions() {
        Dexter.withActivity(ActionDetail.this)
                .withPermissions(
                        Manifest.permission.CAMERA
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            //showPictureDialog();
                            //Toast.makeText(SignUp.this, "All permission granted", Toast.LENGTH_SHORT).show();
                            dispatchTakePictureIntent();

                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            new AlertDialog.Builder(ActionDetail.this)
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
    /*********************************************************************************/

                          /* Select Image From Gallery*/

    /*********************************************************************************/
    private void showPictureDialog() {
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
                    startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 22);
                } else if (items[item].equals(getString(R.string.str_cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode== Activity.RESULT_OK) {
                try {
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                    pic_Path_one = mPhotoFile.getAbsolutePath();
                    Log.e("@URI PATH@", "onActivityResult:" + pic_Path_one);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Glide.with(ActionDetail.this)
                        .load(mPhotoFile)
                        .apply(new RequestOptions().centerCrop()
                                .circleCrop()
                                .placeholder(R.drawable.workplacelogo))
                        .into(imageaction);
            }
            else if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "User Cancel Take Camera Image", Toast.LENGTH_SHORT).show();
            }
        }
        /*if (resultCode == Activity.RESULT_OK) {
           if (requestCode == BEFORE_CAMERA_REQUEST)
                onCaptureImageResult(data);
        }*/
       /* if (requestCode == 22 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            pic_Path_one = getPath(ActionDetail.this, uri);
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
                Glide.with(ActionDetail.this).load(uri).placeholder(R.drawable.workplacelogo).into(imageaction);

            }
        }*/

    }
    /*public String getPath(Context context, Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }*/
   /* private void onCaptureImageResult(Intent data) {
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
        //Glide.with(ActionDetail.this).load(destination.getPath()).placeholder(R.drawable.workplacelogo).into(imagetwo);
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
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        return mFile;
    }
    /**
     * Capture image from camera
     */
    private void dispatchTakePictureIntent() {
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
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
}
