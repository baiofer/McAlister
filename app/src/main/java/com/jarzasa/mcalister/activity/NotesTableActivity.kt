package com.jarzasa.mcalister.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.R.string.table
import com.jarzasa.mcalister.model.Plate
import com.jarzasa.mcalister.model.Table
import com.jarzasa.mcalister.model.Tables
import kotlinx.android.synthetic.main.activity_notes_table.*
import java.io.Serializable

class NotesTableActivity : AppCompatActivity() {

    companion object {
        val EXTRA_TABLE = "EXTRA_TABLE"

        fun intent(context: Context, table: Table?, position: Int): Intent {
            val intent = Intent(context, NotesTableActivity::class.java)
            intent.putExtra(TablesActivity.EXTRA_TABLE, table)
            intent.putExtra(TablesActivity.EXTRA_POSITION, position)
            return intent
        }
    }

    var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_table)

        //Recibimos la mesa
        val table = intent.getSerializableExtra(TablesActivity.EXTRA_TABLE) as? Table

        //Recibimos el plato seleccionado de su lista de platos
        position = intent.getIntExtra(TablesActivity.EXTRA_POSITION, 0)

        //Poner datos almacenados en la pantalla, para partir de los datos que hubiese
        if (table != null) {
            plate_name.text = table.plates[position].name
            quantity_edit.text = Editable.Factory.getInstance().newEditable(table.plates[position].quantity.toString())
            notes_edit.text = Editable.Factory.getInstance().newEditable(table.plates[position].notes)
        }
        //Si se pulsa el botón ACEPTAR, se guardan los datos introducidos en el plato
        ok_notes_button.setOnClickListener {
            val returnIntent = Intent()
            if (table != null) {
                table.plates[position].quantity = quantity_edit.text.toString().toInt()
                table.plates[position].notes = notes_edit.text.toString()
            }
            //Se devuelve la mesa actualizada a la actividad que me llamó
            returnIntent.putExtra(NotesTableActivity.EXTRA_TABLE, table) as? Serializable
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
        //Si se pulsa el botón CANCELAR se regresa a la actividad anterior sin hacer nada
        cancel_notes_button.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
