package com.example.avito_intership.data.mapper

import com.example.avito_intership.domain.model.UserProfile
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class FirebaseUserMapper @Inject constructor() {
    fun map(user: FirebaseUser): UserProfile {
        return UserProfile(
            id = user.uid,
            name = user.displayName,
            email = user.email,
            phone = user.phoneNumber,
            photoUrl = user.photoUrl?.toString(),
        )
    }
}
