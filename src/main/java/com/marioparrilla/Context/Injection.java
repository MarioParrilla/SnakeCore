package com.marioparrilla.Context;

import com.marioparrilla.Annotations.OpenEgg;

public class Injection {

    @OpenEgg
    Example example;

    public void sayHello() {
        example.sayHello();
    }
}
