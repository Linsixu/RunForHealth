package magic.cn.health.ui.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cn.bmob.v3.exception.BmobException;
import magic.cn.health.R;
import magic.cn.health.adapter.NewFriendAdapter;
import magic.cn.health.config.Appconfig;
import magic.cn.health.databinding.ActivityNewFriendsBinding;
import magic.cn.health.db.NewFriendManager;

/**
 * @author 林思旭
 * @since 2018/3/14
 */

public class NewFriendsActivity extends BaseActivity {

    ActivityNewFriendsBinding binding;

    Toolbar toolbar;

    private NewFriendAdapter adapter;

    private RecyclerView recyclerView;
    @Override
    protected void initBind() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_friends);

        toolbar = binding.toolbar.toolbar;

        binding.recyclerViewNewFriends.setLayoutManager(new LinearLayoutManager(this));

        initTitle("新朋友");
    }

    @Override
    protected void initView() {
        adapter = new NewFriendAdapter(this);

        adapter.addAll(NewFriendManager.getInstance(this).getAllNewFriend());

        binding.recyclerViewNewFriends.setAdapter(adapter);

        adapter.setItemAgreeListener(new NewFriendAdapter.ItemAgreeListener() {
            @Override
            public void done(Object o, BmobException e) {
                if(e != null){
                    showToast("添加好友失败:"+e.getMessage());
                }
            }
        });

        //批量更新未读未认证的消息为已读状态
        NewFriendManager.getInstance(this).updateBatchStatus();
    }

    @Override
    protected void destoryView() {

    }

    @Override
    protected void toolBarBackListener(View view) {
        setResult(Appconfig.SUCCEE_BACK);
        finish();
    }
    private void initTitle(String title){
        initToolBar(toolbar,"",true);
        binding.toolbar.txtTitle.setText(title);
    }
}
