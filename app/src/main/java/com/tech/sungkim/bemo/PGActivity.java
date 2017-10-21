package com.tech.sungkim.bemo;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instamojo.android.Instamojo;
import com.instamojo.android.activities.PaymentDetailsActivity;
import com.instamojo.android.callbacks.OrderRequestCallBack;
import com.instamojo.android.helpers.Constants;
import com.instamojo.android.models.Errors;
import com.instamojo.android.models.Order;
import com.instamojo.android.network.Request;
import com.tech.sungkim.model.User;
import com.tech.sungkim.util.DialogStatic;
import com.tech.sungkim.util.PathFirebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PGActivity extends AppCompatActivity {

    private static final String TAG = "bemo";
    private Button payButton;
    private String accessToken;
    private final int REQUEST_Q = 500;
    private DialogStatic mDialogStatic;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pg);
        accessToken = "AAXWrFKsBLRt7p9HIJyvPUPoC38PQy";
        //Instamojo.setBaseUrl("https://api.instamojo.com/");
        payButton = (Button) findViewById(R.id.pay_btn);
        mDialogStatic = new DialogStatic(this, "", "Loading");
        payButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //fetchToken();
                        int permissionCheck = ContextCompat.checkSelfPermission(PGActivity.this,
                                Manifest.permission.READ_PHONE_STATE);
                        int permissionCheck2 = ContextCompat.checkSelfPermission(PGActivity.this,
                                Manifest.permission.READ_SMS);
                        int permissionCheck3 = ContextCompat.checkSelfPermission(PGActivity.this,
                                Manifest.permission.RECEIVE_SMS);
                        if(permissionCheck != PackageManager.PERMISSION_GRANTED &&
                                permissionCheck2 != PackageManager.PERMISSION_GRANTED &&
                                permissionCheck3 != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(PGActivity.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE,
                                            android.Manifest.permission.READ_SMS,
                                            android.Manifest.permission.RECEIVE_SMS}, REQUEST_Q
                            );
                            return;
                        }
                        mDialogStatic.show();
                        fetchToken();

                    }
                });
    }



    private HttpUrl.Builder getHttpURLBuilder() {
        return new HttpUrl.Builder()
                .scheme("https")
                .host("sample-sdk-server.instamojo.com");
    }


    private void fetchToken(){
        OkHttpClient client = new OkHttpClient();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("api.instamojo.com")
                .addPathSegment("oauth2")
                .addPathSegment("token")
                .addPathSegment("")
                .build();
        Log.d(TAG, "fetchToken: " + "url :" + " " + url);

        FormBody body = new FormBody.Builder()
                .add("client_id","cfjc75XLpYj7Noa28WwrX6H2GgiWYoTFbOs0CjWW")
                .add("client_secret","FnZpSlFWZWR9EA1VYXDAdPkRDZSAMTVk96qwAvsAVOq8Z6WOHH0qbdHdgdp2kB9i6UgioONuU6mSuppYDZJzkco4eCDykD87CSGkttW8RG5ly1eTmqI23lveucVCyaHk")
                .add("grant_type","client_credentials")
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString;
                String errorMessage = null;
                String transactionID = null;
                responseString = response.body().string();
                Log.d(TAG, "onResponse: " + " " + responseString);
                response.body().close();
                try {
                    JSONObject responseObject = new JSONObject(responseString);
                    if (responseObject.has("error")) {
                        errorMessage = responseObject.getString("error");
                    } else {
                        accessToken = responseObject.getString("access_token");
                        //transactionID = responseObject.getString("transaction_id");
                        //transactionID = "bocan012345tekv";
                    }
                } catch (JSONException e) {

                    errorMessage = e.getMessage();
                }
                Log.d(TAG, "onResponse: " + "transaction-id: "  + transactionID);
                Log.d(TAG, "onResponse: " + "error "  + errorMessage);
                final String finalErrorMessage = errorMessage;
                final String finalTransactionID = transactionID;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalErrorMessage != null) {
                            return;
                        }
                        fetchTokenAndTransactionID();
                        //createOrder(accessToken, finalTransactionID);
                    }
                });
            }
        });
    }

    private void fetchTokenAndTransactionID() {
        OkHttpClient client = new OkHttpClient();
        HttpUrl url = getHttpURLBuilder()
                .addPathSegment("create")
                .build();


        RequestBody body = new FormBody.Builder()
                .add("env", "production")
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString;
                String errorMessage = null;
                String transactionID = null;
                responseString = response.body().string();
                response.body().close();
                try {
                    JSONObject responseObject = new JSONObject(responseString);
                    if (responseObject.has("error")) {
                        errorMessage = responseObject.getString("error");
                    } else {
                        //accessToken = responseObject.getString("access_token");
                        transactionID = responseObject.getString("transaction_id");
                    }
                } catch (JSONException e) {
                    errorMessage = "Failed to fetch Order tokens";
                }
                Log.d(TAG, "onResponse: " + "transaction-id: "  + transactionID);
                Log.d(TAG, "onResponse: " + "error "  + errorMessage);
                final String finalErrorMessage = errorMessage;
                final String finalTransactionID = transactionID;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalErrorMessage != null) {
                            return;
                        }
                        createOrder(accessToken, finalTransactionID);
                    }
                });

            }
        });

    }

    public void createOrder(String accessToken, String transactionID){
        Log.d(TAG, "createOrder: " + accessToken + " " + transactionID);
        Order order = new Order(accessToken, transactionID, "Prithiviraj", "prith_94@live.com", "9952107077", "10", "Consult");


        if (!order.isValid()){
            //oops order validation failed. Pinpoint the issue(s).

            if (!order.isValidName()){
                Log.e("App", "Buyer name is invalid");
            }

            if (!order.isValidEmail()){
                Log.e("App", "Buyer email is invalid");
            }

            if (!order.isValidPhone()){
                Log.e("App", "Buyer phone is invalid");
            }

            if (!order.isValidAmount()){
                Log.e("App", "Amount is invalid");
            }

            if (!order.isValidDescription()){
                Log.e("App", "description is invalid");
            }

            if (!order.isValidTransactionID()){
                Log.e(TAG, "Transaction ID is invalid");
            }

            if (!order.isValidRedirectURL()){
                Log.e("App", "Redirection URL is invalid");
            }



            return;
        }

        Request request = new Request(order, new OrderRequestCallBack() {
            @Override
            public void onFinish(Order order, Exception error) {
                //dismiss the dialog if showed

                if (error != null) {
                    if (error instanceof Errors.ConnectionError) {
                        Log.e("App", "No internet connection");
                    } else if (error instanceof Errors.ServerError) {
                        Log.e("App", "Server Error. Try again");
                    } else if (error instanceof Errors.AuthenticationError){
                        Log.e("App", "Access token is invalid or expired");
                    } else if (error instanceof Errors.ValidationError){
                        // Cast object to validation to pinpoint the issue
                        Errors.ValidationError validationError = (Errors.ValidationError) error;
                        if (!validationError.isValidTransactionID()) {
                            Log.e("App", "Transaction ID is not Unique");
                            return;
                        }
                        if (!validationError.isValidRedirectURL()) {
                            Log.e("App", "Redirect url is invalid");
                            return;
                        }


                        if (!validationError.isValidWebhook()) {

                            return;
                        }

                        if (!validationError.isValidPhone()) {
                            Log.e("App", "Buyer's Phone Number is invalid/empty");
                            return;
                        }
                        if (!validationError.isValidEmail()) {
                            Log.e("App", "Buyer's Email is invalid/empty");
                            return;
                        }
                        if (!validationError.isValidAmount()) {
                            Log.e("App", "Amount is either less than Rs.9 or has more than two decimal places");
                            return;
                        }
                        if (!validationError.isValidName()) {
                            Log.e("App", "Buyer's Name is required");
                            return;
                        }
                    } else {
                        Log.e("App", error.getMessage());
                    }
                    return;
                }

                startPreCreatedUI(order);
            }
        });
        request.execute();
    }


    private void startPreCreatedUI(Order order){
        //Using Pre created UI
        mDialogStatic.dissmis();
        Intent intent = new Intent(getBaseContext(), PaymentDetailsActivity.class);
        intent.putExtra(Constants.ORDER, order);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE && data != null) {
            String orderID = data.getStringExtra(Constants.ORDER_ID);
            String transactionID = data.getStringExtra(Constants.TRANSACTION_ID);
            String paymentID = data.getStringExtra(Constants.PAYMENT_ID);

            // Check transactionID, orderID, and orderID for null before using them to check the Payment status.
            if (transactionID != null || paymentID != null) {
                Log.d(TAG, "onActivityResult: " + "transaction - id " + transactionID);
                Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
                mDialogStatic = new DialogStatic(this, " ", "Restoring session");
                mDialogStatic.show();
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                ref.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                Log.d(TAG, "onDataChange: episodes : " + user.getEpisodes());
                                String episodeId = user.getEpisodes().get(0);
                                ref.child("episodes").child(episodeId).child("pay").setValue(1);
                                ref.child("episodes").child(episodeId).child("paid").setValue(true);
                                ref.child("episodes").child(episodeId).child("conversation_id")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    String convoId = dataSnapshot.getValue().toString();
                                                    mDialogStatic.dissmis();
                                                    startActivity(new Intent(PGActivity.this, ChatView.class)
                                                                .putExtra("conversation", convoId)
                                                                .putExtra("session_expired", false));
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



            } else {
                Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_Q:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    fetchTokenAndTransactionID();
                } else {
                    // Permission Denied
                }
                break;
        }
    }
}
