package com.example.pharmacyinventory.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class PurchaseDao_Impl implements PurchaseDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PurchaseEntity> __insertionAdapterOfPurchaseEntity;

  private final EntityInsertionAdapter<PurchaseItemEntity> __insertionAdapterOfPurchaseItemEntity;

  public PurchaseDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPurchaseEntity = new EntityInsertionAdapter<PurchaseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `purchases` (`id`,`voucherNo`,`supplierId`,`supplierName`,`purchasedAtMillis`,`total`,`note`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PurchaseEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getVoucherNo());
        if (entity.getSupplierId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getSupplierId());
        }
        statement.bindString(4, entity.getSupplierName());
        statement.bindLong(5, entity.getPurchasedAtMillis());
        statement.bindDouble(6, entity.getTotal());
        statement.bindString(7, entity.getNote());
      }
    };
    this.__insertionAdapterOfPurchaseItemEntity = new EntityInsertionAdapter<PurchaseItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `purchase_items` (`id`,`purchaseId`,`medicineId`,`batchId`,`medicineName`,`batchNo`,`expiryEpochDay`,`quantity`,`purchasePrice`,`mrp`,`lineTotal`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PurchaseItemEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getPurchaseId());
        statement.bindLong(3, entity.getMedicineId());
        statement.bindLong(4, entity.getBatchId());
        statement.bindString(5, entity.getMedicineName());
        statement.bindString(6, entity.getBatchNo());
        statement.bindLong(7, entity.getExpiryEpochDay());
        statement.bindLong(8, entity.getQuantity());
        statement.bindDouble(9, entity.getPurchasePrice());
        statement.bindDouble(10, entity.getMrp());
        statement.bindDouble(11, entity.getLineTotal());
      }
    };
  }

  @Override
  public Object insertPurchase(final PurchaseEntity purchase,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPurchaseEntity.insertAndReturnId(purchase);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertItems(final List<PurchaseItemEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPurchaseItemEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllPurchasesNow(final Continuation<? super List<PurchaseEntity>> $completion) {
    final String _sql = "SELECT * FROM purchases ORDER BY purchasedAtMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PurchaseEntity>>() {
      @Override
      @NonNull
      public List<PurchaseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVoucherNo = CursorUtil.getColumnIndexOrThrow(_cursor, "voucherNo");
          final int _cursorIndexOfSupplierId = CursorUtil.getColumnIndexOrThrow(_cursor, "supplierId");
          final int _cursorIndexOfSupplierName = CursorUtil.getColumnIndexOrThrow(_cursor, "supplierName");
          final int _cursorIndexOfPurchasedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasedAtMillis");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<PurchaseEntity> _result = new ArrayList<PurchaseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PurchaseEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpVoucherNo;
            _tmpVoucherNo = _cursor.getString(_cursorIndexOfVoucherNo);
            final Long _tmpSupplierId;
            if (_cursor.isNull(_cursorIndexOfSupplierId)) {
              _tmpSupplierId = null;
            } else {
              _tmpSupplierId = _cursor.getLong(_cursorIndexOfSupplierId);
            }
            final String _tmpSupplierName;
            _tmpSupplierName = _cursor.getString(_cursorIndexOfSupplierName);
            final long _tmpPurchasedAtMillis;
            _tmpPurchasedAtMillis = _cursor.getLong(_cursorIndexOfPurchasedAtMillis);
            final double _tmpTotal;
            _tmpTotal = _cursor.getDouble(_cursorIndexOfTotal);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _item = new PurchaseEntity(_tmpId,_tmpVoucherNo,_tmpSupplierId,_tmpSupplierName,_tmpPurchasedAtMillis,_tmpTotal,_tmpNote);
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
  public Object getAllItemsNow(final Continuation<? super List<PurchaseItemEntity>> $completion) {
    final String _sql = "SELECT * FROM purchase_items ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PurchaseItemEntity>>() {
      @Override
      @NonNull
      public List<PurchaseItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPurchaseId = CursorUtil.getColumnIndexOrThrow(_cursor, "purchaseId");
          final int _cursorIndexOfMedicineId = CursorUtil.getColumnIndexOrThrow(_cursor, "medicineId");
          final int _cursorIndexOfBatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "batchId");
          final int _cursorIndexOfMedicineName = CursorUtil.getColumnIndexOrThrow(_cursor, "medicineName");
          final int _cursorIndexOfBatchNo = CursorUtil.getColumnIndexOrThrow(_cursor, "batchNo");
          final int _cursorIndexOfExpiryEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryEpochDay");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfPurchasePrice = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasePrice");
          final int _cursorIndexOfMrp = CursorUtil.getColumnIndexOrThrow(_cursor, "mrp");
          final int _cursorIndexOfLineTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "lineTotal");
          final List<PurchaseItemEntity> _result = new ArrayList<PurchaseItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PurchaseItemEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPurchaseId;
            _tmpPurchaseId = _cursor.getLong(_cursorIndexOfPurchaseId);
            final long _tmpMedicineId;
            _tmpMedicineId = _cursor.getLong(_cursorIndexOfMedicineId);
            final long _tmpBatchId;
            _tmpBatchId = _cursor.getLong(_cursorIndexOfBatchId);
            final String _tmpMedicineName;
            _tmpMedicineName = _cursor.getString(_cursorIndexOfMedicineName);
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
            final double _tmpLineTotal;
            _tmpLineTotal = _cursor.getDouble(_cursorIndexOfLineTotal);
            _item = new PurchaseItemEntity(_tmpId,_tmpPurchaseId,_tmpMedicineId,_tmpBatchId,_tmpMedicineName,_tmpBatchNo,_tmpExpiryEpochDay,_tmpQuantity,_tmpPurchasePrice,_tmpMrp,_tmpLineTotal);
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
  public Object getByVoucherNo(final String voucherNo,
      final Continuation<? super PurchaseEntity> $completion) {
    final String _sql = "SELECT * FROM purchases WHERE voucherNo = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, voucherNo);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PurchaseEntity>() {
      @Override
      @Nullable
      public PurchaseEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVoucherNo = CursorUtil.getColumnIndexOrThrow(_cursor, "voucherNo");
          final int _cursorIndexOfSupplierId = CursorUtil.getColumnIndexOrThrow(_cursor, "supplierId");
          final int _cursorIndexOfSupplierName = CursorUtil.getColumnIndexOrThrow(_cursor, "supplierName");
          final int _cursorIndexOfPurchasedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasedAtMillis");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final PurchaseEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpVoucherNo;
            _tmpVoucherNo = _cursor.getString(_cursorIndexOfVoucherNo);
            final Long _tmpSupplierId;
            if (_cursor.isNull(_cursorIndexOfSupplierId)) {
              _tmpSupplierId = null;
            } else {
              _tmpSupplierId = _cursor.getLong(_cursorIndexOfSupplierId);
            }
            final String _tmpSupplierName;
            _tmpSupplierName = _cursor.getString(_cursorIndexOfSupplierName);
            final long _tmpPurchasedAtMillis;
            _tmpPurchasedAtMillis = _cursor.getLong(_cursorIndexOfPurchasedAtMillis);
            final double _tmpTotal;
            _tmpTotal = _cursor.getDouble(_cursorIndexOfTotal);
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _result = new PurchaseEntity(_tmpId,_tmpVoucherNo,_tmpSupplierId,_tmpSupplierName,_tmpPurchasedAtMillis,_tmpTotal,_tmpNote);
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
  public Flow<PurchaseWithItems> observePurchaseWithItems(final long purchaseId) {
    final String _sql = "SELECT * FROM purchases WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, purchaseId);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"purchase_items",
        "purchases"}, new Callable<PurchaseWithItems>() {
      @Override
      @Nullable
      public PurchaseWithItems call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfVoucherNo = CursorUtil.getColumnIndexOrThrow(_cursor, "voucherNo");
            final int _cursorIndexOfSupplierId = CursorUtil.getColumnIndexOrThrow(_cursor, "supplierId");
            final int _cursorIndexOfSupplierName = CursorUtil.getColumnIndexOrThrow(_cursor, "supplierName");
            final int _cursorIndexOfPurchasedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasedAtMillis");
            final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
            final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
            final LongSparseArray<ArrayList<PurchaseItemEntity>> _collectionItems = new LongSparseArray<ArrayList<PurchaseItemEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionItems.containsKey(_tmpKey)) {
                _collectionItems.put(_tmpKey, new ArrayList<PurchaseItemEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshippurchaseItemsAscomExamplePharmacyinventoryDataPurchaseItemEntity(_collectionItems);
            final PurchaseWithItems _result;
            if (_cursor.moveToFirst()) {
              final PurchaseEntity _tmpPurchase;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpVoucherNo;
              _tmpVoucherNo = _cursor.getString(_cursorIndexOfVoucherNo);
              final Long _tmpSupplierId;
              if (_cursor.isNull(_cursorIndexOfSupplierId)) {
                _tmpSupplierId = null;
              } else {
                _tmpSupplierId = _cursor.getLong(_cursorIndexOfSupplierId);
              }
              final String _tmpSupplierName;
              _tmpSupplierName = _cursor.getString(_cursorIndexOfSupplierName);
              final long _tmpPurchasedAtMillis;
              _tmpPurchasedAtMillis = _cursor.getLong(_cursorIndexOfPurchasedAtMillis);
              final double _tmpTotal;
              _tmpTotal = _cursor.getDouble(_cursorIndexOfTotal);
              final String _tmpNote;
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
              _tmpPurchase = new PurchaseEntity(_tmpId,_tmpVoucherNo,_tmpSupplierId,_tmpSupplierName,_tmpPurchasedAtMillis,_tmpTotal,_tmpNote);
              final ArrayList<PurchaseItemEntity> _tmpItemsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpItemsCollection = _collectionItems.get(_tmpKey_1);
              _result = new PurchaseWithItems(_tmpPurchase,_tmpItemsCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<PurchaseReportRow>> observeReport(final long startMillis, final long endMillis) {
    final String _sql = "\n"
            + "        SELECT\n"
            + "            p.id AS purchaseId,\n"
            + "            p.voucherNo AS voucherNo,\n"
            + "            p.purchasedAtMillis AS purchasedAtMillis,\n"
            + "            p.supplierName AS supplierName,\n"
            + "            p.total AS total,\n"
            + "            COALESCE(SUM(pi.quantity), 0) AS itemCount\n"
            + "        FROM purchases p\n"
            + "        LEFT JOIN purchase_items pi ON pi.purchaseId = p.id\n"
            + "        WHERE p.purchasedAtMillis BETWEEN ? AND ?\n"
            + "        GROUP BY p.id\n"
            + "        ORDER BY p.purchasedAtMillis DESC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startMillis);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endMillis);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"purchases",
        "purchase_items"}, new Callable<List<PurchaseReportRow>>() {
      @Override
      @NonNull
      public List<PurchaseReportRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPurchaseId = 0;
          final int _cursorIndexOfVoucherNo = 1;
          final int _cursorIndexOfPurchasedAtMillis = 2;
          final int _cursorIndexOfSupplierName = 3;
          final int _cursorIndexOfTotal = 4;
          final int _cursorIndexOfItemCount = 5;
          final List<PurchaseReportRow> _result = new ArrayList<PurchaseReportRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PurchaseReportRow _item;
            final long _tmpPurchaseId;
            _tmpPurchaseId = _cursor.getLong(_cursorIndexOfPurchaseId);
            final String _tmpVoucherNo;
            _tmpVoucherNo = _cursor.getString(_cursorIndexOfVoucherNo);
            final long _tmpPurchasedAtMillis;
            _tmpPurchasedAtMillis = _cursor.getLong(_cursorIndexOfPurchasedAtMillis);
            final String _tmpSupplierName;
            _tmpSupplierName = _cursor.getString(_cursorIndexOfSupplierName);
            final double _tmpTotal;
            _tmpTotal = _cursor.getDouble(_cursorIndexOfTotal);
            final int _tmpItemCount;
            _tmpItemCount = _cursor.getInt(_cursorIndexOfItemCount);
            _item = new PurchaseReportRow(_tmpPurchaseId,_tmpVoucherNo,_tmpPurchasedAtMillis,_tmpSupplierName,_tmpTotal,_tmpItemCount);
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

  private void __fetchRelationshippurchaseItemsAscomExamplePharmacyinventoryDataPurchaseItemEntity(
      @NonNull final LongSparseArray<ArrayList<PurchaseItemEntity>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshippurchaseItemsAscomExamplePharmacyinventoryDataPurchaseItemEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`purchaseId`,`medicineId`,`batchId`,`medicineName`,`batchNo`,`expiryEpochDay`,`quantity`,`purchasePrice`,`mrp`,`lineTotal` FROM `purchase_items` WHERE `purchaseId` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "purchaseId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfPurchaseId = 1;
      final int _cursorIndexOfMedicineId = 2;
      final int _cursorIndexOfBatchId = 3;
      final int _cursorIndexOfMedicineName = 4;
      final int _cursorIndexOfBatchNo = 5;
      final int _cursorIndexOfExpiryEpochDay = 6;
      final int _cursorIndexOfQuantity = 7;
      final int _cursorIndexOfPurchasePrice = 8;
      final int _cursorIndexOfMrp = 9;
      final int _cursorIndexOfLineTotal = 10;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<PurchaseItemEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final PurchaseItemEntity _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final long _tmpPurchaseId;
          _tmpPurchaseId = _cursor.getLong(_cursorIndexOfPurchaseId);
          final long _tmpMedicineId;
          _tmpMedicineId = _cursor.getLong(_cursorIndexOfMedicineId);
          final long _tmpBatchId;
          _tmpBatchId = _cursor.getLong(_cursorIndexOfBatchId);
          final String _tmpMedicineName;
          _tmpMedicineName = _cursor.getString(_cursorIndexOfMedicineName);
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
          final double _tmpLineTotal;
          _tmpLineTotal = _cursor.getDouble(_cursorIndexOfLineTotal);
          _item_1 = new PurchaseItemEntity(_tmpId,_tmpPurchaseId,_tmpMedicineId,_tmpBatchId,_tmpMedicineName,_tmpBatchNo,_tmpExpiryEpochDay,_tmpQuantity,_tmpPurchasePrice,_tmpMrp,_tmpLineTotal);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
