package com.akakour.wechat;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PushPlusTemplete {
    HTML("html", 0),
    JSON("json", 1),
    CLOUDMINOTOR("cloudMonitor", 2);
    private String name;
    private int order;
}
