package magic.cn.health.app;

import android.app.Application;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import magic.cn.health.bean.User;
import magic.cn.health.event.LocationEvent;
import magic.cn.health.service.BmobMessageHandler;
import magic.cn.health.service.MyLocationListener;
import magic.cn.health.utils.MyLog;
import magic.cn.health.utils.SharePreferenceUtil;

/**
 * @author 林思旭
 * @since 2018/3/8
 */

public class App extends Application {

    public static App instance;

    public static final String PREFERENCE_NAME = "_sharedinfo";

    private static SharePreferenceUtil mSpUtil;

    private List<User> listFriends;

    public LocationClient mLocationClient = null;

    private MyLocationListener myListener = new MyLocationListener();

    private LocationEvent locationEvent = null;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //TODO 集成：1.8、初始化IM SDK，并注册消息接收器
        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            BmobIM.init(this);
            BmobIM.registerDefaultMessageHandler(new BmobMessageHandler(this));
        }
        initImageLoader();

        listFriends = new ArrayList<>();

        //百度地图
        mLocationClient = new LocationClient(instance);
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数

        LocationClientOption option = new LocationClientOption();

        option.setIsNeedLocationDescribe(true);

        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要

        option.setIsNeedLocationPoiList(true);
        //可选，是否需要位置描述信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的位置信息，此处必须为true

        mLocationClient.setLocOption(option);

        mLocationClient.start();
    }

    /**
     * 获取当前运行的进程名
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static App getInstance(){
        return instance;
    }

    /**
     * 获取SharedPreferences实例
     * @return
     */
    public synchronized SharePreferenceUtil getSharedPreferencesUtil(){
        if(mSpUtil==null) {
            String currentId =  BmobIM.getInstance().getCurrentUid();
            MyLog.i("App",currentId);
            String shared_name = currentId+PREFERENCE_NAME;
            mSpUtil = new SharePreferenceUtil(this,shared_name);
        }
        return mSpUtil;
    }


    /**
     * 初始化ImageLoader
     */
    private  void initImageLoader() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(this,
                "health/Cache");// 获取到缓存的目录地址
        FileNameGenerator fileName = new FileNameGenerator() {
            @Override
            public String generate(String imageUri) {
                return null;
            }
        };

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                // 线程池内加载的数量
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(new UnlimitedDiskCache(cacheDir))// 自定义缓存路径
                // .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .writeDebugLogs() // Remove for release app
                .build();
        ImageLoader.getInstance().init(config);
    }

    public List<User> getListFriends() {
        return listFriends;
    }

    public void setListFriends(List<User> listFriends) {
        if(this.listFriends.size()!=0)this.listFriends.clear();
        this.listFriends = listFriends;
    }

    public void clearFriends(){
        this.listFriends.clear();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if(mLocationClient.isStarted())mLocationClient.stop();
    }

    public LocationEvent getLocationEvent() {
        return locationEvent;
    }

    public void setLocationEvent(LocationEvent locationEvent) {
        this.locationEvent = locationEvent;
    }
}
