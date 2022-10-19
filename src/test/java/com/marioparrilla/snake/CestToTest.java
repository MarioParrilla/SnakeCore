package com.marioparrilla.snake;

import com.marioparrilla.snake.Annotations.Cest;
import com.marioparrilla.snake.Annotations.Egg;
import com.marioparrilla.snake.ObjectsToTest.Dependency;
import com.marioparrilla.snake.ObjectsToTest.Example;

@Cest
public class CestToTest {

    @Egg
    public Dependency getDependency() {
        return new Dependency();
    }

    @Egg(name = "exa")
    public Example getExample() {
        return new Example("Snake");
    }
}
