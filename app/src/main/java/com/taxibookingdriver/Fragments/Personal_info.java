package com.taxibookingdriver.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.taxibookingdriver.Controller.Config;
import com.taxibookingdriver.Controller.DialogBox;
import com.taxibookingdriver.Controller.GraduallyTextView;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Controller.Progress_dialog;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.app.Activity.RESULT_OK;
import static com.taxibookingdriver.Activities.Information_tab.viewpager;

/**
 * Created by Admin on 1/16/2018.
 */

@SuppressLint("ValidFragment")
public class Personal_info extends Fragment {

    Context context;
    EditText et_fname, et_lname, et_email, et_pwd, et_id, et_nric;
    TextView txt_mobile;
    RelativeLayout rr_personal;
    Pref_Master pref;
    TextView txt_head;
    RelativeLayout rr_front, rr_back;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 55;
    private static int RESULT_LOAD_IMG = 1;
    private static int CAMERA_PIC_REQUEST = 2;
    ImageView img_front, img_back;
    Uri uri;
    String path = "";
    String var = "";
    String pre_path = "";
    public static String value = "";
    android.support.v7.app.AlertDialog dialog;
    RequestQueue queue;
    ScrollView scrollview;
    RelativeLayout rr_info;
    TextInputLayout input_fname, input_lname, input_email, input_pwd, input_id, input_nric;


    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal_info, container, false);
        context = getActivity();
        pref = new Pref_Master(context);
        checkAndRequestPermissions();

        // dialog = new Progress_dialog();

        queue = Volley.newRequestQueue(context);
        scrollview = view.findViewById(R.id.scrollview);


        pref.setFront_img("");
        pref.setBack_img("");

        txt_head = getActivity().findViewById(R.id.headertext);
        et_fname = view.findViewById(R.id.et_fname);
        et_lname = view.findViewById(R.id.et_lname);
        et_email = view.findViewById(R.id.et_email);
        et_pwd = view.findViewById(R.id.et_pwd);
        et_id = view.findViewById(R.id.et_id);
        et_nric = view.findViewById(R.id.et_nric);
        rr_front = view.findViewById(R.id.rr_front);
        rr_back = view.findViewById(R.id.rr_back);
        img_front = view.findViewById(R.id.img_front);
        img_back = view.findViewById(R.id.img_back);

        input_fname = (TextInputLayout) view.findViewById(R.id.input_fname);
        input_lname = (TextInputLayout) view.findViewById(R.id.input_lname);
        input_email = (TextInputLayout) view.findViewById(R.id.input_email);
        input_pwd = (TextInputLayout) view.findViewById(R.id.input_pwd);
        input_id = (TextInputLayout) view.findViewById(R.id.input_id);
        input_nric = (TextInputLayout) view.findViewById(R.id.input_nric);

        rr_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissions();
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
                        value = "1";
                        alert.dismiss();
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);


                    }
                });
                txt_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        value = "1";
                        alert.dismiss();
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMG);

                    }
                });


