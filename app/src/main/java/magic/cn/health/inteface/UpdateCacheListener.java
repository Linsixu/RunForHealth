package magic.cn.health.inteface;

import cn.bmob.newim.listener.BmobListener1;
import cn.bmob.v3.exception.BmobException;

/**
 * @author 林思旭
 * @since 2018/3/8
 */

public abstract class UpdateCacheListener extends BmobListener1 {

    public abstract void done(BmobException e);

    @Override
    protected void postDone(Object o, BmobException e) {

    }
}
