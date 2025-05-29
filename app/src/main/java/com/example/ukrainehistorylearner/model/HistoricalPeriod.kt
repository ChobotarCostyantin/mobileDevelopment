package com.example.ukrainehistorylearner.model

import android.content.Context
import com.example.ukrainehistorylearner.R

enum class HistoricalPeriod {
    ANCIENT_TIMES,
    KYIV_RUS,
    COSSACK_ERA,
    IMPERIAL_PERIOD,
    REVOLUTION_PERIOD,
    SOVIET_ERA,
    INDEPENDENCE;

    fun getYearRange(context: Context): String {
        return when (this) {
            ANCIENT_TIMES -> context.getString(R.string.ancient_times)
            KYIV_RUS -> context.getString(R.string.kyiv_rus)
            COSSACK_ERA -> context.getString(R.string.cossack_era)
            IMPERIAL_PERIOD -> context.getString(R.string.imperial_period)
            REVOLUTION_PERIOD -> context.getString(R.string.revolution_period)
            SOVIET_ERA -> context.getString(R.string.soviet_era)
            INDEPENDENCE -> context.getString(R.string.independence)
        }
    }


    companion object {
        fun getEntryByYearRange(context: Context, yearRange: String): HistoricalPeriod {
            return entries.firstOrNull { it.getYearRange(context) == yearRange }
                ?: throw IllegalArgumentException("Невідомий діапазон років: $yearRange")
        }

    }
}
