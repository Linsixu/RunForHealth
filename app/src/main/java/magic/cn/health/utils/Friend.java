package magic.cn.health.utils;

import cn.bmob.v3.BmobObject;
import magic.cn.health.bean.User;

/**
 * Friend表
 * @author 林思旭
 * @since 2018/3/12
 */

public class Friend extends BmobObject{
    private User user;

    private User friendUser;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getFriendUser() {
        return friendUser;
    }

    public void setFriendUser(User friendUser) {
        this.friendUser = friendUser;
    }
}
