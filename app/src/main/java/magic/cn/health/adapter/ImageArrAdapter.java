package magic.cn.health.adapter;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import magic.cn.health.R;
import magic.cn.health.utils.ImageLoaderOptions;

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
}
