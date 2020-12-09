package com.yunlsp.framework.ingress.integrate.websocket;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * {@link EnableWebSocketProxy}
 *
 * <p>Class EnableWebSocketProxy Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/9
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(WebSocketProxyAutoConfiguration.class)
public @interface EnableWebSocketProxy {}
