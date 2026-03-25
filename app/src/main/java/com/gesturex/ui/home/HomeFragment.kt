package com.gesturex.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gesturex.databinding.FragmentHomeBinding
import com.gesturex.ui.record.RecordGestureActivity

class HomeFragment : Fragment() {
    private var _b: FragmentHomeBinding? = null
    private val b get() = _b!!
    private lateinit var vm: GestureViewModel
    private lateinit var adapter: GestureAdapter

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?): View {
        _b = FragmentHomeBinding.inflate(i, c, false); return b.root
    }

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)
        vm = ViewModelProvider(requireActivity())[GestureViewModel::class.java]

        adapter = GestureAdapter(
            onToggle = { g, on -> vm.setAtivo(g.id, on) },
            onEdit = { g ->
                startActivity(Intent(requireContext(), com.gesturex.ui.edit.EditGestureActivity::class.java)
                    .putExtra("gesture_id", g.id))
            }
        )
        b.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        b.recyclerView.adapter = adapter

        vm.gestos.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
            b.emptyState.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
            b.recyclerView.visibility = if (lista.isEmpty()) View.GONE else View.VISIBLE
        }

        b.fabNovoGesto.setOnClickListener {
            startActivity(Intent(requireContext(), RecordGestureActivity::class.java))
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
