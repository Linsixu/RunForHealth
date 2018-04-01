package magic.cn.health.ui.activity;

import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import magic.cn.health.R;
import magic.cn.health.adapter.ViewPagerAdapter;
import magic.cn.health.bean.User;
import magic.cn.health.databinding.ActivityMainBinding;
import magic.cn.health.event.AllRefreshEvent;
import magic.cn.health.event.RefreshEvent;
import magic.cn.health.model.UserModel;
import magic.cn.health.ui.fragment.BookFragment;
import magic.cn.health.ui.fragment.ChatFragment;
import magic.cn.health.ui.fragment.RunFragment;
import magic.cn.health.ui.fragment.SetFragment;
import magic.cn.health.utils.IMMLeaks;

public class MainActivity extends BaseActivity {

    ActivityMainBinding binding;


    private RunFragment runFragment = new RunFragment();
    private ChatFragment chatFragment = new ChatFragment();
    private SetFragment setFragment = new SetFragment();
    private BookFragment bookFragment = new BookFragment();

    private LinearLayout layoutBook,layoutChat,layoutNearBy,layoutSet;

    private List<Fragment> list = new ArrayList<>();

    private ViewPager viewPager;

    private ViewPagerAdapter adapter = null;

    private Toolbar toolbar;
    @Override
    protected void initBind() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        layoutBook = binding.layoutButtom.layoutBook;
        layoutChat = binding.layoutButtom.layoutChat;
        layoutNearBy = binding.layoutButtom.layoutNearby;
        layoutSet = binding.layoutButtom.layoutSet;

        viewPager = binding.viewPagerMain;

        toolbar = binding.toolbar.toolbar;

    }

    @Override
    protected void initView() {

        list.add(runFragment);
        list.add(chatFragment);
        list.add(bookFragment);
        list.add(setFragment);

        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();

        adapter = new ViewPagerAdapter(manager,list);

        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(0);
        initRunFragment();


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
//                        initRunFragment();
                        initRunFragment();
                        break;
                    case 1:
                        initChatFragment();
                        break;
                    case 2:
                        initBookFragment();
                        break;
                    case 3:
                        initSetFragment();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        layoutNearBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(0);
                initTitle("约跑");
//                Toast.makeText(MainActivity.this,"点击通讯录",Toast.LENGTH_SHORT).show();
            }
        });

        layoutChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
                initTitle("聊天");
//                Toast.makeText(MainActivity.this,"群聊",Toast.LENGTH_SHORT).show();
            }
        });

        layoutBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(2);
                initTitle("通讯录");
//                Toast.makeText(MainActivity.this,"点击通讯录",Toast.LENGTH_SHORT).show();
            }
        });

        layoutSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(3);
                initTitle("我");
//                Toast.makeText(MainActivity.this,"点击设置",Toast.LENGTH_SHORT).show();
            }
        });

        final User user = BmobUser.getCurrentUser(User.class);

        //判断用户是否登录，并且连接状态不是已连接，则进行连接操作
        if (!TextUtils.isEmpty(user.getObjectId()) &&
                BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                        //TODO 会话：2.7、更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                        BmobIM.getInstance().
                                updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                        user.getUsername(), user.getAvatar()));
                        EventBus.getDefault().post(new AllRefreshEvent());
                    } else {
                        showLog(e.getMessage());
                    }
                }
            });
            //TODO 连接：3.3、监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
            BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                @Override
                public void onChange(ConnectionStatus status) {
//                    showToast(status.getMsg());
                    showLog(BmobIM.getInstance().getCurrentStatus().getMsg());
                }
            });
        }
        //解决leancanary提示InputMethodManager内存泄露的问题
        IMMLeaks.fixFocusedViewLeak(getApplication());
    }

    @Override
    protected void destoryView() {
        //清理导致内存泄露的资源
        BmobIM.getInstance().clear();
        UserModel.getModelInstance().loginOut();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void toolBarBackListener(View view) {

    }

    private void initRunFragment(){
        layoutBook.setSelected(false);
        layoutChat.setSelected(false);
        layoutNearBy.setSelected(true);
        layoutSet.setSelected(false);
        initTitle("约跑");
    }


    private void initChatFragment(){
        layoutBook.setSelected(false);
        layoutChat.setSelected(true);
        layoutNearBy.setSelected(false);
        layoutSet.setSelected(false);
        initTitle("聊天");
    }

    private void initBookFragment(){
        layoutBook.setSelected(true);
        layoutChat.setSelected(false);
        layoutNearBy.setSelected(false);
        layoutSet.setSelected(false);
        initTitle("通讯录");
    }


    private void initSetFragment(){
        layoutBook.setSelected(false);
        layoutChat.setSelected(false);
        layoutNearBy.setSelected(false);
        layoutSet.setSelected(true);
        initTitle("我");
    }


    private void hideFragment(android.support.v4.app.FragmentTransaction transaction){
        if(runFragment != null){
            transaction.hide(runFragment);
        }

        if(chatFragment != null){
            transaction.hide(chatFragment);
        }
        if(bookFragment != null){
            transaction.hide(bookFragment);
        }
        if(setFragment != null){
            transaction.hide(setFragment);
        }
    }

    private void initTitle(String title){
        initToolBar(toolbar,"",false);
        binding.toolbar.txtTitle.setText(title);
    }
    /**
     * 注册自定义消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshEvent event) {
        //重新刷新列表
        showLog("---Main---");
        bookFragment.setNewMessage(true);
//        bookFragment.setNeedRefresh(true);
    }

}
