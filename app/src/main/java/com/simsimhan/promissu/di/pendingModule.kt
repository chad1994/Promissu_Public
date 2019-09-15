package com.simsimhan.promissu.di

import com.simsimhan.promissu.repository.PendingRepository
import com.simsimhan.promissu.repository.PendingRepositoryImpl
import com.simsimhan.promissu.ui.pending.PendingViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val pendingModule = module {
    factory { PendingRepositoryImpl(get()) as PendingRepository}

    viewModel { PendingViewModel(get()) }
}