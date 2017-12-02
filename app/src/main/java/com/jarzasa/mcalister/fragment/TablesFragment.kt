package com.jarzasa.mcalister.fragment

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.activity.AddTableActivity
import com.jarzasa.mcalister.activity.DeleteTableActivity
import com.jarzasa.mcalister.activity.TableActivity
import com.jarzasa.mcalister.activity.TablesActivity
import com.jarzasa.mcalister.model.Table
import com.jarzasa.mcalister.model.Tables

class TablesFragment : Fragment() {

    companion object {

        fun newInstance() = TablesFragment()
    }

    private var mListener: OnFragmentInteractionListener? = null
    lateinit var root: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_tables, container, false)

        //Activo el botón Back de la supportActionBar
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Pinto la lista de mesas
        drawList()

        return root
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // MENU
    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_tables, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
        //Opción Añadir/Modificar mesa
            R.id.add_table_menu -> {
                val intent = AddTableActivity.intent(activity)
                startActivityForResult(intent, TablesActivity.REQUEST_ADD)
                return true
            }
        //Opción borrar mesa
            R.id.erase_item_menu -> {
                val intent = DeleteTableActivity.intent(activity)
                startActivityForResult(intent, TablesActivity.REQUEST_DELETE)
                return true
            }
        //Opción botón Back
            android.R.id.home -> {
                mListener?.cancelActivity()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // FIN MENU
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Recibo las respuestas de las actividades a las que llamo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            TablesActivity.REQUEST_ADD -> {
                //Añado la mesa a la lista de mesas si no existia o la actualizo si ya existia
                if (resultCode == Activity.RESULT_OK) {
                    val tableSelected = data?.getSerializableExtra(AddTableActivity.EXTRA_TABLE) as? Table
                    val operation = data?.getBooleanExtra(AddTableActivity.EXTRA_OPTION, TablesActivity.ADD)
                    if (tableSelected != null) {
                        if (operation == TablesActivity.ADD) {
                            Tables.addTable(tableSelected)
                        } else {
                            Tables.actualizeTable(tableSelected)
                        }
                        drawList()
                    }
                } else { }
            }
            TablesActivity.REQUEST_DELETE -> {
                //Borro la mesa de la lista de mesas
                if (resultCode == Activity.RESULT_OK) {
                    val tableSelected = data?.getSerializableExtra(DeleteTableActivity.EXTRA_TABLE) as? Table
                    if (tableSelected != null) {
                        Tables.deleteTable(tableSelected)
                        drawList()
                    }
                } else { }
            }
            TablesActivity.REQUEST_TABLE -> {
                //Si salen con botón ACEPTAR
                if (resultCode == Activity.RESULT_OK) {
                    //Actualizo la mesa con los datos que traiga
                    val tableSelected = data?.getSerializableExtra(TableActivity.EXTRA_TABLE) as? Table
                    if (tableSelected != null) {
                        Tables.actualizeTable(tableSelected)
                        drawList()
                    }
                    //Si salen con PositiveButton del Alert de la Factura
                } else {
                    if (resultCode == Activity.RESULT_FIRST_USER) {
                        //Borro la mesa de la lista. Está acabada
                        val tableSelected = data?.getSerializableExtra(TableActivity.EXTRA_TABLE) as? Table
                        if (tableSelected != null) {
                            Tables.deleteTable(tableSelected)
                            drawList()
                        }
                    }
                }
            }
        }
    }

    //Pinto la ListView de las mesas
    fun drawList() {
        //Creo el adapter de la lista
        val adapter = ArrayAdapter<Table>(activity, android.R.layout.simple_list_item_1, Tables.toArray())
        root.findViewById<ListView>(R.id.tables_listView).adapter = adapter
        //Determino que hacer si me pulsan una celda de la lista
        root.findViewById<ListView>(R.id.tables_listView).setOnItemClickListener { _, _, position, _ ->
            //Presento la mesa que me han seleccionado con position
            startActivityForResult(TableActivity.intent(activity, position), TablesActivity.REQUEST_TABLE)
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // COMUNICATION WHIT ACTIVITIES AND FRAGMENTS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        commonOnAttach(context)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        commonOnAttach(activity)
    }

    fun commonOnAttach(context: Context?) {
        // Aquí nos llaman cuando el fragment "se engancha" a la actividad, y por tanto ya pertence a ella
        // Lo que vamos a hacer es quedarnos con la referencia a esa actividad para cuando tengamos que avisarle de "cosas"
        if (context is OnFragmentInteractionListener) {
            mListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun cancelActivity()
    }

}
