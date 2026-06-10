package com.example.ui

import android.app.Application
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.AppSettingsEntity
import com.example.data.SensorHistoryEntity
import com.example.network.Content
import com.example.network.GeminiClient
import com.example.network.GeminiRequest
import com.example.network.Part
import com.example.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    
    val history = repository.historyFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val settings = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettingsEntity()
    )

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _aiInsight = MutableStateFlow<String?>(null)
    val aiInsight: StateFlow<String?> = _aiInsight.asStateFlow()

    private val _adminUsers = MutableStateFlow(listOf("Admin"))
    val adminUsers: StateFlow<List<String>> = _adminUsers.asStateFlow()

    fun addAdminUser(name: String) {
        val current = _adminUsers.value.toMutableList()
        if (!current.contains(name)) {
            current.add(name)
            _adminUsers.value = current
        }
    }

    fun removeAdminUser(name: String) {
        val current = _adminUsers.value.toMutableList()
        current.remove(name)
        _adminUsers.value = current
    }

    private var lastAlertTime = 0L
    private var lastInsightTime = 0L

    init {
        viewModelScope.launch {
            repository.fetchHistoryForChart()
        }
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (isActive) {
                repository.fetchLatestData()
                checkConnectionStatus()
                checkForCriticalAlerts()
                
                // Fetch AI Insight every 1 minute if connected
                val now = System.currentTimeMillis()
                if (_isConnected.value && (now - lastInsightTime > 60000)) {
                    fetchAiInsight()
                    lastInsightTime = now
                }

                delay(10000) // Poll every 10 seconds
            }
        }
    }

    private fun checkConnectionStatus() {
        val latest = history.value.firstOrNull()
        if (latest != null) {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val date = sdf.parse(latest.createdAt)
                if (date != null) {
                    val timeDiff = System.currentTimeMillis() - date.time
                    _isConnected.value = timeDiff < 60000 // Connected if < 1 minute
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun checkForCriticalAlerts() {
        val latest = history.value.firstOrNull() ?: return
        val currentSettings = settings.value
        
        val soilWater = latest.field1 ?: 0f
        val isCritical = soilWater < currentSettings.criticalThreshold
        
        if (isCritical) {
            val app = getApplication<Application>()
            val now = System.currentTimeMillis()
            // Alert at most once every 30 seconds
            if (now - lastAlertTime > 30000) {
                playAlertSound(app)
                lastAlertTime = now
            }
        }
    }

    private fun playAlertSound(context: Context) {
        try {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, uri)
            ringtone?.play()
            
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(500)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateSettings(newSettings: AppSettingsEntity) {
        viewModelScope.launch {
            repository.updateSettings(newSettings)
        }
    }

    private fun fetchAiInsight() {
        val recentData = history.value.take(5)
        if (recentData.isEmpty()) return

        val dataStr = recentData.joinToString("\n") {
            "Time: ${it.createdAt}, Landslide: ${it.field1}, Erosion: ${it.field2}, Earthquake: X(${it.field3}), Y(${it.field4}), Z(${it.field5})"
        }

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") return@launch

                val prompt = "Analyze the following real-time disaster sensor data and provide a brief, professional one-sentence safety insight.\n$dataStr"
                
                val request = GeminiRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = prompt)))
                    )
                )

                val response = GeminiClient.service.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (text != null) {
                    _aiInsight.value = text
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _aiInsight.value = "AI Insight unavailable."
            }
        }
    }
}
