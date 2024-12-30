package com.example.pertemuan12_dbeksternal

import android.app.Application
import com.example.pertemuan12_dbeksternal.repository.AppContainer
import com.example.pertemuan12_dbeksternal.repository.MahasiswaContainer

class MahasiswaApplications: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = MahasiswaContainer()
    }
}