package com.example.fileupload.di

import com.example.fileupload.data.remote.FileUploadApi
import com.example.fileupload.presentation.screen.home.HomeViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val mainModule = module {
    singleOf(::FileUploadApi).bind()
    viewModelOf(::HomeViewModel)
}