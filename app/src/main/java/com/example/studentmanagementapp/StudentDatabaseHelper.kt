package com.example.studentmanagementapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class StudentDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "student.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_STUDENTS = "students"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_AGE = "age"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " + TABLE_STUDENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_AGE + " INTEGER" + ")")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_STUDENTS")
        onCreate(db)
    }

    fun addStudent(student: Student): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NAME, student.name)
            put(KEY_AGE, student.age)
        }
        val id = db.insert(TABLE_STUDENTS, null, values)
        db.close()
        return id
    }

    fun getAllStudents(): List<Student> {
        val studentList = mutableListOf<Student>()
        val selectQuery = "SELECT * FROM $TABLE_STUDENTS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val student = Student(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                    age = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_AGE))
                )
                studentList.add(student)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return studentList
    }

    fun updateStudent(student: Student): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NAME, student.name)
            put(KEY_AGE, student.age)
        }
        val rows = db.update(
            TABLE_STUDENTS,
            values,
            "$KEY_ID = ?",
            arrayOf(student.id.toString())
        )
        db.close()
        return rows
    }

    fun deleteStudent(student: Student) {
        val db = this.writableDatabase
        db.delete(
            TABLE_STUDENTS,
            "$KEY_ID = ?",
            arrayOf(student.id.toString())
        )
        db.close()
    }
}