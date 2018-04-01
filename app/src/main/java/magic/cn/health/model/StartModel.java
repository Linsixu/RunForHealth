package magic.cn.health.model;

import android.content.Context;
import android.content.Intent;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import magic.cn.health.bean.Conversation;
import magic.cn.health.bean.User;
import magic.cn.health.ui.activity.ChatActivity;

/**
 * @author 林思旭
 * @since 2018/3/18
 */

public class StartModel {

    private Context context;

    private static StartModel startModel = null;

    public StartModel(Context context) {
        this.context = context;
    }

    public void jumpToChatActivity(User user){
        Intent intent = new Intent(context, ChatActivity.class);
//        intent.putExtra("user",user);
//        context.startActivity(intent);

        BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar());
        //TODO 会话：4.1、创建一个常态会话入口，好友聊天
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, null);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("c", conversationEntrance);
        intent.putExtra("c",conversationEntrance);
        context.startActivity(intent);
    }


    public void jumpToChatActivity(Conversation conversation){
        Intent intent = new Intent();
        intent.setClass(context, ChatActivity.class);
        BmobIMUserInfo info = new BmobIMUserInfo(conversation.getcId(), conversation.getcName(), conversation.getAvatar().toString());
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, null);
        intent.putExtra("c", conversationEntrance);
        context.startActivity(intent);
    }

}
