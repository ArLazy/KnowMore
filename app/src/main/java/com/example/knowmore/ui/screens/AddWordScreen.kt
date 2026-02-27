package com.example.knowmore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.knowmore.ui.viewmodel.WordListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordScreen(
    viewModel: WordListViewModel,
    onNavigateBack: () -> Unit
) {
    var originalWord by remember { mutableStateOf("") }
    var translatedWord by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General") }
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Word") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = originalWord,
                onValueChange = {
                    originalWord = it
                    showError = false
                },
                label = { Text("Original Word") },
                modifier = Modifier.fillMaxWidth(),
                isError = showError && originalWord.isBlank(),
                singleLine = true
            )

            OutlinedTextField(
                value = translatedWord,
                onValueChange = {
                    translatedWord = it
                    showError = false
                },
                label = { Text("Translation") },
                modifier = Modifier.fillMaxWidth(),
                isError = showError && translatedWord.isBlank(),
                singleLine = true
            )

            OutlinedTextField(
                value = language,
                onValueChange = {
                    language = it
                    showError = false
                },
                label = { Text("Language (e.g., Spanish, French)") },
                modifier = Modifier.fillMaxWidth(),
                isError = showError && language.isBlank(),
                singleLine = true
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (originalWord.isBlank() || translatedWord.isBlank() || language.isBlank()) {
                        showError = true
                    } else {
                        viewModel.addWord(
                            originalWord = originalWord.trim(),
                            translatedWord = translatedWord.trim(),
                            language = language.trim(),
                            category = category.trim().ifBlank { "General" }
                        )
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Word")
            }
        }
    }
}
