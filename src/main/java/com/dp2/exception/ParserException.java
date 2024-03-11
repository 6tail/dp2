package com.dp2.exception;

import java.io.IOException;

/**
 * 解析异常
 *
 * @author 6tail
 */
public class ParserException extends IOException {
  private static final long serialVersionUID = 1L;

  public ParserException() {
    super();
  }

  public ParserException(String message) {
    super(message);
  }

  public ParserException(Throwable cause) {
    super();
    super.initCause(cause);
  }

  public ParserException(String message, Throwable cause) {
    super(message);
    super.initCause(cause);
  }
}
