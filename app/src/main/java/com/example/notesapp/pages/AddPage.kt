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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.notesapp.db.dao.NoteDao
import com.example.notesapp.db.entites.NoteEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun AddNote(navController: NavController,noteId: Int) {
    val roomDb = RoomDb.getInstance(LocalContext.current)
    val title = remember {
        mutableStateOf("")
    }

    val content = remember {
        mutableStateOf("")
    }

    LaunchedEffect(noteId) {
        if(noteId!=-1){
            val note = roomDb?.noteDao()?.getNote(noteId)
            title.value = note?.title ?: ""
            content.value = note?.content ?: ""
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomCard(icon = Icons.Filled.ArrowBackIosNew,
                        { navController.navigate("home") { popUpTo("home") { inclusive = true } } })

                    CustomCard(icon = Icons.Filled.Save, {save(roomDb = roomDb!!, navController = navController, title = title.value, content = content.value, snackbarHostState)})
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


fun save(roomDb: RoomDb ,navController: NavController,title:String,content:String,snackbarHostState:SnackbarHostState){
    val dao = roomDb.noteDao()

    if (title.isEmpty() || content.isEmpty()) {

        CoroutineScope(Dispatchers.Main).launch {
            snackbarHostState.showSnackbar("Please fill all the fields")
        }

    } else {

        CoroutineScope(Dispatchers.IO).launch {
            val insert = dao.insert(NoteEntity(id=0,title = title, content = content))
            if(insert!=-1L){
                CoroutineScope(Dispatchers.Main).launch {
                    navController.navigate("home") { popUpTo("home") { inclusive = true } }
                }
            }else{
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

@Composable
fun ShowSnackbar(snackbarHostState: SnackbarHostState) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(16.dp) // Adjust padding as needed
    ) { snackbarData ->
        Snackbar(snackbarData = snackbarData)
    }
}