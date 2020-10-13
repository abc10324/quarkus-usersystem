package com.sam.model.repo

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.mongodb.WriteConcern
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Aggregates.*
import com.mongodb.client.model.Field
import com.sam.util.Const
import com.sam.util.codec.ObjectIdSerializer
import org.bson.BsonObjectId
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import kotlin.streams.toList

@ApplicationScoped
class UserRepo {

    @Inject
    private lateinit var mongoClient:MongoClient

    private lateinit var database: MongoDatabase

    @PostConstruct
    fun init(){
        database = mongoClient.getDatabase(Const.DB_NAME)
    }

    fun getByUserId(userId :String) :UserView?= database.getCollection(Const.COLLECTION_USER,UserView::class.java)
                                                         .aggregate(listOf(
                                                                        match(Document("userId",userId)),
                                                                        lookup("order","_id","userId","orderInfo"),
                                                                        addFields(
                                                                             listOf(
                                                                                     Field("itemList",Document("\$reduce",
                                                                                             Document("input","\$orderInfo")
                                                                                                     .append("initialValue", listOf<String>())
                                                                                                     .append("in",Document("\$let",Document("vars",Document("elem",Document("\$concatArrays", listOf("\$\$value","\$\$this.itemList"))))
                                                                                                             .append("in",Document("\$setUnion","\$\$elem"))))
                                                                                     )),
                                                                                     Field("rmk","\$userId")
                                                                        ))
                                                            ))
                                                         .toList()
                                                         .firstOrNull()

    fun getAll(): List<UserView> = database.getCollection(Const.COLLECTION_USER,UserView::class.java)
                                           .aggregate(listOf(
                                                lookup("order","_id","userId","orderInfo"),
                                                addFields(
                                                        listOf(
                                                                Field("itemList",Document("\$reduce",
                                                                                         Document("input","\$orderInfo")
                                                                                          .append("initialValue", listOf<String>())
                                                                                          .append("in",Document("\$let",Document("vars",Document("elem",Document("\$concatArrays", listOf("\$\$value","\$\$this.itemList"))))
                                                                                                                         .append("in",Document("\$setUnion","\$\$elem"))))
                                                                )),
                                                                Field("rmk","\$userId")
                                                ))
                                           ))
                                           .toList()


    fun insert(user: User) :UserView?{
        database.getCollection(Const.COLLECTION_USER,User::class.java)
                .insertOne(user)

        return getByUserId(user.userId)
    }

}

@JsonIgnoreProperties(ignoreUnknown = true)
data class User(@BsonId
                var _id: ObjectId? = ObjectId(),
                var userId: String,
                var password: String,
                var sex: String,
                var birth: LocalDate?,
                var createTime: LocalDateTime = LocalDateTime.now(),
                var updateTime: LocalDateTime = LocalDateTime.now()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UserView(@JsonSerialize(using = ObjectIdSerializer::class)
                    var _id: ObjectId?,
                    var userId: String,
                    var sex: String,
                    var orderInfo: List<OrderView>,
                    var itemList: List<String>,
                    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                    var createTime: LocalDateTime?,
                    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                    var updateTime: LocalDateTime?,
                    var rmk: String)