package com.jarzasa.mcalister.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.model.Table
import com.jarzasa.mcalister.model.Tables
import kotlinx.android.synthetic.main.activity_delete_table.*
import java.io.Serializable

class DeleteTableActivity : AppCompatActivity() {

    companion object {
        val EXTRA_TABLE = "EXTRA_TABLE"
        fun intent(context: Context) = Intent(context, DeleteTableActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_table)

        ok_erase_button.setOnClickListener { acceptTable() }
        cancel_erase_button.setOnClickListener { cancelTable() }
    }

    private fun cancelTable() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun acceptTable() {
        val table = validateData()
        val returnIntent = Intent()
        returnIntent.putExtra(AddTableActivity.EXTRA_TABLE, table) as? Serializable
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    fun validateData(): Table? {
        val number = table_number_erase.text.toString().toInt()
        if (Tables.isTable(number)) {
            return null
        }
        return Table(number, 1, mutableListOf())
    }
}
