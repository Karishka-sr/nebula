package com.nebula.app

import android.app.Application
import com.nebula.app.data.NebulaRepository

class NebulaApplication : Application() {
    lateinit var repository: NebulaRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = NebulaRepository(this)
    }
}