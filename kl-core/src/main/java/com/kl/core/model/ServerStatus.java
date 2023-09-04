package com.kl.core.model;


import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

@Data
public class ServerStatus extends HashMap<String, String> implements Serializable {
    public static final String OK = "OK";
    public static final String ERROR = "ERROR";
    public static final String TIMEOUT = "TIMEOUT";

}
