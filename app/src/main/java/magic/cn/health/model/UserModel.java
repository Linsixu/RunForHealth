package magic.cn.health.model;

import android.app.Activity;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import magic.cn.health.bean.AddFriendMessage;
import magic.cn.health.bean.AgreeAddFriendMessage;
import magic.cn.health.bean.Friend;
import magic.cn.health.bean.NewFriend;
import magic.cn.health.bean.User;
import magic.cn.health.config.Appconfig;
import magic.cn.health.db.NewFriendManager;
import magic.cn.health.inteface.QueryUserListener;
import magic.cn.health.inteface.UpdateCacheListener;
import magic.cn.health.utils.CharacterParser;
import magic.cn.health.utils.MyLog;

/**
 * @author 林思旭
 * @since 2018/3/8
 */

public class UserModel extends BaseModel {

    private String TAG = "UserModel";

    public static UserModel userModel = null;

    private Activity activity = null;

    private CharacterParser characterParser = new CharacterParser();

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
        String avatar = info.getAvatar();
        String title = conversation.getConversationTitle();
        String toId = msg.getToId();
        String icon = conversation.getConversationIcon();
        User user = BmobUser.getCurrentUser(User.class);
        //SDK内部将新会话的会话标题用objectId表示，因此需要比对用户名和私聊会话标题，后续会根据会话类型进行判断
        if (!username.equals(title) || toId.equals(user.getObjectId()) || (avatar != null && !avatar.equals(icon))) {
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
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        listener.done(list.get(0), null);
                    } else {
                        listener.done(null, new BmobException(000, "查无此人"));
                    }
                } else {
                    listener.done(null, e);
                }
            }
        });

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

    /**
     * 发送添加好友的请求
     */
    //TODO 好友管理：9.7、发送添加好友请求
    public void sendAddFriendMessgae(User user, final MessageSendListener listener){
        BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(),user.getUsername(),user.getAvatar());
        //TODO 会话：4.1、创建一个暂态会话入口，发送好友请求
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, true, null);
        //TODO 消息：5.1、根据会话入口获取消息管理，发送好友请求
        BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
        AddFriendMessage msg = new AddFriendMessage();
        User currentUser = BmobUser.getCurrentUser(User.class);
        msg.setContent("很高兴认识你，可以加个好友一起去跑步吗？");//给对方的一个留言信息
        //TODO 这里只是举个例子，其实可以不需要传发送者的信息过去
        Map<String, Object> map = new HashMap<>();
        map.put("name", currentUser.getUsername());//发送者姓名
        map.put("avatar", currentUser.getAvatar());//发送者的头像
        map.put("uid", currentUser.getObjectId());//发送者的uid
        msg.setExtraMap(map);
        messageManager.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                listener.done(msg,e);
