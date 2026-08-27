package com.example.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

data class LiquidThemeColors(
    val preset: ThemePreset,
    val canvasBase: Color,
    val canvasDeep: Color,
    val canvasHighlight: Color,
    val glassSurface: Color,
    val glassSurfaceLight: Color,
    val glassSurfaceSubtle: Color,
    val glassSurfaceDeep: Color,
    val glassBorder: Color,
    val glassBorderHighlight: Color,
    val glassBorderSubtle: Color,
    val primaryAccent: Color,
    val primaryAccentDark: Color,
    val primaryGlow: Color,
    val textPure: Color = Color(0xFFFFFFFF),
    val textMuted: Color = Color(0xFFA1A1AA),
    val textDim: Color = Color(0xFF71717A)
)

enum class ThemePreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val previewPrimary: Color,
    val previewBackground: Color
) {
    MONOCHROME_MINIMAL(
        id = "monochrome",
        title = "Monochrome Glass",
        subtitle = "Black & white minimal liquid glass (Default)",
        previewPrimary = Color(0xFFFFFFFF),
        previewBackground = Color(0xFF050505)
    ),
    CYBER_CYAN(
        id = "cyber_cyan",
        title = "Cyber Ice Cyan",
        subtitle = "Electric ice cyan glow on cosmic void",
        previewPrimary = Color(0xFF38BDF8),
        previewBackground = Color(0xFF070A10)
    ),
    EMERALD_MATRIX(
        id = "emerald_matrix",
        title = "Emerald Shield",
        subtitle = "Neon matrix emerald security glow",
        previewPrimary = Color(0xFF10B981),
        previewBackground = Color(0xFF040F0A)
    ),
    AMETHYST_VIOLET(
        id = "amethyst_violet",
        title = "Amethyst Violet",
        subtitle = "Royal lavender luminescence & night sheen",
        previewPrimary = Color(0xFFA855F7),
        previewBackground = Color(0xFF0A0512)
    ),
    SOLAR_AMBER(
        id = "solar_amber",
        title = "Solar Amber",
        subtitle = "Warm golden sunrise illumination",
        previewPrimary = Color(0xFFF59E0B),
        previewBackground = Color(0xFF100A03)
    ),
    RUBY_SIREN(
        id = "ruby_siren",
        title = "Ruby Siren",
        subtitle = "High-intensity crimson ruby alert glow",
        previewPrimary = Color(0xFFF43F5E),
        previewBackground = Color(0xFF120306)
    )
}

object ThemeManager {
    private const val PREFS_NAME = "liquid_theme_prefs"
    private const val KEY_PRESET = "selected_theme_preset"

    private var prefs: SharedPreferences? = null

    var currentPreset by mutableStateOf(ThemePreset.MONOCHROME_MINIMAL)
        private set

