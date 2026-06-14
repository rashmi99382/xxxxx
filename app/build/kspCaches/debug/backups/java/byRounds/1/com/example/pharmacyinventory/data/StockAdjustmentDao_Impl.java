package com.example.pharmacyinventory.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class StockAdjustmentDao_Impl implements StockAdjustmentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<StockAdjustmentEntity> __insertionAdapterOfStockAdjustmentEntity;

  public StockAdjustmentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfStockAdjustmentEntity = new EntityInsertionAdapter<StockAdjustmentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `stock_adjustments` (`id`,`medicineId`,`batchId`,`quantityDelta`,`reason`,`referenceType`,`referenceId`,`createdAtMillis`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StockAdjustmentEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getMedicineId());
        statement.bindLong(3, entity.getBatchId());
        statement.bindLong(4, entity.getQuantityDelta());
        statement.bindString(5, entity.getReason());
        statement.bindString(6, entity.getReferenceType());
        if (entity.getReferenceId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getReferenceId());
        }
        statement.bindLong(8, entity.getCreatedAtMillis());
      }
    };
  }

  @Override
  public Object insert(final StockAdjustmentEntity adjustment,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfStockAdjustmentEntity.insertAndReturnId(adjustment);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllNow(final Continuation<? super List<StockAdjustmentEntity>> $completion) {
    final String _sql = "SELECT * FROM stock_adjustments ORDER BY createdAtMillis ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<StockAdjustmentEntity>>() {
      @Override
      @NonNull
      public List<StockAdjustmentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMedicineId = CursorUtil.getColumnIndexOrThrow(_cursor, "medicineId");
          final int _cursorIndexOfBatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "batchId");
          final int _cursorIndexOfQuantityDelta = CursorUtil.getColumnIndexOrThrow(_cursor, "quantityDelta");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfReferenceType = CursorUtil.getColumnIndexOrThrow(_cursor, "referenceType");
          final int _cursorIndexOfReferenceId = CursorUtil.getColumnIndexOrThrow(_cursor, "referenceId");
          final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
          final List<StockAdjustmentEntity> _result = new ArrayList<StockAdjustmentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StockAdjustmentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMedicineId;
            _tmpMedicineId = _cursor.getLong(_cursorIndexOfMedicineId);
            final long _tmpBatchId;
            _tmpBatchId = _cursor.getLong(_cursorIndexOfBatchId);
            final int _tmpQuantityDelta;
            _tmpQuantityDelta = _cursor.getInt(_cursorIndexOfQuantityDelta);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpReferenceType;
            _tmpReferenceType = _cursor.getString(_cursorIndexOfReferenceType);
            final Long _tmpReferenceId;
            if (_cursor.isNull(_cursorIndexOfReferenceId)) {
              _tmpReferenceId = null;
            } else {
              _tmpReferenceId = _cursor.getLong(_cursorIndexOfReferenceId);
            }
            final long _tmpCreatedAtMillis;
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
            _item = new StockAdjustmentEntity(_tmpId,_tmpMedicineId,_tmpBatchId,_tmpQuantityDelta,_tmpReason,_tmpReferenceType,_tmpReferenceId,_tmpCreatedAtMillis);
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
  public Flow<List<StockAdjustmentEntity>> observeForMedicine(final long medicineId) {
    final String _sql = "\n"
            + "        SELECT * FROM stock_adjustments\n"
            + "        WHERE medicineId = ?\n"
            + "        ORDER BY createdAtMillis DESC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, medicineId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"stock_adjustments"}, new Callable<List<StockAdjustmentEntity>>() {
      @Override
      @NonNull
      public List<StockAdjustmentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMedicineId = CursorUtil.getColumnIndexOrThrow(_cursor, "medicineId");
          final int _cursorIndexOfBatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "batchId");
          final int _cursorIndexOfQuantityDelta = CursorUtil.getColumnIndexOrThrow(_cursor, "quantityDelta");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfReferenceType = CursorUtil.getColumnIndexOrThrow(_cursor, "referenceType");
          final int _cursorIndexOfReferenceId = CursorUtil.getColumnIndexOrThrow(_cursor, "referenceId");
          final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
          final List<StockAdjustmentEntity> _result = new ArrayList<StockAdjustmentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StockAdjustmentEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMedicineId;
            _tmpMedicineId = _cursor.getLong(_cursorIndexOfMedicineId);
            final long _tmpBatchId;
            _tmpBatchId = _cursor.getLong(_cursorIndexOfBatchId);
            final int _tmpQuantityDelta;
            _tmpQuantityDelta = _cursor.getInt(_cursorIndexOfQuantityDelta);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpReferenceType;
            _tmpReferenceType = _cursor.getString(_cursorIndexOfReferenceType);
            final Long _tmpReferenceId;
            if (_cursor.isNull(_cursorIndexOfReferenceId)) {
              _tmpReferenceId = null;
            } else {
              _tmpReferenceId = _cursor.getLong(_cursorIndexOfReferenceId);
            }
            final long _tmpCreatedAtMillis;
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
            _item = new StockAdjustmentEntity(_tmpId,_tmpMedicineId,_tmpBatchId,_tmpQuantityDelta,_tmpReason,_tmpReferenceType,_tmpReferenceId,_tmpCreatedAtMillis);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
