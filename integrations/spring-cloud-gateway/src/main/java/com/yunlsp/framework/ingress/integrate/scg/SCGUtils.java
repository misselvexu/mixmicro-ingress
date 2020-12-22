package com.yunlsp.framework.ingress.integrate.scg;

import com.alibaba.fastjson.JSONObject;
import com.yunlsp.framework.ingress.IngressConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static com.yunlsp.framework.ingress.IngressConstants.CHINESE;

/**
 * {@link SCGUtils}
 *
 * <p>Class SCGUtils Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/22
 */
public class SCGUtils implements Serializable {

  private static final String UNKNOWN = "unknown";

  public static final String FULL_TIME_PATTERN = "yyyyMMddHHmmss";

  public static final String FULL_TIME_SPLIT_PATTERN = "yyyy-MM-dd HH:mm:ss";

  public static final String CST_TIME_PATTERN = "EEE MMM dd HH:mm:ss zzz yyyy";

  /**
   * 驼峰转下划线
   *
   * @param value 待转换值
   * @return 结果
   */
  public static String camelToUnderscore(String value) {
    if (StringUtils.isBlank(value)) {
      return value;
    }
    String[] arr = StringUtils.splitByCharacterTypeCamelCase(value);
    if (arr.length == 0) {
      return value;
    }
    StringBuilder result = new StringBuilder();
    IntStream.range(0, arr.length).forEach(i -> {
      if (i != arr.length - 1) {
        result.append(arr[i]).append("_");
      } else {
        result.append(arr[i]);
      }
    });
    return StringUtils.lowerCase(result.toString());
  }

  /**
   * 下划线转驼峰
   *
   * @param value 待转换值
   * @return 结果
   */
  public static String underscoreToCamel(String value) {
    StringBuilder result = new StringBuilder();
    String[] arr = value.split(IngressConstants.StringConstant.UNDER_LINE);
    for (String s : arr) {
      result.append((String.valueOf(s.charAt(0))).toUpperCase()).append(s.substring(1));
    }
    return result.toString();
  }

  /**
   * 判断是否为 ajax请求
   *
   * @param request HttpServletRequest
   * @return boolean
   */
  public static boolean isAjaxRequest(HttpServletRequest request) {
    return (request.getHeader("X-Requested-With") != null
        && "XMLHttpRequest".equals(request.getHeader("X-Requested-With")));
  }

