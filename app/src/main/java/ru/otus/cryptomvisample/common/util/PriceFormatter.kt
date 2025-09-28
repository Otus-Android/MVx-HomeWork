package ru.otus.cryptomvisample.common.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

class PriceFormatter @Inject constructor() {
    fun formatPrice(value: Double): String {
        val bdOriginal = BigDecimal.valueOf(value)
        val isNegative = bdOriginal.signum() < 0
        val bdAbs = bdOriginal.abs()

        val plain = bdAbs.stripTrailingZeros().toPlainString()
        val parts = plain.split('.')
        val intPart = parts[0]
        val fracPart = if (parts.size > 1) parts[1] else ""

        fun countNonZero(s: CharSequence) = s.count { it in '1'..'9' }
        val nonZeroInt = countNonZero(intPart)

        // Decide scale
        val scale: Int = if (nonZeroInt >= 4) {
            // Integer part already has ≥4 non-zero digits → exactly 2 decimals
            2
        } else {
            // Need enough fractional digits so that total non-zero digits ≥ 4,
            // while always showing at least 2 decimals and at most 4 non-zero fractional digits
            var sc = 2
            var nonZeroFrac = countNonZero(fracPart.take(sc))
            // Keep increasing scale until condition met or we already reached 4 non-zero fractional digits
            while (nonZeroInt + nonZeroFrac < 4 && nonZeroFrac < 4 && sc < fracPart.length) {
                sc++
                nonZeroFrac = countNonZero(fracPart.take(sc))
            }
            sc
        }

        val rounded = bdOriginal.setScale(scale, RoundingMode.HALF_UP)

        val nf = NumberFormat.getNumberInstance(Locale.US).apply {
            maximumFractionDigits = scale
            minimumFractionDigits = scale
            isGroupingUsed = true
        }

        val formatted = nf.format(rounded)
        return if (isNegative) "-$$formatted" else "$$formatted"
    }
}