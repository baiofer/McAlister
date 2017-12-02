package com.jarzasa.mcalister.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.fragment.TableFragment
import com.jarzasa.mcalister.model.Table
import java.io.Serializable

class TableActivity : AppCompatActivity(), TableFragment.OnFragmentInteractionListener {


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
}

