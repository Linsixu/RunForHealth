package magic.cn.health.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import magic.cn.health.R;
import magic.cn.health.adapter.UserFriendAdapter;
import magic.cn.health.app.App;
import magic.cn.health.bean.User;
import magic.cn.health.databinding.FragmentBookBinding;
import magic.cn.health.db.NewFriendManager;
import magic.cn.health.event.NewFriendsEvent;
import magic.cn.health.model.UserModel;
import magic.cn.health.ui.activity.NewFriendsActivity;
import magic.cn.health.ui.activity.SearchUserActivity;
import magic.cn.health.utils.CharacterParser;


/** 好友通讯录
 * @author 林思旭
 * @since 2018/3/6
 */

public class BookFragment extends BaseFragment {

    FragmentBookBinding binding;

    private boolean isNewMessage = false;

    private List<User> listUser = new ArrayList<>();

    private CharacterParser characterParser = new CharacterParser();

    private UserFriendAdapter adapter;

    private boolean isNeedRefresh = true;

    private Dialog dialog;

    @Override
    protected View initBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_book,container,false);

        binding.recyclerViewBook.setLayoutManager(new LinearLayoutManager(getActivity()));

        return binding.getRoot();
    }

    @Override
    protected void initView() {
        adapter = new UserFriendAdapter(getActivity());

        binding.recyclerViewBook.setAdapter(adapter);

        binding.layoutSearchFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchUserActivity.class);
                getActivity().startActivity(intent);
            }
        });

        binding.layoutNewFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isNewMessage = false;
                Intent intent = new Intent(getActivity(), NewFriendsActivity.class);
                getActivity().startActivity(intent);
            }
        });

        EventBus.getDefault().register(this);

        dialog = showDialog();
        dialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void destroyView() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isNewMessage || NewFriendManager.getInstance(getActivity()).hasNewFriendInvitation()){
            binding.imgNewMessage.setVisibility(View.VISIBLE);
        }else {
            binding.imgNewMessage.setVisibility(View.GONE);
        }
    }
    public void setNewMessage(boolean newMessage) {
        isNewMessage = newMessage;
    }


    /**
     * 查询本地会话
     */
    public void query() {
        UserModel.getModelInstance().queryFriends(
                new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null && list.size() != 0) {
                            isNeedRefresh = false;//不需要再刷新
                            listUser.clear();
                            listUser.addAll(list);
                            // 根据a-z进行排序(排序成功)
                            Collections.sort(listUser, new Comparator<User>() {
                                @Override
                                public int compare(User lhs, User rhs) {
                                    // TODO Auto-generated method stub
                                    return lhs.getSortLetters().compareToIgnoreCase(rhs.getSortLetters());
                                }
                            });
                            EventBus.getDefault().post(new NewFriendsEvent());
                            showLog("listUser="+listUser.size());
                        }else{
                            EventBus.getDefault().post(new NewFriendsEvent());
                            showLog("无好友");
                        }
                    }
                }
        );
    }

    @Override
    public void onStart() {
        super.onStart();
        showLog("start");
        if(isNeedRefresh){
            dialog.show();
            query();
        }
    }

    @Subscribe(threadMode  = ThreadMode.MAIN)
    public void getFriends(NewFriendsEvent event){
        adapter.addAll(listUser);
        dialog.dismiss();
        App.getInstance().setListFriends(listUser);//缓存好友到App中
    }

    public void setNeedRefresh(boolean needRefresh) {
        isNeedRefresh = needRefresh;
    }

    private Dialog showDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示").setMessage("正在获取好友列表");
        return builder.create();
    }
}
