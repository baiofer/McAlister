package com.jarzasa.mcalister.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.model.Table
import com.jarzasa.mcalister.model.Tables
import kotlinx.android.synthetic.main.activity_delete_table.*
import java.io.Serializable

class DeleteTableActivity : AppCompatActivity() {

    companion object {
        val EXTRA_TABLE = "EXTRA_TABLE"
        fun intent(context: Context) = Intent(context, DeleteTableActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_table)

        //Detección de pulsación de botones
        ok_erase_button.setOnClickListener { acceptTable() }
        cancel_erase_button.setOnClickListener { cancelTable() }
    }

    //Pulsado botón CANCELAR. Salgo de la actividad sin hacer nada
    private fun cancelTable() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    //Pulsado botón ACEPTAR. Salgo devolviendo la mesa que hay que borrar
    private fun acceptTable() {
        val table = validateData()
        val returnIntent = Intent()
        returnIntent.putExtra(AddTableActivity.EXTRA_TABLE, table) as? Serializable
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    //Recojo los datos del formulario
    fun validateData(): Table? {
        val number = table_number_erase.text.toString().toInt()
        if (Tables.isTable(number)) {
            //La mesa no existe, devuelvo null
            return null
        }
        //La mesa existe, devuelvo la mesa
        return Table(number, 1, mutableListOf())
    }
}
