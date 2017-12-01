package com.jarzasa.mcalister.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.model.Table
import com.jarzasa.mcalister.model.Tables
import kotlinx.android.synthetic.main.activity_add_table.*
import java.io.Serializable

class AddTableActivity : AppCompatActivity() {

    companion object {
        val EXTRA_TABLE = "EXTRA_TABLE"
        val EXTRA_OPTION = "EXTRA_OPTION"
        val ADD = false
        val ACTUALIZE = true

        fun intent(context: Context) = Intent(context, AddTableActivity::class.java)
    }

    var number: Int = 0
    var person: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_table)


        ok_add_button.setOnClickListener { acceptTable() }
        cancel_add_button.setOnClickListener { cancelTable() }
    }

    private fun cancelTable() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun acceptTable() {
        val table = validateData()
        val returnIntent = Intent()
        returnIntent.putExtra(EXTRA_TABLE, table) as? Serializable

        if (Tables.isTable(number)) {
            returnIntent.putExtra(EXTRA_OPTION, ADD)
        } else {
            returnIntent.putExtra(EXTRA_OPTION, ACTUALIZE)
        }

        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    fun validateData(): Table? {
        number = table_number_entrance.text.toString().toInt()
        person = table_persons_entrance.text.toString().toInt()
        return Table(number, person, mutableListOf())
    }
}
