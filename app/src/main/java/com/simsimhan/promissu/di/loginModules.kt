package com.simsimhan.promissu.di

import com.simsimhan.promissu.login.LoginViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module


val loginModules = module {
    viewModel { LoginViewModel() }
}