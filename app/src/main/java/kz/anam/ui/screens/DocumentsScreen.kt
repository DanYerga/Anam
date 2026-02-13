package kz.anam.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import kz.anam.data.models.Document
import kz.anam.data.models.DocumentType
import kz.anam.data.models.displayName
import kz.anam.ui.theme.*
import kz.anam.viewmodels.DocumentsViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Экран документов (анализы, УЗИ, справки)
 * Путь: app/src/main/java/kz/anam/ui/screens/DocumentsScreen.kt
 *
 * ОБНОВЛЁН: Использует DocumentType вместо DocumentCategory
 */

@Composable
fun DocumentsScreen(viewModel: DocumentsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showTypePicker by remember { mutableStateOf(false) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.addDocument(it) }
    }

    Box(modifier = Modifier.fillMaxSize().background(PureWhite)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(DeepEggplant, RoyalViolet)))
                    .padding(horizontal = 28.dp, vertical = 32.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📄", style = MaterialTheme.typography.displayMedium)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Documents", style = MaterialTheme.typography.displaySmall, color = PureWhite)
                            Text("Tests, ultrasound, medical reports", style = MaterialTheme.typography.bodyMedium, color = PureWhite.copy(alpha = 0.7f))
                        }
                    }
                }
            }

            // Filter chips
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 28.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedType == null,
                        onClick = { viewModel.filterByType(null) },
                        label = { Text("All") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoyalViolet,
                            selectedLabelColor = PureWhite
                        )
                    )
                }
                DocumentType.values().forEach { type ->
                    item {
                        FilterChip(
                            selected = uiState.selectedType == type,
                            onClick = { viewModel.filterByType(type) },
                            label = { Text(type.displayName()) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoyalViolet,
                                selectedLabelColor = PureWhite
                            )
                        )
                    }
                }
            }

            // Documents list
            if (uiState.documents.isEmpty()) {
                EmptyDocumentsState(onAddClick = { filePicker.launch("*/*") })
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.documents) { document ->
                        DocumentCard(
                            document = document,
                            onDelete = { viewModel.deleteDocument(document.id) }
                        )
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { showTypePicker = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(28.dp),
            containerColor = RoyalViolet
        ) {
            Icon(Icons.Default.Add, "Add", tint = PureWhite)
        }

        if (showTypePicker) {
            TypePickerDialog(
                onTypeSelected = { type ->
                    viewModel.setUploadType(type)
                    showTypePicker = false
                    filePicker.launch("*/*")
                },
                onDismiss = { showTypePicker = false }
            )
        }
    }
}

@Composable
fun EmptyDocumentsState(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📂", style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("No documents", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Add test results, ultrasound results or medical reports", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(containerColor = RoyalViolet),
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(Icons.Default.Add, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add a document")
        }
    }
}

@Composable
fun DocumentCard(document: Document, onDelete: () -> Unit) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LightSurface)
            .clickable { }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = when (document.documentType) {
                        DocumentType.BLOOD_TEST, DocumentType.URINE_TEST -> Error.copy(alpha = 0.1f)
                        DocumentType.ULTRASOUND -> ElectricViolet.copy(alpha = 0.1f)
                        DocumentType.MEDICAL_REPORT, DocumentType.PRESCRIPTION -> Success.copy(alpha = 0.1f)
                        else -> RoyalViolet.copy(alpha = 0.1f)
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            when (document.documentType) {
                                DocumentType.BLOOD_TEST, DocumentType.URINE_TEST -> Icons.Default.Science
                                DocumentType.ULTRASOUND -> Icons.Default.Favorite
                                DocumentType.MEDICAL_REPORT, DocumentType.PRESCRIPTION -> Icons.Default.Description
                                else -> Icons.Default.InsertDriveFile
                            },
                            contentDescription = null,
                            tint = when (document.documentType) {
                                DocumentType.BLOOD_TEST, DocumentType.URINE_TEST -> Error
                                DocumentType.ULTRASOUND -> ElectricViolet
                                DocumentType.MEDICAL_REPORT, DocumentType.PRESCRIPTION -> Success
                                else -> RoyalViolet
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(document.title, style = MaterialTheme.typography.titleSmall, color = TextPrimary, maxLines = 1)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(document.documentType.displayName(), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text(
                        SimpleDateFormat("dd MMM yyyy", Locale("ru")).format(Date(document.uploadDate)),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
            }

            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Error)
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete document?") },
            text = { Text("This action can not be cancelled") },
            confirmButton = {
                Button(
                    onClick = { onDelete(); showDeleteConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TypePickerDialog(onTypeSelected: (DocumentType) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose the document type") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DocumentType.values().forEach { type ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onTypeSelected(type) },
                        color = LightSurface
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                when (type) {
                                    DocumentType.BLOOD_TEST, DocumentType.URINE_TEST -> Icons.Default.Science
                                    DocumentType.ULTRASOUND -> Icons.Default.Favorite
                                    DocumentType.MEDICAL_REPORT, DocumentType.PRESCRIPTION -> Icons.Default.Description
                                    else -> Icons.Default.InsertDriveFile
                                },
                                contentDescription = null,
                                tint = RoyalViolet
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(type.displayName())
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}