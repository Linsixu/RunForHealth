package magic.cn.health.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMAudioMessage;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMLocationMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.BmobRecordManager;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.newim.listener.OnRecordChangeListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.exception.BmobException;
import magic.cn.health.R;
import magic.cn.health.adapter.ChatAdapter;
import magic.cn.health.adapter.GridViewAdapter;
import magic.cn.health.adapter.SmileAdapter;
import magic.cn.health.app.App;
import magic.cn.health.bean.User;
import magic.cn.health.config.Appconfig;
import magic.cn.health.databinding.ActivityChatBinding;
import magic.cn.health.event.KeyboardEvent;
import magic.cn.health.event.LocationEvent;
import magic.cn.health.ui.view.VoiceDialog;
import magic.cn.health.utils.ActivityCollector;
import magic.cn.health.utils.CommonUtils;
import magic.cn.health.utils.FaceText;
import magic.cn.health.utils.FaceTextUtils;
import magic.cn.health.utils.RealPathFromUriUtils;

/**
 * @author 林思旭
 * @since 2018/3/18
 */

public class ChatActivity extends BaseActivity implements MessageListHandler {

    ActivityChatBinding binding;

    private Button btn_send,btn_more;

    private LinearLayout layout_add;

    private User targetUser;

    private int inputNumber,input_length;

    private List<FaceText> faceList;

    private SmileAdapter smileAdapter;

    private EditText edt_msg;

    private KeyboardEvent keyEvent;

    private Button btn_smile;

    private MyThread myThread;
    private Toast toast = null;

    private BmobIMConversation mConversationManager;

    private ChatAdapter adapter;

    protected LinearLayoutManager layoutManager;

    private BmobRecordManager recordManager;

    private VoiceDialog voiceDialog;

    private final String record = "长按 录音";

    private final int CAMERA_STATE = 1;

    @Override
    protected void initBind() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);

        btn_send = binding.includeChatButtom.btnSend;

        btn_more = binding.includeChatButtom.btnMore;

        edt_msg = binding.includeChatButtom.editMsg;

        btn_smile = binding.includeChatButtom.btnSmile;

        layout_add = binding.includeChatAdd.layoutChatAdd;

//        targetUser = (User) getIntent().getSerializableExtra("user");
        BmobIMConversation conversationEntrance = (BmobIMConversation) getIntent().getSerializableExtra("c");

        //TODO 消息：5.1、根据会话入口获取消息管理，聊天页面
        mConversationManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);

        initTitle(mConversationManager.getConversationTitle());
    }

    @Override
    protected void initView() {

        keyEvent = new KeyboardEvent(Appconfig.TYPE_CLOSE);

        initFace();

        layoutManager = new LinearLayoutManager(this);
        binding.recyclerViewChatHistory.setLayoutManager(layoutManager);

//        BmobIMConversation conversationEntrance = (BmobIMConversation) getBundle().getSerializable("c");
//        //TODO 消息：5.1、根据会话入口获取消息管理，聊天页面
//        mConversationManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);

        adapter = new ChatAdapter(this,mConversationManager);

        binding.recyclerViewChatHistory.setAdapter(adapter);

        binding.refreshHistoryContent.setColorSchemeResources(R.color.colorPrimary, R.color.primaryColor);

        firstQueryMessages();

        //初始化dialog
        voiceDialog = new VoiceDialog(this,R.style.NotifyUserDiaolog);

        //初始化语音
        initRecordManager();

        binding.refreshHistoryContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BmobIMMessage msg = adapter.getFirstMessage();
                queryMessages(msg);
            }
        });

        binding.includeChatButtom.btnMicrophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                binding.includeChatButtom.editMsg.setHint(record);
