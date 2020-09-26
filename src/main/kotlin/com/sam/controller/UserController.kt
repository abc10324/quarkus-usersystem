package com.sam.controller

import com.sam.model.repo.User
import com.sam.model.service.UserService
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType


@Path("/User")
@Produces(MediaType.APPLICATION_JSON)
class UserController {

    @Inject
    private lateinit var userSvc: UserService

    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") userId:String): Map<String,Any?> {

        return mutableMapOf("status" to "success",
                            "result" to userSvc.getById(userId))
    }

    @GET
    fun getAll(): Map<String,Any?> = mutableMapOf("status" to "success",
                                                  "result" to userSvc.getAll())

    @POST
    fun insert(user :User): Map<String,Any?> {

        return mutableMapOf("status" to "success",
                            "result" to userSvc.insert(user))
    }

}