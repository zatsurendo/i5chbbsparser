package com.ranc.i5bbsparser.common;

import java.util.HashMap;
import java.util.Map;

public enum BbsType {
    BBS5CH(1, "5chan.jp"),
    SYAKOUBA(2, "shakouba"),
    ;

    private final int id;
    private final String type;

    private static final Map<Integer, BbsType> idToEnum = new HashMap<>();
    static {
        for (BbsType t : values()) {
            idToEnum.put(t.id(), t);
        }
    }

    private static final Map<String, BbsType> typeToEnum = new HashMap<>();
    static {
        for (BbsType t : values()) {
            typeToEnum.put(t.type(), t);
        }
    }

    private BbsType(final int id, final String type) {
        this.id = id;
        this.type = type;
    }

    public String type() {
        return this.type;
    }

    public int id() {
        return this.id;
    }

    public static BbsType valueOf(int id) {
        return idToEnum.get(id);
    }

    public static BbsType typeOf(String type) {
        return typeToEnum.get(type);
    }
}
