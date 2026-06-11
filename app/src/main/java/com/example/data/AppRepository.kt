package com.example.data

import android.content.Context
import androidx.room.Room
import com.example.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppRepository(context: Context) {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "disaster-db"
    ).fallbackToDestructiveMigration().build()

    val historyFlow: Flow<List<SensorHistoryEntity>> = db.sensorHistoryDao().getHistory()
    
    val settingsFlow: Flow<AppSettingsEntity> = db.settingsDao().getSettings().map { 
        it ?: AppSettingsEntity() 
    }

    suspend fun updateSettings(settings: AppSettingsEntity) {
        db.settingsDao().updateSettings(settings)
    }

    suspend fun fetchLatestData() {
        try {
            val response = RetrofitClient.service.getFeeds(results = 1)
            response.feeds.forEach { feed ->
                val entity = SensorHistoryEntity(
                    entryId = feed.entry_id,
                    createdAt = feed.created_at,
                    field1 = feed.field1?.toFloatOrNull(),
                    field2 = feed.field2?.toFloatOrNull(),
                    field3 = feed.field3?.toFloatOrNull(),
                    field4 = feed.field4?.toFloatOrNull(),
                    field5 = feed.field5?.toFloatOrNull()
                )
                db.sensorHistoryDao().insertItem(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchHistoryForChart() {
        try {
            val response = RetrofitClient.service.getFeeds(results = 50)
            val entities = response.feeds.map { feed ->
                SensorHistoryEntity(
                    entryId = feed.entry_id,
                    createdAt = feed.created_at,
                    field1 = feed.field1?.toFloatOrNull(),
                    field2 = feed.field2?.toFloatOrNull(),
                    field3 = feed.field3?.toFloatOrNull(),
                    field4 = feed.field4?.toFloatOrNull(),
                    field5 = feed.field5?.toFloatOrNull()
                )
            }
            db.sensorHistoryDao().insertAll(entities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
