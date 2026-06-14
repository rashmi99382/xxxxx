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
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
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
public final class SaleDao_Impl implements SaleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SaleEntity> __insertionAdapterOfSaleEntity;

  private final EntityInsertionAdapter<SaleItemEntity> __insertionAdapterOfSaleItemEntity;

  public SaleDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSaleEntity = new EntityInsertionAdapter<SaleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `sales` (`id`,`billNo`,`soldAtMillis`,`customerName`,`paymentMode`,`subtotal`,`total`,`profit`,`hadExpiredWarning`,`note`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SaleEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getBillNo());
        statement.bindLong(3, entity.getSoldAtMillis());
        statement.bindString(4, entity.getCustomerName());
        statement.bindString(5, entity.getPaymentMode());
        statement.bindDouble(6, entity.getSubtotal());
        statement.bindDouble(7, entity.getTotal());
        statement.bindDouble(8, entity.getProfit());
        final int _tmp = entity.getHadExpiredWarning() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindString(10, entity.getNote());
      }
    };
    this.__insertionAdapterOfSaleItemEntity = new EntityInsertionAdapter<SaleItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `sale_items` (`id`,`saleId`,`medicineId`,`batchId`,`medicineName`,`batchNo`,`expiryEpochDay`,`quantity`,`mrp`,`purchasePrice`,`lineTotal`,`lineProfit`,`wasExpired`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SaleItemEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getSaleId());
        statement.bindLong(3, entity.getMedicineId());
        statement.bindLong(4, entity.getBatchId());
        statement.bindString(5, entity.getMedicineName());
        statement.bindString(6, entity.getBatchNo());
        statement.bindLong(7, entity.getExpiryEpochDay());
        statement.bindLong(8, entity.getQuantity());
        statement.bindDouble(9, entity.getMrp());
        statement.bindDouble(10, entity.getPurchasePrice());
        statement.bindDouble(11, entity.getLineTotal());
        statement.bindDouble(12, entity.getLineProfit());
        final int _tmp = entity.getWasExpired() ? 1 : 0;
        statement.bindLong(13, _tmp);
      }
    };
  }

  @Override
  public Object insertSale(final SaleEntity sale, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSaleEntity.insertAndReturnId(sale);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertItems(final List<SaleItemEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSaleItemEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllSalesNow(final Continuation<? super List<SaleEntity>> $completion) {
    final String _sql = "SELECT * FROM sales ORDER BY soldAtMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SaleEntity>>() {
      @Override
      @NonNull
      public List<SaleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBillNo = CursorUtil.getColumnIndexOrThrow(_cursor, "billNo");
          final int _cursorIndexOfSoldAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "soldAtMillis");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfPaymentMode = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMode");
          final int _cursorIndexOfSubtotal = CursorUtil.getColumnIndexOrThrow(_cursor, "subtotal");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final int _cursorIndexOfProfit = CursorUtil.getColumnIndexOrThrow(_cursor, "profit");
          final int _cursorIndexOfHadExpiredWarning = CursorUtil.getColumnIndexOrThrow(_cursor, "hadExpiredWarning");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final List<SaleEntity> _result = new ArrayList<SaleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SaleEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpBillNo;
            _tmpBillNo = _cursor.getString(_cursorIndexOfBillNo);
            final long _tmpSoldAtMillis;
            _tmpSoldAtMillis = _cursor.getLong(_cursorIndexOfSoldAtMillis);
            final String _tmpCustomerName;
            _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            final String _tmpPaymentMode;
            _tmpPaymentMode = _cursor.getString(_cursorIndexOfPaymentMode);
            final double _tmpSubtotal;
            _tmpSubtotal = _cursor.getDouble(_cursorIndexOfSubtotal);
            final double _tmpTotal;
            _tmpTotal = _cursor.getDouble(_cursorIndexOfTotal);
            final double _tmpProfit;
            _tmpProfit = _cursor.getDouble(_cursorIndexOfProfit);
            final boolean _tmpHadExpiredWarning;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfHadExpiredWarning);
            _tmpHadExpiredWarning = _tmp != 0;
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _item = new SaleEntity(_tmpId,_tmpBillNo,_tmpSoldAtMillis,_tmpCustomerName,_tmpPaymentMode,_tmpSubtotal,_tmpTotal,_tmpProfit,_tmpHadExpiredWarning,_tmpNote);
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
  public Object getAllItemsNow(final Continuation<? super List<SaleItemEntity>> $completion) {
    final String _sql = "SELECT * FROM sale_items ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SaleItemEntity>>() {
      @Override
      @NonNull
      public List<SaleItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSaleId = CursorUtil.getColumnIndexOrThrow(_cursor, "saleId");
          final int _cursorIndexOfMedicineId = CursorUtil.getColumnIndexOrThrow(_cursor, "medicineId");
          final int _cursorIndexOfBatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "batchId");
          final int _cursorIndexOfMedicineName = CursorUtil.getColumnIndexOrThrow(_cursor, "medicineName");
          final int _cursorIndexOfBatchNo = CursorUtil.getColumnIndexOrThrow(_cursor, "batchNo");
          final int _cursorIndexOfExpiryEpochDay = CursorUtil.getColumnIndexOrThrow(_cursor, "expiryEpochDay");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfMrp = CursorUtil.getColumnIndexOrThrow(_cursor, "mrp");
          final int _cursorIndexOfPurchasePrice = CursorUtil.getColumnIndexOrThrow(_cursor, "purchasePrice");
          final int _cursorIndexOfLineTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "lineTotal");
          final int _cursorIndexOfLineProfit = CursorUtil.getColumnIndexOrThrow(_cursor, "lineProfit");
          final int _cursorIndexOfWasExpired = CursorUtil.getColumnIndexOrThrow(_cursor, "wasExpired");
          final List<SaleItemEntity> _result = new ArrayList<SaleItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SaleItemEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpSaleId;
            _tmpSaleId = _cursor.getLong(_cursorIndexOfSaleId);
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
            final double _tmpMrp;
            _tmpMrp = _cursor.getDouble(_cursorIndexOfMrp);
            final double _tmpPurchasePrice;
            _tmpPurchasePrice = _cursor.getDouble(_cursorIndexOfPurchasePrice);
            final double _tmpLineTotal;
            _tmpLineTotal = _cursor.getDouble(_cursorIndexOfLineTotal);
            final double _tmpLineProfit;
            _tmpLineProfit = _cursor.getDouble(_cursorIndexOfLineProfit);
            final boolean _tmpWasExpired;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfWasExpired);
            _tmpWasExpired = _tmp != 0;
            _item = new SaleItemEntity(_tmpId,_tmpSaleId,_tmpMedicineId,_tmpBatchId,_tmpMedicineName,_tmpBatchNo,_tmpExpiryEpochDay,_tmpQuantity,_tmpMrp,_tmpPurchasePrice,_tmpLineTotal,_tmpLineProfit,_tmpWasExpired);
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
  public Object getByBillNo(final String billNo,
      final Continuation<? super SaleEntity> $completion) {
    final String _sql = "SELECT * FROM sales WHERE billNo = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, billNo);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SaleEntity>() {
      @Override
      @Nullable
      public SaleEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBillNo = CursorUtil.getColumnIndexOrThrow(_cursor, "billNo");
          final int _cursorIndexOfSoldAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "soldAtMillis");
          final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
          final int _cursorIndexOfPaymentMode = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMode");
          final int _cursorIndexOfSubtotal = CursorUtil.getColumnIndexOrThrow(_cursor, "subtotal");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final int _cursorIndexOfProfit = CursorUtil.getColumnIndexOrThrow(_cursor, "profit");
          final int _cursorIndexOfHadExpiredWarning = CursorUtil.getColumnIndexOrThrow(_cursor, "hadExpiredWarning");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final SaleEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpBillNo;
            _tmpBillNo = _cursor.getString(_cursorIndexOfBillNo);
            final long _tmpSoldAtMillis;
            _tmpSoldAtMillis = _cursor.getLong(_cursorIndexOfSoldAtMillis);
            final String _tmpCustomerName;
            _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            final String _tmpPaymentMode;
            _tmpPaymentMode = _cursor.getString(_cursorIndexOfPaymentMode);
            final double _tmpSubtotal;
            _tmpSubtotal = _cursor.getDouble(_cursorIndexOfSubtotal);
            final double _tmpTotal;
            _tmpTotal = _cursor.getDouble(_cursorIndexOfTotal);
            final double _tmpProfit;
            _tmpProfit = _cursor.getDouble(_cursorIndexOfProfit);
            final boolean _tmpHadExpiredWarning;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfHadExpiredWarning);
            _tmpHadExpiredWarning = _tmp != 0;
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            _result = new SaleEntity(_tmpId,_tmpBillNo,_tmpSoldAtMillis,_tmpCustomerName,_tmpPaymentMode,_tmpSubtotal,_tmpTotal,_tmpProfit,_tmpHadExpiredWarning,_tmpNote);
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
  public Flow<SaleWithItems> observeSaleWithItems(final long saleId) {
    final String _sql = "SELECT * FROM sales WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, saleId);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"sale_items",
        "sales"}, new Callable<SaleWithItems>() {
      @Override
      @Nullable
      public SaleWithItems call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfBillNo = CursorUtil.getColumnIndexOrThrow(_cursor, "billNo");
            final int _cursorIndexOfSoldAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "soldAtMillis");
            final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
            final int _cursorIndexOfPaymentMode = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMode");
            final int _cursorIndexOfSubtotal = CursorUtil.getColumnIndexOrThrow(_cursor, "subtotal");
            final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
            final int _cursorIndexOfProfit = CursorUtil.getColumnIndexOrThrow(_cursor, "profit");
            final int _cursorIndexOfHadExpiredWarning = CursorUtil.getColumnIndexOrThrow(_cursor, "hadExpiredWarning");
            final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
            final LongSparseArray<ArrayList<SaleItemEntity>> _collectionItems = new LongSparseArray<ArrayList<SaleItemEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionItems.containsKey(_tmpKey)) {
                _collectionItems.put(_tmpKey, new ArrayList<SaleItemEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipsaleItemsAscomExamplePharmacyinventoryDataSaleItemEntity(_collectionItems);
            final SaleWithItems _result;
            if (_cursor.moveToFirst()) {
              final SaleEntity _tmpSale;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpBillNo;
              _tmpBillNo = _cursor.getString(_cursorIndexOfBillNo);
              final long _tmpSoldAtMillis;
              _tmpSoldAtMillis = _cursor.getLong(_cursorIndexOfSoldAtMillis);
              final String _tmpCustomerName;
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
              final String _tmpPaymentMode;
              _tmpPaymentMode = _cursor.getString(_cursorIndexOfPaymentMode);
              final double _tmpSubtotal;
              _tmpSubtotal = _cursor.getDouble(_cursorIndexOfSubtotal);
              final double _tmpTotal;
              _tmpTotal = _cursor.getDouble(_cursorIndexOfTotal);
              final double _tmpProfit;
              _tmpProfit = _cursor.getDouble(_cursorIndexOfProfit);
              final boolean _tmpHadExpiredWarning;
              final int _tmp;
              _tmp = _cursor.getInt(_cursorIndexOfHadExpiredWarning);
              _tmpHadExpiredWarning = _tmp != 0;
              final String _tmpNote;
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
              _tmpSale = new SaleEntity(_tmpId,_tmpBillNo,_tmpSoldAtMillis,_tmpCustomerName,_tmpPaymentMode,_tmpSubtotal,_tmpTotal,_tmpProfit,_tmpHadExpiredWarning,_tmpNote);
              final ArrayList<SaleItemEntity> _tmpItemsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpItemsCollection = _collectionItems.get(_tmpKey_1);
              _result = new SaleWithItems(_tmpSale,_tmpItemsCollection);
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
  public Flow<List<SaleWithItems>> observeRecentSales(final int limit) {
    final String _sql = "SELECT * FROM sales ORDER BY soldAtMillis DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"sale_items",
        "sales"}, new Callable<List<SaleWithItems>>() {
      @Override
      @NonNull
      public List<SaleWithItems> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfBillNo = CursorUtil.getColumnIndexOrThrow(_cursor, "billNo");
            final int _cursorIndexOfSoldAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "soldAtMillis");
            final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
            final int _cursorIndexOfPaymentMode = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMode");
            final int _cursorIndexOfSubtotal = CursorUtil.getColumnIndexOrThrow(_cursor, "subtotal");
            final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
            final int _cursorIndexOfProfit = CursorUtil.getColumnIndexOrThrow(_cursor, "profit");
            final int _cursorIndexOfHadExpiredWarning = CursorUtil.getColumnIndexOrThrow(_cursor, "hadExpiredWarning");
            final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
            final LongSparseArray<ArrayList<SaleItemEntity>> _collectionItems = new LongSparseArray<ArrayList<SaleItemEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionItems.containsKey(_tmpKey)) {
                _collectionItems.put(_tmpKey, new ArrayList<SaleItemEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipsaleItemsAscomExamplePharmacyinventoryDataSaleItemEntity(_collectionItems);
            final List<SaleWithItems> _result = new ArrayList<SaleWithItems>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final SaleWithItems _item;
              final SaleEntity _tmpSale;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpBillNo;
              _tmpBillNo = _cursor.getString(_cursorIndexOfBillNo);
              final long _tmpSoldAtMillis;
              _tmpSoldAtMillis = _cursor.getLong(_cursorIndexOfSoldAtMillis);
              final String _tmpCustomerName;
              _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
              final String _tmpPaymentMode;
              _tmpPaymentMode = _cursor.getString(_cursorIndexOfPaymentMode);
              final double _tmpSubtotal;
              _tmpSubtotal = _cursor.getDouble(_cursorIndexOfSubtotal);
              final double _tmpTotal;
              _tmpTotal = _cursor.getDouble(_cursorIndexOfTotal);
              final double _tmpProfit;
              _tmpProfit = _cursor.getDouble(_cursorIndexOfProfit);
              final boolean _tmpHadExpiredWarning;
              final int _tmp;
              _tmp = _cursor.getInt(_cursorIndexOfHadExpiredWarning);
              _tmpHadExpiredWarning = _tmp != 0;
              final String _tmpNote;
              _tmpNote = _cursor.getString(_cursorIndexOfNote);
              _tmpSale = new SaleEntity(_tmpId,_tmpBillNo,_tmpSoldAtMillis,_tmpCustomerName,_tmpPaymentMode,_tmpSubtotal,_tmpTotal,_tmpProfit,_tmpHadExpiredWarning,_tmpNote);
              final ArrayList<SaleItemEntity> _tmpItemsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpItemsCollection = _collectionItems.get(_tmpKey_1);
              _item = new SaleWithItems(_tmpSale,_tmpItemsCollection);
              _result.add(_item);
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
  public Flow<List<SaleReportRow>> observeReport(final long startMillis, final long endMillis) {
    final String _sql = "\n"
            + "        SELECT\n"
            + "            s.id AS saleId,\n"
            + "            s.billNo AS billNo,\n"
            + "            s.soldAtMillis AS soldAtMillis,\n"
            + "            s.customerName AS customerName,\n"
            + "            s.paymentMode AS paymentMode,\n"
            + "            s.total AS total,\n"
            + "            s.profit AS profit,\n"
            + "            COALESCE(SUM(si.quantity), 0) AS itemCount\n"
            + "        FROM sales s\n"
            + "        LEFT JOIN sale_items si ON si.saleId = s.id\n"
            + "        WHERE s.soldAtMillis BETWEEN ? AND ?\n"
            + "        GROUP BY s.id\n"
            + "        ORDER BY s.soldAtMillis DESC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startMillis);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endMillis);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sales",
        "sale_items"}, new Callable<List<SaleReportRow>>() {
      @Override
      @NonNull
      public List<SaleReportRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfSaleId = 0;
          final int _cursorIndexOfBillNo = 1;
          final int _cursorIndexOfSoldAtMillis = 2;
          final int _cursorIndexOfCustomerName = 3;
          final int _cursorIndexOfPaymentMode = 4;
          final int _cursorIndexOfTotal = 5;
          final int _cursorIndexOfProfit = 6;
          final int _cursorIndexOfItemCount = 7;
          final List<SaleReportRow> _result = new ArrayList<SaleReportRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SaleReportRow _item;
            final long _tmpSaleId;
            _tmpSaleId = _cursor.getLong(_cursorIndexOfSaleId);
            final String _tmpBillNo;
            _tmpBillNo = _cursor.getString(_cursorIndexOfBillNo);
            final long _tmpSoldAtMillis;
            _tmpSoldAtMillis = _cursor.getLong(_cursorIndexOfSoldAtMillis);
            final String _tmpCustomerName;
            _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
            final String _tmpPaymentMode;
            _tmpPaymentMode = _cursor.getString(_cursorIndexOfPaymentMode);
            final double _tmpTotal;
            _tmpTotal = _cursor.getDouble(_cursorIndexOfTotal);
            final double _tmpProfit;
            _tmpProfit = _cursor.getDouble(_cursorIndexOfProfit);
            final int _tmpItemCount;
            _tmpItemCount = _cursor.getInt(_cursorIndexOfItemCount);
            _item = new SaleReportRow(_tmpSaleId,_tmpBillNo,_tmpSoldAtMillis,_tmpCustomerName,_tmpPaymentMode,_tmpTotal,_tmpProfit,_tmpItemCount);
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
  public Flow<Double> observeRevenue(final long startMillis, final long endMillis) {
    final String _sql = "SELECT COALESCE(SUM(total), 0.0) FROM sales WHERE soldAtMillis BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startMillis);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endMillis);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sales"}, new Callable<Double>() {
      @Override
      @NonNull
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final double _tmp;
            _tmp = _cursor.getDouble(0);
            _result = _tmp;
          } else {
            _result = 0.0;
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
  public Flow<Double> observeProfit(final long startMillis, final long endMillis) {
    final String _sql = "SELECT COALESCE(SUM(profit), 0.0) FROM sales WHERE soldAtMillis BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startMillis);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endMillis);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sales"}, new Callable<Double>() {
      @Override
      @NonNull
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final double _tmp;
            _tmp = _cursor.getDouble(0);
            _result = _tmp;
          } else {
            _result = 0.0;
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
  public Flow<Integer> observeBillCount(final long startMillis, final long endMillis) {
    final String _sql = "SELECT COUNT(*) FROM sales WHERE soldAtMillis BETWEEN ? AND ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startMillis);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endMillis);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"sales"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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

  private void __fetchRelationshipsaleItemsAscomExamplePharmacyinventoryDataSaleItemEntity(
      @NonNull final LongSparseArray<ArrayList<SaleItemEntity>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipsaleItemsAscomExamplePharmacyinventoryDataSaleItemEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`saleId`,`medicineId`,`batchId`,`medicineName`,`batchNo`,`expiryEpochDay`,`quantity`,`mrp`,`purchasePrice`,`lineTotal`,`lineProfit`,`wasExpired` FROM `sale_items` WHERE `saleId` IN (");
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
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "saleId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfSaleId = 1;
      final int _cursorIndexOfMedicineId = 2;
      final int _cursorIndexOfBatchId = 3;
      final int _cursorIndexOfMedicineName = 4;
      final int _cursorIndexOfBatchNo = 5;
      final int _cursorIndexOfExpiryEpochDay = 6;
      final int _cursorIndexOfQuantity = 7;
      final int _cursorIndexOfMrp = 8;
      final int _cursorIndexOfPurchasePrice = 9;
      final int _cursorIndexOfLineTotal = 10;
      final int _cursorIndexOfLineProfit = 11;
      final int _cursorIndexOfWasExpired = 12;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<SaleItemEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final SaleItemEntity _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final long _tmpSaleId;
          _tmpSaleId = _cursor.getLong(_cursorIndexOfSaleId);
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
          final double _tmpMrp;
          _tmpMrp = _cursor.getDouble(_cursorIndexOfMrp);
          final double _tmpPurchasePrice;
          _tmpPurchasePrice = _cursor.getDouble(_cursorIndexOfPurchasePrice);
          final double _tmpLineTotal;
          _tmpLineTotal = _cursor.getDouble(_cursorIndexOfLineTotal);
          final double _tmpLineProfit;
          _tmpLineProfit = _cursor.getDouble(_cursorIndexOfLineProfit);
          final boolean _tmpWasExpired;
          final int _tmp;
          _tmp = _cursor.getInt(_cursorIndexOfWasExpired);
          _tmpWasExpired = _tmp != 0;
          _item_1 = new SaleItemEntity(_tmpId,_tmpSaleId,_tmpMedicineId,_tmpBatchId,_tmpMedicineName,_tmpBatchNo,_tmpExpiryEpochDay,_tmpQuantity,_tmpMrp,_tmpPurchasePrice,_tmpLineTotal,_tmpLineProfit,_tmpWasExpired);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
