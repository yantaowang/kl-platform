package com.kl.monitor.starter.utils;

public enum InterfaceTypeEnum {
    RPC("1"),HTTP("2");
    private String type;
    private InterfaceTypeEnum(String type){
        this.type = type;
    }


    public String getType() {
        return type;
    }
}
