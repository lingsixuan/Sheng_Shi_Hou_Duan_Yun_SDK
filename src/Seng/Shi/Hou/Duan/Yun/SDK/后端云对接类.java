package Seng.Shi.Hou.Duan.Yun.SDK;

import Seng.Shi.Hou.Duan.Yun.SDK.Exception.解包出错;
import Seng.Shi.Hou.Duan.Yun.SDK.data.版本数据类;
import Seng.Shi.Hou.Duan.Yun.SDK.data.账户数据类;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import ling.android.操作.*;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

/**
 * API接口的具体实现。
 * 请注意！服务端并没有使用https协议，这意味着您需要在AndroidManifest.xml中加入android:usesCleartextTraffic="true"设置项。
 * 另外，此类需要有访问网络的权限，您需要在AndroidManifest.xml中声明网络权限。
 *
 * @author 驻魂圣使
 */
public class 后端云对接类 implements API {

    protected int 项目ID;

    protected boolean 摘要校验 = false;

    protected String 摘要盐值 = "";

    protected int 允许时间误差 = 30;

    protected static long 服务器时间;

    protected String 服务器地址 = "https://api.lingsixuan.top/api/";

    protected static okhttp.证书 证书链 = new okhttp.证书()
            .add("api.lingsixuan.top", "sha256/cOZS1D14NVyXRbTjP2SAhTpP4pMHaHWrvT6DJY/tPQ8=");

    protected static boolean isSign = false;

    protected static String serverPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCGP6tTIDE6miH9XINvSZ4MjyHHElpVEvaweIFAnLmFBcIRIXO4iDGh0DVxHH7VmYtAGKTgfZX9NF6agqMoLFQFavnFP3WcCv92kHibduOaV1gymHKffTRVMOXSmTo6/Ya891RpnrkEQi/5Js1zZMPdViB7IHXv5AqOVJ7RfZvZEQIDAQAB";

    protected boolean isPinning = false;

    protected Context context;

    protected String ANDROID_ID;

    protected String 原始数据 = "";

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * 此构造函数生成的对象可以显式规定签名校验和签名密钥的值
     *
     * @param context 当前上下文环境
     * @param 项目ID    此处为你要对接的项目ID，可以在圣使后端云客户端中查看。
     *                API靠项目ID来区分不同项目，
     *                这个值不会改变。
     * @param 摘要校验    这个值用来确定要不要开启数据包摘要检查，开启之后服务器会对下行数据附加摘要，会检查上行数据的摘要。
     *                客户端会对上行数据附加摘要，会检查下行数据的摘要，
     *                以此来保证数据没有被篡改。
     *                如果启用此设置，服务器会强制要求所有上行数据必须附加摘要，
     *                如果关闭此设置，则以上效果都不会生效。
     *                这个值要和项目的设置一致才能正常工作。
     * @param 摘要盐值    摘要校验的盐值，后端云通过这个盐值来保证通信安全。
     *                在摘要校验启用时，数据包的摘要和校验都通过这个盐值进行，
     *                盐值可以在APP内更换，但是更换盐值之后，没有同步更新的APP就无法和服务器建立连接了。
     *                摘要要和项目的设置值一致才能正常工作。
     */
    public 后端云对接类(Context context, int 项目ID, boolean 摘要校验, String 摘要盐值) {
        this.context = context;
        this.项目ID = 项目ID;
        this.摘要校验 = 摘要校验;
        this.摘要盐值 = 摘要盐值;
        this.ANDROID_ID = Settings.System.getString(context.getContentResolver(),
                Settings.System.ANDROID_ID);
    }


