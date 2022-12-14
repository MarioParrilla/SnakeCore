package com.marioparrilla.snake.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.marioparrilla.snake.annotations.*;
import com.marioparrilla.snake.utils.LogUtils;
import com.marioparrilla.snake.utils.PackageUtils;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SnakeApplication implements AutoConfigApplicationContext {

    private static final String ANNOTATIONS_PACKAGE = "com.marioparrilla.snake.annotations";
    private static final String CURRENT_VERSION = "v0.1.5";

    private static AutoConfigApplicationContext singletonContext;

    private Class<?> mainClass;

    private AutoConfig autoConfigAnnotation;

    private List<Class<?>> cest;

    private List<Class<?>> classesToScan;

    private HashMap<String, Object> eggs;

    private boolean showTrace = false;

    private boolean isAutoConfigApp;

    private String propsFilePath;

    private final Map<String, Object> contextConfigFile = new HashMap<>() {{
        put("props_file_path", "./");
        put("verbose_log_trace", false);
    }};
    private final Properties contextPropsFile = new Properties();

    private SnakeApplication() {}

    private SnakeApplication(Class<?> mainClass, String... args) {
        this.eggs = new HashMap<>();
        this.mainClass = mainClass;
        this.cest = new ArrayList<>();
        this.classesToScan = new ArrayList<>();
        this.isAutoConfigApp = isAutoConfigurableApplication();
    }

    public static AutoConfigApplicationContext init(Class<?> mainClass, String... args) throws Exception {
        if (singletonContext == null)
            singletonContext = new SnakeApplication(mainClass, args);
        return singletonContext;
    }

    @Override
    public AutoConfigApplicationContext run() throws Exception {
        long start = System.currentTimeMillis();
        printBanner();
        readConfigurationFile();
        loadConfigurationFile();
        readPropertiesFile();
        if(isAutoConfigApp) {
            autoConfigAnnotation = mainClass.getAnnotation(AutoConfig.class);
            autoScanClasses();
            autoRegisterCest();
            registerCestEggs();
            autoRegisterEggs();
        }
        else {
            registerCestEggs();
        }
        loadPropertiesFile();
        openEggs();
        long end = System.currentTimeMillis();
        LogUtils.info("Time: "+(end - start)+" ms", SnakeApplication.class, true);
        return this;
    }

    @Override
    public boolean isAutoConfigurableApplication() {
        return mainClass.isAnnotationPresent(AutoConfig.class);
    }

    @Override
    public void autoRegisterCest() {
        LogUtils.info("Auto registering cest eggs", SnakeApplication.class, true);
            for (Class<?> clazz : classesToScan) {
                if (!clazz.isInterface() && !clazz.isEnum())
                    registerCest(clazz);
            }
    }

    @Override
    public void registerCestEggs() throws Exception {
        if ((cest == null || cest.size() < 1) && !isAutoConfigApp) {
            throw new Exception("Needs to register some cest");
        }
        else if(cest != null && cest.size() > 0) {
            LogUtils.info("Creating the cest eggs", SnakeApplication.class, true);
            for (Class<?> clazz : cest)
                registerEggs(clazz);
            LogUtils.info("All cest eggs were created", SnakeApplication.class, true);
        }
    }

    @Override
    public void registerCest(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Cest.class)) {
            this.cest.add(clazz);
            LogUtils.info("The Cest from the class "+clazz.getSimpleName()+" was registered", SnakeApplication.class, showTrace);
        }
    }

    @Override
    public void autoRegisterEggs() throws Exception {
        LogUtils.info("Auto registering eggs", SnakeApplication.class, true);
        for (Class<?> clazz : classesToScan) {
            if (!clazz.isInterface() && !clazz.isEnum()) {
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(OpenEgg.class)) {
                        //Checking if the field has a custom name
                        String name = field.getAnnotation(OpenEgg.class).name().isEmpty()
                                ? field.getType().getSimpleName()
                                : field.getAnnotation(OpenEgg.class).name();
                        //Checking if the egg already exists, if not its created
                        boolean currentEggExists = containsEgg(name, field.getType());
                        if (!currentEggExists) {
                            eggs.put(name, getClassInstance(field.getType()));
                            LogUtils.info("The egg "+ name +" with the type "+ clazz.getSimpleName()+" was created", SnakeApplication.class, showTrace);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void registerEggs(Class<?> clazz) throws Exception {
        Object clazzInstance = getClassInstance(clazz);
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(Egg.class)) {
                Class<?> type = method.getReturnType();
                String name = method.getAnnotation(Egg.class).name().isEmpty()
                        ? type.getSimpleName()
                        : method.getAnnotation(Egg.class).name();
                //Checking if the egg already exists, if not its created
                boolean currentEggExists = containsEgg(name, method.getReturnType());
                if (!currentEggExists) {
                    eggs.put(name, method.invoke(clazzInstance));
                    LogUtils.info("The egg "+ name +" with the type "+ method.getReturnType().getSimpleName()+" was created", SnakeApplication.class, showTrace);
                }
            }
        }
    }

    @Override
    public void openEggs() throws Exception {
        if ((classesToScan == null || classesToScan.size() < 1))
            throw new Exception("No exists classes to be scanned");

        for (Class<?> clazz : classesToScan) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(OpenEgg.class)) {
                    LogUtils.info("Opening eggs of "+clazz.getName(), SnakeApplication.class, showTrace);
                    String name = field.getAnnotation(OpenEgg.class).name().isEmpty()
                            ? field.getType().getSimpleName()
                            : field.getAnnotation(OpenEgg.class).name();
                    Object selfInstance = getEgg(clazz.getSimpleName(), clazz);
                    Object injection = getEgg(name);
                    LogUtils.info("Egg: "+clazz.getName(), SnakeApplication.class, showTrace);
                    LogUtils.info("SelfInstance INFO: "+selfInstance, SnakeApplication.class, showTrace);
                    LogUtils.info("Injection INFO: "+injection, SnakeApplication.class, showTrace);

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

                    setFieldValue(field, selfInstance, injection);
                    LogUtils.info("The Egg "+field.getName()+" was instanced with the name "+name, SnakeApplication.class, showTrace);
                }
            }
        }
        LogUtils.info("All eggs were opened", SnakeApplication.class, true);
    }

    @Override
    public AutoConfigApplicationContext cestsToScan(Class<?>[] cests) {
        for (Class<?> clazz : cests) {
            if (this.cest.contains(clazz)) {
                LogUtils.warn("The class "+clazz.getSimpleName()+" is already registered like cest. Not added again", SnakeApplication.class, true);
                continue;
            }
            this.cest.add(clazz);
        }
        return this;
    }

    @Override
    public void autoScanClasses() {
        for (Package packge : Thread.currentThread().getContextClassLoader().getDefinedPackages()) {
            var packageName = packge.getName();
            if (packageName.contains(mainClass.getPackageName()))
                if (!packageName.contains(ANNOTATIONS_PACKAGE)){
                    LogUtils.info("Auto filtering and scanning the classes", SnakeApplication.class, true);
                    this.classesToScan.addAll(PackageUtils.getClassesFromPackage(packageName).stream()
                            .filter(e -> {
                                if(isAutoConfigFilter() && includedInFilter(e.getSimpleName())) {
                                    LogUtils.info("Auto Filter: The class "+e.getSimpleName()+" was filtered", SnakeApplication.class, showTrace);
                                    return false;
                                }
                                else{
                                    LogUtils.info("Auto Scan: The class "+e.getSimpleName()+" was scanned", SnakeApplication.class, showTrace);
                                    return isAutoConfigScan() && includedInScan(e.getSimpleName());
                                }
                            }).toList());
                }
        }
        this.classesToScan.add(mainClass);
    }

    @Override
    public AutoConfigApplicationContext classesToScan(Class<?>[] classes){
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
    public void readConfigurationFile() {
        FileReader fr = null;
        try {
            File file = new File (".\\Snake.conf.json");
            fr = new FileReader (file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            StringBuilder lines = new StringBuilder();
            LogUtils.info("Reading configuration file", SnakeApplication.class, true);
            while((line=br.readLine())!=null)
                lines.append(line);

            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> config = mapper.readValue(lines.toString(), HashMap.class);
            config.keySet().forEach(key -> contextConfigFile.put(key, config.get(key)));
            LogUtils.info("Configuration file read", SnakeApplication.class, true);
        } catch (JsonProcessingException e) {
            LogUtils.error("Can not parse the configuration file", SnakeApplication.class, true);
            e.printStackTrace();
        } catch (IOException e) {
            LogUtils.error("Not exists configuration file", SnakeApplication.class, true);
        }
        finally{
            try{
                if( null != fr ){
                    fr.close();
                }
            }catch (Exception e2){
                e2.printStackTrace();
            }
        }
    }

    @Override
    public void loadConfigurationFile() {
        LogUtils.info("Loading configuration file", SnakeApplication.class, true);
        Object temp = contextConfigFile.get("props_file_path");
        if (!(temp instanceof String)) {
            propsFilePath = ".\\";
            LogUtils.error("Snake.conf.json is not correct:\n{\n\tprops_file_path = "+temp+"\n}", SnakeApplication.class, true);
            LogUtils.error("Auto setted to:\n{\n\tprops_file_path = \".\\\"\n}", SnakeApplication.class, true);
        }
        else
            propsFilePath = (String) temp;
        temp = contextConfigFile.get("verbose_log_trace");
        if (!(temp instanceof Boolean)) {
            showTrace = false;
            LogUtils.error("Snake.conf.json is not correct:\n{\n\tverbose_log_trace = "+temp+"\n}", SnakeApplication.class, true);
            LogUtils.error("Auto setted to:\n{\n\tverbose_log_trace = false\n}", SnakeApplication.class, true);
        }
        else
            showTrace = (boolean) temp;
        LogUtils.info("Configuration file loaded", SnakeApplication.class, true);

    }

    @Override
    public void readPropertiesFile() {
        FileReader fr = null;
        try {
            File file = new File (propsFilePath + "Snake.properties");
            fr = new FileReader (file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            LogUtils.info("Reading properties file", SnakeApplication.class, true);
            contextPropsFile.load(br);
            LogUtils.info("Properties file read", SnakeApplication.class, true);
        }
        catch (IOException e) {
            LogUtils.error("Not exists properties file", SnakeApplication.class, true);
        }
        finally{
            try{
                if( null != fr ){
                    fr.close();
                }
            }catch (Exception e2){
                e2.printStackTrace();
            }
        }
    }

    @Override
    public void loadPropertiesFile() throws Exception {
        LogUtils.info("Loading properties file", SnakeApplication.class, true);
        for (Class<?> clazz : classesToScan) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Prop.class)) {
                    String name = !field.getAnnotation(Prop.class).name().isEmpty()
                            ? field.getAnnotation(Prop.class).name()
                            : null;
                    if (name == null) {
                        LogUtils.error("Is not a valid property name: "+name, SnakeApplication.class, true);
                        return;
                    }
                    Object injection = contextPropsFile.get(name);
                    if (injection == null) {
                        LogUtils.error("The property name "+name+" is not registered in the properties file", SnakeApplication.class, true);
                        return;
                    }
                    if (field.getType() == String.class)
                        setFieldValue(field, getClassInstance(field.getType()), injection);
                    else if (field.getType() == int.class || field.getType() == Integer.class) {
                        setFieldValue(field, Integer.valueOf("0") , Integer.valueOf(injection.toString()));
                    }
                    else if (field.getType() == long.class || field.getType() == Long.class) {
                        setFieldValue(field, Long.valueOf("0") , Long.valueOf(injection.toString()));
                    }
                    else if (field.getType() == float.class || field.getType() == Float.class) {
                        setFieldValue(field, Float.valueOf("0") , Float.valueOf(injection.toString()));
                    }
                    else if (field.getType() == double.class || field.getType() == Double.class) {
                        setFieldValue(field, Double.valueOf("0") , Double.valueOf(injection.toString()));
                    }
                    else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        setFieldValue(field, Boolean.valueOf("0"), Boolean.valueOf(injection.toString()));
                    }
                }
            }
        }
        LogUtils.info("Properties file loaded", SnakeApplication.class, true);
    }

    /**
     *
     * @param clazz The class to be instanced by her constructor
     * @return The instance created
     * @throws Exception
     */
    private Object getClassInstance(Class<?> clazz) throws Exception {
        LogUtils.info("Creating class instance of "+clazz.getSimpleName(), SnakeApplication.class, showTrace);
        if (!clazz.isAnnotationPresent(CustomConstructor.class))
            return clazz.getConstructor().newInstance(null);

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

    /**
     * This method set the injection to one class instance passed.
     * @param field It's the field of one class that wants to be setted one value.
     * @param selfInstance An instance of the field.
     * @param injection The value to be setted to the instance
     * @throws IllegalAccessException
     */
    private void setFieldValue(Field field, Object selfInstance, Object injection) throws IllegalAccessException {
        boolean isAccessible = field.isAccessible();
        if (!isAccessible)
            field.setAccessible(true);
        field.set(selfInstance, injection);
        if (!isAccessible)
            field.setAccessible(false);
    }

    @Override
    public Map<String, Object> getContextConfig() {
        return contextConfigFile;
    }

    @Override
    public Properties getContextProps() {
        return contextPropsFile;
    }

    @Override
    public boolean isAutoConfigScan() {
        if (autoConfigAnnotation == null) return false;
        return autoConfigAnnotation.scan().length > 0;
    }

    @Override
    public boolean isAutoConfigFilter() {
        if (autoConfigAnnotation == null) return false;
        return autoConfigAnnotation.filter().length > 0;
    }

    @Override
    public boolean includedInScan(String className) {
        if (autoConfigAnnotation == null) return false;
        Class<?>[] toScan = autoConfigAnnotation.scan();
        return Arrays.stream(toScan).anyMatch(e -> e.getSimpleName().equals(className));
    }

    @Override
    public boolean includedInFilter(String className) {
        if (autoConfigAnnotation == null) return false;
        Class<?>[] toFilter = autoConfigAnnotation.filter();
        return Arrays.stream(toFilter).anyMatch(e -> e.getSimpleName().equals(className));
    }

    @Override
    public AutoConfigApplicationContext enableVerboseLogTrace() {
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
        return obj != null && obj.getClass() == clazz;
    }

    @Override
    public void printBanner() {
        System.out.println("                 _                           \n" +
                " ___ _ __   __ _| | _____  ___ ___  _ __ ___ \n" +
                "/ __| '_ \\ / _` | |/ / _ \\/ __/ _ \\| '__/ _ \\\n" +
                "\\__ \\ | | | (_| |   <  __/ (_| (_) | | |  __/\n" +
                "|___/_| |_|\\__,_|_|\\_\\___|\\___\\___/|_|  \\___| "+CURRENT_VERSION+" by MarioParrilla");
    }
}
