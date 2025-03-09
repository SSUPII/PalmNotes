package xyz.ssupii.palmnotes.watch.utils

import android.content.Context
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocalFile(private val context: Context, private val filename: String) {

    private var info: Quadruple<String, String, List<String>, String>? = null

    fun readFile(): Quadruple<String, String, List<String>, String>? {
        var returnObj: Quadruple<String, String, List<String>, String>? = null
        try {
            context.openFileInput(filename).use { input ->
                val lines = input.bufferedReader().readLines()
                if (lines.isNotEmpty()) {
                    val lastModifiedMillis = File(context.filesDir, filename).lastModified()
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) //TODO allow selection
                    returnObj = Quadruple(filename, lines[0], lines.subList(1, lines.size), sdf.format(
                        Date(lastModifiedMillis)
                    ))
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        info = returnObj
        return returnObj
    }

    fun getFileInfo(): Quadruple<String, String, List<String>, String>?{
        return info
    }

    fun getFilename(): String{
        return info?.first ?: filename
    }

    fun getTitle(): String{
        return info?.second ?: throw IllegalStateException("File has not been read yet")
    }

    fun getContents(): List<String>{
        return info?.third ?: throw IllegalStateException("File has not been read yet")
    }

    fun getTimestamp(): String{
        return info?.fourth ?: throw IllegalStateException("File has not been read yet")
    }
}