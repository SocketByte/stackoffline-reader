package pl.socketbyte.stackreader.data

import pl.socketbyte.stackreader.data.Comment

data class Reply(val author: String, val votes: Int, val content: String) {

    val comments = mutableListOf<Comment>()

}