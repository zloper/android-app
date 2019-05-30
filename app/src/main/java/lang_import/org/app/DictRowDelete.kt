package lang_import.org.app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import org.jetbrains.anko.db.*
import java.lang.Exception
import android.support.design.widget.TextInputEditText as EditTxt
import org.jetbrains.anko.startActivity as start


class DictRowDelete : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dict_row_delete)
        viewManager = LinearLayoutManager(this)
        val dictName = intent.extras.getString("dictName")

        val completeBtn = findViewById<Button>(R.id.dict_complete)
        val ref = findViewById<EditTxt>(R.id.newRef)

        completeBtn.setOnClickListener {
            try {
                database.use {
                    delete(dictName, "ref = {KEY}", "KEY" to ref.getText().toString())
                }
            } catch (e: Exception) {
                Log.e("DB_RM_ERROR", e.toString())
            }

            finish()
            start<DictShowActivity>("dictName" to dictName)
        }
    }

}

