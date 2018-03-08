package magic.cn.health.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import magic.cn.health.bean.Message;


/**
 * @author 林思旭
 * @since 2018/3/6
 */

public class MessageAdapter extends RecyclerView.Adapter<BindingViewHolder> {

    private final LayoutInflater mLayoutInflater;

    public interface OnItemListener{
        void onMessageClick(Message message);
    }

    public MessageAdapter(Context context) {
        mLayoutInflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
