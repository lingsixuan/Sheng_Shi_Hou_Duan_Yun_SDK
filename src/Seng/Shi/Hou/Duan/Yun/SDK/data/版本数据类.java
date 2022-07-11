package Seng.Shi.Hou.Duan.Yun.SDK.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 保存更新数据的类
 */
public class 版本数据类 {

    private final int 版本号;
    private final String 版本名;
    private final boolean 强制更新;
    private final String 下载链接;
    private final long 发布时间;
    private final String 发布IP;
    private final String 更新内容;

    public 版本数据类(String 数据) throws JSONException {
        this(new JSONObject(数据));
    }

    public 版本数据类(JSONObject json) throws JSONException {
        this.版本号 = json.getInt("版本号");
        this.版本名 = json.getString("版本名称");
        this.强制更新 = json.getInt("强制更新") == 1;
        this.下载链接 = json.getString("更新链接");
        this.发布时间 = json.getLong("发布时间") * 1000;
        this.发布IP = json.getString("IP");
        this.更新内容 = json.getString("更新内容");
    }

    public int get版本号() {
        return 版本号;
    }

    public String get版本名() {
        return 版本名;
    }

    public boolean is强制更新() {
        return 强制更新;
    }

    public String get下载链接() {
        return 下载链接;
    }

    public long get发布时间() {
        return 发布时间;
    }

    public String get发布IP() {
        return 发布IP;
    }

    public String get更新内容() {
        return 更新内容;
    }
}
