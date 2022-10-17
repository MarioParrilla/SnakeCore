package com.marioparrilla.snake;

import com.marioparrilla.snake.Annotations.Egg;
import com.marioparrilla.snake.Annotations.OpenEgg;
import com.marioparrilla.snake.Context.SnakeApplication;
import com.marioparrilla.snake.ObjectsToTest.Dependency;
import com.marioparrilla.snake.ObjectsToTest.Example;

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

    @Egg(name = "exa")
    public Example getExample() {
        return new Example();
    }
}
