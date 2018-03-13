package magic.cn.health.ui.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import magic.cn.health.R;
import magic.cn.health.adapter.SearchAdapter;
import magic.cn.health.bean.User;
import magic.cn.health.config.Appconfig;
import magic.cn.health.databinding.ActivitySearchUserBinding;
import magic.cn.health.model.BaseModel;
import magic.cn.health.model.UserModel;

/**
 * @author 林思旭
 * @since 2018/3/12
 */

public class SearchUserActivity extends BaseActivity {
    ActivitySearchUserBinding binding;

    private Toolbar toolbar;

    private User user = new User(),user2 = new User();

    private SearchAdapter adapter;

    private String searchUsername;

    @Override
    protected void initBind() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_user);

        toolbar = binding.toolbar.toolbar;

        binding.recycleViewSearch.setLayoutManager(new LinearLayoutManager(this));
        initTitle("添加好友");

//        user.setNick("林思旭");
//        user2.setNick("林泽伟");
//        user2.setSex(false);

        adapter = new SearchAdapter(this);

//        adapter.add(user);
//        adapter.add(user2);
        binding.setSearchName(searchUsername);

        binding.setPresenter(this);

        binding.recycleViewSearch.setAdapter(adapter);

        adapter.setListener(new SearchAdapter.onItemClickListener() {
            @Override
            public void onClick(View view) {
                showToast("添加好友");
            }
        });
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void destoryView() {

    }

    @Override
    protected void toolBarBackListener(View view) {

    }

    private void initTitle(String title){
        initToolBar(toolbar,"",false);
        binding.toolbar.txtTitle.setText(title);
    }

    public void queryUser(final String name){
        showLog("name="+name);
//        final List<User> listUser = new ArrayList<>();
        adapter.deleteAll();
        UserModel.getModelInstance().queryUsers(name, BaseModel.DEFAULT_LIMIT, new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                showLog("list.size="+list.size());
                if(list.size() == 0 && e!= null &&e.getMessage().equals(Appconfig.NOT_ADD_USER_SELF)){
                    showToast("不能添加自己");
                    return;
                }
                if(list.size() == 0) {
                    showToast("查无此人");
                    return;
                }
                if(e == null){
                    adapter.addAll(list);
                }
            }
        });
    }
}
