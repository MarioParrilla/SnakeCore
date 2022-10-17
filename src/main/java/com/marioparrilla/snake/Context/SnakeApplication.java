package com.marioparrilla.snake.Context;

import com.marioparrilla.snake.Annotations.Egg;
import com.marioparrilla.snake.Annotations.OpenEgg;
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
        registerEggFromCests();
        openEggs();
        return this;
    }

    private void createEggOfMainClass() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        eggs.put(mainClass.getSimpleName(), mainClass.getDeclaredConstructor().newInstance());
    }

    private void registerEggFromCests() throws Exception {
        if (cest ==  null || cest.length < 1) {
            throw new Exception("The Cest need to have eggs registered");
        }
        logger.info("Creating the eggs");
        for (Class<?> clazz : cest) {
            logger.info("Class Cest: "+clazz.getName());
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(Egg.class)) {
                    Class<?> type = method.getReturnType();
                    String name = method.getAnnotation(Egg.class).name().isEmpty()
                            ? type.getSimpleName()
                            : method.getAnnotation(Egg.class).name();
                    eggs.put(name, type.getDeclaredConstructor().newInstance());
                    logger.info("The egg "+name+" with the type "+type.getSimpleName()+" was created");
                }
            }
        }
    }

    private void openEggs() throws Exception {
        if (eggs.size() < 1)
            throw new Exception("No exists egg");
        if (classesToScan ==  null || classesToScan.length < 1)
            throw new Exception("No exists classes to be scanned");
        logger.info("Opening the eggs");
        for (Class<?> clazz : classesToScan) {
            logger.info("Opening eggs of "+clazz.getName());

            for (Field field : clazz.getDeclaredFields()) {
                logger.info("Egg "+field.getType().getSimpleName());

                if (field.isAnnotationPresent(OpenEgg.class)) {
                    String name = field.getAnnotation(OpenEgg.class).name().isEmpty()
                            ? field.getType().getSimpleName()
                            : field.getAnnotation(OpenEgg.class).name();
                    Object selfInstance = eggs.get(clazz.getSimpleName());
                    Object injection = eggs.get(name);

                    if (injection ==  null) {
                        for (Object obj : eggs.values()) {
                            if (obj.getClass() ==  field.getType())
                                injection = obj;
                        }
                    }

                    if (injection ==  null)
                        throw new Exception("No egg found with the class "+field.getType());

                    boolean isAccessible = field.isAccessible();
                    if (!isAccessible)
                        field.setAccessible(true);
                    field.set(selfInstance, injection);
                    if (!isAccessible)
                        field.setAccessible(false);
                    logger.info("The Egg "+field.getName()+" was instanced with the name "+name);
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
        throw new Exception("No Egg found with this name or class");
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
        return obj !=  null && obj.getClass() ==  clazz;
    }
}
