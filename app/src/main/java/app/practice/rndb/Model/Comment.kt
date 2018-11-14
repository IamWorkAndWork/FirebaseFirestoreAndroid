package app.practice.rndb.Model

import java.util.*

data class Comment(
    val username: String,
    val timeStamp: Date,
    val commentTxt: String,
    val documentId: String,
    val userId: String
) {

}