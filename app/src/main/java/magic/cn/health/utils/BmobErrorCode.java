package magic.cn.health.utils;

/**
 * @author 林思旭
 * @since 2018/3/12
 */

public class BmobErrorCode {


    public static String errorCodeConvertContent(int code){
        switch (code){
            case 101:
                return "用户名或密码错误";
            case 108:
                return "必须填写用户名和密码";
            case 139:
                return "角色名称已存在";
            case 202:
                return "用户名已存在";
            case 9015:
                return "发送不知明错误";
            default:
                return "还不知道的错误";
        }
    }
}
