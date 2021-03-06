package com.zkteco.silkiddemo.Utils;

public enum ErrorCode {

    ERRORCODE500(2, 500),
    ERRORCODE400(3, 400),
    ERRORCODE406(4, 406),
    ERRORCODE412(5, 412),
    ERRORCODE455(5, 455),
    SERVER_ERROR_CODE(7,999);



    private int key;
    private int code;

    ErrorCode(int key, int code) {
        this.key = key;
        this.code = code;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static ErrorCode getByCode(int code) {
        for (ErrorCode rs : ErrorCode.values()) {
            if (rs.getCode()== code) return rs;
        }

        return null;
    }

}
