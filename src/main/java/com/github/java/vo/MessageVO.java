package com.github.java.vo;

import lombok.Data;

@Data
public class MessageVO {
    private String type;
    private String content;
    private String to;
}
