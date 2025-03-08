package xyz.ssupii.palmnotes.watch

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var notes_list: WearableRecyclerView
    private lateinit var adapter: NotesAdapter

    private lateinit var new_note_button: ImageButton
    private lateinit var new_note_button_initial: Button

    private val newNoteButtonListener = View.OnClickListener {
        val intent = Intent(this, EditActivity::class.java)
        startActivity(intent)
    }
    private val settingsButtonListener = View.OnClickListener {
        Log.println(Log.DEBUG, "Login", "Settings button")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show()
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        new_note_button_initial = findViewById(R.id.new_note_button_initial)
        notes_list = findViewById(R.id.notes_list)

        notes_list.setLayoutManager(WearableLinearLayoutManager(this))
        adapter = NotesAdapter(
            mutableListOf(), newNoteButtonListener, settingsButtonListener
        )
        notes_list.adapter = adapter

        new_note_button_initial.visibility = Button.VISIBLE

        new_note_button_initial.setOnClickListener(newNoteButtonListener)
    }

    override fun onResume() {
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show()

        super.onResume()

        val savedNotesFiles: Array<String> = fileList()
        var filesFound: Boolean = false

        adapter.changesComing() //TODO See function implementation
        if (savedNotesFiles.isNotEmpty()) {
            //TODO Add hiding initial big settings button
            for (file in savedNotesFiles) {
                if (file.toLowerCase().endsWith(".txt")) {
                    readFile(file)?.let { noteData ->
                        // Create a Note object and add it to the adapter
                        adapter.addNote(Triple(noteData.first, noteData.second, noteData.third))
                    }
                    filesFound = true
                }
            }
        }
        if(filesFound) notes_list.visibility = WearableRecyclerView.VISIBLE; new_note_button_initial.visibility = Button.GONE
    }

    private fun readFile(file: String): Triple<String, String, List<String>>? {

        var returnTriple: Triple<String, String, List<String>>? = null
        try {
            openFileInput(file).use { input ->
                val lines = input.bufferedReader().readLines()
                if (lines.isNotEmpty()) {
                    returnTriple = Triple(file, lines[0], lines.subList(1, lines.size))
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return returnTriple
    }

}
