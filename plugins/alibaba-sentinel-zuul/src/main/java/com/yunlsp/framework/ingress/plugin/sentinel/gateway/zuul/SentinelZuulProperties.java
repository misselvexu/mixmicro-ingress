package com.yunlsp.framework.ingress.plugin.sentinel.gateway.zuul;

import com.alibaba.csp.sentinel.adapter.gateway.zuul.constants.ZuulConstant;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.filters.SentinelZuulErrorFilter;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.filters.SentinelZuulPostFilter;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.filters.SentinelZuulPreFilter;
import com.yunlsp.framework.ingress.plugin.sentinel.gateway.ConfigConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * {@link SentinelZuulProperties}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/23
 */
@ConfigurationProperties(prefix = ConfigConstants.ZUUL_PREFIX)
public class SentinelZuulProperties {

	@NestedConfigurationProperty
	private SentinelZuulProperties.Order order = new SentinelZuulProperties.Order();

	public Order getOrder() {
		return order;
	}

	public SentinelZuulProperties setOrder(Order order) {
		this.order = order;
		return this;
	}

	public static class Order {

		/**
		 * The order of {@link SentinelZuulPreFilter}.
		 */
		private int pre = 10000;

		/**
		 * The order of {@link SentinelZuulPostFilter}.
		 */
		private int post = ZuulConstant.SEND_RESPONSE_FILTER_ORDER;

		/**
		 * The order of {@link SentinelZuulErrorFilter}.
		 */
		private int error = -1;

		public int getPre() {
			return pre;
		}

		public void setPre(int pre) {
			this.pre = pre;
		}

		public int getPost() {
			return post;
		}

		public void setPost(int post) {
			this.post = post;
		}

		public int getError() {
			return error;
		}

		public void setError(int error) {
			this.error = error;
		}

	}

}
