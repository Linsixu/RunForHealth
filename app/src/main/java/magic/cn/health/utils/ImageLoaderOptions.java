package magic.cn.health.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * @author 林思旭
 * @since 2018/3/11
 */

public class ImageLoaderOptions {
    public static DisplayImageOptions getOptions(){
        DisplayImageOptions options =new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .build();
        return options;
    }

}
