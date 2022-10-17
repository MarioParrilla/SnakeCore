package com.marioparrilla.snake;

import com.marioparrilla.snake.Annotations.Egg;
import com.marioparrilla.snake.ObjectsToTest.Dependency;

public class Cest {
    @Egg
    public Dependency getDependency() {
        return new Dependency();
    }
}
