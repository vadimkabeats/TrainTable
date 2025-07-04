// AppModule.kt
package com.example.trainscheduleapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.trainscheduleapp.data.AppDatabase
import com.example.trainscheduleapp.data.TrainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(
            ctx.applicationContext,
            AppDatabase::class.java,
            "train_schedule_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    @Singleton
    fun provideTrainRepository(db: AppDatabase): TrainRepository =
        TrainRepository(db)
}
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE trains ADD COLUMN notifyOffsetSec INTEGER NOT NULL DEFAULT 10"
        )
    }
}