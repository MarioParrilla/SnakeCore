package com.marioparrilla.ObjectsToTest;

import com.marioparrilla.Annotations.OpenEgg;

public class Dependency {

    @OpenEgg(name = "exa")
    Example example;

    public void sayHello() {
        example.sayHello();
    }
}