    val currentColors: LiquidThemeColors
        get() = paletteFor(currentPreset)

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedId = prefs?.getString(KEY_PRESET, ThemePreset.MONOCHROME_MINIMAL.id)
        currentPreset = ThemePreset.entries.find { it.id == savedId } ?: ThemePreset.MONOCHROME_MINIMAL
    }

    fun setTheme(preset: ThemePreset, context: Context? = null) {
        currentPreset = preset
        val p = prefs ?: context?.applicationContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        p?.edit()?.putString(KEY_PRESET, preset.id)?.apply()
    }

    private fun paletteFor(preset: ThemePreset): LiquidThemeColors {
        return when (preset) {
            ThemePreset.MONOCHROME_MINIMAL -> LiquidThemeColors(
                preset = preset,
                canvasBase = Color(0xFF000000),
                canvasDeep = Color(0xFF08080A),
                canvasHighlight = Color(0xFF141418),
                glassSurface = Color(0x1AFFFFFF),
                glassSurfaceLight = Color(0x28FFFFFF),
                glassSurfaceSubtle = Color(0x0EFFFFFF),
                glassSurfaceDeep = Color(0x33101014),
                glassBorder = Color(0x40FFFFFF),
                glassBorderHighlight = Color(0x80FFFFFF),
                glassBorderSubtle = Color(0x20FFFFFF),
                primaryAccent = Color(0xFFFFFFFF),
                primaryAccentDark = Color(0xFFCBD5E1),
                primaryGlow = Color(0x40FFFFFF),
                textPure = Color(0xFFFFFFFF),
                textMuted = Color(0xFFA1A1AA),
                textDim = Color(0xFF71717A)
            )
            ThemePreset.CYBER_CYAN -> LiquidThemeColors(
                preset = preset,
                canvasBase = Color(0xFF070A10),
                canvasDeep = Color(0xFF0C1019),
                canvasHighlight = Color(0xFF131B2A),
                glassSurface = Color(0x1AFFFFFF),
                glassSurfaceLight = Color(0x28FFFFFF),
                glassSurfaceSubtle = Color(0x0EFFFFFF),
                glassSurfaceDeep = Color(0x330E1726),
                glassBorder = Color(0x33FFFFFF),
                glassBorderHighlight = Color(0x66FFFFFF),
                glassBorderSubtle = Color(0x1FFFFFFF),
                primaryAccent = Color(0xFF38BDF8),
                primaryAccentDark = Color(0xFF0284C7),
                primaryGlow = Color(0x4D38BDF8),
                textPure = Color(0xFFF8FAFC),
                textMuted = Color(0xFF94A3B8),
                textDim = Color(0xFF64748B)
            )
            ThemePreset.EMERALD_MATRIX -> LiquidThemeColors(
                preset = preset,
                canvasBase = Color(0xFF030A06),
                canvasDeep = Color(0xFF06140D),
                canvasHighlight = Color(0xFF0D2217),
                glassSurface = Color(0x1810B981),
                glassSurfaceLight = Color(0x2810B981),
                glassSurfaceSubtle = Color(0x0EFFFFFF),
                glassSurfaceDeep = Color(0x33081C12),
                glassBorder = Color(0x3310B981),
                glassBorderHighlight = Color(0x6634D399),
                glassBorderSubtle = Color(0x1A10B981),
                primaryAccent = Color(0xFF10B981),
                primaryAccentDark = Color(0xFF059669),
                primaryGlow = Color(0x4D10B981),
                textPure = Color(0xFFF0FDF4),
                textMuted = Color(0xFF86EFAC),
                textDim = Color(0xFF4ADE80)
            )
            ThemePreset.AMETHYST_VIOLET -> LiquidThemeColors(
                preset = preset,
                canvasBase = Color(0xFF08030F),
                canvasDeep = Color(0xFF10081C),
                canvasHighlight = Color(0xFF1C0E30),
                glassSurface = Color(0x1AA855F7),
                glassSurfaceLight = Color(0x28A855F7),
                glassSurfaceSubtle = Color(0x0EFFFFFF),
                glassSurfaceDeep = Color(0x331A0B2C),
                glassBorder = Color(0x33A855F7),
                glassBorderHighlight = Color(0x66C084FC),
                glassBorderSubtle = Color(0x1FA855F7),
                primaryAccent = Color(0xFFA855F7),
                primaryAccentDark = Color(0xFF7E22CE),
                primaryGlow = Color(0x4DA855F7),
                textPure = Color(0xFFFAF5FF),
                textMuted = Color(0xFFD8B4FE),
                textDim = Color(0xFFA855F7)
            )
            ThemePreset.SOLAR_AMBER -> LiquidThemeColors(
                preset = preset,
                canvasBase = Color(0xFF0A0702),
                canvasDeep = Color(0xFF140D05),
                canvasHighlight = Color(0xFF24160A),
                glassSurface = Color(0x1AF59E0B),
                glassSurfaceLight = Color(0x28F59E0B),
                glassSurfaceSubtle = Color(0x0EFFFFFF),
                glassSurfaceDeep = Color(0x33261608),
                glassBorder = Color(0x33F59E0B),
                glassBorderHighlight = Color(0x66FCD34D),
                glassBorderSubtle = Color(0x1FF59E0B),
                primaryAccent = Color(0xFFF59E0B),
                primaryAccentDark = Color(0xFFD97706),
                primaryGlow = Color(0x4DF59E0B),
                textPure = Color(0xFFFFFBEB),
                textMuted = Color(0xFFFDE68A),
                textDim = Color(0xFFFBBF24)
            )
            ThemePreset.RUBY_SIREN -> LiquidThemeColors(
                preset = preset,
                canvasBase = Color(0xFF0C0305),
                canvasDeep = Color(0xFF17060A),
                canvasHighlight = Color(0xFF260B12),
                glassSurface = Color(0x1AF43F5E),
                glassSurfaceLight = Color(0x28F43F5E),
                glassSurfaceSubtle = Color(0x0EFFFFFF),
                glassSurfaceDeep = Color(0x332B0A13),
                glassBorder = Color(0x33F43F5E),
                glassBorderHighlight = Color(0x66FDA4AF),
                glassBorderSubtle = Color(0x1FF43F5E),
                primaryAccent = Color(0xFFF43F5E),
                primaryAccentDark = Color(0xFFE11D48),
                primaryGlow = Color(0x4DF43F5E),
                textPure = Color(0xFFFFF1F2),
                textMuted = Color(0xFFFECDD3),
                textDim = Color(0xFFFB7185)
            )
        }
    }
}