    /**
     * 此构造函数生成的对象可以显式规定签名校验和签名密钥的值
     *
     * @param context 当前上下文环境
     * @param 项目ID    此处为你要对接的项目ID，可以在圣使后端云客户端中查看
     *                API靠项目ID来区分不同项目
     *                这个值不会改变
     * @param 摘要校验    这个值用来确定要不要开启数据包摘要检查，开启之后服务器会对下行数据附加摘要，会检查上行数据的摘要。
     *                客户端会对上行数据附加摘要，会检查下行数据的摘要，
     *                以此来保证数据没有被篡改。
     *                如果启用此设置，服务器会强制要求所有上行数据必须附加摘要，
     *                如果关闭此设置，则以上效果都不会生效。
     *                这个值要和项目的设置一致才能正常工作。
     * @param 摘要盐值    摘要校验的盐值，后端云通过这个盐值来保证通信安全。
     *                在摘要校验启用时，数据包的摘要和校验都通过这个盐值进行，
     *                盐值可以在APP内更换，但是更换盐值之后，没有同步更新的APP就无法和服务器建立连接了。
     *                摘要要和项目的设置值一致才能正常工作。
     * @param 允许时间误差  SDK将检查收到的数据包中携带的时间戳，如果时间戳相对当前时间差距超过此阈值（单位秒），则不接受此数据包。
     *                如果你的用户距离服务器（上海）比较远，或者网络波动较大，那么这个阈值需要设置的长一些。默认值30秒。
     */
    public 后端云对接类(Context context, int 项目ID, boolean 摘要校验, String 摘要盐值, int 允许时间误差) {
        this.context = context;
        this.项目ID = 项目ID;
        this.摘要校验 = 摘要校验;
        this.摘要盐值 = 摘要盐值;
        this.允许时间误差 = 允许时间误差;
        this.ANDROID_ID = Settings.System.getString(context.getContentResolver(),
                Settings.System.ANDROID_ID);
    }

    /**
     * 此构造函数生成的对象可以显式规定签名校验和签名密钥的值
     *
     * @param context 当前上下文环境。
     * @param 项目ID    此处为你要对接的项目ID，可以在圣使后端云客户端中查看。
     *                API靠项目ID来区分不同项目，
     *                这个值不会改变。
     * @param 摘要校验    这个值用来确定要不要开启数据包摘要检查，开启之后服务器会对下行数据附加摘要，会检查上行数据的摘要。
     *                客户端会对上行数据附加摘要，会检查下行数据的摘要，
     *                以此来保证数据没有被篡改。
     *                如果启用此设置，服务器会强制要求所有上行数据必须附加摘要，
     *                如果关闭此设置，则以上效果都不会生效。
     *                这个值要和项目的设置一致才能正常工作。
     * @param 摘要盐值    摘要校验的盐值，后端云通过这个盐值来保证通信安全。
     *                在摘要校验启用时，数据包的摘要和校验都通过这个盐值进行，
     *                盐值可以在APP内更换，但是更换盐值之后，没有同步更新的APP就无法和服务器建立连接了。
     *                摘要要和项目的设置值一致才能正常工作。
     * @param 允许时间误差  SDK将检查收到的数据包中携带的时间戳，如果时间戳相对当前时间差距超过此阈值（单位秒），则不接受此数据包。
     *                如果你的用户距离服务器（上海）比较远，或者网络波动较大，那么这个阈值需要设置的长一些。默认值30秒。
     * @param 服务器地址   此处用于指定服务器的地址，一般情况下您无需关心。
     */
    public 后端云对接类(Context context, int 项目ID, boolean 摘要校验, String 摘要盐值, int 允许时间误差, String 服务器地址) {
        this.context = context;
        this.项目ID = 项目ID;
        this.摘要校验 = 摘要校验;
        this.摘要盐值 = 摘要盐值;
        this.允许时间误差 = 允许时间误差;
        this.服务器地址 = 服务器地址;
        this.ANDROID_ID = Settings.System.getString(context.getContentResolver(),
                Settings.System.ANDROID_ID);
    }

    /**
     * 此构造函数生成的对象不会启用签名校验，这非常危险！
     *
     * @param context 当前上下文环境。
     * @param 项目ID    此处为你要对接的项目ID，可以在圣使后端云客户端中查看。
     *                API靠项目ID来区分不同项目，
     *                这个值不会改变。
     */
    @Deprecated
    public 后端云对接类(Context context, int 项目ID) {
        this.context = context;
        this.项目ID = 项目ID;
        this.ANDROID_ID = Settings.System.getString(context.getContentResolver(),
                Settings.System.ANDROID_ID);
    }


