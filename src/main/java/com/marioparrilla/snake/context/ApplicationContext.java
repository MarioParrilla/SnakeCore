package com.marioparrilla.snake.context;

public interface ApplicationContext {

    /**
     * Create the context calling submethods to get the eggs of the classes and inject the eggs.
     * @return ApplicationContext current context
     * @throws Exception
     */
    ApplicationContext run() throws Exception;

    /**
     * @param cests List of the classes that has the eggs to inject
     * @return ApplicationContext current context
     */
    ApplicationContext cestsToScan(Class<?>[] cests);

    /**
     * @param classes List of the classes to scan and inject the eggs
     * @return ApplicationContext current context
     */
    ApplicationContext classesToScan(Class<?>[] classes);

    /**
     * This method enable the logging via output in console
     * @return ApplicationContext current context
     */
    ApplicationContext enableVerboseLogTrace();

    /**
     * This method register the cest class.
     * @param clazz The cest class to be registered.
     */
    void registerCest(Class<?> clazz);

    /**
     * This method goes through the registered cest and creating her variables with the @Egg annotation.
     * @throws Exception
     */
    void registerCestEggs() throws Exception;

    /**
     * This method goes through the class (usually a cest) and it's looking for the @Egg annotation to register it.
     * @param clazz This class has the eggs to be registered, usually the cests.
     * @throws Exception
     */
    void registerEggs(Class<?> clazz) throws Exception;

    /**
     * This method goes through the classes looking for the @OpenEgg annotation to give to the variable the instance required.
     * @throws Exception
     */
    void openEggs() throws Exception;

    /**
     * @return All eggs registered
     */
    Object[] getAllEggs();

    /**
     * @param eggName The name of the egg
     * @return The egg that was saved with this name
     */
    Object getEgg(String eggName);

    /**
     * @param clazz The class of the egg
     * @return The egg that was saved with this class
     */
    <T> T getEgg(Class<T> clazz);


    /**
     * @param eggName The name of the egg
     * @param clazz The class of the egg
     * @return The egg that was saved with this class and name
     */
    <T> T getEgg(String eggName, Class<T> clazz);

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

    /**
     * This method prints the name/logo of the library
     */
    void printBanner();
}
