package magic.cn.health.inteface;

import cn.bmob.newim.listener.BmobListener1;
import cn.bmob.v3.exception.BmobException;
import magic.cn.health.bean.User;

/**
 * @author 林思旭
 * @since 2018/3/8
 */

public abstract class QueryUserListener extends BmobListener1<User> {
    public abstract void done(User s, BmobException e);

    @Override
    protected void postDone(User user, BmobException e) {

    }
}
