package magic.cn.health.ui.activity;

import android.databinding.DataBindingUtil;
import android.view.View;

import magic.cn.health.R;
import magic.cn.health.databinding.ActivityAboutUsBinding;

/**
 * @author 林思旭
 * @since 2018/5/11
 */

public class AboutUsActivity extends BaseActivity {

    ActivityAboutUsBinding binding;


    @Override
    protected void initBind() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_us);
    }

    @Override
    protected void initView() {
        initToolBar(binding.toolbar.toolbar,"",true);
    }

    @Override
    protected void destoryView() {

    }

    @Override
    protected void toolBarBackListener(View view) {
        finish();
    }
}
