package com.xmbest.floatmaster.hilt.module

import android.content.Context
import com.xmbest.floatmaster.manager.FloatWindowManager
import com.xmbest.floatmaster.module.DataStoreModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 全局模块依赖
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFloatWindowManager(@ApplicationContext context: Context) =
        FloatWindowManager(context)

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context) =
        DataStoreModule(context)
}