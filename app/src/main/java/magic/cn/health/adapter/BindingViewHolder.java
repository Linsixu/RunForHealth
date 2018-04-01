package magic.cn.health.adapter;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

/**
 * @author 林思旭
 * @since 2018/3/6
 */

public class BindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder{

    private T mBinding;

    private int layoutId;

    public BindingViewHolder(T binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    public T getBinding(){
        return mBinding;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }
}
