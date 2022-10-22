package com.marioparrilla.snake.context;

import com.marioparrilla.snake.annotations.*;
import com.marioparrilla.snake.utils.LogUtils;
import com.marioparrilla.snake.utils.PackageUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SnakeApplication implements ApplicationContext {

    private static final String ANNOTATIONS_PACKAGE = "com.marioparrilla.snake.annotations";
    private Class<?> mainClass;

    private ClassLoader classLoader;

    private List<Class<?>> cest;

    private List<Class<?>> classesToScan;

    private HashMap<String, Object> eggs;

    private boolean showTrace = false;

    private boolean isAutoConfigApp;

    private SnakeApplication() {}

    private SnakeApplication(Class<?> mainClass, String... args) throws Exception {
        this.eggs = new HashMap<>();
        this.mainClass = mainClass;
        this.cest = new ArrayList<>();
        this.classesToScan = new ArrayList<>();
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.isAutoConfigApp = isAutoConfigurableApplication();
        if (this.isAutoConfigApp) {
            for (Package packge : classLoader.getDefinedPackages()) {
                var packageName = packge.getName();
                if (packageName.contains(mainClass.getPackageName()))
                    if (!packageName.contains(ANNOTATIONS_PACKAGE))
                        this.classesToScan.addAll(PackageUtils.getClassesFromPackage(packageName));

            }
            this.classesToScan.add(mainClass);
            autoRegisterCest();
        }
    }

    public static ApplicationContext init(Class<?> mainClass, String... args) throws Exception {
        return new SnakeApplication(mainClass, args);
    }

    @Override
    public ApplicationContext run() throws Exception {
        registerEggFromCests();
        if(isAutoConfigApp)
            autoRegisterEggs();
        openEggs();
        return this;
    }

    private boolean isAutoConfigurableApplication() throws Exception {
        return mainClass.isAnnotationPresent(AutoConfig.class);
    }

    private void autoRegisterCest() throws Exception{
        LogUtils.info("Auto registering cest eggs", SnakeApplication.class, true);
            for (Class<?> clazz : classesToScan) {
                if (!clazz.isInterface())
                    registerCest(clazz);
            }
    }

    private void registerEggFromCests() throws Exception {
        if ((cest == null || cest.size() < 1) && !isAutoConfigApp) {
            throw new Exception("Need to register some cest");
        }
        else if(cest != null && cest.size() > 0) {
            LogUtils.info("Creating the eggs", SnakeApplication.class, true);
            for (Class<?> clazz : cest)
                registerEggs(clazz);
            LogUtils.info("All eggs were created", SnakeApplication.class, true);
        }
    }

    private void registerCest(Class<?> clazz) throws Exception {
        if (clazz.isAnnotationPresent(Cest.class)) {
            this.cest.add(clazz);
            LogUtils.info("The Cest from the class "+clazz.getSimpleName()+" was registered", SnakeApplication.class, showTrace);
        }
    }
    private void autoRegisterEggs() throws Exception {
        LogUtils.info("Auto registering eggs", SnakeApplication.class, true);
        for (Class<?> clazz : classesToScan) {
            if (!clazz.isInterface()) {
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(OpenEgg.class)) {
                        String name = field.getAnnotation(OpenEgg.class).name().isEmpty()
                                ? field.getType().getSimpleName()
                                : field.getAnnotation(OpenEgg.class).name();
                        var cacheInstance = getEgg(name, field.getType());
                        if (cacheInstance == null) {
                            eggs.put(name, getClassInstance(field.getType()));
                            LogUtils.info("The autoegg "+name+" with the type "+field.getType().getSimpleName()+" was created", SnakeApplication.class, showTrace);
                        }
                    }

                }
            }

        }
    }

    private void registerEggs(Class<?> clazz) throws Exception {
        Object classInstance = getClassInstance(clazz);
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

    private Object getClassInstance(Class<?> clazz) throws Exception {
        LogUtils.info("Creating class instance of "+clazz.getSimpleName(), SnakeApplication.class, showTrace);
        if (!clazz.isAnnotationPresent(CustomConstructor.class))
            return clazz.getDeclaredConstructor().newInstance();

        Parameter[] params = clazz.getDeclaredConstructors()[0].getParameters();
        List<Object> paramsInstances = new ArrayList<>(params.length);
        for (Parameter param : params) {
            Annotation[] annotations = param.getAnnotations();
            if (annotations.length > 0) {
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType() == CustomParam.class)
                        paramsInstances.add(getEgg(((CustomParam) annotation).eggName(), param.getType()));
                }
            }
            else {
                paramsInstances.add(getEgg(param.getType()));
            }
        }
        return clazz.getDeclaredConstructors()[0].newInstance(paramsInstances.toArray());
    }

    private void openEggs() throws Exception {
        if ((classesToScan == null || classesToScan.size() < 1))
            throw new Exception("No exists classes to be scanned");

        for (Class<?> clazz : classesToScan) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(OpenEgg.class)) {
                    LogUtils.info("Opening eggs of "+clazz.getName(), SnakeApplication.class, showTrace);
                    String name = field.getAnnotation(OpenEgg.class).name().isEmpty()
                            ? field.getType().getSimpleName()
                            : field.getAnnotation(OpenEgg.class).name();
                    Object selfInstance = eggs.get(clazz.getSimpleName());
                    Object injection = getEgg(name);

                    if (injection == null) {
                        injection = getEgg(field.getType());
                    }

                    if (injection == null) {
                        if (cest.isEmpty())
                            throw new Exception("No egg found with the class "+field.getType());
                        injection = getClassInstance(field.getType());
                        eggs.put(name, injection);
                        LogUtils.info("The egg "+name+" with the type "+field.getType().getSimpleName()+" was created and opened", SnakeApplication.class, showTrace);
                    }

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
        for (Class<?> clazz : cest) {
            if (this.cest.contains(clazz)) {
                LogUtils.warn("The class "+clazz.getSimpleName()+" is already registered like cest. Not added again", SnakeApplication.class, true);
                continue;
            }
            this.cest.add(clazz);
        }
        return this;
    }

    @Override
    public ApplicationContext classesToScan(Class<?>[] classes){
        for (Class<?> clazz : classes) {
            if (this.classesToScan.contains(clazz)) {
                LogUtils.warn("The class "+clazz.getSimpleName()+" is already registered. Not added again", SnakeApplication.class, true);
                continue;
            }
            this.classesToScan.add(clazz);
        }
        return this;
    }

    @Override
    public ApplicationContext enableTrace() {
        showTrace = true;
        return this;
    }

    @Override
    public Object[] getAllEggs() {
        return eggs.values().toArray();
    }

    @Override
    public Object getEgg(String eggName) {
        return eggs.get(eggName);
    }

    @Override
    public <T> T getEgg(Class<T> clazz){
        for (Object obj : eggs.values()) {
            if(obj.getClass() == clazz)
                return (T) obj;
        }
        return null;
    }

    @Override
    public <T> T getEgg(String eggName, Class<T> clazz){
        var obj = eggs.get(eggName);
        if(obj != null && obj.getClass() == clazz)
            return (T) obj;
        return null;
    }

    @Override
    public boolean containsEgg(String eggName) {
        return eggs.containsKey(eggName);
    }

    @Override
    public boolean containsEgg(Class<?> clazz) {
        for (Object classes : eggs.values()) {
            if(classes.getClass() == clazz)
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
