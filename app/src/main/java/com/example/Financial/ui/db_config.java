package com.example.Financial.ui;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.Financial.R;

public class db_config extends SQLiteOpenHelper {
    private String mTableName = "tbName";
    private String mDatabaseName = "dbName";
    private Context mContext = null;

    public db_config(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public db_config(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public db_config(@Nullable Context context, @Nullable String name, int version, @NonNull SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "";
        String SQL = "";
        switch (mTableName){
            // setting
            case "Setting_IncomeItems":
                sql =   "CREATE TABLE IF NOT EXISTS " + mTableName +
                        "(_no INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "_item VARCHAR(32));";

                break;
            case "Setting_ExpensesItems":
                sql =   "CREATE TABLE IF NOT EXISTS " + mTableName +
                        "(_no INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "_item VARCHAR(32), " +
                        "_fixed BOOLEAN );";
                break;
            case "Setting_Account":
                sql =   "CREATE TABLE IF NOT EXISTS " + mTableName +
                        "(_id INTEGER PRIMARY KEY, " +
                        "_name VARCHAR(32), " +
                        "_application VARCHAR(32));";
                break;

            // cash flow
            case "Income":
                sql =   "CREATE TABLE IF NOT EXISTS " + mTableName +
                        "(_no INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "_date DATE, " +
                        "_item VARCHAR(32), " +
                        "_amount INTEGET, " +
                        "_remark CARCHAR(32));";
            case "Expenses":
                sql =   "CREATE TABLE IF NOT EXISTS " + mTableName +
                        "(_no INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "_date DATE, " +
                        "_item VARCHAR(32), " +
                        "_amount INTEGET, " +
                        "_method VARCHAR(32), " +
                        "_remark CARCHAR(32));";
                break;

            // Deposit
            case "Deposit_DemandDeposit":
                sql =   "CREATE TABLE IF NOT EXISTS " + mTableName +
                        "(_no INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "_date DATE, " +
                        "_account VARCHAR(32), " +
                        "_amount INTEGER, " +
                        "_interest FLOAT);";
                break;
            case "Deposit_TimeDeposit":
                sql =   "CREATE TABLE IF NOT EXISTS " + mTableName +
                        "(_no INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "_date DATE, " +
                        "_account VARCHAR(32), " +
                        "_amount INTEGER, " +
                        "_interest FLOAT, " +
                        "_period INTEGER, " +
                        "_method VARCHAR(32));";
                break;

            // Exchange
            case "Exchange_Cash":
                sql =   "CREATE TABLE IF NOT EXISTS " + mTableName +
                        "(_no INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "_date DATE, " +
                        "_account VARCHAR(32), " +
                        "_currency VARCHAR(32), " +
                        "_amount INTEGER, " +
                        "_rate FLOAT);";
                break;
            case "Exchange_Spot_DemandDeposit":
                sql =   "CREATE TABLE IF NOT EXISTS " + mTableName +
                        "(_no INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "_date DATE, " +
                        "_account VARCHAR(32), " +
                        "_currency VARCHAR(32), " +
                        "_amount INTEGER, " +
                        "_rate FLOAT, " +
                        "_interest FLOAT);";
                break;
            case "Exchange_Spot_TimeDeposit":
                sql =   "CREATE TABLE IF NOT EXISTS " + mTableName +
                        "(_no INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "_date DATE, " +
                        "_account VARCHAR(32), " +
                        "_currency VARCHAR(32), " +
                        "_amount INTEGER, " +
                        "_rate FLOAT, " +
                        "_interest FLOAT, " +
                        "_period INTEGER, " +
                        "_method VARCHAR(32));";
                break;

            // Stock
            case "Stock":
                sql =   "CREATE TABLE IF NOT EXISTS " + mTableName +
                        "(_no INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "_date DATE, " +
                        "_companyCode VARCHAR(32), " +
                        "_companyNode VARCHAR(32), " +
                        "_shares INTEGER, " +
                        "_price FLOAT)";
                break;
            case "Fund":
                break;
            case "Bond":
                break;
            case "AccountReceivable":
                break;
            case "Account Payable":
                break;
        }

        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
