package com.tech.sungkim.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Transformation;
import com.tech.sungkim.bemo.ChatView;
import com.tech.sungkim.bemo.MainActivity;
import com.tech.sungkim.bemo.R;
import com.tech.sungkim.model.MessageChat;
import com.tech.sungkim.util.Blur;
import com.tech.sungkim.util.DialogStatic;

import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.blurry.Blurry;

import static com.razorpay.Segment.context;
import static com.tech.sungkim.util.ImageUtils.retriveVideoFrameFromVideo;


public class AdapterMessage extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private List<MessageChat> listMessage;
    private static final int SENDER = 0;
    private static final int RECIPIENT = 1;
    private static final int TYPE_IMAGE = 0;
    private String TAG = AdapterMessage.class.getName();
    private OnItemClickListener mOnClickListener;
    private static final File storageImageDir = new File(Environment.getExternalStorageDirectory(),"bemo_images");
    private static final File storageVideoDir = new File(Environment.getExternalStorageDirectory(),"bemo_videos");
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");


    public interface OnItemClickListener
    {

        void onItemClick(MessageChat item);

    }

    public AdapterMessage(List<MessageChat> listOfFireChats, OnItemClickListener listener)
    {
        listMessage=listOfFireChats;
        mOnClickListener = listener;

    }



    @Override
    public int getItemViewType(int position)
    {
        if(listMessage.get(position).getSenderOrRecipient()==SENDER){
            return SENDER;
        }
        else{
            return RECIPIENT;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case SENDER:
                View viewSender = inflater.inflate(R.layout.row_sendchat, viewGroup, false);
                viewHolder= new ViewHolderSender(viewSender);
                break;
            case RECIPIENT:
                View viewRecipient = inflater.inflate(R.layout.row_receivechat, viewGroup, false);
                viewHolder=new ViewHolderReceive(viewRecipient);
                break;

            default:
                View viewSenderDefault = inflater.inflate(R.layout.row_sendchat, viewGroup, false);
                viewHolder= new ViewHolderSender(viewSenderDefault);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {

        switch (viewHolder.getItemViewType()){
            case SENDER:
                ViewHolderSender viewHolderSender=(ViewHolderSender)viewHolder;
                senderView(viewHolderSender,position,mOnClickListener);
                break;
            case RECIPIENT:
                ViewHolderReceive viewHolderReceive = (ViewHolderReceive)viewHolder;
                recipientView(viewHolderReceive,position,mOnClickListener);
                break;
        }
    }

    private void senderView(final ViewHolderSender viewHolder, int position,OnItemClickListener listener) {
        MessageChat senderMessage = listMessage.get(position);
        viewHolder.bind(senderMessage, listener);

        if (senderMessage.getUri() != null) {

            Log.d(TAG,"Sending Image Message");


            String text = sdf.format(senderMessage.getTimewrite()).concat(" ").concat(senderMessage.getName());
            viewHolder.image_text.setVisibility(View.VISIBLE);
            viewHolder.image_text.setText(text);


            viewHolder.getTxt_sendchat().setVisibility(View.GONE);
            viewHolder.getContainer().setVisibility(View.VISIBLE);
            String uri = senderMessage.getUri();
            if (senderMessage.getAttachmentType() == TYPE_IMAGE)
            {
                Log.d(TAG,"Image message found");
                viewHolder.getTxt_sendchat().setVisibility(View.GONE);
                viewHolder.getImg_send().setVisibility(View.VISIBLE);

                if(senderMessage.isLocal()){
                    Log.e(TAG,"Image Local");
                    viewHolder.getImg_send().setImageBitmap(senderMessage.getLocalBitmap());
                    viewHolder.getImg_send().setScaleType(ImageView.ScaleType.CENTER_CROP);
                    viewHolder.getImg_send().setEnabled(true);

                }else {
                    Log.e(TAG,"Image  not Local");
                    Glide.with(viewHolder.getImg_send().getContext())
                            .load(uri)
                            .centerCrop()
                            .into(viewHolder.getImg_send());
                    viewHolder.getImg_send().setEnabled(true);
                }
            }

        }
        else {
                Log.d(TAG," sending simple text message");
                String text = senderMessage.getMessage()
                        .concat("\n \n").concat(sdf.format(senderMessage.getTimewrite())
                        .concat("  ").concat(senderMessage.getName()));
                Log.d(TAG,text);
                viewHolder.txt_sendchat.setVisibility(View.VISIBLE);
                viewHolder.txt_sendchat.setText(text);
                viewHolder.getContainer().setVisibility(View.GONE);
        }
    }





    private void recipientView(ViewHolderReceive viewHolder, int position, OnItemClickListener listener) {
        MessageChat receiveMessage = listMessage.get(position);
        viewHolder.bind(receiveMessage, listener);

        if(receiveMessage.getImage_url()!=null)
        {
            Glide.with(viewHolder.img_recv.getContext())
                    .load(receiveMessage.getImage_url())
                    .into(viewHolder.img_recv);

        }

        Glide.with(viewHolder.img_recv.getContext())
                .load(receiveMessage.getImage_url())
                .placeholder(R.drawable.ic_account_circle_black_36dp);



        if(receiveMessage.getMessage()!= null)
        {
            Log.d(TAG," recieving simple text message");
            try
            {
                String text =  receiveMessage.getMessage()
                        .concat("\n \n").concat(sdf.format(receiveMessage.getTimewrite())
                                .concat("  ").concat(receiveMessage.getName()));
                Log.d(TAG,text);
                viewHolder.txt_receive.setVisibility(View.VISIBLE);
                viewHolder.txt_receive.setText(text);
                viewHolder.getContainer().setVisibility(View.GONE);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }


        if(receiveMessage.getUri() != null){
            Log.e(TAG,"image  message recieved");
            Log.e(TAG,receiveMessage.getUri().toString());
            viewHolder.txt_receive.setVisibility(View.GONE);

            viewHolder.getContainer().setVisibility(View.VISIBLE);



            String text = sdf.format(receiveMessage.getTimewrite()).concat(" ").concat(receiveMessage.getName());
            viewHolder.image_text.setVisibility(View.VISIBLE);
            viewHolder.image_text.setText(text);



                if(receiveMessage.isLocal()){
                    Log.e(TAG,"image Local");
                    Log.e(TAG,receiveMessage.getUri().toString());
                    viewHolder.recieve_image_message.setImageBitmap(receiveMessage.getLocalBitmap());
                    viewHolder.recieve_image_message.setScaleType(ImageView.ScaleType.CENTER_CROP);

                }
               else{
                    Log.e(TAG,"image not local");
                    Glide.with(viewHolder.recieve_image_message.getContext())
                            .load(receiveMessage.getUri())
                            .centerCrop()
                            .into(viewHolder.recieve_image_message);

                }
        }

    }

    @Override
    public int getItemCount() {
        return listMessage.size();
    }


    public void refillAdapter(MessageChat newFireChatMessage){
        listMessage.add(newFireChatMessage);
        notifyItemInserted(getItemCount()-1);
    }


    public static class ViewHolderSender extends RecyclerView.ViewHolder {

        public TextView txt_sendchat;
        public ImageView img_send;
        public View container;
        public TextView txt_Button_YES;
        private TextView txt_Button_NO;
        private TextView image_text;




        public View getContainer() {
            return container;
        }

        public ImageView getImg_send() {
            return img_send;
        }



        public void bind(final MessageChat item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }



        public ViewHolderSender(View itemView)
        {
            super(itemView);


            image_text =(TextView)itemView.findViewById(R.id.image_TextView);
            txt_Button_YES=(TextView) itemView.findViewById(R.id.YES_sendchat);
            txt_Button_NO=(TextView) itemView.findViewById(R.id.NO_sendchat);
            txt_sendchat=(TextView)itemView.findViewById(R.id.txt_sendchat);
            container = itemView.findViewById(R.id.img_cont2);
            img_send = (ImageView)itemView.findViewById(R.id.photoImageView_send);

        }

        public TextView getTxt_sendchat() {
            return txt_sendchat;
        }




    }


    public class ViewHolderReceive extends RecyclerView.ViewHolder
    {

        public TextView txt_receive;
        public ImageView img_recv;
        public View container;

        private ImageView recieve_image_message;
        private TextView image_text;

        public View getContainer() {
            return container;
        }

        public ViewHolderReceive(View itemView) {
            super(itemView);
            txt_receive=(TextView)itemView.findViewById(R.id.txt_receive);
            img_recv = (ImageView)itemView.findViewById(R.id.messengerImageView);
            recieve_image_message= (ImageView) itemView.findViewById(R.id.photoImageView_receive);
            container = itemView.findViewById(R.id.img_cont1);
            image_text = (TextView)itemView.findViewById(R.id.image_TextView);
        }





        public void bind(final MessageChat item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }


}