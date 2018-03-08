package magic.cn.health.utils;

import android.util.Log;

/**
 * @author 林思旭
 * @since 2018/3/5
 */

public class MyLog {

    public final static boolean isOpen = true;
    public static void i(String context, String content){
        if(isOpen){
            Log.i(context,content);
        }
    }
}
