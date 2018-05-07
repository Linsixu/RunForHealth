package magic.cn.health.event;

/**
 * @author 林思旭
 * @since 2018/4/27
 */

public class LocationEvent {

    private String locationDescribe;

    private String addr;

    private String country;

    private String street;

    private double latitude;

    private double altitude;

    private String city;

    private String district;

    public String getLocationDescribe() {
        return locationDescribe;
    }

    public void setLocationDescribe(String locationDescribe) {
        this.locationDescribe = locationDescribe;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    @Override
    public String toString() {
        return "LocationEvent{" +
                "locationDescribe='" + locationDescribe + '\'' +
                ", addr='" + addr + '\'' +
                ", country='" + country + '\'' +
                ", street='" + street + '\'' +
                ", latitude=" + latitude +
                ", altitude=" + altitude +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                '}';
    }
}
