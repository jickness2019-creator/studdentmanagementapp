package com.example.studentmanagementapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class StudentViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var dbHelper: StudentDatabaseHelper

    private lateinit var viewModel: StudentViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = StudentViewModel(dbHelper)
    }

    @Test
    fun `test add student`() {
        val student = Student(name = "John Doe", age = 25)
        viewModel.addStudent(student)

        verify(dbHelper).addStudent(student)
        verify(dbHelper).getAllStudents()
    }

    @Test
    fun `test update student`() {
        val student = Student(id = 1, name = "John Doe", age = 26)
        viewModel.updateStudent(student)

        verify(dbHelper).updateStudent(student)
        verify(dbHelper).getAllStudents()
    }

    @Test
    fun `test delete student`() {
        val student = Student(id = 1, name = "John Doe", age = 26)
        viewModel.deleteStudent(student)

        verify(dbHelper).deleteStudent(student)
        verify(dbHelper).getAllStudents()
    }
}