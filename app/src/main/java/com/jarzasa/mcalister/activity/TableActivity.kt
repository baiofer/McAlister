package com.jarzasa.mcalister.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.TextView
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.adapter.TableRecyclerViewAdapter
import com.jarzasa.mcalister.model.Plate
import com.jarzasa.mcalister.model.Table
import com.jarzasa.mcalister.model.Tables
import kotlinx.android.synthetic.main.activity_table.*
import java.io.Serializable

class TableActivity : AppCompatActivity() {

    companion object {
        val REQUEST_PLATE = 1
        val REQUEST_NOTE = 2
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

    var table: Table? = null
    var plateSelected: Int = 0
    lateinit var platesList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)

        //Recibimos la mesa a mostrar
        val posicion = intent.getIntExtra(EXTRA_TABLE, 0)
        table = Tables[posicion]

        //Pintamos el título de la mesa
        table_title.text = getString(R.string.table_name) + table?.number

        //Pintamos la lista de platos de la mesa
        drawList()

        //Detectamos la pulsación de los botones
        ok_table_button.setOnClickListener { acceptTable() }
        cancel_table_button.setOnClickListener { cancelTable() }
        plates_table_button.setOnClickListener { addPlateToTable() }

        //Detectamos el floatingActionButton
        findViewById<FloatingActionButton?>(R.id.bill_button)?.setOnClickListener { v: View ->
            //Aquí ejecutaria el código asociado al botón flotante
            calculeBill()
        }
    }

    fun acceptTable() {
        val returnIntent = Intent()
        returnIntent.putExtra(TableActivity.EXTRA_TABLE, table) as? Serializable
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    fun cancelTable() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    fun addPlateToTable() {
        startActivityForResult(PlatesActivity.intent(this), REQUEST_PLATE)
    }

    fun drawList() {
        /*
        val adapter = ArrayAdapter<Plate>(this, android.R.layout.simple_list_item_1, table?.toArray())
        table_plates.adapter = adapter

        table_plates.setOnItemClickListener { parent, view, position, id ->
            plateSelected = position
            startActivityForResult(NotesTableActivity.intent(this, table, position), REQUEST_NOTE)
        }
        //Si no hay platos seleccionados en la mesa, no mostramos el FloatingActionButton
        if (table?.plates?.size == 0) {
            setBillButton(OFF)
        } else {
            setBillButton(ON)
        }
        */
        platesList = findViewById(R.id.table_plates)
        platesList.layoutManager = LinearLayoutManager(this)
        platesList.itemAnimator = DefaultItemAnimator()

        val adapter = TableRecyclerViewAdapter(table?.plates)
        platesList.adapter = adapter
        //Le digo al RecyclerViewAdapter que me informe cuando pulsen una de sus vistas
        adapter.onClickListener = View.OnClickListener { v: View? ->
            //Aqui me entero de que se ha pulsado una de las vistas
            val position = platesList.getChildAdapterPosition(v)
            //Lanzamos la actividad detalle
            startActivityForResult(NotesTableActivity.intent(this, table, position), REQUEST_NOTE)
        }
        //Si no hay platos seleccionados en la mesa, no mostramos el FloatingActionButton
        if (table?.plates?.size == 0) {
            setBillButton(OFF)
        } else {
            setBillButton(ON)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PLATE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val plateSelected = data?.getSerializableExtra(PlatesActivity.EXTRA_PLATE) as? Plate
                    if (plateSelected != null) {
                        plateSelected.quantity = 1
                        table?.addPlate(plateSelected)
                        drawList()
                    }
                }
            }
            REQUEST_NOTE -> {
                if (resultCode == Activity.RESULT_OK) {
                    table = data?.getSerializableExtra(NotesTableActivity.EXTRA_TABLE) as? Table
                    drawList()
                }
            }
        }
    }

    fun setBillButton(visibility: Boolean) {
        if (visibility == ON) {
            findViewById<FloatingActionButton>(R.id.bill_button).visibility = View.VISIBLE
        } else {
            findViewById<FloatingActionButton>(R.id.bill_button).visibility = View.INVISIBLE
        }
    }

    fun calculeBill() {
        val list = table?.plates
        var totalBill = 0.0
        for (plateIndex in 0..list!!.size - 1 ) {
            val total = list[plateIndex].quantity * list[plateIndex].price
            totalBill = totalBill + total
        }
        AlertDialog.Builder(this@TableActivity)
                .setTitle("FACTURA")
                .setMessage("El importe de su selección es: ${totalBill}")
                .setPositiveButton("Aceptar", { dialog, _ ->
                    val returnIntent = Intent()
                    returnIntent.putExtra(EXTRA_TABLE, table) as? Serializable
                    setResult(Activity.RESULT_FIRST_USER, returnIntent)
                    finish()
                    })
                .setNegativeButton("Cancelar", { _, _ ->  })
                .show()
    }
}
