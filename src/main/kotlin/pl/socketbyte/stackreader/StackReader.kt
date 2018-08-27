package pl.socketbyte.stackreader

import pl.socketbyte.stackreader.data.Comment
import pl.socketbyte.stackreader.data.Metadata
import java.awt.im.InputMethodHighlight
import java.util.*

fun main(args: Array<String>) {
    val zipPath = args[0]
    val metadata = Metadata(zipPath)

    clearScreen()
    if (!metadata.exist()) {
        metadata.generateMetadataFile()
    }
    metadata.readMetadataFile()
    clearScreen()
    while (true) {
        val scanner = Scanner(System.`in`)

        println(CYAN_UNDERLINED + "Enter your question:" + RESET)
        print("$WHITE_BOLD_BRIGHT> $WHITE")
        val line = scanner.nextLine()
        println()
        val resultMap = metadata.query(line)
        println()

        if (resultMap.isEmpty())
            continue
        questionSelect(metadata, resultMap, scanner)
    }
}

fun questionSelect(metadata: Metadata, resultMap: Map<String, String>, scanner: Scanner) {
    println(CYAN_UNDERLINED + "Enter question index or `quit` or `q`" + RESET)
    print("$WHITE_BOLD_BRIGHT> $WHITE")
    val index = scanner.nextLine()
    if (index == "quit" || index == "q") {
        return
    }
    val resultId = resultMap[index]
    if (resultId == null) {
        println("${RED_BRIGHT}Invalid index, try again.$RESET")
        questionSelect(metadata, resultMap, scanner)
        return
    }
    val question = metadata.getQuestion(resultId)
    clearScreen()
    println("      $YELLOW_BRIGHT${question.votes} votes$RESET")
    println("$WHITE_UNDERLINED${question.title}$RESET")
    println(WHITE_BOLD_BRIGHT + question.content + RESET)
    println("         $RED~ $RED_BRIGHT${question.author}$RESET")
    renderComments(question.comments)
    println()
    if (question.replies.isNotEmpty()) {
        println("${BLUE_BACKGROUND_BRIGHT}Answers:$RESET")
    }
    for (reply in question.replies) {
        println("      $YELLOW_BRIGHT${reply.votes} votes$RESET")
        println("$WHITE_BOLD_BRIGHT${reply.content}$RESET")
        println("         $RED~ $RED_BRIGHT${reply.author}$RESET")
        renderComments(reply.comments)
        println()
        println()
    }
    println()
    println("$YELLOW_UNDERLINED Online link: $YELLOW_BRIGHT https://stackoverflow.com/questions/${question.id}$RESET")
    println()
    println()
    return
}

private fun renderComments(comments: List<Comment>) {
    if (comments.isNotEmpty()) {
        println("${WHITE_BACKGROUND_BRIGHT}Comments:$RESET")
    }
    var highlight = true
    for (comment in comments) {
        renderComment(comment, highlight)
        highlight = !highlight
    }
}

private fun renderComment(comment: Comment, highlight: Boolean) {
    println("$RED_BRIGHT${comment.author} $RED~ $CYAN_BRIGHT${comment.content}$RESET")
}

private fun clearScreen() {
    if (System.getProperty("os.name").contains("Windows"))
        ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor()
    else
        Runtime.getRuntime().exec("clear")
}