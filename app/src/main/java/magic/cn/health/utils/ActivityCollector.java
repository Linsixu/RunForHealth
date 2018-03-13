package magic.cn.health.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 林思旭
 * @since 2018/3/11
 */

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<Activity>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
        activity.finish();
    }

    public static void finishAll(){
        for (Activity activity:activities
                ) {
            if(activity!=null&&!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
