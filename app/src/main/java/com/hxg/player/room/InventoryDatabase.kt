package com.hxg.player.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * 将 AudioInfo 指定为包含 entities 列表的唯一类。
 * 将 version 设为 1。每当您更改数据库表的架构时，都必须提升版本号。
 * 将 exportSchema 设为 false，这样就不会保留架构版本记录的备份。
 */
@Database(entities = [AudioInfo::class], version = 1, exportSchema = false)
abstract class InventoryDatabase : RoomDatabase() {
    abstract fun audioInfoDao(): AudioInfoDao

    companion object {
        @Volatile
        private var INSTANCE: InventoryDatabase? = null

        fun getDatabase(context: Context): InventoryDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    InventoryDatabase::class.java,
                    "inventory_database"
                ).fallbackToDestructiveMigration().build().also {
                    INSTANCE = it
                }
            }
        }
    }
}
