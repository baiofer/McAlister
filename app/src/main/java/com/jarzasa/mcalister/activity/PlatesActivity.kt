package com.jarzasa.mcalister.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.jarzasa.mcalister.adapter.PlatesRecyclerViewAdapter
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.model.Plate
import kotlinx.android.synthetic.main.activity_plates.*
import java.io.Serializable

class PlatesActivity : AppCompatActivity() {

    companion object {
        val REQUEST_VIEW = 1
        val EXTRA_PLATE = "EXTRA_PLATE"

        fun intent(context: Context) = Intent(context, PlatesActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plates)

        //Activo el botón de back de la barra
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Creamos el RecyclerView con la lista de platos
        //Montamos el RecyclerView
        // Le decimos su LayoutManager
        plates_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        // Le decimos cómo se anima
        plates_list.itemAnimator = DefaultItemAnimator()
        // Creamos el adapter
        val adapter = PlatesRecyclerViewAdapter()
        // Le decimos qué pasa cuando se pulsa un elemento del adapter
        adapter.buttonListener = object: PlatesRecyclerViewAdapter.ButtonListener {
            override fun buttonSelectPressed(plate: Plate) {
                val returnIntent = Intent()
                returnIntent.putExtra(TableActivity.EXTRA_PLATE, plate) as? Serializable
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
            override fun buttonViewPressed(plate: Plate) {
                val intent = PlateDescriptionActivity.intent(this@PlatesActivity, plate)
                startActivity(intent)
            }
        }
        // Le decimos su adapter
        plates_list.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_VIEW -> {
                if (resultCode == Activity.RESULT_OK) {
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
