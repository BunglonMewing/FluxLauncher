package com.israadev.nuxlauncher.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.israadev.nuxlauncher.R
import com.israadev.nuxlauncher.core.account.AccountManager
import com.israadev.nuxlauncher.core.auth.AuthUser
import com.israadev.nuxlauncher.ui.components.NuxButton
import com.israadev.nuxlauncher.ui.theme.NuxColors
import com.israadev.nuxlauncher.ui.theme.resp
import java.util.UUID

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Form state: Hanya Username
    var username by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    fun submitLogin() {
        val cleanName = username.trim()
        if (cleanName.isBlank()) {
            errorMessage = "Silakan masukkan username terlebih dahulu."
            return
        }
        if (cleanName.length < 3) {
            errorMessage = "Username minimal 3 karakter."
            return
        }
        if (!cleanName.matches(Regex("^[a-zA-Z0-9_]{3,16}$"))) {
            errorMessage = "Username hanya boleh huruf, angka, dan underscore (3-16 karakter)."
            return
        }

        isLoading = true
        errorMessage = ""
        keyboardController?.hide()

        val userId = UUID.nameUUIDFromBytes("FluxPlayer:$cleanName".toByteArray()).toString()
        val authUser = AuthUser(
            uid = userId,
            email = "$cleanName@flux.local",
            username = cleanName,
            photoURL = "",
            platform = "android",
            isActivated = true,
            tier = "flux"
        )

        AccountManager.saveAuthUser(context, authUser)

        val existingGameAcc = AccountManager.accounts.value.firstOrNull {
            it.username.equals(cleanName, ignoreCase = true)
        }
        if (existingGameAcc != null) {
            AccountManager.selectAccount(existingGameAcc)
        } else {
            val guestAcc = AccountManager.createGuestAccount(cleanName)
            AccountManager.addAccount(context, guestAcc)
        }

        isLoading = false
        onAuthSuccess()
    }

    // Two-Column Landscape Console Layout
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(NuxColors.Background)
            .padding((10.dp).resp()),
        horizontalArrangement = Arrangement.spacedBy((10.dp).resp())
    ) {
        // =========================================================================
        // LEFT PANE: BRANDING & GAMING SHOWCASE (38% Width)
        // =========================================================================
        val leftPanelShape = RoundedCornerShape((16.dp).resp())
        Box(
            modifier = Modifier
                .weight(0.38f)
                .fillMaxHeight()
                .clip(leftPanelShape)
                .background(NuxColors.SurfaceLow)
                .border(1.dp, NuxColors.OutlineVariant, leftPanelShape)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.mc_hero_bg),
                    contentDescription = "Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.30f
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    NuxColors.Background.copy(alpha = 0.8f),
                                    NuxColors.Background
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Bar: App Badge & Online Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.16f))
                                .border(1.dp, NuxColors.ForestGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "FLUX CLIENT",
                                color = NuxColors.MintGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(NuxColors.ForestGreen, CircleShape)
                            )
                            Text(
                                text = "OFFLINE READY",
                                color = Color(0xFFA1A1AA),
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Middle: Title & Gaming Highlights
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.nux_icon),
                                contentDescription = "Flux Logo",
                                modifier = Modifier
                                    .size((36.dp).resp())
                                    .clip(RoundedCornerShape((10.dp).resp()))
                            )
                            Column {
                                Text(
                                    text = "MINECRAFT JAVA",
                                    color = NuxColors.MintGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )

                                Text(
                                    text = "FLUX LAUNCHER",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-0.3).sp,
                                    lineHeight = 28.sp
                                )
                            }
                        }

                        Text(
                            text = "Mainkan Minecraft Java Edition di Android dengan kontrol kustom yang responsif, manajemen mod yang mudah, dan performa tinggi.",
                            color = Color(0xFFD4D4D8),
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    // Bottom: Feature Badges
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            GamingFeaturePill(text = "FABRIC / FORGE", icon = Icons.Default.Extension, modifier = Modifier.weight(1f))
                            GamingFeaturePill(text = "OFFLINE PLAY", icon = Icons.Default.Bolt, modifier = Modifier.weight(1f))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            GamingFeaturePill(text = "CUSTOM GUI", icon = Icons.Default.TouchApp, modifier = Modifier.weight(1f))
                            GamingFeaturePill(text = "INSTANT LOGIN", icon = Icons.Default.Shield, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // =========================================================================
        // RIGHT PANE: USERNAME-ONLY LOGIN CONSOLE (62% Width)
        // =========================================================================
        val rightPanelShape = RoundedCornerShape((16.dp).resp())
        Box(
            modifier = Modifier
                .weight(0.62f)
                .fillMaxHeight()
                .clip(rightPanelShape)
                .background(NuxColors.SurfaceLow)
                .border(1.dp, NuxColors.OutlineVariant, rightPanelShape)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = (24.dp).resp(), vertical = (20.dp).resp()),
                verticalArrangement = Arrangement.Center
            ) {
                // Header Title
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "MASUK KE FLUX LAUNCHER",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Cukup masukkan username untuk langsung memulai bermain. Tanpa perlu akun atau kata sandi.",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height((16.dp).resp()))

                // Error Banner
                if (errorMessage.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF261017))
                            .border(1.dp, Color(0xFFF43F5E).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFF43F5E),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = errorMessage,
                                color = Color(0xFFFDA4AF),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height((10.dp).resp()))
                }

                // Input Field Username
                AuthInputField(
                    value = username,
                    onValueChange = {
                        username = it
                        errorMessage = ""
                    },
                    label = "USERNAME PEMAIN",
                    placeholder = "Contoh: Alex, Steve, Gamer123",
                    leadingIcon = Icons.Default.Person,
                    onEnter = { submitLogin() }
                )

                Spacer(modifier = Modifier.height((16.dp).resp()))

                // Submit Button
                NuxButton(
                    onClick = { submitLogin() },
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = NuxColors.ForestGreen,
                    contentColor = NuxColors.OnPrimary,
                    enabled = !isLoading
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = NuxColors.OnPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isLoading) "MEMPERSIAPKAN..." else "MULAI BERMAIN",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = NuxColors.OnPrimary
                        )
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------------
// HELPER COMPOSABLES
// --------------------------------------------------------------------------

@Composable
private fun GamingFeaturePill(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF141824))
            .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NuxColors.MintGreen,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = text,
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AuthInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    onEnter: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFA1A1AA),
            letterSpacing = 0.8.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF0C0E14))
                .border(1.dp, Color(0x2EFFFFFF), RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = NuxColors.MintGreen,
                    modifier = Modifier.size(18.dp)
                )

                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color(0xFF52525B),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        cursorBrush = SolidColor(NuxColors.ForestGreen),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onEnter() }),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
