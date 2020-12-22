package com.yunlsp.framework.ingress;

import java.util.regex.Pattern;

/**
 * {@link IngressConstants}
 *
 * <p>Class IngressConstants Definition
 *
 * @author <a href="mailto:iskp.me@gmail.com">Elve.Xu</a>
 * @version ${project.version} - 2020/12/22
 */
public interface IngressConstants {

  /**
   * Gateway请求头TOKEN名称（不要有空格）
   */
  String GATEWAY_TOKEN_HEADER = "ingress-scg-token";
  /**
   * Gateway请求头TOKEN值
   */
  String GATEWAY_TOKEN_VALUE = "mixmicro:ingress:scg:misselvexu";

  /**
   * 简单手机号正则（这里只是简单校验是否为 11位，实际规则更复杂）
   */
  String MOBILE = "[1]\\d{10}";
  /**
   * 中文正则
   */
  Pattern CHINESE = Pattern.compile("[\u4e00-\u9fa5]");

  /**
   * String Constants Defined .
   *
   * <p></p>
   */
  interface StringConstant {

    String AMPERSAND = "&";
    String AND = "and";
    String AT = "@";
    String ASTERISK = "*";
    String STAR = "*";
    String BACK_SLASH = "\\";
    String COLON = ":";
    String DOUBLE_COLON = "::";
    String COMMA = ",";
    String DASH = "-";
    String DOLLAR = "$";
    String DOT = ".";
    String UNDER_LINE = "_";
    String DOUBLE_DOT = "..";
    String DOT_CLASS = ".class";
    String DOT_JAVA = ".java";
    String DOT_XML = ".xml";
    String EMPTY = "";
    String EQUALS = "=";
    String FALSE = "false";
    String SLASH = "/";
    String HASH = "#";
    String HAT = "^";
    String LEFT_BRACE = "{";
    String LEFT_BRACKET = "(";
    String LEFT_CHEV = "<";
    String DOT_NEWLINE = ",\n";
    String NEWLINE = "\n";
    String N = "n";
    String NO = "no";
    String NULL = "null";
    String OFF = "off";
    String ON = "on";
    String PERCENT = "%";
    String PIPE = "|";
    String PLUS = "+";
    String QUESTION_MARK = "?";
    String EXCLAMATION_MARK = "!";
    String QUOTE = "\"";
    String RETURN = "\r";
    String TAB = "\t";
    String RIGHT_BRACE = "}";
    String RIGHT_BRACKET = ")";
    String RIGHT_CHEV = ">";
    String SEMICOLON = ";";
    String SINGLE_QUOTE = "'";
    String BACKTICK = "`";
    String SPACE = " ";
    String TILDA = "~";
    String LEFT_SQ_BRACKET = "[";
    String RIGHT_SQ_BRACKET = "]";
    String TRUE = "true";
    String UNDERSCORE = "_";
    String UTF_8 = "UTF-8";
    String US_ASCII = "US-ASCII";
    String ISO_8859_1 = "ISO-8859-1";
    String Y = "y";
    String YES = "yes";
    String ONE = "1";
    String ZERO = "0";
    String DOLLAR_LEFT_BRACE = "${";
    String HASH_LEFT_BRACE = "#{";
    String CRLF = "\r\n";

  }

}
