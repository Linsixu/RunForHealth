package magic.cn.health.adapter;

import android.databinding.ViewDataBinding;
import android.text.TextUtils;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMLocationMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMReceiveStatus;
import magic.cn.health.R;
import magic.cn.health.databinding.ItemChatReceiveLocationBinding;
import magic.cn.health.inteface.OnRecyclerViewListener;
import magic.cn.health.utils.ImageLoaderOptions;
import magic.cn.health.utils.MyLog;

/**
 * @author 林思旭
 * @since 2018/5/7
 */

public class ReceiveLocationHolder extends BaseViewHolder {

    private String TAG = "ReceiveLocationHolder";

    ItemChatReceiveLocationBinding binding;

    private OnRecyclerViewListener onRecyclerViewListener;

    private BmobIMConversation c;

    public ReceiveLocationHolder(ViewDataBinding binding, OnRecyclerViewListener listener,BmobIMConversation c) {
        super(binding);
        this.binding = (ItemChatReceiveLocationBinding)binding;
        onRecyclerViewListener = listener;
        this.c = c;
    }

    @Override
    public void initData(Object o) {
        BmobIMMessage msg = (BmobIMMessage)o;

//        //用户信息的获取必须在buildFromDB之前，否则会报错'Entity is detached from DAO context'
//        final BmobIMUserInfo info = msg.getBmobIMUserInfo();
        //显示用户头像
        if(TextUtils.isEmpty(c.getConversationIcon())){
            binding.ivAvatar.setImageResource(R.drawable.icon_head_boy);
        }else{
            ImageLoader.getInstance().displayImage(c.getConversationIcon(), binding.ivAvatar, ImageLoaderOptions.getOptions());
        }
        final BmobIMLocationMessage message = BmobIMLocationMessage.buildFromDB(msg);
        int statue = message.getReceiveStatus();

        if(statue == BmobIMReceiveStatus.DOWNLOADED.getStatus()){
            binding.progressLoad.setVisibility(View.VISIBLE);
        }else if(statue == BmobIMReceiveStatus.READ.getStatus()){
            binding.progressLoad.setVisibility(View.GONE);
        }

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

        binding.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.i(TAG,"点击"+"头像");
            }
        });
    }
}
