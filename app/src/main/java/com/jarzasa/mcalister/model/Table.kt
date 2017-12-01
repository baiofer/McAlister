package com.jarzasa.mcalister.model

import java.io.Serializable

data class Table(var number: Int,
                 var persons: Int,
                 var plates: MutableList<Plate>): Serializable {

    override fun toString() = "Mesa: ${number}            Comensales: ${persons}"

    fun toArray() = plates.toTypedArray()

    fun addPlate(plate: Plate) {
        plates.add(plate)
    }

    fun deletePlate(plate: Plate) {
        for (pla in plates) {
            if (pla.number == plate.number) {
                plates.remove(pla)
                return
            }
        }
    }

    fun actualizePlate(plate: Plate) {
        for (pla in plates) {
            if (pla.number == plate.number) {
                plates.remove(pla)
                plates.add(plate)
                return
            }
        }
        plates.add(plate)
    }

    fun isPlate(num: Int): Boolean {
        for (pla in plates) {
            if (pla.number == num) {
                return false
            }
        }
        return true
    }
}