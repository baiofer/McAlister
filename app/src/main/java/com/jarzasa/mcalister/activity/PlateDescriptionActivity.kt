package com.jarzasa.mcalister.activity

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.model.Plate
import kotlinx.android.synthetic.main.activity_plate_description.*
import android.graphics.Bitmap




class PlateDescriptionActivity : AppCompatActivity() {

    companion object {
        val EXTRA_PLATE = "EXTRA_PLATE"

        fun intent(context: Context, plate: Plate): Intent {
            val intent = Intent(context, PlateDescriptionActivity::class.java)
            intent.putExtra(EXTRA_PLATE, plate)
            return intent
        }
    }

    var plate: Plate? = null
        set(value) {
            field = value
            //Actualizamos la vista con el modelo
            value?.let {
                plate_number.text = "${value.number}"
                plate_name.text = value.name
                ingredients_plate.text = value.ingredients
                allergens_plate.text = value.allergens
                recipe_plate.text = value.recipe
                price_plate.text = getString(R.string.price_format, value.price)
                when (value.photo) {
                    "ajoblanco.jpg" -> photo_plate.setImageResource(R.drawable.ajoblanco)
                    "albondigas_en_salsa_de_almendras.jpg" -> photo_plate.setImageResource(R.drawable.albondigas_en_salsa_de_almendras)
                    "alcachofas_con_jamon_iberico_a_la_montillana.jpg" -> photo_plate.setImageResource(R.drawable.alcachofas_con_jamon_iberico_a_la_montillana)
                    "alubias_con_almejas.jpg" -> photo_plate.setImageResource(R.drawable.alubias_con_almejas)
                    "arroz_chino.jpg" -> photo_plate.setImageResource(R.drawable.arroz_chino)
                    "atun_encebollado.jpg" -> photo_plate.setImageResource(R.drawable.atun_encebollado)
                    "carrillada_iberica_en_salsa_de_canela.jpg" -> photo_plate.setImageResource(R.drawable.carrillada_iberica_en_salsa_de_canela)
                    "cocido_madrileno.jpg" -> photo_plate.setImageResource(R.drawable.cocido_madrileno)
                    "conejo_al_ajillo.jpg" -> photo_plate.setImageResource(R.drawable.conejo_al_ajillo)
                    "costillas_de_cerdo_asadas_al_horno_con_salsa_barbacoa.jpg" -> photo_plate.setImageResource(R.drawable.costillas_de_cerdo_asadas_al_horno_con_salsa_barbacoa)
                    "crema_de_calabaza.jpg" -> photo_plate.setImageResource(R.drawable.crema_de_calabaza)
                    "ensalada_de_pasta_con_atun_y_gambas.jpg" -> photo_plate.setImageResource(R.drawable.ensalada_de_pasta_con_atun_y_gambas)
                    "ensaladilla_rusa.jpg" -> photo_plate.setImageResource(R.drawable.ensaladilla_rusa)
                    "espinacas_con_garbanzos.jpg" -> photo_plate.setImageResource(R.drawable.espinacas_con_garbanzos)
                    "flan_de_chocolate_blanco.jpg" -> photo_plate.setImageResource(R.drawable.flan_de_chocolate_blanco)
                    "lomo_de_bacalao_con_tomate.jpg" -> photo_plate.setImageResource(R.drawable.lomo_de_bacalao_con_tomate)
                    "merluza_al_cava.jpg" -> photo_plate.setImageResource(R.drawable.merluza_al_cava)
                    "mero_en_salsa_de_setas.jpg" -> photo_plate.setImageResource(R.drawable.mero_en_salsa_de_setas)
                    "pechuga_de_pollo_en_salsa_de_almendras_y_curry.jpg" -> photo_plate.setImageResource(R.drawable.pechuga_de_pollo_en_salsa_de_almendras_y_curry)
                    "tarta_de_queso_con_mermelada_de_fresa.jpg" -> photo_plate.setImageResource(R.drawable.tarta_de_queso_con_mermelada_de_fresa)
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plate_description)

        //Recibimos el plato a mostrar
        plate = intent.getSerializableExtra(PlateDescriptionActivity.EXTRA_PLATE) as Plate

        //Activo el botón de back de la barra
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //Si pulsan el botón Back, salgo de la actividad sin mas y vuelvo a la anterior
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
