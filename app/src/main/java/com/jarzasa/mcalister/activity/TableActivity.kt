package com.jarzasa.mcalister.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
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
            //Código asociado al botón flotante
            calculeBill()
        }
    }

    //Se aceptan los datos de la mesa. Se devuelve la mesa con sus valores
    fun acceptTable() {
        val returnIntent = Intent()
        returnIntent.putExtra(TableActivity.EXTRA_TABLE, table) as? Serializable
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    //No se aceptan los datos de la mesa. No se devuelve nada. La mesa queda como estuviese
    fun cancelTable() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    //Se va a la lista de platos para seleccionar un plato
    fun addPlateToTable() {
        startActivityForResult(PlatesActivity.intent(this), REQUEST_PLATE)
    }

    //Se pinta el RecyclerView con los platos seleccionados de la mesa
    fun drawList() {
        platesList = findViewById(R.id.table_plates)
        //Definimos el RecyclerView
        platesList.layoutManager = LinearLayoutManager(this)
        platesList.itemAnimator = DefaultItemAnimator()
        //Le damos el Adapter al RecyclerView
        val adapter = TableRecyclerViewAdapter(table?.plates)
        platesList.adapter = adapter
        //Le digo al RecyclerViewAdapter que me informe cuando pulsen una de sus vistas
        adapter.onClickListener = View.OnClickListener { v: View? ->
            //Aqui me entero de que se ha pulsado una de las vistas
            val position = platesList.getChildAdapterPosition(v)
            //Lanzamos la actividad NotesTable
            startActivityForResult(NotesTableActivity.intent(this, table, position), REQUEST_NOTE)
        }
        //Si no hay platos seleccionados en la mesa, no mostramos el FloatingActionButton
        if (table?.plates?.size == 0) {
            setBillButton(OFF)
        } else {
            setBillButton(ON)
        }
    }

    //Recibo las respuestas de las actividades a las que llamo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_PLATE -> {
                //Recibo elplato seleccionado y lo añado a la lista de platos, con cantidad a 1
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
                //Recibo la mesa con los datos ya cambiados (cantidad y notas)
                if (resultCode == Activity.RESULT_OK) {
                    table = data?.getSerializableExtra(NotesTableActivity.EXTRA_TABLE) as? Table
                    drawList()
                }
            }
        }
    }

    //Determino si mostrar o no el botón flotante (Si no hay platos en la lista de platos, no lo muestro)
    fun setBillButton(visibility: Boolean) {
        if (visibility == ON) {
            findViewById<FloatingActionButton>(R.id.bill_button).visibility = View.VISIBLE
        } else {
            findViewById<FloatingActionButton>(R.id.bill_button).visibility = View.INVISIBLE
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
