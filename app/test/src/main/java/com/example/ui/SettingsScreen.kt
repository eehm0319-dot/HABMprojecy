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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var showNicheDialog by remember { mutableStateOf(false) }
    var tempNicheInput by remember { mutableStateOf("") }
    
    // For account linking modal feedback
    var showLinkDialogFor by remember { mutableStateOf<String?>(null) }
    var linkUsernameInput by remember { mutableStateOf("") }

    val accountsList = listOf(
        AccountInfo("fb", "Facebook", Icons.Default.Public, Color(0xFF1877F2), viewModel.isFacebookLinked),
        AccountInfo("ig", "Instagram", Icons.Default.CameraAlt, Color(0xFFE1306C), viewModel.isInstagramLinked),
        AccountInfo("tiktok", "Tik tok", Icons.Default.PlayCircle, Color(0xFF000000), viewModel.isTikTokLinked),
        AccountInfo("yt", "Youtube", Icons.Default.Slideshow, Color(0xFFFF0000), viewModel.isYouTubeLinked),
        AccountInfo("wa", "Watsap", Icons.Default.Forum, Color(0xFF25D366), viewModel.isWhatsAppLinked),
        AccountInfo("google", "Cuenta google", Icons.Default.AccountCircle, Color(0xFF4285F4), viewModel.isGoogleAccountLinked)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header Section ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configuración",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Configuración",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-1).sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Ajustes de la plataforma HABM",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // --- NICHO SECTION ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "NICHO DE ENFOQUE",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        FilledTonalButton(
                            onClick = {
                                tempNicheInput = viewModel.activeNiche
                                showNicheDialog = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cambiar", fontSize = 12.sp)
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Text(
                        text = "Aquí se elegirá el nicho en el que nos queremos enfocar el cual permanecerá activo por defecto, hasta que decidamos cambiarlo. Las campañas generadas por inteligencia artificial usarán automáticamente esta temática.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    text = "Nicho Activo por Defecto",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = viewModel.activeNiche,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- VINCULACION DE CUENTAS SECTION ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "VINCULACIÓN DE CUENTAS",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Vincula tu aplicación HABM con cada una de tus cuentas",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    accountsList.forEach { acc ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (acc.isConnected) {
                                        // Toggle off directly or prompt
                                        viewModel.toggleLinkStatus(acc.id)
                                        Toast.makeText(context, "${acc.name} desvinculado", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // Open mock configuration linking dialog
                                        linkUsernameInput = ""
                                        showLinkDialogFor = acc.id
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(acc.brandColor.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = acc.icon,
                                            contentDescription = acc.name,
                                            tint = acc.brandColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = acc.name,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (acc.isConnected) "Autorizado y Activo" else "No Vinculado",
                                            fontSize = 11.sp,
                                            color = if (acc.isConnected) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (acc.isConnected) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (acc.isConnected) "LINKED" else "OFFLINE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (acc.isConnected) Color(0xFF2E7D32) else Color(0xFFC62828)
                                        )
                                    }
                                    Switch(
                                        checked = acc.isConnected,
                                        onCheckedChange = {
                                            if (acc.isConnected) {
                                                viewModel.toggleLinkStatus(acc.id)
                                                Toast.makeText(context, "${acc.name} desvinculado", Toast.LENGTH_SHORT).show()
                                            } else {
                                                linkUsernameInput = ""
                                                showLinkDialogFor = acc.id
                                            }
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = acc.brandColor,
                                            uncheckedBorderColor = MaterialTheme.colorScheme.outline
                                        ),
                                        modifier = Modifier.scale(0.8f)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }

    // --- DIALOG: ALTER NICHO ---
    if (showNicheDialog) {
        AlertDialog(
            onDismissRequest = { showNicheDialog = false },
            title = {
                Text(
                    text = "Ajustar Nicho Predeterminado",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Ingresa la temática o nicho a enfocar por defecto en la aplicación HABM:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = tempNicheInput,
                        onValueChange = { tempNicheInput = it },
                        placeholder = { Text("ej. Fitness y Nutrición Crudivegana") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Text(
                        text = "Sugerencias Populares:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    val shortcuts = listOf("Finanzas y Política", "Criptomonedas", "Fitness y Hábitos", "Educación de Negocios")
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        shortcuts.forEach { sug ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { tempNicheInput = sug },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Text(
                                    text = sug,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(10.dp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempNicheInput.isBlank()) {
                            Toast.makeText(context, "El nicho no puede estar en blanco", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.updateActiveNiche(tempNicheInput)
                        showNicheDialog = false
                        Toast.makeText(context, "Nicho actualizado: ${viewModel.activeNiche}", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Guardar Nicho")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNicheDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // --- DIALOG: CONNECT SOCIAL PROFILE ---
    if (showLinkDialogFor != null) {
        val targetId = showLinkDialogFor!!
        val acc = accountsList.find { it.id == targetId }
        
        if (acc != null) {
            AlertDialog(
                onDismissRequest = { showLinkDialogFor = null },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(acc.brandColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = acc.icon, contentDescription = null, tint = acc.brandColor, modifier = Modifier.size(16.dp))
                        }
                        Text(text = "Vincular ${acc.name}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Por favor, introduce tu nombre de usuario, identificador o correo de la cuenta de ${acc.name} para completar la vinculación en la app HABM:",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = linkUsernameInput,
                            onValueChange = { linkUsernameInput = it },
                            label = { Text("Escribe tu cuenta o nick") },
                            placeholder = { Text(if (targetId == "wa") "+54 9 11..." else "@minombre_habm") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Text(
                            text = "Nota: Al aceptar, se generará una clave de vinculación mock de seguridad y la cuenta quedará programada.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = acc.brandColor, contentColor = Color.White),
                        onClick = {
                            if (linkUsernameInput.isBlank()) {
                                Toast.makeText(context, "Por favor ingresa un nombre o número para vincular", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.toggleLinkStatus(targetId)
                            showLinkDialogFor = null
                            Toast.makeText(context, "¡Cuenta ${acc.name} vinculada exitosamente con $linkUsernameInput!", Toast.LENGTH_LONG).show()
                        }
                    ) {
                        Text("Vincular Cuenta")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLinkDialogFor = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

data class AccountInfo(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val brandColor: Color,
    val isConnected: Boolean
)
