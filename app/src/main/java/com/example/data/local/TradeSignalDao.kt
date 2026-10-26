package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeSignalDao {
    @Query("SELECT * FROM trade_signals ORDER BY timestamp DESC")
    fun getAllSignals(): Flow<List<TradeSignalEntity>>

    @Query("SELECT * FROM trade_signals WHERE status = :status ORDER BY timestamp DESC")
    fun getSignalsByStatus(status: String): Flow<List<TradeSignalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignal(signal: TradeSignalEntity): Long

    @Update
    suspend fun updateSignal(signal: TradeSignalEntity)

    @Delete
    suspend fun deleteSignal(signal: TradeSignalEntity)

    @Query("UPDATE trade_signals SET status = :status WHERE id = :id")
    suspend fun updateSignalStatus(id: Long, status: String)

    @Query("DELETE FROM trade_signals")
    suspend fun clearAll()
}
