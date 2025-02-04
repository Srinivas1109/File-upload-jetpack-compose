package com.example.fileupload.di

import com.example.fileupload.data.repository.FileManagerImpl
import com.example.fileupload.data.repository.FileUploadRepositoryImpl
import com.example.fileupload.domain.repository.FileManager
import com.example.fileupload.domain.repository.FileUploadRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::FileUploadRepositoryImpl).bind<FileUploadRepository>()
    singleOf(::FileManagerImpl).bind<FileManager>()
}