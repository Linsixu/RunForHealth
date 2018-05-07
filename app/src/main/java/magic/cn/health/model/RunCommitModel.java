package magic.cn.health.model;

import android.content.Context;
import android.widget.Toast;

import java.util.List;

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import magic.cn.health.app.App;
import magic.cn.health.bean.RunMessage;
import magic.cn.health.bean.User;

/**
 * @author 林思旭
 * @since 2018/4/18
 */

public class RunCommitModel {
    private String TAG = "RunCommitModel";

    private static RunCommitModel model = null;

    private  Context context = null;

    public void setContext(Context context) {
        this.context = context;
    }

    public static RunCommitModel getInstance(){
        if(model == null){
            synchronized (RunCommitModel.class){
                if(model == null){
                    model = new RunCommitModel();
                }
            }
        }
        return model;
    }

    public void addCommitMsg(String msg,String location, SaveListener<String> listener){
        User user = BmobUser.getCurrentUser(User.class);
        RunMessage message = new RunMessage(user,msg,location);
        message.save(listener);
    }


    public void queryCommitMsg(final FindListener listener){
        BmobQuery<RunMessage> query = new BmobQuery<>();
        query.include("user");
//        query.include("message");
        query.order("-updatedAt");
        query.findObjects(new FindListener<RunMessage>() {
            @Override
            public void done(List<RunMessage> list, BmobException e) {
                listener.done(list,e);
            }
        });
    }

    /**
     * 查询自己发布的运动消息
     * @param listener
     */

    public void querySelfCommitMsg(final FindListener listener){
        BmobQuery<RunMessage> query = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(User.class);
        query.addWhereEqualTo("user", user);
        query.include("user");
//        query.include("message");
        query.order("-updatedAt");
        query.findObjects(new FindListener<RunMessage>() {
            @Override
            public void done(List<RunMessage> list, BmobException e) {
                listener.done(list,e);
            }
        });
    }
    public void onItemClick(User user){
        if(UserModel.getModelInstance().getCurrentUser().equals(user)){
            Toast.makeText(App.getInstance(),"不能添加自己",Toast.LENGTH_SHORT).show();
            return;
        }
        if(App.getInstance().getListFriends().contains(user)){
            new StartModel(context).jumpToChatActivity(user);
//            Toast.makeText(App.getInstance(),"对方已是你好友",Toast.LENGTH_SHORT).show();
            return;
        }
        UserModel.getModelInstance().sendAddFriendMessgae(user, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                if(e == null){
                    Toast.makeText(App.getInstance(),"好友请求发送成功，等待验证",Toast.LENGTH_SHORT).show();
                }else {//发送失败
                    Toast.makeText(App.getInstance(),"发送失败"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
