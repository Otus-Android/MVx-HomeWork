package ru.otus.cryptomvisample.common.util

import java.text.DecimalFormat
import javax.inject.Inject

class ChangeFormatter @Inject constructor() {
    private val percentageFormatter = DecimalFormat("#,##0.##%")

    fun format(change: Double): String {
        val changePercent = change / 100.0
        val sign = if (change >= 0) "+" else ""
        return "$sign${percentageFormatter.format(changePercent)}"
    }
}