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
    /**
     * 用户的邮箱，如果您的项目没用强制注册必须输入邮箱，那么这里可能不会有值
     */
    private final String 邮箱;
    /**
     * 用户会员的时间戳，可以直接用时间操作类解析
     */
    private final long 会员;

    /**
     * 当前账户注册时的时间戳
     */
    private final long 注册时间;
    /**
     * 账户绑定的设备ID，如果您的项目没用开启绑定设备功能的话，这个值为0
     */
    private final int 绑定设备;
    /**
     * 用户下一次可以解绑的时间戳，使用此时间戳减去当前时间戳，得到的结果就是间隔时间
     */
    private final long 解绑时间;

    public 账户数据类(JSONObject json) throws JSONException {
        ID = json.getInt("ID");
        账号 = json.getString("账号");
        昵称 = json.getString("昵称");
        邮箱 = json.has("邮箱") ? json.getString("邮箱") : "";
        会员 = json.getLong("会员") * 1000;
        注册时间 = json.getLong("注册时间") * 1000;
        绑定设备 = json.getInt("绑定设备");
        解绑时间 = json.getLong("解绑时间") * 1000;
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

    public String get邮箱() {
        return 邮箱;
    }

    public long get会员() {
        return 会员;
    }

    public long get注册时间() {
        return 注册时间;
    }

    public int get绑定设备() {
        return 绑定设备;
    }

    public long get解绑时间() {
        return 解绑时间;
    }
}
