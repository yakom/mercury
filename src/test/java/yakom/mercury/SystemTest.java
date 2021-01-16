package yakom.mercury;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Test
@Tag("systemTest")
@Target(METHOD)
@Retention(RUNTIME)
public @interface SystemTest {}
