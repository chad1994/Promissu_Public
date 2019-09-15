package com.simsimhan.promissu.di

import com.simsimhan.promissu.repository.MainRepository
import com.simsimhan.promissu.repository.MainRepositoryImpl
import com.simsimhan.promissu.ui.main.MainViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val mainModule = module {
    factory { MainRepositoryImpl(get()) as MainRepository }

    viewModel { MainViewModel(get()) }
}