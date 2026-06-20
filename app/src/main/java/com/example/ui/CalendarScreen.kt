package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SocialPost
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: MainViewModel,
    onNavigateToEditor: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val posts by viewModel.posts.collectAsState()
    val context = LocalContext.current
    
    // Formatting helper
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy • h:mm a", Locale("es", "ES")) }

    // Filter state
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "PENDING", "COMPLETED"

    // Filtered posts sorted by scheduled date
    val filteredPosts = remember(posts, selectedFilter) {
        val sorted = posts.sortedBy { it.scheduledTime }
        when (selectedFilter) {
            "PENDING" -> sorted.filter { it.status != "PUBLISHED" }
            "COMPLETED" -> sorted.filter { it.status == "PUBLISHED" }
            else -> sorted
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("calendar_screen"),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    onNavigateToEditor(System.currentTimeMillis())
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Programar Post")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Title Header ---
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = "Planificador de Tareas",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-1).sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Checklist de cumplimiento para publicaciones de tus redes sociales",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // --- Stats Summary Banner ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    val totalCount = posts.size
                    val completedCount = posts.count { it.status == "PUBLISHED" }
                    val percent = if (totalCount > 0) (completedCount.toFloat() / totalCount.toFloat() * 100f).toInt() else 0

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Progreso de Cumplimiento",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$completedCount de $totalCount completados ($percent%)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$percent%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // --- Tabs Filter Bar ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("Todas (${posts.size})") }
                    )
                    FilterChip(
                        selected = selectedFilter == "PENDING",
                        onClick = { selectedFilter = "PENDING" },
                        label = { Text("Pendientes (${posts.count { it.status != "PUBLISHED" }})") }
                    )
                    FilterChip(
                        selected = selectedFilter == "COMPLETED",
                        onClick = { selectedFilter = "COMPLETED" },
                        label = { Text("Cumplidas (${posts.count { it.status == "PUBLISHED" }})") }
                    )
                }
            }

            // --- Interactive Compliance List ---
            if (filteredPosts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FactCheck,
                                contentDescription = null,
                                modifier = Modifier.size(44.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Sin publicaciones en este bloque",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "No existen publicaciones planificadas que coincidan con el filtro de cumplimiento.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredPosts, key = { it.id }) { post ->
                    val isDone = post.status == "PUBLISHED"
                    
                    // Deduce publication type dynamically
                    val pubType = when {
                        post.platforms.contains("youtube", ignoreCase = true) -> "Video Largo de YouTube"
                        post.content.contains("GUIÓN", ignoreCase = true) || post.content.contains("REELS_SCRIPT", ignoreCase = true) -> "Video Short / Reel"
                        post.content.contains("PORTADA", ignoreCase = true) || post.content.contains("CAROUSEL_COVER", ignoreCase = true) -> "Carrusel Visual de 7 Diapositivas"
                        post.content.contains("FB_HOOK", ignoreCase = true) || post.title.contains("FB:", ignoreCase = true) -> "Post Envolvente de Facebook"
                        else -> "Propuesta para Redacción"
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("compliance_item_${post.id}")
                            .clickable {
                                // clicking the post itself opens editing modal/creator tab
                                viewModel.editingPost = post
                                viewModel.selectedTab = 2 // Nav to Editor
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDone) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isDone) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Compliance Checklist Checkbox
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isDone) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable {
                                        // Toggle compliance fulfillment
                                        val newStatus = if (isDone) "APPROVED" else "PUBLISHED"
                                        viewModel.savePost(post.copy(status = newStatus))
                                        
                                        val feedTask = if (newStatus == "PUBLISHED") "¡Consistencia cumplida!" else "Planificación pendiente"
                                        Toast.makeText(context, "${post.title}: $feedTask", Toast.LENGTH_SHORT).show()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = if (isDone) "Completado" else "Pendiente",
                                    tint = if (isDone) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Meta-Details Columns
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = post.title.ifEmpty { "Publicación sin título" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isDone) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                                    textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                // Date & Time detail (fecha y hora)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = dateFormatter.format(Date(post.scheduledTime)),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Publication Type (tipo de publicación)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = pubType,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // Destination Platforms (plataforma de destino)
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Draw badge for each target platform
                                val platformList = post.platforms.split(",").filter { it.isNotEmpty() }
                                platformList.forEach { plat ->
                                    val (color, label) = when (plat.trim().lowercase()) {
                                        "facebook" -> Pair(Color(0xFF1877F2), "Facebook")
                                        "instagram" -> Pair(Color(0xFFE1306C), "Instagram")
                                        "tiktok" -> Pair(Color(0xFF000000), "TikTok")
                                        "youtube" -> Pair(Color(0xFFFF0000), "YouTube")
                                        "whatsapp", "watsap" -> Pair(Color(0xFF25D366), "Watsap")
                                        else -> Pair(MaterialTheme.colorScheme.secondary, plat.replaceFirstChar { it.uppercase() })
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(color.copy(alpha = 0.1f))
                                            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = label.uppercase(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = color
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Margin spacing helper
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}
