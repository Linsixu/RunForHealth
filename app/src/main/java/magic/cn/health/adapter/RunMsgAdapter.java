package magic.cn.health.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.v3.exception.BmobException;
import magic.cn.health.BR;
import magic.cn.health.R;
import magic.cn.health.bean.RunMessage;
import magic.cn.health.model.RunCommitModel;

/**
 * @author 林思旭
 * @since 2018/4/19
 */

public class RunMsgAdapter extends RecyclerView.Adapter<BindingViewHolder> {
    private List<RunMessage> list;

    private LayoutInflater inflater;

    private Context context;

    private onItemRunClickListener listener;

    public RunMsgAdapter(Context context) {
        this.context = context;

        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        list = new ArrayList<>();
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        binding = DataBindingUtil.inflate(inflater, R.layout.item_run_message,
                parent,false);
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        RunMessage msg = list.get(position);
        holder.getBinding().setVariable(BR.msg,msg);
        RunCommitModel.getInstance().setContext(context);
        holder.getBinding().setVariable(BR.presenter,RunCommitModel.getInstance());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void add(RunMessage message){
        list.add(message);
        notifyDataSetChanged();
    }

    public void addAll(List<RunMessage> messages){
        if(list.size() != 0)deleteAll();
        list.addAll(messages);
        notifyDataSetChanged();
    }

    public void addMsgToHead(RunMessage msg){
        list.add(0,msg);
        notifyDataSetChanged();
    }

    public void deleteAll(){
        list.clear();
        notifyDataSetChanged();
    }

    public interface onItemRunClickListener{
        public void onClick(BmobIMMessage bmobIMMessage, BmobException e);
    }

    public void setListener(onItemRunClickListener listener) {
        this.listener = listener;
    }
}
