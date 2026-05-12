package com.fitforge.app.data.repository

import com.fitforge.app.data.local.dao.CheckInDao
import com.fitforge.app.data.local.dao.SleepDao
import com.fitforge.app.data.local.entity.toEntity
import com.fitforge.app.domain.model.DailyCheckIn
import com.fitforge.app.domain.model.SleepLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepRepository @Inject constructor(private val dao: SleepDao) {
    fun getRecent(): Flow<List<SleepLog>> = dao.getRecent().map { list -> list.map { it.toDomain() } }
    suspend fun getTodaysSleep(): SleepLog? = dao.getByDate(LocalDate.now().toString())?.toDomain()
    suspend fun logSleep(log: SleepLog) = dao.insert(log.toEntity())
}

@Singleton
class CheckInRepository @Inject constructor(private val dao: CheckInDao) {
    fun getRecent(): Flow<List<DailyCheckIn>> = dao.getRecent().map { list -> list.map { it.toDomain() } }
    suspend fun getTodaysCheckIn(): DailyCheckIn? = dao.getByDate(LocalDate.now().toString())?.toDomain()
    suspend fun logCheckIn(checkIn: DailyCheckIn) = dao.insert(checkIn.toEntity())
}
