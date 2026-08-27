package com.example.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ==========================================
// TRUE LIQUID GLASS COLOR PALETTE (CUSTOMIZABLE / DEFAULT B&W MINIMAL)
// ==========================================
object LiquidGlassTheme {
    // Canvas layers
    val CanvasBase: Color get() = ThemeManager.currentColors.canvasBase
    val CanvasDeep: Color get() = ThemeManager.currentColors.canvasDeep
    val CanvasHighlight: Color get() = ThemeManager.currentColors.canvasHighlight

    // Frosted surfaces
    val GlassSurface: Color get() = ThemeManager.currentColors.glassSurface
    val GlassSurfaceLight: Color get() = ThemeManager.currentColors.glassSurfaceLight
    val GlassSurfaceSubtle: Color get() = ThemeManager.currentColors.glassSurfaceSubtle
    val GlassSurfaceDeep: Color get() = ThemeManager.currentColors.glassSurfaceDeep

    // Specular refraction edges & highlights
    val GlassBorder: Color get() = ThemeManager.currentColors.glassBorder
    val GlassBorderHighlight: Color get() = ThemeManager.currentColors.glassBorderHighlight
    val GlassBorderSubtle: Color get() = ThemeManager.currentColors.glassBorderSubtle

    // Dynamic primary theme accent (CyanLiquid is the primary accent token)
    val CyanLiquid: Color get() = ThemeManager.currentColors.primaryAccent
    val CyanLiquidDark: Color get() = ThemeManager.currentColors.primaryAccentDark
    val CyanGlow: Color get() = ThemeManager.currentColors.primaryGlow

    // Fixed functional indicators (Shield / Siren / Caution)
    val EmeraldShield = Color(0xFF10B981)    // Anti-Shutdown protection active
    val EmeraldGlow = Color(0x4D10B981)

    val CoralSiren = Color(0xFFF43F5E)       // Ringing alarm / alert
    val CoralGlow = Color(0x4DF43F5E)

    val AmberWarning = Color(0xFFF59E0B)     // Pre-alarm lockdown / caution
    val TextPure: Color get() = ThemeManager.currentColors.textPure
    val TextMuted: Color get() = ThemeManager.currentColors.textMuted
    val TextDim: Color get() = ThemeManager.currentColors.textDim
}

// ==========================================
// LIQUID GLASS MODIFIERS
// ==========================================

/**
 * Applies a true liquid glass styling to any Composable:
 * - Frosted translucent fill
 * - High-gloss specular top highlight
 * - Crisp refractive border
 * - Subtle inner refraction gradient
 */
fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(22.dp),
    borderAlpha: Float = 0.22f,
    surfaceAlpha: Float = 0.12f,
    glowColor: Color? = null,
    elevation: Dp = 0.dp
): Modifier = this
    .then(
        if (elevation > 0.dp) {
            Modifier.shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = glowColor ?: Color.Black.copy(alpha = 0.5f),
                spotColor = glowColor ?: Color.Black.copy(alpha = 0.5f)
            )
        } else Modifier
    )
    .clip(shape)
    .background(
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = surfaceAlpha * 1.5f),
                Color(0xFF0F172A).copy(alpha = 0.65f),
                Color.White.copy(alpha = surfaceAlpha * 0.5f)
            ),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    )
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = borderAlpha * 1.8f),
                (glowColor ?: Color.White).copy(alpha = borderAlpha * 0.8f),
                Color.White.copy(alpha = borderAlpha * 0.3f)
            ),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        ),
        shape = shape
    )

/**
 * Animated liquid glass background canvas with flowing subtle ambient light mesh
 */
@Composable
fun LiquidGlassCanvas(
    modifier: Modifier = Modifier,
    accentGlow: Color = LiquidGlassTheme.CyanLiquid,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "liquid_mesh")
    val animOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fluid_motion"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LiquidGlassTheme.CanvasBase)
            .drawBehind {
                // Background deep void
                drawRect(
                    Brush.verticalGradient(
                        colors = listOf(
                            LiquidGlassTheme.CanvasDeep,
                            LiquidGlassTheme.CanvasBase
                        )
                    )
                )

                // Fluid orb 1 (Top Left)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentGlow.copy(alpha = 0.16f),
                            accentGlow.copy(alpha = 0.04f),
                            Color.Transparent
                        ),
                        center = Offset(
                            x = size.width * (0.2f + 0.15f * animOffset),
                            y = size.height * (0.15f + 0.1f * animOffset)
                        ),
                        radius = size.width * 0.85f
                    )
                )

                // Fluid orb 2 (Bottom Right)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            LiquidGlassTheme.CyanLiquidDark.copy(alpha = 0.12f),
                            LiquidGlassTheme.CanvasDeep.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        center = Offset(
                            x = size.width * (0.8f - 0.2f * animOffset),
                            y = size.height * (0.85f - 0.1f * animOffset)
                        ),
                        radius = size.width * 0.95f
                    )
                )
            },
        content = content
    )
}

// ==========================================
// LIQUID GLASS COMPONENTS
// ==========================================

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    glowColor: Color? = null,
    borderAlpha: Float = 0.25f,
    surfaceAlpha: Float = 0.14f,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = LiquidGlassTheme.CyanLiquid),
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .liquidGlass(
                shape = shape,
                borderAlpha = borderAlpha,
                surfaceAlpha = surfaceAlpha,
                glowColor = glowColor,
                elevation = if (glowColor != null) 8.dp else 0.dp
            )
            .then(clickModifier)
    ) {
        content()
    }
}

@Composable
fun LiquidGlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accentColor: Color = LiquidGlassTheme.CyanLiquid,
    shape: Shape = RoundedCornerShape(16.dp),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                if (enabled) {
                    Brush.linearGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.35f),
                            accentColor.copy(alpha = 0.15f),
                            Color(0x33000000)
                        )
                    )
                } else {
                    Brush.linearGradient(listOf(Color.White.copy(0.04f), Color.White.copy(0.02f)))
                }
            )
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = if (enabled) {
                        listOf(
                            Color.White.copy(alpha = 0.6f),
                            accentColor,
                            accentColor.copy(alpha = 0.3f)
                        )
                    } else {
                        listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.05f))
                    }
                ),
                shape = shape
            )
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = accentColor),
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
fun LiquidGlassBadge(
    text: String,
    modifier: Modifier = Modifier,
    accentColor: Color = LiquidGlassTheme.CyanLiquid,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .clip(CircleShape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.22f),
                        accentColor.copy(alpha = 0.08f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.5f),
                        accentColor.copy(alpha = 0.6f)
                    )
                ),
                shape = CircleShape
            ),
        color = Color.Transparent
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(5.dp)
        ) {
            leadingIcon?.invoke()
            Text(
                text = text,
                color = LiquidGlassTheme.TextPure,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}
