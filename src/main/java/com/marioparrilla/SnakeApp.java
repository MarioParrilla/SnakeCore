package com.marioparrilla;

import com.marioparrilla.Annotations.Egg;
import com.marioparrilla.Annotations.OpenEgg;
import com.marioparrilla.Context.SnakeApplication;
import com.marioparrilla.ObjectsToTest.Dependency;
import com.marioparrilla.ObjectsToTest.Example;

//@ComponentScan()
public class SnakeApp {

    @OpenEgg
    public static Dependency dependency;

    public static void main(String[] args) throws Exception {
        var context = SnakeApplication.init(SnakeApp.class, args)
                .registerCestEggsClass(new Class[] {SnakeApp.class, Cest.class})
                .classesToScan(new Class[] {SnakeApp.class, Dependency.class})
                .run();

        dependency.sayHello();
    }

    @Egg
    public Example getExample() {
        return new Example();
    }
}
