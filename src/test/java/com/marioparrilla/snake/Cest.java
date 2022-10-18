package com.marioparrilla.snake;

import com.marioparrilla.snake.Annotations.Egg;
import com.marioparrilla.snake.Context.ApplicationContext;
import com.marioparrilla.snake.Context.SnakeApplication;
import com.marioparrilla.snake.ObjectsToTest.Dependency;
import com.marioparrilla.snake.ObjectsToTest.Example;

public class Cest {

    @Egg
    public Dependency getDependency() {
        return new Dependency();
    }

    @Egg(name = "exa")
    public Example getExample() {
        return new Example("mario");
    }
}
