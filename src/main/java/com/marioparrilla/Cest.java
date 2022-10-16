package com.marioparrilla;

import com.marioparrilla.Annotations.Egg;
import com.marioparrilla.ObjectsToTest.Dependency;

public class Cest {
    @Egg
    public Dependency getDependency() {
        return new Dependency();
    }
}
