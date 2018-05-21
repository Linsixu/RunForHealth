package magic.cn.health.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import magic.cn.health.R;
import magic.cn.health.adapter.UserFriendAdapter;
import magic.cn.health.app.App;
import magic.cn.health.bean.Friend;
import magic.cn.health.bean.User;
import magic.cn.health.config.Appconfig;
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

    private Dialog dialog;

    private Dialog  deleteDialog;

    private boolean isRefresh = true;

    private final static int REQUEST_NEWFRIEND_CODE = 1; // 返回的结果码

    private boolean isShow = true;

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
//                Intent intent = new Intent(getActivity(), NewFriendsActivity.class);
//                getActivity().startActivity(intent);
                Intent intent = new Intent(getActivity(), NewFriendsActivity.class);
                startActivityForResult(intent,REQUEST_NEWFRIEND_CODE);
            }
        });

        EventBus.getDefault().register(this);

        dialog = showDialog();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        //删除好友的监听
        adapter.setDeleteUserListener(new UserFriendAdapter.DeleteUserListener() {
            @Override
            public void onClick(View v,int position) {
                User user = listUser.get(position);
                deleteDialog = showDeleteDialog(user.getNick(),user);
                deleteDialog.show();
            }
        });
    }

    @Override
    protected void destroyView() {
        if(dialog.isShowing() && dialog!=null)dialog.dismiss();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isRefresh || isNewMessage)query();
        if(isNewMessage || NewFriendManager.getInstance(getActivity()).hasNewFriendInvitation()){
            binding.imgNewMessage.setVisibility(View.VISIBLE);
        }else {
            binding.imgNewMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Appconfig.SUCCEE_BACK && requestCode == REQUEST_NEWFRIEND_CODE){
            isRefresh = true;
            showLog("onResult");
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
                            if(listUser.size() != list.size()){
                                isRefresh = true;
                                listUser = new ArrayList<>();//listUser.clear再添加会出现问题
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
                                isRefresh = false;
                            }
                            showLog("list="+list.size());
                        }else{
                            EventBus.getDefault().post(new NewFriendsEvent());
                            showLog("无好友");
                            isRefresh = false;
                        }
                    }
                }
        );
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        showLog("start");
//        if(isNeedRefresh){
//            dialog.show();
//            query();
//        }
//    }

    @Subscribe(threadMode  = ThreadMode.MAIN)
    public void getFriends(NewFriendsEvent event){
        adapter.addAll(listUser);
        if(dialog.isShowing())dialog.dismiss();
        App.getInstance().setListFriends(listUser);//缓存好友到App中
    }

    private Dialog showDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示").setMessage("正在获取好友列表");
        return builder.create();
    }

    private Dialog showDeleteDialog(String userName,final User friendUser){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示").setMessage("是否删除:"+userName).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.create().dismiss();
                return;
            }
        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteUser(friendUser);
            }
        });
        return builder.create();
    }

    private void deleteUser(final User friendUser){
        isShow = true;
        User user = UserModel.getModelInstance().getCurrentUser();
        Friend friend = new Friend();
        friend.setUser(user);
        friend.setFriendUser(friendUser);
        friend.setObjectId(friendUser.getFriendsId());
        UserModel.getModelInstance().deleteFriend(friend, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    if(isShow){
                        Toast.makeText(getActivity(),"好友删除成功",Toast.LENGTH_SHORT).show();
                        adapter.remove(friendUser);
                        isShow = false;
                    }
                } else {
                    Toast.makeText(getActivity(),"好友删除失败"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
