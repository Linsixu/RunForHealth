package magic.cn.health.model;

import android.content.Context;

import magic.cn.health.app.App;

/**
 * @author 林思旭
 * @since 2018/3/8
 */

public abstract class BaseModel {
    public int CODE_NULL=1000;
    public static int CODE_NOT_EQUAL=1001;

    public static final int DEFAULT_LIMIT=20;

    public Context getContext(){
        return App.getInstance();
    }
}
