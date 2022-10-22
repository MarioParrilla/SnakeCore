package com.marioparrilla.snake.ObjectsToTest;

import com.marioparrilla.snake.annotations.OpenEgg;

public class Dependency {

    @OpenEgg(name = "exa")
    Example example;

    public void sayHello() {
        example.sayHello();
    }
}
