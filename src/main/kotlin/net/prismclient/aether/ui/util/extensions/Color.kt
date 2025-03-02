package net.prismclient.aether.ui.util.extensions

fun Int.getRed(): Int = this shr 16 and 0xFF

fun Int.getGreen(): Int = this shr 8 and 0xFF

fun Int.getBlue(): Int = this and 0xFF

fun Int.getAlpha(): Int = this shr 24 and 0xFF

fun Int.setAlpha(alpha: Int): Int = (this and 0x00FFFFFF) or (alpha shl 24)

fun Int.setAlpha(alpha: Float): Int = (this and 0x00FFFFFF) or ((alpha * 255f + 0.5).toInt() shl 24)

fun asRGBA(r: Int, g: Int, b: Int, a: Int = 255) =
    r shl 16 or (g shl 8) or b or (a shl 24)

fun asRGBA(r: Float, g: Float, b: Float, a: Float = 1f): Int = asRGBA((r * 255 + 0.5).toInt(), (g * 255 + 0.5).toInt(), (b * 255 + 0.5).toInt(), (a * 255 + 0.5).toInt())
fun asRGBA(r: Int, g: Int, b: Int, a: Float) = asRGBA(r, g, b, (a * 255 + 0.5).toInt())

fun Int.limitRange(): Int =
    asRGBA(this.getRed().limit(), this.getGreen().limit(), this.getBlue().limit(), this.getAlpha().limit())

/**
 * Returns the value if it is greater than 0, and less than 255
 */
fun Int.limit(): Int = this.coerceAtMost(255).coerceAtLeast(0)

/**
 * Returns the value if it is greater than 0f, and less than 1f
 */
fun Float.limit(): Float = this.coerceAtMost(1f).coerceAtLeast(0f)

/**
 * Creates a new color from two provided values based on the progress between each value.
 */
fun transition(color1: Int, color2: Int, progress: Float): Int {
    val red1 = color1 shr 16 and 0xFF
    val green1 = color1 shr 8 and 0xFF
    val blue1 = color1 and 0xFF
    val alpha1 = color1 shr 24 and 0xFF
    val red2 = color2 shr 16 and 0xFF
    val green2 = color2 shr 8 and 0xFF
    val blue2 = color2 and 0xFF
    val alpha2 = color2 shr 24 and 0xFF
    val red = (red1 + ((red2 - red1) * progress + 0.5)).toInt()
    val green = (green1 + ((green2 - green1) * progress + 0.5)).toInt()
    val blue = (blue1 + ((blue2 - blue1) * progress + 0.5)).toInt()
    val alpha = (alpha1 + ((alpha2 - alpha1) * progress + 0.5)).toInt()
    return red shl 16 or (green shl 8) or blue or (alpha shl 24)
}


/**
 * Returns a string of the RGBA values
 */
fun Int.colorToString() = "RGBA(${this.getRed()}, ${this.getGreen()}, ${this.getBlue()}, ${this.getAlpha()})"