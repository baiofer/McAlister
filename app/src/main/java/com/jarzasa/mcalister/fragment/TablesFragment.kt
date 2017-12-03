package com.jarzasa.mcalister.fragment

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import com.jarzasa.mcalister.R
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
                //Decirle a la actividad que añada una mesa
                mListener?.addTable()
                return true
            }
        //Opción borrar mesa
            R.id.erase_item_menu -> {
                //Decirle a la actividad que borre una mesa
                mListener?.deleteTable()
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

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // UTILIDADES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    //Pinto la ListView de las mesas
    fun drawList() {
        //Creo el adapter de la lista
        val adapter = ArrayAdapter<Table>(activity, android.R.layout.simple_list_item_1, Tables.toArray())
        root.findViewById<ListView>(R.id.tables_listView).adapter = adapter
        //Determino que hacer si me pulsan una celda de la lista
        root.findViewById<ListView>(R.id.tables_listView).setOnItemClickListener { _, _, position, _ ->
            //Presento la mesa que me han seleccionado con position
            //Le digo a la activity que hay que presentar la mesa seleccionada
            mListener?.tableSelected(position)
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // COMUNICACION ACTIVITY - FRAGMENT
    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Recibo las respuestas de las actividades a las que llamo
    //Añadir mesa
    fun addTableInTables(tableSelected: Table?, operation: Boolean?) {
        if (tableSelected != null) {
            if (operation == TablesActivity.ADD) {
                Tables.addTable(tableSelected)
            } else {
                Tables.actualizeTable(tableSelected)
            }
        }
        drawList()
    }

    //Borrar mesa
    fun deleteTableInTables(tableSelected: Table?) {
        if (tableSelected != null) {
            Tables.deleteTable(tableSelected)
            drawList()
        }
    }

    //Mesa seleccionada para mostrarla
    fun tableSelectedReturn(tableSelected: Table?, typeReturn: TablesActivity.TYPE_RETURN) {
        if (typeReturn == TablesActivity.TYPE_RETURN.RESULT_OK) {
            //Actualizo la mesa con los datos que traiga
            if (tableSelected != null) {
                Tables.actualizeTable(tableSelected)
                drawList()
            }
            //Si salen con PositiveButton del Alert de la Factura
        } else {
            if (typeReturn == TablesActivity.TYPE_RETURN.RESULT_FIRST_USER) {
                //Borro la mesa de la lista. Está acabada
                if (tableSelected != null) {
                    Tables.deleteTable(tableSelected)
                    drawList()
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // COMUNICACION FRAGMENT - ACTIVITY
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
        fun addTable()
        fun deleteTable()
        fun tableSelected(position: Int)
    }

}
