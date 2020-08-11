package com.yunlsp.framework.ingress.integrate.zuul;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link PattenTester}
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 8/11/20
 */
public class PattenTester {


  @Test
  public void url0() {

    Pattern pattern = Pattern.compile("swagger-ui\\.html");

    //   /xxx/xxxx/swagger-ui.html

    //   /api/act/health
    // ^/.*?swagger-ui\.html

    // /api/**    -> /api/aa/aaa

    // ^/api/.+


    Matcher matcher  = pattern.matcher("http://127.0.0.1:7777/workshop/swagger-ui.html");

    System.out.println(matcher.find());


  }


}
