package com.marioparrilla.snake.utils;

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

    /**
     * Trace via warn channel
     * @param msg The message to trace
     * @param clazz The class from where is going to trace
     * @param canShowTrace The boolean that allow to trace
     */
    public static void warn(String msg, Class<?> clazz, boolean canShowTrace) {
        if(canShowTrace)
            LoggerFactory.getLogger(clazz).warn(msg);
    }

    /**
     * Trace via error channel
     * @param msg The message to trace
     * @param clazz The class from where is going to trace
     * @param canShowTrace The boolean that allow to trace
     */
    public static void error(String msg, Class<?> clazz, boolean canShowTrace) {
        if(canShowTrace)
            LoggerFactory.getLogger(clazz).error(msg);
    }

    /**
     * Trace via debug channel
     * @param msg The message to trace
     * @param clazz The class from where is going to trace
     * @param canShowTrace The boolean that allow to trace
     */
    public static void debug(String msg, Class<?> clazz, boolean canShowTrace) {
        if(canShowTrace)
            LoggerFactory.getLogger(clazz).debug(msg);
    }

    /**
     * Trace via trace channel
     * @param msg The message to trace
     * @param clazz The class from where is going to trace
     * @param canShowTrace The boolean that allow to trace
     */
    public static void trace(String msg, Class<?> clazz, boolean canShowTrace) {
        if(canShowTrace)
            LoggerFactory.getLogger(clazz).trace(msg);
    }
}
