package me.ssttkkl.mrmemorizer.ui.utils

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import java.time.Instant

class LiveTicker(val durationMillis: Long) : LiveData<Long>() {
    lateinit var ticker: Job

    override fun onActive() {
        ticker = GlobalScope.launch {
            while (true) {
                try {
                    postValue(Instant.now().toEpochMilli())
                    delay(durationMillis)
                } catch (exc: CancellationException) {
                    throw exc
                } catch (exc: Exception) {
                    exc.printStackTrace()
                }
            }
        }
    }

    override fun onInactive() {
        ticker.cancel()
    }
}