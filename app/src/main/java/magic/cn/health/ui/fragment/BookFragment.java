package magic.cn.health.ui.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import magic.cn.health.R;
import magic.cn.health.databinding.FragmentBookBinding;
import magic.cn.health.ui.activity.SearchUserActivity;


/** 好友通讯录
 * @author 林思旭
 * @since 2018/3/6
 */

public class BookFragment extends BaseFragment {

    FragmentBookBinding binding;

    @Override
    protected View initBinding(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_book,container,false);
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        binding.layoutSearchFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchUserActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    protected void destroyView() {

    }
}
