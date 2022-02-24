package com.kotori316.limiter;

public class Config {
    private static final Config INSTANCE = new Config();

    public static Config getInstance() {
        return INSTANCE;
    }

    private Config() {
    }

    public int getPermission() {
        return 2;
    }
}
