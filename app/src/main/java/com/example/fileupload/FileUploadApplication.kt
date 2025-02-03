package com.example.fileupload

import android.app.Application
import com.example.fileupload.di.mainModule
import com.example.fileupload.di.networkModule
import com.example.fileupload.di.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FileUploadApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(networkModule, mainModule, repositoryModule)
            androidContext(this@FileUploadApplication)
        }
    }
}