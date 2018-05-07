package magic.cn.health.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import magic.cn.health.R;
import magic.cn.health.adapter.RunMsgAdapter;
import magic.cn.health.app.App;
import magic.cn.health.bean.RunMessage;
import magic.cn.health.bean.User;
import magic.cn.health.config.Appconfig;
import magic.cn.health.databinding.FragmentRunBinding;
import magic.cn.health.model.RunCommitModel;
import magic.cn.health.model.UserModel;
import magic.cn.health.ui.activity.CommitRunActivity;


/**
 * @author 林思旭
 * @since 2018/3/6
 */

public class RunFragment extends BaseFragment {

    FragmentRunBinding binding;


    private final int COMMIT_BACK_CODE = 1;

    private RunMsgAdapter adapter;

    private Dialog dialog;

    @Override
    protected View initBinding(LayoutInflater inflater, ViewGroup container) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_run,container,false);

        return binding.getRoot();
    }

    @Override
    protected void initView() {

        adapter = new RunMsgAdapter(getActivity());

        binding.recycleViewRun.setLayoutManager(new LinearLayoutManager(getActivity()));

        binding.recycleViewRun.setAdapter(adapter);

        dialog = showDialog("正在加载");

        queryMsg();

        queryUserFriends();

        binding.actionButtonCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CommitRunActivity.class);
//                getActivity().startActivity(intent);
                startActivityForResult(intent,COMMIT_BACK_CODE);
            }
        });

        binding.refreshLayoutRun.setColorSchemeResources(R.color.colorPrimary, R.color.primaryColor);

        binding.refreshLayoutRun.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryMsg();
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showLog("Result="+requestCode+","+resultCode);
        if(resultCode == Appconfig.SUCCEE_BACK && requestCode == COMMIT_BACK_CODE){
            //将用户发表的展示出来
            String msg = data.getStringExtra("msg");

            String location = data.getStringExtra("location");

            showLog("msg="+msg);

            adapter.addMsgToHead(new RunMessage(BmobUser.getCurrentUser(User.class),
                    msg,location));

        }
    }

    @Override
    protected void destroyView() {

    }

    private void queryMsg(){
        binding.refreshLayoutRun.setRefreshing(true);
        RunCommitModel.getInstance().queryCommitMsg(new FindListener<RunMessage>() {
            @Override
            public void done(List<RunMessage> list, BmobException e) {
                binding.refreshLayoutRun.setRefreshing(false);
                if(e == null){
                    adapter.addAll(list);
                }else{
                    Toast.makeText(getActivity(),"加载失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void queryUserFriends(){
        if(App.getInstance().getListFriends().size() == 0){
            dialog.show();
            UserModel.getModelInstance().queryFriends(
                    new FindListener<User>() {
                        @Override
                        public void done(List<User> list, BmobException e) {
                            if (e == null && list.size() != 0) {
                                App.getInstance().setListFriends(list);
                            }else{
                                showLog("无好友");
                            }
                            dialog.dismiss();
                        }
                    }
            );
        }else{
            return;
        }
    }
}
