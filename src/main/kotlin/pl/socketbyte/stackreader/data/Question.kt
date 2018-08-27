package pl.socketbyte.stackreader.data

data class Question(val id: Long, val author: String, val votes: Int, val title: String) {

    lateinit var content: String

    val replies = mutableListOf<Reply>()
    val comments = mutableListOf<Comment>()

}