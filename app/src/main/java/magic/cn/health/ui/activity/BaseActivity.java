package magic.cn.health.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import magic.cn.health.utils.MyLog;

/**
 * @author 林思旭
 * @since 2018/3/8
 */

public abstract class BaseActivity extends AppCompatActivity {

    private String TAG = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = this.getClass().getSimpleName();


        initBind();


        initView();

        destoryView();
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

    public void initToolBar(Toolbar toolbar, String title){
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolBarBackListener(view);
            }
        });
    }

}
