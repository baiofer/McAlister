package com.jarzasa.mcalister.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.fragment.TableFragment
import com.jarzasa.mcalister.fragment.TablesFragment
import com.jarzasa.mcalister.model.Plate
import com.jarzasa.mcalister.model.Table
import java.io.Serializable

class TableActivity : AppCompatActivity(), TableFragment.OnFragmentInteractionListener {

    companion object {
        val REQUEST_PLATE = 4
        val REQUEST_NOTE = 5
        val EXTRA_TABLE = "EXTRA_TABLE"
        val EXTRA_PLATE = "EXTRA_PLATE"
        val EXTRA_POSITION = "EXTRA_POSITION"
        val ON = true
        val OFF = false

        fun intent(context: Context, tableIndex: Int): Intent {
            val intent = Intent(context, TableActivity::class.java)
            intent.putExtra(EXTRA_TABLE, tableIndex)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)

        //Recibimos la mesa a mostrar
        val position = intent.getIntExtra(EXTRA_TABLE, 0)

        //Llamamos al fragment
        if (fragmentManager.findFragmentById(R.id.table_fragment) == null) {
            // Si hemos llegado aquí, sabemos que nunca hemos creado el MainFragment, lo creamos
            fragmentManager.beginTransaction()
                    .add(R.id.table_fragment, TableFragment.newInstance(position))
                    .commit()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //COMUNICACION FRAGMENT - ACTIVITY
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //El fragment nos dice que salgamos devolviendo la mesa con los platos incorporados
    override fun okTable(table: Table?) {
        val returnIntent = Intent()
        returnIntent.putExtra(TableActivity.EXTRA_TABLE, table) as? Serializable
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    //El fragment nos dice que salgamos sin modificar nada
    override fun cancelTable() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    //El fragment nos dice que salgamos borrando la mesa. Ha dado la factura
    override fun deleteTable(table: Table?) {
        val returnIntent = Intent()
        returnIntent.putExtra(TableActivity.EXTRA_TABLE, table) as? Serializable
        setResult(Activity.RESULT_FIRST_USER, returnIntent)
        finish()
    }

    override fun selectPlateFromPlates() {
        startActivityForResult(PlatesActivity.intent(this), TableActivity.REQUEST_PLATE)
    }

    override fun getNotesFromPlate(table: Table?, position: Int) {
        startActivityForResult(NotesTableActivity.intent(this, table, position), TableActivity.REQUEST_NOTE)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //COMUNICACION ACTIVITY - FRAGMENT
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Recibo las respuestas de las actividades a las que llamo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            TableActivity.REQUEST_PLATE -> {
                //Recibo elplato seleccionado y lo añado a la lista de platos, con cantidad a 1
                if (resultCode == Activity.RESULT_OK) {
                    val plateSelected = data?.getSerializableExtra(PlatesActivity.EXTRA_PLATE) as? Plate
                    val fragment = fragmentManager.findFragmentById(R.id.table_fragment) as? TableFragment
                    fragment?.addPlateSelectedToTable(plateSelected)
                }
            }
            TableActivity.REQUEST_NOTE -> {
                //Recibo la mesa con los datos ya cambiados (cantidad y notas)
                if (resultCode == Activity.RESULT_OK) {
                    val tableSelected = data?.getSerializableExtra(NotesTableActivity.EXTRA_TABLE) as? Table
                    val fragment = fragmentManager.findFragmentById(R.id.table_fragment) as? TableFragment
                    fragment?.addNotesToPlate(tableSelected)
                }
            }
        }
    }
}

