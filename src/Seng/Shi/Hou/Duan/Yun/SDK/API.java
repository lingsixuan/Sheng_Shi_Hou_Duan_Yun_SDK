package Seng.Shi.Hou.Duan.Yun.SDK;

import Seng.Shi.Hou.Duan.Yun.SDK.Exception.解包出错;
import Seng.Shi.Hou.Duan.Yun.SDK.data.版本数据类;
import Seng.Shi.Hou.Duan.Yun.SDK.data.账户数据类;
import ling.android.操作.okhttp;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import ling.json.JSONException;

import java.io.IOException;

/**
 * 此处定义了后端云所有可供调用的API，您无需关心它具体如何运作，只需要调用它即可
 *
 * @author 驻魂圣使
 */
public interface API {

    /**
     * 登录账户，登录的账户必须在后端云中存在，如果登录的账户不存在，那么登录不会成功。你需要先调用注册API来将账号添加至后端云中，然后登录才能成功
     *
     * @param 账号 登录账户的账号
     * @param 密码 登录账户的密码
     * @param 回调 回调方法
     */

    void 登录账号(String 账号, String 密码, 登录回调 回调);

    /**
     * 注册一个账户到后端云中
     * 此方法用于不需要邮箱的项目，如果您的项目设置了必须输入邮箱，那么此方法调不通
     * 此方法注册的账户会使用默认昵称
     * 使用默认昵称时，后台可能不好区分账户，所以不建议使用此方法
     *
     * @param 账号 账号
     * @param 密码 密码
     * @param 回调 回调方法
     */
    @Deprecated
    void 注册账号(String 账号, String 密码, 注册回调 回调);

    /**
     * 注册一个账户到后端云中，此如果您的项目设置了必须输入邮箱，那么此方法调不通。
     *
     * @param 账号 账号
     * @param 密码 密码
     * @param 昵称 昵称
     * @param 回调 回调方法
     */
    void 注册账号(String 账号, String 密码, String 昵称, 注册回调 回调);

    /**
     * 此方法用于使用卡密
     * 后端云的卡密绑定账户，既卡密的面额在使用之后就加到账户的会员时间之中
     * 使用卡密过后，账户的会员时间会增加，您只需要在登录时判断会员时间戳是否大于当前时间戳即可
     * 无需重复使用卡密
     * 卡密使用过后既销毁，无法重复使用
     * 请注意！使用卡密之后您需要重新调用登录接口获取账户最新数据！！
     *
     * @param 账户 要激活的账户的数据，您可以传入登录回调传入的{@link 账户数据类}
     * @param 卡号 要使用的卡号，卡密不可重复使用，一张卡密只能一个账户使用，卡密使用过后即可丢弃
     */
    void 使用卡密(账户数据类 账户, String 卡号, 使用卡密回调 回调);

    /**
     * 注册一个账户到后端云中
     * 您无需检查邮箱格式是否正确
     * 服务器会代您执行检查
     * 或者您可以调用验证码接口验证邮箱合法性
     *
     * @param 账号 账号
     * @param 密码 密码
     * @param 昵称 昵称
     * @param 邮箱 邮箱
     * @param 回调 回调方法
     */
    void 注册账号(String 账号, String 密码, String 昵称, String 邮箱, 注册回调 回调);


    /**
     * 检查服务器是否有更新版本
     *
     * @param 当前版本号 服务器依靠版本号来判断当前请求的客户端版本和服务器上的众多版本之间的新旧关系。
     *              当您在管理端发布新版本时，版本号一栏请务必填写和APP一样的版本号，且新的版本号必须比历史所有版本都大。
     *              此参数建议和您项目的versionCode的值相同
     * @param 回调    回调方法
     */
    void 检查更新(int 当前版本号, 检查更新回调 回调);

    /**
     * 读取项目公告
     *
     * @param 回调 回调方法
     */
    void 读取公告(读取公告回调 回调);

    /**
     * 设置是否启用证书固定
     * 启用证书固定之后，SDK会验证服务器的公钥证书是否可信，可有效防范中间人攻击。
     * 请注意！服务器提供商可能在任何时候更换证书，证书更换之后APP将无法连接至服务器！
     * 更换证书将会提前在APP内通知开发者
     *
     * @param pinning 是否启用？
     */
    void setSSLPinning(boolean pinning);

    /**
     * 获取当前信任的自签名证书集合
     *
     * @return 自签名证书集
     */
    okhttp.自签名证书集 getTrustSSL();

