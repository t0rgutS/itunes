package com.vakhnenko.utils;

public enum BasicRoles {
    ADMIN((short) 1), USER((short) 2);

    private short value;

    BasicRoles(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }
}
