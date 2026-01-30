package com.example.studentmanagementapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StudentViewModel(private val dbHelper: StudentDatabaseHelper) : ViewModel() {

    private val _students = MutableLiveData<List<Student>>()
    val students: LiveData<List<Student>> = _students

    fun loadStudents() {
        _students.value = dbHelper.getAllStudents()
    }

    fun addStudent(student: Student) {
        dbHelper.addStudent(student)
        loadStudents()
    }

    fun updateStudent(student: Student) {
        dbHelper.updateStudent(student)
        loadStudents()
    }

    fun deleteStudent(student: Student) {
        dbHelper.deleteStudent(student)
        loadStudents()
    }
}