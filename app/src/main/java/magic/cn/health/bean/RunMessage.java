package magic.cn.health.bean;

import cn.bmob.v3.BmobObject;

/**
 * @author 林思旭
 * @since 2018/4/16
 */

public class RunMessage extends BmobObject{

    private User user;

    private String messgae;

    private String location;

    public RunMessage(User user, String messgae,String location) {
        this.user = user;
        this.messgae = messgae;
        this.location = location;
    }

    public User getUser() {
        return user;
    }

    public String getMessgae() {
        return messgae;
    }

    public String getLocation(){return location;}
}
