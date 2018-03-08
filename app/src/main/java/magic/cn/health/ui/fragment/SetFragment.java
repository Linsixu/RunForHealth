package magic.cn.health.ui.fragment;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import magic.cn.health.R;
import magic.cn.health.databinding.FragmentSetBinding;


/**
 * @author 林思旭
 * @since 2018/3/6
 */

public class SetFragment extends BaseFragment {

    FragmentSetBinding binding;

    @Override
    protected View initBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_set,container,false);


        return binding.getRoot();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void destroyView() {

    }
}