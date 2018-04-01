package magic.cn.health.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import magic.cn.health.R;
import magic.cn.health.bean.User;
import magic.cn.health.model.StartModel;
import magic.cn.health.utils.MyLog;

/**
 * @author 林思旭
 * @since 2018/3/15
 */

public class UserFriendAdapter extends RecyclerView.Adapter<BindingViewHolder> {

    private List<User> listUser;

    private LayoutInflater inflater;

    private StartModel startModel;

    public UserFriendAdapter(Context context) {
        startModel = new StartModel(context);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        listUser = new ArrayList<>();
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        binding = DataBindingUtil.inflate(inflater, R.layout.item_user_friends,parent,false);
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        final User user = listUser.get(position);
        holder.getBinding().setVariable(magic.cn.health.BR.user,user);
        holder.getBinding().setVariable(magic.cn.health.BR.presenter,startModel);
        holder.getBinding().executePendingBindings();

        TextView tv_alpha = holder.itemView.findViewById(R.id.tv_alpha);
        // 根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            tv_alpha.setVisibility(View.VISIBLE);
            tv_alpha.setText(user.getSortLetters());
        } else {
            tv_alpha.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public void addAll(List<User> list){
        if(listUser.size() != 0)listUser.clear();
        listUser.addAll(list);
        MyLog.i("UserFriendAdapter",listUser.size()+"");
        notifyDataSetChanged();
    }

    public void add(User user){
        listUser.add(user);
        notifyDataSetChanged();
    }

    public void remove(User user){
        listUser.remove(user);
        notifyDataSetChanged();
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return listUser.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    @SuppressLint("DefaultLocale")
    public int getPositionForSection(int section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = listUser.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section){
                return i;
            }
        }
        return -1;
    }
}
