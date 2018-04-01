package magic.cn.health.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.View;

import cn.bmob.newim.bean.BmobIMAudioMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import magic.cn.health.databinding.ItemChatSentVoiceBinding;
import magic.cn.health.inteface.NewRecordPlayClickListener;
import magic.cn.health.inteface.OnRecyclerViewListener;

/**
 * @author 林思旭
 * @since 2018/3/27
 */

public class SendVoiceHolder extends BaseViewHolder {

    private ItemChatSentVoiceBinding binding;

    private OnRecyclerViewListener listener;

    private Context context;

    public SendVoiceHolder(ViewDataBinding binding, Context context,OnRecyclerViewListener c) {
        super(binding);
        this.binding = (ItemChatSentVoiceBinding) binding;
        this.listener = c;
        this.context = context;
    }

    @Override
    public void initData(Object o) {
        BmobIMMessage msg = (BmobIMMessage)o;
        int status = msg.getSendStatus();
        if (status == BmobIMSendStatus.SEND_FAILED.getStatus()||status == BmobIMSendStatus.UPLOAD_FAILED.getStatus()) {//发送失败/上传失败
            binding.ivFailResend.setVisibility(View.VISIBLE);
            binding.progressLoad.setVisibility(View.GONE);
            binding.tvSendStatus.setVisibility(View.INVISIBLE);
            binding.tvVoiceLength.setVisibility(View.INVISIBLE);
        } else if (status== BmobIMSendStatus.SENDING.getStatus()) {
            binding.progressLoad.setVisibility(View.VISIBLE);
            binding.ivFailResend.setVisibility(View.GONE);
            binding.tvSendStatus.setVisibility(View.INVISIBLE);
            binding.tvVoiceLength.setVisibility(View.INVISIBLE);
        } else {//发送成功
            binding.ivFailResend.setVisibility(View.GONE);
            binding.progressLoad.setVisibility(View.GONE);
            binding.tvSendStatus.setVisibility(View.GONE);
            binding.tvVoiceLength.setVisibility(View.VISIBLE);
        }

        final BmobIMAudioMessage message = BmobIMAudioMessage.buildFromDB(true,msg);

        binding.ivVoice.setOnClickListener(new NewRecordPlayClickListener(context,message,binding.ivVoice));

        binding.ivVoice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onItemLongClick(getAdapterPosition());
                }
                return true;
            }
        });
    }
}
