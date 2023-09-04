package com.kl.common.dto.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class GitVersion implements Serializable {
    private String commitId;
    private String branch;
    private String buildTime;
    private String commitTime;
    private ServerStatus serverStatus;
}
