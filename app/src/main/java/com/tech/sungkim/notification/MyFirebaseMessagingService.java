package com.tech.sungkim.notification;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tech.sungkim.bemo.MainActivity;
import com.tech.sungkim.bemo.NotificationActivity;
import com.tech.sungkim.bemo.SplashActivity;
import com.tech.sungkim.util.Config;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by vikas on 12/6/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService
{

    private  static  final String TAG=MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtil notificationUtil;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        Log.e(TAG,"From:"+ remoteMessage.getFrom());

        if(remoteMessage ==null )
        {
            return;

        }
        //check if message contains a notification payload
        if(remoteMessage.getNotification()!=null)
        {
            String tittle = remoteMessage.getNotification().getTitle();
            Log.e(TAG,"Notification Body:" + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody(),tittle,null);
        }
        //checks to see if message contains a data payload
        if(remoteMessage.getData().size() > 0)
        {
            String imageUrl = remoteMessage.getData().get("image");
            String message =  remoteMessage.getData().get("message");
            String title = remoteMessage.getData().get("title");
            String name = remoteMessage.getData().get("username");
                Log.d(TAG,"app in background");
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(),SplashActivity.class);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              //  resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl))
                {
                    Log.e(TAG,"image not found");
                    showNotificationMessage(getApplicationContext(), title, message,name,resultIntent);
                }
                else
                {
                    // image is present, show notification with image
                    Log.e(TAG,"image found");
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, resultIntent, imageUrl);
                }
            }

        }


    private void handleNotification(String message,String title,String name)
    {

        if(!NotificationUtil.isAppInBackground(getApplicationContext()))
        {
            Intent resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
            showNotificationMessage(getApplicationContext(), title, message,null,resultIntent);

        }
        else
        {
            Intent resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
            showNotificationMessage(getApplicationContext(), title, message,null,resultIntent);

        }
    }

    private void showNotificationMessageWithBigImage(Context applicationContext, String title, String message, Intent resultIntent, String imageUrl)
    {
        notificationUtil = new NotificationUtil(applicationContext);
       // resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtil.showNotificationMessage(title, message,null,resultIntent,imageUrl);

    }

    private void showNotificationMessage(Context applicationContext, String title, String message,String name, Intent resultIntent)
    {
        notificationUtil = new NotificationUtil(applicationContext);
       // resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtil.showNotificationMessage(title, message,name,resultIntent);


    }

}
