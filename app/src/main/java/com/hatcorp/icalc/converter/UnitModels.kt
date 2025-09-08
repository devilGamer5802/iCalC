package com.hatcorp.icalc.converter

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

    companion object {
        fun fromString(name: String?): ConversionCategory? {
            return when (name) {
                "Length" -> Length
                "Mass" -> Mass
                else -> null
            }
        }
    }
}

data class UnitInfo(val name: String, val symbol: String, val toBaseRate: Double)