//

            }
        });


        rr_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissions();
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
                        value = "2";
                        alert.dismiss();
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);

                    }
                });
                txt_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        value = "2";
                        alert.dismiss();
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMG);
                    }
                });


            }
        });

        txt_mobile = view.findViewById(R.id.txt_mobile);
        txt_mobile.setText(pref.getMobile());
        rr_personal = view.findViewById(R.id.rr_personal);

        rr_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validation();

            }
        });

        et_fname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    scrollview.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                }
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
                if (s.length() >= 1) {
                    scrollview.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                input_lname.setErrorEnabled(false);

            }
        });
        et_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    scrollview.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                input_pwd.setErrorEnabled(false);

            }
        });
        et_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    scrollview.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                input_id.setErrorEnabled(false);

            }
        });
        et_nric.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    scrollview.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                input_nric.setErrorEnabled(false);

            }
        });

        return view;
    }


    private void Validation() {
        if (et_fname.getText().toString().equals("")) {
            et_fname.requestFocus();
            input_fname.setError(getString(R.string.Enter_first_name));
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            scrollview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        } else if (et_lname.getText().toString().equals("")) {
            input_fname.setErrorEnabled(false);
            input_lname.setError(getString(R.string.Enter_Last_name));
            et_lname.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            scrollview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        } else if (et_email.getText().toString().length() != 0) {
            input_lname.setErrorEnabled(false);
            if (et_email.getText().toString().contains(" ")) {
                et_email.requestFocus();
                input_email.setError(getString(R.string.Enter_Email_address));
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                scrollview.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString()).matches()) {
                et_email.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                input_email.setError(getString(R.string.Enter_valid_email_address));
                scrollview.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
            } else {
                if (et_pwd.getText().toString().equals("")) {
                    input_pwd.setError(getString(R.string.Enter_password));
                    input_email.setErrorEnabled(false);
                    et_pwd.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    scrollview.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });
                } else if (et_pwd.getText().toString().trim().length() < 6) {
                    input_pwd.setError(getString(R.string.Enter_password_valid));
                    input_email.setErrorEnabled(false);
                    et_pwd.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    scrollview.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });
                } else if (et_id.getText().toString().equals("")) {
                    input_id.setError(getString(R.string.Enter_driving_license_no));
                    input_pwd.setErrorEnabled(false);
                    et_id.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    scrollview.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });
