package com.jarzasa.mcalister.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.fragment.TablesFragment

class TablesActivity : AppCompatActivity(), TablesFragment.OnFragmentInteractionListener {


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

        //Llamamos al fragment
        if (fragmentManager.findFragmentById(R.id.tables_fragment) == null) {
            // Si hemos llegado aquí, sabemos que nunca hemos creado el MainFragment, lo creamos
            fragmentManager.beginTransaction()
                    .add(R.id.tables_fragment, TablesFragment.newInstance())
                    .commit()
        }
    }

    //Cerrar la actividad. Pulsado botón Back
    override fun cancelActivity() {
        finish()
    }







}
