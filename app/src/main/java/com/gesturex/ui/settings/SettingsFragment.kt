package com.gesturex.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gesturex.databinding.FragmentSettingsBinding
import com.gesturex.service.GestureService

class SettingsFragment : Fragment() {
    private var _b: FragmentSettingsBinding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentSettingsBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)
        val prefs = requireContext().getSharedPreferences("gesturex_prefs", Context.MODE_PRIVATE)

        b.switchServico.isChecked = prefs.getBoolean("servico_ativo", true)
        b.switchServico.setOnCheckedChangeListener { _, on ->
            prefs.edit().putBoolean("servico_ativo", on).apply()
            if (on) GestureService.start(requireContext()) else GestureService.stop(requireContext())
        }

        val sens = prefs.getString("sensibilidade", "media")
        when (sens) {
            "alta"  -> b.radioAlta.isChecked = true
            "baixa" -> b.radioBaixa.isChecked = true
            else    -> b.radioMedia.isChecked = true
        }

        b.radioGroupSens.setOnCheckedChangeListener { _, id ->
            val nivel = when (id) {
                b.radioAlta.id  -> "alta"
                b.radioBaixa.id -> "baixa"
                else            -> "media"
            }
            prefs.edit().putString("sensibilidade", nivel).apply()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
