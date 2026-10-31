package com.israadev.nuxlauncher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.israadev.nuxlauncher.R
import com.israadev.nuxlauncher.core.auth.AuthUser
import com.israadev.nuxlauncher.core.models.UserAccount
import com.israadev.nuxlauncher.core.lang.tr
import com.israadev.nuxlauncher.ui.theme.NuxColors
import com.israadev.nuxlauncher.ui.theme.resp

data class NuxNavItem(
    val id: String,
    val icon: ImageVector,
    val labelKey: String
)

val NUX_NAV_ITEMS = listOf(
    NuxNavItem("home", Icons.Outlined.Home, "sidebar.home"),
    NuxNavItem("accounts", Icons.Outlined.Person, "sidebar.accounts"),
    NuxNavItem("mods", Icons.Outlined.Extension, "sidebar.mods"),
    NuxNavItem("ai", Icons.Outlined.Psychology, "sidebar.ai"),
    NuxNavItem("settings", Icons.Outlined.Settings, "sidebar.settings")
)

/**
 * Navigation rail gaya Material 3: indikator pill di belakang ikon aktif,
 * label selalu terlihat, dan pemisah tipis sebelum tombol About.
 *
 * Sengaja tanpa gradient dan tanpa border ganda — kedalaman dibentuk oleh
 * surface tonal + satu indikator primary.
 */
@Composable
fun NuxSidebar(
    activeTab: String,
    onTabSelected: (String) -> Unit,
    onOpenAbout: () -> Unit = {},
    currentAccount: UserAccount? = null,
    launcherUser: AuthUser? = null,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val railShape = RoundedCornerShape(
        topEnd = (28.dp).resp(),
        bottomEnd = (28.dp).resp()
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(railShape)
            .background(NuxColors.SurfaceLow)
            .border(1.dp, NuxColors.OutlineVariant, railShape)
            .padding(top = (12.dp).resp(), bottom = (12.dp).resp()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Brand mark
        val logoShape = RoundedCornerShape((14.dp).resp())
        Box(
            modifier = Modifier
                .size((44.dp).resp())
                .clip(logoShape)
                .background(NuxColors.SurfaceHigh)
                .border(1.dp, NuxColors.OutlineVariant, logoShape)
                .clickable { onTabSelected("home") },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.nux_icon),
                contentDescription = "Flux Launcher",
                modifier = Modifier.size((26.dp).resp())
            )
        }

        Spacer(modifier = Modifier.height((16.dp).resp()))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy((4.dp).resp()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NUX_NAV_ITEMS.forEach { item ->
                val isSelected = activeTab == item.id
                NuxRailItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onTabSelected(item.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height((10.dp).resp()))

        // Divider tipis sebelum About
        Box(
            modifier = Modifier
                .width((28.dp).resp())
                .height(1.dp)
                .background(NuxColors.OutlineVariant)
        )

        Spacer(modifier = Modifier.height((10.dp).resp()))

        Box(
            modifier = Modifier
                .size((36.dp).resp())
                .clip(CircleShape)
                .background(NuxColors.SurfaceElevated)
                .border(1.dp, NuxColors.OutlineVariant, CircleShape)
                .clickable { onOpenAbout() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "About & License",
                tint = NuxColors.GrayNeutral,
                modifier = Modifier.size((17.dp).resp())
            )
        }
    }
}

/**
 * Satu baris navigasi rail: pill indikator + ikon + label.
 */
@Composable
private fun NuxRailItem(
    item: NuxNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val indicatorShape = RoundedCornerShape((16.dp).resp())
    val iconColor = if (isSelected) NuxColors.OnPrimary else NuxColors.GrayNeutral
    val labelColor = if (isSelected) NuxColors.MintGreen else NuxColors.GrayNeutral

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = (6.dp).resp())
            .clip(RoundedCornerShape((12.dp).resp()))
            .clickable(onClick = onClick)
            .padding(vertical = (3.dp).resp()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(width = (52.dp).resp(), height = (30.dp).resp())
                .clip(indicatorShape)
                .background(if (isSelected) NuxColors.ForestGreen else Color.Transparent, indicatorShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = tr(item.labelKey),
                tint = iconColor,
                modifier = Modifier.size((19.dp).resp())
            )
        }

        Spacer(modifier = Modifier.height((2.dp).resp()))

        Text(
            text = tr(item.labelKey),
            color = labelColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = (9.sp).resp(),
            maxLines = 1
        )
    }
}
