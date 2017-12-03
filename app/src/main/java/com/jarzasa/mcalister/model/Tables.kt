package com.jarzasa.mcalister.model

import java.io.Serializable

//Mesas de la aplicación. Es una lista mutable de Table. Es un singleton, común para toda la aplicación
object Tables: Serializable {

    private val tables: MutableList<Table> = mutableListOf()

    fun count() = tables.size

    operator fun get(i: Int) = tables[i]
    operator fun set(i: Int, value: Table) {
        tables[i] = value
    }

    fun addTable(table: Table) {
        tables.add(table)
    }

    fun deleteTable(table: Table) {
        for (tab in tables) {
            if (tab.number == table.number) {
                tables.remove(tab)
                return
            }
        }
    }

    fun actualizeTable(table: Table) {
        for (tab in tables) {
            if (tab.number == table.number) {
                tables.remove(tab)
                tables.add(table)
                return
            }
        }
        tables.add(table)
    }

    fun isTable(num: Int): Boolean {
        for (tab in tables) {
            if (tab.number == num) {
                return false
            }
        }
        return true
    }

    fun toArray() = tables.toTypedArray()

}