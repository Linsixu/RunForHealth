package magic.cn.health.ui.activity;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.view.View;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import magic.cn.health.R;
import magic.cn.health.bean.User;
import magic.cn.health.databinding.ActivityUpdateInfoBinding;
import magic.cn.health.model.UserModel;

/**
 * @author 林思旭
 * @since 2018/5/12
 */

public class UpdateInfoActivity extends BaseActivity {

    ActivityUpdateInfoBinding binding;

    private Dialog dialog;

    private User user;

    private String info_flag = null;
    @Override
    protected void initBind() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_info);
    }

    @Override
    protected void initView() {

        info_flag = getIntent().getStringExtra("info_flag");
        if(info_flag!=null)binding.txtUpdateContent.setText("修改"+info_flag);
        dialog = showDialog("正在提交数据");

        user = UserModel.getModelInstance().getCurrentUser();

        binding.txtFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                String content = binding.editUpdate.getText().toString();
                if(info_flag!=null){
                    if(info_flag.equals("名字")){
                        user.setNick(content);
                    }else if(info_flag.equals("个性签名")){
                        user.setSignature(content);
                    }else if(info_flag.equals("身高")){
                        user.setHeight(content);
                    }else if(info_flag.equals("体重")){
                        user.setWeight(content);
                    }
                    updateUserInfo(user);
                }
            }
        });

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void destoryView() {

    }

    @Override
    protected void toolBarBackListener(View view) {

    }

    private void updateUserInfo(final User user){
        user.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(dialog.isShowing())dialog.dismiss();
                if(e == null){
                    finish();
                }else{
                    showToast("设置失败");
                }
            }
        });
    }
}
