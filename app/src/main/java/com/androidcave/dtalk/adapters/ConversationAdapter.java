package com.androidcave.dtalk.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.androidcave.dtalk.R;
import com.androidcave.dtalk.models.Message;
import com.androidcave.dtalk.utils.CreateAnimationView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private final MessageListItemClickListner mOnClickListner;
    private List<Message> messages;
    private Context context;
    private int LayoutIdForListItem;

    public interface MessageListItemClickListner{
        void onMessageItemClick(Message messageClicked, int position);
    }

    public ConversationAdapter(List<Message> messages, Context context,MessageListItemClickListner listItemClickListner) {
        this.messages = messages;
        this.context = context;
        this.mOnClickListner=listItemClickListner;
    }

    @Override
    public int getItemViewType(int position) {


        Message message=messages.get(position);
        String currentUserUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (message.getSenderUID().equals(currentUserUid)){
            return    LayoutIdForListItem= R.layout.chat_item_right;
        } else if (!message.getSenderUID().equals(currentUserUid)){
            return   LayoutIdForListItem=R.layout.chat_item_left;
        }
        return LayoutIdForListItem=R.layout.chat_item_left;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context=viewGroup.getContext();
        // int LayoutIdForListItem=R.layout.item_message;
        LayoutInflater inflater=LayoutInflater.from(context);
        boolean shouldAttachToParentImediately=false;

        View view=inflater.inflate(LayoutIdForListItem,viewGroup,shouldAttachToParentImediately);

        ConversationViewHolder viewHolder=new ConversationViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder conversationViewHolder, int i) {
    Message message=messages.get(i);


    conversationViewHolder.bind(message);
        CreateAnimationView.createAnimation(conversationViewHolder.itemView);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ConversationViewHolder extends RecyclerView.ViewHolder{
        TextView tvTxtMessage,tvTimeDateSent;
        LottieAnimationView lavMsgStatus;
        ImageView imgMessageSeen;
        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTxtMessage=itemView.findViewById(R.id.txtMessage);
            tvTimeDateSent=itemView.findViewById(R.id.dateTimeSent);
            lavMsgStatus=itemView.findViewById(R.id.msgStatus);
            imgMessageSeen=itemView.findViewById(R.id.imgMsgSeen);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnClickListner.onMessageItemClick(messages.get(getAdapterPosition()),getAdapterPosition());
                    return true;
                }
            });
        }

        void bind(Message message){
            tvTxtMessage.setText(message.getTxtMessage());
           // TODO(1) calender to time string change require here...
            tvTimeDateSent.setText(getTimeFromCalender(message.getTimeDateSent()));

           // imgMessageSeen.setVisibility(View.VISIBLE);

//            if (message.getMsgStatus().equals("sending")){
//            lavMsgStatus.setAnimation("trail_loading.json");
//            }
//            else if (message.getMsgStatus().equals("sent")){
//                lavMsgStatus.setVisibility(View.GONE);
//                imgMessageSeen.setVisibility(View.VISIBLE);
//               // lavMsgStatus.setAnimation("green_check.json");
//            }
//            else if (message.getMsgStatus().equals("seen")){
//                lavMsgStatus.setVisibility(View.GONE);
//                imgMessageSeen.setVisibility(View.VISIBLE);
//                //lavMsgStatus.setAnimation("checked_done.json");
//            }





        }


      private String getTimeFromCalender(String strCal){

          SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

          SimpleDateFormat showDateFormat=new SimpleDateFormat("K:mm a");

          String showTime=null;

          try {
              Date date=dateFormat.parse(strCal);
             showTime=showDateFormat.format(date);
          } catch (ParseException e) {
              e.printStackTrace();
          }

          return showTime;
      }
    }
}
