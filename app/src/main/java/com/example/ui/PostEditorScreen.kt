package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.ui.draw.scale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SocialPost
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostEditorScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val editingPost = viewModel.editingPost

    // Editor Form States
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedPlatforms by remember { mutableStateOf(setOf<String>()) }
    var scheduledTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var status by remember { mutableStateOf("DRAFT") }
    var isEvergreen by remember { mutableStateOf(false) }

    // AI Creator States
    var promptQuery by remember { mutableStateOf("") }
    var selectedTone by remember { mutableStateOf("Profesional") }
    var selectedAIPlatform by remember { mutableStateOf("Instagram") }

    // Listen to changes in the active post being edited
    LaunchedEffect(editingPost) {
        if (editingPost != null) {
            title = editingPost.title
            content = editingPost.content
            status = editingPost.status
            scheduledTime = editingPost.scheduledTime
            isEvergreen = editingPost.isEvergreen
            selectedPlatforms = editingPost.platforms.split(",").filter { it.isNotEmpty() }.toSet()
        } else {
            // Defaults for new post
            title = ""
            content = ""
            status = "DRAFT"
            scheduledTime = System.currentTimeMillis() + (4 * 3600000) // Default 4 hours ahead
            isEvergreen = false
            selectedPlatforms = setOf("instagram")
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("post_editor_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Title Header ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (editingPost != null) "Editar Publicación" else "Nueva Publicación",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (editingPost != null) {
                    TextButton(onClick = { viewModel.editingPost = null }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Crear Nuevo")
                    }
                }
            }
        }

        // --- PLATFORM PUBLISHING OPTIONS SHORTCUTS ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Accesos a Opciones de Publicación por App",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Configura de inmediato el estudio con formato y reglas óptimas por red social:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // FB Choice
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPlatforms = setOf("facebook")
                                    selectedAIPlatform = "Facebook"
                                    promptQuery = "Fórmula para Facebook (Nicho '${viewModel.activeNiche}'): frase de gancho de 15 palabras entre comillas insertada, con descripción motivadora e incertidumbre/motivación al final y 4 hashtags."
                                    title = "FB: Imagen de Frase con Copi Envolvente"
                                    Toast.makeText(context, "Configurado: Para publicar en Facebook", Toast.LENGTH_SHORT).show()
                                }
                                .testTag("sh_fb"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1877F2).copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = Color(0xFF1877F2), modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "Para publicar en Facebook",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // IG Choice
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPlatforms = setOf("instagram")
                                    selectedAIPlatform = "Instagram"
                                    promptQuery = "Diseñar carrusel de Instagram para ayudar a resolver un problema clave sobre '${viewModel.activeNiche}': portada enganchadora, con descripción persuasiva."
                                    title = "IG: Carrusel de 7 Filas"
                                    Toast.makeText(context, "Configurado: Para publicar en Instagram", Toast.LENGTH_SHORT).show()
                                }
                                .testTag("sh_ig"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE1306C).copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFFE1306C), modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "Para publicar en Instagram",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // TikTok Choice
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPlatforms = setOf("tiktok")
                                    selectedAIPlatform = "TikTok"
                                    promptQuery = "Generar Reel/TikTok para '${viewModel.activeNiche}': con un guion enganchador planteando cuestión capciosa de audio y afirmando tener la solución en la descripción."
                                    title = "TikTok: Guion de Reel Resolutor"
                                    Toast.makeText(context, "Configurado: Para publicar en TikTok", Toast.LENGTH_SHORT).show()
                                }
                                .testTag("sh_tiktok"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "Para publicar en tik tok",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // YouTube Choice
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPlatforms = setOf("youtube")
                                    selectedAIPlatform = "LinkedIn" // leverage existing system
                                    promptQuery = "Crear propuesta de video largo para YouTube del nicho '${viewModel.activeNiche}': título atractivo en tercera persona del singular, gancho inicial fuerte y desarrollo de narrativa."
                                    title = "YouTube: Video Largo Narrativo"
                                    Toast.makeText(context, "Configurado: Para publicar en YouTube", Toast.LENGTH_SHORT).show()
                                }
                                .testTag("sh_yt"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFF0000).copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Slideshow, contentDescription = null, tint = Color(0xFFFF0000), modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "Para publicar en youtube",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // --- Core Editor Card ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Title Input
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Tema o Título Interno") },
                        modifier = Modifier.fillMaxWidth().testTag("editor_title_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )

                    // Platform Selection Chips
                    Text(
                        text = "Plataformas de Publicación",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    val availablePlatforms = listOf(
                        Pair("facebook", "Facebook"),
                        Pair("instagram", "Instagram"),
                        Pair("tiktok", "TikTok"),
                        Pair("youtube", "YouTube")
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        availablePlatforms.forEach { (id, name) ->
                            val isSelected = selectedPlatforms.contains(id)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedPlatforms = if (isSelected) {
                                        selectedPlatforms - id
                                    } else {
                                        selectedPlatforms + id
                                    }
                                },
                                label = { Text(name) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when (id) {
                                            "facebook" -> Icons.Default.Public
                                            "instagram" -> Icons.Default.CameraAlt
                                            "tiktok" -> Icons.Default.PlayCircle
                                            "youtube" -> Icons.Default.Slideshow
                                            else -> Icons.Default.Public
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                modifier = Modifier.testTag("chip_$id")
                            )
                        }
                    }

                    // Content Input Area (Adaptive character limits indicators)
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Mensaje / Caption Copie de Publicación") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("editor_content_input"),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )

                    // Platform Adaptive Content Limit Verification
                    val charLength = content.length
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        selectedPlatforms.forEach { platformId ->
                            val limit = when (platformId) {
                                "facebook" -> 2000
                                "tiktok" -> 1500
                                "instagram" -> 2200
                                "youtube" -> 5000
                                else -> 2000
                            }
                            
                            val isExceeded = charLength > limit
                            val progress = (charLength.toFloat() / limit).coerceIn(0f, 1f)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${platformId.uppercase()}: $charLength / $limit caracteres",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isExceeded) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (isExceeded) {
                                    Text(
                                        text = "⚠️ ¡Excede límite!",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Red
                                    )
                                }
                            }
                            
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                color = if (isExceeded) Color.Red else MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }

                    // Time and Date Plan Picker Simulation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Fecha de Lanzamiento",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val formatter = SimpleDateFormat("EEEE d 'de' MMMM 'a las' hh:mm a", Locale.getDefault())
                            Text(
                                text = formatter.format(Date(scheduledTime)),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        // Button optimizer
                        FilledTonalButton(
                            onClick = {
                                val cal = Calendar.getInstance()
                                cal.timeInMillis = scheduledTime
                                cal.set(Calendar.HOUR_OF_DAY, 18)
                                cal.set(Calendar.MINUTE, 0)
                                cal.set(Calendar.SECOND, 0)
                                scheduledTime = cal.timeInMillis
                                Toast.makeText(context, "Hora optimizada para mayor engagement (18:00 hrs)", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp).testTag("optimize_time_btn")
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Optimizar Hora", fontSize = 11.sp)
                        }
                    }

                    // Workflow Dropdown Selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Avanzar Estado Workflow:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            val states = listOf("DRAFT", "REVIEW", "APPROVED")
                            states.forEach { st ->
                                val active = status == st
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                        .border(1.dp, if (active) Color.Transparent else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                        .clickable { status = st }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                        .testTag("wf_$st"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = st,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Evergreen Queue Switch Option
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.QueuePlayNext, contentDescription = null, modifier = Modifier.size(16.dp))
                            Column {
                                Text(
                                    text = "Contenido Evergreen (Reutilizable)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Auto-republicar periódicamente en colas vacías.",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = isEvergreen,
                            onCheckedChange = { isEvergreen = it },
                            modifier = Modifier.scale(0.85f).testTag("evergreen_toggle")
                        )
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (editingPost != null) {
                            IconButton(
                                onClick = {
                                    viewModel.deletePost(editingPost)
                                    Toast.makeText(context, "Publicación eliminada", Toast.LENGTH_SHORT).show()
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                ),
                                modifier = Modifier.size(48.dp).testTag("delete_post_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar Post")
                            }
                        }

                        Button(
                            onClick = {
                                if (title.isBlank()) {
                                    Toast.makeText(context, "Por favor indica un tema o título.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (selectedPlatforms.isEmpty()) {
                                    Toast.makeText(context, "Elige al menos una plataforma de publicación.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val savedPost = SocialPost(
                                    id = editingPost?.id ?: 0,
                                    title = title,
                                    content = content,
                                    scheduledTime = scheduledTime,
                                    platforms = selectedPlatforms.joinToString(","),
                                    status = status,
                                    isEvergreen = isEvergreen,
                                    clicks = editingPost?.clicks ?: 0,
                                    likes = editingPost?.likes ?: 0,
                                    comments = editingPost?.comments ?: 0,
                                    shares = editingPost?.shares ?: 0
                                )

                                viewModel.savePost(savedPost)
                                Toast.makeText(context, "Publicación guardada exitosamente", Toast.LENGTH_SHORT).show()
                                
                                // Reset form
                                viewModel.editingPost = null
                                viewModel.selectedTab = 1 // Navigate back to calendar list view
                            },
                            modifier = Modifier.weight(1f).height(48.dp).testTag("save_post_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Guardar Cambios Plan")
                        }
                    }
                }
            }
        }

        // --- Advanced Gemini AI Content Copie Assistant ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Estudio de Creación IA (Gemini)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Optimiza tus textos con inteligencia artificial",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Prompt field
                    OutlinedTextField(
                        value = promptQuery,
                        onValueChange = { promptQuery = it },
                        placeholder = { Text("Escribe una idea, tema o slogan (ej: '5 hacks para marketing')") },
                        modifier = Modifier.fillMaxWidth().testTag("ai_prompt_input"),
                        singleLine = true
                    )

                    // Platform Target Filter inside generation configuration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Formato Red:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            val aiPlatforms = listOf("Instagram", "X / Twitter", "LinkedIn", "TikTok")
                            aiPlatforms.forEach { platform ->
                                val active = selectedAIPlatform == platform
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                                        .border(1.dp, if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
                                        .clickable { selectedAIPlatform = platform }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = platform,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Tone selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tono de Voz:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            val tones = listOf("Profesional", "Inspirador", "Humorístico", "Persuasivo")
                            tones.forEach { tone ->
                                val active = selectedTone == tone
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent)
                                        .border(1.dp, if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp))
                                        .clickable { selectedTone = tone }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("tone_$tone"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tone,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Generate AI button
                    Button(
                        onClick = {
                            if (promptQuery.isBlank()) {
                                Toast.makeText(context, "Por favor ingresa un tema descriptivo para Gemini.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.generateContentWithAI(promptQuery, selectedAIPlatform, selectedTone)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("ai_generate_btn"),
                        enabled = !viewModel.isGeneratingState
                    ) {
                        if (viewModel.isGeneratingState) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gemini está escribiendo...")
                        } else {
                            Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generar Caption con Gemini")
                        }
                    }

                    // AI Suggestion Response Block
                    if (viewModel.aiGeneratorResponse.isNotEmpty()) {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Sugerencia Inteligente:",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                SelectionContainer {
                                    Text(
                                        text = viewModel.aiGeneratorResponse,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 18.sp
                                    )
                                }
                                
                                // Action to insert
                                if (!viewModel.isGeneratingState && !viewModel.aiGeneratorResponse.contains("Error")) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(
                                            onClick = {
                                                content = viewModel.aiGeneratorResponse
                                                Toast.makeText(context, "Sugerencia insertada en publicación", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.testTag("insert_ai_text_btn")
                                        ) {
                                            Icon(imageVector = Icons.Default.Input, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Usar este Copy")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
