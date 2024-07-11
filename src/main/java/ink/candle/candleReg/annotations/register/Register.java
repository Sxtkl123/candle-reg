package ink.candle.candleReg.annotations.register;

import ink.candle.candleReg.annotations.register.enums.TypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
public @interface Register {

    /**
     * mod id
     * @return mod id
     */
    String value();

    TypeEnum type();

    String name() default "";

}
