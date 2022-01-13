package com.zkteco.silkiddemo.Utils;

public enum EmployeeStatus {
    EMPLOYEE_STATUS_SUCCESS(1,701),
    EMPLOYEE_STATUS_ERROR(4,801),
    ERROR_CODE_100(8,100),
    ERROR_CODE_406(9,406),
    SERVER_ERROR(15,999);

    private int key;
    private int code;

    EmployeeStatus(int key, int code) {
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

    public static EmployeeStatus getByCode(int code){
        for(EmployeeStatus rs : EmployeeStatus.values()){
            if(rs.code==code)return rs;
        }

        return null;


    }
}
