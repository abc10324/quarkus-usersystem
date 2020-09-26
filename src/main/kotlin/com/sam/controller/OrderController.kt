package com.sam.controller

import com.sam.model.repo.Order
import com.sam.model.service.OrderService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/Order")
@Produces(MediaType.APPLICATION_JSON)
class OrderController {

    @Inject
    private lateinit var orderSvc: OrderService

    @GET
    fun getAll(): Map<String,Any?> {
        return mutableMapOf("status" to "success",
                            "result" to orderSvc.getAll())
    }

    @POST
    fun insert(order: Order): Map<String,Any?>{
        return mutableMapOf("status" to "success",
                            "result" to orderSvc.insert(order))
    }

}