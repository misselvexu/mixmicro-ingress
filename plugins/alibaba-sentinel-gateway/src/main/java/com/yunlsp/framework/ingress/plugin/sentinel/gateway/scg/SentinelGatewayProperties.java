package com.yunlsp.framework.ingress.plugin.sentinel.gateway.scg;

import com.yunlsp.framework.ingress.plugin.sentinel.gateway.ConfigConstants;
import com.yunlsp.framework.ingress.plugin.sentinel.gateway.FallbackProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.Ordered;

/**
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
@ConfigurationProperties(prefix = ConfigConstants.GATEWAY_PREFIX)
public class SentinelGatewayProperties {

	@NestedConfigurationProperty
	private FallbackProperties fallback;

	private Integer order = Ordered.HIGHEST_PRECEDENCE;

	public FallbackProperties getFallback() {
		return fallback;
	}

	public SentinelGatewayProperties setFallback(FallbackProperties fallback) {
		this.fallback = fallback;
		return this;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

}
