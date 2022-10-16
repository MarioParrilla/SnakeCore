package com.marioparrilla.ObjectsToTest;

import com.marioparrilla.Annotations.OpenEgg;

public class Dependency {

    @OpenEgg
    Example example;

    public void sayHello() {
        example.sayHello();
    }
}
