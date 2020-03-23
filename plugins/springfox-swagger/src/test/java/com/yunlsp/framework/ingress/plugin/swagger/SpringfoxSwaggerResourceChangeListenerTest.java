package com.yunlsp.framework.ingress.plugin.swagger;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

/**
 * {@link SpringfoxSwaggerResourceChangeListenerTest}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/3/23
 */
public class SpringfoxSwaggerResourceChangeListenerTest {

  private static volatile List<Integer> arrays = Lists.newArrayList();

  @Before
  public void init() {
    for (int i = 0; i < 10; i++) {
      arrays.add(RandomUtils.nextInt());
    }

    Collections.sort(arrays);
    arrays.forEach(System.out::println);
  }

  @Test
  public void testStream() {
    System.out.println("starting ");
    arrays.parallelStream().forEach(System.out::println);
    System.out.println("end");
  }
}
