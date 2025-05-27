package com.example.ukrainehistorylearner.model

enum class HistoricalPeriod {
    ANCIENT_TIMES,
    KYIV_RUS,
    COSSACK_ERA,
    IMPERIAL_PERIOD,
    REVOLUTION_PERIOD,
    SOVIET_ERA,
    INDEPENDENCE;

    fun getYearRange(): String {
        return when (this) {
            ANCIENT_TIMES -> "Стародавні часи (до 882 р)."
            KYIV_RUS -> "Київська Русь (882-1240 рр)."
            COSSACK_ERA -> "Козацька доба (1550-1775 рр)."
            IMPERIAL_PERIOD -> "Імперська доба (1775-1917 рр)."
            REVOLUTION_PERIOD -> "Українська революція (1917-1922 рр)."
            SOVIET_ERA -> "Радянський період (1922-1991 рр)."
            INDEPENDENCE -> "Незалежність (1991-сьогодення)."
        }
    }

    companion object {
        fun getEntryByYearRange(yearRange: String): HistoricalPeriod {
            return when (yearRange) {
                "Стародавні часи (до 882 р)." -> ANCIENT_TIMES
                "Київська Русь (882-1240 рр)." -> KYIV_RUS
                "Козацька доба (1550-1775 рр)." -> COSSACK_ERA
                "Імперська доба (1775-1917 рр)." -> IMPERIAL_PERIOD
                "Українська революція (1917-1922 рр)." -> REVOLUTION_PERIOD
                "Радянський період (1922-1991 рр)." -> SOVIET_ERA
                "Незалежність (1991-сьогодення)." -> INDEPENDENCE
                else -> throw IllegalArgumentException("Невідома діапазон років: $yearRange")
            }
        }
    }
}
