package magic.cn.health.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;
import magic.cn.health.bean.NewFriend;

/**
 * @author 林思旭
 * @since 2018/3/13
 */

public class DaoSession extends AbstractDaoSession {

    private final DaoConfig newFriendDaoConfig;

    private final NewFriendDao newFriendDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        newFriendDaoConfig = daoConfigMap.get(NewFriendDao.class).clone();
        newFriendDaoConfig.initIdentityScope(type);

        newFriendDao = new NewFriendDao(newFriendDaoConfig, this);

        registerDao(NewFriend.class, newFriendDao);
    }

    public void clear() {
        newFriendDaoConfig.getIdentityScope().clear();
    }

    public NewFriendDao getNewFriendDao() {
        return newFriendDao;
    }

}
