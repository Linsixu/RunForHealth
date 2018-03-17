package magic.cn.health.bean;

import cn.bmob.v3.BmobUser;

/**
 * @author 林思旭
 * @since 2018/3/8
 */

public class User extends BmobUser{

    private String avatar;

    /**
     * 显示数据拼音的首字母，实现微信那种联系人按字母先后顺序排序的效果
     */
    private String sortLetters;

    /**
     * 性别-true-男
     */
    private boolean sex = true;

    private String againPwd;

    private String pwd;

    private String nick;

    private String signature;//个人签名

    private String height;

    private String weight;

    public User(){}

    public User(NewFriend friend){
        setObjectId(friend.getUid());
        setUsername(friend.getName());
        setAvatar(friend.getAvatar());
    }
    public String getAgainPwd() {
        return againPwd;
    }

    public String getPwd() {
        return pwd;
    }


    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setAgainPwd(String againPwd) {
        this.againPwd = againPwd;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public boolean getSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public boolean isSex() {
        return sex;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "User{" +
                "avatar='" + avatar + '\'' +
                ", sortLetters='" + sortLetters + '\'' +
                ", sex=" + sex +
                ", againPwd='" + againPwd + '\'' +
                ", pwd='" + pwd + '\'' +
                ", nick='" + nick + '\'' +
                ", signature='" + signature + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User u = (User) obj;
            return this.getObjectId().equals(u.getObjectId());
        }
        return super.equals(obj);
    }
}
