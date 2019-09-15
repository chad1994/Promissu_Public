package com.simsimhan.promissu.di

import com.simsimhan.promissu.repository.PastListRepository
import com.simsimhan.promissu.repository.PastListRepositoryImpl
import com.simsimhan.promissu.ui.pastList.PastListViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module


val pastListModule = module {
    factory { PastListRepositoryImpl(get()) as PastListRepository }

    viewModel { PastListViewModel(get()) }
}