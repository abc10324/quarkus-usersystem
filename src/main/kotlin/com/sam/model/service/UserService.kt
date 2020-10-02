package com.sam.model.service

import com.sam.model.repo.User
import com.sam.model.repo.UserRepo
import com.sam.model.repo.UserView
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class UserService {

    @Inject
    private lateinit var userRepo: UserRepo

    fun getById(userId :String) = userRepo.getByUserId(userId)

    fun getAll() = userRepo.getAll()

    fun insert(user:User) = userRepo.insert(user)

}