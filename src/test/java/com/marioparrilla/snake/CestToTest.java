package com.marioparrilla.snake;

import com.marioparrilla.snake.ObjectsToTest.Example;
import com.marioparrilla.snake.annotations.Cest;
import com.marioparrilla.snake.annotations.Egg;
import com.marioparrilla.snake.ObjectsToTest.Dependency;

@Cest
public class CestToTest {

    @Egg
    public Dependency getDependency() {
        return new Dependency();
    }

    @Egg(name = "idForExampleClass")
    public String getIdString() {
        return "CustomParam";
    }

    @Egg
    public String getIdString0() {
        return "NoCustomParam";
    }

    @Egg(name = "Example")
    public Example getExample() {
        return new Example("Example");
    }
}
