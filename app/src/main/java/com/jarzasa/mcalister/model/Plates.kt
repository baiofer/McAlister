package com.jarzasa.mcalister.model

import java.io.Serializable

object Plates: Serializable {

    var platesDownloaded: MutableList<Plate> = mutableListOf()

    operator fun get(i: Int) = platesDownloaded.get(i)
    operator fun set(i: Int, value: Plate) {
        platesDownloaded.set(i, value)
    }

    fun toArray() = platesDownloaded.toTypedArray()

    fun count() = platesDownloaded.count()

    // Para poder iterar como si fuera una lista
    operator fun iterator() = platesDownloaded.iterator()


}