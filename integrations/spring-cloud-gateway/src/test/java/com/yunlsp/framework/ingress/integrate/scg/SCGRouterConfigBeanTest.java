package com.yunlsp.framework.ingress.integrate.scg;

import com.google.common.io.ByteStreams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertNotNull;

/**
 * {@link SCGRouterConfigBeanTest}
 *
 * <p>Class SCGRouterConfigBeanTest Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/23
 */
@SpringBootApplication
@SpringBootTest(classes = SCGRouterConfigBeanTest.class)
@RunWith(SpringRunner.class)
public class SCGRouterConfigBeanTest {

  @Test
  public void loadResource() throws Exception {

    ResourceLoader resourceLoader = new DefaultResourceLoader();

    Resource resource = resourceLoader.getResource("classpath:/ingress-router-ext.yaml");

    assertNotNull(resource);

    byte[] bytes = ByteStreams.toByteArray(resource.getInputStream());

    assertNotNull(bytes);

    System.out.println(new String(bytes, StandardCharsets.UTF_8));
  }

  @Test
  public void testResourceUtil() throws Exception {

    String file = ResourceUtils.getFile(StringUtils.trimAllWhitespace("classpath: ingress-router-ext.yaml")).getAbsolutePath();

    System.out.println(file);
  }

}