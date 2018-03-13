package magic.cn.health.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import magic.cn.health.R;
import magic.cn.health.bean.User;

/**
 * @author 林思旭
 * @since 2018/3/12
 */

public class SearchAdapter extends RecyclerView.Adapter<BindingViewHolder> {

    private static final int ITEM_VIEW_TYPE_BOY = 1;
    private static final int ITEM_VIEW_TYPE_GIRL = 2;

    private final LayoutInflater mLayoutInflater;

    private onItemClickListener listener;

    private List<User> userList;

    public interface onItemClickListener{
        void onClick(View view);
    }

    public SearchAdapter(Context context) {
        mLayoutInflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        userList = new ArrayList<>();
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        if(viewType == ITEM_VIEW_TYPE_BOY){
            binding = DataBindingUtil.inflate(mLayoutInflater,
                    R.layout.item_add_friend_boy,parent,false);
        }else{
            binding = DataBindingUtil.inflate(mLayoutInflater,
                    R.layout.item_add_friend_girl,parent,false);
        }
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        final User user = userList.get(position);
        holder.getBinding().setVariable(magic.cn.health.BR.user,user);
        holder.getBinding().executePendingBindings();
        Button btn_search = holder.itemView.findViewById(R.id.btn_add);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onClick(view);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    @Override
    public int getItemViewType(int position) {
        if(userList.get(position).getSex()){
            //如果是男生
            return ITEM_VIEW_TYPE_BOY;
        }else{
            return ITEM_VIEW_TYPE_GIRL;
        }
    }

    public void setListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public void addAll(List<User> list){
        if(userList.size()!=0)userList.clear();
        userList.addAll(list);
        notifyDataSetChanged();
    }

    public void add(User user){
        userList.add(user);
        notifyDataSetChanged();
    }
    public void remove(User user){
        if(userList.size() == 0){
            return;
        }
        userList.remove(user);
        notifyDataSetChanged();
    }

    public void deleteAll(){
        userList.clear();
        notifyDataSetChanged();
    }
}
