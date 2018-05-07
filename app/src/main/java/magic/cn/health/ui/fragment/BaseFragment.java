package magic.cn.health.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import magic.cn.health.utils.MyLog;

/**
 * @author 林思旭
 * @since 2018/3/8
 */

public abstract class BaseFragment extends Fragment {
    private String TAG = "";

    private ViewGroup rootView = null;

//    protected boolean isVisible;
//
//    protected boolean isPrepared = false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null){

            rootView = (ViewGroup)initBinding(inflater,container);

            initView();
        }

        return rootView;
    }

    /**
     * 初始化binding
     * @param inflater
     * @param container
     * @return
     */
    protected abstract View initBinding(LayoutInflater inflater,ViewGroup container);

    /**
     * 初始化控件
     */
    protected abstract void initView();


    /**
     * 实现该方法，并在调用viewModel 的 destroy函数
     */
    protected abstract void destroyView();


    @Override
    public void onDestroy() {
        destroyView();
        super.onDestroy();
    }


    public void showLog(String content){
        MyLog.i(TAG,content);
    }

    /**启动指定Activity
     * @param target
     * @param bundle
     */
    public void startActivity(Class<? extends Activity> target, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), target);
        if (bundle != null)
            intent.putExtra(getActivity().getPackageName(), bundle);
        getActivity().startActivity(intent);
    }


    public Dialog showDialog(String remind){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示").setMessage(remind);
        return builder.create();
    }
}