    /**
     * 读取公告
     *
     * @param 回调 回调方法
     */
    @Override
    public void 读取公告(@NotNull 读取公告回调 回调) {
        try {
            JSONObject json = new JSONObject();
            json.put("xm_id", 项目ID);

            发送数据("读取公告.php", json, new 收到数据() {
                @Override
                public void 错误(String 错误详情) {
                    回调.读取公告失败(错误详情);
                }

                @Override
                public void 收到响应(Response request, String temp) throws JSONException, 解包出错, IOException {
                    if (request.code() != 200) {
                        throw new 解包出错("网络异常");
                    }
                    JSONObject json = 解包响应数据(temp);
                    if (!json.getBoolean("状态"))
                        throw new 解包出错(json.getString("信息"));
                    回调.读取公告成功(json.getString("信息"));
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param 当前版本号 服务器依靠版本号来判断当前请求的客户端版本和服务器上的众多版本之间的新旧关系。
     *              当您在管理端发布新版本时，版本号一栏请务必填写和APP一样的版本号，且新的版本号必须比历史所有版本都大。
     *              此参数建议和您项目的versionCode的值相同。
     * @param 回调    回调方法，此值为null时，检查更新的请求不会实际提交到服务器。
     */
    @Override
    public void 检查更新(int 当前版本号, @NotNull 检查更新回调 回调) {
        try {
            JSONObject json = new JSONObject();
            json.put("xm_id", 项目ID);
            json.put("v", 当前版本号);

            发送数据("检查更新.php", json, new 后端云对接类.收到数据() {
                @Override
                public void 错误(String 错误详情) {
                    回调.检查更新出错(错误详情);
                }

                @Override
                public void 收到响应(Response request, String temp) throws JSONException, 解包出错, IOException {
                    if (request.code() != 200)
                        throw new 解包出错("网络异常");
                    JSONObject json = 解包响应数据(temp);
                    if (!json.getBoolean("状态")) {
                        throw new 解包出错(json.getString("信息"));
                    }
                    json = json.getJSONObject("信息");
                    if (json.getInt("更新") == 1) {
                        回调.发现更新(new 版本数据类(json));
                    } else {
                        回调.没有更新();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void 使用卡密(int 账户ID, String 卡号, @NotNull 使用卡密回调 回调) {
        try {
            JSONObject json = new JSONObject();
            json.put("xm_id", 项目ID);
            json.put("key", 卡号);
            json.put("ID", 账户ID);

            发送数据("使用卡密.php", json, new 收到数据() {
                @Override
                public void 错误(String 错误详情) {
                    回调.使用失败(错误详情);
                }

                @Override
                public void 收到响应(Response request, String temp) throws JSONException, 解包出错, IOException {
                    if (request.code() != 200)
                        throw new 解包出错("网络异常");
                    JSONObject json = 解包响应数据(temp);
                    if (json.getBoolean("状态")) {
                        回调.使用成功(卡号);
                    } else {
                        回调.使用失败(json.getString("信息"));
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录账号
     *
     * @param 账号 登录账户的账号。
     * @param 密码 登录账户的密码。
     * @param 回调 回调方法，请注意！回调方法为null时，登录请求并不会真的提交到服务器！
     */
    @Override
    public void 登录账号(String 账号, String 密码, @NotNull 登录回调 回调) {
        try {
            JSONObject json = new JSONObject();
            json.put("xm_id", 项目ID);
            json.put("user", 账号);
            json.put("pass", 密码);
            json.put("UID", ANDROID_ID);

            发送数据("登录.php", json, new 收到数据() {
                @Override
                public void 错误(String 错误详情) {
                    回调.登录失败(错误详情);
                }

                @Override
                public void 收到响应(Response request, String temp) throws JSONException, 解包出错, IOException {
                    if (request.code() != 200)
                        throw new 解包出错("网络异常");
                    JSONObject json = 解包响应数据(temp);
                    if (json.getBoolean("状态")) {
                        回调.登录成功(new 账户数据类(json.getJSONObject("信息")));
                    } else {
                        回调.登录失败(json.getString("信息"));
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void 注册账号(String 账号, String 密码, 注册回调 回调) {
        注册账号(账号, 密码, "默认昵称", "", 回调);
    }

    @Override
    public void 注册账号(String 账号, String 密码, String 昵称, 注册回调 回调) {
        注册账号(账号, 密码, 昵称, "", 回调);
    }

    @Override
    public void 注册账号(String 账号, String 密码, String 昵称, String 邮箱, @NotNull 注册回调 回调) {
        try {
            JSONObject json = new JSONObject();
            json.put("xm_id", 项目ID);
            json.put("user", 账号);
            json.put("pass", 密码);
            json.put("name", 昵称);
            json.put("mail", 邮箱);
            json.put("UID", ANDROID_ID);

            发送数据("注册.php", json, new 收到数据() {
                @Override
                public void 错误(String 错误详情) {
                    回调.注册失败(错误详情);
                }

                @Override
                public void 收到响应(Response request, String temp) throws JSONException, 解包出错, IOException {
                    if (request.code() != 200)
                        throw new 解包出错("网络异常");
                    JSONObject json = 解包响应数据(temp);
                    if (json.getBoolean("状态")) {
                        回调.注册成功(账号, 密码);
                    } else throw new 解包出错(json.getString("信息"));
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用卡密直接登录，要调用此API需要在圣使后端云客户端内启用项目的单码登录设置。
     * 请注意，切换单码登录设置的值，将会导致您项目中的卡密数据定义模糊，污染卡密。
     * 如果您必须切换登录模式，系统会代您删除所有脏污数据，无法撤销！
     *
     * @param 卡号 卡号
     * @param 回调 回调
     */
    @Override
    public void 卡密登录(String 卡号, @NotNull 卡密登录回调 回调) {
        try {
            JSONObject json = new JSONObject();
            json.put("xm_id", 项目ID);
            json.put("UID", ANDROID_ID);
            json.put("key", 卡号);
            发送数据("卡密登录.php", json, new 收到数据() {
                @Override
                public void 错误(String 错误详情) {
                    回调.登录失败(错误详情);
                }

                @Override
                public void 收到响应(Response request, String temp) throws JSONException, 解包出错, IOException {
                    if (request.code() != 200)
                        throw new 解包出错("网络异常");
                    JSONObject json = 解包响应数据(temp);
                    if (json.getBoolean("状态")) {
                        回调.登录成功(json.getString("信息"), json.getLong("到期时间") * 1000);
                    } else {
                        throw new 解包出错(json.getString("信息"));
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解除一张卡密绑定的设备，要调用此API需要在圣使后端云客户端内启用项目的单码登录设置且启用设备绑定功能。
     * 请注意，切换单码登录设置的值，将会导致您项目中的卡密数据定义模糊，污染卡密。
     * 如果您必须切换登录模式，系统会代您删除所有脏污数据，无法撤销！
     *
     * @param 卡号 卡号
     * @param 回调 回调
     */
    @Override
    public void 解绑卡密(String 卡号, @NotNull 解绑卡密回调 回调) {
        try {
            JSONObject json = new JSONObject();
            json.put("xm_id", 项目ID);
            json.put("UID", ANDROID_ID);
            json.put("key", 卡号);
            发送数据("解绑卡密.php", json, new 收到数据() {
                @Override
                public void 错误(String 错误详情) {
                    回调.解绑失败(错误详情);
                }

                @Override
                public void 收到响应(Response request, String temp) throws JSONException, 解包出错, IOException {
                    if (request.code() != 200)
                        throw new 解包出错("网络异常");
                    JSONObject json = 解包响应数据(temp);
                    if (json.getBoolean("状态")) {
                        回调.解绑成功(json.getString("信息"));
                    } else {
                        throw new 解包出错(json.getString("信息"));
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    protected void 发送数据(String api, JSONObject json, @NotNull 收到数据 回调) {
        new okhttp(isPinning ? 证书链 : new okhttp.证书()).取异步对象()
                .post(服务器地址 + api, 生成数据(json), new okhttp.异步请求.响应() {
                    @Override
                    public void 请求出错(@NotNull Call call, @NotNull IOException e) {
                        if (Objects.requireNonNull(e.getMessage()).indexOf("Certificate pinning failure!", 0) != -1) {
                            回调.错误("不被信任的证书！");
                        } else {
                            回调.错误("网络异常");
                        }
                    }

                    @Override
                    public void 请求完成(@NotNull Call call, @NotNull Response response) throws IOException {
                        new Thread(() -> {
                            String temp;
                            try {
                                temp = response.body().string();
                            } catch (IOException e) {
                                mainHandler.post(() -> {
                                    回调.错误("IO错误");
                                });
                                return;
                            }
                            mainHandler.post(() -> {
                                try {
                                    回调.收到响应(response, temp);
                                } catch (JSONException e) {
                                    回调.错误("解析响应失败");
                                } catch (Seng.Shi.Hou.Duan.Yun.SDK.Exception.解包出错 解包出错) {
                                    回调.错误(解包出错.异常);
                                } catch (IOException e) {
                                    回调.错误("IO错误");
                                }
                            });
                        }).start();
                    }
                });
    }


    protected RequestBody 生成数据(JSONObject json) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("json", json.toString());
        if (摘要校验) {
            builder.add("MD5", 加解密操作.MD5加密(json + 摘要盐值));
        }
        return builder.build();
    }


    protected JSONObject 解包响应数据(String 响应数据) throws 解包出错 {
        try {
            this.原始数据 = 响应数据;
            JSONObject json = new JSONObject(响应数据);
            //检查数据包签名
            if (摘要校验) {
                if (!json.has("data") || !json.has("MD5"))
                    throw new 解包出错("数据包没有签名！可能已经被篡改。");
                if (!加解密操作.MD5加密(json.getString("data") + 摘要盐值).equals(json.getString("MD5")))
                    throw new 解包出错("数据包签名损坏！可能已经被篡改");
                if (isSign) {
                    if (!json.has("sign"))
                        throw new 解包出错("数据包签名不完整！可能已经被篡改");
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        RSA rsa = new RSA(new RSA.RSAKey(serverPublicKey, ""));
                        String data = new String(
                                rsa.公钥解密(Base64.getDecoder().decode(json.getString("sign").getBytes())));
                        if (!data.equals(json.getString("MD5")))
                            throw new 解包出错("数据包签名损坏！可能已经被篡改");
                    }
                }
                json = json.getJSONObject("data");
            } else {
                if (json.has("data") || json.has("MD5"))
                    throw new 解包出错("数据包中携带签名，您的设置可能和服务器设置不一致！请参考SDK文档");
            }
            //检查时间戳
            if (json.getLong(
                    "时间") - (new Date().getTime() / 1000) > 允许时间误差 || (new Date().getTime() / 1000) - json.getLong(
                    "时间") > 允许时间误差)
                throw new 解包出错("时间误差超出阈值！");
            服务器时间 = json.getLong("时间");
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
            throw new 解包出错(e.getLocalizedMessage());
        } catch (Exception e) {
            throw new 解包出错("数据包签名损坏！可能已经被篡改");
        }
    }

    /**
     * 设置是否启用证书固定
     * 启用证书固定之后，SDK会验证服务器的公钥证书是否可信，可有效防范中间人攻击。
     * 请注意！服务器提供商可能在任何时候更换证书，证书更换之后APP将无法连接至服务器！
     * 更换证书将会提前在APP内通知开发者
     *
     * @param pinning 是否启用？
     */
    @Override
    public void setPinning(boolean pinning) {
        this.isPinning = pinning;
    }

    /**
     * SDK内部已经设置好了服务器的公钥证书，如果服务器更换了公钥证书而没有及时提供新的SDK
     * 那么您可以通过此方法来设置新的公钥证书。
     * 证书链所有对象共享。
     *
     * @param 证书链
     */
    @Override
    public void set证书链(okhttp.证书 证书链) {
        后端云对接类.证书链 = 证书链;
    }


    /**
     * 执行判断会员等影响计费的敏感操作时，不要使用客户端本地时间。
     * 因为简单的修改系统时间就能绕过。
     * 您可以利用此方法来获取服务器时间。
     * 不过，您需要注意，调用此方法并不会真的请求服务器。
     * 而是将上一次请求服务器时服务器响应的时间戳返回给您。
     * 换句话说，此方法获取到的时间是客户端上次请求服务器时的时间。
     * 如果客户端在本次运行期间没有请求过服务器的任何API，那么此方法的返回值将会是0！
     *
     * @return 上次请求服务器时服务器返回的时间戳
     */
    public long get服务器时间() {
        return 服务器时间 * 1000;
    }

    /**
     * 读取服务器上一次响应的数据
     *
     * @return 上一次响应
     */
    @Override
    public String get原始数据() {
        return this.原始数据;
    }

    /**
     * 是否开启数字签名，启用此设置后，SDK会校验服务器响应数据的数字签名，用以保证数据安全
     *
     * @param Sign 是否启用数字签名
     */
    @Override
    public void setSign(boolean Sign) {
        后端云对接类.isSign = Sign;
    }

    /**
     * 设置服务器的公钥，SDK中已经内置了公钥证书，一般情况下您无需改动。
     *
     * @param publicKey 公钥
     */
    @Override
    public void setServerPublicKey(String publicKey) throws Exception {
        RSA rsa = new RSA(new RSA.RSAKey(publicKey, ""));
        rsa.公钥加密("aaaa".getBytes());
        后端云对接类.serverPublicKey = publicKey;
    }

}
