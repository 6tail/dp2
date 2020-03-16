package com.dp2.exception;

/**
 * 不支持该数据源的解析
 *
 * @author 6tail
 *
 */
public class ParserNotSupportException extends ParserException{
  private static final long serialVersionUID = 1L;

  public ParserNotSupportException(){
    super();
  }

  public ParserNotSupportException(String message){
    super(message);
  }

  public ParserNotSupportException(Throwable cause){
    super(cause);
  }

  public ParserNotSupportException(String message,Throwable cause){
    super(message,cause);
  }
}
