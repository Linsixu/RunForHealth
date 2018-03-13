package magic.cn.health.model;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

import magic.cn.health.bean.User;
import magic.cn.health.config.Appconfig;
import magic.cn.health.utils.MyLog;

/**
 * @author 林思旭
 * @since 2018/3/9
 */

public class RegisterModel implements Model {

    private Activity activity;
    private String TAG = "RegisterModel";

    public RegisterModel(Activity activity) {
        this.activity = activity;
    }

    public void registerFromBmob(User user){
        MyLog.i("RegisterModel","user="+user.toString());
//        UserModel.getModelInstance().register(user.getUsername(), user.getPwd(), user.getAgainPwd(), new LogInListener() {
//
//            @Override
//            public void done(Object o, BmobException e) {
//                MyLog.i("RegisterModel","e="+e);
//            }
//        });
    }
    @Override
    public void destroy() {

    }


    public void selectUserIcon(){
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, Appconfig.REQUESTCODE_TAKE_CAMERA);//2016.4.8注释掉
    }
}
