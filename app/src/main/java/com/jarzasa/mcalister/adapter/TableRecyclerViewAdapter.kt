package com.jarzasa.mcalister.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.model.Plate

//Adapter del RecyclerView de la lista de mesas (Tables)
class TableRecyclerViewAdapter(var plates: MutableList<Plate>?): RecyclerView.Adapter<TableRecyclerViewAdapter.TableViewHolder>() {

    lateinit var view: View
    var onClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TableRecyclerViewAdapter.TableViewHolder {
        view = LayoutInflater.from(parent?.context).inflate(R.layout.cell_table, parent, false)
        view.setOnClickListener(onClickListener)
        return TableViewHolder(view)
    }

    override fun getItemCount(): Int {
        return plates?.count() ?: 0
    }

    override fun onBindViewHolder(holder: TableViewHolder?, position: Int) {
        holder?.bindPlate(plates?.get(position))
    }


    inner class TableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val namePlate = itemView.findViewById<TextView>(R.id.cell_table_name)
        val quantityPlate = itemView.findViewById<TextView>(R.id.cell_table_quantity)
        val pricePlate = itemView.findViewById<TextView>(R.id.cell_plate_price)
        val imagePlate = itemView.findViewById<ImageView>(R.id.cell_table_image)
        val notesPlate = itemView.findViewById<TextView>(R.id.cell_table_notes)

        fun bindPlate(plate: Plate?) {

            namePlate.text = plate?.name ?: ""
            quantityPlate.text = plate?.quantity.toString()
            pricePlate.text = (plate?.quantity!! * plate.price).toString()
            when (plate.photo) {
                "ajoblanco.jpg" -> imagePlate.setImageResource(R.drawable.ajoblanco)
                "albondigas_en_salsa_de_almendras.jpg" -> imagePlate.setImageResource(R.drawable.albondigas_en_salsa_de_almendras)
                "alcachofas_con_jamon_iberico_a_la_montillana.jpg" -> imagePlate.setImageResource(R.drawable.alcachofas_con_jamon_iberico_a_la_montillana)
                "alubias_con_almejas.jpg" -> imagePlate.setImageResource(R.drawable.alubias_con_almejas)
                "arroz_chino.jpg" -> imagePlate.setImageResource(R.drawable.arroz_chino)
                "atun_encebollado.jpg" -> imagePlate.setImageResource(R.drawable.atun_encebollado)
                "carrillada_iberica_en_salsa_de_canela.jpg" -> imagePlate.setImageResource(R.drawable.carrillada_iberica_en_salsa_de_canela)
                "cocido_madrileno.jpg" -> imagePlate.setImageResource(R.drawable.cocido_madrileno)
                "conejo_al_ajillo.jpg" -> imagePlate.setImageResource(R.drawable.conejo_al_ajillo)
                "costillas_de_cerdo_asadas_al_horno_con_salsa_barbacoa.jpg" -> imagePlate.setImageResource(R.drawable.costillas_de_cerdo_asadas_al_horno_con_salsa_barbacoa)
                "crema_de_calabaza.jpg" -> imagePlate.setImageResource(R.drawable.crema_de_calabaza)
                "ensalada_de_pasta_con_atun_y_gambas.jpg" -> imagePlate.setImageResource(R.drawable.ensalada_de_pasta_con_atun_y_gambas)
                "ensaladilla_rusa.jpg" -> imagePlate.setImageResource(R.drawable.ensaladilla_rusa)
                "espinacas_con_garbanzos.jpg" -> imagePlate.setImageResource(R.drawable.espinacas_con_garbanzos)
                "flan_de_chocolate_blanco.jpg" -> imagePlate.setImageResource(R.drawable.flan_de_chocolate_blanco)
                "lomo_de_bacalao_con_tomate.jpg" -> imagePlate.setImageResource(R.drawable.lomo_de_bacalao_con_tomate)
                "merluza_al_cava.jpg" -> imagePlate.setImageResource(R.drawable.merluza_al_cava)
                "mero_en_salsa_de_setas.jpg" -> imagePlate.setImageResource(R.drawable.mero_en_salsa_de_setas)
                "pechuga_de_pollo_en_salsa_de_almendras_y_curry.jpg" -> imagePlate.setImageResource(R.drawable.pechuga_de_pollo_en_salsa_de_almendras_y_curry)
                "tarta_de_queso_con_mermelada_de_fresa.jpg" -> imagePlate.setImageResource(R.drawable.tarta_de_queso_con_mermelada_de_fresa)
            }
            notesPlate.text = plate.notes
        }
    }
}