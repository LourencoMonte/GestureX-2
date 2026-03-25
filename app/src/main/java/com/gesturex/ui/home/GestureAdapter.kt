package com.gesturex.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gesturex.data.model.AcaoGesto
import com.gesturex.data.model.Gesture
import com.gesturex.data.model.TipoGesto
import com.gesturex.databinding.ItemGestureBinding

class GestureAdapter(
    private val onToggle: (Gesture, Boolean) -> Unit,
    private val onEdit: (Gesture) -> Unit
) : ListAdapter<Gesture, GestureAdapter.VH>(Diff()) {

    private val colors = listOf(
        Color.parseColor("#3D5AFE"),
        Color.parseColor("#00C896"),
        Color.parseColor("#FFB300"),
        Color.parseColor("#FF4D8D"),
        Color.parseColor("#9C27B0"),
        Color.parseColor("#FF6D00")
    )

    inner class VH(val b: ItemGestureBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(g: Gesture, pos: Int) {
            val cor = colors[pos % colors.size]
            b.viewAccent.setBackgroundColor(cor)
            b.textNome.text = g.nome
            b.textTipo.text = TipoGesto.label(g.tipoGesto)
            b.textAcao.text = "→ ${AcaoGesto.label(g.acaoDestino)}" +
                if (g.acaoDestino == AcaoGesto.ABRIR_APP && g.appNome.isNotBlank()) ": ${g.appNome}"
                else if (g.acaoDestino == AcaoGesto.LIGAR_PARA && g.contatoNome.isNotBlank()) ": ${g.contatoNome}"
                else ""
            b.switchAtivo.setOnCheckedChangeListener(null)
            b.switchAtivo.isChecked = g.ativo
            b.switchAtivo.setOnCheckedChangeListener { _, on -> onToggle(g, on) }
            b.root.setOnClickListener { onEdit(g) }
        }
    }

    override fun onCreateViewHolder(p: ViewGroup, t: Int) =
        VH(ItemGestureBinding.inflate(LayoutInflater.from(p.context), p, false))

    override fun onBindViewHolder(h: VH, pos: Int) = h.bind(getItem(pos), pos)

    class Diff : DiffUtil.ItemCallback<Gesture>() {
        override fun areItemsTheSame(a: Gesture, b: Gesture) = a.id == b.id
        override fun areContentsTheSame(a: Gesture, b: Gesture) = a == b
    }
}
