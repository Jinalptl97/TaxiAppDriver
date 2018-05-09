package com.taxibookingdriver.Activities;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.taxibookingdriver.Amazon.Utils;
import com.taxibookingdriver.Controller.Config;
import com.taxibookingdriver.Controller.Constant;
import com.taxibookingdriver.Controller.GraduallyTextView;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Controller.Progress_dialog;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Admin on 1/30/2018.
 */

public class ProfileActivity extends AppCompatActivity {
    Context context = this;
    ImageView notify_bar;
    EditText et_fname, et_lname, et_mobile, et_email, et_pwd;
    Pref_Master pref;
    android.support.v7.app.AlertDialog dialog;
    RequestQueue queue;
    private static int RESULT_LOAD_IMG = 1;
    LruCache picassoCache;
    TextView rr_delete;
    File file;
    String android_id, verify;
    private TransferUtility transferUtility;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 55;
    AmazonS3Client amazonS3;
    String ip = "";
    int imagevalue = 0;
    private static final int CAMERA_REQUEST = 1888;
    private int PICK_IMAGE_REQUEST = 1;
    Uri uri;
    String image, copyimagepath, path;
    String ipaddress;
    File folder;
    CircleImageView img_user;
    ImageView img_save, img_edit;
    String btn_change = "0";
    TextInputLayout input_fname, input_lname, input_mobile, input_mail, input_pwd;
    int maxLength;

