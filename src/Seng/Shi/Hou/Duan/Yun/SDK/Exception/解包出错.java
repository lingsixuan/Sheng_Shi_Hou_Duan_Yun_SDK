package Seng.Shi.Hou.Duan.Yun.SDK.Exception;

public class 解包出错 extends Exception {
    public String 异常;
    public 解包出错(String 异常){
        this.异常 = 异常;
    }
}