    /**
     * SDK内部已经设置号了服务器的公钥证书，如果服务器更换了公钥证书而没有即使提供新的SDK
     * 那么您可以通过此方法来设置新的公钥证书。
     * 证书链所有对象共享。
     *
     * @param 证书链
     */
    void set证书链(okhttp.证书固定数据 证书链);

    /**
     * 使用卡密直接登录，要调用此API需要在圣使后端云客户端内启用项目的单码登录设置。
     * 请注意，切换单码登录设置的值，将会导致您项目中的卡密数据定义模糊，污染卡密。
     * 如果您必须切换登录模式，系统会代您删除所有脏污数据，无法撤销！
     *
     * @param 卡号 卡号
     * @param 回调 回调
     */
    void 卡密登录(String 卡号, @NotNull 卡密登录回调 回调);

    /**
     * 解除一张卡密绑定的设备，要调用此API需要在圣使后端云客户端内启用项目的单码登录设置且启用设备绑定功能。
     * 请注意，切换单码登录设置的值，将会导致您项目中的卡密数据定义模糊，污染卡密。
     * 如果您必须切换登录模式，系统会代您删除所有脏污数据，无法撤销！
     *
     * @param 卡号 卡号
     * @param 回调 回调
     */
    void 解绑卡密(String 卡号, @NotNull 解除绑定回调 回调);

    /**
     * 如果您的项目启用了绑定设备功能<br>
     * 那么您将会需要这个API<br>
     * 此API可以解除账号和设备的绑定<br>
     * 请注意！<br>
     * 解除绑定不会验证请求的设备是否是绑定的设备<br>
     * 既只要拥有账号和密码，且账号解绑间隔时间内没有解除过绑定，就可以请求解除绑定！
     *
     * @param user 用户账号
     * @param pass 账号密码
     * @param 回调   回调方法
     */
    void 解绑账号(String user, String pass, @NotNull 解除绑定回调 回调);

    /**
     * 是否开启数字签名，启用此设置后，SDK会校验服务器响应数据的数字签名，用以保证数据安全
     *
     * @param Sign 是否启用数字签名
     */
    void setSign(boolean Sign);

    /**
     * 设置服务器的公钥，SDK中已经内置了公钥证书，一般情况下您无需改动。
     *
     * @param publicKey 公钥
     * @throws Exception 公钥格式错误
     */
    void setServerPublicKey(String publicKey) throws Exception;

    /**
     * 读取服务器上一次响应的数据
     *
     * @return 上一次响应
     */
    String get原始数据();

    /**
     * 设置服务器地址
     *
     * @param url 服务器地址,以/api结尾
     */
    void setHttpUrl(String url);

    long get服务器时间();

    /**
     * 修改账户的密码
     *
     * @param user_data 登录API返回的账户数据对象
     * @param 原始密码      账号的原始密码
     * @param 修改后的密码    修改后的密码
     */
    void 修改密码(账户数据类 user_data, String 原始密码, String 修改后的密码, @NotNull 修改密码回调 回调);

    interface 修改密码回调 {
        void 修改成功(String 提示);

        void 修改失败(String 错误);
    }

    interface 解除绑定回调 {
        void 解绑成功(String 提示);

        void 解绑失败(String 原因);
    }

    interface 卡密登录回调 {
        void 登录成功(String 提示, long 到期时间);

        void 登录失败(String 失败原因);
    }

    interface 读取公告回调 {

        void 读取公告成功(String 公告);

        void 读取公告失败(String 错误);
    }

    interface 检查更新回调 {
        /**
         * 发现更新版本时将会回调此方法
         *
         * @param 版本数据 更新版本数据
         */
        void 发现更新(版本数据类 版本数据);

        void 没有更新();

        void 检查更新出错(String 错误描述);
    }

    interface 使用卡密回调 {
        void 使用成功(String 卡号);

        void 使用失败(String 失败原因);
    }

    interface 注册回调 {
        /**
         * 此处传入的账号和密码均为用户输入的值，可以在注册成功后马上调用登录接口来获取账户数据
         */
        void 注册成功(String 账号, String 密码);

        void 注册失败(String 失败原因);
    }

    interface 登录回调 {
        void 登录成功(账户数据类 data);

        void 登录失败(String 失败原因);
    }

    interface 收到数据 {
        void 错误(String 错误详情);

        void 收到响应(Response request, String temp) throws JSONException, 解包出错, IOException;
    }
}
