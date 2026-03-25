package com.gesturex.data.repository

import androidx.lifecycle.LiveData
import com.gesturex.data.db.GestureDao
import com.gesturex.data.model.Gesture

class GestureRepository(private val dao: GestureDao) {
    val all: LiveData<List<Gesture>> = dao.getAll()
    suspend fun getAtivos() = dao.getAtivos()
    suspend fun insert(g: Gesture) = dao.insert(g)
    suspend fun update(g: Gesture) = dao.update(g)
    suspend fun delete(g: Gesture) = dao.delete(g)
    suspend fun setAtivo(id: Int, ativo: Boolean) = dao.setAtivo(id, ativo)
    suspend fun getById(id: Int) = dao.getById(id)
}
