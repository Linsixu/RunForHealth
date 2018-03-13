package magic.cn.health.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UploadFileListener;
import magic.cn.health.R;
import magic.cn.health.bean.User;
import magic.cn.health.config.Appconfig;
import magic.cn.health.databinding.ActivitySetinfoBinding;
import magic.cn.health.model.UserModel;
import magic.cn.health.utils.ActivityCollector;
import magic.cn.health.utils.CommonUtils;

/**
 * @author 林思旭
 * @since 2018/3/10
 */

public class SetInfoActivity extends BaseActivity{
    ActivitySetinfoBinding binding;

    private String userName,password;

    private User user = new User();

    private UserModel userModel;

    @Override
    protected void initBind() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setinfo);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        userName = bundle.getString("username");
        password = bundle.getString("password");

        binding.setUser(user);
        binding.setPresenter(this);

        userModel = new UserModel();
    }

    @Override
    protected void initView() {

//        if(TextUtils.isEmpty(user.getNick().trim())){
//            showToast("请输入您的昵称!");
//            return;
//        }

        /**
         * 选择头像
         */
        binding.ivSetPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, Appconfig.REQUESTCODE_TAKE_CAMERA);//2016.4.8注释掉
            }
        });
    }

    @Override
    protected void destoryView() {

    }

    @Override
    protected void toolBarBackListener(View view) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = null;
        if(data != null){
            uri = data.getData();
        }
        if(requestCode == Appconfig.REQUESTCODE_TAKE_CAMERA
                &&resultCode == Activity.RESULT_OK){
            Intent intent = new Intent(SetInfoActivity.this,ChoosePhotoActivity.class);
            intent.setData(uri);
            startActivityForResult(intent,Appconfig.REQUESTCODE_CHOOSE_PHOTO);
        }else if(requestCode == Appconfig.REQUESTCODE_CHOOSE_PHOTO && resultCode == requestCode){
            if(uri != null){
                binding.ivSetPicture.setImageURI(uri);
                final BmobFile bmobFile = new BmobFile(new File(uri.getPath()));
                bmobFile.upload(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e != null){
                            showLog(e+"");
                            return;
                        }
                        showLog(e+"");
                        String url = bmobFile.getFileUrl();
                        showLog("url="+url);
                        mApplication.getSharedPreferencesUtil().setAvatarUrl(url);
                    }
                });
            }else {
                showToast("头像设置失败");
            }
        }
    }

    public void register(View view) {


        boolean isMan = true;

        if(TextUtils.isEmpty(user.getNick())){
            showToast("请输入您的昵称!");
            return;
        }

        boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
        if(!isNetConnected){
            showToast("当前网络不可用，请检查您的网络设置");
            return;
        }


        final ProgressDialog progressDialog = new ProgressDialog(SetInfoActivity.this);
        progressDialog.setMessage("正在注册");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        user.setUsername(userName);
        user.setPassword(password);
        user.setPwd(password);
        user.setAvatar(mApplication.getSharedPreferencesUtil().getAvatarUrl());

        showLog("user="+user.toString());

        userModel.register(user, new LogInListener() {
            @Override
            public void done(Object o, BmobException e) {
                if(e == null){
                    progressDialog.dismiss();
                    showToast("注册成功");
                    Intent intent = new Intent(SetInfoActivity.this,MainActivity.class);
                    startActivity(intent);
                    ActivityCollector.finishAll();
                }else if(e.getErrorCode() == 202) {
                    showToast("该用户名已被使用");
                    progressDialog.dismiss();
                    Intent intent = new Intent(SetInfoActivity.this,RegisterActivity.class);
                    startActivity(intent);
                    ActivityCollector.removeActivity(SetInfoActivity.this);
                }else{
                    showToast("不知名错误，请重试");
                }
                showLog("e="+e);
            }
        });
    }
}
