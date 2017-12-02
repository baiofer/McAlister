package com.jarzasa.mcalister.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.model.Table
import com.jarzasa.mcalister.model.Tables
import kotlinx.android.synthetic.main.activity_add_table.*
import java.io.Serializable

class AddTableActivity : AppCompatActivity() {

    companion object {
        val EXTRA_TABLE = "EXTRA_TABLE"
        val EXTRA_OPTION = "EXTRA_OPTION"
        val ADD = false
        val ACTUALIZE = true

        fun intent(context: Context) = Intent(context, AddTableActivity::class.java)
    }

    var number: Int = 0
    var person: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_table)

        //Detección de pulsación de botones
        ok_add_button.setOnClickListener { acceptTable() }
        cancel_add_button.setOnClickListener { cancelTable() }
    }

    //Pulsado botón CANCELAR. Salgo de la actividad sin hacer nada
    private fun cancelTable() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    //Pulsado botón ACEPTAR. Salgo devolviendo la mesa que hay que Añadir/Actualizar
    private fun acceptTable() {
        //Recojo los datos del formulario
        val table = validateData()
        //Devuelvo la mesa
        val returnIntent = Intent()
        returnIntent.putExtra(EXTRA_TABLE, table) as? Serializable
        //Devuelvo la acción a realizar (AÑADIR o ACTUALIZAR
        if (Tables.isTable(number)) {
            returnIntent.putExtra(EXTRA_OPTION, ADD)
        } else {
            returnIntent.putExtra(EXTRA_OPTION, ACTUALIZE)
        }
        //Salgo de la actividad actual
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    //Recojo los datos del formulario
    fun validateData(): Table? {
        number = table_number_entrance.text.toString().toInt()
        person = table_persons_entrance.text.toString().toInt()
        return Table(number, person, mutableListOf())
    }
}
