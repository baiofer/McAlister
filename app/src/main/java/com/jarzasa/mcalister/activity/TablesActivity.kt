package com.jarzasa.mcalister.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.fragment.TableFragment
import com.jarzasa.mcalister.fragment.TablesFragment
import com.jarzasa.mcalister.model.Plate
import com.jarzasa.mcalister.model.Table
import com.jarzasa.mcalister.model.Tables

class TablesActivity : AppCompatActivity(), TablesFragment.OnFragmentInteractionListener, TableFragment.OnFragmentInteractionListener {

    enum class TYPE_RETURN {
        RESULT_OK,
        RESULT_FIRST_USER
    }

    companion object {
        val REQUEST_ADD = 1
        val REQUEST_DELETE = 2
        val ADD = false
        val REQUEST_PLATE = 4
        val REQUEST_NOTE = 5
        val EXTRA_TABLE = "EXTRA_TABLE"
        val EXTRA_PLATE = "EXTRA_PLATE"
        val EXTRA_POSITION = "EXTRA_POSITION"

        fun intent(context: Context) = Intent(context, TablesActivity::class.java)
    }

    var oldPosition: Int = 0
    lateinit var oldTable: Table

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tables)

        //Comprobamos que en la interfaz tenemos el FrameLayout de la mesa (Table)(landscape)
        if (!isPortrait()) {
            //Si no hay mesas, no presento nada
            if (Tables.count() != 0) {
                showTableInLandscape(0)
            }
        }
        //Llamamos al fragment de las Mesas
        showTables()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //UTILIDADES
    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun isPortrait(): Boolean {
        return (findViewById<View>(R.id.table_fragment) == null)
    }

    fun showTableInPortrait(position: Int) {
        if (fragmentManager.findFragmentById(R.id.table_fragment) == null) {
            // Si hemos llegado aquí, sabemos que nunca hemos creado el MainFragment, lo creamos
            fragmentManager.beginTransaction()
                    .add(R.id.tables_fragment, TableFragment.newInstance(position))
                    .commit()
        } else {
            //Si ya existe el fragment, lo reemplazo con los nuevos datos
            fragmentManager.beginTransaction()
                    .replace(R.id.tables_fragment, TableFragment.newInstance(position))
                    .commit()
        }
    }

    fun showTableInLandscape(position: Int) {
        if (fragmentManager.findFragmentById(R.id.table_fragment) == null) {
            // Si hemos llegado aquí, sabemos que nunca hemos creado el MainFragment, lo creamos
            fragmentManager.beginTransaction()
                    .add(R.id.table_fragment, TableFragment.newInstance(position))
                    .commit()
        } else {
            //Si ya existe el fragment, lo reemplazo con los nuevos datos
            fragmentManager.beginTransaction()
                    .replace(R.id.table_fragment, TableFragment.newInstance(position))
                    .commit()
        }
    }

    fun showTables() {
        //Pintamos la lista de mesas
        if (fragmentManager.findFragmentById(R.id.tables_fragment) == null) {
            // Si hemos llegado aquí, sabemos que nunca hemos creado el MainFragment, lo creamos
            fragmentManager.beginTransaction()
                    .add(R.id.tables_fragment, TablesFragment.newInstance())
                    .commit()
        } else {
            //Si ya existe el fragment, lo reemplazo con los nuevos datos
            fragmentManager.beginTransaction()
                    .replace(R.id.tables_fragment, TablesFragment.newInstance())
                    .commit()
        }
    }

    fun showTable(position: Int) {
        if (isPortrait()) {
            showTableInPortrait(position)
        } else {
            showTableInLandscape(position)
            showTables()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //COMUNICACION FRAGMENT_TABLE - ACTIVITY
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //El fragment nos dice que actualicemos la mesa
    override fun okTable(table: Table?, position: Int) {
        if (table != null) {
            Tables.actualizeTable(table)
        }
        if (isPortrait()) {
            showTables()
        } else {
            showTable(position)
            Snackbar.make(findViewById(R.id.tables_fragment), getString(R.string.table_added), Snackbar.LENGTH_LONG)
                    .show()
        }
    }

    //El fragment nos dice que salgamos sin modificar nada
    override fun cancelTable(position: Int) {
        Tables[oldPosition] = oldTable
        if (isPortrait()) {
            showTables()
        } else {
            showTable(oldPosition)
            Snackbar.make(findViewById(R.id.tables_fragment), getString(R.string.discarded_changes), Snackbar.LENGTH_LONG)
                    .show()
        }
    }

    //El fragment nos dice que borremos la mesa. Ha dado la factura
    override fun deleteTable(table: Table?, position: Int) {
        if (table != null) {
            Tables.deleteTable(table)
        }
        if (isPortrait()) {
            showTables()
        } else {
            showTables()
            if (Tables.count() != 0) {
                showTable(0)
            } else {
                //Si ya existe el fragment, lo elimino
                val fragment = fragmentManager.findFragmentById(R.id.table_fragment)
                if (fragment != null) {
                    fragmentManager.beginTransaction()
                            .remove(fragment)
                            .commit()
                }
            }
        }
    }

    override fun selectPlateFromPlates() {
        startActivityForResult(PlatesActivity.intent(this), REQUEST_PLATE)
    }

    override fun getNotesFromPlate(table: Table?, position: Int) {
        startActivityForResult(NotesTableActivity.intent(this, table, position), REQUEST_NOTE)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //COMUNICACION FRAGMENT_TABLES - ACTIVITY
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //El fragment nos pide cerrar la actividad. Pulsado botón Back
    override fun cancelActivity() {
        finish()
    }

    //El fragment nos pide añadir una mesa
    override fun addTable() {
        val intent = AddTableActivity.intent(this)
        startActivityForResult(intent, TablesActivity.REQUEST_ADD)
    }

    //El fragment nos pide borrar una mesa
    override fun deleteTable() {
        val intent = DeleteTableActivity.intent(this)
        startActivityForResult(intent, TablesActivity.REQUEST_DELETE)
    }

    //El fragment nos pide mostrar la mesa seleccionada
    override fun tableSelected(position: Int) {
        oldPosition = position
        oldTable = Tables[position]
        showTable(position)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //COMUNICACION ACTIVITY - FRAGMENT TABLES y FRAGMENT_TABLE
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Recibo las respuestas de las actividades a las que llamo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            TablesActivity.REQUEST_ADD -> {
                if (resultCode == Activity.RESULT_OK) {
                    //Informo al fragment de la mesa a añadir
                    val tableSelected = data?.getSerializableExtra(AddTableActivity.EXTRA_TABLE) as? Table
                    val operation = data?.getBooleanExtra(AddTableActivity.EXTRA_OPTION, TablesActivity.ADD)
                    //Añado la mesa a la lista de mesas si no existia o la actualizo si ya existia
                    val fragment = fragmentManager.findFragmentById(R.id.tables_fragment) as? TablesFragment
                    fragment?.addTableInTables(tableSelected, operation)
                }
            }
            TablesActivity.REQUEST_DELETE -> {
                //Informo al fragment de la mesa a borrar
                if (resultCode == Activity.RESULT_OK) {
                    val tableSelected = data?.getSerializableExtra(DeleteTableActivity.EXTRA_TABLE) as? Table
                    //Borro la mesa de la lista de mesas
                    val fragment = fragmentManager.findFragmentById(R.id.tables_fragment) as? TablesFragment
                    fragment?.deleteTableInTables(tableSelected)
                }
            }
            REQUEST_PLATE -> {
                //Recibo elplato seleccionado y lo añado a la lista de platos, con cantidad a 1
                if (resultCode == Activity.RESULT_OK) {
                    val plateSelected = data?.getSerializableExtra(PlatesActivity.EXTRA_PLATE) as? Plate
                    if (isPortrait()) {
                        //Pintamos el plato en tables_fragment (Portrait)
                        val fragment = fragmentManager.findFragmentById(R.id.tables_fragment) as? TableFragment
                        fragment?.addPlateSelectedToTable(plateSelected)
                    } else {
                        //Pintamos el plato en table_fragment (Landscape)
                        val fragment = fragmentManager.findFragmentById(R.id.table_fragment) as? TableFragment
                        fragment?.addPlateSelectedToTable(plateSelected)
                    }
                }
            }
            REQUEST_NOTE -> {
                //Recibo la mesa con los datos ya cambiados (cantidad y notas)
                if (resultCode == Activity.RESULT_OK) {
                    val tableSelected = data?.getSerializableExtra(NotesTableActivity.EXTRA_TABLE) as? Table
                    if (isPortrait()) {
                        //Pintamos el plato en tables_fragment (Portrait)
                        val fragment = fragmentManager.findFragmentById(R.id.tables_fragment) as? TableFragment
                        fragment?.addNotesToPlate(tableSelected)
                    } else {
                        //Pintamos el plato en table_fragment (Landscape)
                        val fragment = fragmentManager.findFragmentById(R.id.table_fragment) as? TableFragment
                        fragment?.addNotesToPlate(tableSelected)
                    }
                }
            }
        }
    }
}

