package magic.cn.health.adapter;

import android.databinding.ViewDataBinding;

/**
 * @author 林思旭
 * @since 2018/3/26
 */

public abstract class BaseViewHolder<T> extends BindingViewHolder {

    public BaseViewHolder(ViewDataBinding binding) {
        super(binding);
    }

    public abstract void initData(T t);
}
