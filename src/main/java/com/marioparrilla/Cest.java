package com.marioparrilla;

import com.marioparrilla.Annotations.Egg;
import com.marioparrilla.Context.Example;
import com.marioparrilla.Context.Injection;

public class Cest {
    @Egg
    public Example getExample() {
        return new Example();
    }

    @Egg
    public Injection getInjection() {
        return new Injection();
    }
}
