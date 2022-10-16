package com.marioparrilla;

import com.marioparrilla.Annotations.OpenEgg;
import com.marioparrilla.Context.Injection;
import com.marioparrilla.Context.SnakeApplication;

//@ComponentScan()
public class Snake {

    @OpenEgg
    public static Injection injection;

    public static void main(String[] args) throws Exception {
        var context = SnakeApplication.init(Snake.class, args)
                .registerCestEggsClass(new Class[] {Cest.class})
                .classesToScan(new Class[] {Snake.class, Injection.class})
                .run();

        injection.sayHello();
    }

}
