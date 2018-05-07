package magic.cn.health.adapter;

import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import magic.cn.health.databinding.ItemChatReceiveImageBinding;
import magic.cn.health.inteface.OnRecyclerViewListener;
import magic.cn.health.utils.ImageLoaderOptions;
import magic.cn.health.utils.MyLog;

/**
 * @author 林思旭
 * @since 2018/4/12
 */

public class ReceiveImageHolder extends BaseViewHolder {
    private String TAG = "ReceiveImageHolder";

    private ItemChatReceiveImageBinding binding;

    private OnRecyclerViewListener onRecyclerViewListener;

    public ReceiveImageHolder(ViewDataBinding binding, OnRecyclerViewListener listener) {
        super(binding);
        this.binding = (ItemChatReceiveImageBinding)binding;
        onRecyclerViewListener = listener;
    }


    @Override
    public void initData(Object o) {
        BmobIMMessage msg = (BmobIMMessage)o;
        //用户信息的获取必须在buildFromDB之前，否则会报错'Entity is detached from DAO context'
        final BmobIMUserInfo info = msg.getBmobIMUserInfo();
        //可使用buildFromDB方法转化为指定类型的消息
        final BmobIMImageMessage message = BmobIMImageMessage.buildFromDB(false,msg);
        //显示图片
        ImageLoader.getInstance().loadImage(message.getRemoteUrl(), ImageLoaderOptions.getOptions(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                binding.progressLoad.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                binding.progressLoad.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                binding.progressLoad.setVisibility(View.INVISIBLE);
                MyLog.i(TAG,"imageUrl="+imageUri);
                binding.ivPicture.setImageBitmap(loadedImage);
//                ImageLoader.getInstance().displayImage(imageUri, binding.ivPicture,ImageLoaderOptions.getOptions(),null);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                binding.progressLoad.setVisibility(View.INVISIBLE);
            }
        });

        binding.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.i(TAG,"点击" + info.getName() + "的头像");
            }
        });

        binding.ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.i(TAG,"点击图片:"+message.getRemoteUrl()+"");
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
    }
}
