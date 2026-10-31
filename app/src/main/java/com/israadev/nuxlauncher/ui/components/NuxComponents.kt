package com.israadev.nuxlauncher.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.israadev.nuxlauncher.ui.theme.LocalNuxScale
import com.israadev.nuxlauncher.ui.theme.NuxColors
import com.israadev.nuxlauncher.ui.theme.NuxSizes
import com.israadev.nuxlauncher.ui.theme.resp

/**
 * Kartu dasar Flux: surface tonal + outline tipis, tanpa gradien bertumpuk.
 * `backgroundColor` tetap bisa di-override call-site lama.
 */
@Composable
fun NuxCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = NuxColors.SurfaceWhite,
    borderColor: Color = NuxColors.CardBorder,
    shadowColor: Color = Color.Transparent,
    shadowOffset: Dp = 0.dp,
    cornerRadius: Dp = NuxSizes.CornerRadius,
    borderWidth: Dp = NuxSizes.BorderWidth,
    fillMaxHeight: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .then(if (fillMaxHeight) Modifier.fillMaxSize() else Modifier.fillMaxWidth())
            .shadow(
                elevation = shadowOffset,
                shape = shape,
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, shape),
        content = content
    )
}

/**
 * Tombol utama. State layer & ripple ikut Material 3 (LocalIndication),
 * ditambah micro-scale saat ditekan supaya terasa "responsif" seperti game.
 */
@Composable
fun NuxButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = NuxColors.ForestGreen,
    contentColor: Color = Color.White,
    borderColor: Color = Color.Transparent,
    enabled: Boolean = true,
    shadowOffset: Dp = 0.dp,
    cornerRadius: Dp = (14.dp).resp(),
    contentPadding: PaddingValues = PaddingValues(horizontal = (16.dp).resp(), vertical = (9.dp).resp()),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.97f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "btnScale"
    )

    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = if (enabled) 1f else 0.45f
            }
            .shadow(
                elevation = if (enabled) shadowOffset else 0.dp,
                shape = shape,
                ambientColor = backgroundColor,
                spotColor = backgroundColor
            )
            .clip(shape)
            .background(backgroundColor)
            .border(width = NuxSizes.BorderWidth, color = borderColor, shape = shape)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .defaultMinSize(minHeight = (40.dp).resp())
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                content = content
            )
        }
    }
}

/**
 * Chip status / label kecil. Memakai sudut terpotong (dog-tag) sebagai
 * penanda visual "HUD" khas Flux, bukan sekadar pill biasa.
 */
@Composable
fun NuxBadge(
    text: String,
    backgroundColor: Color = NuxColors.SoftLime,
    textColor: Color = NuxColors.MintGreen,
    borderColor: Color? = null,
    modifier: Modifier = Modifier
) {
    val shape = NuxSizes.ShapeHudChip
    val effectiveBorderColor = borderColor ?: textColor.copy(alpha = 0.30f)

    Box(
        modifier = modifier
            .background(backgroundColor, shape)
            .border(1.dp, effectiveBorderColor, shape)
            .padding(horizontal = (8.dp).resp(), vertical = (3.dp).resp())
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = (10.sp).resp(),
            fontWeight = FontWeight.Bold,
            letterSpacing = (0.6.sp).resp(),
            maxLines = 1,
            softWrap = false
        )
    }
}

/**
 * Input teks bergaya outlined field Material 3: outline menebal dan berubah
 * ke primary saat fokus.
 */
@Composable
fun NuxTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingContent: (@Composable () -> Unit)? = null
) {
    val shape = RoundedCornerShape((12.dp).resp())
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) NuxColors.ForestGreen else NuxColors.OutlineVariant,
        label = "fieldBorder"
    )
    val resolvedTextStyle = textStyle ?: TextStyle(
        color = NuxColors.DarkGray,
        fontSize = (13.sp).resp(),
        fontWeight = FontWeight.SemiBold
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(NuxColors.SurfaceInput, shape)
            .border(if (isFocused) 1.5.dp else 1.dp, borderColor, shape)
            .padding(horizontal = (12.dp).resp(), vertical = (9.dp).resp()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = NuxColors.GrayNeutral.copy(alpha = 0.65f),
                    fontSize = (13.sp).resp(),
                    fontWeight = FontWeight.Normal
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = resolvedTextStyle,
                cursorBrush = SolidColor(NuxColors.ForestGreen),
                singleLine = true,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (trailingContent != null) {
            Spacer(modifier = Modifier.width((8.dp).resp()))
            trailingContent()
        }
    }
}

/**
 * Kontainer dialog Material 3: scrim gelap, surface tinggi, radius 24,
 * outline halus. Semua dialog lama otomatis ikut tampilan ini.
 */
@Composable
fun NuxDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    fillMaxHeight: Boolean = false,
    content: @Composable () -> Unit
) {
    val isTablet = LocalNuxScale.current.isTablet
    val defaultWidthFraction = if (isTablet) 0.82f else 0.94f
    val dialogShape = RoundedCornerShape((24.dp).resp())

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.72f))
                .padding(horizontal = (12.dp).resp(), vertical = (8.dp).resp()),
            contentAlignment = Alignment.Center
        ) {
            val dialogModifier = if (fillMaxHeight) {
                modifier.fillMaxWidth(defaultWidthFraction).fillMaxHeight(0.96f)
            } else {
                modifier.fillMaxWidth(defaultWidthFraction).wrapContentHeight()
            }
            Box(
                modifier = dialogModifier
                    .shadow(
                        elevation = (18.dp).resp(),
                        shape = dialogShape,
                        ambientColor = Color.Black,
                        spotColor = Color.Black
                    )
                    .clip(dialogShape)
                    .background(NuxColors.SurfaceHigh)
                    .border(1.dp, NuxColors.OutlineVariant, dialogShape)
            ) {
                content()
            }
        }
    }
}

/**
 * Permukaan pill netral untuk top bar (akun, status). Menggantikan
 * rantai gradient + border ganda yang dulu ditulis berulang di dashboard.
 */
@Composable
fun FluxPill(
    modifier: Modifier = Modifier,
    containerColor: Color = NuxColors.SurfaceElevated,
    borderColor: Color = NuxColors.OutlineVariant,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = (10.dp).resp(), vertical = (6.dp).resp()),
    content: @Composable RowScope.() -> Unit
) {
    val shape = NuxSizes.ShapePill
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Row(
        modifier = modifier
            .clip(shape)
            .background(containerColor, shape)
            .border(1.dp, borderColor, shape)
            .then(clickModifier)
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy((6.dp).resp()),
        content = content
    )
}

/**
 * Tombol ikon persegi-rounded untuk top bar / toolbar.
 */
@Composable
fun FluxIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = NuxColors.SurfaceElevated,
    tint: Color = NuxColors.DarkGray,
    buttonSize: Dp = (38.dp).resp()
) {
    val shape = RoundedCornerShape((12.dp).resp())

    Box(
        modifier = modifier
            .size(buttonSize)
            .clip(shape)
            .background(containerColor, shape)
            .border(1.dp, NuxColors.OutlineVariant, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size((18.dp).resp())
        )
    }
}
