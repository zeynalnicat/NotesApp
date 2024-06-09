package com.example.notesapp.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notesapp.CustomCard
import com.example.notesapp.R
import com.example.notesapp.db.RoomDb
import com.example.notesapp.db.entites.NoteEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun AddNote(navController: NavController, noteId: Int) {
    val roomDb = RoomDb.getInstance(LocalContext.current)
    val title = remember {
        mutableStateOf("")
    }

    val content = remember {
        mutableStateOf("")
    }

    LaunchedEffect(noteId) {
        if (noteId != -1) {
            getNote(roomDb!!, noteId, title, content)
        } else {
            title.value = ""
            content.value = ""
        }
    }


    val snackbarHostState = remember { SnackbarHostState() }


    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.primary))
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomCard(icon = Icons.Filled.ArrowBackIosNew,
                        { navController.navigate("home") { popUpTo("home") { inclusive = true } } })

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CustomCard(
                            icon = Icons.Filled.Save,
                            {
                                if (noteId == -1) {
                                    save(
                                        roomDb = roomDb!!,
                                        navController = navController,
                                        title = title.value,
                                        content = content.value,
                                        snackbarHostState
                                    )
                                } else {
                                    update(
                                        roomDb!!,
                                        NoteEntity(noteId, title.value, content.value),
                                        snackbarHostState,
                                        navController
                                    )

                                }
                            })
                        if (noteId != -1) {
                            CustomCard(icon = Icons.Filled.Delete,{ delete(roomDb!!,navController,
                                NoteEntity(noteId,title.value,content.value)
                            ) })
                        }

                    }

                }

                Spacer(modifier = Modifier.height(30.dp))
                TransparentBorderTextField(textValue = title, placeHolder = "Title")
                Spacer(modifier = Modifier.height(20.dp))
                TransparentBorderTextField(
                    textValue = content,
                    placeHolder = "Type something...",
                    isTitle = false
                )
                ShowSnackbar(snackbarHostState = snackbarHostState)
            }

        }

    }
}

fun update(
    roomDb: RoomDb,
    noteEntity: NoteEntity,
    snackbarHostState: SnackbarHostState,
    navController: NavController
) {

    if (noteEntity.title.isEmpty() || noteEntity.content.isEmpty()) {

        CoroutineScope(Dispatchers.Main).launch {
            snackbarHostState.showSnackbar("Please fill all the fields")
        }


    } else {
        CoroutineScope(Dispatchers.IO).launch {
            roomDb.noteDao().update(noteEntity)
            navController.navigate("home") {
                popUpTo("home") {
                    inclusive = true
                }
            }

        }


    }

}


fun delete(roomDb: RoomDb,navController: NavController,noteEntity: NoteEntity) {
    CoroutineScope(Dispatchers.IO).launch {
        roomDb.noteDao().delete(noteEntity)

        CoroutineScope(Dispatchers.Main).launch {
            navController.navigate("home") {
                popUpTo("home") {
                    inclusive = true
                }
            }
        }
    }
}


fun save(
    roomDb: RoomDb,
    navController: NavController,
    title: String,
    content: String,
    snackbarHostState: SnackbarHostState
) {
    val dao = roomDb.noteDao()

    if (title.isEmpty() || content.isEmpty()) {

        CoroutineScope(Dispatchers.Main).launch {
            snackbarHostState.showSnackbar("Please fill all the fields")
        }

    } else {

        CoroutineScope(Dispatchers.IO).launch {
            val insert = dao.insert(NoteEntity(id = 0, title = title, content = content))
            if (insert != -1L) {
                CoroutineScope(Dispatchers.Main).launch {
                    navController.navigate("home") { popUpTo("home") { inclusive = true } }
                }
            } else {
                snackbarHostState.showSnackbar("Something went wrong")
            }

        }

    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransparentBorderTextField(
    textValue: MutableState<String>,
    placeHolder: String,
    isTitle: Boolean = true
) {

    OutlinedTextField(
        value = textValue.value,
        textStyle = TextStyle(
            fontSize = (if (isTitle) 35.sp else 20.sp)
        ),
        onValueChange = { textValue.value = it },
        placeholder = { Text(text = placeHolder, fontSize = (if (isTitle) 35.sp else 20.sp)) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedTextColor = Color.White,
            focusedBorderColor = Color.Transparent,
            unfocusedTextColor = Color.White,
            unfocusedBorderColor = Color.Transparent,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = colorResource(id = R.color.primary),
                shape = RectangleShape
            )
            .background(
                color = colorResource(id = R.color.primary),
                shape = RectangleShape
            )
            .padding(horizontal = 10.dp)
    )
}

fun getNote(
    roomDb: RoomDb,
    noteId: Int,
    title: MutableState<String>,
    content: MutableState<String>,
) {
    val noteDao = roomDb.noteDao()


    CoroutineScope(Dispatchers.IO).launch {
        val note = noteDao.getNote(noteId)
        title.value = note.title
        content.value = note.content
    }
}

@Composable
fun ShowSnackbar(snackbarHostState: SnackbarHostState) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(16.dp) // Adjust padding as needed
    ) { snackbarData ->
        Snackbar(snackbarData = snackbarData)
    }
}