  /**
   * 正则校验
   *
   * @param regex 正则表达式字符串
   * @param value 要匹配的字符串
   * @return 正则校验结果
   */
  public static boolean match(String regex, String value) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(value);
    return matcher.matches();
  }

  /**
   * 设置响应
   *
   * @param response    HttpServletResponse
   * @param contentType content-type
   * @param status      http状态码
   * @param value       响应内容
   * @throws IOException IOException
   */
  public static void makeResponse(HttpServletResponse response, String contentType,
                                  int status, Object value) throws IOException {
    response.setContentType(contentType);
    response.setStatus(status);
    response.getOutputStream().write(JSONObject.toJSONString(value).getBytes());
  }

  /**
   * 设置成功响应
   *
   * @param response HttpServletResponse
   * @param value    响应内容
   * @throws IOException IOException
   */
  public static void makeSuccessResponse(HttpServletResponse response, Object value) throws IOException {
    makeResponse(response, MediaType.APPLICATION_JSON_VALUE, HttpServletResponse.SC_OK, value);
  }

  /**
   * 设置失败响应
   *
   * @param response HttpServletResponse
   * @param value    响应内容
   * @throws IOException IOException
   */
  public static void makeFailureResponse(HttpServletResponse response, Object value) throws IOException {
    makeResponse(response, MediaType.APPLICATION_JSON_VALUE, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, value);
  }

  /**
   * 设置JSON类型响应
   *
   * @param response HttpServletResponse
   * @param status   http状态码
   * @param value    响应内容
   * @throws IOException IOException
   */
  public static void makeJsonResponse(HttpServletResponse response, int status, Object value) throws IOException {
    makeResponse(response, MediaType.APPLICATION_JSON_VALUE, status, value);
  }

  /**
   * 设置webflux模型响应
   *
   * @param response    ServerHttpResponse
   * @param contentType content-type
   * @param status      http状态码
   * @param value       响应内容
   * @return Mono<Void>
   */
  public static Mono<Void> makeWebFluxResponse(ServerHttpResponse response, String contentType,
                                               HttpStatus status, Object value) {
    response.setStatusCode(status);
    response.getHeaders().add(HttpHeaders.CONTENT_TYPE, contentType);
    DataBuffer dataBuffer = response.bufferFactory().wrap(JSONObject.toJSONString(value).getBytes());
    return response.writeWith(Mono.just(dataBuffer));
  }

  /**
   * 获取HttpServletRequest
   *
   * @return HttpServletRequest
   */
  public static HttpServletRequest getHttpServletRequest() {
    return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
  }

  /**
   * 获取请求IP
   *
   * @return String IP
   */
  public static String getHttpServletRequestIpAddress() {
    HttpServletRequest request = getHttpServletRequest();
    String ip = request.getHeader("x-forwarded-for");
    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
  }

  /**
   * 获取请求IP
   *
   * @param request ServerHttpRequest
   * @return String IP
   */
  public static String getServerHttpRequestIpAddress(ServerHttpRequest request) {
    HttpHeaders headers = request.getHeaders();
    String ip = headers.getFirst("x-forwarded-for");
    if (ip != null && ip.length() != 0 && !UNKNOWN.equalsIgnoreCase(ip)) {
      if (ip.contains(IngressConstants.StringConstant.COMMA)) {
        ip = ip.split(IngressConstants.StringConstant.COMMA)[0];
      }
    }
    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = headers.getFirst("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = headers.getFirst("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = headers.getFirst("HTTP_CLIENT_IP");
    }
    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
    }
    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = headers.getFirst("X-Real-IP");
    }
    if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
      ip = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
    }
    return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
  }

  /**
   * 判断是否包含中文
   *
   * @param value 内容
   * @return 结果
   */
  public static boolean containChinese(String value) {
    if (StringUtils.isBlank(value)) {
      return Boolean.FALSE;
    }
    Matcher matcher = CHINESE.matcher(value);
    return matcher.find();
  }

  /**
   * 格式化时间，格式为 yyyyMMddHHmmss
   *
   * @param localDateTime LocalDateTime
   * @return 格式化后的字符串
   */
  public static String formatFullTime(LocalDateTime localDateTime) {
    return formatFullTime(localDateTime, FULL_TIME_PATTERN);
  }

  /**
   * 根据传入的格式，格式化时间
   *
   * @param localDateTime LocalDateTime
   * @param format        格式
   * @return 格式化后的字符串
   */
  public static String formatFullTime(LocalDateTime localDateTime, String format) {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
    return localDateTime.format(dateTimeFormatter);
  }

  /**
   * 根据传入的格式，格式化时间
   *
   * @param date   Date
   * @param format 格式
   * @return 格式化后的字符串
   */
  public static String getDateFormat(Date date, String format) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
    return simpleDateFormat.format(date);
  }

  /**
   * 格式化 CST类型的时间字符串
   *
   * @param date   CST类型的时间字符串
   * @param format 格式
   * @return 格式化后的字符串
   * @throws java.text.ParseException 异常
   */
  public static String formatCstTime(String date, String format) throws ParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CST_TIME_PATTERN, Locale.US);
    Date usDate = simpleDateFormat.parse(date);
    return getDateFormat(usDate, format);
  }

  /**
   * 格式化 Instant
   *
   * @param instant Instant
   * @param format  格式
   * @return 格式化后的字符串
   */
  public static String formatInstant(Instant instant, String format) {
    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    return localDateTime.format(DateTimeFormatter.ofPattern(format));
  }

  /**
   * 判断当前时间是否在指定时间范围
   *
   * @param from 开始时间
   * @param to   结束时间
   * @return 结果
   */
  public static boolean between(LocalTime from, LocalTime to) {
    LocalTime now = LocalTime.now();
    return now.isAfter(from) && now.isBefore(to);
  }
}

