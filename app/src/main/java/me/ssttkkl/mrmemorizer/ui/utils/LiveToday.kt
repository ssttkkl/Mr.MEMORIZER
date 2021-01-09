package me.ssttkkl.mrmemorizer.ui.utils

import androidx.lifecycle.LiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

object LiveToday : LiveData<LocalDate>() {
    var worker: Job? = null

    override fun onActive() {
        worker = GlobalScope.launch {
            while (true) {
                val td = LocalDate.now()
                postValue(td)

                val tomorrowBeginning = td.plusDays(1).atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
                val now = ZonedDateTime.now().toInstant().toEpochMilli()
                delay(tomorrowBeginning - now)
            }
        }
    }

    override fun onInactive() {
        worker?.cancel()
    }
}