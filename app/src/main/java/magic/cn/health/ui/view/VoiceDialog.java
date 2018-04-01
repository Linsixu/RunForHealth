package magic.cn.health.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import magic.cn.health.R;
import magic.cn.health.databinding.DialogChatVoiceBinding;


/**
 * @author 林思旭
 * @since 2018/3/24
 */

public class VoiceDialog extends Dialog implements View.OnClickListener{

    private DialogChatVoiceBinding binding;
    private Context context;

    private diaLogVoiceListener listener;

    private Drawable[] drawable_Anims;// 话筒动画

    public VoiceDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public VoiceDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    protected VoiceDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.dialog_chat_voice,null,false);
        setContentView(binding.getRoot());
        initVoiceAnimRes();
        this.setCancelable(false);//点击屏幕不消失
        binding.dialogVoiceCancel.setOnClickListener(this);
        binding.dialogVoice.setOnClickListener(this);
        binding.dialogVoiceStart.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        listener.onClick(view);
    }

    public interface diaLogVoiceListener{
        void onClick(View view);
    }

    public void setListener(diaLogVoiceListener listener) {
        this.listener = listener;
    }

    /**
     * 初始化语音动画资源
     *
     * @param
     * @return void
     * @Title: initVoiceAnimRes
     */
    private void initVoiceAnimRes() {
        drawable_Anims = new Drawable[]{
                context.getResources().getDrawable(R.drawable.chat_icon_voice2),
                context.getResources().getDrawable(R.drawable.chat_icon_voice3),
                context.getResources().getDrawable(R.drawable.chat_icon_voice4),
                context.getResources().getDrawable(R.drawable.chat_icon_voice5),
                context.getResources().getDrawable(R.drawable.chat_icon_voice6)};
    }

    public void initVoiceImage(int value){
        binding.dialogVoice.setImageDrawable(drawable_Anims[value]);
    }

    public void initButton(boolean isShow){
        if(isShow){
            binding.dialogVoiceStart.setClickable(true);
        }else {
            binding.dialogVoiceStart.setPressed(false);
            binding.dialogVoiceStart.setClickable(false);
        }
    }

    public void initVoiceText(String content){
        binding.dialogVoiceStart.setText(content);
    }

    public String getVoiceText(){
        return binding.dialogVoiceStart.getText().toString();
    }
}
