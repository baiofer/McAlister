package com.jarzasa.mcalister.activity

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ViewSwitcher
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.model.Plate
import com.jarzasa.mcalister.model.Plates
import kotlinx.android.synthetic.main.activity_mc_alister.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import org.json.JSONObject
import java.net.URL
import java.util.*

class McAlisterActivity : AppCompatActivity() {

    enum class VIEW_INDEX (val index: Int) {
        LOADING(0),
        PLATES(1)
    }

    private lateinit var viewSwitcher: ViewSwitcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mc_alister)

        //Creamos el ViewSwitcher que controla la pantalla principal y la de carga de datos
        viewSwitcher = findViewById(R.id.view_switcher)
        viewSwitcher.setInAnimation(this, android.R.anim.fade_in)
        viewSwitcher.setOutAnimation(this, android.R.anim.fade_out)

        //Si pulsan el botón OK, entro en la aplicación
        in_button.setOnClickListener { enterApp() }

        //Si no hay carta de platos descargada, la descargo
        if (Plates.count() == 0) {
            updatePlates()
        }
    }

    //Voy a la actividad primera. La lista de mesas
    private fun enterApp() {
        val intent = TablesActivity.intent(this)
        startActivity(intent)
    }

    //Carga de datos de internet en background
    private fun updatePlates() {
        //Presento la vista de descarga
        viewSwitcher.displayedChild = VIEW_INDEX.LOADING.index
        async(UI) {
            val newPlates: Deferred<MutableList<Plate>> = bg {
                //Parte que se hace en background
                Thread.sleep(2000)
                downloadPlates()
            }
            //Espero hasta que la descarga esté hecha
            val downPlates = newPlates.await()
            //Presento la vista de acceso a la App
            viewSwitcher.displayedChild = VIEW_INDEX.PLATES.index
            //Si se han descargado los platos, actualizo el modelo con los platos descargados
            if (downPlates.size != 0) {
                Plates.platesDownloaded = downPlates
            } else {
                //Si no se han descargado los platos, saco una alerta con opción de reintentarlo  o salir
                AlertDialog.Builder(this@McAlisterActivity)
                        .setTitle("ERROR")
                        .setMessage("No se ha podido descargar la carta de platos")
                        .setPositiveButton("Reintentar", { dialog, _ ->
                            dialog.dismiss()
                            updatePlates() })
                        .setNegativeButton("Salir", { _, _ -> finish() })
                        .show()
            }
        }
    }
    //Parte de la descarga y parcheo de los datos recibidos. Se realiza en background
    private fun downloadPlates(): MutableList<Plate> {
        try {
            //Descargamos los datos
            val platesDown: MutableList<Plate> = mutableListOf()
            val url = URL("http://www.mocky.io/v2/5a1999eb300000a32f63f4ed")
            val jsonString = Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next()

            //Analizamos los datos que acabamos de descargarnos
            val jsonRoot = JSONObject(jsonString.toString())
            val results = jsonRoot.getJSONArray("results")
            for (plateIndex in 0..results.length() - 1) {
                val plateReceived = results.getJSONObject(plateIndex)
                val number = plateReceived.getInt("number")
                val name = plateReceived.getString("name")
                val ingredients = plateReceived.getString("ingredients")
                val allergens = plateReceived.getString("allergens")
                val photo = plateReceived.getString("photo")
                val recipe = plateReceived.getString("recipe")
                val price = plateReceived.getDouble("price")
                val quantity = plateReceived.getInt("quantity")
                val notes = plateReceived.getString("notes")
                val plate = Plate(number, name, ingredients, allergens, photo, recipe, price, quantity, notes)
                platesDown.add(plate)
            }
            //Devolvemos los platos descargados ya parseados
            return platesDown
        } catch(ex: Exception) {
            ex.printStackTrace()
        }
        //Si ha habido error, devolvemos la lista de platos descargados vacia
        return mutableListOf()
    }

}
