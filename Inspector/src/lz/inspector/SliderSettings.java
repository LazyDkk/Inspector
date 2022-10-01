package lz.inspector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SliderSettings {
	float min() default Slider.Settings.MIN;

	float max() default Slider.Settings.MAX;

	float precision() default Slider.Settings.PRECISION;

	float tick() default Slider.Settings.TICK;
}