package magic.cn.health.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import magic.cn.health.BR;
import magic.cn.health.R;
import magic.cn.health.bean.Conversation;
import magic.cn.health.model.StartModel;

/**
 * @author 林思旭
 * @since 2018/3/27
 */

public class ConversationAdapter extends RecyclerView.Adapter<BindingViewHolder>{

    private List<Conversation> list;

    private LayoutInflater inflater;

    private Context context;

    private StartModel startModel;

    public ConversationAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        list = new ArrayList<>();

        startModel = new StartModel(context);
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        binding = DataBindingUtil.inflate(inflater, R.layout.item_conversation,parent,false);
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        Conversation conversation = list.get(position);
        holder.getBinding().setVariable(BR.conversation,conversation);
        holder.getBinding().setVariable(BR.presenter,startModel);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addAll(List<Conversation> list){
        if(this.list.size() != 0){
            clearAll();
        }
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void add(Conversation conversation){
        list.add(conversation);
        notifyDataSetChanged();
    }

    public void remove(Conversation conversation){
        list.remove(conversation);
        notifyDataSetChanged();
    }

    private void clearAll(){
        list.clear();
        notifyDataSetChanged();
    }
}
