package magic.cn.health.ui.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

import java.io.File;

import magic.cn.health.R;
import magic.cn.health.config.Appconfig;
import magic.cn.health.databinding.ActivityChoosephotoBinding;
import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

/**
 * @author 林思旭
 * @since 2018/3/10
 */

public class ChoosePhotoActivity extends BaseActivity {

    ActivityChoosephotoBinding binding;


    CompositeSubscription subscription = new CompositeSubscription();
    @Override
    protected void initBind() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_choosephoto);

//        binding.setPresenter(this);

        binding.imgDecide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rightClick();
            }
        });

        binding.imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leftClick();
            }
        });
    }

    @Override
    protected void initView() {

        Uri uri = getIntent().getData();

        binding.cropView.extensions()
                .load(uri.toString());

    }

    @Override
    protected void destoryView() {
        subscription = null;
    }

    @Override
    protected void toolBarBackListener(View view) {

    }

    public void leftClick(){
        finish();
        showToast("取消选择");
    }

    public void rightClick(){
        final File croppedFile = new File(getCacheDir(), "cropped.jpg");
        Observable<Void> onSave = Observable.from(binding.cropView.extensions()
                .crop()
                .quality(87)
                .format(Bitmap.CompressFormat.JPEG)
                .into(croppedFile))
                .subscribeOn(io())
                .observeOn(mainThread());
        subscription.add(onSave.subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent intent = getIntent();
                intent.setData(Uri.fromFile(croppedFile));
                showLog("uri="+Uri.fromFile(croppedFile));
                ChoosePhotoActivity.this.setResult(Appconfig.REQUESTCODE_CHOOSE_PHOTO,intent);
                finish();
            }
        }));
    }

}
