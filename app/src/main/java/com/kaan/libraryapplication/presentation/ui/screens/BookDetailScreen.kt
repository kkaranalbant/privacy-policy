package com.kaan.libraryapplication.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.kaan.libraryapplication.presentation.viewmodel.BookDetailUiState
import com.kaan.libraryapplication.presentation.viewmodel.BookDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: String,
    onBackClick: () -> Unit,
    viewModel: BookDetailViewModel = viewModel(factory = BookDetailViewModel.provideFactory(bookId))
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Details") },
                actions = {
                    IconButton(onClick = { /* Toggle Favorite Action */ }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is BookDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
                is BookDetailUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                is BookDetailUiState.Success -> {
                    val book = state.book
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        val reviews by viewModel.reviews.collectAsState()
                        val isFavorite by viewModel.isFavorite.collectAsState()
                        val isAdmin by viewModel.isAdmin.collectAsState()
                        var showReviewDialog by remember { mutableStateOf(false) }

                        // Top Section for Book Info
                        Column {
                             AsyncImage(
                                model = book.coverImageUrl ?: "https://via.placeholder.com/300",
                                contentDescription = book.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = book.title, style = MaterialTheme.typography.headlineSmall)
                                    Text(text = "by ${book.author}", style = MaterialTheme.typography.titleMedium)
                                }
                                IconButton(onClick = { viewModel.toggleFavorite() }) {
                                    Icon(
                                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Category: ${book.category}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Published: ${book.publishedYear}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = book.description, style = MaterialTheme.typography.bodyLarge)
                        }

                        Divider(modifier = Modifier.padding(vertical = 16.dp))

                        // Reviews Section
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                             Text(text = "Reviews", style = MaterialTheme.typography.titleLarge)
                             Button(onClick = { showReviewDialog = true }) {
                                 Text("Add Review")
                             }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        


                        // Top Section for Book Info
                        // ... (omitted, no change needed in top section)
                        
                        // Copying Top Section Logic implicitly since I am only targeting the Review Loop part in my mind, 
                        // BUT replace_file_content needs Context.
                        // I will target the Whole Review Section.

                        // ... (re-pasting Top Section because I can't easily skip lines in replacement without complex start/end)
                        // Actually I can target specific lines?
                        // "val reviews by viewModel.reviews.collectAsState()" is at line 71.
                        // "reviews.forEach" is around line 131.
                        
                        // I'll replace the review item rendering logic.
                        if (reviews.isEmpty()) {
                            Text("No reviews yet. Be the first!", style = MaterialTheme.typography.bodyMedium, color = androidx.compose.ui.graphics.Color.Gray)
                        } else {
                            reviews.forEach { review ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                            Text(text = review.userName, style = MaterialTheme.typography.labelLarge)
                                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                                Text(text = "Rating: ${review.rating}/5", style = MaterialTheme.typography.labelMedium)
                                                if (isAdmin) {
                                                    IconButton(onClick = { viewModel.deleteReview(review.id) }) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Delete Review", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                                    }
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = review.comment, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }

                        if (showReviewDialog) {
                            AddReviewDialog(
                                onDismiss = { showReviewDialog = false },
                                onSubmit = { rating, comment ->
                                    viewModel.addReview(rating, comment)
                                    showReviewDialog = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddReviewDialog(onDismiss: () -> Unit, onSubmit: (Int, String) -> Unit) {
    var rating by remember { androidx.compose.runtime.mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Write a Review") },
        text = {
            Column {
                Text("Rating: $rating")
                Slider(
                    value = rating.toFloat(),
                    onValueChange = { rating = it.toInt() },
                    valueRange = 1f..5f,
                    steps = 3
                )
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comment") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(rating, comment) }) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
