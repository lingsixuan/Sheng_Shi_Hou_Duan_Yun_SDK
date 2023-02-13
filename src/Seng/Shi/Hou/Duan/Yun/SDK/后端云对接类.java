package Seng.Shi.Hou.Duan.Yun.SDK;

import Seng.Shi.Hou.Duan.Yun.SDK.Exception.解包出错;
import Seng.Shi.Hou.Duan.Yun.SDK.data.版本数据类;
import Seng.Shi.Hou.Duan.Yun.SDK.data.账户数据类;
import Seng.Shi.Hou.Duan.Yun.SDK.常量池.POST;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import ling.android.工具.Base64;
import ling.android.操作.*;
import okhttp3.*;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Objects;

/**
 * API接口的具体实现。
 * 此类需要有访问网络的权限，您需要在AndroidManifest.xml中声明网络权限。
 * 要使用此类，您需要在圣使后端云系统中拥有账号！
 *
 * @author 驻魂圣使
 */
public class 后端云对接类 implements API, POST {

    protected int 项目ID;

    protected boolean 摘要校验 = false;

    protected String 摘要盐值 = "";

    protected int 允许时间误差 = 30;

    protected static long 服务器时间;

    protected String 服务器地址 = "https://api.lingsixuan.top/api/";

    protected static okhttp.证书固定数据 证书链 = new okhttp.证书固定数据()
            .add("api.lingsixuan.top", "sha256/cOZS1D14NVyXRbTjP2SAhTpP4pMHaHWrvT6DJY/tPQ8=");

    protected static boolean isSign = false;

    protected static String serverPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCGP6tTIDE6miH9XINvSZ4MjyHHElpVEvaweIFAnLmFBcIRIXO4iDGh0DVxHH7VmYtAGKTgfZX9NF6agqMoLFQFavnFP3WcCv92kHibduOaV1gymHKffTRVMOXSmTo6/Ya891RpnrkEQi/5Js1zZMPdViB7IHXv5AqOVJ7RfZvZEQIDAQAB";

    protected static okhttp.自签名证书集 trustSSL = new okhttp.自签名证书集();
    protected boolean isSSLPinning = false;

    protected Context context;

    protected String ANDROID_ID;

    protected String 原始数据 = "";

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final String Android_Name = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;

