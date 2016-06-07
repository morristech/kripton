package com.abubusoft.kripton.processor.test03;

import android.database.Cursor;
import java.util.LinkedList;

/**
 * Generated by Kripton Library.
 *
 *  @since Tue Jun 07 22:47:02 CEST 2016
 *
 */
public class BindBean01Cursor {
  protected Cursor cursor;

  /**
   * Index for column "id"
   */
  protected int index0;

  /**
   * Index for column "messageDate"
   */
  protected int index1;

  /**
   * Index for column "messageText"
   */
  protected int index2;

  /**
   * Index for column "value"
   */
  protected int index3;

  BindBean01Cursor(Cursor cursor) {
    wrap(cursor);
  }

  public BindBean01Cursor wrap(Cursor cursor) {
    this.cursor=cursor;

    index0=cursor.getColumnIndex("id");
    index1=cursor.getColumnIndex("message_date");
    index2=cursor.getColumnIndex("message_text");
    index3=cursor.getColumnIndex("value");

    return this;
  }

  public LinkedList<Bean01> execute() {

    LinkedList<Bean01> resultList=new LinkedList<Bean01>();
    Bean01 resultBean=new Bean01();

    if (cursor.moveToFirst()) {
      do
       {
        resultBean=new Bean01();

        if (index0>=0 && !cursor.isNull(index0)) { resultBean.setId(cursor.getLong(index0));}
        if (index1>=0 && !cursor.isNull(index1)) { resultBean.setMessageDate(cursor.getLong(index1));}
        if (index2>=0 && !cursor.isNull(index2)) { resultBean.setMessageText(cursor.getString(index2));}
        if (index3>=0 && !cursor.isNull(index3)) { resultBean.setValue(cursor.getLong(index3));}

        resultList.add(resultBean);
      } while (cursor.moveToNext());
    }
    cursor.close();

    return resultList;
  }

  public void executeListener(OnBean01Listener listener) {
    Bean01 resultBean=new Bean01();

    if (cursor.moveToFirst()) {
      do
       {
        if (index0>=0) { resultBean.setId(0L);}
        if (index1>=0) { resultBean.setMessageDate(0L);}
        if (index2>=0) { resultBean.setMessageText(null);}
        if (index3>=0) { resultBean.setValue(0L);}

        if (index0>=0 && !cursor.isNull(index0)) { resultBean.setId(cursor.getLong(index0));}
        if (index1>=0 && !cursor.isNull(index1)) { resultBean.setMessageDate(cursor.getLong(index1));}
        if (index2>=0 && !cursor.isNull(index2)) { resultBean.setMessageText(cursor.getString(index2));}
        if (index3>=0 && !cursor.isNull(index3)) { resultBean.setValue(cursor.getLong(index3));}

        listener.onRow(resultBean, cursor.getPosition(),cursor.getCount());
      } while (cursor.moveToNext());
    }
    cursor.close();
  }

  public static BindBean01Cursor create(Cursor cursor) {
    return new BindBean01Cursor(cursor);
  }

  /**
   * Listener for row read from database.
   *
   * @param bean bean read from database. Only selected columns/fields are valorized.
   * @param rowPosition position of row.
   * @param rowCount total number of rows.
   *
   */
  public interface OnBean01Listener {
    void onRow(Bean01 bean, int rowPosition, int rowCount);
  }
}
