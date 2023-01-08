package com.marioparrilla.snake.context;

import java.util.Map;
import java.util.Properties;

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
    void readConfigurationFile();

    /**
     * This method recover the configuration took from the Snake.config.json file and load it
     */
    void loadConfigurationFile();

    /**
     * This method read the properties file and load it.
     */
    void readPropertiesFile();

    void loadPropertiesFile() throws Exception;

    /**
     * @return The configuration took from the Snake.config.json file
     */
    Map<String, Object> getContextConfig();

    /**
     * @return The properties took from the Snake.properties file
     */
    Properties getContextProps();
}
