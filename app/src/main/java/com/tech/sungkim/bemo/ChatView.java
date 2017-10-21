package com.tech.sungkim.bemo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.tech.sungkim.adapters.AdapterMessage;
import com.tech.sungkim.model.MessageChat;
import com.tech.sungkim.model.Provider;
import com.tech.sungkim.model.SupportTeam;
import com.tech.sungkim.model.User;
import com.tech.sungkim.util.Config;
import com.tech.sungkim.util.DialogStatic;
import com.tech.sungkim.util.FileUtils;
import com.tech.sungkim.util.StaticMethods;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import in.juspay.godel.ui.OnScreenDisplay;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.attr.x;

public class ChatView extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_PHOTO_PICKER = 150;

    private static final int REQUEST_Q = 501;

    private static final String TAG = "bemo";

    private ProgressBar progress;
    private ImageView img_doctor;
    public TextView txt_namedoctor, txt_profession, txt_experience;
    public ImageButton  imgbtn_sendchat;
    private RecyclerView chat_recyclerView;
    private EditText edit_message;
    public Toolbar toolbar;
    private String mProviderId;
    private boolean back;

    private String session = null;
    private String gcmId;
    private File selectFile;
    private String mConversationId;
    private DatabaseReference pathFirebase;
    private DatabaseReference pathMessages;
    private DatabaseReference pathUserAttention;
    private DatabaseReference pathRecordUser;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private String mFilePath;
    private ValueEventListener listenerStateAttention;
    private DatabaseReference pathRecordProvider;
    private int SENDER = 0;
    private int RECEIVE = 1;
    private String Support_Id;

    private AdapterMessage adapter;
    private long mTimeOut;
    private RelativeLayout expiredLayout;
    private String Patient_name;
    private String Patient_id;

    private DialogStatic dialogStatic;

    Handler handT = new Handler();
    Runnable run;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatStorageRef;
    private String episodeId;

    private static final int REQUEST_Q3 = 503;
    private static final int REQUEST_Q4 = 504;
    private FirebaseAuth mAuth;
    private FirebaseUser fb_user;
    private DialogStatic loadingDialog;
    private Provider mProvider;
    private User bemoUser;
    private FloatingActionMenu attachMenu;
    private TextView image_send_button;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseAuth.AuthStateListener authListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);
        back = false;
        pathFirebase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        fb_user = mAuth.getCurrentUser();
        loadingDialog = new DialogStatic(ChatView.this, "", "Loading chat window");
        initialization();
        Toolbar bar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialogStatic = new DialogStatic(ChatView.this, "", "Sending msg...");

        mFirebaseStorage = FirebaseStorage.getInstance();
        mChatStorageRef = mFirebaseStorage.getReference().child("chat_photos");
        List<MessageChat> emptyMessageChat = new ArrayList<>();
        adapter = new AdapterMessage(emptyMessageChat, mItemClick);
        chat_recyclerView.setAdapter(adapter);
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference().child("user_files");


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // new ProviderManager(ListPatients.this).clearProviderData();
                     //StaticMethods.showErrorMessageToUser("You don't have access to a account of provider",
                     //ChatView.this);
                }
            }
        };

    }




    private void galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void initialization() {


        Log.i(TAG, "initialization: ");
        progress = (ProgressBar) findViewById(R.id.progress);
        img_doctor = (ImageView) findViewById(R.id.therapist_image);
        dialogStatic = new DialogStatic(this, "Please wait", "Loading");
        txt_namedoctor = (TextView) findViewById(R.id.txt_namedoctor);
        txt_profession = (TextView) findViewById(R.id.txt_profession);
        txt_experience = (TextView) findViewById(R.id.txt_experience);
        chat_recyclerView = (RecyclerView) findViewById(R.id.chat_recyclerView);
        edit_message = (EditText) findViewById(R.id.edit_message);
        imgbtn_sendchat = (ImageButton) findViewById(R.id.imgbtn_sendchat);
        image_send_button =(TextView)findViewById(R.id.send_image_message);
        Patient_name = fb_user.getDisplayName();
        Patient_id = fb_user.getUid();







        expiredLayout = (RelativeLayout) findViewById(R.id.xpired_window);

       // attachMenu = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        imgbtn_sendchat.setOnClickListener(this);



        //setting onClick listener on the + button to send image in the message
        image_send_button.setOnClickListener(this);


        chat_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chat_recyclerView.setHasFixedSize(true);
        pathFirebase.child("users").child(fb_user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        bemoUser = dataSnapshot.getValue(User.class);
                        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("basic_app_info", MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(bemoUser);
                        Log.d(TAG, "onDataChange: The user json :" + json);
                        // myObject - instance of MyObject
                        prefsEditor.putString("User", json);
                        prefsEditor.commit();
                        back = true;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



        //getting the fcm_id of the user from the shared preferences
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        gcmId = pref.getString("regId", null);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Log.d(TAG,"Entered in the session chat");
            Boolean isExpired = bundle.getBoolean("session_expired");
            Log.d(TAG,isExpired.toString());




            //if the user session is still not expired
            if (isExpired == false) {
                mConversationId = (String) bundle.get("conversation");
                Log.d(TAG, "initialization: " + "conversation id" + mConversationId);
                pathMessages = pathFirebase.child("conversation").child(mConversationId).child("messages");
                pathFirebase.child("conversation").child(mConversationId)
                        .child("end_time").addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.getValue().toString().equals("")) {
                            Log.d(TAG, "onDataChange: " + "end_time value event");
                            mTimeOut = (long) dataSnapshot.getValue();
                            int permissionCheck2 = ContextCompat.checkSelfPermission(ChatView.this,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            if (permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(ChatView.this,
                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_Q3
                                );
                            }

                            pathFirebase.child("conversation").child(mConversationId)
                                    .addValueEventListener(timeCheckMessageListener);
                            pathFirebase.child("conversation").child(mConversationId)
                                    .child("messages").addChildEventListener(messageListener);
                            imgbtn_sendchat.setEnabled(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }


            // if the user's session expired
            else {
                Log.d(TAG," Not Entered in the session chat");
                mConversationId = (String) bundle.get("conversation");
                pathFirebase.child("conversation").child(mConversationId)
                        .child("messages").addChildEventListener(messageListener);
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.xpired_window);
                layout.setVisibility(View.VISIBLE);
              //  ((FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu)).setVisibility(View.INVISIBLE);
                Button payButton = (Button) findViewById(R.id.pay_btn_);
                payButton.setEnabled(true);
                imgbtn_sendchat.setEnabled(false);
                payButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: uid : " + mAuth.getCurrentUser().getUid());
                        pathFirebase.child("episodes")
                                .child(bemoUser.getEpisodes().get(0)).child("paid").setValue(false);
                        startActivity(new Intent(ChatView.this, PGActivity.class));

                    }
                });
            }
        }


        //fetching the image of the doctor and displaying it on the toolbar with the name of the doctor
        imgbtn_sendchat.setEnabled(false);
        loadingDialog.show();
        mProvider = (Provider) bundle.get("provider");
        if (mProvider != null) {

            String nm = mProvider.getFullName();
            String prof = mProvider.getSpecialty();
            String imageUrl = mProvider.getimage();
            ImageView imageView = (ImageView) findViewById(R.id.therapist_image);
            txt_namedoctor.setText("Dr. " + nm);
            txt_profession.setText("Specialty: " + prof);
            Glide.with(ChatView.this)
                    .load(imageUrl)
                    .centerCrop()
                    .into(imageView);

            loadingDialog.dissmis();
        }



        //taking the name of the patient from Question 7 to display in the chat with the message
        pathFirebase.child("users").child(Patient_id).child("Question7").child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Patient_name = dataSnapshot.getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //fetching counseller UID from the database to display name and profile pic of the Therapist
        pathFirebase.child("users").child(fb_user.getUid()).child("episodes").child("0")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        episodeId = (String) dataSnapshot.getValue();

                        //fetching the support member id assigned to the patient
                        pathFirebase.child("episodes").child(episodeId).child("Participants").child("Support_id").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Support_Id = dataSnapshot.getValue().toString();
                                Log.e(TAG,Support_Id);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        //fetchingg the Doctor's data from the database
                        pathFirebase.child("episodes").child(episodeId).child("Participants")
                                .child("counsellor_uid").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null && !(dataSnapshot.getValue()).equals("")) {
                                    mProviderId = (String) dataSnapshot.getValue();

                                    pathFirebase.child("users").child(mProviderId).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (mProvider == null) {
                                                String nm = (String) dataSnapshot.child("name").getValue();
                                                String prof = (String) dataSnapshot.child("specialty").getValue();
                                                String imageUrl = (String) dataSnapshot.child("image").getValue();
                                                ImageView imageView = (ImageView) findViewById(R.id.therapist_image);
                                                txt_namedoctor.setText("Dr. " + nm);
                                                txt_profession.setText("Specialty: " + prof);
                                                Glide.with(ChatView.this)
                                                        .load(imageUrl)
                                                        .centerCrop()
                                                        .into(imageView);
                                                imgbtn_sendchat.setEnabled(true);
                                                loadingDialog.dissmis();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }






    //download images and stores it in storage
    public void downloadRemoteAttachments(final MessageChat newMessage) {
        progress.setVisibility(View.VISIBLE);
        imgbtn_sendchat.setEnabled(false);

        image_send_button.setEnabled(false);

        progress.setVisibility(View.VISIBLE);
        Log.d(TAG, "downloadRemoteAttachments: use this link!!" + newMessage.getUri());
        Ion.with(ChatView.this)
                .load(newMessage.getUri())
                .progressBar(progress)
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        System.out.println("progress " + downloaded + " /" + total);
                    }
                })
                .write(selectFile)
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        imgbtn_sendchat.setEnabled(true);

                        image_send_button.setEnabled(true);

                        if (e == null && file != null) {
                            galleryAddPic(file.getAbsolutePath());
                            Log.i(TAG, "onCompleted: " + file.getAbsolutePath() + " total space: " + file.getTotalSpace());
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            Bitmap imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            if (imageBitmap.getWidth() >= 512) {
                                options.inSampleSize = 2;
                            } else if (imageBitmap.getWidth() < 512) {
                                options.inSampleSize = 1;
                            } else if (imageBitmap.getWidth() >= 1024) {
                                options.inSampleSize = 8;
                            }
                            Bitmap mBitmapInsurance = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                            newMessage.setLocalBitmap(mBitmapInsurance);
                            adapter.refillAdapter(newMessage);
                            chat_recyclerView.scrollToPosition(adapter.getItemCount() - 1);

                        }
                    }
                });
        progress.setVisibility(View.GONE);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chatview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //if the app is closed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handT != null && run != null) handT.removeCallbacks(run);

        if (timeCheckMessageListener != null && pathMessages != null) {
            pathMessages.removeEventListener(timeCheckMessageListener);
        }

        if (listenerStateAttention != null && pathUserAttention != null)
            pathUserAttention.removeEventListener(listenerStateAttention);

    }

        //image uploading on the database and a new message is generated in
        //the conversation id of the user
    public void sendFile(final Uri selectedImageUri) {
        progress.setVisibility(View.VISIBLE);
        Log.e(TAG,"Entered in the send File function ");
        progress.setVisibility(View.VISIBLE);

        StorageReference photoRef = mChatStorageRef.child(selectedImageUri.getLastPathSegment());
        photoRef.putFile(selectedImageUri).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialogStatic.dissmis();
                StaticMethods.msgSnackbar(edit_message, e.getMessage());
                Log.d(TAG," image Not uploaded");

            }
        });
        photoRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // When the image has successfully uploaded, we get its download URL

                Log.e(TAG,"Uploaded image on the database");

                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Map<String, Object> newMessage = new HashMap<>();
                newMessage.put("message",null);
                newMessage.put("receive_therapist", mProviderId);
                newMessage.put("receive_support",Support_Id);
                newMessage.put("sender", fb_user.getUid());
                newMessage.put("uri", downloadUrl.toString());
                newMessage.put("timewrite", new Date().getTime());
                newMessage.put("attachment", true);
                String image_url = String.valueOf(fb_user.getPhotoUrl() != null ? fb_user.getPhotoUrl().toString() : null);
                newMessage.put("image_url",image_url);
                newMessage.put("conversation_id",mConversationId);

               // String username = fb_user.getDisplayName() != null ? fb_user.getDisplayName() : "Anonymous";
                newMessage.put("name", Patient_name);




                String unformatted = selectedImageUri.getLastPathSegment();
                if (unformatted.contains("/")) {
                    unformatted = unformatted.replace("/", "_");
                    unformatted = unformatted.replaceAll(":", "semi");
                }
                newMessage.put("fileName", unformatted);
                newMessage.put("uri", downloadUrl.toString());
                StorageMetadata metadata = taskSnapshot.getMetadata();
                String contentType = metadata.getContentType();
                if (contentType.contains("image")) {
                    newMessage.put("attachmentType", 0);

                }


                newMessage.put("timewrite", new Date().getTime());
                System.out.println(newMessage);
                pathMessages.push().setValue(newMessage);
                edit_message.setText("");
            }

        });



    }




    //if the image is greater than 1024 width then this method is called and the image is compressed using this method
    private File createMediaFile(String ext, String dir_name, boolean compressed) throws IOException {
        progress.setVisibility(View.VISIBLE);
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "BEMO_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory(), dir_name);

        if (!storageDir.isDirectory()) {
            storageDir.mkdir();
            System.out.println(storageDir.getAbsolutePath());
        }
        if (compressed) {
            imageFileName += "compressed";
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ext,         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mFilePath = image.getAbsolutePath();
        progress.setVisibility(View.GONE);
        return image;

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_Q:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendAttachment();
                } else {
                    // Permission Denied
                    Toast.makeText(ChatView.this, "Sorry, You can't send attachments", Toast.LENGTH_SHORT)
                            .show();
                }
                break;


            case REQUEST_Q3:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   // dispatchVideoIntent();
                } else {
                    // Permission Denied
                    Toast.makeText(ChatView.this, "Sorry, You can't record video", Toast.LENGTH_SHORT)
                            .show();
                }
                break;

            case REQUEST_Q4:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //dialogStatic.show();
                    if (pathFirebase != null) {
                        pathFirebase.child("conversation").child(mConversationId)
                                .addValueEventListener(timeCheckMessageListener);
                        pathFirebase.child("conversation").child(mConversationId)
                                .child("messages").addChildEventListener(messageListener);
                    }

                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



        //if clicked on the + button to select the picture the this function will work
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            // final Uri selectedImageUri = data.getData();
            // sendFile(selectedImageUri);

            Log.e(TAG, "Entered in photo Selected ");
            final Uri selectedMedia = data.getData();
            File someFile = null;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(FileUtils.getFile(this, selectedMedia)));
                if (bitmap.getWidth() >= 1024) {
                    someFile = createMediaFile(".jpeg", "bemo_images", true);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, new FileOutputStream(someFile));
                } else {
                    sendFile(selectedMedia);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            if (someFile != null) {
                Log.e(TAG, "photo Selected ");
                Log.e(TAG, "Going to send File Function");
                sendFile(selectedMedia);

            }


        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgbtn_sendchat:
                sendMessage();
                break;

           case R.id.send_image_message:
                int permissionCheck = ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatView.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_Q
                    );
                    return;
                }

                sendAttachment();
                break;


        }
    }




    public void sendAttachment() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/* video/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
    }





    //when user clicks on send button then message is being send
    private void sendMessage() {
        String sendMessage = edit_message.getText().toString().trim();


       if (!sendMessage.isEmpty() && fb_user.getUid() != null) {


            Map<String, Object> newMessage = new HashMap<>();
            newMessage.put("message", sendMessage);
            newMessage.put("receive_therapist", mProviderId);
            newMessage.put("receive_support",Support_Id);
            newMessage.put("sender", fb_user.getUid());
            newMessage.put("conversation_id",mConversationId);


          //  newMessage.put("name", fb_user.getDisplayName() != null ? fb_user.getDisplayName() : "Anonymous");
            newMessage.put("name",Patient_name);
            newMessage.put("timewrite", new Date().getTime());
            newMessage.put("attachmentType", 3);

            String image_url = String.valueOf(fb_user.getPhotoUrl() != null ? fb_user.getPhotoUrl().toString() : null);
            newMessage.put("image_url",image_url);
            pathMessages.push().setValue(newMessage);
            edit_message.setText("");
            InputMethodManager mgr = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(edit_message.getWindowToken(), 0);
        } else {
            StaticMethods.msgSnackbar(edit_message, "Enter the message");
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ("peru".equals("chile")) {
                return true;
            } else {
                StaticMethods.msgSnackbar(imgbtn_sendchat, "Sorry, currently session online ");
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_close_session:
                // sign out logic

                SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("basic_app_info", MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putString("User", "");
                prefsEditor.apply();
                signOut();

                break;

            case android.R.id.home:
                if (bemoUser != null) {
                    if (back) {
                        startActivity(new Intent(this, UserDashB.class));
                    }
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }





    private void signOut() {
        mAuth.addAuthStateListener(authListener);
        mAuth.signOut();
        Intent listPatients = new Intent(ChatView.this,LoginUserAct.class);
        startActivity(listPatients);
    }




    //checks if the free trial is over then blocks the chat
    private ValueEventListener timeCheckMessageListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            long now = new Date().getTime();
            if (now >= mTimeOut)
            {
                // Toast.makeText(ChatView.this, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new
                //  Date(mTimeOut)), Toast.LENGTH_SHORT).show();
                if (expiredLayout != null) {
                    expiredLayout.setVisibility(View.INVISIBLE);
                    //  attachMenu.setVisibility(View.VISIBLE);
                    Button payButton = (Button) findViewById(R.id.pay_btn_);
                    payButton.setEnabled(true);
                    imgbtn_sendchat.setEnabled(true);
                    payButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG, "onClick: uid : " + mAuth.getCurrentUser().getUid());

                            pathFirebase.child("episodes")
                                    .child(bemoUser.getEpisodes().get(0)).child("paid").setValue(false);
                            startActivity(new Intent(ChatView.this, PGActivity.class));
                        }
                    });
                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    //Listener to message events whenever any new messages comes this will listen and accordingly action will be taken
    private ChildEventListener messageListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            MessageChat newMessage = dataSnapshot.getValue(MessageChat.class);
            String id = fb_user.getUid();
            //String support_id = fb_user.

            //if the new message is sent by this user id who has looged in the app
            //then the message is sent by sender
            if (newMessage.getSender().equals(id)) {

                newMessage.setSenderOrRecipient(SENDER);

            }
            else if (newMessage.getSender().equals(Support_Id))
            {
                newMessage.setSenderOrRecipient(RECEIVE);

            }

            //if id does not matches then the message is recieved through
            //this only we are differentiating in  the chat and it is helping us to differentiate the chat
            //of the sender and the receiver
            else {
                newMessage.setSenderOrRecipient
                        (RECEIVE);
            }


            //checking if the user has sent an image message or not
            if (newMessage.getUri() != null) {
                Log.d("tejasvi", " " +
                        newMessage.getFileName());
                if (newMessage.getFileName() != null) {

                    //getting the folder of bemo_images in the SD card to store the images in the compressed form
                    File storageImageDir = new File
                            (Environment.getExternalStorageDirectory(), "bemo_images");
                    File storageVideoDir = new File
                            (Environment.getExternalStorageDirectory(), "bemo_videos");
                    if (!storageImageDir.isDirectory()) {
                        storageImageDir.mkdir();
                    }

                    if (!storageVideoDir.isDirectory()) {
                        storageVideoDir.mkdir();
                    }

                    if (newMessage.getAttachmentType() ==
                            0) {
                        selectFile = new File
                                (storageImageDir, newMessage.getFileName());
                    }




                    if (selectFile.exists() && !
                            selectFile.isDirectory() && selectFile.getTotalSpace() >= 0) {
                        // file is an image
                        Log.d(TAG, "onChildAdded: " +
                                "came here");
                        if (newMessage.getAttachmentType
                                () == 0) {
                            BitmapFactory.Options options
                                    = new BitmapFactory.Options();

                            Log.e(TAG,selectFile.toString());

                            try {
                                Bitmap imageBitmap =
                                        BitmapFactory.decodeFile(selectFile.getAbsolutePath());
                                if (imageBitmap.getWidth() <=
                                        512) {
                                    options.inSampleSize = 2;
                                } else if
                                        (imageBitmap.getWidth() < 512) {
                                    options.inSampleSize = 1;
                                } else if (imageBitmap.getWidth
                                        () >= 1024) {
                                    options.inSampleSize = 8;
                                }
                                Bitmap mBitmapInsurance =
                                        BitmapFactory.decodeFile(selectFile.getAbsolutePath(), options);
                                newMessage.setLocalBitmap
                                        (mBitmapInsurance);
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();

                            }
                        }


                        newMessage.setLocal(true);
                        adapter.refillAdapter
                                (newMessage);

                        chat_recyclerView.scrollToPosition(adapter.getItemCount() - 1);

                    }

                    //if image is not store locally then download it from the internet
                    else {
                        downloadRemoteAttachments
                                (newMessage);

                    }
                }
            } else {
                adapter.refillAdapter(newMessage);
                chat_recyclerView.scrollToPosition
                        (adapter.getItemCount() - 1);
            }
            dialogStatic.dissmis();
        }


        @Override
        public void onChildChanged(DataSnapshot
                                           dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot
                                           dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot
                                         dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError
                                        databaseError) {

        }
    };

    //on click listener set on the adapter so that when the user clicks on the
    //image it should open in the  full image view.
    private AdapterMessage.OnItemClickListener mItemClick = new AdapterMessage.OnItemClickListener() {
        @Override
        public void onItemClick(MessageChat item) {




            if (item.getAttachmentType() == 0) {

                if (item.getUri() != null) {

                    Log.e(TAG, "image clicked");
                    Log.e(TAG,item.getUri().toString());
                    startActivity(new Intent(ChatView.this, ShowMediaActivity.class)
                            .putExtra("uri", item.getUri()));
                }
            }

        }
    };

}
