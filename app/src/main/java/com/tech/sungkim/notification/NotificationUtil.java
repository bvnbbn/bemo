package com.tech.sungkim.notification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import com.tech.sungkim.bemo.R;
import com.tech.sungkim.util.Config;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by vikas on 12/6/17.
 */

public class NotificationUtil
{


    private static String TAG = NotificationUtil.class.getSimpleName();
    private Context context;


    public NotificationUtil(Context context)
    {
        this.context=context;
    }


    public void showNotificationMessage(String title,String message,String name,Intent intent)
    {
        showNotificationMessage(title,message,name,intent,null);
    }

    public void showNotificationMessage(String title,String message,String name,Intent intent,String imageUrl)
    {
        //check for empty push message
        if(TextUtils.isEmpty(message))
            return;

        //notification icon
        final int icon= R.mipmap.bem_launcher;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent resultPendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);


        if(!TextUtils.isEmpty(imageUrl))
        {
            if(imageUrl != null && imageUrl.length() >4 && Patterns.WEB_URL.matcher(imageUrl).matches())
            {
                Bitmap bitmap = getBitmapFromURL(imageUrl);

                if(bitmap != null)
                {
                    showImageNotification(bitmap,mBuilder,icon,title,message,resultPendingIntent);

                }
                else
                {
                    showNotificationWithoutImage(mBuilder,icon,title,message,name,resultPendingIntent);

                }

            }
        }
        else
        {
            showNotificationWithoutImage(mBuilder,icon,title,message,name,resultPendingIntent);

        }
    }

    private void showNotificationWithoutImage(NotificationCompat.Builder mBuilder, int icon, String title, String message,String name,PendingIntent resultPendingIntent)
    {
       // NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        //inboxStyle.addLine(message);
       // inboxStyle.addLine(title);

        Log.d(TAG,title);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification;
        if(message.equals("sent a photo"))
        {

        }


        else {
            notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    //.setStyle(inboxStyle)
                    .setSmallIcon(R.mipmap.bemo_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                    .setContentText(message)
                    .setContentIntent(resultPendingIntent)
                    .setSound(defaultSoundUri)
                    .build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Config.NOTIFICATION_ID_BIG_IMAGE, notification);
        }
    }


    private void showImageNotification( Bitmap bitmap,NotificationCompat.Builder mBuilder, int icon, String title, String message, PendingIntent resultPendingIntent)
    {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);

        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setStyle(bigPictureStyle)
               // .setWhen(getTimeInMilliSec(timeStamp))
                .setSmallIcon(R.mipmap.bem_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),icon))
                .setContentText(message)
                .build();
        NotificationManager notificationManager =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Config.NOTIFICATION_ID_BIG_IMAGE, notification);


    }

    /*
    Downloading push notification image

     */

    public Bitmap getBitmapFromURL(String imageURL)
    {
        try
        {
            URL url = new URL(imageURL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            Bitmap image = BitmapFactory.decodeStream(inputStream);
            return image;


        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

    }

    public static boolean isAppInBackground(Context context)
    {
        boolean isInBackground=true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH)
        {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = am.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfos)
            {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                {
                    for(String activeProcess:processInfo.pkgList)
                    {
                        if(activeProcess.equals(context.getPackageName()))
                        {
                            isInBackground = false;
                        }
                    }
                }
            }

        }
        else
        {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentName = taskInfo.get(0).topActivity;
            if(componentName.getPackageName().equals(context.getPackageName()))
            {
                isInBackground = false;
            }
        }

        return isInBackground;

    }

    public static void clearNotifications(Context context)
    {
        NotificationManager notificationManager =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
    public static long getTimeInMilliSec(String timeStamp)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            Date date = format.parse(timeStamp);
            return date.getTime();

        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return 0;
    }
}