    @Override
    public void onBackPressed() {

        if (file != null) {
            file.delete();
        }
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            if (uri.toString().startsWith("content://com.google.android.apps.docs.storage")) {
                InputStream is = null;
                try {
                    is = context.getContentResolver().openInputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (is != null) {
                    Bitmap pictureBitmap = BitmapFactory.decodeStream(is);
//You can use this bitmap according to your purpose or Set bitmap to imageview

                    uri = getImageUri(context, pictureBitmap);

                    try {
                        path = getPath(uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    img_user.setImageURI(Uri.parse(path));

                    File source = new File(path);

                    File folder = new File(Environment.getExternalStorageDirectory().toString() + "/JoyDriver/GalleryImage");
                    folder.mkdirs();
//Save the path as a string value
                    String extStorageDirectory = folder.toString();
//Create New file and name it Image2.PNG
                    Long tsLong = System.currentTimeMillis() / 1000;
                    String ts = tsLong.toString();

                    File file = new File(extStorageDirectory, ts + ".jpg");
                    String pathh = file.getAbsolutePath();


                    File f = new File(pathh);
                    if (!f.exists()) {
                        try {
                            f.createNewFile();
                            copyFile(source, f);
                        } catch (IOException e) {
// TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

//Drawable d = new BitmapDrawable(getResources(), myBitmap);

                    copyimagepath = f.getAbsolutePath();

                    Log.e("GalleryDrive", copyimagepath);

                    imagevalue = 1;

                }
            } else {

                uri = data.getData();

                try {
                    path = getPath(uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                File source = new File(path);

                File folder = new File(Environment.getExternalStorageDirectory().toString() + "/JoyDriver/GalleryImage");
                folder.mkdirs();
                //Save the path as a string value
                String extStorageDirectory = folder.toString();
                //Create New file and name it Image2.PNG
                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();

                file = new File(extStorageDirectory, ts + ".jpg");
                String pathh = file.getAbsolutePath();


                File f = new File(pathh);
                if (!f.exists()) {
                    try {
                        f.createNewFile();
                        copyFile(source, f);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                //Drawable d = new BitmapDrawable(getResources(), myBitmap);

                copyimagepath = f.getAbsolutePath();

                img_user.setImageURI(Uri.parse(copyimagepath));

                imagevalue = 1;
            }


        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            uri = getImageUri(context, photo);


            try {
                path = getPath(uri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }


            File source = new File(path);

            File folder = new File(Environment.getExternalStorageDirectory().toString() + "/Joy/Cameraimage");
            folder.mkdirs();
            //Save the path as a string value
            String extStorageDirectory = folder.toString();
            //Create New file and name it Image2.PNG
            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();

            file = new File(extStorageDirectory, ts + ".jpg");
            String pathh = file.getAbsolutePath();


            File f = new File(pathh);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                    copyFile(source, f);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            //Drawable d = new BitmapDrawable(getResources(), myBitmap);

            copyimagepath = f.getAbsolutePath();

            img_user.setImageURI(Uri.parse(copyimagepath));

            Log.e("Camera", copyimagepath);

            imagevalue = 1;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        checkAndRequestPermissions();
        verify = "yes";

        pref = new Pref_Master(context);
//        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //dialog = new Progress_dialog();
        queue = Volley.newRequestQueue(context);
        transferUtility = Utils.getTransferUtility(context);
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
        ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        amazonS3 = Utils.getS3Client(context);
        et_fname = findViewById(R.id.et_fname);
        et_lname = findViewById(R.id.et_lname);
        et_mobile = findViewById(R.id.et_mobile);

        maxLength = Integer.parseInt(Constant.MOBILE_LENGTH);
        et_mobile.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});


        et_email = findViewById(R.id.et_email);
        et_pwd = findViewById(R.id.et_pwd);
        img_user = findViewById(R.id.img_user);
        // img_edit = findViewById(R.id.img_edit);
        notify_bar = findViewById(R.id.notify_bar);
        img_edit = findViewById(R.id.img_edit);
        img_save = findViewById(R.id.img_save);

        input_fname = findViewById(R.id.input_fname);
        input_lname = findViewById(R.id.input_lname);
        input_pwd = findViewById(R.id.input_pwd);
        input_mobile = findViewById(R.id.input_mobile);
        input_mail = findViewById(R.id.input_mail);

        getAddreess();

        Picasso.Builder builder = new Picasso.Builder(getApplicationContext());
        picassoCache = new LruCache(getApplicationContext());
        builder.memoryCache(picassoCache);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        et_fname.setEnabled(false);
        et_lname.setEnabled(false);
        et_email.setEnabled(false);
        et_pwd.setFocusable(false);
        et_pwd.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        et_pwd.setClickable(false);
        et_mobile.setEnabled(false);
        img_user.setEnabled(false);

        et_fname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                input_fname.setErrorEnabled(false);

            }
        });

        et_lname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                input_lname.setErrorEnabled(false);
            }
        });

        et_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                input_mobile.setErrorEnabled(false);
            }
        });

        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                input_mail.setErrorEnabled(false);
            }
        });

        et_fname.setText(pref.getFname());
        et_lname.setText(pref.getLname());
        et_email.setText(pref.getEmail());
