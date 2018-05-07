package magic.cn.health.ui.activity;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import magic.cn.health.R;
import magic.cn.health.adapter.RunMsgAdapter;
import magic.cn.health.bean.RunMessage;
import magic.cn.health.databinding.ActivitySelfMsgBinding;
import magic.cn.health.model.RunCommitModel;

/**
 * @author 林思旭
 * @since 2018/4/28
 */

public class SelfMsgActivity extends BaseActivity {

    ActivitySelfMsgBinding binding;

    private RunMsgAdapter adapter = null;

    private Dialog dialog = null;

    @Override
    protected void initBind() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_self_msg);
    }

    @Override
    protected void initView() {
        initToolBar(binding.toolbar.toolbar,"",true);
        binding.toolbar.txtTitle.setText("发起运动");

        binding.recycleViewSelfMsg.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RunMsgAdapter(this);

        binding.recycleViewSelfMsg.setAdapter(adapter);

        dialog = showDialog("正在获取数据");

        querySelfMsg();

        binding.refreshLayoutSelf.setColorSchemeResources(R.color.primaryColor,R.color.colorPrimary);

        binding.refreshLayoutSelf.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                querySelfMsg();
            }
        });

    }

    @Override
    protected void destoryView() {

    }

    @Override
    protected void toolBarBackListener(View view) {
        finish();
    }


    public void querySelfMsg(){
        binding.refreshLayoutSelf.setRefreshing(true);
        dialog.show();
        RunCommitModel.getInstance().querySelfCommitMsg(new FindListener<RunMessage>() {
            @Override
            public void done(List<RunMessage> list, BmobException e) {
                binding.refreshLayoutSelf.setRefreshing(false);
                if(e == null){
                    adapter.addAll(list);
                    if(dialog.isShowing())dialog.dismiss();
                }else{
                    showToast("加载失败");
                }
            }
        });
    }
}
