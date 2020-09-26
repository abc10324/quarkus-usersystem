package com.sam.model.service

import com.sam.model.repo.Order
import com.sam.model.repo.OrderRepo
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class OrderService {

    @Inject
    private lateinit var orderRepo: OrderRepo

    fun getAll() = orderRepo.getAll()

    fun insert(order: Order) = orderRepo.insert(order)

}