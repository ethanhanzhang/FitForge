package com.fitforge.app.data.repository

import com.fitforge.app.data.local.dao.UserDao
import com.fitforge.app.data.local.entity.toEntity
import com.fitforge.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val dao: UserDao) {

    fun getProfile(): Flow<UserProfile?> = dao.getProfile().map { it?.toDomain() }

    suspend fun getProfileOnce(): UserProfile? = dao.getProfileOnce()?.toDomain()

    suspend fun saveProfile(profile: UserProfile) = dao.upsertProfile(profile.toEntity())

    suspend fun isOnboardingComplete(): Boolean = dao.getProfileOnce() != null
}
