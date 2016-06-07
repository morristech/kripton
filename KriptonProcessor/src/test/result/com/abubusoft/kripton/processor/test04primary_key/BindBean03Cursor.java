package com.abubusoft.kripton.processor.test04primary_key;

import android.database.Cursor;
import java.util.LinkedList;

/**
 * Generated by Kripton Library.
 *
 *  @since Tue Jun 07 22:47:03 CEST 2016
 *
 */
public class BindBean03Cursor {
  protected Cursor cursor;

  /**
   * Index for column "id"
   */
  protected int index0;

  /**
   * Index for column "text"
   */
  protected int index1;

  BindBean03Cursor(Cursor cursor) {
    wrap(cursor);
  }

  public BindBean03Cursor wrap(Cursor cursor) {
    this.cursor=cursor;

    index0=cursor.getColumnIndex("id");
    index1=cursor.getColumnIndex("text");

    return this;
  }

  public LinkedList<Bean03> execute() {

    LinkedList<Bean03> resultList=new LinkedList<Bean03>();
    Bean03 resultBean=new Bean03();

    if (cursor.moveToFirst()) {
      do
       {
        resultBean=new Bean03();

        if (index0>=0 && !cursor.isNull(index0)) { resultBean.setId(cursor.getLong(index0));}
        if (index1>=0 && !cursor.isNull(index1)) { resultBean.setText(cursor.getString(index1));}

        resultList.add(resultBean);
      } while (cursor.moveToNext());
    }
    cursor.close();

    return resultList;
  }

  public void executeListener(OnBean03Listener listener) {
    Bean03 resultBean=new Bean03();

    if (cursor.moveToFirst()) {
      do
       {
        if (index0>=0) { resultBean.setId(null);}
        if (index1>=0) { resultBean.setText(null);}

        if (index0>=0 && !cursor.isNull(index0)) { resultBean.setId(cursor.getLong(index0));}
        if (index1>=0 && !cursor.isNull(index1)) { resultBean.setText(cursor.getString(index1));}

        listener.onRow(resultBean, cursor.getPosition(),cursor.getCount());
      } while (cursor.moveToNext());
    }
    cursor.close();
  }

  public static BindBean03Cursor create(Cursor cursor) {
    return new BindBean03Cursor(cursor);
  }

  /**
   * Listener for row read from database.
   *
   * @param bean bean read from database. Only selected columns/fields are valorized.
   * @param rowPosition position of row.
   * @param rowCount total number of rows.
   *
   */
  public interface OnBean03Listener {
    void onRow(Bean03 bean, int rowPosition, int rowCount);
  }
}
