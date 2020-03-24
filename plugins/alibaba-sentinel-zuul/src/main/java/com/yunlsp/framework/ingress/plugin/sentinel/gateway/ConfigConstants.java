package com.yunlsp.framework.ingress.plugin.sentinel.gateway;


import com.yunlsp.framework.ingress.plugin.sentinel.gateway.zuul.SentinelZuulProperties;

/**
 * {@link ConfigConstants}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/23
 */
public final class ConfigConstants {

	/**
	 * Netflix Zuul type.
	 */
	public static final String APP_TYPE_ZUUL_GATEWAY = "12";

	/**
	 * ConfigurationProperties for {@link SentinelZuulProperties}.
	 */
	public static final String ZUUL_PREFIX = "spring.cloud.sentinel.zuul";

	private ConfigConstants() {
		throw new AssertionError("Must not instantiate constant utility class");
	}

}
