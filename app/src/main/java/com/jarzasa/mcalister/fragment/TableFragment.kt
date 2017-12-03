package com.jarzasa.mcalister.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.activity.NotesTableActivity
import com.jarzasa.mcalister.activity.PlatesActivity
import com.jarzasa.mcalister.activity.TableActivity
import com.jarzasa.mcalister.adapter.TableRecyclerViewAdapter
import com.jarzasa.mcalister.model.Plate
import com.jarzasa.mcalister.model.Table
import com.jarzasa.mcalister.model.Tables

class TableFragment : Fragment() {

    companion object {

        private val ARG_POSITION = "ARG_POSITION"

        fun newInstance(position: Int): TableFragment {
            val fragment = TableFragment()
            val args = Bundle()
            args.putInt(ARG_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

    private var mListener: OnFragmentInteractionListener? = null
    var position: Int = 0
    lateinit var root: View
    var table: Table? = null
    lateinit var platesList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            position = arguments!!.getInt(ARG_POSITION)
            table = Tables[position]
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_table, container, false)

        //Pintamos el título de la mesa
        val title = root.findViewById<TextView>(R.id.table_title)
        title.text = getString(R.string.table_name) + table?.number

        //Pintamos la lista de platos de la mesa
        drawList()

        //Detectamos la pulsación de los botones
        //Se aceptan los datos de la mesa. Se devuelve la mesa con sus valores
        root.findViewById<Button>(R.id.ok_table_button).setOnClickListener { mListener?.okTable(table) }
        //No se aceptan los datos de la mesa. No se devuelve nada. La mesa queda como estuviese
        root.findViewById<Button>(R.id.cancel_table_button).setOnClickListener { mListener?.cancelTable() }
        //Se va a la lista de platos para seleccionar un plato
        root.findViewById<Button>(R.id.plates_table_button).setOnClickListener { mListener?.selectPlateFromPlates() }

        //Detectamos el floatingActionButton
        root.findViewById<FloatingActionButton?>(R.id.bill_button)?.setOnClickListener { _: View ->
            //Código asociado al botón flotante
            calculeBill()
        }
        return root
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // UTILIDADES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    //Mostrar una mesa en la interfaz
    fun showTable(tableShow: Table, positionShow: Int) {
        table = tableShow
        position = positionShow
        //Pintamos el título de la mesa
        val title = root.findViewById<TextView>(R.id.table_title)
        title.text = getString(R.string.table_name) + table?.number

        //Pintamos la lista de platos de la mesa
        drawList()
    }
    //Se pinta el RecyclerView con los platos seleccionados de la mesa
    fun drawList() {
        platesList = root.findViewById(R.id.table_plates)
        //Definimos el RecyclerView
        platesList.layoutManager = LinearLayoutManager(activity)
        platesList.itemAnimator = DefaultItemAnimator()
        //Le damos el Adapter al RecyclerView
        val adapter = TableRecyclerViewAdapter(table?.plates)
        platesList.adapter = adapter
        //Le digo al RecyclerViewAdapter que me informe cuando pulsen una de sus vistas
        adapter.onClickListener = View.OnClickListener { v: View? ->
            //Aqui me entero de que se ha pulsado una de las vistas
            val position = platesList.getChildAdapterPosition(v)
            //Pedimos las notas y cantidad de un plato
            //Lanzamos la actividad NotesTable
            mListener?.getNotesFromPlate(table, position)
        }
        //Si no hay platos seleccionados en la mesa, no mostramos el FloatingActionButton
        if (table?.plates?.size == 0) {
            setBillButton(TableActivity.OFF)
        } else {
            setBillButton(TableActivity.ON)
        }
    }

    //Determino si mostrar o no el botón flotante (Si no hay platos en la lista de platos, no lo muestro)
    fun setBillButton(visibility: Boolean) {
        if (visibility == TableActivity.ON) {
            root.findViewById<FloatingActionButton>(R.id.bill_button).visibility = View.VISIBLE
        } else {
            root.findViewById<FloatingActionButton>(R.id.bill_button).visibility = View.INVISIBLE
        }
    }

    //Calculo el importe total de la mesa
    fun calculeBill() {
        val list = table?.plates
        var totalBill = 0.0
        for (plateIndex in 0..list!!.size - 1 ) {
            val total = list[plateIndex].quantity * list[plateIndex].price
            totalBill = totalBill + total
        }
        //Presento el importe total. Con NegativeButton no hago nada y con positiveButton vuelvo a la
        //actividad que llamó a esta (TablesActivity)
        AlertDialog.Builder(activity)
                .setTitle("FACTURA")
                .setMessage("El importe de su selección es: ${totalBill}")
                .setPositiveButton("Aceptar", { _, _ ->
                    mListener?.deleteTable(table)
                })
                .setNegativeButton("Cancelar", { _, _ ->  })
                .show()
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // COMUNICACION ACTIVITY - FRAGMENT
    ///////////////////////////////////////////////////////////////////////////////////////////////

    fun addPlateSelectedToTable(plateSelected: Plate?) {
        if (plateSelected != null) {
            plateSelected.quantity = 1
            table?.addPlate(plateSelected)
            drawList()
        }
    }

    fun addNotesToPlate(tableSelected: Table?) {
        table = tableSelected
        drawList()
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
        fun okTable(table: Table?)
        fun cancelTable()
        fun deleteTable(table: Table?)
        fun selectPlateFromPlates()
        fun getNotesFromPlate(table: Table?, position: Int)
    }


}
