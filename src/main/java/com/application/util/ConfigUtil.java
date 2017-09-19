package com.application.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by chengchao on 16-10-13.
 */
@Component
@Profile("dev")
public class ConfigUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtil.class);

    private static Map<String, String> config;

    static {
        config = new HashMap<>();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("application-dev");
        Enumeration<String> keys = resourceBundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String value = resourceBundle.getString(key);
            LOGGER.info("读取配置信息 : {} ==> {}", key, value);
            config.put(key, value);
        }

    }

    public static final String getConfig(String key) {

        String value = config.get(key);
        return (value == null) ? "" : value;
    }

    public static final String getConfig(String key, Object params[]) {
        /*例如String str = "I'm not a {0}, age is {1,number,short}", height is {2,number,#.#};  参数格式可以不定义*/
        String value = MessageFormat.format(config.get(key), params);
        return (value == null) ? "" : value;
    }

    public static final void setConfig(String key, String value) {
        config.put(key, value);
    }


    public static final Set<String> getConfigKeySet() {
        return config.keySet();
    }


}
