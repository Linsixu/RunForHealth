package magic.cn.health.adapter;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.bmob.newim.bean.BmobIMAudioMessage;
import cn.bmob.newim.bean.BmobIMLocationMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import magic.cn.health.R;
import magic.cn.health.app.App;
import magic.cn.health.bean.Conversation;
import magic.cn.health.utils.ImageLoaderOptions;
import magic.cn.health.utils.SharePreferenceUtil;
import magic.cn.health.utils.TimeUtil;

/**
 * databinding适配
 * @author 林思旭
 * @since 2018/3/16
 */

public class ImageArrAdapter {
    @BindingAdapter("app:imageUrl")
    public static void  loadImageView(ImageView imageView,String url){
        if(url != null){
            ImageLoader.getInstance().displayImage(url,imageView, ImageLoaderOptions.getOptions());
        }else{
            imageView.setImageResource(R.drawable.icon_head_boy);
        }
    }

    @BindingAdapter("app:loadIcon")
    public static void loadUserIcon(ImageView imageView,BmobIMMessage message){
        if(message != null){
            BmobIMUserInfo info = message.getBmobIMUserInfo();
            if(info != null && info.getAvatar() != null){
                ImageLoader.getInstance().displayImage(info.getAvatar(),imageView,ImageLoaderOptions.getOptions());
            }else {
                imageView.setImageResource(R.drawable.icon_head_boy);
            }
        }
    }

    @BindingAdapter("app:loadUserImage")
    public static void loadUserSelfIcon(ImageView imageView,BmobIMMessage message){
        if(message != null) {
            BmobIMUserInfo info = message.getBmobIMUserInfo();
            if(info != null && !TextUtils.isEmpty(info.getAvatar())){
                ImageLoader.getInstance().displayImage(info.getAvatar(),imageView,ImageLoaderOptions.getOptions());
            }else {
                SharePreferenceUtil sharePreferenceUtil = App.getInstance().getSharedPreferencesUtil();
                if(sharePreferenceUtil!=null && sharePreferenceUtil.getAvatarUrl()!=null){
                    ImageLoader.getInstance().displayImage(sharePreferenceUtil.getAvatarUrl(),imageView,ImageLoaderOptions.getOptions());
                }else{
                    imageView.setImageResource(R.drawable.icon_head_boy);
                }
            }
        }
    }
    @BindingAdapter("app:data")
    public static void bindFormat(TextView textView,Long date){
        if (date == 0) {
            return;
        }
        textView.setText(TimeUtil.getChatTime(date));
    }

    @BindingAdapter("app:initTime")
    public static void initVoiceTime(TextView textView,BmobIMMessage message){
        //使用buildFromDB方法转化成指定类型的消息
        BmobIMAudioMessage msg = BmobIMAudioMessage.buildFromDB(true,message);
        textView.setText(msg.getDuration()+"\''");
    }

    @BindingAdapter("app:setUnRead")
    public static void setUnReadMsg(TextView textView, Conversation conversation){
        //查询指定未读消息数
        long unread = conversation.getUnReadCount();
        if(unread>0){
            textView.setVisibility(View.VISIBLE);
            textView.setText(String.valueOf(unread));
        }else{
            textView.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("app:setLocation")
    public static void setLocationMsg(TextView textView,BmobIMMessage message){
        BmobIMLocationMessage locationMessage = BmobIMLocationMessage.buildFromDB(message);
        textView.setText(locationMessage.getAddress());
    }
}
