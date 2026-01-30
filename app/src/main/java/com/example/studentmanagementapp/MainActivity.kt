package com.example.studentmanagementapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentmanagementapp.ui.theme.StudentManagementAppTheme

data class Student(val id: Long = 0, val name: String, val age: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudentManagementAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val dbHelper = StudentDatabaseHelper(context)
                    val viewModel: StudentViewModel = viewModel(factory = StudentViewModelFactory(dbHelper))
                    StudentScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun StudentScreen(viewModel: StudentViewModel) {
    val studentList by viewModel.students.observeAsState(initial = emptyList())
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadStudents()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                selectedStudent = null
                setShowDialog(true)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Student")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(studentList) { student ->
                StudentListItem(
                    student = student,
                    onEdit = {
                        selectedStudent = student
                        setShowDialog(true)
                    },
                    onDelete = { viewModel.deleteStudent(student) }
                )
            }
        }
    }

    if (showDialog) {
        AddEditStudentDialog(
            student = selectedStudent,
            onDismiss = { setShowDialog(false) },
            onSave = { student ->
                if (selectedStudent == null) {
                    viewModel.addStudent(student)
                } else {
                    viewModel.updateStudent(student)
                }
                setShowDialog(false)
            }
        )
    }
}

@Composable
fun StudentListItem(student: Student, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Name: ${student.name}, Age: ${student.age}")
        Row {
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditStudentDialog(
    student: Student?,
    onDismiss: () -> Unit,
    onSave: (Student) -> Unit
) {
    var name by remember { mutableStateOf(student?.name ?: "") }
    var age by remember { mutableStateOf(student?.age?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (student == null) "Add Student" else "Edit Student") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val ageInt = age.toIntOrNull() ?: 0
                val newStudent = student?.copy(name = name, age = ageInt) ?: Student(name = name, age = ageInt)
                onSave(newStudent)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
