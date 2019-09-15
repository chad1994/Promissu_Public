package com.simsimhan.promissu.di

import com.simsimhan.promissu.repository.InvitingRepository
import com.simsimhan.promissu.repository.InvitingRepositoryImpl
import com.simsimhan.promissu.ui.invinting.InvitingViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val invitingModule  = module {
    factory { InvitingRepositoryImpl(get()) as InvitingRepository}

    viewModel { InvitingViewModel(get()) }
}