package com.tailtown.pawcare.di

import com.tailtown.pawcare.data.remote.RemoteHealthRepository
import com.tailtown.pawcare.data.remote.RemoteInboxRepository
import com.tailtown.pawcare.data.remote.RemoteShopRepository
import com.tailtown.pawcare.data.remote.RemoteVetRepository
import com.tailtown.pawcare.data.repository.HealthRepository
import com.tailtown.pawcare.data.repository.InboxRepository
import com.tailtown.pawcare.data.repository.ShopRepository
import com.tailtown.pawcare.data.repository.VetRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindVetRepository(impl: RemoteVetRepository): VetRepository

    @Binds @Singleton
    abstract fun bindShopRepository(impl: RemoteShopRepository): ShopRepository

    @Binds @Singleton
    abstract fun bindHealthRepository(impl: RemoteHealthRepository): HealthRepository

    @Binds @Singleton
    abstract fun bindInboxRepository(impl: RemoteInboxRepository): InboxRepository
}
