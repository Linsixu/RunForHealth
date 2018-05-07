package magic.cn.health.service;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;

import magic.cn.health.app.App;
import magic.cn.health.event.LocationEvent;
import magic.cn.health.utils.MyLog;

/**
 * @author 林思旭
 * @since 2018/4/27
 */
public class MyLocationListener extends BDAbstractLocationListener {
    @Override
    public void onReceiveLocation(BDLocation location){
        //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
        //以下只列举部分获取地址相关的结果信息
        //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

        MyLog.i("info",""+location.getLocType());

        double latitude = location.getLatitude();//纬度
        double altitude = location.getAltitude();//经度
        String addr = location.getAddrStr();    //获取详细地址信息
        String country = location.getCountry();    //获取国家
        String province = location.getProvince();    //获取省份
        String city = location.getCity();    //获取城市
        String district = location.getDistrict();    //获取区县
        String street = location.getStreet();    //获取街道信息
        String buildName = location.getBuildingName();
        String locationDescribe = location.getLocationDescribe();//获取位置描述信息

        MyLog.i("info",latitude+","+altitude);
        MyLog.i("info","province="+province+"city="+city+"district="+district+"buildName="+buildName);
        LocationEvent locationEvent = new LocationEvent();
        locationEvent.setAddr(addr);
        locationEvent.setCountry(country);
        locationEvent.setStreet(street);
        locationEvent.setLocationDescribe(locationDescribe);
        locationEvent.setAltitude(altitude);
        locationEvent.setLatitude(latitude);
        locationEvent.setCity(city);
        locationEvent.setDistrict(district);

        MyLog.i("info",locationEvent.toString());


        App.getInstance().setLocationEvent(locationEvent);

//        EventBus.getDefault().post(locationEvent);
    }
}