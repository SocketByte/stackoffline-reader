package pl.socketbyte.stackreader.data

import com.google.gson.GsonBuilder
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.RAMDirectory
import pl.socketbyte.stackreader.*
import java.io.File
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.Enumeration
import java.util.zip.ZipFile

class Metadata(private val zipPath: String) {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val cache = mutableMapOf<String, String>()
    private val infoFile = File("metadata.txt")

    private val analyzer = StandardAnalyzer()
    private val directory = RAMDirectory()

    private val config = IndexWriterConfig(analyzer)
    private val writer = IndexWriter(directory, config)

    private val zipFile = ZipFile(zipPath)

    fun exist() = infoFile.exists()

    fun getQuestion(id: String): Question {
        val entry = zipFile.getEntry("stack/data/$id.json")
                ?: throw RuntimeException("Question with id $id does not exist")

        val text = read(zipFile.getInputStream(entry))
        return gson.fromJson<Question>(text, Question::class.java)
    }

    fun query(queryText: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val parser = QueryParser("title", analyzer)
        val query = parser.parse(queryText)

        val maxQuery = 10
        val reader = DirectoryReader.open(writer)
        val searcher = IndexSearcher(reader)
        val docs = searcher.search(query, maxQuery)
        val hits = docs.scoreDocs

        var index = -1
        for (i in hits.indices) {
            val document = searcher.doc(hits[i].doc) ?: continue
            if (document.get("id").isNullOrEmpty())
                continue
            if (zipFile.getEntry("stack/data/${document.get("id")}.json") == null)
                continue
            index++
        }
        println("${BLUE_UNDERLINED}Found " + (if (index == -1) 0 else index) + " questions for this query.$RESET")
        for (i in 0..index) {
            val document = searcher.doc(hits[i].doc) ?: continue

            result[(i + 1).toString()] = document.get("id")
            println(BLUE_BRIGHT + (i + 1).toString() + "$BLUE. $WHITE" + document.get("title"))
        }
        reader.close()
        return result
    }

    fun generateMetadataFile() {
        println("Generating metadata file.")
        val entries = zipFile.entries()

        var index = 0
        while (entries.hasMoreElements()) {
            try {
                val entry = entries.nextElement()
                val stream = zipFile.getInputStream(entry)

                val text = read(stream)
                val jsonObject = gson.fromJson<Question>(text, Question::class.java) ?: continue

                cache[jsonObject.title] = jsonObject.id.toString()
                print("Loaded $index files.\r")
                index++
            } catch (e: Exception) {

            }
        }
        println("Successfully loaded files. Proceeding...")

        if (!infoFile.exists())
            infoFile.createNewFile()
        val builder = StringBuilder()
        index = 0
        for ((key, value) in cache) {
            builder.append("$key:$value\n")
            print("Appended $index metadata infos.\r")
            index++
        }
        infoFile.writeText(builder.toString())

        println("Successfully created metadata file.")
    }

    fun readMetadataFile() {
        if (!infoFile.exists())
            return

        val lines = infoFile.readLines()

        for ((index, line) in lines.withIndex()) {
            try {
                val info = line.split(":")
                val title = info[0]
                val id = info[1]

                writer.addMetadataInfo(title, id)
            } catch (e: Exception) {
                println("${RED_BRIGHT}Error occured while loading $index metadata entry. Passing it.$RESET")
            }
        }
        println("${GREEN_BRIGHT}Loaded metadata file successfully.$RESET")
    }

    private fun read(input: java.io.InputStream): String {
        val s = java.util.Scanner(input).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }
}
private fun IndexWriter.addMetadataInfo(title: String, id: String) {
    val doc = Document()
    doc.add(TextField("title", title, Field.Store.YES))
    doc.add(StringField("id", id, Field.Store.YES))
    this.addDocument(doc)
}