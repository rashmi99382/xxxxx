package com.example.pharmacyinventory.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
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
public final class MedicineDao_Impl implements MedicineDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MedicineEntity> __insertionAdapterOfMedicineEntity;

  private final EntityDeletionOrUpdateAdapter<MedicineEntity> __updateAdapterOfMedicineEntity;

  public MedicineDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMedicineEntity = new EntityInsertionAdapter<MedicineEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `medicines` (`id`,`name`,`company`,`genericName`,`category`,`supplierId`,`minStock`,`createdAtMillis`,`updatedAtMillis`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MedicineEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getCompany());
        statement.bindString(4, entity.getGenericName());
        statement.bindString(5, entity.getCategory());
        if (entity.getSupplierId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getSupplierId());
        }
        statement.bindLong(7, entity.getMinStock());
        statement.bindLong(8, entity.getCreatedAtMillis());
        statement.bindLong(9, entity.getUpdatedAtMillis());
      }
    };
    this.__updateAdapterOfMedicineEntity = new EntityDeletionOrUpdateAdapter<MedicineEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `medicines` SET `id` = ?,`name` = ?,`company` = ?,`genericName` = ?,`category` = ?,`supplierId` = ?,`minStock` = ?,`createdAtMillis` = ?,`updatedAtMillis` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MedicineEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getCompany());
        statement.bindString(4, entity.getGenericName());
        statement.bindString(5, entity.getCategory());
        if (entity.getSupplierId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getSupplierId());
        }
        statement.bindLong(7, entity.getMinStock());
        statement.bindLong(8, entity.getCreatedAtMillis());
        statement.bindLong(9, entity.getUpdatedAtMillis());
        statement.bindLong(10, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final MedicineEntity medicine,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMedicineEntity.insertAndReturnId(medicine);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final MedicineEntity medicine,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMedicineEntity.handle(medicine);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MedicineEntity>> observeAll() {
    final String _sql = "SELECT * FROM medicines ORDER BY name COLLATE NOCASE";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"medicines"}, new Callable<List<MedicineEntity>>() {
      @Override
      @NonNull
      public List<MedicineEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCompany = CursorUtil.getColumnIndexOrThrow(_cursor, "company");
          final int _cursorIndexOfGenericName = CursorUtil.getColumnIndexOrThrow(_cursor, "genericName");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSupplierId = CursorUtil.getColumnIndexOrThrow(_cursor, "supplierId");
          final int _cursorIndexOfMinStock = CursorUtil.getColumnIndexOrThrow(_cursor, "minStock");
          final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
          final int _cursorIndexOfUpdatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtMillis");
          final List<MedicineEntity> _result = new ArrayList<MedicineEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MedicineEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCompany;
            _tmpCompany = _cursor.getString(_cursorIndexOfCompany);
            final String _tmpGenericName;
            _tmpGenericName = _cursor.getString(_cursorIndexOfGenericName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final Long _tmpSupplierId;
            if (_cursor.isNull(_cursorIndexOfSupplierId)) {
              _tmpSupplierId = null;
            } else {
              _tmpSupplierId = _cursor.getLong(_cursorIndexOfSupplierId);
            }
            final int _tmpMinStock;
            _tmpMinStock = _cursor.getInt(_cursorIndexOfMinStock);
            final long _tmpCreatedAtMillis;
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
            final long _tmpUpdatedAtMillis;
            _tmpUpdatedAtMillis = _cursor.getLong(_cursorIndexOfUpdatedAtMillis);
            _item = new MedicineEntity(_tmpId,_tmpName,_tmpCompany,_tmpGenericName,_tmpCategory,_tmpSupplierId,_tmpMinStock,_tmpCreatedAtMillis,_tmpUpdatedAtMillis);
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
  public Flow<List<MedicineEntity>> search(final String query) {
    final String _sql = "\n"
            + "        SELECT * FROM medicines\n"
            + "        WHERE ? = ''\n"
            + "           OR name LIKE '%' || ? || '%'\n"
            + "           OR company LIKE '%' || ? || '%'\n"
            + "           OR genericName LIKE '%' || ? || '%'\n"
            + "        ORDER BY name COLLATE NOCASE\n"
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
    return CoroutinesRoom.createFlow(__db, false, new String[] {"medicines"}, new Callable<List<MedicineEntity>>() {
      @Override
      @NonNull
      public List<MedicineEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCompany = CursorUtil.getColumnIndexOrThrow(_cursor, "company");
          final int _cursorIndexOfGenericName = CursorUtil.getColumnIndexOrThrow(_cursor, "genericName");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSupplierId = CursorUtil.getColumnIndexOrThrow(_cursor, "supplierId");
          final int _cursorIndexOfMinStock = CursorUtil.getColumnIndexOrThrow(_cursor, "minStock");
          final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
          final int _cursorIndexOfUpdatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtMillis");
          final List<MedicineEntity> _result = new ArrayList<MedicineEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MedicineEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCompany;
            _tmpCompany = _cursor.getString(_cursorIndexOfCompany);
            final String _tmpGenericName;
            _tmpGenericName = _cursor.getString(_cursorIndexOfGenericName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final Long _tmpSupplierId;
            if (_cursor.isNull(_cursorIndexOfSupplierId)) {
              _tmpSupplierId = null;
            } else {
              _tmpSupplierId = _cursor.getLong(_cursorIndexOfSupplierId);
            }
            final int _tmpMinStock;
            _tmpMinStock = _cursor.getInt(_cursorIndexOfMinStock);
            final long _tmpCreatedAtMillis;
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
            final long _tmpUpdatedAtMillis;
            _tmpUpdatedAtMillis = _cursor.getLong(_cursorIndexOfUpdatedAtMillis);
            _item = new MedicineEntity(_tmpId,_tmpName,_tmpCompany,_tmpGenericName,_tmpCategory,_tmpSupplierId,_tmpMinStock,_tmpCreatedAtMillis,_tmpUpdatedAtMillis);
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
  public Object getById(final long medicineId,
      final Continuation<? super MedicineEntity> $completion) {
    final String _sql = "SELECT * FROM medicines WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, medicineId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MedicineEntity>() {
      @Override
      @Nullable
      public MedicineEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCompany = CursorUtil.getColumnIndexOrThrow(_cursor, "company");
          final int _cursorIndexOfGenericName = CursorUtil.getColumnIndexOrThrow(_cursor, "genericName");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSupplierId = CursorUtil.getColumnIndexOrThrow(_cursor, "supplierId");
          final int _cursorIndexOfMinStock = CursorUtil.getColumnIndexOrThrow(_cursor, "minStock");
          final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
          final int _cursorIndexOfUpdatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtMillis");
          final MedicineEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCompany;
            _tmpCompany = _cursor.getString(_cursorIndexOfCompany);
            final String _tmpGenericName;
            _tmpGenericName = _cursor.getString(_cursorIndexOfGenericName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final Long _tmpSupplierId;
            if (_cursor.isNull(_cursorIndexOfSupplierId)) {
              _tmpSupplierId = null;
            } else {
              _tmpSupplierId = _cursor.getLong(_cursorIndexOfSupplierId);
            }
            final int _tmpMinStock;
            _tmpMinStock = _cursor.getInt(_cursorIndexOfMinStock);
            final long _tmpCreatedAtMillis;
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
            final long _tmpUpdatedAtMillis;
            _tmpUpdatedAtMillis = _cursor.getLong(_cursorIndexOfUpdatedAtMillis);
            _result = new MedicineEntity(_tmpId,_tmpName,_tmpCompany,_tmpGenericName,_tmpCategory,_tmpSupplierId,_tmpMinStock,_tmpCreatedAtMillis,_tmpUpdatedAtMillis);
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
  public Flow<MedicineWithBatches> observeMedicineWithBatches(final long medicineId) {
    final String _sql = "SELECT * FROM medicines WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, medicineId);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"batches",
        "medicines"}, new Callable<MedicineWithBatches>() {
      @Override
      @Nullable
      public MedicineWithBatches call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
            final int _cursorIndexOfCompany = CursorUtil.getColumnIndexOrThrow(_cursor, "company");
            final int _cursorIndexOfGenericName = CursorUtil.getColumnIndexOrThrow(_cursor, "genericName");
            final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
            final int _cursorIndexOfSupplierId = CursorUtil.getColumnIndexOrThrow(_cursor, "supplierId");
            final int _cursorIndexOfMinStock = CursorUtil.getColumnIndexOrThrow(_cursor, "minStock");
            final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
            final int _cursorIndexOfUpdatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtMillis");
            final LongSparseArray<ArrayList<BatchEntity>> _collectionBatches = new LongSparseArray<ArrayList<BatchEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionBatches.containsKey(_tmpKey)) {
                _collectionBatches.put(_tmpKey, new ArrayList<BatchEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipbatchesAscomExamplePharmacyinventoryDataBatchEntity(_collectionBatches);
            final MedicineWithBatches _result;
            if (_cursor.moveToFirst()) {
              final MedicineEntity _tmpMedicine;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpName;
              _tmpName = _cursor.getString(_cursorIndexOfName);
              final String _tmpCompany;
              _tmpCompany = _cursor.getString(_cursorIndexOfCompany);
              final String _tmpGenericName;
              _tmpGenericName = _cursor.getString(_cursorIndexOfGenericName);
              final String _tmpCategory;
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
              final Long _tmpSupplierId;
              if (_cursor.isNull(_cursorIndexOfSupplierId)) {
                _tmpSupplierId = null;
              } else {
                _tmpSupplierId = _cursor.getLong(_cursorIndexOfSupplierId);
              }
              final int _tmpMinStock;
              _tmpMinStock = _cursor.getInt(_cursorIndexOfMinStock);
              final long _tmpCreatedAtMillis;
              _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
              final long _tmpUpdatedAtMillis;
              _tmpUpdatedAtMillis = _cursor.getLong(_cursorIndexOfUpdatedAtMillis);
              _tmpMedicine = new MedicineEntity(_tmpId,_tmpName,_tmpCompany,_tmpGenericName,_tmpCategory,_tmpSupplierId,_tmpMinStock,_tmpCreatedAtMillis,_tmpUpdatedAtMillis);
              final ArrayList<BatchEntity> _tmpBatchesCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpBatchesCollection = _collectionBatches.get(_tmpKey_1);
              _result = new MedicineWithBatches(_tmpMedicine,_tmpBatchesCollection);
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
  public Flow<Integer> observeMedicineCount() {
    final String _sql = "SELECT COUNT(*) FROM medicines";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"medicines"}, new Callable<Integer>() {
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

  @Override
  public Object countNow(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM medicines";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
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
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllNow(final Continuation<? super List<MedicineEntity>> $completion) {
    final String _sql = "SELECT * FROM medicines ORDER BY name COLLATE NOCASE";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MedicineEntity>>() {
      @Override
      @NonNull
      public List<MedicineEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCompany = CursorUtil.getColumnIndexOrThrow(_cursor, "company");
          final int _cursorIndexOfGenericName = CursorUtil.getColumnIndexOrThrow(_cursor, "genericName");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSupplierId = CursorUtil.getColumnIndexOrThrow(_cursor, "supplierId");
          final int _cursorIndexOfMinStock = CursorUtil.getColumnIndexOrThrow(_cursor, "minStock");
          final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
          final int _cursorIndexOfUpdatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtMillis");
          final List<MedicineEntity> _result = new ArrayList<MedicineEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MedicineEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCompany;
            _tmpCompany = _cursor.getString(_cursorIndexOfCompany);
            final String _tmpGenericName;
            _tmpGenericName = _cursor.getString(_cursorIndexOfGenericName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final Long _tmpSupplierId;
            if (_cursor.isNull(_cursorIndexOfSupplierId)) {
              _tmpSupplierId = null;
            } else {
              _tmpSupplierId = _cursor.getLong(_cursorIndexOfSupplierId);
            }
            final int _tmpMinStock;
            _tmpMinStock = _cursor.getInt(_cursorIndexOfMinStock);
            final long _tmpCreatedAtMillis;
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
            final long _tmpUpdatedAtMillis;
            _tmpUpdatedAtMillis = _cursor.getLong(_cursorIndexOfUpdatedAtMillis);
            _item = new MedicineEntity(_tmpId,_tmpName,_tmpCompany,_tmpGenericName,_tmpCategory,_tmpSupplierId,_tmpMinStock,_tmpCreatedAtMillis,_tmpUpdatedAtMillis);
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
  public Object getByNaturalKey(final String name, final String company, final String genericName,
      final Continuation<? super MedicineEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM medicines\n"
            + "        WHERE name = ?\n"
            + "          AND company = ?\n"
            + "          AND genericName = ?\n"
            + "        LIMIT 1\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, name);
    _argIndex = 2;
    _statement.bindString(_argIndex, company);
    _argIndex = 3;
    _statement.bindString(_argIndex, genericName);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MedicineEntity>() {
      @Override
      @Nullable
      public MedicineEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCompany = CursorUtil.getColumnIndexOrThrow(_cursor, "company");
          final int _cursorIndexOfGenericName = CursorUtil.getColumnIndexOrThrow(_cursor, "genericName");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSupplierId = CursorUtil.getColumnIndexOrThrow(_cursor, "supplierId");
          final int _cursorIndexOfMinStock = CursorUtil.getColumnIndexOrThrow(_cursor, "minStock");
          final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
          final int _cursorIndexOfUpdatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtMillis");
          final MedicineEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCompany;
            _tmpCompany = _cursor.getString(_cursorIndexOfCompany);
            final String _tmpGenericName;
            _tmpGenericName = _cursor.getString(_cursorIndexOfGenericName);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final Long _tmpSupplierId;
            if (_cursor.isNull(_cursorIndexOfSupplierId)) {
              _tmpSupplierId = null;
            } else {
              _tmpSupplierId = _cursor.getLong(_cursorIndexOfSupplierId);
            }
            final int _tmpMinStock;
            _tmpMinStock = _cursor.getInt(_cursorIndexOfMinStock);
            final long _tmpCreatedAtMillis;
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
            final long _tmpUpdatedAtMillis;
            _tmpUpdatedAtMillis = _cursor.getLong(_cursorIndexOfUpdatedAtMillis);
            _result = new MedicineEntity(_tmpId,_tmpName,_tmpCompany,_tmpGenericName,_tmpCategory,_tmpSupplierId,_tmpMinStock,_tmpCreatedAtMillis,_tmpUpdatedAtMillis);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipbatchesAscomExamplePharmacyinventoryDataBatchEntity(
      @NonNull final LongSparseArray<ArrayList<BatchEntity>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipbatchesAscomExamplePharmacyinventoryDataBatchEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`medicineId`,`supplierId`,`batchNo`,`expiryEpochDay`,`quantity`,`purchasePrice`,`mrp`,`createdAtMillis`,`updatedAtMillis` FROM `batches` WHERE `medicineId` IN (");
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
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "medicineId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfMedicineId = 1;
      final int _cursorIndexOfSupplierId = 2;
      final int _cursorIndexOfBatchNo = 3;
      final int _cursorIndexOfExpiryEpochDay = 4;
      final int _cursorIndexOfQuantity = 5;
      final int _cursorIndexOfPurchasePrice = 6;
      final int _cursorIndexOfMrp = 7;
      final int _cursorIndexOfCreatedAtMillis = 8;
      final int _cursorIndexOfUpdatedAtMillis = 9;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<BatchEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final BatchEntity _item_1;
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
          _item_1 = new BatchEntity(_tmpId,_tmpMedicineId,_tmpSupplierId,_tmpBatchNo,_tmpExpiryEpochDay,_tmpQuantity,_tmpPurchasePrice,_tmpMrp,_tmpCreatedAtMillis,_tmpUpdatedAtMillis);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
