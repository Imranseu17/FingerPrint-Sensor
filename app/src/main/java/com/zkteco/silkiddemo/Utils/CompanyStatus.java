package com.zkteco.silkiddemo.Utils;

public enum CompanyStatus {
    Company_STATUS_SUCCESS(1,701),
    Company_STATUS_ERROR(4,801),
    ERROR_CODE_100(8,100),
    ERROR_CODE_406(9,406),
    SERVER_ERROR(15,999);

    private int key;
    private int code;

    CompanyStatus(int key, int code) {
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

    public static CompanyStatus getByCode(int code){
        for(CompanyStatus rs : CompanyStatus.values()){
            if(rs.code==code)return rs;
        }

        return null;


    }
}