    static {
        trustSSL.add("api.lingsixuan.top", "-----BEGIN CERTIFICATE-----\n" +
                "MIIGbjCCBNagAwIBAgIRAKX37J9hwUSRizXKjoa2I70wDQYJKoZIhvcNAQEMBQAw\n" +
                "WTELMAkGA1UEBhMCQ04xJTAjBgNVBAoTHFRydXN0QXNpYSBUZWNobm9sb2dpZXMs\n" +
                "IEluYy4xIzAhBgNVBAMTGlRydXN0QXNpYSBSU0EgRFYgVExTIENBIEcyMB4XDTIy\n" +
                "MDcyNDAwMDAwMFoXDTIzMDcyNDIzNTk1OVowHTEbMBkGA1UEAxMSYXBpLmxpbmdz\n" +
                "aXh1YW4udG9wMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvzlH5cTI\n" +
                "4jPngZ1swgFOB4kaxdnUe2ts4HSuFB7qUi95DbPWL624Y/XBtah00q93Mi5hOAYS\n" +
                "fukb+BKkiBgTuygMnf1gULdEHC/XJ1Yyp1jb6dC8ZxYpmsfTYXvenM97fT1uy7up\n" +
                "CiqkRoH5Al0Yh64Hev9Py0IZGhhUmTOKkA9SaMc7NQ/CzW22Xx3awBNeBQRXlEcO\n" +
                "oAS4GWk6K/e/BF7jboMRV5QJURzJQBQssnFY1VY0THDrjI9HiAk4p7ARKsQc5Fid\n" +
                "RETIOQNHRxV9/o8HsiOTZynAbjFJoPeAK808PMjJ3euFYz9cLNymIIXnW6MI3f2c\n" +
                "CWR1by9nrXHavQIDAQABo4IC6zCCAucwHwYDVR0jBBgwFoAUXzp8ERB+DGdxYdyL\n" +
                "o7UAA2f1VxwwHQYDVR0OBBYEFIF8PW1UBy0zEahhC9PiGQunQ2JAMA4GA1UdDwEB\n" +
                "/wQEAwIFoDAMBgNVHRMBAf8EAjAAMB0GA1UdJQQWMBQGCCsGAQUFBwMBBggrBgEF\n" +
                "BQcDAjBJBgNVHSAEQjBAMDQGCysGAQQBsjEBAgIxMCUwIwYIKwYBBQUHAgEWF2h0\n" +
                "dHBzOi8vc2VjdGlnby5jb20vQ1BTMAgGBmeBDAECATB9BggrBgEFBQcBAQRxMG8w\n" +
                "QgYIKwYBBQUHMAKGNmh0dHA6Ly9jcnQudHJ1c3QtcHJvdmlkZXIuY24vVHJ1c3RB\n" +
                "c2lhUlNBRFZUTFNDQUcyLmNydDApBggrBgEFBQcwAYYdaHR0cDovL29jc3AudHJ1\n" +
                "c3QtcHJvdmlkZXIuY24wHQYDVR0RBBYwFIISYXBpLmxpbmdzaXh1YW4udG9wMIIB\n" +
                "fQYKKwYBBAHWeQIEAgSCAW0EggFpAWcAdQCt9776fP8QyIudPZwePhhqtGcpXc+x\n" +
                "DCTKhYY069yCigAAAYIvSEBKAAAEAwBGMEQCICiTx8jeE+SlBc7VL/R9XurScbNR\n" +
                "s+7p38HDwceGaYzzAiBTZCEyzXh2WGLHn7IntYCV50ujbqaWbokUlcR7egwGXwB2\n" +
                "AHoyjFTYty22IOo44FIe6YQWcDIThU070ivBOlejUutSAAABgi9IQKYAAAQDAEcw\n" +
                "RQIhAP4QShzKQDsjEzXoYIwW1wTK+mutrDM+VoSN15Yy8yqHAiAyLmtzOZeMKRdq\n" +
                "mSDtrZo8ax+WyA6YcAFJByWitcYjpwB2AOg+0No+9QY1MudXKLyJa8kD08vREWvs\n" +
                "62nhd31tBr1uAAABgi9IQAsAAAQDAEcwRQIgFbpWlOHwtDVeC+LBefCNQZw1SKRR\n" +
                "yv8dCt3R1gl5ZRECIQCRyUkY54iQHe5aH2Q3MP1/0VrsCGUHrA8wtqgeV3Z/yTAN\n" +
                "BgkqhkiG9w0BAQwFAAOCAYEAg1Z+/Yz2Cuqb+FE84j3xBxxARhB+ZMbczt8Hi5v4\n" +
                "9jnUuJ85lLcjBTrIn+FxUdfZEd73CxVAveQzL+ppEihTvmO5NoU5ugD/lnhANYEo\n" +
                "JFMapLuRNYfN+/t0Ac60b5CE7Mt7UHfXUMjJWgqJcXx6oUVr27n3U3lgXolMG0d4\n" +
                "tI0kHu9bvLTFyupcpPjYAu2fBQt9qfu6u9bpfvAOsnii4dOCO0i00o8HDsb9e73n\n" +
                "s11ma/8LGwmweO5zqB+Oye/Exiyz6ormgrf3BS+HcLCqPpOp4GKO18Oy5B6V5oWZ\n" +
                "X20eQPYAvu/hnXBI7/+nHJDHDDcXN0H4aIYvmS1fQUgIH5kmaLJqjeJiT6Ql87uL\n" +
                "oo9/mUUYexGcyFMjlrCAF3NKNtMjzBrjo3kWQ4AjrVMDT7sgN9Ng9glEoqKgZZGi\n" +
                "bkvXQUt+LUhXDAvdatyFSB3UIySe9zOCgSNnudOVLtoI97gH6AUcnFHdTZwX5BoM\n" +
                "tOzu97DusRN/uVReRHL27eNk\n" +
                "-----END CERTIFICATE-----\n" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIIFBzCCA++gAwIBAgIRALIM7VUuMaC/NDp1KHQ76aswDQYJKoZIhvcNAQELBQAw\n" +
                "ezELMAkGA1UEBhMCR0IxGzAZBgNVBAgMEkdyZWF0ZXIgTWFuY2hlc3RlcjEQMA4G\n" +
                "A1UEBwwHU2FsZm9yZDEaMBgGA1UECgwRQ29tb2RvIENBIExpbWl0ZWQxITAfBgNV\n" +
                "BAMMGEFBQSBDZXJ0aWZpY2F0ZSBTZXJ2aWNlczAeFw0yMjAxMTAwMDAwMDBaFw0y\n" +
                "ODEyMzEyMzU5NTlaMFkxCzAJBgNVBAYTAkNOMSUwIwYDVQQKExxUcnVzdEFzaWEg\n" +
                "VGVjaG5vbG9naWVzLCBJbmMuMSMwIQYDVQQDExpUcnVzdEFzaWEgUlNBIERWIFRM\n" +
                "UyBDQSBHMjCCAaIwDQYJKoZIhvcNAQEBBQADggGPADCCAYoCggGBAKjGDe0GSaBs\n" +
                "Yl/VhMaTM6GhfR1TAt4mrhN8zfAMwEfLZth+N2ie5ULbW8YvSGzhqkDhGgSBlafm\n" +
                "qq05oeESrIJQyz24j7icGeGyIZ/jIChOOvjt4M8EVi3O0Se7E6RAgVYcX+QWVp5c\n" +
                "Sy+l7XrrtL/pDDL9Bngnq/DVfjCzm5ZYUb1PpyvYTP7trsV+yYOCNmmwQvB4yVjf\n" +
                "IIpHC1OcsPBntMUGeH1Eja4D+qJYhGOxX9kpa+2wTCW06L8T6OhkpJWYn5JYiht5\n" +
                "8exjAR7b8Zi3DeG9oZO5o6Qvhl3f8uGU8lK1j9jCUN/18mI/5vZJ76i+hsgdlfZB\n" +
                "Rh5lmAQjD80M9TY+oD4MYUqB5XrigPfFAUwXFGehhlwCVw7y6+5kpbq/NpvM5Ba8\n" +
                "SeQYUUuMA8RXpTtGlrrTPqJryfa55hTuX/ThhX4gcCVkbyujo0CYr+Uuc14IOyNY\n" +
                "1fD0/qORbllbgV41wiy/2ZUWZQUodqHWkjT1CwIMbQOY5jmrSYGBwwIDAQABo4IB\n" +
                "JjCCASIwHwYDVR0jBBgwFoAUoBEKIz6W8Qfs4q8p74Klf9AwpLQwHQYDVR0OBBYE\n" +
                "FF86fBEQfgxncWHci6O1AANn9VccMA4GA1UdDwEB/wQEAwIBhjASBgNVHRMBAf8E\n" +
                "CDAGAQH/AgEAMB0GA1UdJQQWMBQGCCsGAQUFBwMBBggrBgEFBQcDAjAiBgNVHSAE\n" +
                "GzAZMA0GCysGAQQBsjEBAgIxMAgGBmeBDAECATBDBgNVHR8EPDA6MDigNqA0hjJo\n" +
                "dHRwOi8vY3JsLmNvbW9kb2NhLmNvbS9BQUFDZXJ0aWZpY2F0ZVNlcnZpY2VzLmNy\n" +
                "bDA0BggrBgEFBQcBAQQoMCYwJAYIKwYBBQUHMAGGGGh0dHA6Ly9vY3NwLmNvbW9k\n" +
                "b2NhLmNvbTANBgkqhkiG9w0BAQsFAAOCAQEAHMUom5cxIje2IiFU7mOCsBr2F6CY\n" +
                "eU5cyfQ/Aep9kAXYUDuWsaT85721JxeXFYkf4D/cgNd9+hxT8ZeDOJrn+ysqR7NO\n" +
                "2K9AdqTdIY2uZPKmvgHOkvH2gQD6jc05eSPOwdY/10IPvmpgUKaGOa/tyygL8Og4\n" +
                "3tYyoHipMMnS4OiYKakDJny0XVuchIP7ZMKiP07Q3FIuSS4omzR77kmc75/6Q9dP\n" +
                "v4wa90UCOn1j6r7WhMmX3eT3Gsdj3WMe9bYD0AFuqa6MDyjIeXq08mVGraXiw73s\n" +
                "Zale8OMckn/BU3O/3aFNLHLfET2H2hT6Wb3nwxjpLIfXmSVcVd8A58XH0g==\n" +
                "-----END CERTIFICATE-----\n");
    }

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
     * @param 服务器地址   此处用于指定服务器的地址，一般情况下您无需关心。<br>
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

