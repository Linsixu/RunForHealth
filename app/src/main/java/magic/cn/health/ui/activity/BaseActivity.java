package magic.cn.health.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import magic.cn.health.app.App;
import magic.cn.health.utils.ActivityCollector;
import magic.cn.health.utils.MyLog;

/**
 * @author 林思旭
 * @since 2018/3/8
 */

public abstract class BaseActivity extends AppCompatActivity {

    private String TAG = "";
    public App mApplication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = this.getClass().getSimpleName();

        ActivityCollector.addActivity(this);

        mApplication = App.getInstance();

        initBind();


        initView();
    }



    /**
     * 用于初始化bind
     */
    protected abstract void initBind();

    /**
     * 初始化布局
     */
    protected abstract void initView();

    /**
     * 活动销毁时候调用
     */
    protected abstract void destoryView();

    /**
     * 用于ToolBar点击返回事件监听
     * @param view
     */
    protected abstract void toolBarBackListener(View view);

    public void showLog(String content){
        MyLog.i(TAG,content);
    }

    public void initToolBar(Toolbar toolbar, String title,boolean isOpenBack){
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(isOpenBack);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toolBarBackListener(view);
                }
            });
        }
        toolbar.setTitle(title);
    }

    public void startActivity(Class<?> activity){
        Intent intent = new Intent(this, activity);
        this.startActivity(intent);
    }

    public void showToast(String info){
        Toast.makeText(BaseActivity.this,info,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destoryView();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(Boolean empty){

    }

    public Bundle getBundle() {
        if (getIntent() != null && getIntent().hasExtra(getPackageName()))
            return getIntent().getBundleExtra(getPackageName());
        else
            return null;
    }

    public Dialog showDialog(String content){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示").setMessage(content);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
