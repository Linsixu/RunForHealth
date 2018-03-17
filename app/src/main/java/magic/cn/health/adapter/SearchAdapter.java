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

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;
import magic.cn.health.R;
import magic.cn.health.app.App;
import magic.cn.health.bean.User;
import magic.cn.health.model.UserModel;
import magic.cn.health.utils.MyLog;

/**
 * @author 林思旭
 * @since 2018/3/12
 */

public class SearchAdapter extends RecyclerView.Adapter<BindingViewHolder> {
    private String TAG = "SearchAdapter";

    private static final int ITEM_VIEW_TYPE_BOY = 1;
    private static final int ITEM_VIEW_TYPE_GIRL = 2;

    private final LayoutInflater mLayoutInflater;

    private onItemClickListener listener;

    private List<User> userList;

    private Context context;

    public interface onItemClickListener{
        void onClick(BmobIMMessage bmobIMMessage, BmobException e);
    }

    public SearchAdapter(Context context) {
        this.context = context;
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
    public void onBindViewHolder(BindingViewHolder holder, final int position) {
        final User user = userList.get(position);
        holder.getBinding().setVariable(magic.cn.health.BR.user,user);
        holder.getBinding().executePendingBindings();
        final Button btn_add = holder.itemView.findViewById(R.id.btn_add);
        final List<User> friends = App.getInstance().getListFriends();
        MyLog.i(TAG,"isTrue="+friends.contains(user));
        if(friends.contains(user) && friends.size() != 0){
            btn_add.setEnabled(false);
            btn_add.setText("已添加");
            btn_add.setBackgroundDrawable(null);
            btn_add.setTextColor(context.getResources().getColor(R.color.base_color_text_black));
        }
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserModel.getModelInstance().sendAddFriendMessgae(user, new MessageSendListener() {
                    @Override
                    public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                        listener.onClick(bmobIMMessage,e);
//                       MyLog.i(TAG,"e="+e+","+"bmobIMMessage="+bmobIMMessage);
                    }
                });
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
