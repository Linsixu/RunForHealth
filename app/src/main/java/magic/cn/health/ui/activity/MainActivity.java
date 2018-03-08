package magic.cn.health.ui.activity;

import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import magic.cn.health.R;
import magic.cn.health.adapter.ViewPagerAdapter;
import magic.cn.health.databinding.ActivityMainBinding;
import magic.cn.health.ui.fragment.BookFragment;
import magic.cn.health.ui.fragment.ChatFragment;
import magic.cn.health.ui.fragment.RunFragment;
import magic.cn.health.ui.fragment.SetFragment;

public class MainActivity extends BaseActivity {

    ActivityMainBinding binding;


    private RunFragment runFragment = new RunFragment();
    private ChatFragment chatFragment = new ChatFragment();
    private SetFragment setFragment = new SetFragment();
    private BookFragment bookFragment = new BookFragment();

    private LinearLayout layoutBook,layoutChat,layoutNearBy,layoutSet;

    private List<Fragment> list = new ArrayList<>();

    private ViewPager viewPager;

    private ViewPagerAdapter adapter = null;

    @Override
    protected void initBind() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        layoutBook = binding.layoutButtom.layoutBook;
        layoutChat = binding.layoutButtom.layoutChat;
        layoutNearBy = binding.layoutButtom.layoutNearby;
        layoutSet = binding.layoutButtom.layoutSet;

        viewPager = binding.viewPagerMain;

    }

    @Override
    protected void initView() {

        list.add(runFragment);
        list.add(chatFragment);
        list.add(bookFragment);
        list.add(setFragment);

        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();

        adapter = new ViewPagerAdapter(manager,list);

        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        initRunFragment();
                        break;
                    case 1:
                        initChatFragment();
                        break;
                    case 2:
                        initBookFragment();
                        break;
                    case 3:
                        initSetFragment();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        layoutNearBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initRunFragment();
                Toast.makeText(MainActivity.this,"点击通讯录",Toast.LENGTH_SHORT).show();
            }
        });

        layoutChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initChatFragment();
                Toast.makeText(MainActivity.this,"群聊",Toast.LENGTH_SHORT).show();
            }
        });

        layoutBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initBookFragment();
                Toast.makeText(MainActivity.this,"点击通讯录",Toast.LENGTH_SHORT).show();
            }
        });

        layoutSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initSetFragment();
                Toast.makeText(MainActivity.this,"点击设置",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void destoryView() {

    }

    @Override
    protected void toolBarBackListener(View view) {

    }

    private void initRunFragment(){
        layoutBook.setSelected(false);
        layoutChat.setSelected(false);
        layoutNearBy.setSelected(true);
        layoutSet.setSelected(false);
    }


    private void initChatFragment(){
        layoutBook.setSelected(false);
        layoutChat.setSelected(true);
        layoutNearBy.setSelected(false);
        layoutSet.setSelected(false);
    }

    private void initBookFragment(){
        layoutBook.setSelected(true);
        layoutChat.setSelected(false);
        layoutNearBy.setSelected(false);
        layoutSet.setSelected(false);
    }


    private void initSetFragment(){

        layoutBook.setSelected(false);
        layoutChat.setSelected(false);
        layoutNearBy.setSelected(false);
        layoutSet.setSelected(true);
    }


    private void hideFragment(android.support.v4.app.FragmentTransaction transaction){
        if(runFragment != null){
            transaction.hide(runFragment);
        }

        if(chatFragment != null){
            transaction.hide(chatFragment);
        }
        if(bookFragment != null){
            transaction.hide(bookFragment);
        }
        if(setFragment != null){
            transaction.hide(setFragment);
        }
    }
}
