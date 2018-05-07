package magic.cn.health.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import magic.cn.health.R;
import magic.cn.health.adapter.GridViewAdapter;
import magic.cn.health.adapter.SmileAdapter;
import magic.cn.health.app.App;
import magic.cn.health.config.Appconfig;
import magic.cn.health.databinding.ActivityCommitRunBinding;
import magic.cn.health.event.KeyboardEvent;
import magic.cn.health.model.RunCommitModel;
import magic.cn.health.ui.view.EmoticonsEditText;
import magic.cn.health.utils.FaceText;
import magic.cn.health.utils.FaceTextUtils;

/**
 * 提交运动信息界面
 * @author 林思旭
 * @since 2018/4/16
 */

public class CommitRunActivity extends BaseActivity {

    private int txtContentSize = 0;

    private final int MAX_INPUT = 140;

    private int input_length = 0;

    private KeyboardEvent keyEvent;

    private List<FaceText> faceList;

    ActivityCommitRunBinding binding;

    private EmoticonsEditText edt_msg;

    private Dialog dialog;

    private String location = null;

    @Override
    protected void initBind() {
        binding = DataBindingUtil.setContentView(this,R.layout.activity_commit_run);

        edt_msg = binding.editMsg;
    }

    @Override
    protected void initView() {

        //默认进来是打开键盘
        keyEvent = new KeyboardEvent(Appconfig.TYPE_OPEN_KEYBOARD);

        initFace();

        binding.setPresenter(this);

        dialog = showDialog("正在提交");

        edt_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                txtContentSize = start + after;//获取内容变化前总体内容个数
                if(input_length >= 0){
                    input_length = MAX_INPUT - txtContentSize;
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count != 0){
                    txtContentSize = count + txtContentSize;
                }
                if(before != 0){
                    txtContentSize = txtContentSize - before;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.txtRemindSize.setText(input_length+"/140");
            }
        });

        edt_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(keyEvent.getCurrentType() == Appconfig.TYPE_OEPN_SMILE){
                    keyEvent.setWantType(Appconfig.TYPE_OPEN_KEYBOARD_AND_CLOSE_SMILE);
                    EventBus.getDefault().post(keyEvent);
                }
            }
        });

        binding.imgCommitCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.imgSmile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(keyEvent.getCurrentType() == Appconfig.TYPE_OPEN_KEYBOARD){
                    keyEvent.setWantType(Appconfig.TYPE_OPEN_SMILE_AND_CLOSE_KEYBOARD);
                    EventBus.getDefault().post(keyEvent);
                }
            }
        });


    }

    @Override
    protected void destoryView() {
//        if(mLocationClient.isStarted())mLocationClient.stop();
    }

    @Override
    protected void toolBarBackListener(View view) {

    }

    /**
     * 初始化表情界面
     */
    private void initFace() {
        faceList = FaceTextUtils.faceTexts;
        List<View> views = new ArrayList<View>();
        // 添加每个View的内容
        for (int i = 0; i < 2; i++) {
            views.add(getGridView(i));
        }
        SmileAdapter Viewadapter = new SmileAdapter(
                CommitRunActivity.this, views);
        binding.viewPagerSpeakFace.setAdapter(Viewadapter);
    }

    /**
     *
     * @param index：表情页面的页数
     * @return
     */
    private View getGridView(int index){
        View faceView = View.inflate(this, R.layout.include_emo_gridview, null);
        GridView gridview = (GridView)faceView.findViewById(R.id.gridview);
        List<FaceText> list = new ArrayList<FaceText>();
        //区分两个版
        if(index == 0){
            list.addAll(faceList.subList(0, 21));
        }else{
            list.addAll(faceList.subList(21 , faceList.size()));
        }
        final GridViewAdapter gridAdapter = new GridViewAdapter(this, list);
        gridview.setAdapter(gridAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                FaceText faceText = (FaceText) gridAdapter.getItem(position);
                String key = faceText.text.toString();
                try {
                    if (edt_msg != null && !TextUtils.isEmpty(key)) {
                        //插入特殊的字符串
                        int start = edt_msg.getSelectionStart();//可以试试End结尾的
                        CharSequence content = edt_msg.getText().insert(start, key);
                        edt_msg.setText(content);
                        //定位光标的位置
                        CharSequence info = edt_msg.getText();
                        if (info instanceof Spannable) {
                            Spannable spanText = (Spannable) info;
                            Selection.setSelection(spanText, start + key.length());
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });
        return faceView;

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventCommit(KeyboardEvent event){
        if(event.getWantType() == Appconfig.TYPE_OPEN_SMILE_AND_CLOSE_KEYBOARD &&
                event.getCurrentType() == Appconfig.TYPE_OPEN_KEYBOARD){
            hideSoftInputView();
            new CommitThread().start();
        }else if(event.getCurrentType() == Appconfig.TYPE_DELAY_SHOW_SMILE){
            binding.viewPagerSpeakFace.setVisibility(View.VISIBLE);
            keyEvent.setCurrentType(Appconfig.TYPE_OEPN_SMILE);
        }else if(event.getCurrentType() == Appconfig.TYPE_OEPN_SMILE &&
                event.getWantType() == Appconfig.TYPE_OPEN_KEYBOARD_AND_CLOSE_SMILE){
            binding.viewPagerSpeakFace.setVisibility(View.GONE);
            keyEvent.setCurrentType(Appconfig.TYPE_OPEN_KEYBOARD);
            showSoftInputView();
        }
    }

    class CommitThread extends Thread{
        @Override
        public void run() {
            try {
                sleep(60);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(keyEvent.getCurrentType() == Appconfig.TYPE_OPEN_KEYBOARD &&
                    keyEvent.getWantType() == Appconfig.TYPE_OPEN_SMILE_AND_CLOSE_KEYBOARD){
                keyEvent.setWantType(Appconfig.TYPE_OEPN_SMILE);
                keyEvent.setCurrentType(Appconfig.TYPE_DELAY_SHOW_SMILE);
                EventBus.getDefault().post(keyEvent);
            }
        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this
                .getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (CommitRunActivity.this.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (CommitRunActivity.this.getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(CommitRunActivity.this
                                .getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软键盘
     */
    public void showSoftInputView() {
        if (CommitRunActivity.this.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
            if (CommitRunActivity.this.getCurrentFocus() != null)
                ((InputMethodManager) (CommitRunActivity.this)
                        .getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .showSoftInput(edt_msg, 0);
        }
    }


    public void commitMsg(View view){
        if(TextUtils.isEmpty(edt_msg.getText())){
            showToast("内容不能为空");
            return;
        }
        dialog.show();
        final String content = edt_msg.getText().toString();
        if(App.getInstance().getLocationEvent()!= null){
            location = App.getInstance().getLocationEvent().getLocationDescribe();
            if(TextUtils.isEmpty(location))location = "";
        }
        RunCommitModel.getInstance().addCommitMsg(content,location, new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                dialog.dismiss();
                if(e == null){
                    Intent intent = new Intent();
                    intent.putExtra("msg",content);
                    intent.putExtra("location",location);
                    setResult(Appconfig.SUCCEE_BACK,intent);
                    finish();
                }else{
                    showToast("提交失败");
                }
            }
        });
    }
}
