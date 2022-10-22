package com.marioparrilla.snake.context;

public interface ApplicationContext {

    /**
     * @param cest List of the classes that has the eggs to inject
     * @return ApplicationContext current context
     */
    ApplicationContext registerCestEggsClass(Class<?>[] cest);

    /**
     * @param classes List of the classes to scan and inject the eggs
     * @return ApplicationContext current context
     */
    ApplicationContext classesToScan(Class<?>[] classes);

    /**
     * This method enable the logging via output in console
     * @return ApplicationContext current context
     */
    ApplicationContext enableTrace();

    /**
     * Create the context calling submethods to get the eggs of the classes and inject the eggs.
     * @return ApplicationContext current context
     * @throws Exception
     */
    ApplicationContext run() throws Exception;


    /**
     * @param eggName The name of the egg
     * @return The egg that was saved with this name
     */
    Object getEgg(String eggName);

    /**
     * @param clazz The class of the egg
     * @return The egg that was saved with this class
     */
    <T> T getEgg(Class<T> clazz) throws Exception;


    /**
     * @param eggName The name of the egg
     * @param clazz The class of the egg
     * @return The egg that was saved with this class and name
     */
    <T> T getEgg(String eggName, Class<T> clazz) throws Exception;

    /**
     * @param eggName The name of the egg
     * @return True if it has this egg else false
     */
    boolean containsEgg(String eggName);

    /**
     * @param clazz The class of the egg
     * @return True if it has this egg else false
     */
    boolean containsEgg(Class<?> clazz);

    /**
     * @param eggName The name of the egg
     * @param clazz The class of the egg
     * @return True if it has this egg else false
     */
    boolean containsEgg(String eggName, Class<?> clazz);
}
