package magic.cn.health.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import magic.cn.health.R;
import magic.cn.health.bean.NewFriend;
import magic.cn.health.config.Appconfig;
import magic.cn.health.model.UserModel;

/**
 * @author 林思旭
 * @since 2018/3/14
 */

public class NewFriendAdapter extends RecyclerView.Adapter<BindingViewHolder> {

    private String TAG = "NewFriendAdapter";

    private List<NewFriend> friendList;

    private LayoutInflater inflater;

    private Context context;

    private ItemAgreeListener itemAgreeListener;

    public interface ItemAgreeListener{
        public void done(Object o, BmobException e);
    }

    public NewFriendAdapter(Context context) {
        inflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;

        friendList = new ArrayList<>();
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        binding = DataBindingUtil.inflate(inflater, R.layout.item_new_friend,parent,false);
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        final NewFriend friend = friendList.get(position);
        if(friend == null)return;
        ImageView avatar = holder.itemView.findViewById(R.id.img_new_friend_avatar);
        TextView txt_name = holder.itemView.findViewById(R.id.txt_new_friend_name);
//        TextView txt_time = holder.itemView.findViewById(R.id.txt_new_friend_time);
        TextView txt_say = holder.itemView.findViewById(R.id.txt_new_friend_say);
        final Button btn_agree = holder.itemView.findViewById(R.id.btn_agree);
        if(TextUtils.isEmpty(friend.getAvatar())){
            avatar.setImageResource(R.drawable.icon_head_boy);
        }else{
            ImageLoader.getInstance().displayImage(friend.getAvatar(),avatar);
        }
        txt_name.setText("姓名:"+friend.getName());
//        txt_time.setText("时间:"+friend.getTime().toString());
        txt_say.setText(friend.getMsg());

        //
        Integer status = friend.getStatus();
        //当状态是未添加或者是已读未添加
        if (status == null || status == Appconfig.STATUS_VERIFY_NONE || status == Appconfig.STATUS_VERIFY_READED) {
            btn_agree.setText("同意");
            btn_agree.setEnabled(true);
            btn_agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserModel.getModelInstance().agreeAdd(friend, new SaveListener<Object>() {
                        @Override
                        public void done(Object o, BmobException e) {
                            itemAgreeListener.done(o,e);
                            if(e == null){
                                btn_agree.setEnabled(false);
                                btn_agree.setText("已添加");
                                btn_agree.setBackgroundDrawable(null);
                                btn_agree.setTextColor(context.getResources().getColor(R.color.base_color_text_black));
                            }else{
                                btn_agree.setEnabled(true);
                            }
                        }
                    });
                }
            });
        }else{
            btn_agree.setEnabled(false);
            btn_agree.setText("已添加");
            btn_agree.setBackgroundDrawable(null);
            btn_agree.setTextColor(context.getResources().getColor(R.color.base_color_text_black));
        }
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public void addAll(List<NewFriend> list){
        if(friendList.size() != 0){
            clearAll();
        }
        friendList.addAll(list);
        notifyDataSetChanged();
    }

    public void add(NewFriend friend){
        friendList.add(friend);
        notifyDataSetChanged();
    }

    public void remove(NewFriend friend){
        friendList.remove(friend);
        notifyDataSetChanged();
    }

    private void clearAll(){
        friendList.clear();
        notifyDataSetChanged();
    }

    public void setItemAgreeListener(ItemAgreeListener itemAgreeListener) {
        this.itemAgreeListener = itemAgreeListener;
    }


}
