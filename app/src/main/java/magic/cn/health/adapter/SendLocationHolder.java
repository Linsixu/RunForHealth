package magic.cn.health.adapter;

import android.databinding.ViewDataBinding;
import android.view.View;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMLocationMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;
import magic.cn.health.databinding.ItemChatSendLocationBinding;
import magic.cn.health.inteface.OnRecyclerViewListener;
import magic.cn.health.utils.MyLog;

/**
 * @author 林思旭
 * @since 2018/5/7
 */

public class SendLocationHolder extends BaseViewHolder {

    private String TAG = "SendLocationHolder";

    ItemChatSendLocationBinding binding;

    private OnRecyclerViewListener onRecyclerViewListener;

    private BmobIMConversation c;

    public SendLocationHolder(ViewDataBinding binding,OnRecyclerViewListener listener,BmobIMConversation c) {
        super(binding);
        this.binding = (ItemChatSendLocationBinding) binding;
        onRecyclerViewListener = listener;
        this.c = c;
    }

    @Override
    public void initData(Object o) {
        BmobIMMessage msg = (BmobIMMessage)o;

        final BmobIMLocationMessage message = BmobIMLocationMessage.buildFromDB(msg);
        int status =message.getSendStatus();
        if (status == BmobIMSendStatus.SEND_FAILED.getStatus()) {
            binding.ivFailResend.setVisibility(View.VISIBLE);
            binding.progressLoad.setVisibility(View.GONE);
        } else if (status== BmobIMSendStatus.SENDING.getStatus()) {
            binding.ivFailResend.setVisibility(View.GONE);
            binding.progressLoad.setVisibility(View.VISIBLE);
        } else {
            binding.ivFailResend.setVisibility(View.GONE);
            binding.progressLoad.setVisibility(View.GONE);
        }
        binding.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.i(TAG,"头像");
            }
        });

        binding.tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.i(TAG,"经度：" + message.getLongitude() + ",维度：" + message.getLatitude());
                if(onRecyclerViewListener!=null){
                    onRecyclerViewListener.onItemClick(getAdapterPosition());
                }
            }
        });
        binding.tvLocation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onRecyclerViewListener != null) {
                    onRecyclerViewListener.onItemLongClick(getAdapterPosition());
                }
                return true;
            }
        });
        //重发
        binding.ivFailResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.resendMessage(message, new MessageSendListener() {
                    @Override
                    public void onStart(BmobIMMessage msg) {
                        binding.progressLoad.setVisibility(View.VISIBLE);
                        binding.ivFailResend.setVisibility(View.GONE);
                        binding.tvSendStatus.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void done(BmobIMMessage msg, BmobException e) {
                        if (e == null) {
                            binding.tvSendStatus.setVisibility(View.VISIBLE);
                            binding.tvSendStatus.setText("已发送");
                            binding.ivFailResend.setVisibility(View.GONE);
                            binding.progressLoad.setVisibility(View.GONE);
                        } else {
                            binding.ivFailResend.setVisibility(View.VISIBLE);
                            binding.progressLoad.setVisibility(View.GONE);
                            binding.tvSendStatus.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }
}
