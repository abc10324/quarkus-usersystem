package com.sam.model.repo

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.sam.util.Const
import org.bson.Document
import org.bson.types.ObjectId
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import kotlin.streams.toList

@ApplicationScoped
class OrderRepo {

    @Inject
    private lateinit var mongoClient: MongoClient

    private lateinit var collection: MongoCollection<Document>

    @PostConstruct
    private fun init(){
        collection = mongoClient.getDatabase(Const.DB_NAME).getCollection(Const.COLLECTION_ORDER)
    }

    fun getAll(): List<Order> = collection.find()
                                          .toList()
                                          .stream()
                                          .map { Order.parse(it) }
                                          .toList()

    fun insert(order :Order) :Order{

        var doc :Document = Document("userId",order.userId).append("itemList",order.itemList)

        order._id = collection.insertOne(doc).insertedId.asObjectId().value

        return order
    }

}

data class Order(var _id :ObjectId?,
                 var userId: ObjectId,
                 var itemList: List<String>){

    companion object{
        fun parse(doc: Document): Order = Order(doc.getObjectId("_id"),
                                                doc.getObjectId("userId"),
                                                doc.getList("itemList",String::class.java))
    }

    fun get_id(): String = _id.toString()

    fun getUserId(): String = userId.toString()

    fun setUserId(userId :String){
        this.userId = ObjectId(userId)
    }
}
