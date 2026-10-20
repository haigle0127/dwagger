package com.haigle.dwagger.spring.annotations;

import com.haigle.dwagger.spring.configuration.DwaggerAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/***
 * Enable Dwagger enhanced annotation and use @EnableSwagger2 annotation together.
 *
 * inlude:
 * <ul>
 *     <li>Interface sorting </li>
 *     <li>Interface document download  (word)</li>
 * </ul>
 * @author haigle
 * @since 2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({DwaggerAutoConfiguration.class})
@ConditionalOnWebApplication
public @interface EnableDwagger {
}
