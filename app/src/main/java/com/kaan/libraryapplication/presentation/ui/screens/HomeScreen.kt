package com.kaan.libraryapplication.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.kaan.libraryapplication.data.local.db.entities.BookEntity
import com.kaan.libraryapplication.presentation.viewmodel.BooksViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onBookClick: (String) -> Unit,
    viewModel: BooksViewModel = viewModel(factory = BooksViewModel.Factory)
) {
    val books by viewModel.allBooks.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Library Books") })
        },
        floatingActionButton = {
           // Add book action if needed
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Connectivity / Filter Section
            var searchText by remember { mutableStateOf("") }
            val context = androidx.compose.ui.platform.LocalContext.current
            val isBluetoothEnabled = remember { 
                com.kaan.libraryapplication.connectivity.ConnectivityManager(context).isBluetoothEnabled()
            }

            OutlinedTextField(
                value = searchText,
                onValueChange = { 
                    searchText = it 
                    // Search logic triggered here via ViewModel (omitted for brevity, just UI demo)
                },
                label = { Text("Search Books") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                prefix = { Icon(Icons.Default.Search, contentDescription = "Search") }
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                 AssistChip(
                    onClick = { /* Filter logic */ },
                    label = { Text("Category: All") }
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isBluetoothEnabled) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = "Bluetooth Status",
                        tint = if (isBluetoothEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBluetoothEnabled) "BLE On" else "BLE Off", style = MaterialTheme.typography.labelSmall)
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Initial filter logic (client side for demo)
                val filteredBooks = books.filter { it.title.contains(searchText, ignoreCase = true) }
                items(filteredBooks) { book ->
                    BookItem(book = book, onClick = { onBookClick(book.bookId) })
                }
            }
        }
    }
}

@Composable
fun BookItem(book: BookEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = book.coverImageUrl ?: "https://via.placeholder.com/100",
                contentDescription = book.title,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )
            Column {
                Text(text = book.title, style = MaterialTheme.typography.titleMedium)
                Text(text = book.author, style = MaterialTheme.typography.bodyMedium)
                Text(text = book.category, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