//                if (e == null) {//发送成功
//                    toast("好友请求发送成功，等待验证");
//                } else {//发送失败
//                    toast("发送失败:" + e.getMessage());
//                }
            }
        });
    }

    /**
     * 发送同意添加好友的消息
     */
    //TODO 好友管理：9.8、发送同意添加好友
    public void sendAgreeAddFriendMessage(final NewFriend add, final SaveListener<Object> listener) {
        BmobIMUserInfo info = new BmobIMUserInfo(add.getUid(), add.getName(), add.getAvatar());
        //TODO 会话：4.1、创建一个暂态会话入口，发送同意好友请求
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, true, null);
        //TODO 消息：5.1、根据会话入口获取消息管理，发送同意好友请求
        BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
        //而AgreeAddFriendMessage的isTransient设置为false，表明我希望在对方的会话数据库中保存该类型的消息
        AgreeAddFriendMessage msg = new AgreeAddFriendMessage();
        final User currentUser = BmobUser.getCurrentUser(User.class);
        msg.setContent("我通过了你的好友验证请求，我们可以开始 聊天了!");//这句话是直接存储到对方的消息表中的
        Map<String, Object> map = new HashMap<>();
        map.put("msg", currentUser.getUsername() + "同意添加你为好友");//显示在通知栏上面的内容
        map.put("uid", add.getUid());//发送者的uid-方便请求添加的发送方找到该条添加好友的请求
        map.put("time", add.getTime());//添加好友的请求时间
        msg.setExtraMap(map);
        messageManager.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {//发送成功
                    //TODO 3、修改本地的好友请求记录
                    NewFriendManager.getInstance(getContext()).updateNewFriend(add, Appconfig.STATUS_VERIFIED);
                    listener.done(msg, e);
                } else {//发送失败
                    MyLog.i(TAG,e.getErrorCode()+","+e.getMessage());
                    listener.done(msg, e);
                }
            }
        });
    }

    //TODO 好友管理：9.12、添加好友
    public void agreeAddFriend(User friend, SaveListener<String> listener) {
        Friend f = new Friend();
        User user = BmobUser.getCurrentUser(User.class);
        f.setUser(user);
        f.setFriendUser(friend);
        f.save(listener);
    }
    /**
     * 删除好友
     *
     * @param f
     * @param listener
     */
    //TODO 好友管理：9.3、删除好友
    public void deleteFriend(Friend f, UpdateListener listener) {
        Friend friend = new Friend();
        friend.delete(f.getObjectId(), listener);
    }
    /**
     * TODO 用户管理：2.4、获取当前用户
     *
     * @return
     */
    public User getCurrentUser() {
        return BmobUser.getCurrentUser(User.class);
    }


    /**
     * TODO 好友管理：9.10、添加到好友表中再发送同意添加好友的消息
     *
     * @param add
     * @param listener
     */
    public void agreeAdd(final NewFriend add, final SaveListener<Object> listener) {
        User user = new User();
        user.setObjectId(add.getUid());
        user.setAvatar(add.getAvatar());
        user.setUsername(add.getName());
        agreeAddFriend(user, new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    sendAgreeAddFriendMessage(add, listener);
                } else {
                    MyLog.i(TAG,e.getErrorCode()+","+e.getMessage());
                    listener.done(null, e);
                }
            }
        });
    }

    /**
     * 查询好友
     *
     * @param listener
     */
    //TODO 好友管理：9.2、查询好友
    public void queryFriends(final FindListener<User> listener) {
        final List<User> listUser = new ArrayList<>();
        BmobQuery<Friend> query = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        query.addWhereEqualTo("user", user);
        query.include("friendUser");
        query.order("-updatedAt");
        query.findObjects(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        //添加首字母
                        for (int i = 0; i < list.size(); i++) {
                            Friend friend = list.get(i);
                            // 汉字转换成拼音
                            User friendUser = friend.getFriendUser();
                            String username = friendUser.getUsername();//改变了(获取Nick来排序)
                            if (username != null) {
                                String pinyin = characterParser.getSelling(friendUser.getNick());//出现空指针(忘记New了)改变了(获取Nick来排序)
                                String sortString = pinyin.substring(0, 1).toUpperCase();
                                // 正则表达式，判断首字母是否是英文字母
                                if (sortString.matches("[A-Z]")) {
                                    MyLog.i(TAG,"UpperCase="+sortString.toUpperCase()+" ");
                                    friendUser.setSortLetters(sortString.toUpperCase());
                                } else {
                                    friendUser.setSortLetters("#");
                                }
                            } else {
                                friendUser.setSortLetters("#");
                            }
                            listUser.add(friendUser);
                            MyLog.i(TAG,"nick="+friendUser.getNick());
                        }
                        listener.done(listUser, e);
                    } else {
                        listener.done(listUser, new BmobException(0, "暂无联系人"));
                    }
                } else {
                    listener.done(listUser, e);
                }
            }
        });
    }


    private void sortListUser(List<Friend> list){

    }
}
