package magic.cn.health.bean;

import cn.bmob.v3.BmobObject;

/**
 * @author 林思旭
 * @since 2018/3/15
 */

public class Friend extends BmobObject {

    private User user;
    private User friendUser;

//    private transient String pinyin;
//
//    public String getPinyin() {
//        return pinyin;
//    }
//
//    public void setPinyin(String pinyin) {
//        this.pinyin = pinyin;
//    }

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
