package com.marioparrilla.snake.ObjectsToTest;

import com.marioparrilla.snake.annotations.CustomConstructor;
import com.marioparrilla.snake.annotations.CustomParam;

@CustomConstructor
public class Example {
    private String name = "No name";

    public Example(String name) {
        this.name = name;
    }

//    public Example(@CustomParam(eggName = "idForExampleClass") String name) {
//        this.name = name;
//    }

    public void sayHello(){
        System.out.println("Hello "+name);
    }

    @Override
    public String toString() {
        return "Example{" +
                "name='" + name + '\'' +
                '}';
    }
}
