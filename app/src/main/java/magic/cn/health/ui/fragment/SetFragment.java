package magic.cn.health.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import magic.cn.health.R;
import magic.cn.health.bean.User;
import magic.cn.health.databinding.FragmentSetBinding;
import magic.cn.health.model.UserModel;
import magic.cn.health.ui.activity.LoginActivity;
import magic.cn.health.utils.ActivityCollector;


/**
 * @author 林思旭
 * @since 2018/3/6
 */

public class SetFragment extends BaseFragment {

    FragmentSetBinding binding;

    private User user;
    @Override
    protected View initBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_set,container,false);

        user = UserModel.getModelInstance().getCurrentUser();

        binding.setPresenter(this);

        binding.setUser(user);

        return binding.getRoot();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void destroyView() {

    }

    public void exitApp(View view){
        showDialog().show();
    }


    private Dialog showDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示").setMessage("是否退出登陆").setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.create().dismiss();
                return;
            }
        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCollector.finishAll();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                UserModel.getModelInstance().loginOut();
            }
        });
        return builder.create();
    }
}
