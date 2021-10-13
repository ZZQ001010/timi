package com.github.mybatis.database.model;

import lombok.Data;

/**
 * Pair: 值对
 *
 * @author wudarui
 */
@Data
@SuppressWarnings("unused")
public class Pair {
    private final String key;

    private final String value;

    public Pair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Pair(String kv) {
        int index = kv.indexOf(':');
        if (index > 0) {
            this.key = kv.substring(0, index).trim();
            this.value = kv.substring(index + 1).trim();
        } else {
            this.key = kv.trim();
            this.value = null;
        }
    }
}