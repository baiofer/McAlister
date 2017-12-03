package com.jarzasa.mcalister.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.fragment.TableFragment
import com.jarzasa.mcalister.fragment.TablesFragment
import com.jarzasa.mcalister.model.Plate
import com.jarzasa.mcalister.model.Table
import com.jarzasa.mcalister.model.Tables
import java.io.Serializable

class TablesActivity : AppCompatActivity(), TablesFragment.OnFragmentInteractionListener, TableFragment.OnFragmentInteractionListener {

    enum class TYPE_RETURN {
        RESULT_OK,
        RESULT_FIRST_USER
    }

    companion object {
        val REQUEST_ADD = 1
        val REQUEST_DELETE = 2
        val REQUEST_TABLE = 3
        val ADD = false

        fun intent(context: Context) = Intent(context, TablesActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tables)

        //Comprobamos que en la interfaz tenemos el FrameLayout de la mesa (Table)
        if (findViewById<View>(R.id.table_fragment) != null) {
            //Si no hay mesas, no presento nada
            if (Tables.count() != 0) {
                //Llamamos al fragment
                if (fragmentManager.findFragmentById(R.id.table_fragment) == null) {
                    // Si hemos llegado aquí, sabemos que nunca hemos creado el MainFragment, lo creamos
                    fragmentManager.beginTransaction()
                            .add(R.id.table_fragment, TableFragment.newInstance(0))
                            .commit()
                }
            }
        }

        //Comprobamos que en la interfaz tenemos el FrameLayout de la lista de mesas (Tables)
        if (findViewById<View>(R.id.tables_fragment) != null) {
            //Llamamos al fragment
            if (fragmentManager.findFragmentById(R.id.tables_fragment) == null) {
                // Si hemos llegado aquí, sabemos que nunca hemos creado el MainFragment, lo creamos
                fragmentManager.beginTransaction()
                        .add(R.id.tables_fragment, TablesFragment.newInstance())
                        .commit()
            }
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //UTILIDADES
    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun showFragmentTable(position: Int?) {
        //Comprobamos si la interfaz tiene el fragment de una mesa
        val fragment = findViewById<View>(R.id.table_fragment)
        if (fragment == null) {
            //No existe el fragment en la interfaz, lanzo la actividad de la mesa
            if (position != null) {
                startActivityForResult(TableActivity.intent(this, position), TablesActivity.REQUEST_TABLE)
            }
        } else {
            //Existe el fragment de la actividad, presento la mesa en el fragment
            //Muestro el fragment de la mesa
            if (fragmentManager.findFragmentById(R.id.table_fragment) == null) {
                // Si hemos llegado aquí, sabemos que nunca hemos creado el MainFragment, lo creamos
                fragmentManager.beginTransaction()
                        .add(R.id.table_fragment, TableFragment.newInstance(0))
                        .commit()
            } else {
                fragmentManager.beginTransaction()
                        .replace(R.id.table_fragment, TableFragment.newInstance(position!!))
                        .commit()
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //COMUNICACION FRAGMENT_TABLE - ACTIVITY
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //El fragment nos dice que salgamos devolviendo la mesa con los platos incorporados
    override fun okTable(table: Table?) {
        val returnIntent = Intent()
        returnIntent.putExtra(TableActivity.EXTRA_TABLE, table) as? Serializable
        setResult(Activity.RESULT_OK, returnIntent)
        //finish()
    }

    //El fragment nos dice que salgamos sin modificar nada
    override fun cancelTable() {
        setResult(Activity.RESULT_CANCELED)
        //finish()
    }

    //El fragment nos dice que salgamos borrando la mesa. Ha dado la factura
    override fun deleteTable(table: Table?) {
        val returnIntent = Intent()
        returnIntent.putExtra(TableActivity.EXTRA_TABLE, table) as? Serializable
        setResult(Activity.RESULT_FIRST_USER, returnIntent)
        //finish()
    }

    override fun selectPlateFromPlates() {
        startActivityForResult(PlatesActivity.intent(this), TableActivity.REQUEST_PLATE)
    }

    override fun getNotesFromPlate(table: Table?, position: Int) {
        startActivityForResult(NotesTableActivity.intent(this, table, position), TableActivity.REQUEST_NOTE)
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
        showFragmentTable(position)
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
            TablesActivity.REQUEST_TABLE -> {
                //Informo al fragment de la mesa seleccionada
                //Si salen con botón ACEPTAR
                if (resultCode == Activity.RESULT_OK) {
                    val typeReturn = TYPE_RETURN.RESULT_OK
                    val tableSelected = data?.getSerializableExtra(TableActivity.EXTRA_TABLE) as? Table
                    //Actualizo la mesa con los datos que traiga
                    val fragment = fragmentManager.findFragmentById(R.id.tables_fragment) as? TablesFragment
                    fragment?.tableSelectedReturn(tableSelected, typeReturn)
                    //Si salen con PositiveButton del Alert de la Factura
                } else {
                    if (resultCode == Activity.RESULT_FIRST_USER) {
                        //Borro la mesa de la lista. Está acabada
                        val typeReturn = TYPE_RETURN.RESULT_FIRST_USER
                        val tableSelected = data?.getSerializableExtra(TableActivity.EXTRA_TABLE) as? Table
                        val fragment = fragmentManager.findFragmentById(R.id.tables_fragment) as? TablesFragment
                        fragment?.tableSelectedReturn(tableSelected, typeReturn)
                    }
                }
            }
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

