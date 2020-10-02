package com.sam.model.repo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.sam.util.Const
import com.sam.util.codec.ObjectIdDeserializer
import com.sam.util.codec.ObjectIdSerializer
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import kotlin.streams.toList

@ApplicationScoped
class OrderRepo {

    @Inject
    private lateinit var mongoClient: MongoClient

    private lateinit var database: MongoDatabase

    private lateinit var collection: MongoCollection<Document>

    @PostConstruct
    private fun init(){
        collection = mongoClient.getDatabase(Const.DB_NAME).getCollection(Const.COLLECTION_ORDER)

        database = mongoClient.getDatabase(Const.DB_NAME)
    }

    fun getAll(): List<Order> = database.getCollection(Const.COLLECTION_ORDER,Order::class.java)
                                          .find()
                                          .toList()


    fun insert(order :Order) :OrderView{

//        var doc :Document = Document("userId",order.userId).append("itemList",order.itemList)
//
//        order._id = collection.insertOne(doc).insertedId.asObjectId().value

        database.getCollection(Const.COLLECTION_ORDER,Order::class.java).insertOne(order)

        var orderView = OrderView(order._id,order.userId,order.itemList)

        return orderView
    }

}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Order(@BsonId
                 var _id :ObjectId = ObjectId(),
                 var userId: ObjectId,
                 var itemList: List<String>){

//    companion object{
//        fun parse(doc: Document): Order = Order(doc.getObjectId("_id"),
//                                                doc.getObjectId("userId"),
//                                                doc.getList("itemList",String::class.java))
//    }
//
//    fun get_id(): String = _id.toString()
//
//    fun getUserId(): String = userId.toString()
//
//    fun setUserId(userId :String){
//        this.userId = ObjectId(userId)
//    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class OrderView(
        @JsonSerialize(using = ObjectIdSerializer::class)
        var _id :ObjectId = ObjectId(),
        @JsonSerialize(using = ObjectIdSerializer::class)
        var userId: ObjectId,
        var itemList: List<String>
)