            发送数据("检查更新.php", json, new 收到数据() {
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
                    json = json.getJSONObject("数据");
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
    public void 使用卡密(账户数据类 账户, String 卡号, @NotNull 使用卡密回调 回调) {
        try {
            JSONObject json = new JSONObject();
            json.put("xm_id", 项目ID);
            json.put("key", 卡号);
            json.put("UID", ANDROID_ID);
            json.put("power", 账户.get授权码());
            json.put("ID", 账户.getID());

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
            json.put("UIDName", Android_Name);

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
                        回调.登录成功(new 账户数据类(json.getJSONObject("数据")));
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
            json.put("UIDName", Android_Name);

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
     * 使用卡密直接登录，要调用此API需要在圣使后端云客户端内启用项目的单码登录设置。<br>
     * 请注意，切换单码登录设置的值，将会导致您项目中的卡密数据定义模糊，污染卡密。<br>
     * 如果您必须切换登录模式，系统会代您删除所有脏污数据，无法撤销！<br>
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
            json.put("UIDName", Android_Name);
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
                        json = json.getJSONObject("数据");
                        回调.登录成功(json.getString("message"), json.getLong("到期时间") * 1000);
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
     * 解除一张卡密绑定的设备，要调用此API需要在圣使后端云客户端内启用项目的单码登录设置且启用设备绑定功能。<br>
     * 请注意，切换单码登录设置的值，将会导致您项目中的卡密数据定义模糊，污染卡密。<br>
     * 如果您必须切换登录模式，系统会代您删除所有脏污数据，无法撤销！<br>
     *
     * @param 卡号 卡号
     * @param 回调 回调
     */
    @Override
    public void 解绑卡密(String 卡号, @NotNull 解除绑定回调 回调) {
        try {
            JSONObject json = new JSONObject();
            json.put("xm_id", 项目ID);
            json.put("UID", ANDROID_ID);
            json.put("UIDName", Android_Name);
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

    /**
     * 如果您的项目启用了绑定设备功能<br>
     * 那么您将会需要这个API<br>
     * 此API可以解除账号和设备的绑定<br>
     * 请注意！<br>
     * 解除绑定不会验证请求的设备是否是绑定的设备<br>
     * 既只要拥有账号和密码，且账号解绑间隔时间内没有解除过绑定，就可以请求解除绑定！<br>
     *
     * @param user 用户账号
     * @param pass 账号密码
     * @param 回调   回调方法
     */
    @Override
    public void 解绑账号(String user, String pass, @NotNull 解除绑定回调 回调) {
        try {
            JSONObject json = new JSONObject();
            json.put(__XM_ID, 项目ID);
            json.put(__UID, ANDROID_ID);
            json.put(__UIDName, Android_Name);
            json.put(__USER, user);
            json.put(__PASS, pass);
            发送数据("解除绑定", json, new 收到数据() {
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
        try {
            new okhttp(trustSSL, isSSLPinning ? 证书链 : new okhttp.证书固定数据()).取异步对象()
                    .post(服务器地址 + api, 生成数据(json), new okhttp.异步请求.响应() {
                        @Override
                        public void 请求出错(@NotNull Call call, @NotNull IOException e) {
                            if (Objects.requireNonNull(e.getMessage()).contains("Certificate pinning failure!")) {
                                回调.错误("不被信任的证书！");
                            } else {
                                回调.错误("网络异常");
                            }
                            e.printStackTrace();
                        }

                        @Override
                        public void 请求完成(@NotNull Call call, @NotNull okhttp.回复 回复) throws IOException {
                            new Thread(() -> {
                                String temp;
                                try {
                                    temp = 回复.getResponse().body().string();
                                } catch (IOException e) {
                                    mainHandler.post(() -> {
                                        回调.错误("IO错误");
                                    });
                                    return;
                                }
                                mainHandler.post(() -> {
                                    try {
                                        回调.收到响应(回复.getResponse(), temp);
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
        } catch (IOException | GeneralSecurityException e) {
            回调.错误("网络异常");
            e.printStackTrace();
        }
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
                if (!加解密操作.MD5加密(json.getJSONObject("data") + 摘要盐值).equals(json.getString("MD5")))
                    throw new 解包出错("数据包签名损坏！可能已经被篡改");
                if (isSign) {
                    if (!json.has("sign"))
                        throw new 解包出错("数据包签名不完整！可能已经被篡改");
                    //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    RSA rsa = new RSA(new RSA.RSAKey(serverPublicKey, ""));
                    String data = new String(
                            rsa.公钥解密(Base64.getDecoder().decode(json.getString("sign").getBytes())));
                    if (!data.equals(json.getString("MD5")))
                        throw new 解包出错("数据包签名损坏！可能已经被篡改");
                    //}
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
    public void setSSLPinning(boolean pinning) {
        this.isSSLPinning = pinning;
    }

    @Override
    public okhttp.自签名证书集 getTrustSSL() {
        return trustSSL;
    }

    /**
     * SDK内部已经设置好了服务器的公钥证书，如果服务器更换了公钥证书而没有及时提供新的SDK
     * 那么您可以通过此方法来设置新的公钥证书。
     * 证书链所有对象共享。
     *
     * @param 证书链
     */
    @Override
    public void set证书链(okhttp.证书固定数据 证书链) {
        后端云对接类.证书链 = 证书链;
    }


    /**
     * 执行判断会员等影响计费的敏感操作时，不要使用客户端本地时间。<br>
     * 因为简单的修改系统时间就能绕过。<br>
     * 您可以利用此方法来获取服务器时间。<br>
     * 不过，您需要注意，调用此方法并不会真的请求服务器。<br>
     * 而是将上一次请求服务器时服务器响应的时间戳返回给您。<br>
     * 换句话说，此方法获取到的时间是客户端上次请求服务器时的时间。<br>
     * 如果客户端在本次运行期间没有请求过服务器的任何API，那么此方法的返回值将会是0！<br>
     *
     * @return 上次请求服务器时服务器返回的时间戳
     */
    @Override
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

    @Override
    public void setHttpUrl(String url) {
        this.服务器地址 = url;
    }

    /**
     * 修改账户的密码
     *
     * @param user_data 登录API返回的账户数据对象
     * @param 原始密码      账号的原始密码
     * @param 修改后的密码    修改后的密码
     */
    @Override
    public void 修改密码(账户数据类 user_data, String 原始密码, String 修改后的密码, @NotNull 修改密码回调 回调) {
        try {
            JSONObject json = new JSONObject();
            json.put(__XM_ID, 项目ID);
            json.put(__USER_ID, user_data.getID());
            json.put(__PASS, 原始密码);
            json.put(__UPDATE_PASS, 修改后的密码);
            json.put(__POWER, user_data.get授权码());
            json.put(__UID, ANDROID_ID);
            发送数据("修改密码", json, new 收到数据() {
                @Override
                public void 错误(String 错误详情) {
                    回调.修改失败(错误详情);
                }

                @Override
                public void 收到响应(Response request, String temp) throws JSONException, 解包出错, IOException {
                    if (request.code() != 200)
                        throw new 解包出错("网络异常");
                    JSONObject json = 解包响应数据(temp);
                    if (json.getBoolean("状态")) {
                        回调.修改成功(json.getString("信息"));
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
