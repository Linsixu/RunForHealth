package magic.cn.health.model;

import android.app.Activity;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import magic.cn.health.bean.User;
import magic.cn.health.config.Appconfig;
import magic.cn.health.inteface.QueryUserListener;
import magic.cn.health.inteface.UpdateCacheListener;
import magic.cn.health.utils.MyLog;

/**
 * @author 林思旭
 * @since 2018/3/8
 */

public class UserModel extends BaseModel {

    private String TAG = "UserModel";

    public static UserModel userModel = null;

    private Activity activity = null;

    public UserModel(){

    }
    public static UserModel getModelInstance(){
        if(userModel == null){
            synchronized (UserModel.class){
                if(userModel == null){
                    userModel = new UserModel();
                }
            }
        }
        return userModel;
    }

    /** 登录
     * **
     * @param user
     */
    public void login(User user, final SaveListener<User> listener) {
//        if(TextUtils.isEmpty(user.getUsername())){
//            listener.internalStart();
//            listener.done("请填写用户名",new BmobException());
//            MyLog.i(TAG,"请填写用户名");
//            return;
//        }
//        if(TextUtils.isEmpty(password)){
//            listener.internalStart();
//            listener.done("请填写用户名",new BmobException());
//            MyLog.i(TAG,"请填写密码");
//            return;
//        }
//        final User user =new User();
        user.setPassword(user.getPwd());
        user.login(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                listener.done(user,e);
                if(user != null){
                    String userId = user.getObjectId();
                    String name = user.getUsername();
                    String avatar = user.getAvatar();
                    BmobIMUserInfo info = new BmobIMUserInfo(userId,name,avatar);
                    BmobIM.getInstance().updateUserInfo(info);
                    MyLog.i(TAG,user.toString());
                    //重新与Bmob服务器建立IM链接
                    buildconnectFromBmob();
                }
                MyLog.i(TAG,"e="+e);
            }
        });
    }

    /**
     * 退出登陆
     */
    public void loginOut(){
        BmobUser.logOut();
        disconnectFromBmob();
    }


    /**
     * 注册
     */
    public void register(User user, final LogInListener listener){
        if(TextUtils.isEmpty(user.getUsername())){
            MyLog.i(TAG,"请填写用户名");
            listener.internalStart();
            return;
        }
        if(TextUtils.isEmpty(user.getPwd())){
            MyLog.i(TAG,"请填写密码");
            listener.internalStart();
            return;
        }
//        if(TextUtils.isEmpty(user.getPwd())){
//            MyLog.i(TAG,"请填写确认密码");
//            listener.internalStart();
//            return;
//        }
//        if(!password.equals(againPwd)){
//            MyLog.i(TAG,"两次输入的密码不一致，请重新输入");
//            listener.internalStart();
//            return;
//        }
//        final User user = new User();
//        user.setUsername(username);
//        user.setPassword(password);
        MyLog.i(TAG,"user="+user);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                MyLog.i(TAG,"signUpException="+e);
                listener.done(user,e);
                if(user != null){
//                    //重新与Bmob服务器建立IM链接
                    buildconnectFromBmob();
                    //根据文档显示需要更新本地用户信息
                    String userId = user.getObjectId();
                    String name = user.getUsername();
                    String avatar = user.getAvatar();
                    BmobIMUserInfo info = new BmobIMUserInfo(userId,name,avatar);
                    BmobIM.getInstance().updateUserInfo(info);
                    MyLog.i(TAG,user.toString());

                }
            }
        });
    }

    /**查询用户信息
     * @param objectId
     * @param listener
     */
    public void queryUserInfo(String objectId, final QueryUserListener listener){
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", objectId);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {

            }
        });
    }


    /**查询用户
     * @param username
     * @param limit
     * @param listener
     */
    public void queryUsers(String username, final int limit, final FindListener<User> listener){
        BmobQuery<User> query = new BmobQuery<>();
        MyLog.i(TAG,"user="+username);
//        去掉当前用户
        try {
            BmobUser user = BmobUser.getCurrentUser(User.class);
            query.addWhereNotEqualTo("username",user.getUsername());
            if(username.equals(user.getUsername())){
                listener.done(new ArrayList<User>(),new BmobException(Appconfig.NOT_ADD_USER_SELF));
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            MyLog.i(TAG,"error="+e);
        }
//        query.addWhereContains("username", username);
//        query = query.addWhereExists("username");
        query.addWhereEqualTo("username",username);
        query.setLimit(limit);
        query.order("-createdAt");
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                listener.done(list,e);
                MyLog.i(TAG,"query="+e);
            }
        });
    }


    /**更新用户资料和会话资料
     * @param event
     * @param listener
     */
    public void updateUserInfo(MessageEvent event, final UpdateCacheListener listener) {
        final BmobIMConversation conversation = event.getConversation();
        final BmobIMUserInfo info = event.getFromUserInfo();
        final BmobIMMessage msg = event.getMessage();
        String username = info.getName();
        String title = conversation.getConversationTitle();
        //SDK内部将新会话的会话标题用objectId表示，因此需要比对用户名和私聊会话标题，后续会根据会话类型进行判断
        if (!username.equals(title)) {
            UserModel.getModelInstance().queryUserInfo(info.getUserId(), new QueryUserListener() {
                @Override
                public void done(User s, BmobException e) {
                    if (e == null) {
                        String name = s.getUsername();
                        String avatar = s.getAvatar();
                        conversation.setConversationIcon(avatar);
                        conversation.setConversationTitle(name);
                        info.setName(name);
                        info.setAvatar(avatar);
                        //TODO 会话：2.7、更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                        BmobIM.getInstance().updateUserInfo(info);
                        //TODO 会话：4.7、更新会话资料-如果消息是暂态消息，则不更新会话资料
                        if (!msg.isTransient()) {
                            BmobIM.getInstance().updateConversation(conversation);
                        }
                    } else {
                        MyLog.i(TAG,e+"");
                    }
                    listener.done(null);
                }
            });
        } else {
            listener.done(null);
        }
    }

    /**
     * 与服务器重新建立链接
     *
     */
    //TODO 连接：3.1、登录成功、注册成功或处于登录状态重新打开应用后执行连接IM服务器的操作
    public void buildconnectFromBmob(){
        User user = BmobUser.getCurrentUser(User.class);
        if (!TextUtils.isEmpty(user.getObjectId())) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        //连接成功
                    } else {
                        //连接失败
                        MyLog.i(TAG,"connect="+e.getMessage());
                    }
                }
            });
        }
    }

    public void disconnectFromBmob(){
        //TODO 连接：3.2、退出登录需要断开与IM服务器的连接
        BmobIM.getInstance().disConnect();
    }
}
