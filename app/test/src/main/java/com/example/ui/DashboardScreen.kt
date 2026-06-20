package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SocialPost
import com.example.data.SocialProfile
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    
    var nicheInput by remember(viewModel.activeNiche) { mutableStateOf(viewModel.activeNiche) }
    val result = viewModel.nicheGeneratedResult
    val isGenerating = viewModel.isNicheGenerating
    val error = viewModel.nicheError

    val suggestionNiches = listOf(
        "Finanzas y Política",
        "Educación de Negocios",
        "Fitness y Hábitos",
        "Cripto y Futuro"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header Concept ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                Text(
                    text = "SocialGrow Studio",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-1).sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Generador de Contenido Inteligente Multicanal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // --- Studio Welcome Intro Banner ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Studio",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Campañas por Nicho",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Suministra tu temática preferida. SocialGrow redactará las tácticas y copys optimizados bajo la secuencia de frecuencia exacta para todas tus redes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
                        )
                    }
                }
            }
        }

        // --- Niche Finder Field ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Describe tu Nicho de Audiencia",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    OutlinedTextField(
                        value = nicheInput,
                        onValueChange = { nicheInput = it },
                        placeholder = { Text("ejemplo: Finanzas Personales y Política Dominicana") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("niche_query_input"),
                        singleLine = true,
                        trailingIcon = {
                            if (nicheInput.isNotEmpty()) {
                                IconButton(onClick = { nicheInput = "" }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Limpiar")
                                }
                            }
                        }
                    )

                    // Quick suggestion chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        suggestionNiches.forEach { sug ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                    .clickable { nicheInput = sug }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sug,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Generation Action Button
                    Button(
                        onClick = {
                            if (nicheInput.isBlank()) {
                                Toast.makeText(context, "Por favor ingresa un nicho de preferencia", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.generateNicheContentWithAI(nicheInput)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("niche_generate_button"),
                        enabled = !isGenerating
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generando toda la campaña...")
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generar Estrategia Inteligente")
                        }
                    }
                }
            }
        }

        // --- Error Block ---
        if (error != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        // --- Content Output Sequence Header ---
        if (result != null) {
            item {
                Text(
                    text = "Resultado en Secuencia de Plataformas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // 1. FACEBOOK: Cada 4 horas Card
            item {
                FacebookStrategyCard(
                    hook = result.facebookHook,
                    imageDesc = result.facebookImageDescription,
                    caption = result.facebookCaption,
                    onCopy = {
                        val fullText = "${result.facebookHook}\n\n${result.facebookCaption}"
                        clipboardManager.setText(AnnotatedString(fullText))
                        Toast.makeText(context, "Copy de Facebook copiado al portapapeles", Toast.LENGTH_SHORT).show()
                    },
                    onSchedule = {
                        val now = System.currentTimeMillis()
                        val post = SocialPost(
                            title = "FB: Frase Gancho - $nicheInput",
                            content = "${result.facebookHook}\n\n${result.facebookCaption}",
                            scheduledTime = now + (4 * 3600000), // 4 hours from now
                            platforms = "facebook",
                            mediaUri = "Facebook JPG Image",
                            status = "APPROVED"
                        )
                        viewModel.savePost(post)
                        Toast.makeText(context, "¡Publicación de Facebook programada para dentro de 4h!", Toast.LENGTH_LONG).show()
                    }
                )
            }

            // 2. REELS: 1 diario Card
            item {
                ReelsStrategyCard(
                    script = result.reelsScript,
                    caption = result.reelsCaption,
                    onCopy = {
                        val fullText = "GUION:\n${result.reelsScript}\n\nDESCRIPCION:\n${result.reelsCaption}"
                        clipboardManager.setText(AnnotatedString(fullText))
                        Toast.makeText(context, "Estructura de Reel copiado al portapapeles", Toast.LENGTH_SHORT).show()
                    },
                    onSchedule = {
                        val now = System.currentTimeMillis()
                        val post = SocialPost(
                            title = "Reel Diario - $nicheInput",
                            content = "GUION:\n${result.reelsScript}\n\nDESCRIPCION:\n${result.reelsCaption}",
                            scheduledTime = now + (12 * 3600000), // 12 hours from now (today's reel)
                            platforms = "facebook,instagram,tiktok",
                            mediaUri = "Reel Video File",
                            status = "APPROVED"
                        )
                        viewModel.savePost(post)
                        Toast.makeText(context, "¡Reel unificado programada para dentro de 12h!", Toast.LENGTH_LONG).show()
                    }
                )
            }

            // 3. INSTAGRAM CARUSEL: 2 diarios Card
            item {
                CarouselStrategyCard(
                    cover = result.carouselCover,
                    slides = result.carouselSlides,
                    onCopy = {
                        val fullText = buildString {
                            append("PORTADA: ").append(result.carouselCover).append("\n\n")
                            result.carouselSlides.forEachIndexed { index, s ->
                                append("IMAGEN ${index + 1}: ").append(s).append("\n")
                            }
                        }
                        clipboardManager.setText(AnnotatedString(fullText))
                        Toast.makeText(context, "Frases de Carrusel copiadas", Toast.LENGTH_SHORT).show()
                    },
                    onSchedule = {
                        val now = System.currentTimeMillis()
                        val fullText = buildString {
                            append("PORTADA: ").append(result.carouselCover).append("\n\n")
                            result.carouselSlides.forEachIndexed { index, s ->
                                append("DIAPOSITIVA ${index + 1}: ").append(s).append("\n\n")
                            }
                            append("#EgoBoost #MarketingMental #Superacion #Sabiduria")
                        }
                        val post = SocialPost(
                            title = "IG Carousel - $nicheInput",
                            content = fullText,
                            scheduledTime = now + (24 * 3600000), // 24 hours from now
                            platforms = "instagram",
                            mediaUri = "Instagram Carousel Folder",
                            status = "APPROVED"
                        )
                        viewModel.savePost(post)
                        Toast.makeText(context, "¡Carrusel de 7 paneles programado para mañana!", Toast.LENGTH_LONG).show()
                    }
                )
            }

            // 4. YOUTUBE VIDEO LARGO: 1 semanal Card
            item {
                YoutubeStrategyCard(
                    script = result.youtubeScript,
                    onCopy = {
                        clipboardManager.setText(AnnotatedString(result.youtubeScript))
                        Toast.makeText(context, "Narración de YouTube copiada al portapapeles", Toast.LENGTH_SHORT).show()
                    },
                    onSchedule = {
                        val now = System.currentTimeMillis()
                        val post = SocialPost(
                            title = "YouTube Video Semanal - $nicheInput",
                            content = result.youtubeScript,
                            scheduledTime = now + (48 * 3600000), // 2 days ahead
                            platforms = "youtube",
                            mediaUri = "Long Video Draft",
                            status = "APPROVED"
                        )
                        viewModel.savePost(post)
                        Toast.makeText(context, "¡Guión de YouTube guardado en tu planificador!", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }
}

// ======================== SUB-VIEW COMPOSABLES DESIGN ========================

@Composable
fun FacebookStrategyCard(
    hook: String,
    imageDesc: String,
    caption: String,
    onCopy: () -> Unit,
    onSchedule: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1877F2).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = Color(0xFF1877F2), modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(text = "Facebook Feed", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "Frecuencia: Cada 4 horas • Imagen JPG + Copy Envolvente", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Interactive Preview of the JPG Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF2C3E50),
                                Color(0xFF0F2027)
                            )
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "VISTA PREVIA JPG",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White.copy(alpha = 0.4f),
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = hook,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = FontStyle.Italic,
                        fontSize = 13.sp,
                        color = Color(0xFF00FFCC),
                        lineHeight = 18.sp
                    )
                }
            }

            // Design details
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "Sugerencia de Fondo:", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                    Text(text = imageDesc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontStyle = FontStyle.Italic)
                }
            }

            // Copy text
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Descripción Envolvente (Incertidumbre / Motivación):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = caption,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Action Row
            ActionButtonsRow(onCopy = onCopy, onSchedule = onSchedule)
        }
    }
}

