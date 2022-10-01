package Seng.Shi.Hou.Duan.Yun.SDK.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 此类中保存了用户账户的数据，您可以将它视为用户的账户。
 * 此类实现了Serializable接口，可以轻易在Activity之间传递
 */
public final class 账户数据类 implements Serializable {
    private final int ID;
    private final String 账号;
    private final String 昵称;
    private final String 邮箱;
    private final long 会员;
    private final long 注册时间;
    private final int 绑定设备;
    private final long 解绑时间;
    private final String 授权码;

    public 账户数据类(JSONObject json) throws JSONException {
        ID = json.getInt("ID");
        账号 = json.getString("账号");
        昵称 = json.getString("昵称");
        邮箱 = json.has("邮箱") ? json.getString("邮箱") : "";
        会员 = json.getLong("会员") * 1000;
        注册时间 = json.getLong("注册时间") * 1000;
        绑定设备 = json.getInt("绑定设备");
        解绑时间 = json.getLong("解绑时间") * 1000;
        授权码 = json.has("授权码") ? json.getString("授权码") : "";
    }

    public int getID() {
        return ID;
    }

    public String get账号() {
        return 账号;
    }

    public String get昵称() {
        return 昵称;
    }

    /**
     * 用户的邮箱，如果您的项目没用强制注册必须输入邮箱，那么这里可能不会有值
     */
    public String get邮箱() {
        return 邮箱;
    }

    /**
     * 用户会员的时间戳，可以直接用时间操作类解析
     */
    public long get会员() {
        return 会员;
    }

    /**
     * 当前账户注册时的时间戳
     */
    public long get注册时间() {
        return 注册时间;
    }

    /**
     * 账户上一次登录的设备ID<br>
     * 如果您的项目启用了绑定设备功能，那么这个账户只能在这个设备上登录，除非调用解绑API解除绑定
     */
    public int get绑定设备() {
        return 绑定设备;
    }

    /**
     * 用户下一次可以解绑的时间戳，使用此时间戳减去当前时间戳，得到的结果就是间隔时间
     */
    public long get解绑时间() {
        return 解绑时间;
    }

    /**
     * 账户的授权码<br>
     * 这个字符串代表了账户的访问权限<br>
     * 每次调用登录API都会生成一个新的授权码<br>
     * 同一时间只有一个授权码生效<br>
     * 既，您可以通过此授权码检查登录是否失效
     */
    public String get授权码() {
        return 授权码;
    }
}
