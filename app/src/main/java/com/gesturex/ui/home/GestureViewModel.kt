package com.gesturex.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gesturex.data.db.GestureDatabase
import com.gesturex.data.model.Gesture
import com.gesturex.data.repository.GestureRepository
import kotlinx.coroutines.launch

class GestureViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = GestureRepository(GestureDatabase.getInstance(app).gestureDao())
    val gestos = repo.all

    fun salvar(g: Gesture) = viewModelScope.launch {
        if (g.id == 0) repo.insert(g) else repo.update(g)
    }

    fun deletar(g: Gesture) = viewModelScope.launch { repo.delete(g) }

    fun setAtivo(id: Int, ativo: Boolean) = viewModelScope.launch { repo.setAtivo(id, ativo) }

    suspend fun getById(id: Int) = repo.getById(id)
}
