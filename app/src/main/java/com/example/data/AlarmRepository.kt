package com.example.data

import kotlinx.coroutines.flow.Flow

class AlarmRepository(private val alarmDao: AlarmDao) {
    val allAlarms: Flow<List<AlarmEntity>> = alarmDao.getAllAlarms()
    val enabledAlarms: Flow<List<AlarmEntity>> = alarmDao.getEnabledAlarms()

    suspend fun getAlarmById(id: Long): AlarmEntity? = alarmDao.getAlarmById(id)

    suspend fun insert(alarm: AlarmEntity): Long = alarmDao.insertAlarm(alarm)

    suspend fun update(alarm: AlarmEntity) = alarmDao.updateAlarm(alarm)

    suspend fun delete(alarm: AlarmEntity) = alarmDao.deleteAlarm(alarm)

    suspend fun deleteById(id: Long) = alarmDao.deleteAlarmById(id)

    suspend fun toggleAlarmState(id: Long, isEnabled: Boolean) = alarmDao.updateAlarmState(id, isEnabled)
}