@Composable
fun ReelsStrategyCard(
    script: String,
    caption: String,
    onCopy: () -> Unit,
    onSchedule: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE1306C).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.SwitchVideo, contentDescription = null, tint = Color(0xFFE1306C), modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(text = "Reels / Shorts / TikTok", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "Frecuencia: 1 Diario • Guión de Resolución + Audio Gancho", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Script details
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "📖 GUIÓN DE AUDIO Y VIDEO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = script,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Caption details
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Copy de Descripción (Afirma solución en BIO/DESCR):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = caption,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Action Row
            ActionButtonsRow(onCopy = onCopy, onSchedule = onSchedule)
        }
    }
}

@Composable
fun CarouselStrategyCard(
    cover: String,
    slides: List<String>,
    onCopy: () -> Unit,
    onSchedule: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0077B5).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Collections, contentDescription = null, tint = Color(0xFF0077B5), modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(text = "Instagram Carousel", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "Frecuencia: 2 Diarios • 7 Paneles de Impulso de Ego", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Text(
                text = "Desliza para ver la secuencia del carrusel:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Swipeable Carousel Slides View
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Cover
                item {
                    CarouselSlideItem(
                        indexLabel = "PORTADA",
                        phrase = cover,
                        gradientColors = listOf(Color(0xFFFF416C), Color(0xFFFF4B2B))
                    )
                }
                // Slides
                itemsIndexed(slides) { index, phrase ->
                    val gradient = when (index % 3) {
                        0 -> listOf(Color(0xFF11998e), Color(0xFF38ef7d))
                        1 -> listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
                        else -> listOf(Color(0xFFF3904F), Color(0xFF3B4371))
                    }
                    CarouselSlideItem(
                        indexLabel = "PANEL ${index + 1}",
                        phrase = phrase,
                        gradientColors = gradient
                    )
                }
            }

            // Action Row
            ActionButtonsRow(onCopy = onCopy, onSchedule = onSchedule)
        }
    }
}

@Composable
fun CarouselSlideItem(
    indexLabel: String,
    phrase: String,
    gradientColors: List<Color>
) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .height(170.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradientColors))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.25f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = indexLabel,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Text(
                    text = phrase,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(vertical = 8.dp)
                )

                Text(
                    text = "SocialGrow",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun YoutubeStrategyCard(
    script: String,
    onCopy: () -> Unit,
    onSchedule: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF0000).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, tint = Color(0xFFFF0000), modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(text = "YouTube Video Largo", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "Frecuencia: 1 Semanal • Narrativa en 3ra Persona, Política y Finanzas", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Script details
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "🎤 RELATO PERSONAL Y COMUNIDAD DIGITAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = script,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Action Row
            ActionButtonsRow(onCopy = onCopy, onSchedule = onSchedule)
        }
    }
}

@Composable
fun ActionButtonsRow(
    onCopy: () -> Unit,
    onSchedule: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onCopy,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Copiar Texto", fontSize = 12.sp)
        }

        Button(
            onClick = onSchedule,
            modifier = Modifier.weight(1.2f),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Planificar Post", fontSize = 12.sp)
        }
    }
}
