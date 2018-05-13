package magic.cn.health.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import magic.cn.health.R;
import magic.cn.health.bean.User;
import magic.cn.health.databinding.ActivityRegisterBinding;
import magic.cn.health.utils.PhoneFormatCheckUtils;

/**
 * @author 林思旭
 * @since 2018/3/9
 */

public class RegisterActivity extends BaseActivity {

    ActivityRegisterBinding databinding;
    private User user;
    @Override
    protected void initBind() {

        databinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
    }

    @Override
    protected void initView() {
        user = new User();

        databinding.setUser(user);

        databinding.btnNextstep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(user.getUsername())){
                    showToast("请填写用户名");
                    return;
                }else{
                    if(!PhoneFormatCheckUtils.isChinaPhoneLegal(user.getUsername())){
                        showToast("请输入合法手机号码");
                        return;
                    }
                }
                if(TextUtils.isEmpty(user.getPwd())){
                    showToast("请填写密码");
                    return;
                }
                if(TextUtils.isEmpty(user.getAgainPwd())){
                    showToast("请填写确认密码");
                    return;
                }
                if(!user.getPwd().equals(user.getAgainPwd())){
                    showToast("两次输入的密码不一致，请重新输入");
                    return;
                }
                jumpToRegister();

            }
        });


    }

    @Override
    protected void destoryView() {

    }

    @Override
    protected void toolBarBackListener(View view) {

    }

    public void jumpToRegister(){
            Bundle data = new Bundle();
            data.putString("username",user.getUsername());
            data.putString("password",user.getPwd());

            Intent intent = new Intent(RegisterActivity.this,SetInfoActivity.class);
            intent.putExtra("data",data);
            startActivity(intent);
    }


}
