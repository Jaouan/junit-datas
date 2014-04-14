package com.jaouan.junit.datas;

/**
 * Datas rule exception.
 * 
 * @author Maxence Jaouan
 * 
 */
public class DatasRuleException extends Exception {

  /**
   * serialVersionUID.
   */
  private static final long serialVersionUID = -5188483795760944700L;

  /**
   * DatasRuleException's contructor.
   * 
   * @param message
   *          Message.
   * @param cause
   *          Cause.
   */
  public DatasRuleException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * DatasRuleException's contructor.
   * 
   * @param message
   *          Message.
   */
  public DatasRuleException(final String message) {
    super(message);
  }

}
