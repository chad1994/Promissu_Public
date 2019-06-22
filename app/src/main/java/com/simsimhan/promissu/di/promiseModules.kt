package com.simsimhan.promissu.di

import com.simsimhan.promissu.ui.promise.PromiseViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module


val promiseModules = module {
    viewModel { PromiseViewModel() }
}