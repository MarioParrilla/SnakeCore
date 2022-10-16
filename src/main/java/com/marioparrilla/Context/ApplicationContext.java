package com.marioparrilla.Context;

public interface ApplicationContext {

    ApplicationContext registerCestEggsClass(Class<?>[] cest);

    ApplicationContext classesToScan(Class<?>[] classes);

    ApplicationContext run() throws Exception;

    Object getEgg(String eggName);

    <T> T getEgg(Class<T> clazz);

    <T> T getEgg(Class<T> clazz, String eggName);

    boolean containsEgg(String eggName);
}
