package com.sam.model.repo

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Aggregates.*
import com.mongodb.client.model.Field
import com.sam.util.Const
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import kotlin.streams.toList

@ApplicationScoped
class UserRepo {

    @Inject
    private lateinit var mongoClient:MongoClient

    private lateinit var collection:MongoCollection<Document>

    @PostConstruct
    fun init(){
        collection = mongoClient.getDatabase(Const.DB_NAME)
                                .getCollection(Const.COLLECTION_USER)
    }

    fun getByUserId(userId :String) :UserView{

        var result = collection.aggregate(listOf(
                                        match(Document("userId",userId)),
                                        addFields(Field("rmk","\$userId"))
                                ))
                                .toList()
                                .stream()
                                .map { UserView.parse(it) }
                                .findFirst()
                                .get()

        return result
    }

    fun getAll(): List<UserView> = collection.aggregate(listOf(
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
                                         .stream()
                                         .map { UserView.parse(it) }
                                         .toList()

    fun insert(user: User) :User{
        var userDoc = Document("userId",user.userId)
                       .append("password",user.password)
                       .append("sex",user.sex)

        user._id = collection.insertOne(userDoc).insertedId.asObjectId().value

        println("user._id = ${user._id}")

        return user
    }

}

data class User(@BsonId
                var _id: ObjectId?,
                var userId: String,
                var password: String,
                var sex: String
){
    companion object {
        fun parse(doc: Document): User = User(doc.getObjectId("_id"),
                                              doc.getString("userId"),
                                              doc.getString("password"),
                                              doc.getString("sex"))
    }

    fun get_id() :String = _id.toString()
}

data class UserView(var _id: String,
                    var userId: String,
                    var sex: String,
                    var orderInfo: List<Order>,
                    var itemList: List<String>,
                    var rmk: String){
    companion object{
        fun parse(doc: Document) :UserView = UserView(doc.getObjectId("_id").toString(),
                                                      doc.getString("userId"),
                                                      doc.getString("sex"),
                                                      doc.getList("orderInfo",Document::class.java)
                                                              .stream()
                                                              .map { Order.parse(it) }
                                                              .toList(),
                                                      doc.getList("itemList",String::class.java),
                                                      doc.getString("rmk"))
    }
}