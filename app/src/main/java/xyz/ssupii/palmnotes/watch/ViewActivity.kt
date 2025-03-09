package xyz.ssupii.palmnotes.watch

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import xyz.ssupii.palmnotes.watch.utils.LocalFile
import xyz.ssupii.palmnotes.watch.utils.Quadruple
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViewActivity: AppCompatActivity() {
    private lateinit var filename: String

    private lateinit var titleView: TextView
    private lateinit var noteView: TextView
    private lateinit var contents: Quadruple<String, String, List<String>, String>
    private lateinit var file: LocalFile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        filename = intent.getStringExtra("filename") ?: throw IllegalStateException("A file must be provided to this activity.")
        file = LocalFile(this, filename)
        contents = file.readFile()!!

        titleView = findViewById(R.id.view_note_title)
        noteView = findViewById(R.id.view_note_contents)
        titleView.text = contents.second
        noteView.text = contents.third.joinToString("\n")
    }
}