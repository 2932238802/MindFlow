package com.example.app.util;

import java.util.UUID;

// 获取 唯一表示符
public class GetId {
    public static String getUuid() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        String uuidstr = str.replace("-", "");
        return uuidstr;
    }
}