//                } else if (et_nric.getText().toString().equals("")) {
//                    input_nric.setError(getString(R.string.Enter_nric_no));
//                    input_id.setErrorEnabled(false);
//                    et_nric.requestFocus();
//                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//                    scrollview.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View v, MotionEvent event) {
//                            return true;
//                        }
//                    });
                } else if (pref.getFront_img().equals("")) {
                    input_nric.setErrorEnabled(false);
                    Toast.makeText(context, getString(R.string.Choose_front_image), Toast.LENGTH_SHORT).show();
                } else if (pref.getBack_img().equals("")) {
                    input_nric.setErrorEnabled(false);
                    Toast.makeText(context, getString(R.string.Choose_back_image), Toast.LENGTH_SHORT).show();

                } else {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    scrollview.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });
                    Log.e("abc", "abc");
                    Check_details();

                }
            }
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            scrollview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

        } else if (et_pwd.getText().toString().equals("")) {
            input_pwd.setError(getString(R.string.Enter_password));
            et_pwd.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            scrollview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        } else if (et_pwd.getText().toString().trim().length() < 6) {
            input_pwd.setError(getString(R.string.Enter_password_valid));
            et_pwd.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            scrollview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        } else if (et_id.getText().toString().equals("")) {
            input_id.setError(getString(R.string.Enter_driving_license_no));
            input_pwd.setErrorEnabled(false);
            et_id.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            scrollview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
//        } else if (et_nric.getText().toString().equals("")) {
//            input_nric.setError(getString(R.string.Enter_nric_no));
//            input_id.setErrorEnabled(false);
//            et_nric.requestFocus();
//            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//            scrollview.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    return true;
//                }
//            });
        } else if (pref.getFront_img().equals("")) {
            input_nric.setErrorEnabled(false);
            Toast.makeText(context, getString(R.string.Choose_front_image), Toast.LENGTH_SHORT).show();
        } else if (pref.getBack_img().equals("")) {
            input_nric.setErrorEnabled(false);
            Toast.makeText(context, getString(R.string.Choose_back_image), Toast.LENGTH_SHORT).show();


        } else {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            scrollview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            Log.e("def", "def");
            Check_details();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        int locationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Hello", String.valueOf(requestCode));
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK && null != data) {


            Bitmap photo = (Bitmap) data.getExtras().get("data");

            if (value.equals("1")) {
                uri = getImageUri(context, photo);


                try {
                    path = getPath(uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }


                File source = new File(path);

                File folder = new File(Environment.getExternalStorageDirectory().toString() + "/Joydriver/Cameraimage");
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
                var = f.getAbsolutePath();
                Log.e("var_", var);
                pre_path = var.substring(var.lastIndexOf("/") + 1);
                Log.e("pre_path", pre_path);


                Bitmap bitmap = BitmapFactory.decodeFile(var);
                img_front.setImageBitmap(bitmap);

                pref.setFront_img(pre_path);
                pref.setFront_img_var(var);
            } else {
                uri = getImageUri(context, photo);


                try {
                    path = getPath(uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }


                File source = new File(path);

                File folder = new File(Environment.getExternalStorageDirectory().toString() + "/Joydriver/Cameraimage");
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
                var = f.getAbsolutePath();
                Log.e("var_", var);
                pre_path = var.substring(var.lastIndexOf("/") + 1);
                Log.e("pre_path", pre_path);


                Bitmap bitmap = BitmapFactory.decodeFile(var);
                img_back.setImageBitmap(bitmap);

                pref.setBack_img(pre_path);
                pref.setBack_img_var(var);
            }


        } else if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
            if (value.equals("1")) {
                uri = data.getData();
                Log.e("uri", data.getData().toString());

                if (uri.toString().startsWith("content://com.google.android.apps.docs.storage")) {
                    try {
                        InputStream is = context.getContentResolver().openInputStream(uri);
                        if (is != null) {
                            Bitmap pictureBitmap = BitmapFactory.decodeStream(is);
//You can use this bitmap according to your purpose or Set bitmap to imageview

                            uri = getImageUri(context, pictureBitmap);

                            try {
                                path = getPath(uri);
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }

                            File source = new File(path);

                            File folder = new File(Environment.getExternalStorageDirectory().toString() + "/Joydriver/GalleryImage");
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

                            var = f.getAbsolutePath();

                            pre_path = var.substring(var.lastIndexOf("/") + 1);
                            img_front.setImageURI(Uri.parse(var));

                            Log.e("var", var);
                            Log.e("pre_path", pre_path);

                            pref.setFront_img(pre_path);
                            pref.setFront_img_var(var);

                        }
                    } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {

                    try {
                        path = getPath(uri);
                        Log.e("Path", path);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    File source = new File(path);

                    File folder = new File(Environment.getExternalStorageDirectory().toString() + "/Joydriver/GalleryImage");
                    folder.mkdirs();
                    String extStorageDirectory = folder.toString();

                    File file = new File(extStorageDirectory, getSaltString() + ".jpg");
                    String pathh = file.getAbsolutePath();
                    Log.e("PAthhh", pathh);

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

                    var = f.getAbsolutePath();

                    pre_path = var.substring(var.lastIndexOf("/") + 1);
                    img_front.setImageURI(Uri.parse(var));

                    Log.e("var", var);
                    Log.e("pre_path", pre_path);
//                Bitmap myBitmap = decodeSampledBitmapFromFile(f.getAbsolutePath(), 1000, 700);
//                try {
//                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(f.getAbsolutePath()));
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                var = f.getAbsolutePath();
//                Log.e("var_", var);
//                pre_path = var.substring(var.lastIndexOf("/") + 1);
//                Log.e("pre_path", pre_path);
//
//
//                Bitmap bitmap = BitmapFactory.decodeFile(var);
//                img_front.setImageBitmap(bitmap);

                    pref.setFront_img(pre_path);
                    pref.setFront_img_var(var);
                }
            } else {
                uri = data.getData();
                Log.e("uri", data.getData().toString());

                if (uri.toString().startsWith("content://com.google.android.apps.docs.storage")) {
                    try {
                        InputStream is = context.getContentResolver().openInputStream(uri);
                        if (is != null) {
                            Bitmap pictureBitmap = BitmapFactory.decodeStream(is);
//You can use this bitmap according to your purpose or Set bitmap to imageview

                            uri = getImageUri(context, pictureBitmap);

                            try {
                                path = getPath(uri);
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }

                            File source = new File(path);

                            File folder = new File(Environment.getExternalStorageDirectory().toString() + "/Joydriver/GalleryImage");
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

                            var = f.getAbsolutePath();

                            pre_path = var.substring(var.lastIndexOf("/") + 1);
                            img_back.setImageURI(Uri.parse(var));

                            Log.e("var", var);
                            Log.e("pre_path", pre_path);

                            pref.setBack_img(pre_path);
                            pref.setBack_img_var(var);
                        }
                    } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    try {
                        path = getPath(uri);
                        Log.e("Path", path);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    File source = new File(path);

                    File folder = new File(Environment.getExternalStorageDirectory().toString() + "/Joydriver/GalleryImage");
                    folder.mkdirs();
                    String extStorageDirectory = folder.toString();

                    File file = new File(extStorageDirectory, getSaltString() + ".jpg");
                    String pathh = file.getAbsolutePath();
                    Log.e("PAthhh", pathh);

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
//
//                Bitmap myBitmap = decodeSampledBitmapFromFile(f.getAbsolutePath(), 1000, 700);
//                try {
//                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(f.getAbsolutePath()));
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                var = f.getAbsolutePath();
//                Log.e("var_", var);
//                pre_path = var.substring(var.lastIndexOf("/") + 1);
//                Log.e("pre_path", pre_path);
//
//                Bitmap bitmap = BitmapFactory.decodeFile(var);
//                img_back.setImageBitmap(bitmap);

                    var = f.getAbsolutePath();

                    pre_path = var.substring(var.lastIndexOf("/") + 1);
                    img_back.setImageURI(Uri.parse(var));

                    Log.e("var", var);
                    Log.e("pre_path", pre_path);

                    pref.setBack_img(pre_path);
                    pref.setBack_img_var(var);
                }
            }

        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private String currentDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    private void storeCameraPhotoInSDCard(Bitmap bitmap, String currentDate) {
        File outputFile = new File(Environment.getExternalStorageDirectory(), "/Joydriver/GalleryImage" + getSaltString() + ".jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getImageFileFromSDCard(String filename) {
        Bitmap bitmap = null;
        File imageFile = new File(Environment.getExternalStorageDirectory() + filename);
        try {
            FileInputStream fis = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    protected String getSaltString() {
        String SALTCHARS = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 12) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }

        return salt.toString();
    }


    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) { // BEST QUALITY MATCH

//First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

// Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
//if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }

        options.inSampleSize = inSampleSize;

// Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
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

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("Personal Info");
    }

    @SuppressLint("NewApi")
    private String getPath(Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
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
                cursor = context.getContentResolver()
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

    private void Check_details() {

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
        postParam.put("tab", "1");
        postParam.put("mobile", txt_mobile.getText().toString());
        postParam.put("email", et_email.getText().toString());
        postParam.put("idno", et_id.getText().toString());

        Log.e("parameter_personal_info", " " + new JSONObject(postParam));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Config.Check_details, new JSONObject(postParam),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        Log.e("Reposnse", response.toString());

                        //{"status":200,"activity":"register-success","message":"Registered Successfully !","data":[{"uid":"1516340787249"}]}

                        String jsonData = response.toString();

//

                        try {

                            JSONObject jobj = new JSONObject(jsonData);

                            if (jobj.getString("status").equals("200")) {

                                if (jobj.getString("activity").equals("allow-tab2")) {

                                    viewpager.setCurrentItem(1);
                                    txt_head = getActivity().findViewById(R.id.headertext);
                                    txt_head.setText(getResources().getString(R.string.Car));
                                    pref.setFname(et_fname.getText().toString());
                                    pref.setLname(et_lname.getText().toString());
                                    pref.setEmail(et_email.getText().toString());
                                    pref.setPwd(et_pwd.getText().toString());
                                    pref.setId(et_id.getText().toString());
                                    pref.setNric(et_nric.getText().toString());


                                } else {
                                    Toast.makeText(context, jobj.getString("message"), Toast.LENGTH_LONG).show();
                                }
                            } else if (jobj.getString("status").equals("400")) {
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                scrollview.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        return false;
                                    }
                                });
                                if (jobj.getString("activity").equals("mobile-exist")) {
                                    DialogBox.setAction(context, jobj.getString("message"));
//                                    Intent i = new Intent(context, FirstScreen.class);
//                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(i);
                                } else if (jobj.getString("activity").equals("email-exist")) {
                                    et_email.requestFocus();
                                    InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    im.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                    scrollview.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            return false;
                                        }
                                    });
                                    Toast.makeText(context, jobj.getString("message"), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, jobj.getString("message"), Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Toast.makeText(context, jobj.getString("message"), Toast.LENGTH_LONG).show();
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


}