//                binding.includeChatButtom.editMsg.setGravity(Gravity.CENTER);
//                showShortToast().show();
                voiceDialog.show();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
                    showToast("尚未连接IM服务器");
                    return;
                }
                sendMessage();
            }
        });


        btn_smile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(keyEvent.getCurrentType() == Appconfig.TYPE_CLOSE){
                    btn_smile.setSelected(true);
                    keyEvent.setWantType(Appconfig.TYPE_OEPN_SMILE);//希望打开表情框
                    EventBus.getDefault().post(keyEvent);
                }else if(keyEvent.getCurrentType() == Appconfig.TYPE_OEPN_SMILE){
                    btn_smile.setSelected(false);
                    keyEvent.setWantType(Appconfig.TYPE_OPEN_KEYBOARD_AND_CLOSE_SMILE);//打开软键盘，关闭表情框
                    EventBus.getDefault().post(keyEvent);
                }else if(keyEvent.getCurrentType() == Appconfig.TYPE_OPEN_KEYBOARD){
                    btn_smile.setSelected(true);
                    keyEvent.setWantType(Appconfig.TYPE_OPEN_SMILE_AND_CLOSE_KEYBOARD);//关闭软键盘，打开表情框
                    EventBus.getDefault().post(keyEvent);
                }else if(keyEvent.getCurrentType() == Appconfig.TYPE_OPEN_FUCTION){
                    btn_smile.setSelected(true);
                    keyEvent.setWantType(Appconfig.TYPE_OPEN_SMILE_AND_CLOSE_FUNCTION);
                    EventBus.getDefault().post(keyEvent);
                }
            }
        });

        binding.includeChatButtom.editMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(keyEvent.getCurrentType() == Appconfig.TYPE_OEPN_SMILE){
                    btn_smile.setSelected(false);
                    keyEvent.setWantType(Appconfig.TYPE_OPEN_KEYBOARD_AND_CLOSE_SMILE);
                    EventBus.getDefault().post(keyEvent);//提示该关闭表情框
                }else if(keyEvent.getCurrentType() == Appconfig.TYPE_OPEN_FUCTION){
                    keyEvent.setWantType(Appconfig.TYPE_OPEN_KEYBOARD_AND_CLOSE_FUNCTION);
                    EventBus.getDefault().post(keyEvent);//提示该关闭表情框
                }else {
                    keyEvent.setCurrentType(Appconfig.TYPE_OPEN_KEYBOARD);
                }
                showLog("点击");
                binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        scrollToBottom();
                        binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        showLog("Layout");
                    }
                });
            }
        });

        binding.includeChatButtom.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(keyEvent.getCurrentType() == Appconfig.TYPE_CLOSE){
                    keyEvent.setWantType(Appconfig.TYPE_OPEN_FUCTION);
                    EventBus.getDefault().post(keyEvent);
                }else if(keyEvent.getCurrentType() ==Appconfig.TYPE_OEPN_SMILE){
                    btn_smile.setSelected(false);
                    keyEvent.setWantType(Appconfig.TYPE_OPEN_FUNCTION_AND_CLOSE_SMILE);
                    EventBus.getDefault().post(keyEvent);
                }else if(keyEvent.getCurrentType() == Appconfig.TYPE_OPEN_KEYBOARD){
                    keyEvent.setWantType(Appconfig.TYPE_OPEN_FUNCTION_AND_CLOSE_KEYBOARD);
                    EventBus.getDefault().post(keyEvent);
                }else if(keyEvent.getCurrentType() == Appconfig.TYPE_OPEN_FUCTION){
                    keyEvent.setWantType(Appconfig.TYPE_OPEN_KEYBOARD_AND_CLOSE_FUNCTION);
                    EventBus.getDefault().post(keyEvent);
                }
            }
        });
        binding.includeChatButtom.editMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                showLog("isTrue="+TextUtils.isEmpty(binding.includeChatButtom.editMsg.getText()));
                if(TextUtils.isEmpty(binding.includeChatButtom.editMsg.getText())){
                    btn_more.setVisibility(View.VISIBLE);
                    btn_send.setVisibility(View.GONE);
                }else{
                    btn_more.setVisibility(View.GONE);
                    btn_send.setVisibility(View.VISIBLE);
                }
            }
        });


        voiceDialog.setListener(new VoiceDialog.diaLogVoiceListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.dialog_voice_cancel:
                        if(recordManager.isRecording()){
                            //放弃发送语音
                            recordManager.cancelRecording();
                        }
                        if(voiceDialog.isShowing()){
                            voiceDialog.initVoiceText(getResources().getString(R.string.record));
                            voiceDialog.dismiss();
                        }
                        break;
                    case R.id.dialog_voice:
                        break;
                    case R.id.dialog_voice_start:
                        if (!CommonUtils.checkSdCard()) {
                            showToast("发送语音需要sdcard支持！");
                        }
                        if(voiceDialog.getVoiceText().equals(getResources().getString(R.string.record))){
                            try {
                                // 开始录音
                                recordManager.startRecording(mConversationManager.getConversationId());
                                voiceDialog.initVoiceText("发送");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else if(voiceDialog.getVoiceText().equals("发送")){
                            int recordTime = recordManager.stopRecording();
                            if (recordTime > 1) {
                                // 发送语音文件
                                sendVoiceMessage(recordManager.getRecordFilePath(mConversationManager.getConversationId()), recordTime);
                            } else {// 录音时间过短，则提示录音过短的提示
                                showShortToast().show();
                            }
                            voiceDialog.initVoiceText(getResources().getString(R.string.record));
                        }
                        break;
                }
            }
        });

        binding.includeChatAdd.layoutAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
                    showToast("尚未连接IM服务器");
                    return;
                }
                showLog("点击");
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, Appconfig.REQUESTCODE_TAKE_LOCAL);//2016.4.8注释掉
            }
        });

        binding.includeChatAdd.layoutAddCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 启动系统相机
                startActivityForResult(intent, CAMERA_STATE);
            }
        });

        binding.includeChatAdd.layoutAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendLocationMessage();
            }
        });
    }

    @Override
    protected void destoryView() {
        EventBus.getDefault().unregister(this);
        //清理资源(语音相关)
        if (recordManager != null) {
            recordManager.clear();
        }
//        //TODO 消息：5.4、更新此会话的所有消息为已读状态
        if (mConversationManager != null) {
            mConversationManager.updateLocalCache();
        }
        hideSoftInputView();
    }


    @Override
    protected void onResume() {
        //锁屏期间的收到的未读消息需要添加到聊天界面中
        addUnReadMessage();
        //添加页面消息监听器
        BmobIM.getInstance().addMessageListHandler(this);
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知
        BmobNotificationManager.getInstance(this).cancelNotification();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //移除页面消息监听器
        BmobIM.getInstance().removeMessageListHandler(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EventKeyBoard(KeyboardEvent event){
        if(event.getWantType() == Appconfig.TYPE_OEPN_SMILE &&
                event.getCurrentType() == Appconfig.TYPE_CLOSE){
            binding.viewPagerSpeakFace.setVisibility(View.VISIBLE);
            keyEvent.setCurrentType(Appconfig.TYPE_OEPN_SMILE);
            scrollToBottom();
        }else if(event.getWantType() == Appconfig.TYPE_OPEN_KEYBOARD_AND_CLOSE_SMILE &&
                event.getCurrentType() == Appconfig.TYPE_OEPN_SMILE){
            //在处于表情框出现状态，希望关闭表情框，弹出键盘
            binding.viewPagerSpeakFace.setVisibility(View.GONE);
            keyEvent.setCurrentType(Appconfig.TYPE_OPEN_KEYBOARD);
            showSoftInputView();
            scrollToBottom();
        }else if(event.getWantType() == Appconfig.TYPE_OPEN_SMILE_AND_CLOSE_KEYBOARD &&
                event.getCurrentType() == Appconfig.TYPE_OPEN_KEYBOARD){
            //延迟显示表情框
            hideSoftInputView();
            new MyThread().start();
        }else if(event.getCurrentType() == Appconfig.TYPE_DELAY_SHOW_SMILE){
            binding.viewPagerSpeakFace.setVisibility(View.VISIBLE);
            keyEvent.setCurrentType(Appconfig.TYPE_OEPN_SMILE);
            scrollToBottom();
        }else if(event.getCurrentType() == Appconfig.TYPE_OPEN_FUCTION &&
                event.getWantType() ==Appconfig.TYPE_OPEN_SMILE_AND_CLOSE_FUNCTION){
            layout_add.setVisibility(View.GONE);
            binding.viewPagerSpeakFace.setVisibility(View.VISIBLE);
            keyEvent.setCurrentType(Appconfig.TYPE_OEPN_SMILE);
            scrollToBottom();
        }else if(event.getCurrentType() == Appconfig.TYPE_CLOSE &&
                event.getWantType() == Appconfig.TYPE_OPEN_FUCTION){
            //这里开始时对功能框的逻辑判断
            layout_add.setVisibility(View.VISIBLE);
            keyEvent.setCurrentType(Appconfig.TYPE_OPEN_FUCTION);
            scrollToBottom();
        }else if(event.getCurrentType() == Appconfig.TYPE_OEPN_SMILE &&
                event.getWantType() == Appconfig.TYPE_OPEN_FUNCTION_AND_CLOSE_SMILE){
            binding.viewPagerSpeakFace.setVisibility(View.GONE);
            layout_add.setVisibility(View.VISIBLE);
            keyEvent.setCurrentType(Appconfig.TYPE_OPEN_FUCTION);
            scrollToBottom();
        }else if(event.getCurrentType() == Appconfig.TYPE_OPEN_KEYBOARD &&
                event.getWantType() == Appconfig.TYPE_OPEN_FUNCTION_AND_CLOSE_KEYBOARD){
            //延迟显示功能框
            hideSoftInputView();
            new MyThread().start();
        }else if(event.getCurrentType() == Appconfig.TYPE_DELAY_SHOW_FUNCTION){
            layout_add.setVisibility(View.VISIBLE);
            keyEvent.setCurrentType(Appconfig.TYPE_OPEN_FUCTION);
            scrollToBottom();
        }else if(event.getCurrentType() == Appconfig.TYPE_OPEN_FUCTION &&
                event.getWantType() == Appconfig.TYPE_OPEN_KEYBOARD_AND_CLOSE_FUNCTION){
            layout_add.setVisibility(View.GONE);
            showSoftInputView();
            scrollToBottom();
            keyEvent.setCurrentType(Appconfig.TYPE_OPEN_KEYBOARD);
        }
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        showLog("聊天页面接收到消息：" + list.size());
        //当注册页面消息监听时候，有消息（包含离线消息）到来时会回调该方法
        for (int i = 0; i < list.size(); i++) {
            addMessage2Chat(list.get(i));
        }
    }

    /**
     * 添加消息到聊天界面中
     *
     * @param event
     */
    private void addMessage2Chat(MessageEvent event) {
        BmobIMMessage msg = event.getMessage();
        if (mConversationManager != null && event != null && mConversationManager.getConversationId().equals(event.getConversation().getConversationId()) //如果是当前会话的消息
                && !msg.isTransient()) {//并且不为暂态消息
            if (adapter.findPosition(msg) < 0) {//如果未添加到界面中
                adapter.addMessage(msg);
                //更新该会话下面的已读状态
                mConversationManager.updateReceiveStatus(msg);
            }
            scrollToBottom();
        } else {
            showLog("不是与当前聊天对象的消息");
        }
    }

    class MyThread extends Thread{
        @Override
        public void run() {
            // TODO Auto-generated method stub
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
            }else if(keyEvent.getCurrentType() == Appconfig.TYPE_OPEN_KEYBOARD &&
                    keyEvent.getWantType() == Appconfig.TYPE_OPEN_FUNCTION_AND_CLOSE_KEYBOARD){
                keyEvent.setWantType(Appconfig.TYPE_OPEN_FUCTION);
                keyEvent.setCurrentType(Appconfig.TYPE_DELAY_SHOW_FUNCTION);
                EventBus.getDefault().post(keyEvent);
            }
//            keyEvent.setType(Appconfig.TYPE_DELAY_SHOW_SMILE);
//            EventBus.getDefault().post(keyEvent);
        }
    }

    @Override
    protected void toolBarBackListener(View view) {
        ActivityCollector.removeActivity(this);
        finish();
    }

    private void initTitle(String title){
        initToolBar(binding.toolbar.toolbar,"",true);
        binding.toolbar.txtTitle.setText(title);
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
                ChatActivity.this, views);
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

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
//        imageView_face.setSelected(false);
        InputMethodManager manager = ((InputMethodManager) this
                .getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (ChatActivity.this.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (ChatActivity.this.getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(ChatActivity.this
                                .getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软键盘
     */
    public void showSoftInputView() {
//        imageView_face.setSelected(false);
        if (ChatActivity.this.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
            if (ChatActivity.this.getCurrentFocus() != null)
                ((InputMethodManager) (ChatActivity.this)
                        .getSystemService(Activity.INPUT_METHOD_SERVICE))
                        .showSoftInput(edt_msg, 0);
        }
    }



    /**
     * 显示录音时间过短的Toast
     *
     * @return void
     * @Title: showShortToast
     */
    private Toast showShortToast() {
        if (toast == null) {
            toast = new Toast(this);
        }
        View view = LayoutInflater.from(this).inflate(
                R.layout.include_chat_voice_short, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        return toast;
    }

    /**
     * 发送文本消息
     */
    private void sendMessage() {
        String text = binding.includeChatButtom.editMsg.getText().toString();
        if (TextUtils.isEmpty(text.trim())) {
            showToast("请输入内容");
            return;
        }
        boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
        if (!isNetConnected) {
            showToast("请检查你的网络");
             return;
        }
        //TODO 发送消息：6.1、发送文本消息
        BmobIMTextMessage msg = new BmobIMTextMessage();
        msg.setContent(text);
        mConversationManager.sendMessage(msg, listener);
    }

    /**
     * 消息发送监听器
     */
    public MessageSendListener listener = new MessageSendListener() {

        @Override
        public void onProgress(int value) {
            super.onProgress(value);
            //文件类型的消息才有进度值
            showLog("onProgress：" + value);
        }

        @Override
        public void onStart(BmobIMMessage msg) {
            super.onStart(msg);
            adapter.addMessage(msg);
            binding.includeChatButtom.editMsg.setText("");
            scrollToBottom();
        }

        @Override
        public void done(BmobIMMessage msg, BmobException e) {
            adapter.notifyDataSetChanged();
            binding.includeChatButtom.editMsg.setText("");
//            //java.lang.NullPointerException: Attempt to invoke virtual method 'void android.widget.TextView.setText(java.lang.CharSequence)' on a null object reference
            scrollToBottom();
            if (e != null) {
                showToast(e.getMessage());
            }
        }
    };

    public void firstQueryMessages(){
        binding.refreshHistoryContent.setRefreshing(true);
        //自动刷新
        queryMessages(null);
    }

    /**
     * 首次加载，可设置msg为null，下拉刷新的时候，默认取消息表的第一个msg作为刷新的起始时间点，默认按照消息时间的降序排列
     *
     * @param msg
     */
    public void queryMessages(BmobIMMessage msg) {
        //TODO 消息：5.2、查询指定会话的消息记录
        mConversationManager.queryMessages(msg, 10, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                binding.refreshHistoryContent.setRefreshing(false);
                if (e == null) {
                    if (null != list && list.size() > 0) {
                        adapter.addMessages(list);
                        layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                    }
                } else {
                    showLog(e.getMessage() + "(" + e.getErrorCode() + ")");
                }
            }
        });
    }

    /**
     * 添加未读的通知栏消息到聊天界面
     */
    private void addUnReadMessage() {
        List<MessageEvent> cache = BmobNotificationManager.getInstance(this).getNotificationCacheList();
        if (cache.size() > 0) {
            int size = cache.size();
            for (int i = 0; i < size; i++) {
                MessageEvent event = cache.get(i);
                addMessage2Chat(event);
            }
        }
        scrollToBottom();
    }

    private void scrollToBottom() {
        showLog(adapter.getItemCount() - 1+"");
        layoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (layout_add.getVisibility() == View.VISIBLE) {
                layout_add.setVisibility(View.GONE);
                return false;
            } else if(binding.viewPagerSpeakFace.getVisibility() == View.VISIBLE){
                binding.viewPagerSpeakFace.setVisibility(View.GONE);
                return false;
            }else{
                return super.onKeyDown(keyCode, event);
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void initRecordManager() {
        // 语音相关管理器
        recordManager = BmobRecordManager.getInstance(this);
        // 设置音量大小监听--在这里开发者可以自己实现：当剩余10秒情况下的给用户的提示，类似微信的语音那样
        recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {


            @Override
            public void onVolumeChanged(int value) {
                voiceDialog.initVoiceImage(value);
                showLog("value="+value);
            }

            @Override
            public void onTimeChanged(int recordTime, String localPath) {
                showLog("已录音长度:" + recordTime);
                if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {// 1分钟结束，发送消息
                    // 需要重置按钮
                    voiceDialog.initButton(false);
//                    // 取消录音框
//                    layout_record.setVisibility(View.INVISIBLE);
                    // 发送语音消息
                    sendVoiceMessage(localPath, recordTime);
                    //是为了防止过了录音时间后，会多发一条语音出去的情况。
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            voiceDialog.initButton(true);
                        }
                    }, 1000);
                }
            }
        });
    }

    /**
     * 发送语音消息
     *
     * @param local
     * @param length
     * @return void
     * @Title: sendVoiceMessage
     */
    private void sendVoiceMessage(String local, int length) {
        //TODO 发送消息：6.5、发送本地音频文件消息
        BmobIMAudioMessage audio = new BmobIMAudioMessage(local);
        //可设置额外信息-开发者设置的额外信息，需要开发者自己从extra中取出来
        Map<String, Object> map = new HashMap<>();
        map.put("from", "优酷");
        //TODO 自定义消息：7.1、给消息设置额外信息
        audio.setExtraMap(map);
        //设置语音文件时长：可选
//        audio.setDuration(length);
        mConversationManager.sendMessage(audio, listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = null;
        if(data != null){
            uri = data.getData();
        }
        if(requestCode == Appconfig.REQUESTCODE_TAKE_LOCAL && uri != null){
//            showLog("path="+uri.getPath());
            String realPathFromUri = RealPathFromUriUtils.getRealPathFromUri(this, data.getData());
            showLog("realPathFromUri="+realPathFromUri);
            if(!TextUtils.isEmpty(uri.getPath()))sendLocalImageMessage(realPathFromUri);
        }else if(resultCode == RESULT_OK && requestCode == CAMERA_STATE){
            Bundle bundle = data.getExtras(); // 从data中取出传递回来缩略图的信息，图片质量差，适合传递小图片
            Bitmap bitmap = (Bitmap) bundle.get("data"); // 将data中的信息流解析为Bitmap类
            Uri uriCamera = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),
                    bitmap, null,null));
            String realPathFromUri = RealPathFromUriUtils.getRealPathFromUri(this, uriCamera);
            showLog("realPathFromUri="+realPathFromUri);
            if(!TextUtils.isEmpty(uriCamera.getPath())){
                sendLocalImageMessage(realPathFromUri);
            }
        }
    }

    /**
     * 发送本地图片文件
     */
    public void sendLocalImageMessage(String url) {
        //TODO 发送消息：6.2、发送本地图片消息
        //正常情况下，需要调用系统的图库或拍照功能获取到图片的本地地址，开发者只需要将本地的文件地址传过去就可以发送文件类型的消息
        BmobIMImageMessage image = new BmobIMImageMessage(url);
        showLog("image="+image);
        mConversationManager.sendMessage(image, listener);
    }
    /**
     * 发送地理位置消息
     */
    public void sendLocationMessage() {
        //TODO 发送消息：6.10、发送位置消息
        //测试数据，真实数据需要从地图SDK中获取
        LocationEvent locationEvent = App.getInstance().getLocationEvent();
        if(locationEvent != null){
            String provinceCity = locationEvent.getCity()+locationEvent.getDistrict()+locationEvent.getStreet();
            double latitude = locationEvent.getLatitude();
            double altitude = locationEvent.getAltitude();
            BmobIMLocationMessage location = new BmobIMLocationMessage(provinceCity, latitude, altitude);
            Map<String, Object> map = new HashMap<>();
            map.put("from", "百度地图");
            location.setExtraMap(map);
            mConversationManager.sendMessage(location, listener);
        }
    }
}
