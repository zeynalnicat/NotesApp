package com.example.notesapp


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notesapp.db.RoomDb
import com.example.notesapp.db.entites.NoteEntity
import com.example.notesapp.pages.AddNote
import com.example.notesapp.ui.theme.NotesAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NotesAppTheme {
                NavigationHost(navController)
            }
        }
    }
}


@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Home(navController)
        }

        composable("add/{note}") { backStackEntry ->
            val note = backStackEntry.arguments?.getString("note") ?: "-1"
            AddNote(navController, note.toInt())


        }
    }
}

@Composable
fun Home(navController: NavHostController) {
    val notes = remember { mutableStateOf<List<NoteEntity>>(emptyList()) }
    val dao = RoomDb.getInstance(LocalContext.current)?.noteDao()
    val search = remember {
        mutableStateOf("")
    }
    val listColors = listOf(0xFFFD99FF, 0xFFFF9E9E, 0xFF91F48F, 0xFFFFF599, 0xFF9EFFFF, 0xFFB69CFF)
    val isSet = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        notes.value = dao?.getAllNotes() ?: emptyList()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.size(width = 70.dp, height = 70.dp),
                onClick = { navController.navigate("add/${-1}") },
                shape = RoundedCornerShape(35.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 14.dp),
                containerColor = colorResource(id = R.color.primary),
                contentColor = Color.White,
                content = {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "ADD")

                }

            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.primary))
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                if (!isSet.value) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Notes", fontSize = 30.sp, color = Color.White)
                        CustomCard(icon = Icons.Filled.Search, { isSet.value = !isSet.value })
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            shape = RoundedCornerShape(20.dp),
                            placeholder = { Text(text = "Search in notes") },
                            value = search.value,
                            onValueChange = { search.value = it },

                            )
                        CustomCard(icon = Icons.Filled.Search, { isSet.value = !isSet.value })
                    }

                }


                if (notes.value.isEmpty()) {
                    Column(

                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.rafiki),
                            contentScale = ContentScale.FillWidth,
                            contentDescription = "No notes",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Create your first note!",
                            fontSize = 16.sp,
                            color = Color.White
                        )


                    }
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(notes.value.size) { index ->
                            val color = listColors[index % listColors.size]
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clickable {
                                        navController.navigate("add/${notes.value[index].id}")
                                    }
                                    ,
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(color = color))
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = notes.value[index].title, fontSize = 20.sp, fontWeight = FontWeight(900))
                                }

                            }
                        }
                    }
                }


            }
        }
    }
}

@Composable
fun CustomCard(icon: ImageVector, action: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.secondary))
    ) {
        Button(
            onClick = { action() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Icon(imageVector = icon, contentDescription = "Search")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NotesAppTheme {
//        Home()
    }
}