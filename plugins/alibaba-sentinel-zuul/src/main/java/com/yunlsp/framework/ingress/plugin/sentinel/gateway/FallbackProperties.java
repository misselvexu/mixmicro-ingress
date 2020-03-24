package com.yunlsp.framework.ingress.plugin.sentinel.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * {@link FallbackProperties}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/23
 */
public class FallbackProperties {

	/**
	 * The fallback mode for sentinel spring-cloud-gateway. choose `redirect` or
	 * `response`.
	 */
	private String mode;

	/**
	 * Redirect Url for `redirect` mode.
	 */
	private String redirect;

	/**
	 * Response Body for `response` mode.
	 */
	private String responseBody;

	/**
	 * Response Status for `response` mode.
	 */
	private Integer responseStatus = HttpStatus.TOO_MANY_REQUESTS.value();

	/**
	 * Content-Type for `response` mode.
	 */
	private String contentType = MediaType.APPLICATION_JSON.toString();

	public String getMode() {
		return mode;
	}

	public FallbackProperties setMode(String mode) {
		this.mode = mode;
		return this;
	}

	public String getRedirect() {
		return redirect;
	}

	public FallbackProperties setRedirect(String redirect) {
		this.redirect = redirect;
		return this;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public FallbackProperties setResponseBody(String responseBody) {
		this.responseBody = responseBody;
		return this;
	}

	public Integer getResponseStatus() {
		return responseStatus;
	}

	public FallbackProperties setResponseStatus(Integer responseStatus) {
		this.responseStatus = responseStatus;
		return this;
	}

	public String getContentType() {
		return contentType;
	}

	public FallbackProperties setContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

}
