package com.simsimhan.promissu.di

import com.simsimhan.promissu.promise.create.CreateViewModel
import org.koin.androidx.viewmodel.experimental.builder.viewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val createModule = module{
    viewModel { CreateViewModel()}
}