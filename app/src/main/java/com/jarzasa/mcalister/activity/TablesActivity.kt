package com.jarzasa.mcalister.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.jarzasa.mcalister.R
import com.jarzasa.mcalister.model.Table
import com.jarzasa.mcalister.model.Tables
import kotlinx.android.synthetic.main.activity_tables.*

class TablesActivity : AppCompatActivity() {

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

        //Activo el botón de back de la barra
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        drawList()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // MENU
    ////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_tables, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.add_table_menu -> {
                val intent = AddTableActivity.intent(this)
                startActivityForResult(intent, REQUEST_ADD)
                return true
            }
            R.id.erase_item_menu -> {
                val intent = DeleteTableActivity.intent(this)
                startActivityForResult(intent, REQUEST_DELETE)
                return true
            }
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_ADD -> {
                if (resultCode == Activity.RESULT_OK) {
                    val tableSelected = data?.getSerializableExtra(AddTableActivity.EXTRA_TABLE) as? Table
                    val operation = data?.getBooleanExtra(AddTableActivity.EXTRA_OPTION, ADD)
                    if (tableSelected != null) {
                        if (operation == ADD) {
                            Tables.addTable(tableSelected)
                        } else {
                            Tables.actualizeTable(tableSelected)
                        }
                        drawList()
                    }
                } else { }
            }
            REQUEST_DELETE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val tableSelected = data?.getSerializableExtra(DeleteTableActivity.EXTRA_TABLE) as? Table
                    if (tableSelected != null) {
                        Tables.deleteTable(tableSelected)
                        drawList()
                    }
                } else { }
            }
            REQUEST_TABLE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val tableSelected = data?.getSerializableExtra(TableActivity.EXTRA_TABLE) as? Table
                    if (tableSelected != null) {
                        Tables.actualizeTable(tableSelected)
                        drawList()
                    }
                } else {
                    if (resultCode == Activity.RESULT_FIRST_USER) {
                        val tableSelected = data?.getSerializableExtra(TableActivity.EXTRA_TABLE) as? Table
                        if (tableSelected != null) {
                            Tables.deleteTable(tableSelected)
                            drawList()
                        }
                    }
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun drawList() {
        val adapter = ArrayAdapter<Table>(this, android.R.layout.simple_list_item_1, Tables.toArray())
        tables_listView.adapter = adapter

        tables_listView.setOnItemClickListener { parent, view, position, id ->
            startActivityForResult(TableActivity.intent(this, position), REQUEST_TABLE)
        }
    }
}
