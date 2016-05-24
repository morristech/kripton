package com.abubusoft.kripton.processor.test03;

import java.lang.String;

/**
 * Generated by Kripton Library.
 *
 *  @since Wed May 25 01:06:01 CEST 2016
 *
 */
public class Bean01Table {
  public static final String TABLE_NAME = "bean01";

  public static final String CREATE_TABLE_SQL = "CREATE TABLE bean01( id INTEGER PRIMARY KEY AUTOINCREMENT, message_date INTEGER, message_text TEXT NOT NULL, value INTEGER);";

  public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS bean01;";

  public static final String COLUMN_ID = "id";

  public static final String COLUMN_MESSAGE_DATE = "message_date";

  public static final String COLUMN_MESSAGE_TEXT = "message_text";

  public static final String COLUMN_VALUE = "value";
}
