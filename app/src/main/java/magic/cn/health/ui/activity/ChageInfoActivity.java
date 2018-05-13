package magic.cn.health.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import magic.cn.health.R;
import magic.cn.health.bean.User;
import magic.cn.health.config.Appconfig;
import magic.cn.health.databinding.ActivityChangeInfoBinding;
import magic.cn.health.model.UserModel;

/**
 * @author 林思旭
 * @since 2018/5/11
 */

public class ChageInfoActivity extends BaseActivity {
    ActivityChangeInfoBinding binding;

    private Uri uri = null;

    private Dialog dialog;
    @Override
    protected void initBind() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_info);
    }

    @Override
    protected void initView() {

        initTitle("修改个人信息");

        User user = UserModel.getModelInstance().getCurrentUser();
        binding.setUser(user);

        dialog = showDialog("正在修改");
        binding.imgUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, Appconfig.REQUESTCODE_TAKE_CAMERA);
            }
        });

        binding.layoutNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChageInfoActivity.this,UpdateInfoActivity.class);
                intent.putExtra("info_flag","名字");
                startActivityForResult(intent,Appconfig.INFO_CHAGE_NICK);
            }
        });
        binding.layoutSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChageInfoActivity.this,UpdateInfoActivity.class);
                intent.putExtra("info_flag","个性签名");
                startActivityForResult(intent,Appconfig.INFO_CHAGE_SIGNATURE);
            }
        });
        binding.layoutHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChageInfoActivity.this,UpdateInfoActivity.class);
                intent.putExtra("info_flag","身高");
                startActivityForResult(intent,Appconfig.INFO_CHAGE_HEIGHT);
            }
        });
        binding.layoutWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChageInfoActivity.this,UpdateInfoActivity.class);
                intent.putExtra("info_flag","体重");
                startActivityForResult(intent,Appconfig.INFO_CHAGE_WEIGHT);
            }
        });
    }

    @Override
    protected void destoryView() {
        if(dialog.isShowing())dialog.dismiss();
    }

    @Override
    protected void toolBarBackListener(View view) {
        finish();
    }

    private void initTitle(String title){
        initToolBar(binding.toolbar.toolbar,"",true);
        binding.toolbar.txtTitle.setText(title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null){
            uri = data.getData();
        }
        switch (requestCode){
            case Appconfig.REQUESTCODE_TAKE_CAMERA:
                if(resultCode == Activity.RESULT_OK){
                    Intent intent = new Intent(ChageInfoActivity.this,ChoosePhotoActivity.class);
                    intent.setData(uri);
                    startActivityForResult(intent,Appconfig.REQUESTCODE_CHOOSE_PHOTO);
                }
                break;
            case Appconfig.REQUESTCODE_CHOOSE_PHOTO:
                if(resultCode == requestCode){
                    if(uri != null){
                        dialog.show();
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
                                User user = UserModel.getModelInstance().getCurrentUser();
                                user.setAvatar(url);
                                updateUserInfo(user);
                            }
                        });
                    }else {
                        showToast("头像设置失败");
                    }
                }
                break;
            default:
                User user = UserModel.getModelInstance().getCurrentUser();
                binding.setUser(user);
                break;

        }
    }

    private void updateUserInfo(final User user){
        user.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(dialog.isShowing())dialog.dismiss();
                if(e == null){
                    binding.setUser(user);
                }else{
                    showToast("设置失败");
                }
            }
        });
    }
}
