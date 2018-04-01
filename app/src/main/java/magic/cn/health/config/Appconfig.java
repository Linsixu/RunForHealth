package magic.cn.health.config;

import android.annotation.SuppressLint;
import android.os.Environment;


/**
 * @ClassName: Appconfig
 * @date 2015-11-17 下午2:48:33
 */
@SuppressLint("SdCardPath")
public class Appconfig {

	/**
	 * 存放发送图片的目录
	 */
	public static String BMOB_PICTURE_PATH = Environment.getExternalStorageDirectory()	+ "/bmobimdemo/image/";

	/**
	 * 我的头像保存目录
	 */
	public static String MyAvatarDir = "/sdcard/bmobimdemo/avatar/";
	/**
	 * 拍照回调
	 */
	public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;//拍照修改头像
	public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;//本地相册修改头像
	public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;//系统裁剪头像

	public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;//拍照
	public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;//本地图片
	public static final int REQUESTCODE_TAKE_LOCATION = 0x000003;//位置

	public static final int REQUESTCODE_CHOOSE_PHOTO = 0x000004;
	public static final int REQUESTCODE_CHANGE_INFO = 0x000005;
	public static final int REQUESTCODE_CHANGE_INFO_NICK = 0x000006;
	public static final int REQUESTCODE_CHANGE_INFO_SIGNATURE = 0x000007;
	public static final String EXTRA_STRING = "extra_string";

	/**
	 * 一些布局类型标识符
	 */
	public static final int LAYOUT_ONE_TV  = 0;
	public static final int LAYOUT_TV_SWITCH = 1;
	public static final int LAYOUT_TWO_TV  = 1;
	public static final int LAYOUT_IV_TV  = 1;
	public static final int LAYOUT_TV_IV  = 2;
	public static final int LAYOUT_TWOTV_IV  = 2;
	public static final int LAYOUT_ONE_BTN = 3;

	public static final String ACTION_REGISTER_SUCCESS_FINISH ="register.success.finish";//注册成功之后登陆页面退出


	public static final String NOT_ADD_USER_SELF = "不能添加自己";

	//是否是debug模式
	public static final boolean DEBUG = true;
	//好友请求：未读-未添加->接收到别人发给我的好友添加请求，初始状态
	public static final int STATUS_VERIFY_NONE = 0;
	//好友请求：已读-未添加->点击查看了新朋友，则都变成已读状态
	public static final int STATUS_VERIFY_READED = 2;
	//好友请求：已添加
	public static final int STATUS_VERIFIED = 1;
	//好友请求：拒绝
	public static final int STATUS_VERIFY_REFUSE = 3;
	//好友请求：我发出的好友请求-暂未存储到本地数据库中
	public static final int STATUS_VERIFY_ME_SEND = 4;



	public static int TYPE_CLOSE = 11;
	public static int TYPE_OEPN_SMILE = 12;
	public static int TYPE_CLOSE_SMILE = -12;
	public static int TYPE_OPEN_KEYBOARD = 13;
	public static int TYPE_CLOSE_KEYBOARD = -13;
	public static int TYPE_OPEN_FUCTION = 14;
	public static int TYPE_CLOSE_FUNCTION = -14;
	public static int TYPE_DELAY_SHOW_SMILE = 15;
	public static int TYPE_DELAY_SHOW_FUNCTION = -15;

	public static int TYPE_OPEN_SMILE_AND_CLOSE_KEYBOARD = 16;
	public static int TYPE_OPEN_KEYBOARD_AND_CLOSE_SMILE = 17;

	public static int TYPE_OPEN_FUNCTION_AND_CLOSE_KEYBOARD = 18;
	public static int TYPE_OPEN_KEYBOARD_AND_CLOSE_FUNCTION = 19;

	public static int TYPE_OPEN_SMILE_AND_CLOSE_FUNCTION = 20;
	public static int TYPE_OPEN_FUNCTION_AND_CLOSE_SMILE = 21;
}