//        if (pref.getEmail().equals("")) {
//            et_email.setHint("Email Id (Optional)");
//        }
        et_mobile.setText(pref.getMobile());
        et_pwd.setText(pref.getPwd());

        et_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_change.equals("1")) {
                    Intent i = new Intent(ProfileActivity.this, PasswordActivity.class);
                    i.putExtra("value", "2");
                    startActivity(i);
                }
            }
        });

        notify_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });

        if (pref.getBack_img().equals("")) {

            Picasso.with(context)
                    .load(R.drawable.personal) //extract as User instance method
                    .placeholder(R.drawable.personal)
                    .into(img_user);
        } else {

            Picasso.with(context)
                    .load(Constant.BUCKET_URL + pref.getUID() + "/" + "Profile" + "/" + pref.getBack_img()) //extract as User instance method
                    .placeholder(R.drawable.personal)
                    .into(img_user);
        }

        rr_delete = findViewById(R.id.rr_delete);
        rr_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                LayoutInflater li = LayoutInflater.from(context);
                View v = li.inflate(R.layout.dialogdelete, null);
                final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context).setView(v).show();
                alert.setCancelable(false);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView ok = v.findViewById(R.id.con_ok);
                TextView cancel = v.findViewById(R.id.cancel);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();

                        DeleteAccount();

                    }
                });


                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });


            }
        });

        img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_change = "1";
                img_edit.setVisibility(View.GONE);
                img_save.setVisibility(View.VISIBLE);

                et_fname.setEnabled(true);
                et_fname.setClickable(true);

                et_lname.setEnabled(true);
                et_lname.setClickable(true);

                et_mobile.setEnabled(true);
                et_mobile.setClickable(true);

                et_email.setEnabled(true);
                et_email.setClickable(true);

                et_pwd.setClickable(true);
                img_user.setEnabled(true);

            }
        });

        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_fname.getText().toString().equals("")) {
                    et_fname.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    input_fname.setError(getString(R.string.Enter_first_name));
                } else if (et_lname.getText().toString().equals("")) {
                    input_fname.setErrorEnabled(false);
                    et_lname.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    input_lname.setError(getString(R.string.Enter_Last_name));
                } else if (et_mobile.getText().toString().equals("")) {
                    input_lname.setErrorEnabled(false);
                    et_mobile.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    input_mobile.setError(getString(R.string.Enter_mobile));
                } else if (et_mobile.getText().toString().length() < maxLength) {
                    et_mobile.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    input_mobile.setError(getString(R.string.Mobile_number));
                } else if (et_email.getText().toString().length() != 0) {
                    input_mobile.setErrorEnabled(false);
                    if (et_email.getText().toString().contains(" ")) {
                        et_email.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        input_mail.setError(getString(R.string.Enter_Email_address));

                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString()).matches()) {
                        et_email.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        input_mail.setError(getString(R.string.Enter_valid_email_address));

                    } else {
                        input_mobile.setErrorEnabled(false);
                        input_lname.setErrorEnabled(false);
                        input_mail.setErrorEnabled(false);
                        input_fname.setErrorEnabled(false);
                        if (imagevalue == 1) {
                            beginUpload(copyimagepath);

                        } else {
                            image = pref.getBack_img();
                            makeJsonObjReq();
                        }
                    }


                } else {
                    input_mobile.setErrorEnabled(false);
                    input_lname.setErrorEnabled(false);
                    input_mail.setErrorEnabled(false);
                    input_fname.setErrorEnabled(false);
                    if (imagevalue == 1) {
                        beginUpload(copyimagepath);

                    } else {
                        image = pref.getBack_img();
                        makeJsonObjReq();
                    }
                }
            }
        });

        img_user.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

                checkAndRequestPermissions();

                if (file != null) {
                    file.delete();
                    LayoutInflater li = LayoutInflater.from(context);
                    View vv = li.inflate(R.layout.dialog_popup_camera, null);
                    final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context).setView(vv).show();
                    alert.setCancelable(true);
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView txt_camera = vv.findViewById(R.id.txt_camera);
                    TextView txt_gallery = vv.findViewById(R.id.txt_gallery);
                    txt_camera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            alert.dismiss();
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);


                        }
                    });
                    txt_gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMG);
                        }
                    });
                } else {
                    LayoutInflater li = LayoutInflater.from(context);
                    View vv = li.inflate(R.layout.dialog_popup_camera, null);
                    final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(context).setView(vv).show();
                    alert.setCancelable(true);
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView txt_camera = vv.findViewById(R.id.txt_camera);
                    TextView txt_gallery = vv.findViewById(R.id.txt_gallery);
                    txt_camera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            alert.dismiss();
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);


                        }
                    });
                    txt_gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMG);
                        }
                    });
                }
            }
        });
        MyApplication.getInstance().trackScreenView("Profile Screen");

    }

    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.CAMERA);
        int locationPermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }

        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    private void beginUpload(String filePath) {

        CannedAccessControlList cannedAccessControlList;
        cannedAccessControlList = CannedAccessControlList.PublicRead;


        if (filePath == null) {
            Toast.makeText(context, getString(R.string.Could_not_find_path), Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);

        amazonS3.deleteObject(Constant.BUCKET + "/" + "user-images" + "/" + pref.getUID() + "/" + "Profile", file.getName());

        copyimagepath = file.getName();

//        Toast.makeText(getApplicationContext(),file.getName(), Toast.LENGTH_LONG).show();

        Log.e("Upload", " " + Constant.BUCKET);
        final TransferObserver observer = transferUtility.upload(Constant.BUCKET + "/" + "user-images" + "/" + pref.getUID() + "/" + "Profile", file.getName(), file, new ObjectMetadata());


        /*
         * Note that usually we set the transfer listener after initializing the
         * transfer. However it isn't required in this sample app. The flow is
         * click upload button -> start an activity for image selection
         * startActivityForResult -> onActivityResult -> beginUpload -> onResume
         * -> set listeners to in progress transfers.
         */
        // observer.setTransferListener(new UploadListener());

        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.COMPLETED.equals(observer.getState())) {


                    //Toast.makeText(getApplicationContext(), "File Upload Complete", Toast.LENGTH_SHORT).show();
                    verify = "yes";
                    image = copyimagepath;
                    makeJsonObjReq();

                }
                if (state.IN_PROGRESS.equals(observer.getState())) {

                }

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                long _bytesCurrent = bytesCurrent;
                long _bytesTotal = bytesTotal;
                double percentage = ((double) _bytesCurrent / (double) _bytesTotal * 100);
                Log.e("percentage", "" + percentage);

            }

            @Override
            public void onError(int id, Exception ex) {
            }
        });


    }


    private String getPath(Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }
        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }


    private void makeJsonObjReq() {


        LayoutInflater li = LayoutInflater.from(context);
        View vv = li.inflate(R.layout.progress_dialog_layout, null);
        dialog = new android.support.v7.app.AlertDialog.Builder(context, R.style.cart_dialog).setView(vv).show();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        GraduallyTextView txt_load;
        ImageView img_gif;

        txt_load = (GraduallyTextView) vv.findViewById(R.id.txt_load);
        img_gif = (ImageView) vv.findViewById(R.id.img_gif);

        Glide.with(context)
                .load(R.drawable.taxi_gif)
                .asGif()
                .placeholder(R.drawable.taxi_gif)
                .crossFade()
                .into(img_gif);
        txt_load.startLoading();
        dialog.show();

        if (copyimagepath == null) {
            copyimagepath = "";
        }
        if (!pref.getMobile().equals(et_mobile.getText().toString())) {
            verify = "no";
        }
        if (!pref.getEmail().equals(et_email.getText().toString())) {
            verify = "no";
        }

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("uid", pref.getUID());
        postParam.put("utype", Config.driver);
        postParam.put("oldmobile", pref.getMobile());
        postParam.put("newmobile", et_mobile.getText().toString());
        postParam.put("oldemail", pref.getEmail());
        postParam.put("newemail", et_email.getText().toString());
        postParam.put("fname", et_fname.getText().toString());
        postParam.put("lname", et_lname.getText().toString());
        postParam.put("password", et_pwd.getText().toString());
        postParam.put("upic", image);
        postParam.put("verifiedotp", verify);

        Log.e("parameter", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.Update_profile, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.e("Reposnse", response.toString());
                        try {
                            Log.e("Status", response.getString("status"));

                            if (response.getString("status").equals("200")) {

                                img_save.setVisibility(View.GONE);
                                img_edit.setVisibility(View.VISIBLE);

                                et_fname.setEnabled(false);
                                et_lname.setEnabled(false);
                                et_email.setEnabled(false);
                                // et_pwd.setEnabled(false);
                                et_mobile.setEnabled(false);
                                img_user.setEnabled(false);
                                btn_change = "0";

                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();

                                if (response.getString("activity").equals("otp-sent")) {


                                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_LONG).show();

                                    Intent i = new Intent(getApplicationContext(), OTPActivity_Profile_change.class);
                                    i.putExtra("otp", response.getString("OTP"));
                                    i.putExtra("fname", et_fname.getText().toString());
                                    i.putExtra("lname", et_lname.getText().toString());
                                    i.putExtra("mobile", et_mobile.getText().toString());
                                    i.putExtra("email", et_email.getText().toString());
                                    i.putExtra("pass", et_pwd.getText().toString());
                                    i.putExtra("image", image);
                                    startActivity(i);


                                } else if (response.getString("activity").equals("update-profile")) {

                                    JSONArray jsonArray = new JSONArray(response.getString("data"));

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jobj1 = jsonArray.getJSONObject(i);

                                        pref.setFname(jobj1.getString("fname"));
                                        pref.setLname(jobj1.getString("lname"));
                                        pref.setMobile(jobj1.getString("newmobile"));
                                        pref.setEmail(jobj1.getString("newemail"));
                                        pref.setBack_img(jobj1.getString("upic"));


                                    }

                                    Log.e("image_value", Constant.BUCKET_URL + pref.getUID() + "/" + pref.getBack_img());

//                                    Glide.with(context)
//                                            .load(Constant.BUCKET_URL + pref.getUID() + "/" + "Profile" + "/" + pref.getBack_img()) //extract as User instance method
//                                            //.placeholder(R.drawable.personal)
//                                            .into(img_user);

                                }


                            } else if (response.getString("status").equals("400")) {

                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                VolleyLog.e("Error", "Error: " + error.getMessage());

            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Config.getHeaderParam(context);
            }


        };
        jsonObjReq.setTag("POST");
        // Adding request to request queue
        queue.add(jsonObjReq);

        // Cancelling request
    /* if (queue!= null) {
    queue.cancelAll(TAG);
    } */

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public void DeleteAccount() {

        LayoutInflater li = LayoutInflater.from(context);
        View vv = li.inflate(R.layout.progress_dialog_layout, null);
        dialog = new android.support.v7.app.AlertDialog.Builder(context, R.style.cart_dialog).setView(vv).show();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        GraduallyTextView txt_load;
        ImageView img_gif;

        txt_load = (GraduallyTextView) vv.findViewById(R.id.txt_load);
        img_gif = (ImageView) vv.findViewById(R.id.img_gif);

        Glide.with(context)
                .load(R.drawable.taxi_gif)
                .asGif()
                .placeholder(R.drawable.taxi_gif)
                .crossFade()
                .into(img_gif);
        txt_load.startLoading();
        dialog.show();
        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("uid", pref.getUID());
        postParam.put("utype", "Driver");
        postParam.put("ip", ipaddress);
        postParam.put("deviceid", android_id);
        postParam.put("dtoken", FirebaseInstanceId.getInstance().getToken());


        Log.e("parameter", " " + new JSONObject(postParam));

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.deleteaccount, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Reposnse", response.toString());
                        dialog.dismiss();
                        try {
                            Log.e("Status", response.getString("status"));

                            if (response.getString("status").equals("200")) {

                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();

                                Intent i = new Intent(getApplicationContext(), FirstScreen.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);


                            } else {
                                Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                VolleyLog.e("Error", "Error: " + error.getMessage());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Config.getHeaderParam(context);
            }
        };
        jsonObjReq.setTag("POST");
        queue.add(jsonObjReq);

    }

    public String getAddreess() {

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    //if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress() ) {
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        ipaddress = inetAddress.getHostAddress();

                    }
                }
            }
        } catch (SocketException ex) {
            /*Log.d(TAG, ex.toString());*/
        }


        return null;
    }

    public void deleteRecursive(File fileOrDirectory) {


        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }


}
