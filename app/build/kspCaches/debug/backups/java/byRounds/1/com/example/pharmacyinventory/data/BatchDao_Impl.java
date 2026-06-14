package com.example.pharmacyinventory.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BatchDao_Impl implements BatchDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BatchEntity> __insertionAdapterOfBatchEntity;

  private final EntityDeletionOrUpdateAdapter<BatchEntity> __updateAdapterOfBatchEntity;

  private final SharedSQLiteStatement __preparedStmtOfDecrementQuantity;

  private final SharedSQLiteStatement __preparedStmtOfIncrementQuantity;

  public BatchDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBatchEntity = new EntityInsertionAdapter<BatchEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `batches` (`id`,`medicineId`,`supplierId`,`batchNo`,`expiryEpochDay`,`quantity`,`purchasePrice`,`mrp`,`createdAtMillis`,`updatedAtMillis`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BatchEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getMedicineId());
        if (entity.getSupplierId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getSupplierId());
        }
        statement.bindString(4, entity.getBatchNo());
        statement.bindLong(5, entity.getExpiryEpochDay());
        statement.bindLong(6, entity.getQuantity());
        statement.bindDouble(7, entity.getPurchasePrice());
        statement.bindDouble(8, entity.getMrp());
        statement.bindLong(9, entity.getCreatedAtMillis());
        statement.bindLong(10, entity.getUpdatedAtMillis());
      }
    };
    this.__updateAdapterOfBatchEntity = new EntityDeletionOrUpdateAdapter<BatchEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `batches` SET `id` = ?,`medicineId` = ?,`supplierId` = ?,`batchNo` = ?,`expiryEpochDay` = ?,`quantity` = ?,`purchasePrice` = ?,`mrp` = ?,`createdAtMillis` = ?,`updatedAtMillis` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BatchEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getMedicineId());
        if (entity.getSupplierId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getSupplierId());
        }
        statement.bindString(4, entity.getBatchNo());
        statement.bindLong(5, entity.getExpiryEpochDay());
        statement.bindLong(6, entity.getQuantity());
        statement.bindDouble(7, entity.getPurchasePrice());
        statement.bindDouble(8, entity.getMrp());
        statement.bindLong(9, entity.getCreatedAtMillis());
        statement.bindLong(10, entity.getUpdatedAtMillis());
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfDecrementQuantity = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE batches\n"
                + "        SET quantity = quantity - ?,\n"
                + "            updatedAtMillis = ?\n"
                + "        WHERE id = ?\n"
                + "          AND quantity >= ?\n"
                + "        ";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementQuantity = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE batches\n"
                + "        SET quantity = quantity + ?,\n"
                + "            updatedAtMillis = ?\n"
                + "        WHERE id = ?\n"
                + "        ";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final BatchEntity batch, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfBatchEntity.insertAndReturnId(batch);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final BatchEntity batch, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfBatchEntity.handle(batch);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object decrementQuantity(final long batchId, final int quantity,
      final long updatedAtMillis, final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDecrementQuantity.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, quantity);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, updatedAtMillis);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, batchId);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, quantity);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDecrementQuantity.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementQuantity(final long batchId, final int quantity,
      final long updatedAtMillis, final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementQuantity.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, quantity);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, updatedAtMillis);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, batchId);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfIncrementQuantity.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final long batchId, final Continuation<? super BatchEntity> $completion) {
    final String _sql = "SELECT * FROM batches WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, batchId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BatchEntity>() {
      @Override
      @Nullable
      public BatchEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMedicineId = CursorUtil.getColumnIndexOrThrow(_cursor, "medicineId");
          final int _cursorIndexOfSupplierId = CursorUtil.getColumnIndexOrThrow(_cursor, "supplierId");
          final int _cursorIndexOfBatchNo = CursorUtil.getColumnIndexOrThrow(_cursor, "batchNo");
          final int _cursorIndexOfExpiryEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryEpochDay");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfPurchasePrice = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasePrice");
          final int _cursorIndexOfMrp = CursorUtil.getColumnIndexOrThrow(_cursor, "mrp");
          final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
          final int _cursorIndexOfUpdatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtMillis");
          final BatchEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMedicineId;
            _tmpMedicineId = _cursor.getLong(_cursorIndexOfMedicineId);
            final Long _tmpSupplierId;
            if (_cursor.isNull(_cursorIndexOfSupplierId)) {
              _tmpSupplierId = null;
            } else {
              _tmpSupplierId = _cursor.getLong(_cursorIndexOfSupplierId);
            }
            final String _tmpBatchNo;
            _tmpBatchNo = _cursor.getString(_cursorIndexOfBatchNo);
            final long _tmpExpiryEpochDay;
            _tmpExpiryEpochDay = _cursor.getLong(_cursorIndexOfExpiryEpochDay);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final double _tmpPurchasePrice;
            _tmpPurchasePrice = _cursor.getDouble(_cursorIndexOfPurchasePrice);
            final double _tmpMrp;
            _tmpMrp = _cursor.getDouble(_cursorIndexOfMrp);
            final long _tmpCreatedAtMillis;
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
            final long _tmpUpdatedAtMillis;
            _tmpUpdatedAtMillis = _cursor.getLong(_cursorIndexOfUpdatedAtMillis);
            _result = new BatchEntity(_tmpId,_tmpMedicineId,_tmpSupplierId,_tmpBatchNo,_tmpExpiryEpochDay,_tmpQuantity,_tmpPurchasePrice,_tmpMrp,_tmpCreatedAtMillis,_tmpUpdatedAtMillis);
          } else {
            _result = null;
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
  public Object getAllNow(final Continuation<? super List<BatchEntity>> $completion) {
    final String _sql = "SELECT * FROM batches ORDER BY expiryEpochDay ASC, id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BatchEntity>>() {
      @Override
      @NonNull
      public List<BatchEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMedicineId = CursorUtil.getColumnIndexOrThrow(_cursor, "medicineId");
          final int _cursorIndexOfSupplierId = CursorUtil.getColumnIndexOrThrow(_cursor, "supplierId");
          final int _cursorIndexOfBatchNo = CursorUtil.getColumnIndexOrThrow(_cursor, "batchNo");
          final int _cursorIndexOfExpiryEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryEpochDay");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfPurchasePrice = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasePrice");
          final int _cursorIndexOfMrp = CursorUtil.getColumnIndexOrThrow(_cursor, "mrp");
          final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
          final int _cursorIndexOfUpdatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtMillis");
          final List<BatchEntity> _result = new ArrayList<BatchEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BatchEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMedicineId;
            _tmpMedicineId = _cursor.getLong(_cursorIndexOfMedicineId);
            final Long _tmpSupplierId;
            if (_cursor.isNull(_cursorIndexOfSupplierId)) {
              _tmpSupplierId = null;
            } else {
              _tmpSupplierId = _cursor.getLong(_cursorIndexOfSupplierId);
            }
            final String _tmpBatchNo;
            _tmpBatchNo = _cursor.getString(_cursorIndexOfBatchNo);
            final long _tmpExpiryEpochDay;
            _tmpExpiryEpochDay = _cursor.getLong(_cursorIndexOfExpiryEpochDay);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final double _tmpPurchasePrice;
            _tmpPurchasePrice = _cursor.getDouble(_cursorIndexOfPurchasePrice);
            final double _tmpMrp;
            _tmpMrp = _cursor.getDouble(_cursorIndexOfMrp);
            final long _tmpCreatedAtMillis;
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
            final long _tmpUpdatedAtMillis;
            _tmpUpdatedAtMillis = _cursor.getLong(_cursorIndexOfUpdatedAtMillis);
            _item = new BatchEntity(_tmpId,_tmpMedicineId,_tmpSupplierId,_tmpBatchNo,_tmpExpiryEpochDay,_tmpQuantity,_tmpPurchasePrice,_tmpMrp,_tmpCreatedAtMillis,_tmpUpdatedAtMillis);
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
  public Object getByNaturalKey(final long medicineId, final String batchNo,
      final long expiryEpochDay, final Continuation<? super BatchEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM batches\n"
            + "        WHERE medicineId = ?\n"
            + "          AND batchNo = ?\n"
            + "          AND expiryEpochDay = ?\n"
            + "        LIMIT 1\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, medicineId);
    _argIndex = 2;
    _statement.bindString(_argIndex, batchNo);
    _argIndex = 3;
    _statement.bindLong(_argIndex, expiryEpochDay);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BatchEntity>() {
      @Override
      @Nullable
      public BatchEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMedicineId = CursorUtil.getColumnIndexOrThrow(_cursor, "medicineId");
          final int _cursorIndexOfSupplierId = CursorUtil.getColumnIndexOrThrow(_cursor, "supplierId");
          final int _cursorIndexOfBatchNo = CursorUtil.getColumnIndexOrThrow(_cursor, "batchNo");
          final int _cursorIndexOfExpiryEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryEpochDay");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfPurchasePrice = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasePrice");
          final int _cursorIndexOfMrp = CursorUtil.getColumnIndexOrThrow(_cursor, "mrp");
          final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
          final int _cursorIndexOfUpdatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtMillis");
          final BatchEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMedicineId;
            _tmpMedicineId = _cursor.getLong(_cursorIndexOfMedicineId);
            final Long _tmpSupplierId;
            if (_cursor.isNull(_cursorIndexOfSupplierId)) {
              _tmpSupplierId = null;
            } else {
              _tmpSupplierId = _cursor.getLong(_cursorIndexOfSupplierId);
            }
            final String _tmpBatchNo;
            _tmpBatchNo = _cursor.getString(_cursorIndexOfBatchNo);
            final long _tmpExpiryEpochDay;
            _tmpExpiryEpochDay = _cursor.getLong(_cursorIndexOfExpiryEpochDay);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final double _tmpPurchasePrice;
            _tmpPurchasePrice = _cursor.getDouble(_cursorIndexOfPurchasePrice);
            final double _tmpMrp;
            _tmpMrp = _cursor.getDouble(_cursorIndexOfMrp);
            final long _tmpCreatedAtMillis;
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
            final long _tmpUpdatedAtMillis;
            _tmpUpdatedAtMillis = _cursor.getLong(_cursorIndexOfUpdatedAtMillis);
            _result = new BatchEntity(_tmpId,_tmpMedicineId,_tmpSupplierId,_tmpBatchNo,_tmpExpiryEpochDay,_tmpQuantity,_tmpPurchasePrice,_tmpMrp,_tmpCreatedAtMillis,_tmpUpdatedAtMillis);
          } else {
            _result = null;
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
  public Object getFefoBatchesForSale(final long medicineId, final long todayEpochDay,
      final boolean includeExpired, final Continuation<? super List<BatchEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM batches\n"
            + "        WHERE medicineId = ?\n"
            + "          AND quantity > 0\n"
            + "          AND (? = 1 OR expiryEpochDay >= ?)\n"
            + "        ORDER BY expiryEpochDay ASC, id ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, medicineId);
    _argIndex = 2;
    final int _tmp = includeExpired ? 1 : 0;
    _statement.bindLong(_argIndex, _tmp);
    _argIndex = 3;
    _statement.bindLong(_argIndex, todayEpochDay);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<BatchEntity>>() {
      @Override
      @NonNull
      public List<BatchEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMedicineId = CursorUtil.getColumnIndexOrThrow(_cursor, "medicineId");
          final int _cursorIndexOfSupplierId = CursorUtil.getColumnIndexOrThrow(_cursor, "supplierId");
          final int _cursorIndexOfBatchNo = CursorUtil.getColumnIndexOrThrow(_cursor, "batchNo");
          final int _cursorIndexOfExpiryEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryEpochDay");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfPurchasePrice = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasePrice");
          final int _cursorIndexOfMrp = CursorUtil.getColumnIndexOrThrow(_cursor, "mrp");
          final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
          final int _cursorIndexOfUpdatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtMillis");
          final List<BatchEntity> _result = new ArrayList<BatchEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BatchEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMedicineId;
            _tmpMedicineId = _cursor.getLong(_cursorIndexOfMedicineId);
            final Long _tmpSupplierId;
            if (_cursor.isNull(_cursorIndexOfSupplierId)) {
              _tmpSupplierId = null;
            } else {
              _tmpSupplierId = _cursor.getLong(_cursorIndexOfSupplierId);
            }
            final String _tmpBatchNo;
            _tmpBatchNo = _cursor.getString(_cursorIndexOfBatchNo);
            final long _tmpExpiryEpochDay;
            _tmpExpiryEpochDay = _cursor.getLong(_cursorIndexOfExpiryEpochDay);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final double _tmpPurchasePrice;
            _tmpPurchasePrice = _cursor.getDouble(_cursorIndexOfPurchasePrice);
            final double _tmpMrp;
            _tmpMrp = _cursor.getDouble(_cursorIndexOfMrp);
            final long _tmpCreatedAtMillis;
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
            final long _tmpUpdatedAtMillis;
            _tmpUpdatedAtMillis = _cursor.getLong(_cursorIndexOfUpdatedAtMillis);
            _item = new BatchEntity(_tmpId,_tmpMedicineId,_tmpSupplierId,_tmpBatchNo,_tmpExpiryEpochDay,_tmpQuantity,_tmpPurchasePrice,_tmpMrp,_tmpCreatedAtMillis,_tmpUpdatedAtMillis);
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
  public Flow<List<StockRow>> observeStockRows() {
    final String _sql = "\n"
            + "        SELECT\n"
            + "            m.id AS medicineId,\n"
            + "            b.id AS batchId,\n"
            + "            m.name AS name,\n"
            + "            m.company AS company,\n"
            + "            m.genericName AS genericName,\n"
            + "            m.category AS category,\n"
            + "            s.name AS supplierName,\n"
            + "            m.minStock AS minStock,\n"
            + "            b.batchNo AS batchNo,\n"
            + "            b.expiryEpochDay AS expiryEpochDay,\n"
            + "            b.quantity AS quantity,\n"
            + "            b.purchasePrice AS purchasePrice,\n"
            + "            b.mrp AS mrp\n"
            + "        FROM batches b\n"
            + "        INNER JOIN medicines m ON m.id = b.medicineId\n"
            + "        LEFT JOIN suppliers s ON s.id = b.supplierId\n"
            + "        ORDER BY m.name COLLATE NOCASE, b.expiryEpochDay ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"batches", "medicines",
        "suppliers"}, new Callable<List<StockRow>>() {
      @Override
      @NonNull
      public List<StockRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMedicineId = 0;
          final int _cursorIndexOfBatchId = 1;
          final int _cursorIndexOfName = 2;
          final int _cursorIndexOfCompany = 3;
          final int _cursorIndexOfGenericName = 4;
          final int _cursorIndexOfCategory = 5;
          final int _cursorIndexOfSupplierName = 6;
          final int _cursorIndexOfMinStock = 7;
          final int _cursorIndexOfBatchNo = 8;
          final int _cursorIndexOfExpiryEpochDay = 9;
          final int _cursorIndexOfQuantity = 10;
          final int _cursorIndexOfPurchasePrice = 11;
          final int _cursorIndexOfMrp = 12;
          final List<StockRow> _result = new ArrayList<StockRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StockRow _item;
            final long _tmpMedicineId;
            _tmpMedicineId = _cursor.getLong(_cursorIndexOfMedicineId);
            final long _tmpBatchId;
            _tmpBatchId = _cursor.getLong(_cursorIndexOfBatchId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCompany;
            _tmpCompany = _cursor.getString(_cursorIndexOfCompany);
            final String _tmpGenericName;
            _tmpGenericName = _cursor.getString(_cursorIndexOfGenericName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpSupplierName;
            if (_cursor.isNull(_cursorIndexOfSupplierName)) {
              _tmpSupplierName = null;
            } else {
              _tmpSupplierName = _cursor.getString(_cursorIndexOfSupplierName);
            }
            final int _tmpMinStock;
            _tmpMinStock = _cursor.getInt(_cursorIndexOfMinStock);
            final String _tmpBatchNo;
            _tmpBatchNo = _cursor.getString(_cursorIndexOfBatchNo);
            final long _tmpExpiryEpochDay;
            _tmpExpiryEpochDay = _cursor.getLong(_cursorIndexOfExpiryEpochDay);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final double _tmpPurchasePrice;
            _tmpPurchasePrice = _cursor.getDouble(_cursorIndexOfPurchasePrice);
            final double _tmpMrp;
            _tmpMrp = _cursor.getDouble(_cursorIndexOfMrp);
            _item = new StockRow(_tmpMedicineId,_tmpBatchId,_tmpName,_tmpCompany,_tmpGenericName,_tmpCategory,_tmpSupplierName,_tmpMinStock,_tmpBatchNo,_tmpExpiryEpochDay,_tmpQuantity,_tmpPurchasePrice,_tmpMrp);
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
  public Flow<List<StockRow>> observeAvailableStockRows(final String query) {
    final String _sql = "\n"
            + "        SELECT\n"
            + "            m.id AS medicineId,\n"
            + "            b.id AS batchId,\n"
            + "            m.name AS name,\n"
            + "            m.company AS company,\n"
            + "            m.genericName AS genericName,\n"
            + "            m.category AS category,\n"
            + "            s.name AS supplierName,\n"
            + "            m.minStock AS minStock,\n"
            + "            b.batchNo AS batchNo,\n"
            + "            b.expiryEpochDay AS expiryEpochDay,\n"
            + "            b.quantity AS quantity,\n"
            + "            b.purchasePrice AS purchasePrice,\n"
            + "            b.mrp AS mrp\n"
            + "        FROM batches b\n"
            + "        INNER JOIN medicines m ON m.id = b.medicineId\n"
            + "        LEFT JOIN suppliers s ON s.id = b.supplierId\n"
            + "        WHERE b.quantity > 0\n"
            + "          AND (? = ''\n"
            + "              OR m.name LIKE '%' || ? || '%'\n"
            + "              OR m.company LIKE '%' || ? || '%'\n"
            + "              OR b.batchNo LIKE '%' || ? || '%')\n"
            + "        ORDER BY m.name COLLATE NOCASE, b.expiryEpochDay ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    _argIndex = 3;
    _statement.bindString(_argIndex, query);
    _argIndex = 4;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"batches", "medicines",
        "suppliers"}, new Callable<List<StockRow>>() {
      @Override
      @NonNull
      public List<StockRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMedicineId = 0;
          final int _cursorIndexOfBatchId = 1;
          final int _cursorIndexOfName = 2;
          final int _cursorIndexOfCompany = 3;
          final int _cursorIndexOfGenericName = 4;
          final int _cursorIndexOfCategory = 5;
          final int _cursorIndexOfSupplierName = 6;
          final int _cursorIndexOfMinStock = 7;
          final int _cursorIndexOfBatchNo = 8;
          final int _cursorIndexOfExpiryEpochDay = 9;
          final int _cursorIndexOfQuantity = 10;
          final int _cursorIndexOfPurchasePrice = 11;
          final int _cursorIndexOfMrp = 12;
          final List<StockRow> _result = new ArrayList<StockRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StockRow _item;
            final long _tmpMedicineId;
            _tmpMedicineId = _cursor.getLong(_cursorIndexOfMedicineId);
            final long _tmpBatchId;
            _tmpBatchId = _cursor.getLong(_cursorIndexOfBatchId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCompany;
            _tmpCompany = _cursor.getString(_cursorIndexOfCompany);
            final String _tmpGenericName;
            _tmpGenericName = _cursor.getString(_cursorIndexOfGenericName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpSupplierName;
            if (_cursor.isNull(_cursorIndexOfSupplierName)) {
              _tmpSupplierName = null;
            } else {
              _tmpSupplierName = _cursor.getString(_cursorIndexOfSupplierName);
            }
            final int _tmpMinStock;
            _tmpMinStock = _cursor.getInt(_cursorIndexOfMinStock);
            final String _tmpBatchNo;
            _tmpBatchNo = _cursor.getString(_cursorIndexOfBatchNo);
            final long _tmpExpiryEpochDay;
            _tmpExpiryEpochDay = _cursor.getLong(_cursorIndexOfExpiryEpochDay);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            final double _tmpPurchasePrice;
            _tmpPurchasePrice = _cursor.getDouble(_cursorIndexOfPurchasePrice);
            final double _tmpMrp;
            _tmpMrp = _cursor.getDouble(_cursorIndexOfMrp);
            _item = new StockRow(_tmpMedicineId,_tmpBatchId,_tmpName,_tmpCompany,_tmpGenericName,_tmpCategory,_tmpSupplierName,_tmpMinStock,_tmpBatchNo,_tmpExpiryEpochDay,_tmpQuantity,_tmpPurchasePrice,_tmpMrp);
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
