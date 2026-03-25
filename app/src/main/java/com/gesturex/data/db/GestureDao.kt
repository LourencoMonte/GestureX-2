package com.gesturex.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gesturex.data.model.Gesture

@Dao
interface GestureDao {
    @Query("SELECT * FROM gestures ORDER BY id DESC")
    fun getAll(): LiveData<List<Gesture>>

    @Query("SELECT * FROM gestures WHERE ativo = 1")
    suspend fun getAtivos(): List<Gesture>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(g: Gesture): Long

    @Update
    suspend fun update(g: Gesture)

    @Delete
    suspend fun delete(g: Gesture)

    @Query("UPDATE gestures SET ativo = :ativo WHERE id = :id")
    suspend fun setAtivo(id: Int, ativo: Boolean)

    @Query("SELECT * FROM gestures WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Gesture?
}
