package com.vunke.videochat.base;

/**
 * Created by zhuxi on 2020/2/27.
 */

public class BaseConfig {
//   public static final String  BASE_URL = "http://134.175.229.3:8083/VideoPhone/";//基础URL
   public static final String  BASE_URL = "http://124.232.136.236:8082/VideoPhone/";//test
    /**
     *  获取联系人列表
     **/
    public static final String GET_USER_CONTACTS_LIST = "addressInfo.do";
    public static final String LOGIN = "login.do";
    public static final String GET_OPTIONAL_ACCOUNT ="oai/getOptionalAccount.do";;
    public static final String FIXE_LINE_BINDING ="oai/fixedLineBinding.do";;
    public static final  String ipaddr = "10.255.25.48";
    public static final  String port = "5060";
    public static final  String areaCode = "+86";
    public static final   String lastCallNumber = "LAST_CALL_NUMBER";
    public static final   String NINE = "9";
    public static final   String RECEIVE_MAIN_ACTIVITY = "receive_main_activity";
    public static final   String RECEIVE_VIDEO_ACTIVITY = "receive_video_activity";
    public static final  String RECEIVE_UPB_CHANGE = "receive_usb_change";


}
