package magic.cn.health.adapter;

import android.databinding.ViewDataBinding;
import android.view.View;

import cn.bmob.newim.bean.BmobIMLocationMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMReceiveStatus;
import magic.cn.health.databinding.ItemChatReceiveLocationBinding;
import magic.cn.health.inteface.OnRecyclerViewListener;
import magic.cn.health.utils.MyLog;

/**
 * @author 林思旭
 * @since 2018/5/7
 */

public class ReceiveLocationHolder extends BaseViewHolder {

    private String TAG = "ReceiveLocationHolder";

    ItemChatReceiveLocationBinding binding;

    private OnRecyclerViewListener onRecyclerViewListener;

    public ReceiveLocationHolder(ViewDataBinding binding, OnRecyclerViewListener listener) {
        super(binding);
        this.binding = (ItemChatReceiveLocationBinding)binding;
        onRecyclerViewListener = listener;
    }

    @Override
    public void initData(Object o) {
        BmobIMMessage msg = (BmobIMMessage)o;

//        //用户信息的获取必须在buildFromDB之前，否则会报错'Entity is detached from DAO context'
//        final BmobIMUserInfo info = msg.getBmobIMUserInfo();

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
