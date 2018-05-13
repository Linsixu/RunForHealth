package magic.cn.health.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.v3.BmobUser;
import magic.cn.health.BR;
import magic.cn.health.R;
import magic.cn.health.databinding.ItemChatSentMessageBinding;
import magic.cn.health.inteface.OnRecyclerViewListener;
import magic.cn.health.utils.ImageLoaderOptions;
import magic.cn.health.utils.MyLog;

/**
 * @author 林思旭
 * @since 2018/3/20
 */

public class ChatAdapter extends RecyclerView.Adapter<BindingViewHolder>{

    private String TAG = "ChatAdapter";
    //文本
    private final int TYPE_RECEIVER_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    //图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;
    //位置
    private final int TYPE_SEND_LOCATION = 4;
    private final int TYPE_RECEIVER_LOCATION = 5;
    //语音
    private final int TYPE_SEND_VOICE =6;
    private final int TYPE_RECEIVER_VOICE = 7;
    //视频
    private final int TYPE_SEND_VIDEO =8;
    private final int TYPE_RECEIVER_VIDEO = 9;

    //同意添加好友成功后的样式
    private final int TYPE_AGREE = 10;
    /**
     * 显示时间间隔:10分钟
     */
    private final long TIME_INTERVAL = 10 * 60 * 1000;

    private List<BmobIMMessage> msgs = new ArrayList<>();

    private String currentUid="";

    private BmobIMConversation c;

    private LayoutInflater mLayoutInflater;

    private Context context;

    private ViewDataBinding binding = null;

    private OnRecyclerViewListener onRecyclerViewListener;

