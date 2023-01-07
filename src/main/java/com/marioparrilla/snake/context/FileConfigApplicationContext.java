package com.marioparrilla.snake.context;

public interface FileConfigApplicationContext extends ApplicationContext {

    @Override
    FileConfigApplicationContext run() throws Exception;

    FileConfigApplicationContext cestsToScan(Class<?>[] cests);

    @Override
    FileConfigApplicationContext classesToScan(Class<?>[] classes);

    @Override
    FileConfigApplicationContext enableVerboseLogTrace();

    /**
     * This method read the configuration file and modify the default configuration
     */
    void readConfigFile();

}
