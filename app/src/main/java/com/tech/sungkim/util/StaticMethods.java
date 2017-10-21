package com.tech.sungkim.util;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tech.sungkim.bemo.R;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StaticMethods {


    public static  String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MM dd"); //new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return format.format(date);
    }

    public static void designRegisterLogin(TextView txt1, TextView txt2, View view1, View view2,
                                           CardView login , CardView register, int type, Context context){
        txt1.setTextColor(type==1 ? ContextCompat.getColor(context, R.color.colorActiveLogin)
                :  ContextCompat.getColor(context, R.color.colorInactiveLogin));
        txt2.setTextColor(type==2 ? ContextCompat.getColor(context, R.color.colorActiveLogin)
                :  ContextCompat.getColor(context, R.color.colorInactiveLogin));
        view1.setVisibility(type==1 ? View.VISIBLE : View.INVISIBLE);
        view2.setVisibility(type==2 ? View.VISIBLE : View.INVISIBLE);

        login.setVisibility(type ==1 ? View.VISIBLE : View.GONE);
        register.setVisibility(type == 2 ? View.VISIBLE : View.GONE);
    }

    public static int viewCheckbook(CompoundButton compoundButton, CheckBox fami,
                                     CheckBox rela, CheckBox grief, CheckBox work){

        int typeChat = 100;

       /* switch (compoundButton.getId()){
            case R.id.checkFamily: typeChat = 0; break;
            case R.id.checkRelationship: typeChat = 1; break;
            case R.id.checkGrief: typeChat = 2; break;
            case R.id.checkWorkstress: typeChat = 3; break;
            default: typeChat = 100; break;
        }*/

        if (compoundButton==fami) typeChat = 0;
        if (compoundButton==rela) typeChat = 1;
        if (compoundButton==grief) typeChat = 2;
        if (compoundButton==work) typeChat = 3;

        fami.setChecked(typeChat == 0);
        rela.setChecked(typeChat == 1);
        grief.setChecked(typeChat == 2);
        work.setChecked(typeChat == 3);
        return typeChat;
    }

    public static Boolean validateConnect(View view, String comment){
        boolean var = false;
        if (comment.length()==0){
            msgSnackbar(view, "Enter some description");
            var = false;
        } else if (comment.length()<5 && comment.length()>0){
            msgSnackbar(view, "Your Description shoud have at least 6 characters");
            var = false;
        }else var =  true;
       return var;
    }

    public static Boolean validateTypeChat(View view, int type){
        Boolean var;
        if(type==100){
            var = false;
            msgSnackbar(view,"Select a category");
        }else var = true;
        return var;
    }

    public static void msgSnackbar(View v, String msg){
        Snackbar snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.RED);
        snackbar.show();
    }

    public static void msgSnackbarColor(View v, String msg, int color){
        Snackbar snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(color);
        snackbar.show();
    }

    public static boolean isValidEmail(View view, String email) {
        boolean isGoodEmail = (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            StaticMethods.msgSnackbar(view,"Please enter a valid email address");
            return false;
        }
        return true;
    }

    public static boolean isValidPassword(View view, String password) {
        if (TextUtils.isEmpty(password)){
            StaticMethods.msgSnackbar(view,"Enter password!");
            return false ;
        }
        else if (password.length() < 6) {
            StaticMethods.msgSnackbar(view,"Password should be longer than 6 characters");
            return false;
        }
        return true;
    }

    public static void showErrorMessageToUser(String errorMessage, Context context){

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setMessage(errorMessage)
                //.setTitle("")
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public static boolean isValidDate(String date, View view){
        if(date!=null && date.length()>3) return true;
        else {
            StaticMethods.msgSnackbar(view,"Enter Birth date!");
            return false;
        }
    }

    public static boolean isValidFullName(String name, View view){
        if(name!=null && name.length()>3) return true;
        else {
            StaticMethods.msgSnackbar(view,"Name can't be less than 3 characters!");
            return false;
        }
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private static int getNrosMonths(long days){
        return Integer.parseInt(String.valueOf(days/30)) ;
    }

    public static String getLastTime(String timeLast){//Conversion de timestamp a string
        String fecha;
        Long current =  new Date().getTime();
        if (current > Long.valueOf(timeLast)){
            long duration  = current - Long.valueOf(timeLast);
            //long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
            long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
            long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
            long diffDays =  TimeUnit.MILLISECONDS.toDays(duration);

            if (diffDays==0){
                if (diffInHours>0){
                    fecha = diffInHours + "h ago";
                }else {
                    fecha = diffInMinutes + "m ago";
                }
            }
            else  if (diffDays >0 && diffDays<30){
                fecha = diffDays + "d ago";
            }else {
                fecha = getNrosMonths(diffDays) + "mon ago";
            }
        }else return "-";
        return fecha;
    }

    public static String averageRating(List<String> rating){//algunos usuarios no calificaran => agregar solo v.!=null en la lista
        int sum_rating = 0;
        int average;
        if (rating!=null){
            for (String conv: rating){
                sum_rating = sum_rating + Integer.parseInt(conv);
            }
            average =  sum_rating/(rating.size());
        }else average = 0;
        return String.valueOf(""+average);
    }

    public static void showTextWarning(TextView txt, Context context){
        if (isInternetAvailable(context)){
            txt.setVisibility(View.GONE);
        }else {
            txt.setText(R.string.txt_error_internet);
            txt.setVisibility(View.VISIBLE);
        }
    }


    public static void showTimeSession(TextView txt_timeEstimated, int timeEstimatedSeg ){
        txt_timeEstimated.setVisibility(View.VISIBLE);
        int s; int h; int m=0;//minutes
        s=timeEstimatedSeg;

        String seg = String.valueOf(s);
        String min = "0" + String.valueOf(m);
        if (s<10) {
            seg = "0" + String.valueOf(s);
        }
        if(s >= 60){
            m=s/60;
            s = s- m*60;
            seg = String.valueOf(s);
            min = String.valueOf(m);
            if(s<10){ seg = "0" + s; }
            if (m<10) { min = "0" + m;}
        } if(m>=60){
            h=m/60;
            m = m - h*60;
            min = String.valueOf(m);
            if (m<10) { min = "0" + m;}
        }
        txt_timeEstimated.setText(min + ":" + seg);
    }


    public static void changeColorView(LinearLayout layout, ImageView img, TextView txt,  int select, int tipo, Context context){

        layout.setBackgroundColor(select==1 ? ContextCompat.getColor(context, R.color.bemo_light_light4) : ContextCompat.getColor(context, R.color.white));
        txt.setTextColor(select == 1  ? ContextCompat.getColor(context, R.color.bemo_light_2) : ContextCompat.getColor(context, R.color.gray));

        if (tipo ==1){
            img.setImageDrawable(select==1 ? ContextCompat.getDrawable(context, R.mipmap.relation) :  ContextCompat.getDrawable(context, R.mipmap.relation_g));
        }else if (tipo == 2){
            img.setImageDrawable(select==1 ? ContextCompat.getDrawable(context, R.mipmap.grief) :  ContextCompat.getDrawable(context, R.mipmap.grief_g));
        }else if (tipo == 3){
            img.setImageDrawable(select==1 ? ContextCompat.getDrawable(context, R.mipmap.stress) :  ContextCompat.getDrawable(context, R.mipmap.stress_g));
        }else {
            txt.setTextColor(select == 1  ? ContextCompat.getColor(context, R.color.bemo_light) : ContextCompat.getColor(context, R.color.gray));
        }



    }


}
