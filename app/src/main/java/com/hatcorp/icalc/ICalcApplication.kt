package com.hatcorp.icalc

import android.app.Application
import com.hatcorp.icalc.data.HistoryRepository

class ICalcApplication : Application() {
    lateinit var historyRepository: HistoryRepository
    override fun onCreate() {
        super.onCreate()
        historyRepository = HistoryRepository(this)
    }
}