package magic.cn.health.ui.activity;

import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import magic.cn.health.R;
import magic.cn.health.bean.User;
import magic.cn.health.databinding.ActivityLoginBinding;
import magic.cn.health.model.UserModel;
import magic.cn.health.utils.BmobErrorCode;

/**
 * @author 林思旭
 * @since 2018/3/9
 */

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding binding;

    private TextView txtRegister;

    private User user;

    private UserModel model;
    @Override
    protected void initBind() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        txtRegister = binding.tvRegister;

        user = new User();

        model = new UserModel();

        binding.setUser(user);

    }

    @Override
    protected void initView() {
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(RegisterActivity.class);
            }
        });


        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(user.getUsername())){
                    showToast("请填写用户名");
                    return;
                }
                if(TextUtils.isEmpty(user.getPwd())){
                    showToast("请填写密码");
                    return;
                }else {
                    model.login(user, new SaveListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            if(user!=null){
                                startActivity(MainActivity.class);
                            }else {
                                showToast(BmobErrorCode.errorCodeConvertContent(e.getErrorCode()));
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void destoryView() {

    }

    @Override
    protected void toolBarBackListener(View view) {

    }

}
