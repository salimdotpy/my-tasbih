package com.salimtech.mytasbih

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns


class DBHelper(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "TasbihDB"
        private val TABLE_NAME = "mytasbih"
        private val KEY_SETTINGS = "settings"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val newtb = ("CREATE TABLE " + TABLE_NAME + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_SETTINGS + " TEXT" + ")")

//        "create table "+TABLE_NAME+" (id integer primary key, name text)"
        db?.execSQL(newtb)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun insertData(settings: String?): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("settings", settings)
        db.insert(TABLE_NAME, null, contentValues)
        return true
    }

    fun getData(id: Int): String {
        val db = this.readableDatabase
        val cursor = db.rawQuery("select * from $TABLE_NAME where id=$id", null)
        val eSETTINGS = cursor.getColumnIndex(KEY_SETTINGS)

        if (cursor.moveToFirst()) {
            return cursor.getString(eSETTINGS)
        }
        return ""
    }

    fun getDatas(): String {
        val db = this.readableDatabase
        val cursor = db.rawQuery("select * from $TABLE_NAME", null)
        var res = ""
        val eSETTINGS = cursor.getColumnIndex(KEY_SETTINGS)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            res += """
            ${cursor.getString(eSETTINGS)}
            """.trimIndent()
            cursor.moveToNext()
        }
        db.close()
        return res
    }

    fun updateData(id: Int?, settings: String?): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("settings", settings)
        db.update(
            TABLE_NAME, contentValues, "id = ? ", arrayOf(
                Integer.toString(
                    id!!
                )
            )
        )
        return true
    }

    fun deleteData(id: Int?): Boolean {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "id = ? ", arrayOf(Integer.toString(id!!)))
        return true
    }
}