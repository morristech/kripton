package com.abubusoft.kripton.processor.test04primary_key;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.abubusoft.kripton.android.KriptonLibrary;
import com.abubusoft.kripton.android.sqlite.AbstractBindDatabaseHelper;
import com.abubusoft.kripton.common.Logger;
import java.lang.Override;
import java.lang.String;

public class BindDummy03Database extends AbstractBindDatabaseHelper implements BindDummy03DaoFactory {
  private static BindDummy03Database instance;

  public static final String name = "dummy";

  public static final int version = 1;

  protected BindDaoBean03 daoBean03 = new BindDaoBean03();

  protected BindDummy03Database(Context context) {
    super(context, name, null, version);
  }

  public BindDaoBean03 getDaoBean03() {
    daoBean03.setDatabase(getWritableDatabase());
    return daoBean03;
  }

  @Override
  public BindDaoBean03 getDaoBean03(SQLiteDatabase database) {
    daoBean03.setDatabase(database);
    return daoBean03;
  }

  /**
   * <p>Allow to execute a transaction. The database will be open in write mode.</p>
   *
   * @param transaction
   * 	transaction to execute
   */
  public void execute(Transaction transaction) {
    SQLiteDatabase database=getWritableDatabase();
    try {
      database.beginTransaction();
      if (transaction!=null && transaction.onExecute(this, database)) {
        database.setTransactionSuccessful();
      }
    } finally {
      database.endTransaction();
    }
  }

  /**
   *
   * instance
   */
  public static BindDummy03Database instance() {
    if (instance==null) {
      instance=new BindDummy03Database(KriptonLibrary.context);
    }
    return instance;
  }

  /**
   *
   * onCreate
   *
   */
  @Override
  public void onCreate(SQLiteDatabase database) {
    // generate tables
    Logger.info("DDL: %s",Bean03Table.CREATE_TABLE_SQL);
    database.execSQL(Bean03Table.CREATE_TABLE_SQL);
  }

  /**
   *
   * onUpgrade
   */
  @Override
  public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    // drop tables
    Logger.info("DDL: %s",Bean03Table.DROP_TABLE_SQL);
    database.execSQL(Bean03Table.DROP_TABLE_SQL);

    // generate tables
    Logger.info("DDL: %s",Bean03Table.CREATE_TABLE_SQL);
    database.execSQL(Bean03Table.CREATE_TABLE_SQL);
  }

  public interface Transaction extends AbstractTransaction<BindDummy03DaoFactory> {
  }
}