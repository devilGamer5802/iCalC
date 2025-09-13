package com.hatcorp.icalc.converter

import kotlin.math.pow

sealed class ConversionCategory(val name: String, val units: List<UnitInfo>) {
    data object Length : ConversionCategory("Length", listOf(
        UnitInfo("Meter", "m", 1.0),
        UnitInfo("Kilometer", "km", 1000.0),
        UnitInfo("Centimeter", "cm", 0.01),
        UnitInfo("Millimeter", "mm", 0.001),
        UnitInfo("Mile", "mi", 1609.34),
        UnitInfo("Yard", "yd", 0.9144),
        UnitInfo("Foot", "ft", 0.3048),
        UnitInfo("Inch", "in", 0.0254)
    ))
    data object Mass : ConversionCategory("Mass", listOf(
        UnitInfo("Gram", "g", 1.0),
        UnitInfo("Kilogram", "kg", 1000.0),
        UnitInfo("Milligram", "mg", 0.001),
        UnitInfo("Tonne", "t", 1_000_000.0),
        UnitInfo("Pound", "lb", 453.592),
        UnitInfo("Ounce", "oz", 28.3495)
    ))

    data object Data : ConversionCategory("Data", listOf(
        UnitInfo("Byte", "B", 1.0),
        UnitInfo("Kilobyte", "KB", 1024.0),
        UnitInfo("Megabyte", "MB", 1024.0.pow(2)),
        UnitInfo("Gigabyte", "GB", 1024.0.pow(3)),
        UnitInfo("Terabyte", "TB", 1024.0.pow(4))
    ))

    data object Speed : ConversionCategory("Speed", listOf(
        UnitInfo("Meters/second", "m/s", 1.0),
        UnitInfo("Kilometers/hour", "km/h", 0.277778),
        UnitInfo("Miles/hour", "mph", 0.44704),
        UnitInfo("Feet/second", "ft/s", 0.3048)
    ))
    data object Temperature : ConversionCategory("Temperature", listOf(
        UnitInfo("Celsius", "°C", 0.0),
        UnitInfo("Fahrenheit", "°F", 0.0),
        UnitInfo("Kelvin", "K", 0.0)
    ))

    companion object {
        fun fromString(name: String?): ConversionCategory? {
            return when (name) {
                "Length" -> Length
                "Mass" -> Mass
                "Data" -> Data
                "Speed" -> Speed
                "Temperature" -> Temperature
                else -> null
            }
        }
    }
}


data class UnitInfo(val name: String, val symbol: String, val toBaseRate: Double)