    public ChatAdapter(Context context,BmobIMConversation c) {
        this.context = context;
        mLayoutInflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        try {
            currentUid = BmobUser.getCurrentUser().getObjectId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.c =c;
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BindingViewHolder bindingViewHolder = null;
        if(viewType == TYPE_SEND_TXT){
            binding = DataBindingUtil.inflate(mLayoutInflater, R.layout.item_chat_sent_message,
                    parent,false);
            bindingViewHolder = new BindingViewHolder(binding);
            bindingViewHolder.setLayoutId(R.layout.item_chat_sent_message);
        }else if(viewType == TYPE_RECEIVER_TXT){
            binding = DataBindingUtil.inflate(mLayoutInflater,R.layout.item_chat_receive_message,
                    parent,false);
            bindingViewHolder = new BindingViewHolder(binding);
            bindingViewHolder.setLayoutId(R.layout.item_chat_receive_message);
        }else if(viewType == TYPE_SEND_IMAGE){
            binding = DataBindingUtil.inflate(mLayoutInflater,R.layout.item_chat_send_image,
                    parent,false);
            bindingViewHolder = new SendImageHolder(binding,c,onRecyclerViewListener);
            bindingViewHolder.setLayoutId(R.layout.item_chat_send_image);
        }else if(viewType == TYPE_RECEIVER_IMAGE){
            MyLog.i(TAG,"TYPE_RECEIVER_IMAGE");
            binding = DataBindingUtil.inflate(mLayoutInflater,R.layout.item_chat_receive_image,
                    parent,false);
            bindingViewHolder = new ReceiveImageHolder(binding,onRecyclerViewListener,c);
            bindingViewHolder.setLayoutId(R.layout.item_chat_receive_image);
        }else if(viewType == TYPE_SEND_LOCATION){
            binding = DataBindingUtil.inflate(mLayoutInflater,R.layout.item_chat_send_location,
                    parent,false);
            bindingViewHolder = new SendLocationHolder(binding,onRecyclerViewListener,c);
            bindingViewHolder.setLayoutId(R.layout.item_chat_send_location);
        }else if(viewType == TYPE_RECEIVER_LOCATION){
            binding = DataBindingUtil.inflate(mLayoutInflater,R.layout.item_chat_receive_location,
                    parent,false);
            bindingViewHolder = new ReceiveLocationHolder(binding,onRecyclerViewListener,c);
            bindingViewHolder.setLayoutId(R.layout.item_chat_receive_location);
        }else if(viewType == TYPE_SEND_VOICE){
            binding = DataBindingUtil.inflate(mLayoutInflater,R.layout.item_chat_sent_voice,
                    parent,false);
            bindingViewHolder = new SendVoiceHolder(binding,context,onRecyclerViewListener);
            bindingViewHolder.setLayoutId(R.layout.item_chat_sent_voice);
        }else if(viewType == TYPE_RECEIVER_VOICE){
            binding = DataBindingUtil.inflate(mLayoutInflater,R.layout.item_chat_received_voice,
                    parent,false);
            bindingViewHolder = new ReceiveVoiceHolder<BmobIMMessage>(binding,context,onRecyclerViewListener);
            bindingViewHolder.setLayoutId(R.layout.item_chat_received_voice);
//            return new ReceiveVoiceHolder<BmobIMMessage>(binding,context,onRecyclerViewListener);
        }else if(viewType == TYPE_AGREE){
            binding = DataBindingUtil.inflate(mLayoutInflater,R.layout.item_chat_agree,
                    parent,false);
            bindingViewHolder = new BindingViewHolder(binding);
            bindingViewHolder.setLayoutId(R.layout.item_chat_agree);
        }
        return bindingViewHolder;
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, final int position) {
        BmobIMMessage msg = msgs.get(position);
        MyLog.i(TAG,"avatar="+msg.getBmobIMUserInfo());
        switch (holder.getLayoutId()){
            case R.layout.item_chat_agree:
                holder.getBinding().setVariable(magic.cn.health.BR.msg,msg);
                holder.getBinding().executePendingBindings();
                break;
            case R.layout.item_chat_sent_message:
                holder.getBinding().setVariable(magic.cn.health.BR.msg, msg);
                holder.getBinding().setVariable(magic.cn.health.BR.isShowTime,shouldShowTime(position));
                holder.getBinding().executePendingBindings();
                ItemChatSentMessageBinding binding = (ItemChatSentMessageBinding) holder.getBinding();
                int status = msg.getSendStatus();
                if (status == BmobIMSendStatus.SEND_FAILED.getStatus()) {
                    binding.ivFailResend.setVisibility(View.VISIBLE);
                    binding.progressLoad.setVisibility(View.GONE);
                } else if (status == BmobIMSendStatus.SENDING.getStatus()) {
                    binding.ivFailResend.setVisibility(View.GONE);
                    binding.progressLoad.setVisibility(View.VISIBLE);
                } else {
                    binding.ivFailResend.setVisibility(View.GONE);
                    binding.progressLoad.setVisibility(View.GONE);
                }
                break;
            case R.layout.item_chat_receive_message:
                holder.getBinding().setVariable(magic.cn.health.BR.msg, msg);
                holder.getBinding().setVariable(magic.cn.health.BR.isShowTime,shouldShowTime(position));
                ImageView avatar_msg = holder.itemView.findViewById(R.id.iv_avatar);
                if(TextUtils.isEmpty(c.getConversationIcon())){
                    avatar_msg.setImageResource(R.drawable.icon_head_boy);
                }else{
                    ImageLoader.getInstance().displayImage(c.getConversationIcon(),avatar_msg, ImageLoaderOptions.getOptions());
                }
                holder.getBinding().executePendingBindings();
                break;
            case R.layout.item_chat_sent_voice:
                holder.getBinding().setVariable(magic.cn.health.BR.msg, msg);
                holder.getBinding().setVariable(magic.cn.health.BR.isShowTime,shouldShowTime(position));
//                ItemChatSentVoiceBinding bindingVoice = (ItemChatSentVoiceBinding) holder.getBinding();
                ((SendVoiceHolder)holder).initData(msg);
                break;
            case R.layout.item_chat_received_voice :
                holder.getBinding().setVariable(magic.cn.health.BR.msg, msg);
                holder.getBinding().setVariable(magic.cn.health.BR.isShowTime,shouldShowTime(position));
                ((ReceiveVoiceHolder)holder).initData(msg);
                ImageView avatar_voice = holder.itemView.findViewById(R.id.iv_avatar);
                if(TextUtils.isEmpty(c.getConversationIcon())){
                    avatar_voice.setImageResource(R.drawable.icon_head_boy);
                }else{
                    ImageLoader.getInstance().displayImage(c.getConversationIcon(),avatar_voice, ImageLoaderOptions.getOptions());
                }
                break;
            case R.layout.item_chat_send_image:
                holder.getBinding().setVariable(BR.msg,msg);
                holder.getBinding().setVariable(magic.cn.health.BR.isShowTime,shouldShowTime(position));
                ((SendImageHolder)holder).initData(msg);
                break;
            case R.layout.item_chat_receive_image:
                holder.getBinding().setVariable(BR.msg,msg);
                holder.getBinding().setVariable(BR.isShowTime,shouldShowTime(position));
                ((ReceiveImageHolder)holder).initData(msg);
                break;
            case R.layout.item_chat_send_location:
                holder.getBinding().setVariable(BR.msg,msg);
                holder.getBinding().setVariable(BR.isShowTime,shouldShowTime(position));
                ((SendLocationHolder)holder).initData(msg);
                break;
            case R.layout.item_chat_receive_location:
                holder.getBinding().setVariable(BR.msg,msg);
                holder.getBinding().setVariable(BR.isShowTime,shouldShowTime(position));
                ((ReceiveLocationHolder)holder).initData(msg);
                break;
        }
    }
    @Override
    public int getItemCount() {
        return msgs.size();
    }
    @Override
    public int getItemViewType(int position) {
        BmobIMMessage message = msgs.get(position);
        if(message.getMsgType().equals(BmobIMMessageType.IMAGE.getType())){
            return message.getFromId().equals(currentUid) ? TYPE_SEND_IMAGE: TYPE_RECEIVER_IMAGE;
        }else if(message.getMsgType().equals(BmobIMMessageType.LOCATION.getType())){
            return message.getFromId().equals(currentUid) ? TYPE_SEND_LOCATION: TYPE_RECEIVER_LOCATION;
        }else if(message.getMsgType().equals(BmobIMMessageType.VOICE.getType())){
            return message.getFromId().equals(currentUid) ? TYPE_SEND_VOICE: TYPE_RECEIVER_VOICE;
        }else if(message.getMsgType().equals(BmobIMMessageType.TEXT.getType())){
            MyLog.i(TAG,"content="+message.getContent());
            MyLog.i(TAG,"FromId="+message.getFromId()+",toId="+message.getToId()+",currentUid="+currentUid);
            return message.getFromId().equals(currentUid) ? TYPE_SEND_TXT: TYPE_RECEIVER_TXT;
        }else if(message.getMsgType().equals(BmobIMMessageType.VIDEO.getType())){
            return message.getFromId().equals(currentUid) ? TYPE_SEND_VIDEO: TYPE_RECEIVER_VIDEO;
        }else if(message.getMsgType().equals("agree")) {//显示欢迎
            return TYPE_AGREE;
        }else{
            return -1;
        }
    }


    public int findPosition(BmobIMMessage message) {
        int index = this.getCount();
        int position = -1;
        while(index-- > 0) {
            if(message.equals(this.getItem(index))) {
                position = index;
                break;
            }
        }
        return position;
    }
    public int getCount() {
        return this.msgs == null?0:this.msgs.size();
    }
    /**获取消息
     * @param position
     * @return
     */
    public BmobIMMessage getItem(int position){
        return this.msgs == null?null:(position >= this.msgs.size()?null:this.msgs.get(position));
    }
    public void addMessages(List<BmobIMMessage> messages) {
        msgs.addAll(0, messages);
        notifyDataSetChanged();
    }

    public void addMessage(BmobIMMessage message) {
        msgs.addAll(Arrays.asList(message));
        notifyDataSetChanged();
    }
    /**移除消息
     * @param position
     */
    public void remove(int position){
        msgs.remove(position);
        notifyDataSetChanged();
    }

    public BmobIMMessage getFirstMessage() {
        if (null != msgs && msgs.size() > 0) {
            return msgs.get(0);
        } else {
            return null;
        }
    }
    private boolean shouldShowTime(int position) {
        if (position == 0) {
            return true;
        }
        long lastTime = msgs.get(position - 1).getCreateTime();
        long curTime = msgs.get(position).getCreateTime();
        return curTime - lastTime > TIME_INTERVAL;
    }

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }
}
