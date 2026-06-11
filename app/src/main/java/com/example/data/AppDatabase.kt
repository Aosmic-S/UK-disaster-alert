package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "sensor_history")
data class SensorHistoryEntity(
    @PrimaryKey val entryId: Long,
    val createdAt: String,
    val field1: Float?, // Landslide
    val field2: Float?, // Bridge
    val field3: Float?, // Eq X
    val field4: Float?, // Eq Y
    val field5: Float?  // Eq Z
)

@Dao
interface SensorHistoryDao {
    @Query("SELECT * FROM sensor_history ORDER BY createdAt DESC LIMIT 100")
    fun getHistory(): Flow<List<SensorHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: SensorHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SensorHistoryEntity>)
}

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val goodThreshold: Float = 950f,
    val criticalThreshold: Float = 500f,
    val isLandslideActive: Boolean = true,
    val isBridgeActive: Boolean = true,
    val isEarthquakeActive: Boolean = true,
    val publicDataEnabled: Boolean = true,
    val landslideName: String = "Landslide Prevention Array",
    val bridgeName: String = "Bridge Stability Array",
    val earthquakeName: String = "Seismic Array",
    val themeMode: String = "Immersive"
)

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getSettings(): Flow<AppSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSettings(settings: AppSettingsEntity)
}

@Database(entities = [SensorHistoryEntity::class, AppSettingsEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sensorHistoryDao(): SensorHistoryDao
    abstract fun settingsDao(): SettingsDao
}
