package com.hidex.app.data.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.hidex.app.data.model.ProtectedApp;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ProtectedAppDao_Impl implements ProtectedAppDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ProtectedApp> __insertionAdapterOfProtectedApp;

  private final EntityDeletionOrUpdateAdapter<ProtectedApp> __deletionAdapterOfProtectedApp;

  private final SharedSQLiteStatement __preparedStmtOfDeleteProtectedAppByPackage;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllProtectedApps;

  public ProtectedAppDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfProtectedApp = new EntityInsertionAdapter<ProtectedApp>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `protected_apps` (`packageName`,`appName`,`addedTimestamp`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProtectedApp entity) {
        statement.bindString(1, entity.getPackageName());
        statement.bindString(2, entity.getAppName());
        statement.bindLong(3, entity.getAddedTimestamp());
      }
    };
    this.__deletionAdapterOfProtectedApp = new EntityDeletionOrUpdateAdapter<ProtectedApp>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `protected_apps` WHERE `packageName` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProtectedApp entity) {
        statement.bindString(1, entity.getPackageName());
      }
    };
    this.__preparedStmtOfDeleteProtectedAppByPackage = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM protected_apps WHERE packageName = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllProtectedApps = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM protected_apps";
        return _query;
      }
    };
  }

  @Override
  public Object insertProtectedApp(final ProtectedApp app,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfProtectedApp.insert(app);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertProtectedApps(final List<ProtectedApp> apps,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfProtectedApp.insert(apps);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteProtectedApp(final ProtectedApp app,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfProtectedApp.handle(app);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteProtectedAppByPackage(final String packageName,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteProtectedAppByPackage.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, packageName);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteProtectedAppByPackage.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllProtectedApps(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllProtectedApps.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllProtectedApps.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ProtectedApp>> getAllProtectedApps() {
    final String _sql = "SELECT * FROM protected_apps ORDER BY appName ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"protected_apps"}, new Callable<List<ProtectedApp>>() {
      @Override
      @NonNull
      public List<ProtectedApp> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfAddedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "addedTimestamp");
          final List<ProtectedApp> _result = new ArrayList<ProtectedApp>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProtectedApp _item;
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpAppName;
            _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            final long _tmpAddedTimestamp;
            _tmpAddedTimestamp = _cursor.getLong(_cursorIndexOfAddedTimestamp);
            _item = new ProtectedApp(_tmpPackageName,_tmpAppName,_tmpAddedTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllProtectedAppsList(
      final Continuation<? super List<ProtectedApp>> $completion) {
    final String _sql = "SELECT * FROM protected_apps";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ProtectedApp>>() {
      @Override
      @NonNull
      public List<ProtectedApp> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "packageName");
          final int _cursorIndexOfAppName = CursorUtil.getColumnIndexOrThrow(_cursor, "appName");
          final int _cursorIndexOfAddedTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "addedTimestamp");
          final List<ProtectedApp> _result = new ArrayList<ProtectedApp>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProtectedApp _item;
            final String _tmpPackageName;
            _tmpPackageName = _cursor.getString(_cursorIndexOfPackageName);
            final String _tmpAppName;
            _tmpAppName = _cursor.getString(_cursorIndexOfAppName);
            final long _tmpAddedTimestamp;
            _tmpAddedTimestamp = _cursor.getLong(_cursorIndexOfAddedTimestamp);
            _item = new ProtectedApp(_tmpPackageName,_tmpAppName,_tmpAddedTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object isAppProtected(final String packageName,
      final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM protected_apps WHERE packageName = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, packageName);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Boolean>() {
      @Override
      @NonNull
      public Boolean call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Boolean _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp != 0;
          } else {
            _result = false;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
