package com.example.pharmacyinventory.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile MedicineDao _medicineDao;

  private volatile BatchDao _batchDao;

  private volatile SupplierDao _supplierDao;

  private volatile SaleDao _saleDao;

  private volatile PurchaseDao _purchaseDao;

  private volatile StockAdjustmentDao _stockAdjustmentDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `medicines` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `company` TEXT NOT NULL, `genericName` TEXT NOT NULL, `category` TEXT NOT NULL, `supplierId` INTEGER, `minStock` INTEGER NOT NULL, `createdAtMillis` INTEGER NOT NULL, `updatedAtMillis` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_medicines_name` ON `medicines` (`name`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_medicines_category` ON `medicines` (`category`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_medicines_supplierId` ON `medicines` (`supplierId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `batches` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `medicineId` INTEGER NOT NULL, `supplierId` INTEGER, `batchNo` TEXT NOT NULL, `expiryEpochDay` INTEGER NOT NULL, `quantity` INTEGER NOT NULL, `purchasePrice` REAL NOT NULL, `mrp` REAL NOT NULL, `createdAtMillis` INTEGER NOT NULL, `updatedAtMillis` INTEGER NOT NULL, FOREIGN KEY(`medicineId`) REFERENCES `medicines`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`supplierId`) REFERENCES `suppliers`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_batches_medicineId` ON `batches` (`medicineId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_batches_supplierId` ON `batches` (`supplierId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_batches_batchNo` ON `batches` (`batchNo`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_batches_expiryEpochDay` ON `batches` (`expiryEpochDay`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `suppliers` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `contactPerson` TEXT NOT NULL, `phone` TEXT NOT NULL, `address` TEXT NOT NULL, `balance` REAL NOT NULL, `createdAtMillis` INTEGER NOT NULL, `updatedAtMillis` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_suppliers_name` ON `suppliers` (`name`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_suppliers_phone` ON `suppliers` (`phone`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sales` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `billNo` TEXT NOT NULL, `soldAtMillis` INTEGER NOT NULL, `customerName` TEXT NOT NULL, `paymentMode` TEXT NOT NULL, `subtotal` REAL NOT NULL, `total` REAL NOT NULL, `profit` REAL NOT NULL, `hadExpiredWarning` INTEGER NOT NULL, `note` TEXT NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_sales_billNo` ON `sales` (`billNo`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sales_soldAtMillis` ON `sales` (`soldAtMillis`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sale_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `saleId` INTEGER NOT NULL, `medicineId` INTEGER NOT NULL, `batchId` INTEGER NOT NULL, `medicineName` TEXT NOT NULL, `batchNo` TEXT NOT NULL, `expiryEpochDay` INTEGER NOT NULL, `quantity` INTEGER NOT NULL, `mrp` REAL NOT NULL, `purchasePrice` REAL NOT NULL, `lineTotal` REAL NOT NULL, `lineProfit` REAL NOT NULL, `wasExpired` INTEGER NOT NULL, FOREIGN KEY(`saleId`) REFERENCES `sales`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`medicineId`) REFERENCES `medicines`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`batchId`) REFERENCES `batches`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sale_items_saleId` ON `sale_items` (`saleId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sale_items_medicineId` ON `sale_items` (`medicineId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sale_items_batchId` ON `sale_items` (`batchId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `purchases` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `voucherNo` TEXT NOT NULL, `supplierId` INTEGER, `supplierName` TEXT NOT NULL, `purchasedAtMillis` INTEGER NOT NULL, `total` REAL NOT NULL, `note` TEXT NOT NULL, FOREIGN KEY(`supplierId`) REFERENCES `suppliers`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_purchases_voucherNo` ON `purchases` (`voucherNo`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_purchases_supplierId` ON `purchases` (`supplierId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_purchases_purchasedAtMillis` ON `purchases` (`purchasedAtMillis`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `purchase_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `purchaseId` INTEGER NOT NULL, `medicineId` INTEGER NOT NULL, `batchId` INTEGER NOT NULL, `medicineName` TEXT NOT NULL, `batchNo` TEXT NOT NULL, `expiryEpochDay` INTEGER NOT NULL, `quantity` INTEGER NOT NULL, `purchasePrice` REAL NOT NULL, `mrp` REAL NOT NULL, `lineTotal` REAL NOT NULL, FOREIGN KEY(`purchaseId`) REFERENCES `purchases`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`medicineId`) REFERENCES `medicines`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`batchId`) REFERENCES `batches`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_purchase_items_purchaseId` ON `purchase_items` (`purchaseId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_purchase_items_medicineId` ON `purchase_items` (`medicineId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_purchase_items_batchId` ON `purchase_items` (`batchId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `stock_adjustments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `medicineId` INTEGER NOT NULL, `batchId` INTEGER NOT NULL, `quantityDelta` INTEGER NOT NULL, `reason` TEXT NOT NULL, `referenceType` TEXT NOT NULL, `referenceId` INTEGER, `createdAtMillis` INTEGER NOT NULL, FOREIGN KEY(`medicineId`) REFERENCES `medicines`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`batchId`) REFERENCES `batches`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_stock_adjustments_medicineId` ON `stock_adjustments` (`medicineId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_stock_adjustments_batchId` ON `stock_adjustments` (`batchId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_stock_adjustments_createdAtMillis` ON `stock_adjustments` (`createdAtMillis`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f902f047a4dc576e0969008de07434ee')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `medicines`");
        db.execSQL("DROP TABLE IF EXISTS `batches`");
        db.execSQL("DROP TABLE IF EXISTS `suppliers`");
        db.execSQL("DROP TABLE IF EXISTS `sales`");
        db.execSQL("DROP TABLE IF EXISTS `sale_items`");
        db.execSQL("DROP TABLE IF EXISTS `purchases`");
        db.execSQL("DROP TABLE IF EXISTS `purchase_items`");
        db.execSQL("DROP TABLE IF EXISTS `stock_adjustments`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsMedicines = new HashMap<String, TableInfo.Column>(9);
        _columnsMedicines.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicines.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicines.put("company", new TableInfo.Column("company", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicines.put("genericName", new TableInfo.Column("genericName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicines.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicines.put("supplierId", new TableInfo.Column("supplierId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicines.put("minStock", new TableInfo.Column("minStock", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicines.put("createdAtMillis", new TableInfo.Column("createdAtMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicines.put("updatedAtMillis", new TableInfo.Column("updatedAtMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMedicines = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMedicines = new HashSet<TableInfo.Index>(3);
        _indicesMedicines.add(new TableInfo.Index("index_medicines_name", false, Arrays.asList("name"), Arrays.asList("ASC")));
        _indicesMedicines.add(new TableInfo.Index("index_medicines_category", false, Arrays.asList("category"), Arrays.asList("ASC")));
        _indicesMedicines.add(new TableInfo.Index("index_medicines_supplierId", false, Arrays.asList("supplierId"), Arrays.asList("ASC")));
        final TableInfo _infoMedicines = new TableInfo("medicines", _columnsMedicines, _foreignKeysMedicines, _indicesMedicines);
        final TableInfo _existingMedicines = TableInfo.read(db, "medicines");
        if (!_infoMedicines.equals(_existingMedicines)) {
          return new RoomOpenHelper.ValidationResult(false, "medicines(com.example.pharmacyinventory.data.MedicineEntity).\n"
                  + " Expected:\n" + _infoMedicines + "\n"
                  + " Found:\n" + _existingMedicines);
        }
        final HashMap<String, TableInfo.Column> _columnsBatches = new HashMap<String, TableInfo.Column>(10);
        _columnsBatches.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBatches.put("medicineId", new TableInfo.Column("medicineId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBatches.put("supplierId", new TableInfo.Column("supplierId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBatches.put("batchNo", new TableInfo.Column("batchNo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBatches.put("expiryEpochDay", new TableInfo.Column("expiryEpochDay", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBatches.put("quantity", new TableInfo.Column("quantity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBatches.put("purchasePrice", new TableInfo.Column("purchasePrice", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBatches.put("mrp", new TableInfo.Column("mrp", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBatches.put("createdAtMillis", new TableInfo.Column("createdAtMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBatches.put("updatedAtMillis", new TableInfo.Column("updatedAtMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBatches = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysBatches.add(new TableInfo.ForeignKey("medicines", "CASCADE", "NO ACTION", Arrays.asList("medicineId"), Arrays.asList("id")));
        _foreignKeysBatches.add(new TableInfo.ForeignKey("suppliers", "SET NULL", "NO ACTION", Arrays.asList("supplierId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesBatches = new HashSet<TableInfo.Index>(4);
        _indicesBatches.add(new TableInfo.Index("index_batches_medicineId", false, Arrays.asList("medicineId"), Arrays.asList("ASC")));
        _indicesBatches.add(new TableInfo.Index("index_batches_supplierId", false, Arrays.asList("supplierId"), Arrays.asList("ASC")));
        _indicesBatches.add(new TableInfo.Index("index_batches_batchNo", false, Arrays.asList("batchNo"), Arrays.asList("ASC")));
        _indicesBatches.add(new TableInfo.Index("index_batches_expiryEpochDay", false, Arrays.asList("expiryEpochDay"), Arrays.asList("ASC")));
        final TableInfo _infoBatches = new TableInfo("batches", _columnsBatches, _foreignKeysBatches, _indicesBatches);
        final TableInfo _existingBatches = TableInfo.read(db, "batches");
        if (!_infoBatches.equals(_existingBatches)) {
          return new RoomOpenHelper.ValidationResult(false, "batches(com.example.pharmacyinventory.data.BatchEntity).\n"
                  + " Expected:\n" + _infoBatches + "\n"
                  + " Found:\n" + _existingBatches);
        }
        final HashMap<String, TableInfo.Column> _columnsSuppliers = new HashMap<String, TableInfo.Column>(8);
        _columnsSuppliers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSuppliers.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSuppliers.put("contactPerson", new TableInfo.Column("contactPerson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSuppliers.put("phone", new TableInfo.Column("phone", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSuppliers.put("address", new TableInfo.Column("address", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSuppliers.put("balance", new TableInfo.Column("balance", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSuppliers.put("createdAtMillis", new TableInfo.Column("createdAtMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSuppliers.put("updatedAtMillis", new TableInfo.Column("updatedAtMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSuppliers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSuppliers = new HashSet<TableInfo.Index>(2);
        _indicesSuppliers.add(new TableInfo.Index("index_suppliers_name", true, Arrays.asList("name"), Arrays.asList("ASC")));
        _indicesSuppliers.add(new TableInfo.Index("index_suppliers_phone", false, Arrays.asList("phone"), Arrays.asList("ASC")));
        final TableInfo _infoSuppliers = new TableInfo("suppliers", _columnsSuppliers, _foreignKeysSuppliers, _indicesSuppliers);
        final TableInfo _existingSuppliers = TableInfo.read(db, "suppliers");
        if (!_infoSuppliers.equals(_existingSuppliers)) {
          return new RoomOpenHelper.ValidationResult(false, "suppliers(com.example.pharmacyinventory.data.SupplierEntity).\n"
                  + " Expected:\n" + _infoSuppliers + "\n"
                  + " Found:\n" + _existingSuppliers);
        }
        final HashMap<String, TableInfo.Column> _columnsSales = new HashMap<String, TableInfo.Column>(10);
        _columnsSales.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("billNo", new TableInfo.Column("billNo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("soldAtMillis", new TableInfo.Column("soldAtMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("customerName", new TableInfo.Column("customerName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("paymentMode", new TableInfo.Column("paymentMode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("subtotal", new TableInfo.Column("subtotal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("total", new TableInfo.Column("total", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("profit", new TableInfo.Column("profit", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("hadExpiredWarning", new TableInfo.Column("hadExpiredWarning", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSales.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSales = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSales = new HashSet<TableInfo.Index>(2);
        _indicesSales.add(new TableInfo.Index("index_sales_billNo", true, Arrays.asList("billNo"), Arrays.asList("ASC")));
        _indicesSales.add(new TableInfo.Index("index_sales_soldAtMillis", false, Arrays.asList("soldAtMillis"), Arrays.asList("ASC")));
        final TableInfo _infoSales = new TableInfo("sales", _columnsSales, _foreignKeysSales, _indicesSales);
        final TableInfo _existingSales = TableInfo.read(db, "sales");
        if (!_infoSales.equals(_existingSales)) {
          return new RoomOpenHelper.ValidationResult(false, "sales(com.example.pharmacyinventory.data.SaleEntity).\n"
                  + " Expected:\n" + _infoSales + "\n"
                  + " Found:\n" + _existingSales);
        }
        final HashMap<String, TableInfo.Column> _columnsSaleItems = new HashMap<String, TableInfo.Column>(13);
        _columnsSaleItems.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleItems.put("saleId", new TableInfo.Column("saleId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleItems.put("medicineId", new TableInfo.Column("medicineId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleItems.put("batchId", new TableInfo.Column("batchId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleItems.put("medicineName", new TableInfo.Column("medicineName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleItems.put("batchNo", new TableInfo.Column("batchNo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleItems.put("expiryEpochDay", new TableInfo.Column("expiryEpochDay", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleItems.put("quantity", new TableInfo.Column("quantity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleItems.put("mrp", new TableInfo.Column("mrp", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleItems.put("purchasePrice", new TableInfo.Column("purchasePrice", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleItems.put("lineTotal", new TableInfo.Column("lineTotal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleItems.put("lineProfit", new TableInfo.Column("lineProfit", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSaleItems.put("wasExpired", new TableInfo.Column("wasExpired", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSaleItems = new HashSet<TableInfo.ForeignKey>(3);
        _foreignKeysSaleItems.add(new TableInfo.ForeignKey("sales", "CASCADE", "NO ACTION", Arrays.asList("saleId"), Arrays.asList("id")));
        _foreignKeysSaleItems.add(new TableInfo.ForeignKey("medicines", "RESTRICT", "NO ACTION", Arrays.asList("medicineId"), Arrays.asList("id")));
        _foreignKeysSaleItems.add(new TableInfo.ForeignKey("batches", "RESTRICT", "NO ACTION", Arrays.asList("batchId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSaleItems = new HashSet<TableInfo.Index>(3);
        _indicesSaleItems.add(new TableInfo.Index("index_sale_items_saleId", false, Arrays.asList("saleId"), Arrays.asList("ASC")));
        _indicesSaleItems.add(new TableInfo.Index("index_sale_items_medicineId", false, Arrays.asList("medicineId"), Arrays.asList("ASC")));
        _indicesSaleItems.add(new TableInfo.Index("index_sale_items_batchId", false, Arrays.asList("batchId"), Arrays.asList("ASC")));
        final TableInfo _infoSaleItems = new TableInfo("sale_items", _columnsSaleItems, _foreignKeysSaleItems, _indicesSaleItems);
        final TableInfo _existingSaleItems = TableInfo.read(db, "sale_items");
        if (!_infoSaleItems.equals(_existingSaleItems)) {
          return new RoomOpenHelper.ValidationResult(false, "sale_items(com.example.pharmacyinventory.data.SaleItemEntity).\n"
                  + " Expected:\n" + _infoSaleItems + "\n"
                  + " Found:\n" + _existingSaleItems);
        }
        final HashMap<String, TableInfo.Column> _columnsPurchases = new HashMap<String, TableInfo.Column>(7);
        _columnsPurchases.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchases.put("voucherNo", new TableInfo.Column("voucherNo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchases.put("supplierId", new TableInfo.Column("supplierId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchases.put("supplierName", new TableInfo.Column("supplierName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchases.put("purchasedAtMillis", new TableInfo.Column("purchasedAtMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchases.put("total", new TableInfo.Column("total", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchases.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPurchases = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysPurchases.add(new TableInfo.ForeignKey("suppliers", "SET NULL", "NO ACTION", Arrays.asList("supplierId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesPurchases = new HashSet<TableInfo.Index>(3);
        _indicesPurchases.add(new TableInfo.Index("index_purchases_voucherNo", true, Arrays.asList("voucherNo"), Arrays.asList("ASC")));
        _indicesPurchases.add(new TableInfo.Index("index_purchases_supplierId", false, Arrays.asList("supplierId"), Arrays.asList("ASC")));
        _indicesPurchases.add(new TableInfo.Index("index_purchases_purchasedAtMillis", false, Arrays.asList("purchasedAtMillis"), Arrays.asList("ASC")));
        final TableInfo _infoPurchases = new TableInfo("purchases", _columnsPurchases, _foreignKeysPurchases, _indicesPurchases);
        final TableInfo _existingPurchases = TableInfo.read(db, "purchases");
        if (!_infoPurchases.equals(_existingPurchases)) {
          return new RoomOpenHelper.ValidationResult(false, "purchases(com.example.pharmacyinventory.data.PurchaseEntity).\n"
                  + " Expected:\n" + _infoPurchases + "\n"
                  + " Found:\n" + _existingPurchases);
        }
        final HashMap<String, TableInfo.Column> _columnsPurchaseItems = new HashMap<String, TableInfo.Column>(11);
        _columnsPurchaseItems.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchaseItems.put("purchaseId", new TableInfo.Column("purchaseId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchaseItems.put("medicineId", new TableInfo.Column("medicineId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchaseItems.put("batchId", new TableInfo.Column("batchId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchaseItems.put("medicineName", new TableInfo.Column("medicineName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchaseItems.put("batchNo", new TableInfo.Column("batchNo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchaseItems.put("expiryEpochDay", new TableInfo.Column("expiryEpochDay", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchaseItems.put("quantity", new TableInfo.Column("quantity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchaseItems.put("purchasePrice", new TableInfo.Column("purchasePrice", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchaseItems.put("mrp", new TableInfo.Column("mrp", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPurchaseItems.put("lineTotal", new TableInfo.Column("lineTotal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPurchaseItems = new HashSet<TableInfo.ForeignKey>(3);
        _foreignKeysPurchaseItems.add(new TableInfo.ForeignKey("purchases", "CASCADE", "NO ACTION", Arrays.asList("purchaseId"), Arrays.asList("id")));
        _foreignKeysPurchaseItems.add(new TableInfo.ForeignKey("medicines", "RESTRICT", "NO ACTION", Arrays.asList("medicineId"), Arrays.asList("id")));
        _foreignKeysPurchaseItems.add(new TableInfo.ForeignKey("batches", "RESTRICT", "NO ACTION", Arrays.asList("batchId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesPurchaseItems = new HashSet<TableInfo.Index>(3);
        _indicesPurchaseItems.add(new TableInfo.Index("index_purchase_items_purchaseId", false, Arrays.asList("purchaseId"), Arrays.asList("ASC")));
        _indicesPurchaseItems.add(new TableInfo.Index("index_purchase_items_medicineId", false, Arrays.asList("medicineId"), Arrays.asList("ASC")));
        _indicesPurchaseItems.add(new TableInfo.Index("index_purchase_items_batchId", false, Arrays.asList("batchId"), Arrays.asList("ASC")));
        final TableInfo _infoPurchaseItems = new TableInfo("purchase_items", _columnsPurchaseItems, _foreignKeysPurchaseItems, _indicesPurchaseItems);
        final TableInfo _existingPurchaseItems = TableInfo.read(db, "purchase_items");
        if (!_infoPurchaseItems.equals(_existingPurchaseItems)) {
          return new RoomOpenHelper.ValidationResult(false, "purchase_items(com.example.pharmacyinventory.data.PurchaseItemEntity).\n"
                  + " Expected:\n" + _infoPurchaseItems + "\n"
                  + " Found:\n" + _existingPurchaseItems);
        }
        final HashMap<String, TableInfo.Column> _columnsStockAdjustments = new HashMap<String, TableInfo.Column>(8);
        _columnsStockAdjustments.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStockAdjustments.put("medicineId", new TableInfo.Column("medicineId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStockAdjustments.put("batchId", new TableInfo.Column("batchId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStockAdjustments.put("quantityDelta", new TableInfo.Column("quantityDelta", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStockAdjustments.put("reason", new TableInfo.Column("reason", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStockAdjustments.put("referenceType", new TableInfo.Column("referenceType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStockAdjustments.put("referenceId", new TableInfo.Column("referenceId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStockAdjustments.put("createdAtMillis", new TableInfo.Column("createdAtMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysStockAdjustments = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysStockAdjustments.add(new TableInfo.ForeignKey("medicines", "CASCADE", "NO ACTION", Arrays.asList("medicineId"), Arrays.asList("id")));
        _foreignKeysStockAdjustments.add(new TableInfo.ForeignKey("batches", "CASCADE", "NO ACTION", Arrays.asList("batchId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesStockAdjustments = new HashSet<TableInfo.Index>(3);
        _indicesStockAdjustments.add(new TableInfo.Index("index_stock_adjustments_medicineId", false, Arrays.asList("medicineId"), Arrays.asList("ASC")));
        _indicesStockAdjustments.add(new TableInfo.Index("index_stock_adjustments_batchId", false, Arrays.asList("batchId"), Arrays.asList("ASC")));
        _indicesStockAdjustments.add(new TableInfo.Index("index_stock_adjustments_createdAtMillis", false, Arrays.asList("createdAtMillis"), Arrays.asList("ASC")));
        final TableInfo _infoStockAdjustments = new TableInfo("stock_adjustments", _columnsStockAdjustments, _foreignKeysStockAdjustments, _indicesStockAdjustments);
        final TableInfo _existingStockAdjustments = TableInfo.read(db, "stock_adjustments");
        if (!_infoStockAdjustments.equals(_existingStockAdjustments)) {
          return new RoomOpenHelper.ValidationResult(false, "stock_adjustments(com.example.pharmacyinventory.data.StockAdjustmentEntity).\n"
                  + " Expected:\n" + _infoStockAdjustments + "\n"
                  + " Found:\n" + _existingStockAdjustments);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "f902f047a4dc576e0969008de07434ee", "c922dcafc330385098c3b09e1ea9823f");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "medicines","batches","suppliers","sales","sale_items","purchases","purchase_items","stock_adjustments");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `medicines`");
      _db.execSQL("DELETE FROM `batches`");
      _db.execSQL("DELETE FROM `suppliers`");
      _db.execSQL("DELETE FROM `sales`");
      _db.execSQL("DELETE FROM `sale_items`");
      _db.execSQL("DELETE FROM `purchases`");
      _db.execSQL("DELETE FROM `purchase_items`");
      _db.execSQL("DELETE FROM `stock_adjustments`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(MedicineDao.class, MedicineDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BatchDao.class, BatchDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SupplierDao.class, SupplierDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SaleDao.class, SaleDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PurchaseDao.class, PurchaseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(StockAdjustmentDao.class, StockAdjustmentDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public MedicineDao medicineDao() {
    if (_medicineDao != null) {
      return _medicineDao;
    } else {
      synchronized(this) {
        if(_medicineDao == null) {
          _medicineDao = new MedicineDao_Impl(this);
        }
        return _medicineDao;
      }
    }
  }

  @Override
  public BatchDao batchDao() {
    if (_batchDao != null) {
      return _batchDao;
    } else {
      synchronized(this) {
        if(_batchDao == null) {
          _batchDao = new BatchDao_Impl(this);
        }
        return _batchDao;
      }
    }
  }

  @Override
  public SupplierDao supplierDao() {
    if (_supplierDao != null) {
      return _supplierDao;
    } else {
      synchronized(this) {
        if(_supplierDao == null) {
          _supplierDao = new SupplierDao_Impl(this);
        }
        return _supplierDao;
      }
    }
  }

  @Override
  public SaleDao saleDao() {
    if (_saleDao != null) {
      return _saleDao;
    } else {
      synchronized(this) {
        if(_saleDao == null) {
          _saleDao = new SaleDao_Impl(this);
        }
        return _saleDao;
      }
    }
  }

  @Override
  public PurchaseDao purchaseDao() {
    if (_purchaseDao != null) {
      return _purchaseDao;
    } else {
      synchronized(this) {
        if(_purchaseDao == null) {
          _purchaseDao = new PurchaseDao_Impl(this);
        }
        return _purchaseDao;
      }
    }
  }

  @Override
  public StockAdjustmentDao stockAdjustmentDao() {
    if (_stockAdjustmentDao != null) {
      return _stockAdjustmentDao;
    } else {
      synchronized(this) {
        if(_stockAdjustmentDao == null) {
          _stockAdjustmentDao = new StockAdjustmentDao_Impl(this);
        }
        return _stockAdjustmentDao;
      }
    }
  }
}
