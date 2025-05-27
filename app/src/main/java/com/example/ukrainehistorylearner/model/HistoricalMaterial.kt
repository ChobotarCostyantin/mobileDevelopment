package com.example.ukrainehistorylearner.model

open class HistoricalMaterial(
    val id: String,
    val title: String,
    val period: HistoricalPeriod,
) {

    open fun display() {
        println("Матеріал: $title (${period.getYearRange()}")
    }

    open fun getFullInfo(): String {
        return "ID:$id, '$title', період: ${period.name}"
    }

    companion object {
        private var totalMaterials = 0

        fun getTotalMaterialsCount(): Int = totalMaterials

        fun registerNewMaterial() {
            totalMaterials++
        }
    }
}
