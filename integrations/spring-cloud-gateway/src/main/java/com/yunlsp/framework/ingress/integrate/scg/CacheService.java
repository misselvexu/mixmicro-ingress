package com.yunlsp.framework.ingress.integrate.scg;

/**
 * {@link CacheService}
 *
 * <p>Class CacheService Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/22
 */
public interface CacheService {

  /**
   * 获取当前请求次数
   *
   * @param uri uri
   * @param ip ip
   * @return 次数
   */
  int getCurrentRequestCount(String uri, String ip);

  /**
   * 设置请求次数
   *
   * @param uri uri
   * @param ip ip
   * @param time time
   */
  void setCurrentRequestCount(String uri, String ip, Long time);

  /**
   * 递增请求次数
   *
   * @param uri uri
   * @param ip ip
   */
  void incrCurrentRequestCount(String uri, String ip);
}
