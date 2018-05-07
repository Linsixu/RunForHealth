package magic.cn.health.adapter;

import android.databinding.ViewDataBinding;
import android.text.TextUtils;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;
import magic.cn.health.databinding.ItemChatSendImageBinding;
import magic.cn.health.inteface.OnRecyclerViewListener;
import magic.cn.health.utils.ImageLoaderOptions;
import magic.cn.health.utils.MyLog;

/**
 * @author 林思旭
 * @since 2018/4/12
 */

public class SendImageHolder extends BaseViewHolder {

    private String TAG = "SendImageHolder";

    private ItemChatSendImageBinding binding;

    private BmobIMConversation c;

    private OnRecyclerViewListener onRecyclerViewListener;

    public SendImageHolder(ViewDataBinding binding, BmobIMConversation c, OnRecyclerViewListener listener) {
        super(binding);
        this.binding = (ItemChatSendImageBinding)binding;
        this.c = c;
        onRecyclerViewListener = listener;
    }

    @Override
    public void initData(Object o) {
        BmobIMMessage msg = (BmobIMMessage)o;
        //用户信息的获取必须在buildFromDB之前，否则会报错'Entity is detached from DAO context'
        final BmobIMUserInfo info = msg.getBmobIMUserInfo();
//        ImageLoaderFactory.getLoader().loadAvator(iv_avatar,info != null ? info.getAvatar() : null, R.mipmap.head);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
//        String time = dateFormat.format(msg.getCreateTime());
//        tv_time.setText(time);
        //
        final BmobIMImageMessage message = BmobIMImageMessage.buildFromDB(true, msg);
        int status =message.getSendStatus();
        if (status == BmobIMSendStatus.SEND_FAILED.getStatus() ||status == BmobIMSendStatus.UPLOAD_FAILED.getStatus()) {
            binding.ivFailResend.setVisibility(View.VISIBLE);
            binding.progressLoad.setVisibility(View.GONE);
            binding.tvSendStatus.setVisibility(View.INVISIBLE);
        } else if (status== BmobIMSendStatus.SENDING.getStatus()) {
            binding.ivFailResend.setVisibility(View.GONE);
            binding.progressLoad.setVisibility(View.VISIBLE);
            binding.tvSendStatus.setVisibility(View.INVISIBLE);
        } else {
            binding.tvSendStatus.setVisibility(View.VISIBLE);
            binding.tvSendStatus.setText("已发送");
            binding.ivFailResend.setVisibility(View.GONE);
            binding.progressLoad.setVisibility(View.GONE);
        }

        MyLog.i(TAG,"RemoteUrl="+message.getRemoteUrl());
        MyLog.i(TAG,"LocalPath="+message.getLocalPath());

        //发送的不是远程图片地址，则取本地地址
        ImageLoader.getInstance().displayImage(TextUtils.isEmpty(message.getRemoteUrl()) ? message.getLocalPath():message.getRemoteUrl(),
                binding.ivPicture, ImageLoaderOptions.getOptions(),null);
//        ImageLoaderFactory.getLoader().load(iv_picture, TextUtils.isEmpty(message.getRemoteUrl()) ? message.getLocalPath():message.getRemoteUrl(),R.mipmap.ic_launcher,null);
//    ViewUtil.setPicture(TextUtils.isEmpty(message.getRemoteUrl()) ? message.getLocalPath():message.getRemoteUrl(), R.mipmap.ic_launcher, iv_picture,null);

        binding.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.i(TAG,"点击" + info.getName() + "的头像");
            }
        });
        binding.ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.i(TAG,"点击图片:"+(TextUtils.isEmpty(message.getRemoteUrl()) ? message.getLocalPath():message.getRemoteUrl())+"");
                if(onRecyclerViewListener!=null){
                    onRecyclerViewListener.onItemClick(getAdapterPosition());
                }
            }
        });

        binding.ivPicture.setOnLongClickListener(new View.OnLongClickListener() {
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
