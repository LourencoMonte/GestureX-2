package com.gesturex.ui.edit

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gesturex.databinding.ActivityAppPickerBinding

data class AppInfo(val nome: String, val pkg: String)

class AppPickerActivity : AppCompatActivity() {
    private lateinit var b: ActivityAppPickerBinding
    private var listaCompleta = listOf<AppInfo>()

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivityAppPickerBinding.inflate(layoutInflater); setContentView(b.root)
        b.btnVoltar.setOnClickListener { finish() }

        val pm = packageManager
        listaCompleta = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
            .mapNotNull { ai ->
                val nome = pm.getApplicationLabel(ai).toString()
                if (nome.isNotBlank()) AppInfo(nome, ai.packageName) else null
            }
            .sortedBy { it.nome }

        val adapter = AppAdapter(listaCompleta) { app ->
            setResult(Activity.RESULT_OK, Intent().putExtra("pkg", app.pkg).putExtra("nome", app.nome))
            finish()
        }

        b.recyclerApps.layoutManager = LinearLayoutManager(this)
        b.recyclerApps.adapter = adapter

        b.editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val q = s.toString().lowercase()
                adapter.filtrar(listaCompleta.filter { it.nome.lowercase().contains(q) })
            }
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
        })
    }
}

class AppAdapter(
    private var lista: List<AppInfo>,
    private val onClick: (AppInfo) -> Unit
) : RecyclerView.Adapter<AppAdapter.VH>() {

    inner class VH(val tv: TextView) : RecyclerView.ViewHolder(tv)

    override fun onCreateViewHolder(p: ViewGroup, t: Int): VH {
        val tv = TextView(p.context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setPadding(48, 32, 48, 32)
            textSize = 15f
            setTextColor(resources.getColor(com.gesturex.R.color.text_primary, context.theme))
        }
        return VH(tv)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val app = lista[pos]; h.tv.text = app.nome; h.tv.setOnClickListener { onClick(app) }
    }

    override fun getItemCount() = lista.size

    fun filtrar(nova: List<AppInfo>) { lista = nova; notifyDataSetChanged() }
}
