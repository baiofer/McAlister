package com.jarzasa.mcalister.model

import android.graphics.drawable.Drawable
import java.io.Serializable

//Modelo de un Plato (Plate)
data class Plate (val number: Int,
                  val name: String,
                  val ingredients: String,
                  val allergens: String,
                  val photo: String,
                  val recipe: String,
                  val price: Double,
                  var quantity: Int,
                  var notes: String): Serializable {

    override fun toString(): String {
        return "${number}    ${name}       Cantidad: ${quantity}    Precio: ${price}"
    }
}

