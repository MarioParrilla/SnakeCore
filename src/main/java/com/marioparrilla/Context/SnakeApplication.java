package com.marioparrilla.Context;

import com.marioparrilla.Annotations.Egg;
import com.marioparrilla.Annotations.OpenEgg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class SnakeApplication implements ApplicationContext {

    private final Logger logger = LoggerFactory.getLogger(SnakeApplication.class);

    private Class<?> mainClass;

    private Class<?>[] cest;

    private Class<?>[] classesToScan;

    private HashMap<String, Object> eggs;

    private SnakeApplication() {}

    private SnakeApplication(Class<?> mainClass, String... args) {
        this.eggs = new HashMap<>();
        this.mainClass = mainClass;
    }

    public static ApplicationContext init(Class<?> mainClass, String... args) {
        return new SnakeApplication(mainClass, args);
    }

    @Override
    public ApplicationContext run() throws Exception {
        createEggOfMainClass();
        createEggs();
        openEggs();
        return this;
    }

    private void createEggOfMainClass() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        eggs.put(mainClass.getSimpleName(), mainClass.getDeclaredConstructor().newInstance());
    }

    private void createEggs() throws Exception {
        if (cest ==  null || cest.length < 1) {
            throw new Exception("The Cest need to be registered");
        }
        logger.info("Creating the eggs");
        for (Class<?> clazz : cest) {
            logger.info("Class Cest: "+clazz.getName());
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(Egg.class)) {
                    Class<?> type = method.getReturnType();
                    eggs.put(type.getSimpleName(), type.getDeclaredConstructor().newInstance());
                    logger.info("The egg "+type.getSimpleName()+" was created");
                }
            }
        }
    }

    private void openEggs() throws IllegalAccessException {
        logger.info("Opening the eggs");
        for (Class<?> clazz : classesToScan) {
            logger.info("Opening eggs of "+clazz.getName());

            for (Field field : clazz.getDeclaredFields()) {
                logger.info("Egg "+field.getType().getSimpleName());

                if (field.isAnnotationPresent(OpenEgg.class)) {
                    Object selfInstance = eggs.get(clazz.getSimpleName());
                    Object injection = eggs.get(field.getType().getSimpleName());

                    boolean isAccessible = field.isAccessible();
                    if (!isAccessible)
                        field.setAccessible(true);
                    field.set(selfInstance, injection);
                    if (!isAccessible)
                        field.setAccessible(false);
                    logger.info("The Egg "+field.getName()+" was instanced");
                }
            }
        }
    }

    @Override
    public ApplicationContext registerCestEggsClass(Class<?>[] cest) {
        this.cest = cest;
        return this;
    }

    @Override
    public ApplicationContext classesToScan(Class<?>[] classes){
        this.classesToScan = classes;
        return this;
    }

    @Override
    public Object getEgg(String eggName) {
        return eggs.get(eggName);
    }

    @Override
    public <T> T getEgg(Class<T> clazz) throws Exception {
        for (Object classes : eggs.values()) {
            if(classes.getClass() ==  clazz)
                return (T) classes;
        }
        throw new Exception("No Egg found");
    }

    @Override
    public <T> T getEgg(String eggName, Class<T> clazz) throws Exception {
        var obj = eggs.get(eggName);
        if(obj.getClass() ==  clazz)
            return (T) obj;
        throw new Exception("No Egg found");
    }

    @Override
    public boolean containsEgg(String eggName) {
        return eggs.containsKey(eggName);
    }

    @Override
    public boolean containsEgg(Class<?> clazz) {
        for (Object classes : eggs.values()) {
            if(classes.getClass() ==  clazz)
                return true;
        }
        return false;
    }

    @Override
    public boolean containsEgg(String eggName, Class<?> clazz) {
        var obj = eggs.get(eggName);
        return obj.getClass() ==  clazz;
    }
}
