package com.marioparrilla.snake.Utils;

import org.slf4j.LoggerFactory;

public class LogUtils {

    /**
     * Trace via info channel
     * @param msg The message to trace
     * @param clazz The class from where is going to trace
     * @param canShowTrace The boolean that allow to trace
     */
    public static void info(String msg, Class<?> clazz, boolean canShowTrace) {
        if(canShowTrace)
            LoggerFactory.getLogger(clazz).info(msg);
    }

}
