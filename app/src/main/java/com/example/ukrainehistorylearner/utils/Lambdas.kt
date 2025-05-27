package com.example.ukrainehistorylearner.utils

import com.example.ukrainehistorylearner.model.HistoricalMaterial

val printMaterialInfo: (HistoricalMaterial) -> Unit = {
    println("Lambda: ${it.title} — ${it.period.name}")
}
