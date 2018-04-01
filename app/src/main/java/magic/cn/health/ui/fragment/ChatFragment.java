package magic.cn.health.ui.fragment;

import android.databinding.DataBindingUtil;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import magic.cn.health.R;
import magic.cn.health.adapter.ConversationAdapter;
import magic.cn.health.bean.Conversation;
import magic.cn.health.bean.PrivateConversation;
import magic.cn.health.databinding.FragmentChatBinding;
import magic.cn.health.event.RefreshEvent;


/**
 * 会话界面
 * @author 林思旭
 * @since 2018/3/6
 */

public class ChatFragment extends BaseFragment {

    FragmentChatBinding binding;

    private ConversationAdapter adapter;

    @Override
    protected View initBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat,container,false);
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        adapter = new ConversationAdapter(getActivity());

        binding.swRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.primaryColor);

        binding.swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
            }
        });
        binding.rcView.setLayoutManager(new LinearLayoutManager(getActivity()));

        binding.rcView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.swRefresh.setRefreshing(true);
        query();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void destroyView() {

    }

    /**
     查询本地会话
     */
    public void query(){
        adapter.addAll(getConversations());
        binding.swRefresh.setRefreshing(false);
    }


    /**
     * 获取会话列表的数据：增加新朋友会话
     * @return
     */
    private List<Conversation> getConversations(){
        //添加会话
        List<Conversation> conversationList = new ArrayList<>();
        conversationList.clear();
        //TODO 会话：4.2、查询全部会话
        List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
        if(list!=null && list.size()>0){
            for (BmobIMConversation item:list){
                switch (item.getConversationType()){
                    case 1://私聊
                        conversationList.add(new PrivateConversation(item));
                        break;
                    default:
                        break;
                }
            }
        }
        showLog("list="+list);
//        //添加新朋友会话-获取好友请求表中最新一条记录
//        List<NewFriend> friends = NewFriendManager.getInstance(getActivity()).getAllNewFriend();
//        if(friends!=null && friends.size()>0){
//            conversationList.add(new NewFriendConversation(friends.get(0)));
//        }
        //重新排序
        Collections.sort(conversationList);
        return conversationList;
    }

    /**注册自定义消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshEvent event){
        showLog("---会话页接收到自定义消息---");
        //因为新增`新朋友`这种会话类型
        adapter.addAll(getConversations());
    }

    /**注册离线消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event){
        //重新刷新列表
        adapter.addAll(getConversations());
    }

    /**注册消息接收事件
     * @param event
     * 1、与用户相关的由开发者自己维护，SDK内部只存储用户信息
     * 2、开发者获取到信息后，可调用SDK内部提供的方法更新会话
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event){
        //重新获取本地消息并刷新列表
        adapter.addAll(getConversations());
    }
}
