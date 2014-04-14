package com.jaouan.junit.datas;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Datas annotation.
 * 
 * @author Maxence Jaouan
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Datas {

  /**
   * Get file path.
   * 
   * @return file path.
   */
  String value();

  /**
   * Get datas count.
   * 
   * @return Datas count.
   */
  long count() default 0;

  /**
   * Get inject datas once time. FALSE by default. Assuming field is already injected if not null.
   * 
   * @return Inject datas once time.
   */
  boolean once() default false;

  /**
   * Get shuffle state. FALSE by default.
   * 
   * @return Shuffle state.
   */
  boolean shuffle() default false;

}
