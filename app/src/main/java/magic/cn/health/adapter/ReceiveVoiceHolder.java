package magic.cn.health.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.View;

import cn.bmob.newim.bean.BmobIMAudioMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.core.BmobDownloadManager;
import cn.bmob.newim.listener.FileDownloadListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import magic.cn.health.databinding.ItemChatReceivedVoiceBinding;
import magic.cn.health.inteface.NewRecordPlayClickListener;
import magic.cn.health.inteface.OnRecyclerViewListener;

/**
 * @author 林思旭
 * @since 2018/3/26
 */

public class ReceiveVoiceHolder<T> extends BaseViewHolder {

    private ItemChatReceivedVoiceBinding binding;

    private String currentUid;

    private Context context;

    private OnRecyclerViewListener onRecyclerViewListener;

    public ReceiveVoiceHolder(ViewDataBinding binding, Context context,OnRecyclerViewListener c) {
        super(binding);
        this.binding = (ItemChatReceivedVoiceBinding) binding;
        this.context = context;
        currentUid = BmobUser.getCurrentUser().getObjectId();
        onRecyclerViewListener = c;
    }

    @Override
    public void initData(Object o) {
        BmobIMMessage msg = (BmobIMMessage)o;
        //显示特有属性
        final BmobIMAudioMessage message = BmobIMAudioMessage.buildFromDB(false, msg);
        boolean isExists = BmobDownloadManager.isAudioExist(currentUid, message);
        if(!isExists){//若指定格式的录音文件不存在，则需要下载，因为其文件比较小，故放在此下载
            BmobDownloadManager downloadTask = new BmobDownloadManager(context,msg,new FileDownloadListener() {

                @Override
                public void onStart() {
                    binding.progressLoad.setVisibility(View.VISIBLE);
                    binding.tvVoiceLength.setVisibility(View.GONE);
                    binding.ivVoice.setVisibility(View.INVISIBLE);//只有下载完成才显示播放的按钮
                }

                @Override
                public void done(BmobException e) {
                    if(e==null){
                        binding.progressLoad.setVisibility(View.GONE);
                        binding.tvVoiceLength.setVisibility(View.VISIBLE);
                        //用databind绑定了
                        binding.tvVoiceLength.setText(message.getDuration()+"\''");
                        binding.ivVoice.setVisibility(View.VISIBLE);
                    }else{
                        binding.progressLoad.setVisibility(View.GONE);
                        binding.tvVoiceLength.setVisibility(View.GONE);
                        binding.ivVoice.setVisibility(View.INVISIBLE);
                    }
                }
            });
            downloadTask.execute(message.getContent());
        }else{
            binding.tvVoiceLength.setVisibility(View.VISIBLE);
            binding.tvVoiceLength.setText(message.getDuration() + "\''");
        }
        binding.ivVoice.setOnClickListener(new NewRecordPlayClickListener(context, message, binding.ivVoice));

        binding.ivVoice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onRecyclerViewListener != null) {
                    onRecyclerViewListener.onItemLongClick(getAdapterPosition());
                }
                return true;
            }
        });

    }

}
