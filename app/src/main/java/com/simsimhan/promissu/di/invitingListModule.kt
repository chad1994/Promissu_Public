package com.simsimhan.promissu.di

import com.simsimhan.promissu.repository.InvitingListRepository
import com.simsimhan.promissu.repository.InvitingListRepositoryImpl
import com.simsimhan.promissu.ui.invitingList.InvitingListViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module


val invitingListModule  = module {
    factory { InvitingListRepositoryImpl(get()) as InvitingListRepository}

    viewModel { InvitingListViewModel(get()) }
}