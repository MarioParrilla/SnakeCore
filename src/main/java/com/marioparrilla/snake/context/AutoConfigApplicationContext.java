package com.marioparrilla.snake.context;

public interface AutoConfigApplicationContext extends FileConfigApplicationContext {

    @Override
    AutoConfigApplicationContext run() throws Exception;

    AutoConfigApplicationContext cestsToScan(Class<?>[] cests);

    @Override
    AutoConfigApplicationContext classesToScan(Class<?>[] classes);

    @Override
    AutoConfigApplicationContext enableVerboseLogTrace();

    /**
     * This function return you if the application is auto configured.
     * @return boolean Indicates if this application is auto configured.
     */
    boolean isAutoConfigurableApplication();

    /**
     * This method auto scan of your application and if you use on the @AutoConfig annotation the parameters scan and filter, this method will
     * automatically in the case of scan only scan the classes putted in the scan parameter and in the case of filter, will automatically exclude
     * these classes of the scan.
     */
    void autoScanClasses();

    /**
     * This method auto register the classes of the application that has the annotation @Cest like cest of your application.
     * @throws Exception
     */
    void autoRegisterCest() throws Exception;

    /**
     * This method auto scan your classes looking for a variables with the annotation @OpenEgg and then if the instance of the variable is already instanced
     * checking it by the variable class and the name if it has in the annotation @OpenEgg in instanced eggs, it's reused like singleton.
     * @throws Exception
     */
    void autoRegisterEggs() throws Exception;

    /**
     * This function returns if in the @AutoConfig annotation has been used the parameter scan.
     * @return boolean Indicates if in the @AutoConfig annotation has been used the parameter scan.
     */
    boolean isAutoConfigScan();

    /**
     * This function returns if in the @AutoConfig annotation has been used the parameter filter.
     * @return boolean Indicates if on the @AutoConfig annotation has been used the parameter filter.
     */
    boolean isAutoConfigFilter();

    /**
     * This function return if the class passed is in the classes to be scanned.
     * @param className The class name to be checked.
     * @return boolean Indicates if the class passed is in on the classes to be scanned. One important thing is that if this function it's used and
     * the context is not autoconfigured will return false.
     */
    boolean includedInScan(String className);

    /**
     * This function return if the class passed is in the classes to be excludes.
     * @param className The class name to be checked.
     * @return boolean Indicates if the class passed is in on the classes to be excludes. One important thing is that if this function it's used and
     * the context is not autoconfigured will return false.
     */
    boolean includedInFilter(String className);
}
