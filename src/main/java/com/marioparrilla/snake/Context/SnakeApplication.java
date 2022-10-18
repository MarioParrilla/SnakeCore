package com.marioparrilla.snake.Context;

import com.marioparrilla.snake.Annotations.Egg;
import com.marioparrilla.snake.Annotations.OpenEgg;
import com.marioparrilla.snake.Utils.LogUtils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class SnakeApplication implements ApplicationContext {

    private Class<?> mainClass;

    private Class<?>[] cest;

    private Class<?>[] classesToScan;

    private HashMap<String, Object> eggs;

    private boolean showTrace = false;

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
        LogUtils.info("Creating the egg of the main class "+mainClass.getSimpleName(), SnakeApplication.class, true);
        eggs.put(mainClass.getSimpleName(), mainClass.getDeclaredConstructor().newInstance());
    }

    private void registerEggFromCests() throws Exception {
        if (cest ==  null || cest.length < 1) {
            throw new Exception("The Cest need to have eggs registered");
        }

        LogUtils.info("Creating the eggs", SnakeApplication.class, true);

        for (Class<?> clazz : cest) {
            LogUtils.info("Class Cest: "+clazz.getName(), SnakeApplication.class, showTrace);
            Object classInstance = clazz.getDeclaredConstructor().newInstance();
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(Egg.class)) {
                    Class<?> type = method.getReturnType();
                    String name = method.getAnnotation(Egg.class).name().isEmpty()
                            ? type.getSimpleName()
                            : method.getAnnotation(Egg.class).name();
                    eggs.put(name, method.invoke(classInstance));
                    LogUtils.info("The egg "+name+" with the type "+type.getSimpleName()+" was created", SnakeApplication.class, showTrace);
                }
            }
        }
        LogUtils.info("All eggs were created", SnakeApplication.class, true);
    }

    private void openEggs() throws Exception {
        if (eggs.size() < 1)
            throw new Exception("No exists egg");
        if (classesToScan ==  null || classesToScan.length < 1)
            throw new Exception("No exists classes to be scanned");

        LogUtils.info("Opening the eggs", SnakeApplication.class, true);

        for (Class<?> clazz : classesToScan) {
            LogUtils.info("Opening eggs of "+clazz.getName(), SnakeApplication.class, showTrace);

            for (Field field : clazz.getDeclaredFields()) {
                LogUtils.info("Egg "+field.getType().getSimpleName(), SnakeApplication.class, showTrace);

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
                    LogUtils.info("The Egg "+field.getName()+" was instanced with the name "+name, SnakeApplication.class, showTrace);
                }
            }
        }
        LogUtils.info("All eggs were opened", SnakeApplication.class, true);

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
    public ApplicationContext enableTrace() {
        showTrace = true;
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
