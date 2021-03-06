package lang_import.org.app

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.widget.*
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.article_activity.*
import kotlinx.android.synthetic.main.informers_menu.*
import kotlinx.android.synthetic.main.informers_menu.back_btn
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.select
import java.lang.Exception


class InformersMenu : AppCompatActivity() {
    private lateinit var viewManager: RecyclerView.LayoutManager

    var informersMap = ReaderActivity().informersMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val env = PreferenceManager.getDefaultSharedPreferences(this)
        val currentInformersList = env.getStringSet("informers", mutableSetOf())

        updateInformers(readUrlDB())
        val names = informersMap.keys.toTypedArray()

        setContentView(R.layout.informers_menu)
        viewManager = LinearLayoutManager(this)
        val adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_multiple_choice, names)
        informersList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE)
        informersList.setAdapter(adapter)

        //view checkbox
        val isSelected = { x: Int -> informersList.getCheckedItemPositions().get(x) }
        //get item from ListView by position
        val getItem = { x: Int -> informersList.getItemAtPosition(x).toString() }

        //preset checkbox from env
        for (i in 0..informersList.getCount() - 1) {
            if (getItem(i) in currentInformersList) {
                informersList.setItemChecked(i, true)
            }
        }

        //helper func() for hard resave SharedPreferences (update StringSet)
        fun forceUpdateEnv() {
            env.edit().putInt("dummy", 0).apply()
            env.edit().putInt("dummy", 1).apply()
        }

        back_btn.setOnClickListener { view ->
            this.finish()
        }

        informersList.setOnItemClickListener { adapterView, view, i, len ->
            if (isSelected(i)) {
                currentInformersList.add(informersList.getItemAtPosition(i).toString())
            } else {
                if (getItem(i) in currentInformersList) {
                    currentInformersList.remove(getItem(i))
                }
            }
            env.edit().putStringSet("informers", currentInformersList).apply()
            env.edit().putBoolean("needRefresh", true).apply()
            forceUpdateEnv()


        }
        //TODO add title
        //setTitle(intent.extras.getString("title"))
    }
    private fun readUrlDB(): HashMap<String, String> {
        val customUrls: HashMap<String, String> = hashMapOf()
        try {
            val allRows = database.use {
                select(INFORMERS_DB).exec { parseList(classParser<DictRowParserUrl>()) }
            }
            for (row in allRows) {
                val resLst = row.getLst()
                customUrls[resLst[0]] = resLst[1]
            }
        } catch (e: Exception) {
            Log.e("DB_ACCESS_ERROR:", e.toString())
        }
        return customUrls
    }

    private fun updateInformers(mp: HashMap<String, String>) {
        for (key in mp.keys) {
            val v = mp.get(key)
            if (v != null) {
                informersMap.put(key, v)
            }
        }
    }

}



