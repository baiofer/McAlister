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
import com.jarzasa.mcalister.model.Table
import com.jarzasa.mcalister.model.Tables

class TablesActivity : AppCompatActivity(), TablesFragment.OnFragmentInteractionListener {

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
    //COMUNICACION FRAGMENT - ACTIVITY
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
    //COMUNICACION ACTIVITY - FRAGMENT TABLES
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
                    //showFragmentTable(Tables.count()-1)
                }
            }
            TablesActivity.REQUEST_DELETE -> {
                //Informo al fragment de la mesa a borrar
                if (resultCode == Activity.RESULT_OK) {
                    val tableSelected = data?.getSerializableExtra(DeleteTableActivity.EXTRA_TABLE) as? Table
                    //Borro la mesa de la lista de mesas
                    val fragment = fragmentManager.findFragmentById(R.id.tables_fragment) as? TablesFragment
                    fragment?.deleteTableInTables(tableSelected)
                    //if (Tables.count() != 0) {
                    //    showFragmentTable(0)
                    //}
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
                //if (Tables.count() != 0) {
                //    showFragmentTable(0)
                //}
            }
        }
    }
}

