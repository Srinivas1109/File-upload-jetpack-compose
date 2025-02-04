package com.example.fileupload.di

import com.example.fileupload.data.remote.FileUploadApiImpl
import com.example.fileupload.domain.repository.FileUploadApi
import com.example.fileupload.presentation.screen.home.HomeViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val mainModule = module {
    singleOf(::FileUploadApiImpl).bind<FileUploadApi>()
    viewModelOf(::HomeViewModel)
}