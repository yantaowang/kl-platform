package com.kl.starter.kafka.enums;

public enum KafkaCMDTypeEnum {
    INSERT("insert"),
    UPDATE("update"),
    DELETE("delete"),
    INSERT_OR_UPDATE("insertOrUpdate");

    private String type;

    private KafkaCMDTypeEnum(String type) {
        this.type = type;
    }

    public String type() {
        return this.type;
    }
}

