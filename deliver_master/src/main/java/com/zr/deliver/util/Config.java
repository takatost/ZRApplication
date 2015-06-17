package com.zr.deliver.util;

/**
 * Created by Administrator on 2015/5/6.
 */
public class Config {

    /**
     * 为了版本的向下兼容性，通过minSdkVersion和targetSdkVersion控制最低安卓sdk版本和目标编译版本
     * 目标编译版本一般跟市场占有率最高的安卓版本匹配，最后调试应该从最低到最高的版本上整个调试一遍
     * 通过support保证新的api在低版本的机器上能顺利运行，对于一些support上没支持的比较新的api应该
     * 使用Build判断，避免程序crash，也没有必要为了很低的安卓版本去做兼容而增加工作量，现在最低使用4.0
     * 老旧的谷歌开始标注废弃的api应该劲量减少使用比如gallery等，减少程序出bug的风险，最新的metiral标准
     * 可以学习，由于需求机器安卓版本5.0以上太高，市场低于5.0的版本太高，暂时不应该放进代码里，如果对
     * metiral标准比较熟悉想获得较好界面效果又是单独开发，可以适量使用新api但是必须使用Build判断（务必），
     * 如果与同事合作开发就避免使用以减少风险
     */

    public static String DELIVER_DATA = "deliver_data";

    public static String DELIVER_ID = "deliver_name";

    public static String DELIVER_PASS = "deliver_password";

    public static String DELIVER_STATUS = "deliver_status";
    //保存一个默认配置
    public static int DEFAULT_ID = 10001;

    //进入详情的key

    public static String ORDER_DETAIL_KEY = "order_detail";

    //配送员所有请求链接
//  public static String BASE_URL = "http://192.168.1.113:8080/";
    public static String BASE_URL = "http://192.168.1.88:8080/";

    public static String DELIVER_LOGIN_URL = BASE_URL
            + "NearbyGo/phone/delivery/login";

    public static String DELIVER_SIGNIN_URL = BASE_URL
            + "NearbyGo/phone/delivery/signIn";

    public static String DELIVER_SIGNOUT_URL = BASE_URL
            + "NearbyGo/phone/delivery/signOut";
    public static String DELIVER_ORDER_URL = BASE_URL
            + "NearbyGo/phone/delivery/dymanreceive";

    public static String DELIVER_ORDER_FINISH_URL = BASE_URL
            + "NearbyGo/phone/delivery/dymanfinish";

    //轮询动作
    public static final String POLLING_ACTION = "com.zr.sanhua.AlarmPollingService";
    /**
     * 登陆相关
     */
    // RSA公钥
    public static String pubKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCOhls3uxPVcDOWhACZafybA2/Cu1gqtxSWeE2E"
            + "\n\rXXIM1n3L90WeYi1zDPb31cYyDP5hkMoD5AW1vHWgZTqqmvI5qxPXA0ckJi+SWNf5bFTplt9s3+9s"
            + "\n\rX1zgbETdudcB84JTFzAiZQPEtkw9kfu7Nbz+b0kd2MlW+YlVGQrOKkTMnwIDAQAB";

    // RSA私钥
    public static String priKeyStr = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAI6GWze7E9VwM5aEAJlp/JsDb8K7"
            + "\n\rWCq3FJZ4TYRdcgzWfcv3RZ5iLXMM9vfVxjIM/mGQygPkBbW8daBlOqqa8jmrE9cDRyQmL5JY1/ls"
            + "\n\rVOmW32zf72xfXOBsRN251wHzglMXMCJlA8S2TD2R+7s1vP5vSR3YyVb5iVUZCs4qRMyfAgMBAAEC"
            + "\n\rgYACQnEJxiZ/WMMInNkhlYOStZA9BxlTvAlQhWG9OnoHaBMge7AX3biYvVjg/vugaYJS66e4PhI1"
            + "\n\rGmLHAzPV5pT2fQe3eDl50gkH3TCGdqB6iPuGf1RTtVNDLN/h5csmnFtFtzDrIH3wInmk3+V+A5yR"
            + "\n\rz+fwAV6AGUKdxwgNDX9McQJBAMSMRIWHuWJVdXz1dT5omoMwYv6DdlssUYzJBgP3pnOqhR2Lt6A9"
            + "\n\r+qzsv2RL7/glI5FbeWisnam3GsyRtYBVxVkCQQC5osxoUz2Msnf4H7TtYgs0r2nDKK/hdK9c4eeP"
            + "\n\rLV0ZX4xSL8u+194ji4K6Cf617zghv1e8H1Gj6SH+KbzfEUq3AkEAoXJ6wpinLd/23wsdIIN6EDMJ"
            + "\n\rzficmu2/mv5xQ4cEolQ6ffeLgUQICk16NV+vIU0Yd0kFZHcOFx+CEvHJfj52GQJBAJpPl/dI6qsg"
            + "\n\rV9WsFawWihYbkqEmGz4gzv041FISdYd4A572GDnmG8QUXnDjihYWauSyt+2rYyQL0bQDYftT1nUC"
            + "\n\rQGvcEu/9lHOYkmdlOJD0PeP2MpXDf2I5/D0ilSOTEtYr2C7RCdMcrIMiry/WtxYRCGqcYR3EnXG3"
            + "\n\rDV8jBNvQKoE=";


}
