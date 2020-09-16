package com.vunke.videochat.model;

import java.util.List;

/**
 * Created by zhuxi on 2020/9/14.
 */

public class AccountBean {


    /**
     * code : 200
     * optionalFixedLine : ["7318478702101","7318478702102","7318478702103","7318478702104","7318478702105"]
     * message : success
     */

    private int code;
    private String message;
    private List<String> optionalFixedLine;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getOptionalFixedLine() {
        return optionalFixedLine;
    }

    public void setOptionalFixedLine(List<String> optionalFixedLine) {
        this.optionalFixedLine = optionalFixedLine;
    }
}
