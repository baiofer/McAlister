package com.jarzasa.mcalister.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.model.Plate
import com.jarzasa.mcalister.model.Plates

class PlatesRecyclerViewAdapter(): RecyclerView.Adapter<PlatesRecyclerViewAdapter.PlateViewHolder>() {

    lateinit var view: View
    var buttonListener: ButtonListener? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlateViewHolder {
        // Creamos la vista plantilla que más tarde se rellenará con el plato en cuestión que se quiera mostrar
        view = LayoutInflater.from(parent?.context).inflate(R.layout.cell_plate, parent, false)

        // Le decimos a la vista de este ViewHolder a quién llamar cuando se le pulse
        val viewHolder = PlateViewHolder(view)
        viewHolder.buttonListener = buttonListener

        return viewHolder
    }

    override fun getItemCount(): Int {
        return Plates.count()
    }

    override fun onBindViewHolder(holder: PlateViewHolder?, position: Int) {
        // Le decimos al ViewHolder (la "fila") que "pinte" el número en ella, eso lo hacemos con la llamada a bindNumber
        holder?.bindPlate(Plates[position])
    }

    inner class PlateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textPlate = itemView.findViewById<TextView>(R.id.plate_name)
        val buttonSelect  = itemView.findViewById<Button>(R.id.select_plate)
        val buttonView = itemView.findViewById<Button>(R.id.view_plate)
        var buttonListener: ButtonListener? = null

        fun bindPlate(plate: Plate?) {
            textPlate.text = plate?.name ?: ""

            buttonSelect.setOnClickListener {
                if (plate != null) {
                    buttonListener?.buttonSelectPressed(plate)
                }
            }
            buttonView.setOnClickListener {
                if (plate != null) {
                    buttonListener?.buttonViewPressed(plate)
                }
            }
        }
    }

    interface ButtonListener {
        fun buttonViewPressed(plate: Plate)
        fun buttonSelectPressed(plate: Plate)
    }
}